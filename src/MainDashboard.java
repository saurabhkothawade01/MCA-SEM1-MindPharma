import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainDashboard extends JFrame {

    private JLabel imageLabel;
    private ArrayList<ImageIcon> imageList;
    private int currentImageIndex;

    public MainDashboard() {

        setTitle("MindPharma");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Initialize image list
        imageList = new ArrayList<>();
        imageList.add(new ImageIcon("images/b.png"));
        imageList.add(new ImageIcon("images/c.png"));
        imageList.add(new ImageIcon("images/e.png"));

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Registration form panel
        JPanel panel = new JPanel(new GridBagLayout());
        Border titledBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 5),
                "MindPharma - Your Path to Mental Wellness  ");
        ((TitledBorder) titledBorder).setTitleFont(new Font("Arial", Font.BOLD, 18));
        panel.setBorder(
                BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;

        // Slideshow label
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        updateImage(); // Initial update

        Timer timer = new Timer(3000, new ActionListener() { // Change image every 3 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                updateImage();
            }
        });
        timer.start();

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        panel.add(imageLabel, constraints);

        JLabel titleLabel = new JLabel("MindPharma Maindashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        panel.add(titleLabel, constraints);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 6, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttonsPanel.setBackground(Color.WHITE);

        // Create buttons
        JButton medicineButton = createStyledButton("Manage Medicine Details");
        JButton companyButton = createStyledButton("Manage Company Details");
        JButton salesButton = createStyledButton("Record Sales");
        JButton reportsButton = createStyledButton("Generate Reports");
        JButton userButton = createStyledButton("Manage User");
        JButton logoutButton = createStyledButton("Logout");

        // Add buttons to the panel
        buttonsPanel.add(medicineButton);
        buttonsPanel.add(companyButton);
        buttonsPanel.add(salesButton);
        buttonsPanel.add(reportsButton);
        buttonsPanel.add(userButton);
        buttonsPanel.add(logoutButton);

        reportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ReportGenerationWindow reportGenerationWindow = new ReportGenerationWindow();
                reportGenerationWindow.setVisible(true);
                MainDashboard.this.dispose();
            }
        });

        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = LoginPage.getLoggedInUsername();
                ManageUser manageUser = new ManageUser(username);
                manageUser.setVisible(true);

                MainDashboard.this.dispose();
            }
        });

        medicineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                MedicineDetailsPage medicineDetailsPage = new MedicineDetailsPage();
                medicineDetailsPage.setVisible(true);
                MainDashboard.this.dispose();
            }
        });

        companyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CompanyDetailsPage companyDetailsPage = new CompanyDetailsPage();
                companyDetailsPage.setVisible(true);
                MainDashboard.this.dispose();
            }
        });

        salesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SalesPage salesPage = new SalesPage();
                salesPage.setVisible(true);
                MainDashboard.this.dispose();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(MainDashboard.this, "Are you sure you want to logout?",
                        "Logout", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    LoginPage loginPage = new LoginPage();
                    loginPage.setVisible(true);
                    MainDashboard.this.dispose();
                }
            }
        });

        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        add(mainPanel);

        setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        setLocationRelativeTo(null);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setBackground(Color.LIGHT_GRAY);
        button.setBorder(new LineBorder(Color.BLACK, 2));
        button.setFocusPainted(false);

        return button;
    }

    private void updateImage() {
        if (!imageList.isEmpty()) {
            imageLabel.setIcon(imageList.get(currentImageIndex));
            currentImageIndex = (currentImageIndex + 1) % imageList.size();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainDashboard mainDashboard = new MainDashboard();
                mainDashboard.setVisible(true);
            }
        });
    }
}
