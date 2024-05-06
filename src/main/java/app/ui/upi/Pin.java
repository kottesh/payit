package app.ui.upi;

import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import java.util.ArrayList;

public class Pin extends JPanel {
    private JPasswordField pin;
    private JButton proceed;
    private JButton cancel;
    private ArrayList<JButton> numpad;

    public Pin() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]"));

        pin = new JPasswordField();
        pin.putClientProperty(FlatClientProperties.STYLE,"" + "showRevealButton: true");

        proceed = new JButton("Proceed");
        cancel = new JButton("Cancel");
        numpad = new ArrayList<>(10);

        for (int i = 0; i < 10; i++) {
            numpad.add(new JButton(Integer.toString(i)));
        }
    
        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 35 45 30 45", "fill, 250:500"));
        panel.putClientProperty(
            FlatClientProperties.STYLE,"" +
            "arc: 20;" +
            "[light]background:darken(@background, 3%);" +
            "[dark]background:lighten(@background, 3%)"
        );

        panel.add(pin, "wrap");

        panel.add(proceed, "span, split 2, sizegroup btn, grow"); // Proceed and Cancel buttons share the row
        panel.add(cancel, "sizegroup btn, grow");

        panel.add(proceed, "span, split 2, sizegroup btn, grow");
        panel.add(cancel, "sizegroup btn, grow");
        add(panel);
    }
}
