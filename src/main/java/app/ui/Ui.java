package app.ui;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import app.ui.manager.panelHandler;

import java.awt.*;

public class Ui extends JFrame {
    public Ui() {
        init();
    }

    private void init() {
        setTitle("Payit");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);
        setContentPane(new Home());
        panelHandler.getInstance().initApp(this);
    }

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 16));
        FlatMacLightLaf.setup();

        EventQueue.invokeLater(() -> new Ui().setVisible(true));
    }
}
