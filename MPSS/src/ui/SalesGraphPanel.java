package ui;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SalesGraphPanel extends JPanel {
    private final DataStore store = DataStore.getInstance();
    private Map<String, Integer> monthlyData;

    public SalesGraphPanel() {
        setLayout(new BorderLayout(5,5));
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT,10,8));
        topBar.setBackground(new Color(210,220,235));
        topBar.setBorder(BorderFactory.createTitledBorder("Actions"));

        JButton btnRefresh = new JButton("Refresh Graph");
        btnRefresh.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnRefresh.setBackground(new Color(0,130,50)); btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btnRefresh.setOpaque(true); btnRefresh.setBorderPainted(false); btnRefresh.setFocusPainted(false);
        btnRefresh.setPreferredSize(new Dimension(170,40));
        btnRefresh.addActionListener(e -> { loadData(); repaint(); });
        topBar.add(btnRefresh);

        add(topBar, BorderLayout.NORTH);
        add(new BarChartCanvas(), BorderLayout.CENTER);
        loadData();
    }

    private void loadData() {
        monthlyData = store.reportGenerator.generateMonthGraph(store.inventory, store.transactions);
    }

    class BarChartCanvas extends JPanel {
        BarChartCanvas() { setBackground(Color.WHITE); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (monthlyData==null||monthlyData.isEmpty()) return;
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int W=getWidth(),H=getHeight(),mL=60,mR=20,mT=40,mB=50;
            int cW=W-mL-mR, cH=H-mT-mB;
            int maxVal=monthlyData.values().stream().mapToInt(Integer::intValue).max().orElse(1);
            if(maxVal==0) maxVal=1;
            g2.setColor(Color.DARK_GRAY);
            g2.drawLine(mL,mT,mL,mT+cH);
            g2.drawLine(mL,mT+cH,mL+cW,mT+cH);
            g2.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,10));
            for(int i=0;i<=5;i++){
                int val=maxVal*i/5, y=mT+cH-cH*i/5;
                g2.setColor(new Color(200,200,200)); g2.drawLine(mL+1,y,mL+cW,y);
                g2.setColor(Color.DARK_GRAY); g2.drawString(String.valueOf(val),mL-32,y+4);
            }
            String[] months=monthlyData.keySet().toArray(new String[0]);
            int n=months.length;
            int barW=(cW/n)-6;
            Color[] palette={new Color(41,128,185),new Color(39,174,96),new Color(142,68,173),
                new Color(230,126,34),new Color(231,76,60),new Color(26,188,156),
                new Color(52,73,94),new Color(200,160,0),new Color(192,57,43),
                new Color(44,62,80),new Color(100,120,130),new Color(93,173,226)};
            for(int i=0;i<n;i++){
                int val=monthlyData.get(months[i]);
                int barH=val==0?2:(int)((double)val/maxVal*cH);
                int x=mL+i*(cW/n)+3, y=mT+cH-barH;
                g2.setColor(palette[i%palette.length]); g2.fillRoundRect(x,y,barW,barH,4,4);
                g2.setColor(palette[i%palette.length].darker()); g2.drawRoundRect(x,y,barW,barH,4,4);
                g2.setColor(Color.BLACK); g2.setFont(new Font(Font.SANS_SERIF,Font.BOLD,11));
                String vs=String.valueOf(val); int sw=g2.getFontMetrics().stringWidth(vs);
                g2.drawString(vs,x+barW/2-sw/2,y-3);
                g2.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,11));
                int lw=g2.getFontMetrics().stringWidth(months[i]);
                g2.drawString(months[i],x+barW/2-lw/2,mT+cH+16);
            }
            g2.setFont(new Font(Font.SANS_SERIF,Font.BOLD,13));
            g2.setColor(new Color(33,97,140));
            String t="Monthly Units Sold — 2026";
            g2.drawString(t,W/2-g2.getFontMetrics().stringWidth(t)/2,mT-14);
        }
    }
}
