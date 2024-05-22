package app.ui.manager;

import javax.swing.JComponent;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import java.awt.*;

import app.ui.Ui;

public class panelHandler {
    private Ui ui;
    private static panelHandler instance;

    public static panelHandler getInstance() {
        if (instance == null) {
            instance = new panelHandler();
        }
        return instance;
    }

    public void initApp(Ui ui) {
        this.ui = ui;
    }

    public void show(JComponent form) {
        EventQueue.invokeLater(() -> {
            FlatAnimatedLafChange.showSnapshot();
            ui.setContentPane(form);
            ui.revalidate();
            ui.repaint();
            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }
}
