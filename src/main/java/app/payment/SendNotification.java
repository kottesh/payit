package app;

import java.awt.*;

public class SendNotification {

    public static boolean sendOTP(String otp) {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage("tray.gif");

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");

        trayIcon.setToolTip("System Tray Demo");

        try {tray.add(trayIcon);}
        catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }

        trayIcon.displayMessage("CUBIC-BANK", "OTP: " + otp + ". Don't share to anyone!", TrayIcon.MessageType.NONE);

        return true;
    }
}
