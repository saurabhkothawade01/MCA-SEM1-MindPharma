import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class LoginPage extends JFrame {

    static String loggedInUsername;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("MindPharma");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Opens the window maximized
        //setUndecorated(true); // Removes window decorations (title bar, close, minimize, maximize buttons)
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main content panel
         JPanel mainPanel = new JPanel(new BorderLayout());
         mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add some padding
 
         // Registration form panel
        JPanel panel = new JPanel(new GridBagLayout());
        Border titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2),"MindPharma - Your Path to Mental Wellness");
        ((TitledBorder) titledBorder).setTitleFont(new Font("Arial", Font.BOLD, 18)); // Set font, style, and size
        panel.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10))); // Add a titled border
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;

        JLabel titleLabel = new JLabel("MindPharma Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        panel.add(titleLabel, constraints);

        JLabel usernameLabel = new JLabel("Username:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        panel.add(usernameLabel, constraints);

        usernameField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(passwordLabel, constraints);

        passwordField = new JPasswordField(20);
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(passwordField, constraints);

        JButton loginButton = new JButton("Login");
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, constraints);

        JButton registerButton = new JButton("Register");
        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, constraints);
    
            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Open the login window
                    RegistrationPage registrationPage = new RegistrationPage();
                    registrationPage.setVisible(true);
                    LoginPage.this.dispose(); // Close the registration window
                }
            });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Validate that username, password, and confirm password are not empty
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginPage.this, "Please entered all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (validateLogin(username, password)) {
                    JOptionPane.showMessageDialog(LoginPage.this, "Login Successful!");
                    // Open the main dashboard or perform other actions
                    MainDashboard mainDashboard = new MainDashboard();
                    mainDashboard.setVisible(true);
                    LoginPage.this.dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginPage.this, "Invalid username or password. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

      mainPanel.add(panel, BorderLayout.CENTER);
      add(mainPanel);
      setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
      //pack();
      setLocationRelativeTo(null); // Center the registration window on the screen
    }


    // Getter for the logged-in username
    public static String getLoggedInUsername() {
        return loggedInUsername;
    }


    private boolean validateLogin(String username, String password) {
        // Perform database validation here (replace the following code with actual database connection)
        String jdbcUrl = "jdbc:mysql://localhost:3306/mindpharma";
        String dbUsername = "root";
        String dbPassword = "saurabh";

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
            String query = "SELECT * FROM user WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            loggedInUsername = username; 

            return resultSet.next(); // If there is a match, login is successful
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Login failed due to an exception
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginPage loginPage = new LoginPage();
                loginPage.setVisible(true);
            }
        });
    }
}
