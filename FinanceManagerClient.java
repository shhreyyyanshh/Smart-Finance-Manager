import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Standalone Java Swing desktop client for the Finance Manager backend.
 */
public class FinanceManagerClient extends JFrame {
    private static final String DEFAULT_BASE_URL = "http://localhost:8080";

    private final JTextField amountField;
    private final JTextField categoryField;
    private final JComboBox<String> typeComboBox;
    private final DefaultTableModel tableModel;
    private final HttpClient httpClient;
    private final String baseUrl;
    private final String addEndpoint;
    private final String allEndpoint;

    public FinanceManagerClient() {
        super("Finance Manager");
        this.httpClient = HttpClient.newHttpClient();
        this.baseUrl = resolveBaseUrl();
        this.addEndpoint = baseUrl + "/api/add";
        this.allEndpoint = baseUrl + "/api/all";

        amountField = new JTextField(15);
        categoryField = new JTextField(15);
        typeComboBox = new JComboBox<>(new String[]{"income", "expense"});

        tableModel = new DefaultTableModel(new Object[]{"ID", "Amount", "Category", "Type"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable transactionTable = new JTable(tableModel);
        transactionTable.setRowHeight(24);

        JButton addButton = new JButton("Add Transaction");
        JButton viewButton = new JButton("View Transactions");

        addButton.addActionListener(event -> addTransaction());
        viewButton.addActionListener(event -> loadTransactions());

        JPanel formPanel = createFormPanel(addButton, viewButton);

        setLayout(new BorderLayout(10, 10));
        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(transactionTable), BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);
        setTitle("Finance Manager - " + baseUrl);
    }

    private JPanel createFormPanel(JButton addButton, JButton viewButton) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Transaction Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1;
        panel.add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Category:"), gbc);

        gbc.gridx = 1;
        panel.add(categoryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Type:"), gbc);

        gbc.gridx = 1;
        panel.add(typeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(addButton, gbc);

        gbc.gridx = 1;
        panel.add(viewButton, gbc);

        return panel;
    }

    private void addTransaction() {
        String amountText = amountField.getText().trim();
        String category = categoryField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();

        if (amountText.isEmpty() || category.isEmpty()) {
            showError("Amount and category are required.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount < 0) {
                showError("Amount must be non-negative.");
                return;
            }
        } catch (NumberFormatException exception) {
            showError("Please enter a valid amount.");
            return;
        }

        String jsonBody = String.format(
                "{\"amount\": %.2f, \"category\": \"%s\", \"type\": \"%s\"}",
                amount,
                escapeJson(category),
                type
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(addEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JOptionPane.showMessageDialog(this, "Transaction added successfully.");
                clearForm();
                loadTransactions();
            } else {
                showError("Failed to add transaction. Server returned: " + response.statusCode());
            }
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            showError("Unable to connect to backend: " + exception.getMessage());
        }
    }

    private void loadTransactions() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(allEndpoint))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                List<TransactionRow> transactions = parseTransactions(response.body());
                populateTable(transactions);
            } else {
                showError("Failed to fetch transactions. Server returned: " + response.statusCode());
            }
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            showError("Unable to fetch transactions: " + exception.getMessage());
        }
    }

    private void populateTable(List<TransactionRow> transactions) {
        tableModel.setRowCount(0);

        for (TransactionRow transaction : transactions) {
            tableModel.addRow(new Object[]{
                    transaction.id,
                    transaction.amount,
                    transaction.category,
                    transaction.type
            });
        }
    }

    /**
     * A lightweight parser for the expected JSON response from /api/all.
     * Expected format:
     * [{"id":1,"amount":1000.0,"category":"Salary","type":"income"}]
     */
    private List<TransactionRow> parseTransactions(String json) {
        List<TransactionRow> transactions = new ArrayList<>();

        Pattern objectPattern = Pattern.compile("\\{(.*?)\\}");
        Matcher objectMatcher = objectPattern.matcher(json);

        while (objectMatcher.find()) {
            String objectText = objectMatcher.group(1);

            long id = extractLong(objectText, "\"id\"\\s*:\\s*(\\d+)");
            double amount = extractDouble(objectText, "\"amount\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)");
            String category = extractString(objectText, "\"category\"\\s*:\\s*\"(.*?)\"");
            String type = extractString(objectText, "\"type\"\\s*:\\s*\"(.*?)\"");

            transactions.add(new TransactionRow(id, amount, category, type));
        }

        return transactions;
    }

    private long extractLong(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        return matcher.find() ? Long.parseLong(matcher.group(1)) : 0L;
    }

    private double extractDouble(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        return matcher.find() ? Double.parseDouble(matcher.group(1)) : 0.0;
    }

    private String extractString(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        return matcher.find() ? matcher.group(1).replace("\\\"", "\"") : "";
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Allows switching between local and deployed backends without editing code.
     * Priority:
     * 1. Java system property: -Dfinance.api.baseUrl=...
     * 2. Environment variable: FINANCE_API_BASE_URL
     * 3. Local default: http://localhost:8080
     */
    private String resolveBaseUrl() {
        String systemPropertyUrl = System.getProperty("finance.api.baseUrl");
        if (systemPropertyUrl != null && !systemPropertyUrl.isBlank()) {
            return removeTrailingSlash(systemPropertyUrl.trim());
        }

        String environmentUrl = System.getenv("FINANCE_API_BASE_URL");
        if (environmentUrl != null && !environmentUrl.isBlank()) {
            return removeTrailingSlash(environmentUrl.trim());
        }

        return DEFAULT_BASE_URL;
    }

    private String removeTrailingSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    private void clearForm() {
        amountField.setText("");
        categoryField.setText("");
        typeComboBox.setSelectedIndex(0);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FinanceManagerClient frame = new FinanceManagerClient();
            frame.setVisible(true);
        });
    }

    /**
     * Small helper model used only by the Swing table.
     */
    private static class TransactionRow {
        private final long id;
        private final double amount;
        private final String category;
        private final String type;

        public TransactionRow(long id, double amount, String category, String type) {
            this.id = id;
            this.amount = amount;
            this.category = category;
            this.type = type;
        }
    }
}
