package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Motor Part Shop Software (MPSS)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT,15,10));
        header.setBackground(new Color(20,60,100));
        JLabel logo = new JLabel("  Motor Part Shop Software (MPSS)");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        JLabel sub = new JLabel("Inventory Management System — JIT Philosophy");
        sub.setForeground(new Color(180,210,255));
        sub.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        header.add(logo); header.add(Box.createHorizontalStrut(20)); header.add(sub);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        tabs.addTab("Inventory",     new InventoryPanel());
        tabs.addTab("Order List",    new OrderPanel());
        tabs.addTab("Daily Revenue", new ReportPanel());
        tabs.addTab("Monthly Graph", new SalesGraphPanel());
        tabs.addTab("Vendors",       new VendorPanel());

        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT,10,4));
        status.setBackground(new Color(44,62,80));
        JLabel statusLabel = new JLabel("  MPSS v1.0  |  Ready  |  Traceability: UML to Code");
        statusLabel.setForeground(new Color(200,220,255));
        statusLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        status.add(statusLabel);

        add(header, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
