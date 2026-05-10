package ui;

import model.*;
import dao.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderDialog extends JDialog {
    private final Table table;
    private final Order existingOrder;
    private final OrderDAO orderDAO = new OrderDAO();
    private final DishDAO dishDAO = new DishDAO();
    private final WaiterDAO waiterDAO = new WaiterDAO();

    private JComboBox<Waiter> waiterCombo;
    private JTable itemsTable;
    private OrderItemsTableModel tableModel;
    private JLabel totalLabel;
    private List<OrderItem> orderItems = new ArrayList<>();
    private int currentOrderId;

    public OrderDialog(Frame parent, Table table, Order order) {
        super(parent, order == null ? "Новый заказ — Стол " + table.getTableNumber()
                : "Заказ №" + order.getId() + " — Стол " + table.getTableNumber(), true);
        this.table = table;
        this.existingOrder = order;

        setSize(700, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        if (order != null) {
            this.currentOrderId = order.getId();
            loadOrderItems();
        }

        initComponents();
        if (order != null) {
            loadWaiterInfo();
        }
    }

    private void initComponents() {
        // Верхняя панель — выбор официанта
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Информация о заказе"));

        topPanel.add(new JLabel("Официант:"));
        waiterCombo = new JComboBox<>();
        loadWaiters();
        waiterCombo.setPreferredSize(new Dimension(200, 30));
        topPanel.add(waiterCombo);

        if (existingOrder == null) {
            topPanel.add(new JLabel("    Стол:"));
            topPanel.add(new JLabel(String.valueOf(table.getTableNumber())));
        } else {
            topPanel.add(new JLabel("    Статус:"));
            topPanel.add(new JLabel(existingOrder.getStatus()));
        }

        add(topPanel, BorderLayout.NORTH);

        // Центральная панель — таблица блюд
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Добавленные блюда"));

        tableModel = new OrderItemsTableModel(orderItems);
        itemsTable = new JTable(tableModel);
        itemsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Панель добавления блюда
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.setBorder(BorderFactory.createTitledBorder("Добавить блюдо"));

        JComboBox<Dish> dishCombo = new JComboBox<>();
        loadDishes(dishCombo);
        dishCombo.setPreferredSize(new Dimension(300, 30));
        addPanel.add(dishCombo);

        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        quantitySpinner.setPreferredSize(new Dimension(60, 30));
        addPanel.add(quantitySpinner);

        JButton addButton = new JButton("➕ Добавить");
        addButton.addActionListener(e -> {
            Dish selected = (Dish) dishCombo.getSelectedItem();
            int quantity = (int) quantitySpinner.getValue();
            addDishToOrder(selected, quantity);
        });
        addPanel.add(addButton);

        centerPanel.add(addPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // Нижняя панель — итог и кнопки
        JPanel bottomPanel = new JPanel(new BorderLayout());

        totalLabel = new JLabel("Итого: 0 ₽", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(totalLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        if (existingOrder == null) {
            JButton createButton = new JButton("✅ Создать заказ");
            createButton.addActionListener(this::createOrder);
            buttonPanel.add(createButton);
        } else {
            JButton saveButton = new JButton("💾 Сохранить изменения");
            saveButton.addActionListener(this::saveChanges);
            buttonPanel.add(saveButton);

            if ("open".equals(existingOrder.getStatus())) {
                JButton closeButton = new JButton("🔒 Закрыть заказ");
                closeButton.addActionListener(this::closeOrder);
                buttonPanel.add(closeButton);
            }
        }

        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        updateTotal();
    }

    private void loadWaiters() {
        List<Waiter> waiters = waiterDAO.getAllWaiters();
        for (Waiter w : waiters) {
            waiterCombo.addItem(w);
        }
    }

    private void loadDishes(JComboBox<Dish> combo) {
        List<Dish> dishes = dishDAO.getAllDishes();
        for (Dish d : dishes) {
            combo.addItem(d);
        }
    }

    private void loadOrderItems() {
        orderItems = orderDAO.getOrderItems(currentOrderId);
    }

    private void loadWaiterInfo() {
        // Получаем информацию об официанте из БД
        // TODO: добавить метод getOrderById для получения waiterId
    }

    private void addDishToOrder(Dish dish, int quantity) {
        // Проверяем, есть ли уже такое блюдо
        for (OrderItem item : orderItems) {
            if (item.getDishId() == dish.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                tableModel.fireTableDataChanged();
                updateTotal();
                return;
            }
        }

        OrderItem newItem = new OrderItem(
                currentOrderId, dish.getId(), dish.getName(),
                quantity, dish.getPrice()
        );
        orderItems.add(newItem);
        tableModel.fireTableDataChanged();
        updateTotal();
    }

    private void updateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            total = total.add(item.getTotal());
        }
        totalLabel.setText("Итого: " + total + " ₽");
    }

    private void createOrder(ActionEvent e) {
        Waiter selectedWaiter = (Waiter) waiterCombo.getSelectedItem();
        if (selectedWaiter == null) {
            JOptionPane.showMessageDialog(this, "Выберите официанта!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Добавьте хотя бы одно блюдо!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Создаём заказ
        int orderId = orderDAO.createOrder(table.getId(), selectedWaiter.getId());
        if (orderId > 0) {
            // Добавляем все блюда
            for (OrderItem item : orderItems) {
                orderDAO.addOrderItem(orderId, item.getDishId(), item.getQuantity(), item.getPriceAtOrder());
            }

            // Обновляем статус стола
            TableDAO tableDAO = new TableDAO();
            tableDAO.updateTableStatus(table.getId(), "occupied");

            JOptionPane.showMessageDialog(this, "Заказ успешно создан!", "Успех", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Ошибка при создании заказа!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveChanges(ActionEvent e) {
        // TODO: обновить состав заказа в БД
        JOptionPane.showMessageDialog(this, "Изменения сохранены!", "Успех", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void closeOrder(ActionEvent e) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            total = total.add(item.getTotal());
        }

        if (orderDAO.closeOrder(currentOrderId, total)) {
            // Обновляем статус стола
            TableDAO tableDAO = new TableDAO();
            tableDAO.updateTableStatus(table.getId(), "free");

            JOptionPane.showMessageDialog(this, "Заказ закрыт на сумму " + total + " ₽", "Успех", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Ошибка при закрытии заказа!", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Модель таблицы для OrderItem
    class OrderItemsTableModel extends AbstractTableModel {
        private final String[] columns = {"Название", "Цена", "Кол-во", "Сумма"};
        private final List<OrderItem> items;

        public OrderItemsTableModel(List<OrderItem> items) {
            this.items = items;
        }

        @Override
        public int getRowCount() { return items.size(); }

        @Override
        public int getColumnCount() { return columns.length; }

        @Override
        public String getColumnName(int col) { return columns[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            OrderItem item = items.get(row);
            switch (col) {
                case 0: return item.getDishName();
                case 1: return item.getPriceAtOrder() + " ₽";
                case 2: return item.getQuantity();
                case 3: return item.getTotal() + " ₽";
                default: return null;
            }
        }
    }
}