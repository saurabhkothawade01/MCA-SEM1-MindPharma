import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManageUser extends JFrame {

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField contactNumberField;
    private JTextField addressField;
    private JTextField dobField;
    private JTextField genderField;
    private JLabel photoLabel;

    public ManageUser(String username) {

        setTitle("MindPharma");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add some padding

        // User information panel
        JPanel panel = new JPanel(new GridBagLayout());
        Border userBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 5),
                "MindPharma - User Information  ");
        ((TitledBorder) userBorder).setTitleFont(new Font("Arial", Font.BOLD, 18));
        panel.setBorder(
                BorderFactory.createCompoundBorder(userBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
                GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        // Fetch user information from the database based on the username
        UserData userData = getUserData(username);

        // Username label and field (non-editable)
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(userData.getUsername());
        usernameField.setEditable(false);

        // Email label and field
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(userData.getEmail());

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(userData.getPassword());

        // Contact Number label and field
        JLabel contactNumberLabel = new JLabel("Contact Number:");
        contactNumberField = new JTextField(userData.getContactNumber());

        // Address label and field
        JLabel addressLabel = new JLabel("Address:");
        addressField = new JTextField(userData.getAddress());

        // Date of Birth label and field
        JLabel dobLabel = new JLabel("Date of Birth:");
        dobField = new JTextField(userData.getDob());

        // Gender label and field
        JLabel genderLabel = new JLabel("Gender:");
        genderField = new JTextField(userData.getGender());

        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(usernameLabel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 1;
        panel.add(usernameField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(emailLabel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 2;
        panel.add(emailField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(passwordLabel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 3;
        panel.add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        panel.add(contactNumberLabel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 4;
        panel.add(contactNumberField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        panel.add(addressLabel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 5;
        panel.add(addressField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 6;
        panel.add(dobLabel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 6;
        panel.add(dobField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 7;
        panel.add(genderLabel, constraints);

        constraints.gridx = 2;
        constraints.gridy = 7;
        panel.add(genderField, constraints);

        // Save and Cancel buttons for user information
        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update user information in the database
                boolean success = updateUser(username,
                        emailField.getText(),
                        new String(passwordField.getPassword()),
                        contactNumberField.getText(),
                        addressField.getText(),
                        dobField.getText(),
                        genderField.getText());

                if (success) {
                    JOptionPane.showMessageDialog(ManageUser.this, "User information updated successfully.");
                    dispose(); // Close the current window
                    MainDashboard mainDashboard = new MainDashboard();
                    mainDashboard.setVisible(true); // Show the Main Dashboard
                } else {
                    JOptionPane.showMessageDialog(ManageUser.this, "Failed to update user information.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainDashboard mainDashboard = new MainDashboard();
                mainDashboard.setVisible(true);
                ManageUser.this.dispose();
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 8;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(saveButton, constraints);

        constraints.gridx = 2;
        constraints.gridy = 8;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(cancelButton, constraints);

        // Photo panel
        JPanel photoPanel = new JPanel(new BorderLayout());
        Border photoBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 5),
                "MindPharma - User Photo  ");
        ((TitledBorder) photoBorder).setTitleFont(new Font("Arial", Font.BOLD, 18));
        photoPanel.setBorder(
                BorderFactory.createCompoundBorder(photoBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // Photo label (You can replace this with an actual image)
        photoLabel = new JLabel("User Photo Placeholder", SwingConstants.CENTER);
        photoLabel.setVerticalAlignment(SwingConstants.CENTER);
        photoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        photoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    loadAndDisplayImage();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        displayUserImageFromDatabase(); // New method for displaying image from the database
  
        photoPanel.add(photoLabel, BorderLayout.CENTER);

        // Adding user and photo panels to the main panel
        mainPanel.add(panel, BorderLayout.WEST);
        mainPanel.add(photoPanel, BorderLayout.CENTER);

        add(mainPanel);
        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setLocationRelativeTo(null);
    }

    private void loadAndDisplayImage() throws SQLException {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Save the image to the database
                saveImageToDatabase(selectedFile);

                // Display the selected image with a border
                int targetWidth = 500; // Set the desired width
                int targetHeight = 500; // Set the desired height

                ImageIcon originalIcon = new ImageIcon(selectedFile.getAbsolutePath());
                Image originalImage = originalIcon.getImage();

                // Scale the image to fit within the specified dimensions
                Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);

                // Create a JPanel to contain the JLabel with a border
                JPanel imagePanel = new JPanel(null); // Set the layout to null for absolute positioning

                // Convert 2cm to pixels (assuming 1 inch = 2.54 cm and using a standard DPI of
                // 96)
                int borderSize = (int) (2 * Toolkit.getDefaultToolkit().getScreenResolution() / 2.54);

                imagePanel.setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize, borderSize, borderSize));

                // Create a JLabel to display the image
                JLabel imageLabel = new JLabel(scaledIcon);

                // Calculate the position to center the image within the JPanel
                int x = (targetWidth - scaledIcon.getIconWidth()) / 2 + borderSize;
                int y = (targetHeight - scaledIcon.getIconHeight()) / 2 + borderSize;

                // Set the bounds of the imageLabel within the imagePanel
                imageLabel.setBounds(x, y, scaledIcon.getIconWidth(), scaledIcon.getIconHeight());

                // Add the JLabel to the JPanel
                imagePanel.add(imageLabel);

                // Set the layout of photoLabel to null to allow absolute positioning
                photoLabel.setLayout(null);

                // Calculate the position to center the imagePanel within the photoLabel
                int panelX = (photoLabel.getWidth() - targetWidth - 2 * borderSize) / 2;
                int panelY = (photoLabel.getHeight() - targetHeight - 2 * borderSize) / 2;

                // Set the bounds of the imagePanel within the photoLabel
                imagePanel.setBounds(panelX, panelY, targetWidth + 2 * borderSize, targetHeight + 2 * borderSize);

                // Add the imagePanel to the photoLabel
                photoLabel.removeAll(); // Clear previous components
                photoLabel.add(imagePanel);

                // Refresh the UI
                photoLabel.revalidate();
                photoLabel.repaint();
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading image.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayUserImageFromDatabase() {
        try {
            // Retrieve user's image from the database based on the username
            byte[] imageData = getUserImageFromDatabase(usernameField.getText());

            if (imageData != null) {
                // Convert byte array to Image
                Image image = new ImageIcon(imageData).getImage();

                // Resize the image to 500x500
                int targetWidth = 500;
                int targetHeight = 500;
                Image scaledImage = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);

                // Create an ImageIcon from the scaled image
                ImageIcon resizedImageIcon = new ImageIcon(scaledImage);

                // Display the resized image
                photoLabel.setIcon(resizedImageIcon);
            } else {
                // Handle the case where the user has no image in the database
                System.out.println("User has no image in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database-related errors
        }
    }

    // Method to retrieve user's image from the database
    private byte[] getUserImageFromDatabase(String username) throws SQLException {
        String jdbcUrl = "jdbc:mysql://localhost:3306/mindpharma";
        String dbUsername = "root";
        String dbPassword = "saurabh";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "SELECT photo FROM user WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Get the image data from the result set
                        return resultSet.getBytes("photo");
                    }
                }
            }
        }

        return null; // Return null if the user has no image in the database
    }

    // Method to save an image to the database (placeholder, you need to modify this
    // based on your database schema)
    private void saveImageToDatabase(File file) throws IOException, SQLException {
        // Replace this with your database connection and insert query
        String jdbcUrl = "jdbc:mysql://localhost:3306/mindpharma";
        String dbUsername = "root";
        String dbPassword = "saurabh";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword)) {
            String query = "UPDATE user SET photo = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    preparedStatement.setBinaryStream(1, fis, (int) file.length());
                    preparedStatement.setString(2, usernameField.getText());
                    preparedStatement.executeUpdate();
                }
            }
        }
    }

    // Method to fetch user data from the database
    private UserData getUserData(String username) {
        // Replace this with your database connection and query
        // The following is just a placeholder, you should use PreparedStatement
        String jdbcUrl = "jdbc:mysql://localhost:3306/mindpharma";
        String dbUsername = "root";
        String dbPassword = "saurabh";

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
            String query = "SELECT * FROM user WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String fetchedUsername = resultSet.getString("username");
                String fetchedEmail = resultSet.getString("email");
                String fetchedPassword = resultSet.getString("password");
                String fetchedContactNumber = resultSet.getString("contact_number");
                String fetchedAddress = resultSet.getString("address");
                String fetchedDob = resultSet.getString("dob");
                String fetchedGender = resultSet.getString("gender");

                return new UserData(fetchedUsername, fetchedEmail, fetchedPassword, fetchedContactNumber,
                        fetchedAddress, fetchedDob, fetchedGender);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null if the user is not found (handle this in your code)
    }

    // Method to update user data in the database
    private boolean updateUser(String username, String email, String password, String contactNumber, String address,
            String dob, String gender) {
        // Replace this with your database connection and update query
        // The following is just a placeholder, you should use PreparedStatement
        String jdbcUrl = "jdbc:mysql://localhost:3306/mindpharma";
        String dbUsername = "root";
        String dbPassword = "saurabh";

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
            String query = "UPDATE user SET email = ?, password = ?, contact_number = ?, address = ?, dob = ?, gender = ? WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, contactNumber);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, dob);
            preparedStatement.setString(6, gender);
            preparedStatement.setString(7, username);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // Return false if the update fails (handle this in your code)
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Example: Create a ManageUser for the user "exampleUser"
                ManageUser manageUser = new ManageUser("exampleUser");
                manageUser.setVisible(true);
            }
        });
    }
}

// A simple class to hold user data
class UserData {
    private String username;
    private String email;
    private String password;
    private String contactNumber;
    private String address;
    private String dob;
    private String gender;

    public UserData(String username, String email, String password, String contactNumber, String address, String dob,
            String gender) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.contactNumber = contactNumber;
        this.address = address;
        this.dob = dob;
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getDob() {
        return dob;
    }

    public String getGender() {
        return gender;
    }
}
