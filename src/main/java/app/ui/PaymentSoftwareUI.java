import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PaymentSoftwareUI extends JFrame {
    private JPanel mainPanel;
    private JTextField amountField;
    private JButton proceedButton;

    public PaymentSoftwareUI() {
        setTitle("Payment Software");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon("pexels-ivan-samkov-7620562.jpg"); 
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };

        JLabel titleLabel = new JLabel("Welcome to PayIT");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(70, 130, 180));

        JLabel subLabel = new JLabel("Don't think.....Jus PayIT!!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.GRAY);

        JLabel amountLabel = new JLabel("Enter Amount:");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        amountLabel.setForeground(Color.BLACK);

        amountField = new JTextField(15);
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));
        amountField.setPreferredSize(new Dimension(200, 30));
        amountField.setBorder(new RoundedBorder(30));

        proceedButton = new JButton("Proceed");
        proceedButton.setFont(new Font("Arial", Font.BOLD, 16));

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        JMenu aboutUsMenu = new JMenu("About Us");
        JMenuItem aboutUsItem1 = new JMenuItem("Our Company");
        JMenuItem aboutUsItem2 = new JMenuItem("Our Team");
        aboutUsMenu.add(aboutUsItem1);
        aboutUsMenu.add(aboutUsItem2);

        JMenu queriesMenu = new JMenu("Queries");
        JMenuItem queriesItem1 = new JMenuItem("FAQs");
        JMenuItem queriesItem2 = new JMenuItem("Support");
        queriesMenu.add(queriesItem1);
        queriesMenu.add(queriesItem2);

        JMenu contactUsMenu = new JMenu("Contact Us");
        JMenuItem contactUsItem1 = new JMenuItem("Email");
        JMenuItem contactUsItem2 = new JMenuItem("Phone");
        contactUsMenu.add(contactUsItem1);
        contactUsMenu.add(contactUsItem2);

        JMenu otherToolsMenu = new JMenu("Other Tools");
        JMenuItem otherToolsItem1 = new JMenuItem("Calculator");
        JMenuItem otherToolsItem2 = new JMenuItem("Converter");
        otherToolsMenu.add(otherToolsItem1);
        otherToolsMenu.add(otherToolsItem2);

        menuBar.add(aboutUsMenu);
        menuBar.add(queriesMenu);
        menuBar.add(contactUsMenu);
        menuBar.add(otherToolsMenu);

        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(menuBar)
                .addComponent(titleLabel)
                .addComponent(subLabel)
                .addGroup(layout.createSequentialGroup()
                        .addGap(100)
                        .addComponent(amountLabel)
                        .addComponent(amountField)
                        .addComponent(proceedButton)
                        .addGap(100)
                )
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(menuBar)
                .addGap(20)
                .addComponent(titleLabel)
                .addGap(20)
                .addComponent(subLabel)
                .addGap(40)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(amountLabel)
                        .addComponent(amountField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(proceedButton)
                )
        );

        proceedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open next page based on entered amount
                String amount = amountField.getText();
                // Add your logic here for what should happen when the proceed button is clicked
            }
        });

        // Add mainPanel to the frame
        getContentPane().add(mainPanel);
        
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new PaymentSoftwareUI();
                frame.setVisible(true);
            }
        });
    }

    class RoundedBorder implements Border {
        private int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius + 1, this.radius + 1, this.radius + 2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D graphics = (Graphics2D) g;
            graphics.setColor(c.getForeground());
            graphics.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}
