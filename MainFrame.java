import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

class Stock {
    private String name;
    private double price;

    public Stock(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return name + ": $" + price;
    }
}

class Portfolio {
    private Map<String, Integer> stocks;

    public Portfolio() {
        this.stocks = new HashMap<>();
    }

    public void buyStock(String stockName, int quantity) {
        stocks.put(stockName, stocks.getOrDefault(stockName, 0) + quantity);
    }

    public void sellStock(String stockName, int quantity) {
        if (stocks.containsKey(stockName)) {
            int currentQuantity = stocks.get(stockName);
            if (currentQuantity >= quantity) {
                stocks.put(stockName, currentQuantity - quantity);
                if (stocks.get(stockName) == 0) {
                    stocks.remove(stockName);
                }
            }
        }
    }

    public double getPortfolioValue(Map<String, Stock> marketData) {
        double totalValue = 0;
        for (String stockName : stocks.keySet()) {
            Stock stock = marketData.get(stockName);
            if (stock != null) {
                totalValue += stock.getPrice() * stocks.get(stockName);
            }
        }
        return totalValue;
    }

    public Map<String, Integer> getStocks() {
        return stocks;
    }
}


class TradingPlatform {
    private Map<String, Stock> marketData;
    private Portfolio portfolio;

    public TradingPlatform() {
        this.marketData = new HashMap<>();
        this.portfolio = new Portfolio();
        initializeMarketData();
    }

    private void initializeMarketData() {
        marketData.put("AAPL", new Stock("AAPL", 150.0));
        marketData.put("GOOGL", new Stock("GOOGL", 2800.0));
        marketData.put("AMZN", new Stock("AMZN", 3400.0));
        marketData.put("TSLA", new Stock("TSLA", 700.0));
    }

    public void updateStockPrice(String stockName, double newPrice) {
        Stock stock = marketData.get(stockName);
        if (stock != null) {
            stock.setPrice(newPrice);
        }
    }

    public void buyStock(String stockName, int quantity) {
        if (marketData.containsKey(stockName)) {
            portfolio.buyStock(stockName, quantity);
        }
    }

    public void sellStock(String stockName, int quantity) {
        if (marketData.containsKey(stockName)) {
            portfolio.sellStock(stockName, quantity);
        }
    }

    public double getPortfolioValue() {
        return portfolio.getPortfolioValue(marketData);
    }

    public Map<String, Stock> getMarketData() {
        return marketData;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }
}

public class MainFrame extends JFrame {
    private TradingPlatform platform;
    private JTextArea displayArea;
    private JTextField stockField;
    private JTextField quantityField;

    public MainFrame() {
        platform = new TradingPlatform();
        setTitle("Simulated Stock Trading Platform");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea), BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        JLabel stockLabel = new JLabel("Stock:");
        controlPanel.add(stockLabel);

        stockField = new JTextField(10);
        controlPanel.add(stockField);

        JLabel quantityLabel = new JLabel("Quantity:");
        controlPanel.add(quantityLabel);

        quantityField = new JTextField(10);
        controlPanel.add(quantityField);

        JButton buyButton = new JButton("Buy");
        buyButton.addActionListener(new BuyButtonListener());
        controlPanel.add(buyButton);

        JButton sellButton = new JButton("Sell");
        sellButton.addActionListener(new SellButtonListener());
        controlPanel.add(sellButton);

        JButton updateButton = new JButton("Update Portfolio");
        updateButton.addActionListener(new UpdateButtonListener());
        controlPanel.add(updateButton);

        JButton refreshMarketButton = new JButton("Refresh Market Data");
        refreshMarketButton.addActionListener(new RefreshMarketButtonListener());
        controlPanel.add(refreshMarketButton);

        add(controlPanel, BorderLayout.SOUTH);

        updateDisplay();
    }

    private void updateDisplay() {
        displayArea.setText("Market Data:\n");
        for (Stock stock : platform.getMarketData().values()) {
            displayArea.append(stock.toString() + "\n");
        }
        displayArea.append("\nPortfolio Value: $" + platform.getPortfolioValue() + "\n");

        displayArea.append("\nOwned Stocks:\n");
        for (Map.Entry<String, Integer> entry : platform.getPortfolio().getStocks().entrySet()) {
            displayArea.append(entry.getKey() + ": " + entry.getValue() + " shares\n");
        }
    }

    private class BuyButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                String stockName = stockField.getText().trim().toUpperCase();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                platform.buyStock(stockName, quantity);
                updateDisplay();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(MainFrame.this, "Please enter a valid quantity.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class SellButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                String stockName = stockField.getText().trim().toUpperCase();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                platform.sellStock(stockName, quantity);
                updateDisplay();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(MainFrame.this, "Please enter a valid quantity.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class UpdateButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            updateDisplay();
        }
    }

    private class RefreshMarketButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            platform.updateStockPrice("AAPL", 155.0);
            platform.updateStockPrice("GOOGL", 2850.0);
            platform.updateStockPrice("AMZN", 3450.0);
            platform.updateStockPrice("TSLA", 720.0);
            updateDisplay();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
}
