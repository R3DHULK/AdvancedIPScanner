import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AdvancedIPScanner extends JFrame {
    private JTextArea logTextArea;
    private JButton startButton;
    private JButton stopButton;
    private JButton clearButton;
    private List<String> reachableIPs;
    private ExecutorService executorService;

    public AdvancedIPScanner() {
        setTitle("Advanced IP Scanner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(logTextArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        startButton = new JButton("Start Scan");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startScan();
            }
        });
        buttonPanel.add(startButton);

        stopButton = new JButton("Stop Scan");
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopScan();
            }
        });
        buttonPanel.add(stopButton);

        clearButton = new JButton("Clear Log");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearLog();
            }
        });
        buttonPanel.add(clearButton);

        add(buttonPanel, BorderLayout.SOUTH);

        reachableIPs = new ArrayList<>();
    }

    private void startScan() {
        clearLog();
        log("Starting IP scan...");
        log("Scanning local network...");

        executorService = Executors.newFixedThreadPool(20);
        String baseIPAddress = getBaseIPAddress();

        for (int i = 0; i <= 255; i++) {
            String ipAddress = baseIPAddress + i;
            executorService.execute(new IPScanTask(ipAddress));
        }
    }

    private void stopScan() {
        if (executorService != null) {
            executorService.shutdownNow();
            log("IP scan stopped.");
        }
    }

    private String getBaseIPAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            String hostAddress = localHost.getHostAddress();
            return hostAddress.substring(0, hostAddress.lastIndexOf('.') + 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void log(String message) {
        logTextArea.append(message + "\n");
    }

    private void clearLog() {
        logTextArea.setText("");
        reachableIPs.clear();
    }

    private class IPScanTask implements Runnable {
        private String ipAddress;

        public IPScanTask(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        @Override
        public void run() {
            try {
                InetAddress inetAddress = InetAddress.getByName(ipAddress);
                if (inetAddress.isReachable(5000)) {
                    reachableIPs.add(ipAddress);
                    log("Reachable IP: " + ipAddress);
                }
            } catch (IOException e) {
                // Handle exception if necessary
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AdvancedIPScanner ipScanner = new AdvancedIPScanner();
                ipScanner.setVisible(true);
            }
        });
    }
}
