import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CompanyDetailsPage extends JFrame {
    private JTextField idField, companyNameField, emailField, contactField, addressField;
    private JTable companyTable;
    private int generatedId = 1;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mindpharma";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "saurabh";

    private Connection connection;

    public CompanyDetailsPage() {
        setTitle("MindPharma");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Opens the window maximized
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 5),
                "MindPharma - Your Path to Mental Wellness  ", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18))); // Add a titled border

        try {
            // Establish the database connection
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        }

        // Generate ID for the next medicine
        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM company")) {
            if (resultSet.next()) {
                generatedId = resultSet.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        getContentPane().add(mainPanel);

        // Components for entering new company details
        idField = new JTextField(10);
        idField.setEditable(false); // Make it non-editable
        idField.setText(String.valueOf(generatedId)); // Set the generated ID

        companyNameField = new JTextField();
        emailField = new JTextField();
        contactField = new JTextField();
        addressField = new JTextField();

        // Table to display existing company details
        companyTable = new JTable();
        refreshTable(); // Fetch and display company details in the table initially

        // Add ListSelectionListener to the company table to detect row selection events
        companyTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = companyTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        // Get data from the selected row and populate the text fields
                        idField.setText(companyTable.getValueAt(selectedRow, 0).toString());
                        companyNameField.setText(companyTable.getValueAt(selectedRow, 1).toString());
                        emailField.setText(companyTable.getValueAt(selectedRow, 2).toString());
                        contactField.setText(companyTable.getValueAt(selectedRow, 3).toString());
                        addressField.setText(companyTable.getValueAt(selectedRow, 4).toString());
                    }
                }
            }
        });

        // Buttons for Add, Update, and Delete operations
        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String companyName = companyNameField.getText();
                String email = emailField.getText();
                String contact = contactField.getText();

                if (isDuplicateCompany(companyName)) {
                    JOptionPane.showMessageDialog(CompanyDetailsPage.this,
                            "Duplicate company name.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else if (isDuplicateEmail(email)) {
                    JOptionPane.showMessageDialog(CompanyDetailsPage.this,
                            "Duplicate company email.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else if (isDuplicateContact(contact)) {
                    JOptionPane.showMessageDialog(CompanyDetailsPage.this,
                            "Duplicate company contact.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    addCompany();
                    refreshTable();
                    clearFields();
                    generatedId++;
                    idField.setText(String.valueOf(generatedId));
                }
            }
        });

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateCompany();
                refreshTable();
                clearFields();
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = companyTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int companyId = Integer.parseInt(idField.getText());
                    // Delete the selected row from the database
                    deleteCompany(companyId);
                    // Refresh the table after deleting the company
                    refreshTable();
                    // Clear the text fields
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(CompanyDetailsPage.this,
                            "Please select a row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current window
                MainDashboard mainDashboard = new MainDashboard();
                mainDashboard.setVisible(true); // Show the Main Dashboard
            }
        });

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFields(); // Clear text fields
            }
        });

        // Add Search Button and corresponding action listener
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show a dialog to input company name for searching
                String searchCompanyName = JOptionPane.showInputDialog(CompanyDetailsPage.this, "Enter Company Name:");
                if (searchCompanyName != null && !searchCompanyName.isEmpty()) {
                    searchCompany(searchCompanyName);
                }
            }
        });

        // Layout for entering new company details
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.add(new JLabel("Company Name:"));
        inputPanel.add(companyNameField);
        inputPanel.add(new JLabel("Email:"));
        inputPanel.add(emailField);
        inputPanel.add(new JLabel("Contact No.:"));
        inputPanel.add(contactField);
        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(addressField);

        // Layout for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(searchButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(new JScrollPane(companyTable), BorderLayout.CENTER);

        getContentPane().add(mainPanel);

        // Initialize the table with existing medicine details
        refreshTable();
    }

    private void searchCompany(String companyName) {
        DefaultTableModel tableModel = (DefaultTableModel) companyTable.getModel();
        tableModel.setRowCount(0); // Clear existing rows

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM company WHERE company_name LIKE ? AND username=?")) {
            statement.setString(1, "%" + companyName + "%");
            statement.setString(2, LoginPage.loggedInUsername);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String companyNameResult = resultSet.getString("company_name");
                String email = resultSet.getString("email");
                String contact = resultSet.getString("contact_no");
                String address = resultSet.getString("address");

                tableModel.addRow(new Object[] { id, companyNameResult, email, contact, address });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to search for company.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        idField.setText("");
        companyNameField.setText("");
        emailField.setText("");
        contactField.setText("");
        addressField.setText("");
    }

    private boolean isDuplicateCompany(String companyName) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM company WHERE company_name=? AND username=?")) {
            statement.setString(1, companyName);
            statement.setString(2, LoginPage.loggedInUsername);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isDuplicateEmail(String email) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM company WHERE email=? AND username=?")) {
            statement.setString(1, email);
            statement.setString(2, LoginPage.loggedInUsername);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isDuplicateContact(String contact) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM company WHERE contact_no=? AND username=?")) {
            statement.setString(1, contact);
            statement.setString(2, LoginPage.loggedInUsername);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addCompany() {
        String companyName = companyNameField.getText();
        String email = emailField.getText();
        String contact = contactField.getText();
        String address = addressField.getText();

        // Check for empty fields
        if (companyName.isEmpty() || email.isEmpty() || contact.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all the fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method to avoid further errors
        }

        if (isDuplicateCompany(companyName)) {
            JOptionPane.showMessageDialog(this, "Duplicate company name found. Cannot add duplicate medicine.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                    PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO company (company_name, email, contact_no, address, username) VALUES (?, ?, ?, ?, ?)")) {

                statement.setString(1, companyName);
                statement.setString(2, email);
                statement.setString(3, contact);
                statement.setString(4, address);
                statement.setString(5, LoginPage.loggedInUsername);

                int affectedRows = statement.executeUpdate();
                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(this, "Company added successfully.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add company.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to add company.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            refreshTable();
        }

    }

    private void updateCompany() {
        int companyId = Integer.parseInt(idField.getText());
        String companyName = companyNameField.getText();
        String email = emailField.getText();
        String contact = contactField.getText();
        String address = addressField.getText();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE company SET company_name = ?, email = ?, contact_no = ?, address = ? WHERE id = ? AND username = ?")) {

            statement.setString(1, companyName);
            statement.setString(2, email);
            statement.setString(3, contact);
            statement.setString(4, address);
            statement.setInt(5, companyId);
            statement.setString(6, LoginPage.loggedInUsername);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Company updated successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update company.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update company.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        refreshTable();
    }

    private void deleteCompany(int id) {
        int companyId = Integer.parseInt(idField.getText());

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM company WHERE id = ? AND username = ?")) {

            statement.setInt(1, companyId);
            statement.setString(2, LoginPage.loggedInUsername);

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Company deleted successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete company.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete company.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        refreshTable();
    }

    private void refreshTable() {
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Company ID");
        tableModel.addColumn("Company Name");
        tableModel.addColumn("Email");
        tableModel.addColumn("Contact No.");
        tableModel.addColumn("Address");

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM company WHERE username = ?")) {

            statement.setString(1, LoginPage.loggedInUsername);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String companyName = resultSet.getString("company_name");
                String email = resultSet.getString("email");
                String contact = resultSet.getString("contact_no");
                String address = resultSet.getString("address");

                tableModel.addRow(new Object[] { id, companyName, email, contact, address });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch company details.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        companyTable.setModel(tableModel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CompanyDetailsPage companyDetailsPage = new CompanyDetailsPage();
                companyDetailsPage.setVisible(true);
            }
        });
    }
}
