package app.ui;

import java.awt.Component;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import com.formdev.flatlaf.FlatClientProperties;

import app.ui.manager.panelHandler;
import app.ui.services.netBanking;
import app.utils.DB;
import net.miginfocom.swing.MigLayout;

public class createAccount extends JPanel {
    public createAccount() {
        init();
        addListeners();
    }

    private void addListeners() {
        next.addActionListener(e -> {
            if (validateFields()) {
                if (insertCustomerData()) {
                    panelHandler.getInstance().show(new netBanking(accNo));
                } else {
                     JOptionPane.showMessageDialog(this, "Failed to insert record into database.",
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    String accNo = generateAccountNumber();
    private boolean insertCustomerData() {
        String customerSql = "INSERT INTO customers(customer_id, first_name, last_name, email, phone_number, address, city, state, postal_code, country, date_of_birth, gender) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String accountSql = "INSERT INTO accounts(acc_no, acc_type, customer_id, ifsc_code, balance, min_bal, bank_id, account_status, creation_date) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DB.connect();
            conn.setAutoCommit(false); // start transaction

            pstmt = conn.prepareStatement(customerSql);
            long customerId = System.currentTimeMillis() % 100000000L; // Generate customer_id
            pstmt.setLong(1, customerId);
            pstmt.setString(2, firstName.getText().trim());
            pstmt.setString(3, lastName.getText().trim());
            pstmt.setString(4, email.getText().trim());
            pstmt.setString(5, phoneNumber.getText().trim());
            pstmt.setString(6, address.getText().trim());
            pstmt.setString(7, city.getText().trim());
            pstmt.setString(8, state.getText().trim());
            pstmt.setString(9, postalCode.getText().trim());
            pstmt.setString(10, country.getText().trim());

            // parse date of birth
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date dobDate = dateFormat.parse(dob.getText().trim());
            pstmt.setDate(11, new java.sql.Date(dobDate.getTime()));

            // get selected gender
            String gender = male.isSelected() ? "M" : female.isSelected() ? "F" : other.isSelected() ? "O" : null;
            pstmt.setString(12, gender);


            pstmt.executeUpdate();

            // insert into accounts table
            pstmt = conn.prepareStatement(accountSql);
            String ifscCode = "IFSC00009";
            double minBal = 1500;
            long bankId = 1;
            String accountStatus = "Active";
            java.sql.Date creationDate = new java.sql.Date(System.currentTimeMillis());

            pstmt.setString(1, accNo);
            pstmt.setString(2, "SAVINGS");
            pstmt.setLong(3, customerId);
            pstmt.setString(4, ifscCode);
            pstmt.setDouble(5, 10000); // Initial balance, assuming 0
            pstmt.setDouble(6, minBal);
            pstmt.setLong(7, bankId);
            pstmt.setString(8, accountStatus);
            pstmt.setDate(9, creationDate);

            pstmt.executeUpdate();

            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException | ParseException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // rollback transaction in case of error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Generates a unique 10-digit account number.
     * The first digit is fixed to '9', followed by the last 9 digits of the current time in milliseconds.
     * This is a simple method and might not be suitable for all use cases.
     *
     * @return A unique 10-digit account number as a String.
     */
    public static String generateAccountNumber() {
        long currentTimeMillis = System.currentTimeMillis();
        String uniquePart = Long.toString(currentTimeMillis).substring(Long.toString(currentTimeMillis).length() - 9);
        
        // Prepend '9' to ensure the account number does not start with '0' and is 10 digits long
        String accountNumber = "9" + uniquePart;
        
        return accountNumber;
    }


    private boolean validateFields() {
        // check if first name is not empty
        if (firstName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            firstName.requestFocus();
            return false;
        }

        // check if last name is not empty
        if (lastName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Last Name cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            lastName.requestFocus();
            return false;
        }

        // check if email is valid
        if (!email.getText().trim().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Email is not valid", "Validation Error", JOptionPane.ERROR_MESSAGE);
            email.requestFocus();
            return false;
        }

        // check if phone number is valid
        if (phoneNumber.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in phone number", "Validation Error", JOptionPane.ERROR_MESSAGE);
            phoneNumber.requestFocus();
            return false;
        }

        // check if address is not empty
        if (address.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Address cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            address.requestFocus();
            return false;
        }

        // check if city is not empty
        if (city.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "City cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            city.requestFocus();
            return false;
        }

        // check if state is not empty
        if (state.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "State cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            state.requestFocus();
            return false;
        }

        // check if postal code is valid
        if (postalCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill in postal code", "Validation Error", JOptionPane.ERROR_MESSAGE);
            postalCode.requestFocus();
            return false;
        }

        // check if country is not empty
        if (country.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Country cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            country.requestFocus();
            return false;
        }

        // check if date of birth is valid
        if (!dob.getText().trim().matches("^\\d{2}-\\d{2}-\\d{4}$")) {
            JOptionPane.showMessageDialog(this, "Date of Birth is not valid", "Validation Error", JOptionPane.ERROR_MESSAGE);
            dob.requestFocus();
            return false;
        }

        // check if a gender is selected
        if (!male.isSelected() && !female.isSelected() && !other.isSelected()) {
            JOptionPane.showMessageDialog(this, "Please select a gender", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // all checks passed
        return true;
    }

    private void init() {
        setLayout(new MigLayout("fill, insets 20", "[center]", "[center]")); 

        firstName = new JTextField(); 
        firstName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "First Name");

        lastName = new JTextField(); 
        lastName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Last Name");

        email = new JTextField(); 
        email.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "someone@example.com");

        phoneNumber = new JTextField();
        phoneNumber.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "+91 xxxxx xxxxx");
        phoneNumber.setDocument(new PlainDocument());
        ((AbstractDocument) phoneNumber.getDocument()).setDocumentFilter(new docFilter(10, "\\d*"));

        address = new JTextField();
        address.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "door no, street");

        city = new JTextField();
        state = new JTextField();

        postalCode = new JTextField();
        postalCode.setDocument(new PlainDocument());
        ((AbstractDocument) postalCode.getDocument()).setDocumentFilter(new docFilter(6, "\\d*"));

        country = new JTextField();

        dob = new JTextField();
        dob.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "01-01-2000");

        // for gender
        male = new JRadioButton();
        female = new JRadioButton();
        other = new JRadioButton();
        //

        next = new JButton("Next");
        next.putClientProperty(
            FlatClientProperties.STYLE,
            "[light]background:darken(@background, 10%);" +
            "borderWidth: 0;" +
            "focusWidth: 0;"
        );
        JPanel pane = new JPanel(new MigLayout("fillx, wrap, insets 35 45 30 45", "fill, 400:600"));
        pane.putClientProperty(
            FlatClientProperties.STYLE, "arc: 20;" +
            "[light]background: darken(@background, 4%)");

        JLabel label1 = new JLabel("Infos");
        label1.putClientProperty(
            FlatClientProperties.STYLE,
            "font:bold +10;"
        );

        pane.add(label1);
        pane.add(new JLabel("Full Name"), "gapy 8, split 3");
        pane.add(firstName);
        pane.add(lastName);
        pane.add(new JLabel("Gender"), "gapy 8");
        pane.add(createGenderPanel());
        pane.add(new JLabel("DOB"), "gapy 8");
        pane.add(dob);
        pane.add(new JLabel("Email"), "gapy 8");
        pane.add(email);
        pane.add(new JLabel("Phone Number"), "gapy 8");
        pane.add(phoneNumber);
        pane.add(new JLabel("Address"), "gapy 8");
        pane.add(address);
        pane.add(new JLabel("City"), "gapy 8");
        pane.add(city);
        pane.add(new JLabel("State"), "gapy 8");
        pane.add(state); 
        pane.add(new JLabel("Postal Code"), "gapy 8");
        pane.add(postalCode); 
        pane.add(new JLabel("Country"), "gapy 8");
        pane.add(country); 

        pane.add(next, "gapy 14");

        add(pane);
    }

    private Component createGenderPanel() {
        JPanel panel = new JPanel(new MigLayout("insets 0"));
        panel.putClientProperty(FlatClientProperties.STYLE, "background: null");
        male = new JRadioButton("Male");
        female = new JRadioButton("Female");
        other = new JRadioButton("Others");

        ButtonGroup genders = new ButtonGroup();
        genders.add(male);
        genders.add(female);
        genders.add(other);

        panel.add(male);
        panel.add(female);
        panel.add(other);

        return panel;
    }

    class docFilter extends DocumentFilter {
        private final int maxLength;
        private final Pattern pattern;

        public docFilter(int maxLength, String regex) {
            this.maxLength = maxLength;
            this.pattern = Pattern.compile(regex);
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                throws BadLocationException {
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = currentText.substring(0, offset) + text + currentText.substring(offset);
            if ((newText.length() <= maxLength) && pattern.matcher(newText).matches()) {
                super.insertString(fb, offset, text, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
            if ((newText.length() <= maxLength) && pattern.matcher(newText).matches()) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    private JTextField firstName; 
    private JTextField lastName;
    private JTextField email;
    private JTextField phoneNumber;
    private JTextField address;
    private JTextField city;
    private JTextField state;
    private JTextField postalCode;
    private JTextField country;
    private JTextField dob;
    private JRadioButton male, female, other;
    private JButton next;
}
