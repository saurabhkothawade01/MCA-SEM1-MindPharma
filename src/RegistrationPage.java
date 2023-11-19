import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class RegistrationPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField contactNumberField;
    private JTextField addressField;
    private JTextField dobField;
    private JComboBox<String> genderComboBox;

    public RegistrationPage() {

        setTitle("MindPharma");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add some padding

        // Registration form panel
        JPanel panel = new JPanel(new GridBagLayout());
        Border titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 5),
                "MindPharma - Your Path to Mental Wellness  ");
        ((TitledBorder) titledBorder).setTitleFont(new Font("Arial", Font.BOLD, 18)); // Set font, style, and size
        panel.setBorder(
                BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10))); // Add
                                                                                                                    // a
                                                                                                                    // titled
                                                                                                                    // border
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;

        JLabel titleLabel = new JLabel("MindPharma Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        panel.add(titleLabel, constraints);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        panel.add(usernameLabel, constraints);

        usernameField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(usernameField, constraints);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(passwordLabel, constraints);

        passwordField = new JPasswordField(20);
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(passwordField, constraints);

        // Confirm Password
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(confirmPasswordLabel, constraints);

        confirmPasswordField = new JPasswordField(20);
        constraints.gridx = 1;
        constraints.gridy = 3;
        panel.add(confirmPasswordField, constraints);

        // Name
        JLabel nameLabel = new JLabel("Name:");
        constraints.gridx = 0;
        constraints.gridy = 4;
        panel.add(nameLabel, constraints);

        nameField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 4;
        panel.add(nameField, constraints);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        constraints.gridx = 0;
        constraints.gridy = 5;
        panel.add(emailLabel, constraints);

        emailField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 5;
        panel.add(emailField, constraints);

        // Contact Number
        JLabel contactNumberLabel = new JLabel("Contact Number:");
        constraints.gridx = 0;
        constraints.gridy = 6;
        panel.add(contactNumberLabel, constraints);

        contactNumberField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 6;
        panel.add(contactNumberField, constraints);

        // Address
        JLabel addressLabel = new JLabel("Address:");
        constraints.gridx = 0;
        constraints.gridy = 7;
        panel.add(addressLabel, constraints);

        addressField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 7;
        panel.add(addressField, constraints);

        // Date of Birth (DOB)
        JLabel dobLabel = new JLabel("Date of Birth:");
        constraints.gridx = 0;
        constraints.gridy = 8;
        panel.add(dobLabel, constraints);

        dobField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 8;
        panel.add(dobField, constraints);

        // Gender
        JLabel genderLabel = new JLabel("Gender:");
        constraints.gridx = 0;
        constraints.gridy = 9;
        panel.add(genderLabel, constraints);

        String[] genders = { "Male", "Female", "Other" };
        genderComboBox = new JComboBox<>(genders);
        constraints.gridx = 1;
        constraints.gridy = 9;
        panel.add(genderComboBox, constraints);

        JButton registerButton = new JButton("Register");
        constraints.gridx = 0;
        constraints.gridy = 10;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, constraints);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String name = nameField.getText();
                String email = emailField.getText();
                String contactNumber = contactNumberField.getText();
                String address = addressField.getText();
                String dobText = dobField.getText();

                // Validate that username, password, and confirm password are not empty
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty()
                        || email.isEmpty() || contactNumber.isEmpty() || address.isEmpty() || dobText.isEmpty()) {
                    JOptionPane.showMessageDialog(RegistrationPage.this, "Please entered all fields.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Convert the date format from 'dd/MM/yyyy' to 'yyyy-MM-dd'
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date dob = null;

                try {
                    dob = inputFormat.parse(dobText);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                    // Handle the parse exception (invalid date format)
                    JOptionPane.showMessageDialog(RegistrationPage.this, "Invalid date format. Please use dd/MM/yyyy.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String formattedDob = outputFormat.format(dob);
                String gender = (String) genderComboBox.getSelectedItem();

                if (password.equals(confirmPassword)) {
                    if (registerUser(username, password, name, email, contactNumber, address, formattedDob, gender)) {
                        JOptionPane.showMessageDialog(RegistrationPage.this, "Registration Successful!");
                        // Clear all text fields
                        usernameField.setText("");
                        passwordField.setText("");
                        confirmPasswordField.setText("");
                        nameField.setText("");
                        emailField.setText("");
                        contactNumberField.setText("");
                        addressField.setText("");
                        dobField.setText("");

                        // Open the login window
                        dispose();
                        LoginPage loginPage = new LoginPage();
                        loginPage.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(RegistrationPage.this, "Registration failed. Please try again.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(RegistrationPage.this, "Passwords do not match. Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton loginButton = new JButton("Login");
        constraints.gridx = 0;
        constraints.gridy = 12;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, constraints);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the login window
                LoginPage loginPage = new LoginPage();
                loginPage.setVisible(true);
                RegistrationPage.this.dispose(); // Close the registration window
            }
        });

        mainPanel.add(panel, BorderLayout.CENTER);
        add(mainPanel);
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setLocationRelativeTo(null); // Center the registration window on the screen
    }

    private boolean registerUser(String username, String password, String name, String email,
            String contactNumber, String address, String dob, String gender) {

        // Check if the username or contact number already exists
        if (isUsernameExists(username)) {
            JOptionPane.showMessageDialog(RegistrationPage.this, "Username is already exists.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false; // Registration failed due to existing username
        }

        if (isContactNumberExists(contactNumber)) {
            JOptionPane.showMessageDialog(RegistrationPage.this, "Mobile Number is already exists.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false; // Registration failed due to existing contact number
        }

        if (isEmailExists(email)) {
            JOptionPane.showMessageDialog(RegistrationPage.this, "Email ID is already exists.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false; // Registration failed due to existing emailid
        }

        // Perform database registration here
        String jdbcUrl = "jdbc:mysql://localhost:3306/mindpharma";
        String dbUsername = "root";
        String dbPassword = "saurabh";

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
            String query = "INSERT INTO user (username, password, name, email, contact_number, address, dob, gender) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, name);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, contactNumber);
            preparedStatement.setString(6, address);
            preparedStatement.setString(7, dob);
            preparedStatement.setString(8, gender);
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // If registration is successful, rowsAffected will be greater than 0
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Registration failed due to an exception
        }
    }

    private boolean isUsernameExists(String username) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mindpharma";
        String dbUsername = "root";
        String dbPassword = "saurabh";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT COUNT(*) FROM user WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // Return true if the username exists, otherwise false
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // Return false in case of an exception or other issues
    }

    private boolean isContactNumberExists(String contactNumber) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mindpharma";
        String dbUsername = "root";
        String dbPassword = "saurabh";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT COUNT(*) FROM user WHERE contact_number = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, contactNumber);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // Return true if the contact number exists, otherwise false
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isEmailExists(String emailid) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mindpharma";
        String dbUsername = "root";
        String dbPassword = "saurabh";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT COUNT(*) FROM user WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, emailid);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0; // Return true if the emailid exists, otherwise false
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                RegistrationPage registrationPage = new RegistrationPage();
                registrationPage.setVisible(true);
            }
        });
    }
}
