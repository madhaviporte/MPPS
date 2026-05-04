package ui;

import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class OrderPanel extends JPanel {
    private final DataStore store = DataStore.getInstance();
    private JTextArea outputArea;
    private DefaultTableModel tableModel;

    public OrderPanel() {
        setLayout(new BorderLayout(5,5));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(buildTopBar(),   BorderLayout.NORTH);
        add(buildSplitter(), BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,10,8));
        p.setBackground(new Color(210,220,235));
        p.setBorder(BorderFactory.createTitledBorder("Actions"));

        p.add(makeBtn("Generate Order List", new Color(0,130,50),  e -> generateOrderList()));
        p.add(makeBtn("Clear",               new Color(60,60,60),  e -> { tableModel.setRowCount(0); outputArea.setText(""); }));
        return p;
    }

    private JButton makeBtn(String label, Color bg, java.awt.event.ActionListener al) {
        JButton b = new JButton(label);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(200,40));
        b.addActionListener(al);
        return b;
    }

    private JSplitPane buildSplitter() {
        String[] cols = {"Part Name","Rack","Current Stock","Threshold","Order Qty","Vendor","Contact","Address"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(22);
        table.getTableHeader().setBackground(new Color(33,97,140));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font(Font.SANS_SERIF,Font.BOLD,13));
        JScrollPane topSP = new JScrollPane(table);
        topSP.setBorder(BorderFactory.createTitledBorder("Low-Stock / Order Items"));

        outputArea = new JTextArea();
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        JScrollPane botSP = new JScrollPane(outputArea);
        botSP.setBorder(BorderFactory.createTitledBorder("Order + Revenue Summary"));

        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSP, botSP);
        sp.setDividerLocation(220); sp.setResizeWeight(0.6);
        return sp;
    }

    private void generateOrderList() {
        tableModel.setRowCount(0);
        List<String> orderLines = store.orderManager.generateOrderList(store.inventory, store.vendors);
        store.lastOrderList = orderLines;
        for (MotorPart part : store.inventory.getPartsList()) {
            if (part.getState()==MotorPart.PartState.ORDER_PENDING
             || part.getState()==MotorPart.PartState.OUT_OF_STOCK
             || part.getState()==MotorPart.PartState.LOW_STOCK) {
                Vendor v = store.vendors.get(part.getVendorId());
                int reqQty = store.orderManager.calcRequiredQty(part);
                tableModel.addRow(new Object[]{
                    part.getPartName(), part.getRackNumber(),
                    part.getCurrentStock(), part.getThresholdValue(), reqQty,
                    v!=null?v.getVendorName():"—",
                    v!=null?v.getContactNumber():"—",
                    v!=null?v.getAddress():"—"
                });
            }
        }
        double dailyRevenue = 0;
        for (SaleTransaction t : store.transactions) dailyRevenue += t.calcRevenue();
        StringBuilder sb = new StringBuilder();
        sb.append("=== END-OF-DAY SUMMARY ===\n\n");
        sb.append(String.format("Total Daily Revenue : Rs. %.2f%n%n", dailyRevenue));
        if (orderLines.isEmpty()) sb.append("All parts adequately stocked. No orders needed.\n");
        else { sb.append("Items to Order: ").append(orderLines.size()).append("\n"); for (String l:orderLines) sb.append("  * ").append(l).append("\n"); }
        outputArea.setText(sb.toString());
        outputArea.setCaretPosition(0);
        if (orderLines.isEmpty()) JOptionPane.showMessageDialog(this,"All parts adequately stocked!","Order Check",JOptionPane.INFORMATION_MESSAGE);
        else JOptionPane.showMessageDialog(this,orderLines.size()+" item(s) need to be ordered.","Order List Generated",JOptionPane.WARNING_MESSAGE);
    }
}
