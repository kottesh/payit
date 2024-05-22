package app.ui.upi;

import javax.swing.*;

import com.formdev.flatlaf.FlatClientProperties;

import app.ui.manager.Manager;
import net.miginfocom.swing.MigLayout;

public class Upi extends JPanel {
    private JTextField fromVpa;
    private JTextField toVpa;
    private JTextField amount; 
    private JTextField remarks;
    private JButton makePay;

    public Upi() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));
        fromVpa = new JTextField();
        toVpa = new JTextField();
        amount = new JTextField();
        remarks = new JTextField();
        makePay = new JButton("Make Payment");

        fromVpa.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "jack.andrew@bank");
        toVpa.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "jannet.marsh@bank");

        makePay.addActionListener(e -> {
            if (fromVpa.getText().isEmpty() || toVpa.getText().isEmpty() || amount.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
            } else {
                Manager.getInstance().showUi(new Pin());
            }
        });
        
        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "fill, 250:500"));
        panel.putClientProperty(
            FlatClientProperties.STYLE, "" +  
            "arc: 20;" +
            "[light]background:darken(@background, 3%);" +
            "[dark]background:lighten(@background, 3%)"
        );

        JLabel title = new JLabel("UPI");
        title.putClientProperty(FlatClientProperties.STYLE, "" + "font:bold +12");

        panel.add(title);
        panel.add(new JLabel("From VPA"), "gapy 12");
        panel.add(fromVpa);
        panel.add(new JLabel("To VPA"), "gapy 8");
        panel.add(toVpa);
        panel.add(new JLabel("Amount"), "gapy 8");
        panel.add(amount);
        panel.add(new JLabel("Remarks"), "gapy 8");
        panel.add(remarks);
        panel.add(makePay, "gapy 12");

        add(panel);
    }
}
