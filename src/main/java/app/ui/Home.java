package app.ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatClientProperties;
import app.ui.manager.*;
import app.ui.upi.*;

import net.miginfocom.swing.MigLayout;

public class Home extends JPanel {
    private JButton bank; 
    private JButton pay;
    private JLabel title;

    public Home() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));
        bank = new JButton("BANK");
        bank.addActionListener(e -> {
            Manager.getInstance().showUi(new Home());
        });
        pay = new JButton("PAY");
        pay.addActionListener(e -> {
            Manager.getInstance().showUi(new Upi());
        });
        
        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "fill, 250:500"));
        panel.putClientProperty(
            FlatClientProperties.STYLE, "" +  
            "arc: 20;" +
            "[light]background:darken(@background, 5%);" +
            "[dark]background:lighten(@background, 5%)"
        );

        title = new JLabel("Payit");
        title.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold +10");
        panel.add(title);
        panel.add(bank);
        panel.add(pay);
        add(panel);
    }
}
