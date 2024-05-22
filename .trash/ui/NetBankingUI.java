import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


// JPanel for background image
class ImagePanel extends JPanel {
    private Image backgroundImage;

    public ImagePanel(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

public class NetBankingUI extends JFrame implements ActionListener {
    JButton loginButton;
    JPanel panel;
    JLabel usernamelabel, passwordlabel;
    final JTextField usernameField;
    final JPasswordField passwordField;

    NetBankingUI() {
        // Load background image from file
        ImageIcon backgroundImageIcon = new ImageIcon("background.jpg");
        Image backgroundImage = backgroundImageIcon.getImage();

        //background image
        ImagePanel imagePanel = new ImagePanel(backgroundImage);
        imagePanel.setLayout(new GridBagLayout());

        usernamelabel = new JLabel("Username:");
        usernameField = new JTextField(15);

        passwordlabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);

        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST; // Align to the left
        gbc.insets = new Insets(10, 10, 10, 10); //padding

        imagePanel.add(usernamelabel, gbc);
        gbc.gridy++;
        imagePanel.add(usernameField, gbc);
        gbc.gridy++;
        imagePanel.add(passwordlabel, gbc);
        gbc.gridy++;
        imagePanel.add(passwordField, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        imagePanel.add(loginButton, gbc);

        add(imagePanel, BorderLayout.CENTER);

        loginButton.addActionListener(this);
        setTitle("Online NetBanking System");
    }

    public void actionPerformed(ActionEvent ae) {
        String usernameValue = usernameField.getText();
        String passwordValue = passwordField.getText();

        if (usernameValue.equals("abcd") && passwordValue.equals("hello123")) {
            JOptionPane.showMessageDialog(this, "Login Successful! \n Welcome, " + usernameValue + " :)");

            //opening a new page
            JFrame newFrame = new JFrame("Online Netbanking System");
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(10, 10, 10, 10);

            JLabel fromLabel = new JLabel("From:");
            JTextField fromField = new JTextField(15);
            panel.add(fromLabel, gbc);
            gbc.gridy++;
            panel.add(fromField, gbc);

            JLabel toLabel = new JLabel("To:");
            JTextField toField = new JTextField(15);
            gbc.gridy++;
            panel.add(toLabel, gbc);
            gbc.gridy++;
            panel.add(toField, gbc);

            JLabel amountLabel = new JLabel("Amount:");
            JTextField amountField = new JTextField(15);
            gbc.gridy++;
            panel.add(amountLabel, gbc);
            gbc.gridy++;
            panel.add(amountField, gbc);

            JButton submitButton = new JButton("Submit");
            submitButton.setPreferredSize(new Dimension(80, 30));
            gbc.gridy++;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(submitButton, gbc);

            newFrame.getContentPane().add(panel);
            newFrame.setSize(500, 400);
            newFrame.setLocationRelativeTo(null);
            newFrame.setVisible(true);

            submitButton.addActionListener(e -> {
                String fromValue = fromField.getText();
                String toValue = toField.getText();
                String amountValue = amountField.getText();
                //System.out.println("From: " + fromValue + ", To: " + toValue + ", Amount: " + amountValue);

                // add another new page after clicking okay for inputting transac pswd
                JFrame newFrame2 = new JFrame("Online Netbanking System");
                JPanel panel2 = new JPanel(new GridBagLayout());
                GridBagConstraints gbc2 = new GridBagConstraints();
                gbc2.gridx = 0;
                gbc2.gridy = 0;
                gbc2.anchor = GridBagConstraints.WEST;
                gbc2.insets = new Insets(10, 10, 10, 10);

                JLabel TPLabel = new JLabel("Transaction Password:");
                JTextField TPField = new JPasswordField(15);
                panel2.add(TPLabel, gbc2);
                gbc2.gridy++;
                panel2.add(TPField, gbc2);

                JButton submitButton2 = new JButton("Submit");
                submitButton2.setPreferredSize(new Dimension(80, 30));
                gbc2.gridy++;
                gbc2.fill = GridBagConstraints.HORIZONTAL;
                panel2.add(submitButton2, gbc2);

                newFrame2.getContentPane().add(panel2);
                newFrame2.setSize(500, 400);
                newFrame2.setLocationRelativeTo(null);
                newFrame2.setVisible(true);

                submitButton2.addActionListener(e2 -> {
                    String TPValue = TPField.getText(); //getting the text from TPField

                    if (TPValue.equals("asdfghjkl")) {
                        JFrame newFrame3 = new JFrame("Online Netbanking System");
                        JPanel panel3 = new JPanel(new GridBagLayout());
                        GridBagConstraints gbc3 = new GridBagConstraints();
                        gbc3.gridx = 0;
                        gbc3.gridy = 0;
                        gbc3.anchor = GridBagConstraints.WEST;
                        gbc3.insets = new Insets(10, 10, 10, 10);

                        JLabel OTPLabel = new JLabel("OTP:");
                        JTextField OTPField = new JPasswordField(15);
                        panel3.add(OTPLabel, gbc3);
                        gbc3.gridy++;
                        panel3.add(OTPField, gbc3);

                        JButton submitButton3 = new JButton("Submit");
                        gbc3.gridy++;
                        gbc3.fill = GridBagConstraints.HORIZONTAL;
                        panel3.add(submitButton3, gbc3);

                        newFrame3.getContentPane().add(panel3);
                        newFrame3.setSize(500, 400);
                        newFrame3.setLocationRelativeTo(null);
                        newFrame3.setVisible(true);

                        submitButton3.addActionListener(e3 -> {
                            String OTPValue = OTPField.getText(); //getting the text from OTPField

                            if (OTPValue.equals("2004")) {
                                //displaying the transaction information on a new page
                                JFrame infoFrame = new JFrame("Transaction Information");
                                JPanel infoPanel = new JPanel(new GridBagLayout());
                                GridBagConstraints infoGBC = new GridBagConstraints();
                                infoGBC.gridx = 0;
                                infoGBC.gridy = 0;
                                infoGBC.anchor = GridBagConstraints.WEST;
                                infoGBC.insets = new Insets(10, 10, 10, 10);

                                //to get the current date and time
                                LocalDateTime now = LocalDateTime.now();
                                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(" HH:mm:ss");
                                String date = now.format(formatter1);
                                String time = now.format(formatter2);

                                JLabel fromInfoLabel = new JLabel(  "From               : " + fromValue);
                                JLabel toInfoLabel = new JLabel(    "To                 : " + toValue);
                                JLabel amountInfoLabel = new JLabel("Amount             : " + amountValue);
                                JLabel dateInfoLabel = new JLabel(  "Date               : " + date);
                                JLabel timeInfoLabel = new JLabel(  "Time               : " + time);
                                JLabel tsInfoLabel = new JLabel(    "Transaction status : Success");
                                //JLabel tidInfoLabel = new JLabel(      "Transaction ID     : ");

                                infoPanel.add(fromInfoLabel, infoGBC);
                                infoGBC.gridy++;
                                infoPanel.add(toInfoLabel, infoGBC);
                                infoGBC.gridy++;
                                infoPanel.add(amountInfoLabel, infoGBC);
                                infoGBC.gridy++;
                                infoPanel.add(dateInfoLabel, infoGBC);
                                infoGBC.gridy++;
                                infoPanel.add(timeInfoLabel, infoGBC);
                                infoGBC.gridy++;
                                infoPanel.add(tsInfoLabel, infoGBC);
                                infoGBC.gridy++;
                                //infoPanel.add(tidInfoLabel, infoGBC);

                                infoFrame.getContentPane().add(infoPanel);
                                infoFrame.setSize(400, 300);
                                infoFrame.setLocationRelativeTo(null);
                                infoFrame.setVisible(true);
                            } else {
                                JOptionPane.showMessageDialog(null, "Invalid OTP!");
                            }
                        });

                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid transaction password!");
                    }
                });

            });
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Online Netbanking System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            try {
                //setting the frame content to NetBankingUI instance
                NetBankingUI form = new NetBankingUI();
                form.setSize(500, 400);
                form.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        });
    }
}
