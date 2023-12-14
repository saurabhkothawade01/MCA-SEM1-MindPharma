import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;

public class MedicineDetailsPage extends JFrame {

    private JTable medicineTable;
    private JTextField idField, medicineNameField, companyNameField, categoryField, quantityField, priceField;
    private JComboBox<String> companyComboBox;
    private JComboBox<String> categoryComboBox;
    private JDateChooser expiryDateChooser;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mindpharma";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "saurabh";

    private int generatedId = 1;

    private Connection connection;

    public MedicineDetailsPage() {

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
                ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM medicine")) {
            if (resultSet.next()) {
                generatedId = resultSet.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        idField = new JTextField(10);
        idField.setEditable(false); // Make it non-editable
        idField.setText(String.valueOf(generatedId)); // Set the generated ID

        // Table model for displaying medicine details
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Medicine Name");
        tableModel.addColumn("Company Name");
        tableModel.addColumn("Category");
        tableModel.addColumn("Quantity");
        tableModel.addColumn("Price Per Unit");
        tableModel.addColumn("Expiry Date");

        medicineTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(medicineTable);

        // Text fields for adding/updating medicine details
        medicineNameField = new JTextField(20);
        companyNameField = new JTextField(20);
        categoryField = new JTextField(15);
        quantityField = new JTextField(10);
        priceField = new JTextField(10);
        expiryDateChooser = new JDateChooser();
        expiryDateChooser.setDateFormatString("dd/MM/yyyy");

        // Add ListSelectionListener to the table to detect row selection events
        medicineTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = medicineTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        // Get data from the selected row and populate the text fields
                        idField.setText(medicineTable.getValueAt(selectedRow, 0).toString());
                        medicineNameField.setText(medicineTable.getValueAt(selectedRow, 1).toString());
                        companyNameField.setText(medicineTable.getValueAt(selectedRow, 2).toString());
                        categoryField.setText(medicineTable.getValueAt(selectedRow, 3).toString());
                        quantityField.setText(medicineTable.getValueAt(selectedRow, 4).toString());
                        priceField.setText(medicineTable.getValueAt(selectedRow, 5).toString());
                        Object expiryDateValue = medicineTable.getValueAt(selectedRow, 6);
                        if (expiryDateValue instanceof Date) {
                            expiryDateChooser.setDate((Date) expiryDateValue);
                        }

                    }
                }
            }
        });

        // Buttons for Add, Update, and Delete operations
        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String medicineName = medicineNameField.getText();
                if (isDuplicateMedicine(medicineName)) {
                    JOptionPane.showMessageDialog(MedicineDetailsPage.this,
                            "Duplicate medicine name.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    addMedicine();
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
                // Update the selected medicine details in the database
                updateMedicine();
                // Refresh the table after updating the medicine
                refreshTable();
                clearFields();
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = medicineTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int id = Integer.parseInt(idField.getText());
                    // Delete the selected row from the database
                    deleteMedicine(id);
                    // Refresh the table after deleting the medicine
                    refreshTable();
                    // Clear the text fields
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(MedicineDetailsPage.this,
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
                // Show a dialog to input medicine name for searching
                String searchMedicineName = JOptionPane.showInputDialog(MedicineDetailsPage.this,
                        "Enter Medicine Name:");
                if (searchMedicineName != null && !searchMedicineName.isEmpty()) {
                    searchMedicine(searchMedicineName);
                }
            }
        });

        // Combo box for company names
        companyComboBox = new JComboBox<>(fetchCompanyNames());

        // Combo box for categories
        categoryComboBox = new JComboBox<>(new String[] { "Tablets", "Capsules", "Syrups", "Injections" });

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        inputPanel.add(new JLabel("Medicine Name:"));
        inputPanel.add(medicineNameField);
        inputPanel.add(new JLabel("Company Name:"));
        inputPanel.add(companyComboBox);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryComboBox);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Price Per Unit:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Expiry Date:"));
        // inputPanel.add(expiryDateField);
        inputPanel.add(expiryDateChooser);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(searchButton);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add components to the main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);

        // Initialize the table with existing medicine details
        refreshTable();
    }

    private String[] fetchCompanyNames() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT DISTINCT company_name FROM company WHERE username = ?")) {

            statement.setString(1, LoginPage.loggedInUsername);
            ResultSet resultSet = statement.executeQuery();

            // Extract company names from the result set
            java.util.List<String> companyNames = new java.util.ArrayList<>();
            while (resultSet.next()) {
                companyNames.add(resultSet.getString("company_name"));
            }

            // Convert the list to an array
            String[] companyNameArray = new String[companyNames.size()];
            companyNameArray = companyNames.toArray(companyNameArray);

            return companyNameArray;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch company names.", "Error", JOptionPane.ERROR_MESSAGE);
            return new String[0]; // Return an empty array in case of an error
        }
    }

    private void searchMedicine(String medicineName) {
        DefaultTableModel tableModel = (DefaultTableModel) medicineTable.getModel();
        tableModel.setRowCount(0); // Clear existing rows

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM medicine WHERE medicine_name LIKE ? AND username = ?")) {
            statement.setString(1, "%" + medicineName + "%");
            statement.setString(2, LoginPage.loggedInUsername); // Add the username condition
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String medicineNameResult = resultSet.getString("medicine_name");
                String companyName = resultSet.getString("company_name");
                String category = resultSet.getString("category");
                int quantity = resultSet.getInt("quantity");
                double pricePerUnit = resultSet.getDouble("price_per_unit");

                tableModel
                        .addRow(new Object[] { id, medicineNameResult, companyName, category, quantity, pricePerUnit });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to search for medicine.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        medicineNameField.setText("");
        quantityField.setText("");
        priceField.setText("");
        expiryDateChooser.setDate(null);
    }

    private boolean isDuplicateMedicine(String medicineName) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM medicine WHERE medicine_name=? AND username=?")) {
            statement.setString(1, medicineName);
            statement.setString(2, LoginPage.loggedInUsername); // Add the username condition
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void addMedicine() {
        String medicineName = medicineNameField.getText();
        String companyName = companyComboBox.getSelectedItem().toString();
        String category = categoryComboBox.getSelectedItem().toString();

        if (medicineName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a medicine name.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method to avoid further errors
        }

        // Check for empty quantity field
        String quantityStr = quantityField.getText();
        if (quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method to avoid further errors
        }

        int quantity = Integer.parseInt(quantityStr);

        // Check for empty price field
        String priceStr = priceField.getText();
        if (priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a price.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method to avoid further errors
        }

        double pricePerUnit = Double.parseDouble(priceStr);
        java.util.Date utilExpiryDate = expiryDateChooser.getDate();
        java.sql.Date expiryDate = null;

        if (utilExpiryDate != null) {
            expiryDate = new java.sql.Date(utilExpiryDate.getTime());
        } else {
            JOptionPane.showMessageDialog(MedicineDetailsPage.this, "Please select a valid expiry date.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int newId = generateUniqueID(); // Call a method to generate a unique ID

        // Check for duplicate medicine
        if (isDuplicateMedicine(medicineName)) {
            JOptionPane.showMessageDialog(this, "Duplicate medicine name found. Cannot add duplicate medicine.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            // Insert the new medicine with the generated ID into the database
            try (PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO medicine (id, medicine_name, company_name, category, quantity, price_per_unit, expiry_date, username) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                insertStatement.setInt(1, newId);
                insertStatement.setString(2, medicineName);
                insertStatement.setString(3, companyName);
                insertStatement.setString(4, category);
                insertStatement.setInt(5, quantity);
                insertStatement.setDouble(6, pricePerUnit);
                insertStatement.setTimestamp(7, new java.sql.Timestamp(expiryDate.getTime()));
                insertStatement.setString(8, LoginPage.loggedInUsername);

                insertStatement.executeUpdate();
                JOptionPane.showMessageDialog(this, "Medicine added successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to add medicine.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        // Refresh the table after adding a new medicine
        refreshTable();

    }

    private int generateUniqueID() {
        int uniqueID = 1; // Default unique ID if no records are present

        try (Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT MAX(id) FROM medicine")) {

            if (resultSet.next()) {
                uniqueID = resultSet.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to generate unique ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return uniqueID;
    }

    private void updateMedicine() {
        // Get values from text fields (similar to addMedicine)
        int id = Integer.parseInt(idField.getText());
        String medicineName = medicineNameField.getText();
        String companyName = companyComboBox.getSelectedItem().toString();
        String category = categoryComboBox.getSelectedItem().toString();

        if (medicineName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a medicine name.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method to avoid further errors
        }

        // Check for empty quantity field
        String quantityStr = quantityField.getText();
        if (quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method to avoid further errors
        }

        int quantity = Integer.parseInt(quantityStr);

        // Check for empty price field
        String priceStr = priceField.getText();
        if (priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a price.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method to avoid further errors
        }

        double pricePerUnit = Double.parseDouble(priceStr);

        java.util.Date utilExpiryDate = expiryDateChooser.getDate();

        // Validate date of birth
        if (utilExpiryDate == null) {
            JOptionPane.showMessageDialog(MedicineDetailsPage.this, "Please select a valid date of birth.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.sql.Date expiryDate = new java.sql.Date(utilExpiryDate.getTime());

        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE medicine SET medicine_name=?, company_name=?, category=?, quantity=?, price_per_unit=?, expiry_date=? WHERE id=? AND username=?")) {
            statement.setString(1, medicineName);
            statement.setString(2, companyName);
            statement.setString(3, category);
            statement.setInt(4, quantity);
            statement.setDouble(5, pricePerUnit);
            statement.setTimestamp(6, new java.sql.Timestamp(expiryDate.getTime()));
            statement.setInt(7, id);
            statement.setString(8, LoginPage.loggedInUsername);

            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Medicine updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to update medicine.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMedicine(int id) {
        try (PreparedStatement statement = connection
                .prepareStatement("DELETE FROM medicine WHERE id=? AND username=?")) {
            statement.setInt(1, id);
            statement.setString(2, LoginPage.loggedInUsername); // Set the username

            statement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Medicine deleted successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to delete medicine.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        DefaultTableModel tableModel = (DefaultTableModel) medicineTable.getModel();
        tableModel.setRowCount(0); // Clear existing rows

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM medicine WHERE username = ?")) {
            statement.setString(1, LoginPage.loggedInUsername);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String medicineName = resultSet.getString("medicine_name");
                String companyName = resultSet.getString("company_name");
                String category = resultSet.getString("category");
                int quantity = resultSet.getInt("quantity");
                double pricePerUnit = resultSet.getDouble("price_per_unit");
                Date expiryDate = resultSet.getDate("expiry_date");

                tableModel.addRow(
                        new Object[] { id, medicineName, companyName, category, quantity, pricePerUnit, expiryDate });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to refresh medicine data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MedicineDetailsPage medicineDetailsPage = new MedicineDetailsPage();
                medicineDetailsPage.setVisible(true);
            }
        });
    }
}
