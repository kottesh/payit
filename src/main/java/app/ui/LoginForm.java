import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Login / Sign Up for PayIT");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 550);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel to hold all components
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Left panel for image
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        ImageIcon imageIcon = new ImageIcon("Java/pexels-mia-stein-3894157.jpg");
        Image image = imageIcon.getImage();
        Image newImage = image.getScaledInstance(300, -1, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newImage);
        JLabel imageLabel = new JLabel(imageIcon);
        leftPanel.add(imageLabel, BorderLayout.CENTER);

        // Right panel for login/signup form
        JPanel rightPanel = new JPanel(new GridLayout(0, 1));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

        // Username Panel
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10)); // Adjust the gaps
        JLabel usernameLabel = new JLabel("Username:");
        customizeLabel(usernameLabel); // Customize the label
        usernamePanel.add(usernameLabel);
        usernameField = new JTextField(20);
        customizeTextField(usernameField); // Customize the text field
        usernamePanel.add(usernameField);

        // Password Panel
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10)); // Adjust the gaps
        JLabel passwordLabel = new JLabel("Password:");
        customizeLabel(passwordLabel); // Customize the label
        passwordPanel.add(passwordLabel);
        passwordField = new JPasswordField(20);
        customizeTextField(passwordField); // Customize the text field
        passwordPanel.add(passwordField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton loginButton = new JButton("Login");
        JButton signUpButton = new JButton("Create Account");
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        // Add components to right panel
        rightPanel.add(usernamePanel);
        rightPanel.add(passwordPanel);
        rightPanel.add(buttonPanel);

        // Add left and right panels to main panel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        getContentPane().add(mainPanel);

        // Action Listener for Login Button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (isValidLogin(username, password)) {
                    JOptionPane.showMessageDialog(null, "Logging in as " + username);
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password. Please try again.");
                }
                dispose();
            }
        });

        // Action Listener for Sign Up Button
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateAccountForm signUpForm = new CreateAccountForm();
                signUpForm.setVisible(true);

                dispose();
            }
        });
    }

    private void customizeTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
        textField.setPreferredSize(new Dimension(200, 30));
    }

    private boolean isValidLogin(String username, String password) {
        // Add your login validation logic here
        return !username.isEmpty() && !password.isEmpty();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginForm form = new LoginForm();
                form.setVisible(true);
            }
        });
    }

    private void customizeLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.PLAIN, 16));
    }
}
