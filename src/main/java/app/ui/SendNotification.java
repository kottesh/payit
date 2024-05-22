package app.ui;

import java.awt.*;

public class SendNotification {

    public static boolean sendOTP(String otp, String title) {
        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage("tray.gif");

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");

        trayIcon.setToolTip("System Tray Demo");

        try {tray.add(trayIcon);}
        catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }

        trayIcon.displayMessage(title, "OTP: " + otp + ". Don't share with anyone!", TrayIcon.MessageType.NONE);

        return true;
    }

    public static void main(String[] args) {
        String otp = "12345";
        if (!sendOTP(otp, "OTP")) {System.err.println("Could not initialize system tray.");}
        else{System.out.println("One Time Password" +"Your One Time Password is: "+otp);}
    }
}
