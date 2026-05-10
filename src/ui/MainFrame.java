package ui;

import dao.*;
import model.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainFrame extends JFrame {
    private FloorPlanPanel floorPlan;
    private JPanel statsPanel;
    private JPanel ordersPanel;      // Верхний блок — список заказов
    private JPanel infoPanel;        // Нижний блок — информация
    private Timer refreshTimer;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private final OrderDAO orderDAO = new OrderDAO();
    private final WaiterDAO waiterDAO = new WaiterDAO();

    public MainFrame() {
        setTitle("🍽️ SELECT * FROM Menu - Система учёта заказов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Левая панель - карта зала
        floorPlan = new FloorPlanPanel();
        floorPlan.setOnTableSelected(this::refreshStats);
        add(floorPlan, BorderLayout.CENTER);

        // Правая панель - статистика
        statsPanel = new JPanel(new BorderLayout());
        statsPanel.setPreferredSize(new Dimension(350, 0));
        statsPanel.setBackground(new Color(248, 248, 248));

        // Верхний блок - заказы (scroll)
        ordersPanel = new JPanel();
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
        JScrollPane ordersScroll = new JScrollPane(ordersPanel);
        ordersScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "📋 Активные заказы",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), new Color(100, 70, 40)
        ));
        ordersScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        ordersScroll.setPreferredSize(new Dimension(350, 350));

        // Нижний блок - информация
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "ℹ️ Информация",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), new Color(100, 70, 40)
        ));
        infoPanel.setPreferredSize(new Dimension(350, 350));

        statsPanel.add(ordersScroll, BorderLayout.NORTH);
        statsPanel.add(infoPanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.EAST);

        // Верхняя панель - заголовок
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(255, 248, 225));

        JLabel titleLabel = new JLabel("SELECT * FROM Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(100, 70, 40));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Кнопка обновления справа
        JButton refreshBtn = new JButton("🔄 Обновить");
        refreshBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshBtn.addActionListener(e -> refreshStats());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(255, 248, 225));
        btnPanel.add(refreshBtn);
        topPanel.add(btnPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Нижняя панель - статус
        JLabel statusBar = new JLabel(" ✅ Готов к работе | PostgreSQL connected");
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusBar.setFont(new Font("Arial", Font.PLAIN, 11));
        statusBar.setForeground(new Color(100, 100, 100));
        add(statusBar, BorderLayout.SOUTH);

        // Автообновление каждые 30 секунд
        refreshTimer = new Timer(30000, e -> refreshStats());
        refreshTimer.start();

        refreshStats();
    }

    private void refreshStats() {
        refreshOrdersList();
        refreshInfoPanel();
    }

    private void refreshOrdersList() {
        ordersPanel.removeAll();

        List<Order> openOrders = orderDAO.getAllOpenOrders(); // Нужно добавить этот метод

        if (openOrders.isEmpty()) {
            JLabel emptyLabel = new JLabel("  Нет активных заказов");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            emptyLabel.setForeground(Color.GRAY);
            ordersPanel.add(emptyLabel);
        } else {
            for (Order order : openOrders) {
                ordersPanel.add(createOrderCard(order));
                ordersPanel.add(Box.createVerticalStrut(10));
            }
        }

        ordersPanel.revalidate();
        ordersPanel.repaint();
    }

    private JPanel createOrderCard(Order order) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Верхняя строка: номер заказа и статус
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel orderNumLabel = new JLabel("Заказ №" + order.getId());
        orderNumLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(orderNumLabel, BorderLayout.WEST);

        JLabel statusLabel = new JLabel(order.getStatus().toUpperCase());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 11));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

        // Цвет статуса
        if ("open".equals(order.getStatus())) {
            statusLabel.setBackground(new Color(255, 152, 0));  // оранжевый
            statusLabel.setText("🟡 ОТКРЫТ");
        } else if ("paid".equals(order.getStatus())) {
            statusLabel.setBackground(new Color(76, 175, 80));   // зелёный
            statusLabel.setText("✅ ОПЛАЧЕН");
        } else {
            statusLabel.setBackground(new Color(100, 100, 100)); // серый
            statusLabel.setText("📋 ЗАКРЫТ");
        }
        headerPanel.add(statusLabel, BorderLayout.EAST);

        card.add(headerPanel, BorderLayout.NORTH);

        // Информация о заказе
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        // Получаем информацию о столе и официанте
        TableDAO tableDAO = new TableDAO();
        Table table = tableDAO.getTableById(order.getTableId());
        String tableInfo = (table != null) ? "Стол " + table.getTableNumber() + " (" + table.getSeats() + " мест)" : "Стол №" + order.getTableId();

        JLabel tableLabel = new JLabel("📍 " + tableInfo);
        tableLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        tableLabel.setForeground(new Color(80, 80, 80));
        infoPanel.add(tableLabel);

        // Информация об официанте (если есть метод)
        String waiterInfo = "Официант: ID " + order.getWaiterId();
        JLabel waiterLabel = new JLabel("👨‍🍳 " + waiterInfo);
        waiterLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        waiterLabel.setForeground(new Color(80, 80, 80));
        infoPanel.add(waiterLabel);

        card.add(infoPanel, BorderLayout.CENTER);

        // Сумма заказа
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JLabel sumLabel = new JLabel("💰 " + order.getTotalSum() + " ₽");
        sumLabel.setFont(new Font("Arial", Font.BOLD, 13));
        sumLabel.setForeground(new Color(46, 125, 50));
        bottomPanel.add(sumLabel, BorderLayout.WEST);

        card.add(bottomPanel, BorderLayout.SOUTH);

        // Клик по карточке
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Table tableForOrder = tableDAO.getTableById(order.getTableId());
                if (tableForOrder != null) {
                    OrderDialog dialog = new OrderDialog(MainFrame.this, tableForOrder, order);
                    dialog.setVisible(true);
                    refreshStats();
                    floorPlan.refreshTables();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 250, 235));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255, 193, 7), 2),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        });

        return card;
    }

    private void refreshInfoPanel() {
        infoPanel.removeAll();

        // Текущая дата и время
        JPanel dateTimePanel = createInfoCard("🕐 Текущее время",
                dateFormat.format(new Date()) + " " + timeFormat.format(new Date()));
        infoPanel.add(dateTimePanel);
        infoPanel.add(Box.createVerticalStrut(10));

        // Количество активных официантов
        List<Waiter> allWaiters = waiterDAO.getAllWaiters();
        long activeWaiters = allWaiters.stream()
                .filter(w -> w.getShift() != null && !w.getShift().isEmpty())
                .count();
        JPanel waitersPanel = createInfoCard("👩‍🍳 Официанты в зале",
                String.valueOf(activeWaiters) + " человек");
        infoPanel.add(waitersPanel);
        infoPanel.add(Box.createVerticalStrut(10));

        // Статистика по заказам за сегодня
        List<Order> todayOrders = orderDAO.getTodayOrders(); // Нужно добавить метод
        long completedOrders = todayOrders.stream()
                .filter(o -> "closed".equals(o.getStatus()) || "paid".equals(o.getStatus()))
                .count();
        long activeOrders = todayOrders.stream()
                .filter(o -> "open".equals(o.getStatus()))
                .count();

        JPanel ordersStatsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        ordersStatsPanel.setBackground(Color.WHITE);
        ordersStatsPanel.add(createStatLine("📊 Завершённых заказов:", String.valueOf(completedOrders)));
        ordersStatsPanel.add(createStatLine("🔄 Активных заказов:", String.valueOf(activeOrders)));

        JPanel ordersStatsCard = createInfoCard("📈 Статистика заказов", "");
        ordersStatsCard.add(ordersStatsPanel, BorderLayout.CENTER);
        infoPanel.add(ordersStatsCard);
        infoPanel.add(Box.createVerticalStrut(10));

        // Выручка за сегодня
        BigDecimal todayRevenue = orderDAO.getTodayRevenue(); // Нужно добавить метод
        JPanel revenuePanel = createInfoCard("💰 Выручка за сегодня",
                todayRevenue + " ₽");
        infoPanel.add(revenuePanel);
        infoPanel.add(Box.createVerticalStrut(10));

        // Дополнительно: информация о смене
        String shift = getCurrentShift();
        JPanel shiftPanel = createInfoCard("🔄 Текущая смена", shift);
        infoPanel.add(shiftPanel);

        infoPanel.revalidate();
        infoPanel.repaint();
    }

    private JPanel createInfoCard(String title, String value) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(new Color(100, 70, 40));
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(new Color(46, 125, 50));
        valueLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createStatLine(String label, String value) {
        JPanel line = new JPanel(new BorderLayout());
        line.setBackground(Color.WHITE);
        JLabel labelL = new JLabel(label);
        labelL.setFont(new Font("Arial", Font.PLAIN, 11));
        JLabel valueL = new JLabel(value);
        valueL.setFont(new Font("Arial", Font.BOLD, 12));
        valueL.setForeground(new Color(80, 80, 80));
        line.add(labelL, BorderLayout.WEST);
        line.add(valueL, BorderLayout.EAST);
        return line;
    }

    private String getCurrentShift() {
        int hour = Integer.parseInt(timeFormat.format(new Date()).split(":")[0]);
        if (hour >= 6 && hour < 15) return "Утренняя (06:00 - 15:00)";
        if (hour >= 15 && hour < 23) return "Вечерняя (15:00 - 23:00)";
        return "Ночная (23:00 - 06:00)";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}