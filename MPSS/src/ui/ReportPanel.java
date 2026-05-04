package ui;

import model.*;
import javax.swing.*;
import java.awt.*;

public class ReportPanel extends JPanel {
    private final DataStore store = DataStore.getInstance();
    private JTextArea outputArea;

    public ReportPanel() {
        setLayout(new BorderLayout(5,5));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT,10,8));
        topBar.setBackground(new Color(210,220,235));
        topBar.setBorder(BorderFactory.createTitledBorder("Actions"));

        topBar.add(makeBtn("Generate Daily Report", new Color(0,130,50),  e -> {
            outputArea.setText(store.reportGenerator.generateDailyReport(store.transactions, store.lastOrderList));
            outputArea.setCaretPosition(0);
        }));
        topBar.add(makeBtn("Print Order Report",    new Color(0,100,180), e -> {
            outputArea.setText(store.reportGenerator.printOrderReport(store.lastOrderList));
            outputArea.setCaretPosition(0);
        }));

        outputArea = new JTextArea();
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);

        add(topBar, BorderLayout.NORTH);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    private JButton makeBtn(String label, Color bg, java.awt.event.ActionListener al) {
        JButton b = new JButton(label);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(220,40));
        b.addActionListener(al);
        return b;
    }
}
