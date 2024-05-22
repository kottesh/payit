package app.ui;

import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;

import app.ui.manager.panelHandler;
import net.miginfocom.swing.MigLayout;
import java.awt.*;

public class Home extends JPanel {
    private JButton bank;
    private JButton pay;
    Home() {
        init();
    }

    private void init() {
        bank = new JButton("BANK");
        bank.setPreferredSize(new Dimension(100, 50));
        bank.addActionListener(e -> {
            panelHandler.getInstance().show(new Login());
        });
        pay = new JButton("PAY");
        pay.setPreferredSize(new Dimension(100, 50));
        pay.addActionListener(e -> {
            panelHandler.getInstance().show(new ReceiversInfo());
            System.out.println("Passing to Payment Gateway");
        });

        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));
        JPanel pane = new JPanel(new MigLayout("fillx, wrap, insets 35 45 30 45", "fill, 300:400"));
        pane.putClientProperty(
            FlatClientProperties.STYLE, "arc: 20;" +
            "[light]background: darken(@background, 3%);"
        );

        JLabel header = new JLabel("Payit");
        header.putClientProperty(
            FlatClientProperties.STYLE, 
            "font: bold +16;"
        );

        pane.add(header);
        JLabel caption = new JLabel("Revolutionizing digital payments");
        caption.putClientProperty(
            FlatClientProperties.STYLE, 
            "[light]foreground: lighten(@foreground, 30%);"
        );
        pane.add(caption, "span, gapbottom 14, align center");
        pane.add(bank, "gapy 12, split 2");
        pane.add(pay);
        add(pane);
    }
}
