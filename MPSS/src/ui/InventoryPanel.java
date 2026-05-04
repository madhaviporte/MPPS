package ui;

import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

/**
 * Traceability:
 *  Use Case : Record part sale, Check inventory
 *  Class Diagram: Inventory, MotorPart, SaleTransaction
 *  Sequence Diagram: Shop Owner -> MPSS UI -> Inventory
 *
 * NOTE: Threshold is NEVER entered by the user.
 *       It is auto-calculated as: avg weekly sales (last 4 weeks) x 1.5
 *       and refreshed every time a sale is recorded.
 */
public class InventoryPanel extends JPanel {
    private final DataStore store = DataStore.getInstance();
    private DefaultTableModel tableModel;
    private JTable table;

    public InventoryPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonBar(),  BorderLayout.SOUTH);
        refreshTable();
    }

    // ── Table ──────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        String[] cols = {"Part #","Part Name","Current Stock","Auto Threshold","Rack","Vendor","State"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(33,97,140));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        table.setDefaultRenderer(Object.class, new StateRenderer());

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder(
            "Inventory — Parts List   |   Threshold = Avg Weekly Sales (last 4 wks) x 1.5"));

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 2));
        legend.add(legendBox(new Color(255,243,205), "Low Stock"));
        legend.add(legendBox(new Color(255,205,210), "Out of Stock"));
        legend.add(legendBox(new Color(213,245,227), "Order Pending"));
        legend.add(legendBox(new Color(205,245,255), "Restocked"));
        legend.add(legendBox(new Color(220,220,220), "Discontinued"));
        legend.add(legendBox(Color.WHITE,            "In Stock"));

        JPanel p = new JPanel(new BorderLayout(4,4));
        p.add(sp,     BorderLayout.CENTER);
        p.add(legend, BorderLayout.SOUTH);
        return p;
    }

    private JLabel legendBox(Color c, String text) {
        JLabel l = new JLabel("  " + text + "  ");
        l.setOpaque(true); l.setBackground(c); l.setForeground(Color.BLACK);
        l.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        l.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        return l;
    }

    // ── Button bar ─────────────────────────────────────────────────
    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.setBackground(new Color(210,220,235));
        bar.setBorder(BorderFactory.createTitledBorder("Actions"));

        bar.add(makeBtn("Add New Part",     new Color(0,130,50),  e -> showAddPartDialog()));
        bar.add(makeBtn("Record Sale",      new Color(0,100,180), e -> showRecordSaleDialog()));
        bar.add(makeBtn("Restock Part",     new Color(0,130,50),  e -> showRestockDialog()));
        bar.add(makeBtn("Discontinue Part", new Color(180,0,0),   e -> showDiscontinueDialog()));
        bar.add(makeBtn("Refresh Table",    new Color(60,60,60),  e -> refreshTable()));

        // Info label explaining auto-threshold
        JLabel info = new JLabel(
            "  Threshold is auto-calculated from last 4 weeks of sales — no manual entry needed.");
        info.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));
        info.setForeground(new Color(80,80,80));
        bar.add(info);

        return bar;
    }

    private JButton makeBtn(String label, Color bg, java.awt.event.ActionListener al) {
        JButton b = new JButton(label);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(170, 40));
        b.addActionListener(al);
        return b;
    }

    // ── Dialog: Add New Part ───────────────────────────────────────
    private void showAddPartDialog() {
        JTextField tfPartNo   = new JTextField(10);
        JTextField tfPartName = new JTextField(14);
        JTextField tfStock    = new JTextField(6);
        JTextField tfRack     = new JTextField(6);
        JTextField tfVendorId = new JTextField(8);

        // Threshold is NOT an input field — shown as read-only note
        JLabel threshNote = new JLabel("Will be auto-calculated from sales history.");
        threshNote.setForeground(new Color(0,100,0));
        threshNote.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        form.add(new JLabel("Part Number:"));     form.add(tfPartNo);
        form.add(new JLabel("Part Name:"));       form.add(tfPartName);
        form.add(new JLabel("Current Stock:"));   form.add(tfStock);
        form.add(new JLabel("Rack Number:"));     form.add(tfRack);
        form.add(new JLabel("Vendor ID:"));       form.add(tfVendorId);
        form.add(new JLabel("Threshold:"));       form.add(threshNote);

        int r = JOptionPane.showConfirmDialog(this, form, "Add New Part",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        try {
            String partNo = tfPartNo.getText().trim();
            String name   = tfPartName.getText().trim();
            int    stock  = Integer.parseInt(tfStock.getText().trim());
            String rack   = tfRack.getText().trim();
            String vendor = tfVendorId.getText().trim();

            if (partNo.isEmpty()||name.isEmpty()) { JOptionPane.showMessageDialog(this,"Part # and Name required."); return; }
            if (store.inventory.getPartByNumber(partNo) != null) { JOptionPane.showMessageDialog(this,"Part # already exists."); return; }

            store.inventory.addPart(new MotorPart(partNo, name, stock, rack, vendor));
            // Recalculate thresholds for all parts (new part starts at 0 until it has sales)
            store.inventory.recalcAllThresholds(store.transactions);
            refreshTable();
            JOptionPane.showMessageDialog(this,
                "Part '" + name + "' added.\n");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,"Stock must be a whole number.");
        }
    }

    // ── Dialog: Record Sale ────────────────────────────────────────
    private void showRecordSaleDialog() {
        JTextField tfPartNo = new JTextField(10);
        JTextField tfQty    = new JTextField(6);
        JTextField tfPrice  = new JTextField(10);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        form.add(new JLabel("Part Number:"));     form.add(tfPartNo);
        form.add(new JLabel("Quantity Sold:"));   form.add(tfQty);
        form.add(new JLabel("Sale Price (Rs):")); form.add(tfPrice);

        int r = JOptionPane.showConfirmDialog(this, form, "Record Part Sale",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        try {
            String partNo = tfPartNo.getText().trim();
            int    qty    = Integer.parseInt(tfQty.getText().trim());
            double price  = Double.parseDouble(tfPrice.getText().trim());

            MotorPart part = store.inventory.getPartByNumber(partNo);
            if (part == null) { JOptionPane.showMessageDialog(this,"Part not found: "+partNo); return; }
            if (part.getCurrentStock() < qty) {
                JOptionPane.showMessageDialog(this,"Not enough stock. Available: "+part.getCurrentStock()); return; }

            store.inventory.updateStock(partNo, part.getCurrentStock() - qty);
            String txId = "TX" + String.format("%03d", store.transactions.size()+1);
            SaleTransaction tx = new SaleTransaction(txId, new Date(), qty, price, partNo);
            store.transactions.add(tx);

            // Recalculate thresholds for ALL parts after every sale
            store.inventory.recalcAllThresholds(store.transactions);

            refreshTable();

            // Show what the new threshold is for this part
            float newThresh = part.getThresholdValue();
            JOptionPane.showMessageDialog(this,
                String.format("Sale recorded!%nTransaction ID : %s%nRevenue        : Rs. %.2f%n" +
                              "New Auto-Threshold for %s : %.1f units",
                              txId, tx.calcRevenue(), part.getPartName(), newThresh));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,"Quantity must be whole number; Price can be decimal.");
        }
    }

    // ── Dialog: Restock ───────────────────────────────────────────
    private void showRestockDialog() {
        JTextField tfPartNo = new JTextField(10);
        JTextField tfQty    = new JTextField(8);

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        form.add(new JLabel("Part Number:"));  form.add(tfPartNo);
        form.add(new JLabel("Qty Received:")); form.add(tfQty);

        int r = JOptionPane.showConfirmDialog(this, form, "Restock Part",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;
        try {
            String partNo = tfPartNo.getText().trim();
            int    qty    = Integer.parseInt(tfQty.getText().trim());
            MotorPart part = store.inventory.getPartByNumber(partNo);
            if (part == null) { JOptionPane.showMessageDialog(this,"Part not found: "+partNo); return; }
            part.receiveStock(qty);
            refreshTable();
            JOptionPane.showMessageDialog(this,"Restocked! New stock: "+part.getCurrentStock());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,"Quantity must be a whole number.");
        }
    }

    // ── Dialog: Discontinue ───────────────────────────────────────
    private void showDiscontinueDialog() {
        JTextField tfPartNo = new JTextField(10);
        JPanel form = new JPanel(new GridLayout(1, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        form.add(new JLabel("Part Number:")); form.add(tfPartNo);

        int r = JOptionPane.showConfirmDialog(this, form, "Discontinue Part",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;
        String partNo = tfPartNo.getText().trim();
        MotorPart part = store.inventory.getPartByNumber(partNo);
        if (part == null) { JOptionPane.showMessageDialog(this,"Part not found: "+partNo); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Discontinue: "+part.getPartName()+"?","Confirm",JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            part.discontinue(); refreshTable();
            JOptionPane.showMessageDialog(this,part.getPartName()+" discontinued.");
        }
    }

    // ── Refresh ───────────────────────────────────────────────────
    public void refreshTable() {
        tableModel.setRowCount(0);
        for (MotorPart p : store.inventory.getPartsList()) {
            tableModel.addRow(new Object[]{
                p.getPartNumber(), p.getPartName(),
                p.getCurrentStock(),
                String.format("%.1f", p.getThresholdValue()),
                p.getRackNumber(), p.getVendorId(),
                p.getState().name()
            });
        }
    }

    // ── Row colour by state ────────────────────────────────────────
    static class StateRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean focus, int row, int col) {
            Component c = super.getTableCellRendererComponent(t,v,sel,focus,row,col);
            if (!sel) {
                String state = (String) t.getValueAt(row, 6);
                switch (state) {
                    case "LOW_STOCK":     c.setBackground(new Color(255,243,205)); break;
                    case "OUT_OF_STOCK":  c.setBackground(new Color(255,205,210)); break;
                    case "ORDER_PENDING": c.setBackground(new Color(213,245,227)); break;
                    case "RESTOCKED":     c.setBackground(new Color(205,245,255)); break;
                    case "DISCONTINUED":  c.setBackground(new Color(220,220,220)); break;
                    default:              c.setBackground(Color.WHITE);
                }
                c.setForeground(Color.BLACK);
            }
            return c;
        }
    }
}
