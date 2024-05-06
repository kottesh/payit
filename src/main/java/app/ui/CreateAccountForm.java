import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateAccountForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JTextField ageField;
    private JComboBox<String> genderComboBox;

    public CreateAccountForm() {
        setTitle("Create Account for PayIT");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        ImageIcon imageIcon = new ImageIcon("Java/pexels-mia-stein-3894157.jpg");
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(300, -1, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newImage);
        JLabel imageLabel = new JLabel(imageIcon);
        leftPanel.add(imageLabel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridLayout(7, 1));

        // Components for create account form
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        emailField = new JTextField(20);
        ageField = new JTextField(5);
        genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});

        // Username Panel
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10)); // Adjust the gaps
        JLabel usernameLabel = new JLabel("Username:");
        customizeLabel(usernameLabel); // Customize the label
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        // Password Panel
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10)); // Adjust the gaps
        JLabel passwordLabel = new JLabel("Password:");
        customizeLabel(passwordLabel); // Customize the label
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        // Email Panel
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10)); // Adjust the gaps
        JLabel emailLabel = new JLabel("Email:");
        customizeLabel(emailLabel); // Customize the label
        emailPanel.add(emailLabel);
        emailPanel.add(emailField);

        // Age Panel
        JPanel agePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10)); // Adjust the gaps
        JLabel ageLabel = new JLabel("Age:");
        customizeLabel(ageLabel); // Customize the label
        agePanel.add(ageLabel);
        agePanel.add(ageField);

        // Gender Panel
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10)); // Adjust the gaps
        JLabel genderLabel = new JLabel("Gender:");
        customizeLabel(genderLabel); // Customize the label
        genderPanel.add(genderLabel);
        genderPanel.add(genderComboBox);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createAccountButton = new JButton("Create Account");
        buttonPanel.add(createAccountButton);

        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Add create account logic here
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String email = emailField.getText();
                String age = ageField.getText();
                String gender = (String) genderComboBox.getSelectedItem();
        
                // Add create account validation logic here
                if (!username.isEmpty() && !password.isEmpty() && !email.isEmpty() && !age.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Account created successfully!");
                    dispose(); // Close the current window
                    LoginForm loginForm = new LoginForm();
                    loginForm.setVisible(true); // Open the login form
                } else {
                    JOptionPane.showMessageDialog(null, "Please fill in all the fields.");
                }
            }
        });

        rightPanel.add(usernamePanel);
        rightPanel.add(passwordPanel);
        rightPanel.add(emailPanel);
        rightPanel.add(agePanel);
        rightPanel.add(genderPanel);
        rightPanel.add(buttonPanel);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        getContentPane().add(panel);
    }

    private void customizeLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.PLAIN, 16));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CreateAccountForm form = new CreateAccountForm();
                form.setVisible(true);
            }
        });
    }
}
