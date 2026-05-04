package ui;

import model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class VendorPanel extends JPanel {
    private final DataStore store = DataStore.getInstance();
    private DefaultTableModel tableModel;

    public VendorPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildButtonBar(),  BorderLayout.SOUTH);
        refreshTable();
    }

    private JPanel buildTablePanel() {
        String[] cols = {"Vendor ID","Vendor Name","Address","Contact"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        table.getTableHeader().setBackground(new Color(33,97,140));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Vendor Directory"));
        JPanel p = new JPanel(new BorderLayout()); p.add(sp);
        return p;
    }

    private JPanel buildButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.setBackground(new Color(210, 220, 235));
        bar.setBorder(BorderFactory.createTitledBorder("Actions"));
        bar.add(makeBtn("Add New Vendor", new Color(0,150,60),   e -> showAddVendorDialog()));
        bar.add(makeBtn("Refresh",        new Color(60,60,60),   e -> refreshTable()));
        return bar;
    }

    private JButton makeBtn(String label, Color bg, java.awt.event.ActionListener al) {
        JButton b = new JButton(label);
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(170, 40));
        b.addActionListener(al);
        return b;
    }

    private void showAddVendorDialog() {
        JTextField tfId=new JTextField(10), tfName=new JTextField(14),
                   tfAddr=new JTextField(20), tfContact=new JTextField(12);
        JPanel form = new JPanel(new GridLayout(4,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        form.add(new JLabel("Vendor ID:"));    form.add(tfId);
        form.add(new JLabel("Vendor Name:")); form.add(tfName);
        form.add(new JLabel("Address:"));     form.add(tfAddr);
        form.add(new JLabel("Contact No:")); form.add(tfContact);
        int r = JOptionPane.showConfirmDialog(this, form, "Add New Vendor",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;
        String id=tfId.getText().trim(), name=tfName.getText().trim();
        if (id.isEmpty()||name.isEmpty()) { JOptionPane.showMessageDialog(this,"ID and Name required."); return; }
        store.vendors.put(id, new Vendor(id,name,tfAddr.getText().trim(),tfContact.getText().trim()));
        refreshTable();
        JOptionPane.showMessageDialog(this,"Vendor '"+name+"' added!");
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Vendor v : store.vendors.values())
            tableModel.addRow(new Object[]{v.getVendorId(),v.getVendorName(),v.getAddress(),v.getContactNumber()});
    }
}
