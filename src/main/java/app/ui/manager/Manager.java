package app.ui.manager;

import app.ui.*;
import javax.swing.*;
import java.awt.*;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

public class Manager {
   private Ui ui; 
   private static Manager instance;

   public static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    public void initApp(Ui ui) {
        this.ui = ui;
    }

    public void showUi(JComponent component) {
        EventQueue.invokeLater(() -> {
                FlatAnimatedLafChange.showSnapshot();
                ui.setContentPane(component);
                ui.revalidate();
                ui.repaint();
                FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }
}
