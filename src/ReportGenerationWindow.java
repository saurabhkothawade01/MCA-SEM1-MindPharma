import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class ReportGenerationWindow extends JFrame {
    private JComboBox<String> reportTypeComboBox;

    public ReportGenerationWindow() {
        setTitle("MindPharma");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 5),
                "MindPharma - Your Path to Mental Wellness  ", TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 18)));

        // Create a drop-down list for report types
        reportTypeComboBox = new JComboBox<>(new String[] { "Select Report Type", "Medicine Inventory Report",
                "Best-selling Medicines", "Sales Trends Over Time",
                "Sales Report", "Company Inventory Report", "Expiry Medicine Inventory Report" });
        reportTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton generateButton = new JButton("Generate Report");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedReport = (String) reportTypeComboBox.getSelectedItem();
                if ("Select Report Type".equals(selectedReport)) {
                    JOptionPane.showMessageDialog(ReportGenerationWindow.this,
                            "Please select a valid report type.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    // Call a method to generate the selected report based on the chosen report type
                    generateReport(selectedReport);
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

        // Layout for report generation window
        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        inputPanel.add(new JLabel("Select Report Type:"));
        inputPanel.add(reportTypeComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(generateButton);
        buttonPanel.add(backButton);

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
    }

    private void generateReport(String reportType) {
        switch (reportType) {
            case "Medicine Inventory Report":
                generateMedicineInventoryReport(LoginPage.loggedInUsername);
                break;
            case "Sales Report":
                DateRangeInputDialog dateRangeDialog = new DateRangeInputDialog(this, "Enter Date Range");
                dateRangeDialog.setVisible(true);

                if (dateRangeDialog.isConfirmed()) {
                    Date startDate = dateRangeDialog.getStartDate();
                    Date endDate = dateRangeDialog.getEndDate();
                    generateSalesReport(LoginPage.loggedInUsername, "Custom", startDate, endDate); // Pass the
                                                                                                   // reportType as
                                                                                                   // "Custom"
                }
                break;
            case "Best-selling Medicines":
                generateBestSellingMedicinesReport(LoginPage.loggedInUsername);
                break;
            case "Sales Trends Over Time":
                generateSalesTrendsReport(LoginPage.loggedInUsername);
                break;

            case "Company Inventory Report":
                generateCompanyInventoryReport(LoginPage.loggedInUsername);
                break;

            case "Expiry Medicine Inventory Report":
                generateExpiryMedicineInventoryReport(LoginPage.loggedInUsername);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid report type.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add this method to your existing class (e.g., CompanyDetailsPage)
    private void generateExpiryMedicineInventoryReport(String loggedInUsername) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mindpharma", "root",
                "saurabh");
                PreparedStatement statement = connection
                        .prepareStatement("SELECT * FROM medicine WHERE expiry_date <= ? AND username = ?")) {

            // Get the current date
            LocalDate currentDate = LocalDate.now();

            // Set the current date and username as parameters for the query
            statement.setDate(1, java.sql.Date.valueOf(currentDate));
            statement.setString(2, loggedInUsername);

            try (ResultSet resultSet = statement.executeQuery()) {

                // Create a StringBuilder to store the report content
                StringBuilder reportContent = new StringBuilder();
                reportContent.append("Expiry Medicine Inventory Report\n\n");

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String medicineName = resultSet.getString("medicine_name");
                    String companyName = resultSet.getString("company_name");
                    String category = resultSet.getString("category");
                    int quantity = resultSet.getInt("quantity");
                    double pricePerUnit = resultSet.getDouble("price_per_unit");
                    Date expiryDate = resultSet.getDate("expiry_date");

                    // Append data to the reportContent
                    reportContent.append(String.format("Medicine ID: %d\n", id));
                    reportContent.append(String.format("Medicine Name: %s\n", medicineName));
                    reportContent.append(String.format("Company Name: %s\n", companyName));
                    reportContent.append(String.format("Category: %s\n", category));
                    reportContent.append(String.format("Quantity: %d\n", quantity));
                    reportContent.append(String.format("Price Per Unit: %.2f\n", pricePerUnit));
                    reportContent.append(String.format("Expiry Date: %s\n\n", expiryDate));
                }

                // Display or save the report content as needed
                JTextArea reportTextArea = new JTextArea(reportContent.toString());
                reportTextArea.setEditable(false);

                JScrollPane scrollPane = new JScrollPane(reportTextArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));

                JOptionPane.showMessageDialog(this, scrollPane, "Expiry Medicine Inventory Report",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to generate Expiry Medicine Inventory Report.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // DateRangeInputDialog class for obtaining date range input
    class DateRangeInputDialog extends JDialog {
        private final JTextField startDateField;
        private final JTextField endDateField;
        private boolean confirmed = false;

        public DateRangeInputDialog(JFrame parent, String title) {
            super(parent, title, true);

            setSize(300, 150);
            setLocationRelativeTo(parent);

            startDateField = new JTextField();
            endDateField = new JTextField();

            JButton confirmButton = new JButton("Generate Report");
            confirmButton.addActionListener(e -> {
                confirmed = true;
                dispose();
            });

            JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            inputPanel.add(new JLabel("Start Date (MM/dd/yyyy):"));
            inputPanel.add(startDateField);
            inputPanel.add(new JLabel("End Date (MM/dd/yyyy):"));
            inputPanel.add(endDateField);
            inputPanel.add(new JLabel());
            inputPanel.add(confirmButton);

            add(inputPanel);
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public Date getStartDate() {
            return parseDate(startDateField.getText());
        }

        public Date getEndDate() {
            return parseDate(endDateField.getText());
        }

        private Date parseDate(String dateString) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                return dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    // Method to generate Daily, Weekly, Monthly, or Custom Date Range Sales Report
    private void generateSalesReport(String loggedInUsername, String reportType, Date startDate, Date endDate) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mindpharma", "root",
                "saurabh")) {
            StringBuilder reportContent = new StringBuilder();

            String dateFilter;
            switch (reportType) {
                case "Daily":
                    dateFilter = "DATE(sale_date) = ?";
                    break;
                case "Weekly":
                    dateFilter = "DATE(sale_date) BETWEEN ? AND ?";
                    break;
                case "Monthly":
                    dateFilter = "MONTH(sale_date) = MONTH(?) AND YEAR(sale_date) = YEAR(?)";
                    break;
                case "Custom":
                    dateFilter = "DATE(sale_date) BETWEEN ? AND ?";
                    break;
                default:
                    dateFilter = "1"; // No date filter
            }

            String query = "SELECT sale_id, customer_name, total_amount, sale_date FROM sales WHERE " + dateFilter
                    + " AND username = '" + loggedInUsername + "'";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                if (!reportType.equals("All")) {
                    statement.setDate(1, new java.sql.Date(startDate.getTime()));
                    if (reportType.equals("Weekly") || reportType.equals("Custom")) {
                        statement.setDate(2, new java.sql.Date(endDate.getTime()));
                    }
                }

                ResultSet resultSet = statement.executeQuery();

                reportContent.append("Sales Report\n\n");
                reportContent.append(String.format("%-10s %-20s %-15s %-20s\n", "Sale ID", "Customer Name",
                        "Total Amount", "Sale Date"));

                while (resultSet.next()) {
                    int saleId = resultSet.getInt("sale_id");
                    String customerName = resultSet.getString("customer_name");
                    double totalAmount = resultSet.getDouble("total_amount");
                    Date saleDate = resultSet.getDate("sale_date");

                    reportContent.append(
                            String.format("%-10d %-20s %-15.2f %-20s\n", saleId, customerName, totalAmount, saleDate));
                }

                JTextArea reportTextArea = new JTextArea(reportContent.toString());
                reportTextArea.setEditable(false);

                JScrollPane scrollPane = new JScrollPane(reportTextArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));

                JOptionPane.showMessageDialog(this, scrollPane, "Sales Report", JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to generate Sales Report.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to generate Best-selling Medicines Report
    private void generateBestSellingMedicinesReport(String loggedInUsername) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mindpharma", "root",
                "saurabh")) {
            StringBuilder reportContent = new StringBuilder();

            String query = "SELECT medicine_name, SUM(quantity) AS total_sold FROM sale_details WHERE username = '"
                    + loggedInUsername + "' GROUP BY medicine_name ORDER BY total_sold DESC";

            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query)) {

                reportContent.append("Best-selling Medicines Report\n\n");
                reportContent.append(String.format("%-20s %-15s\n", "Medicine Name", "Total Quantity Sold"));

                while (resultSet.next()) {
                    String medicineName = resultSet.getString("medicine_name");
                    int totalSold = resultSet.getInt("total_sold");

                    reportContent.append(String.format("%-20s %-15d\n", medicineName, totalSold));
                }

                JTextArea reportTextArea = new JTextArea(reportContent.toString());
                reportTextArea.setEditable(false);

                JScrollPane scrollPane = new JScrollPane(reportTextArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));

                JOptionPane.showMessageDialog(this, scrollPane, "Best-selling Medicines Report",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to generate Best-selling Medicines Report.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to generate Sales Trends Over Time Report
    private void generateSalesTrendsReport(String loggedInUsername) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mindpharma", "root",
                "saurabh")) {
            StringBuilder reportContent = new StringBuilder();

            String query = "SELECT DATE(sale_date) AS sale_date, SUM(total_amount) AS total_sales FROM sales WHERE username = '"
                    + loggedInUsername + "' GROUP BY DATE(sale_date) ORDER BY sale_date";

            try (Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(query)) {

                reportContent.append("Sales Trends Over Time Report\n\n");
                reportContent.append(String.format("%-20s %-15s\n", "Sale Date", "Total Sales"));

                while (resultSet.next()) {
                    Date saleDate = resultSet.getDate("sale_date");
                    double totalSales = resultSet.getDouble("total_sales");

                    reportContent.append(String.format("%-20s %-15.2f\n", saleDate, totalSales));
                }

                JTextArea reportTextArea = new JTextArea(reportContent.toString());
                reportTextArea.setEditable(false);

                JScrollPane scrollPane = new JScrollPane(reportTextArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));

                JOptionPane.showMessageDialog(this, scrollPane, "Sales Trends Over Time Report",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to generate Sales Trends Over Time Report.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Add this method to your existing class (e.g., CompanyDetailsPage)
    private void generateCompanyInventoryReport(String loggedInUsername) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mindpharma", "root",
                "saurabh");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(
                        "SELECT c.id AS company_id, c.company_name, c.email, c.contact_no, c.address, m.medicine_name, m.category, m.quantity, m.price_per_unit, m.expiry_date "
                                +
                                "FROM company c " +
                                "LEFT JOIN medicine m ON c.company_name = m.company_name WHERE c.username = '"
                                + loggedInUsername + "'")) {

            // Create a StringBuilder to store the report content
            StringBuilder reportContent = new StringBuilder();
            reportContent.append("Company Inventory Report\n\n");

            while (resultSet.next()) {
                int companyId = resultSet.getInt("company_id");
                String companyName = resultSet.getString("company_name");
                String email = resultSet.getString("email");
                String contactNo = resultSet.getString("contact_no");
                String address = resultSet.getString("address");
                String medicineName = resultSet.getString("medicine_name");
                String category = resultSet.getString("category");
                int quantity = resultSet.getInt("quantity");
                double pricePerUnit = resultSet.getDouble("price_per_unit");
                Date expiryDate = resultSet.getDate("expiry_date");

                // Append data to the reportContent
                reportContent.append(String.format("Company ID: %d\n", companyId));
                reportContent.append(String.format("Company Name: %s\n", companyName));
                reportContent.append(String.format("Email: %s\n", email));
                reportContent.append(String.format("Contact No.: %s\n", contactNo));
                reportContent.append(String.format("Address: %s\n", address));
                reportContent.append(String.format("Medicine: %s\n", medicineName));
                reportContent.append(String.format("Category: %s\n", category));
                reportContent.append(String.format("Quantity: %d\n", quantity));
                reportContent.append(String.format("Price Per Unit: %.2f\n", pricePerUnit));
                reportContent.append(String.format("Expiry Date: %s\n\n", expiryDate));
            }

            // Display or save the report content as needed
            JTextArea reportTextArea = new JTextArea(reportContent.toString());
            reportTextArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(reportTextArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Company Inventory Report",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to generate Company Inventory Report.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateMedicineInventoryReport(String loggedInUsername) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mindpharma", "root",
                "saurabh");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement
                        .executeQuery("SELECT * FROM medicine WHERE username = '" + LoginPage.loggedInUsername + "'")) {

            // Create a StringBuilder to store the report content
            StringBuilder reportContent = new StringBuilder();
            reportContent.append("Medicine Inventory Report\n\n");
            reportContent.append(String.format("%-5s %-20s %-20s %-15s %-10s %-15s %-15s\n", "ID", "Medicine Name",
                    "Company Name", "Category", "Quantity", "Price Per Unit", "Expiry Date"));

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String medicineName = resultSet.getString("medicine_name");
                String companyName = resultSet.getString("company_name");
                String category = resultSet.getString("category");
                int quantity = resultSet.getInt("quantity");
                double pricePerUnit = resultSet.getDouble("price_per_unit");
                Date expiryDate = resultSet.getDate("expiry_date");

                // Append data to the reportContent
                reportContent.append(String.format("%-5d %-20s %-20s %-15s %-10d %-15.2f %-15s\n", id, medicineName,
                        companyName, category, quantity, pricePerUnit, expiryDate));

                // Check for low-stock alert (e.g., quantity less than a threshold)
                int lowStockThreshold = 10; // Adjust the threshold as needed
                if (quantity < lowStockThreshold) {
                    reportContent.append("Low Stock Alert: " + medicineName + " - Quantity running low!\n");
                }
            }

            // Display or save the report content as needed
            JTextArea reportTextArea = new JTextArea(reportContent.toString());
            reportTextArea.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(reportTextArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JOptionPane.showMessageDialog(this, scrollPane, "Medicine Inventory Report",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to generate Medicine Inventory Report.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ReportGenerationWindow reportGenerationWindow = new ReportGenerationWindow();
                reportGenerationWindow.setVisible(true);
            }
        });
    }
}
