package app.ui;

import javax.swing.*;

import app.ui.manager.Manager;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import java.awt.*;

public class Ui extends JFrame {
    Ui() {
        setTitle("Payit");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setContentPane(new Home());
        Manager.getInstance().initApp(this);
    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 16));
        FlatMacLightLaf.setup();
        EventQueue.invokeLater(
            () -> new Ui().setVisible(true)
        );
    }
}
