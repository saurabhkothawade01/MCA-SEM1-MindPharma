import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SalesPage extends JFrame {
    private JTextField customerNameField, addressField, contactField, quantityField;
    private JComboBox<String> categoryComboBox, medicineComboBox;
    private JTable saleTable;
    private JButton addToCartButton, sellButton, showButton, backButton;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mindpharma";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "saurabh";

    private Connection connection;

    public SalesPage() {
        setTitle("MindPharma - Sales");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 5),
                "MindPharma - Your Path to Mental Wellness ", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18)));

        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        }

        // Components for entering sale details
        customerNameField = new JTextField();
        addressField = new JTextField();
        contactField = new JTextField();
        quantityField = new JTextField();

        categoryComboBox = new JComboBox<>();
        medicineComboBox = new JComboBox<>();

        // Table to display items in the cart
        saleTable = new JTable();
        initializeSaleTable(); // Initialize the sale table with column names

        // Buttons for Add to Cart, Sell, and Show Sales
        addToCartButton = new JButton("Add to Cart");
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToCart();
            }
        });

        sellButton = new JButton("Sell");
        sellButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sellItems();
            }
        });

        showButton = new JButton("Show Sales");
        showButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSales();
            }
        });

        backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current window
                MainDashboard mainDashboard = new MainDashboard();
                mainDashboard.setVisible(true); // Show the Main Dashboard
            }
        });

        // Add an ActionListener to the categoryComboBox to trigger fetching medicine
        // names when the category changes
        categoryComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchMedicineNames();
            }
        });

        // Layout for entering sale details
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.add(new JLabel("Customer Name:"));
        inputPanel.add(customerNameField);
        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(addressField);
        inputPanel.add(new JLabel("Contact No.:"));
        inputPanel.add(contactField);
        inputPanel.add(new JLabel("Category:"));
        inputPanel.add(categoryComboBox);
        inputPanel.add(new JLabel("Medicine:"));
        inputPanel.add(medicineComboBox);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);

        // Layout for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addToCartButton);
        buttonPanel.add(sellButton);
        buttonPanel.add(showButton);
        buttonPanel.add(backButton);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(new JScrollPane(saleTable), BorderLayout.CENTER);

        getContentPane().add(mainPanel);

        // Initialize the dropdowns with data
        fetchAndSetComboBoxData();
        // Initialize the sale table with column names

        initializeSaleTable();
    }

    private void fetchAndSetComboBoxData() {
        fetchCategories();
        fetchMedicineNames();
    }

    private void fetchCategories() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection
                        .prepareStatement("SELECT DISTINCT category FROM medicine WHERE username = ?")) {

            statement.setString(1, LoginPage.loggedInUsername);

            ResultSet resultSet = statement.executeQuery();

            List<String> categories = new ArrayList<>();
            while (resultSet.next()) {
                String category = resultSet.getString("category");
                categories.add(category);
            }

            // Set categories to the categoryComboBox
            categoryComboBox.setModel(new DefaultComboBoxModel<>(categories.toArray(new String[0])));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch categories.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchMedicineNames() {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();

        if (selectedCategory != null) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                    PreparedStatement statement = connection.prepareStatement(
                            "SELECT medicine_name FROM medicine WHERE category = ? AND username = ?")) {

                statement.setString(1, selectedCategory);
                statement.setString(2, LoginPage.loggedInUsername);
                ResultSet resultSet = statement.executeQuery();

                List<String> medicineNames = new ArrayList<>();
                while (resultSet.next()) {
                    String medicineName = resultSet.getString("medicine_name");
                    medicineNames.add(medicineName);
                }

                // Set medicine names to the medicineComboBox
                medicineComboBox.setModel(new DefaultComboBoxModel<>(medicineNames.toArray(new String[0])));

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to fetch medicine names.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void initializeSaleTable() {
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Medicine");
        tableModel.addColumn("Category");
        tableModel.addColumn("Quantity");
        saleTable.setModel(tableModel);
    }

    private void addToCart() {
        String category = categoryComboBox.getSelectedItem().toString();
        String medicine = medicineComboBox.getSelectedItem().toString();
        String quantityStr = quantityField.getText();
        String custname = customerNameField.getText();
        String contact = contactField.getText();

        if (quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (custname.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Customer Name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Contact.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity = Integer.parseInt(quantityStr);

        // Check if there is enough quantity of the selected medicine
    if (!hasEnoughQuantity(medicine, category, quantity)) {
        JOptionPane.showMessageDialog(this, "Not enough quantity of " + medicine + " available.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

        DefaultTableModel tableModel = (DefaultTableModel) saleTable.getModel();
        tableModel.addRow(new Object[] { medicine, category, quantity });

        // Clear the fields for selecting another item
        quantityField.setText("");
    }

    private void sellItems() {
        // Get customer details
        String customerName = customerNameField.getText();
        String address = addressField.getText();
        String contact = contactField.getText();

        // Get sale details from the table
        DefaultTableModel tableModel = (DefaultTableModel) saleTable.getModel();
        int rowCount = tableModel.getRowCount();

        if (rowCount == 0) {
            JOptionPane.showMessageDialog(this, "No items in the cart.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prepare bill content
        StringBuilder billContent = new StringBuilder();
        double[] totalAmount = { 0.0 };

        for (int i = 0; i < rowCount; i++) {
            String medicine = tableModel.getValueAt(i, 0).toString();
            String category = tableModel.getValueAt(i, 1).toString();
            int quantity = Integer.parseInt(tableModel.getValueAt(i, 2).toString());

            // Fetch medicine price from the database
            double medicinePrice = fetchMedicinePrice(medicine, category);

            if (medicinePrice < 0) {
                // Handle the case where the medicine price is not found
                JOptionPane.showMessageDialog(this, "Failed to fetch medicine price for " + medicine, "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Calculate line total
            double lineTotal = medicinePrice * quantity;

            // Update total amount
            totalAmount[0] += lineTotal;

            // Append line to the bill content
            billContent.append(String.format("%s - %s: %d x %.2f = %.2f%n", category, medicine, quantity, medicinePrice,
                    lineTotal));
        }

        // Display bill window
        JFrame billFrame = new JFrame("Bill");
        billFrame.setSize(400, 300);
        billFrame.setLocationRelativeTo(null);
        billFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel billPanel = new JPanel(new BorderLayout());

        JTextArea billTextArea = new JTextArea();
        billTextArea.setEditable(false);
        billTextArea.setText(String.format("Customer: %s%nAddress: %s%nContact: %s%n%n%s%n%nTotal Amount: %.2f",
                customerName, address, contact, billContent.toString(), totalAmount[0]));

        JButton confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the database with sale details
                updateDatabaseWithSale(customerName, address, contact, tableModel, totalAmount[0]);

                // Clear sale table and fields
                tableModel.setRowCount(0);
                clearSaleFields();

                // Close the bill window
                billFrame.dispose();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the bill window without updating the database
                billFrame.dispose();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        billPanel.add(new JScrollPane(billTextArea), BorderLayout.CENTER);
        billPanel.add(buttonPanel, BorderLayout.SOUTH);

        billFrame.getContentPane().add(billPanel);
        billFrame.setVisible(true);
    }

    private double fetchMedicinePrice(String medicine, String category) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT price_per_unit FROM medicine WHERE medicine_name = ? AND category = ? AND username = ?")) {

            statement.setString(1, medicine);
            statement.setString(2, category);
            statement.setString(3, LoginPage.loggedInUsername);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("price_per_unit");
            } else {
                // Return a negative value to indicate that the medicine price is not found
                return -1.0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Return a negative value to indicate an error fetching the medicine price
            return -1.0;
        }
    }

    private void clearSaleFields() {
        // Clear customer details and sale fields
        customerNameField.setText("");
        addressField.setText("");
        contactField.setText("");
        quantityField.setText("");
        categoryComboBox.setSelectedIndex(0);
        medicineComboBox.setSelectedIndex(0);
    }

    private boolean hasEnoughQuantity(String medicine, String category, int quantity) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT quantity FROM medicine WHERE medicine_name = ? AND category = ? AND username = ?")) {
    
            statement.setString(1, medicine);
            statement.setString(2, category);
            statement.setString(3, LoginPage.loggedInUsername);
    
            ResultSet resultSet = statement.executeQuery();
    
            if (resultSet.next()) {
                int availableQuantity = resultSet.getInt("quantity");
                return availableQuantity >= quantity;
            } else {
                return false; // Medicine not found
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error fetching quantity
        }
    }
    

    private void updateDatabaseWithSale(String customerName, String address, String contact,
            DefaultTableModel tableModel, double totalAmount) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            connection.setAutoCommit(false); // Start a transaction

            // Insert into the sales table
            String insertSaleSQL = "INSERT INTO sales (customer_name, address, contact_no, total_amount, username) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement saleStatement = connection.prepareStatement(insertSaleSQL,
                    Statement.RETURN_GENERATED_KEYS)) {
                saleStatement.setString(1, customerName);
                saleStatement.setString(2, address);
                saleStatement.setString(3, contact);
                saleStatement.setDouble(4, totalAmount);
                saleStatement.setString(5, LoginPage.loggedInUsername);

                int affectedRows = saleStatement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating sale failed, no rows affected.");
                }

                // Get the generated sale_id
                try (ResultSet generatedKeys = saleStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int saleId = generatedKeys.getInt(1);

                        // Insert into the sale_details table
                        String insertDetailsSQL = "INSERT INTO sale_details (sale_id, medicine_name, category, quantity, medicine_price, line_total, username) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement detailsStatement = connection.prepareStatement(insertDetailsSQL)) {
                            for (int i = 0; i < tableModel.getRowCount(); i++) {
                                String medicine = tableModel.getValueAt(i, 0).toString();
                                String category = tableModel.getValueAt(i, 1).toString();
                                int quantity = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                                double medicinePrice = fetchMedicinePrice(medicine, category);
                                double lineTotal = quantity * medicinePrice;

                                detailsStatement.setInt(1, saleId);
                                detailsStatement.setString(2, medicine);
                                detailsStatement.setString(3, category);
                                detailsStatement.setInt(4, quantity);
                                detailsStatement.setDouble(5, medicinePrice);
                                detailsStatement.setDouble(6, lineTotal);
                                detailsStatement.setString(7, LoginPage.loggedInUsername);

                                detailsStatement.addBatch();

                                // Update the quantity of sold medicine in the medicine table
                                updateMedicineQuantity(connection, medicine, category, quantity);
                            }

                            detailsStatement.executeBatch();
                        }

                        connection.commit(); // Commit the transaction

                        JOptionPane.showMessageDialog(this, "Sale recorded successfully.", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        throw new SQLException("Creating sale failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            try {
                connection.rollback(); // Rollback the transaction in case of an error
            } catch (SQLException e) {
                e.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Failed to record sale.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMedicineQuantity(Connection connection, String medicine, String category, int quantity)
            throws SQLException {
        // Update the quantity of sold medicine in the medicine table
        String updateQuantitySQL = "UPDATE medicine SET quantity = quantity - ? WHERE medicine_name = ? AND category = ? AND username = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateQuantitySQL)) {
            updateStatement.setInt(1, quantity);
            updateStatement.setString(2, medicine);
            updateStatement.setString(3, category);
            updateStatement.setString(4, LoginPage.loggedInUsername);

            int updatedRows = updateStatement.executeUpdate();
            if (updatedRows == 0) {
                throw new SQLException("Updating medicine quantity failed, no rows affected.");
            }
        }
    }

    private void showSales() {
        DefaultTableModel saleTableModel = new DefaultTableModel();
        saleTableModel.addColumn("Sale ID");
        saleTableModel.addColumn("Customer Name");
        saleTableModel.addColumn("Address");
        saleTableModel.addColumn("Contact No.");
        saleTableModel.addColumn("Total Amount");
        saleTableModel.addColumn("Sale Date");

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM sales WHERE username = ?")) {

            statement.setString(1, LoginPage.loggedInUsername); // Set the parameter value here
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int saleId = resultSet.getInt("sale_id");
                String customerName = resultSet.getString("customer_name");
                String address = resultSet.getString("address");
                String contact = resultSet.getString("contact_no");
                double totalAmount = resultSet.getDouble("total_amount");
                Timestamp saleDate = resultSet.getTimestamp("sale_date");

                saleTableModel.addRow(new Object[] { saleId, customerName, address, contact, totalAmount, saleDate });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch sales data.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Create a new frame to display sales information
        JFrame salesFrame = new JFrame("MindPharma");
        salesFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // salesFrame.setLocationRelativeTo(null);
        salesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2),
                "MindPharma - Your Path to Mental Wellness", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18))); // Add a titled border

        // Create JTable for general sales information
        JTable salesTable = new JTable(saleTableModel);
        JScrollPane salesScrollPane = new JScrollPane(salesTable);

        // Create JTable for sale details
        DefaultTableModel detailsTableModel = new DefaultTableModel();
        detailsTableModel.addColumn("Medicine Name");
        detailsTableModel.addColumn("Category");
        detailsTableModel.addColumn("Quantity");
        detailsTableModel.addColumn("Medicine Price");
        detailsTableModel.addColumn("Line Total");

        JTable detailsTable = new JTable(detailsTableModel);
        JScrollPane detailsScrollPane = new JScrollPane(detailsTable);

        // Add action listener to the sales table to update details table
        salesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = salesTable.getSelectedRow();
                    if (selectedRow != -1) {
                        // Fetch sale details for the selected sale_id
                        int selectedSaleId = (int) salesTable.getValueAt(selectedRow, 0);
                        updateSaleDetailsTable(selectedSaleId, detailsTableModel);
                    }
                }
            }
        });

        // Add the tables to the main panel
        mainPanel.add(salesScrollPane, BorderLayout.NORTH);
        mainPanel.add(detailsScrollPane, BorderLayout.CENTER);

        // Add the main panel to the frame
        salesFrame.add(mainPanel);

        salesFrame.setVisible(true);
    }

    private void updateSaleDetailsTable(int saleId, DefaultTableModel detailsTableModel) {
        // Clear existing data
        detailsTableModel.setRowCount(0);

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM sale_details WHERE sale_id = ? AND username = ?")) {

            statement.setInt(1, saleId);
            statement.setString(2, LoginPage.loggedInUsername);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String medicineName = resultSet.getString("medicine_name");
                String category = resultSet.getString("category");
                int quantity = resultSet.getInt("quantity");
                double medicinePrice = resultSet.getDouble("medicine_price");
                double lineTotal = resultSet.getDouble("line_total");

                detailsTableModel.addRow(new Object[] { medicineName, category, quantity, medicinePrice, lineTotal });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch sale details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SalesPage SalesPage = new SalesPage();
                SalesPage.setVisible(true);
            }
        });
    }
}
