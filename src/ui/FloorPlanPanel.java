package ui;

import model.Order;
import model.Table;
import dao.TableDAO;
import dao.OrderDAO;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FloorPlanPanel extends JPanel {
    private final Map<Integer, TableButton> tableButtons = new HashMap<>();
    private final TableDAO tableDAO = new TableDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private Runnable onTableSelected;

    public FloorPlanPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 220));  // бежевый фон (зал)
        setBorder(BorderFactory.createTitledBorder("📍 План зала"));
        loadTables();
    }

    public void setOnTableSelected(Runnable callback) {
        this.onTableSelected = callback;
    }

    private void loadTables() {
        List<Table> tables = tableDAO.getAllTables();

        // Словарь для позиционирования столов
        // Формат: (номер стола) -> (gridx, gridy, ширина, высота)
        Map<Integer, int[]> positions = new HashMap<>();
        positions.put(1, new int[]{0, 0, 80, 80});
        positions.put(2, new int[]{0, 1, 80, 80});
        positions.put(3, new int[]{2, 0, 120, 80});
        positions.put(4, new int[]{4, 0, 100, 80});
        positions.put(5, new int[]{2, 2, 100, 80});
        positions.put(6, new int[]{4, 2, 80, 80});
        positions.put(7, new int[]{1, 3, 200, 100});
        positions.put(8, new int[]{0, 2, 80, 80});

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.CENTER;

        for (Table table : tables) {
            int[] pos = positions.get(table.getTableNumber());
            if (pos == null) continue;

            TableButton btn = new TableButton(table, pos[2], pos[3]);
            btn.addActionListener(e -> {
                onTableClick(btn);
                if (onTableSelected != null) onTableSelected.run();
            });

            gbc.gridx = pos[0];
            gbc.gridy = pos[1];
            add(btn, gbc);
            tableButtons.put(table.getId(), btn);
        }
    }

    private void onTableClick(TableButton btn) {
        Table table = btn.getTable();
        Order openOrder = orderDAO.getOpenOrderByTableId(table.getId());

        String status = "free".equals(table.getStatus()) ? "СВОБОДЕН" : "ЗАНЯТ";
        String orderInfo = (openOrder != null) ? "\nЗаказ №" + openOrder.getId() : "";

        int option = JOptionPane.showConfirmDialog(
                this,
                "Стол " + table.getTableNumber() + " (" + table.getSeats() + " мест)\n" +
                        "Статус: " + status + orderInfo + "\n\n" +
                        ("free".equals(table.getStatus()) ? "Открыть новый заказ?" : "Открыть текущий заказ?"),
                "Стол " + table.getTableNumber(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            if ("free".equals(table.getStatus())) {
                openNewOrderDialog(table);
            } else {
                editOrderDialog(table, openOrder);
            }
        }
    }

    private void openNewOrderDialog(Table table) {
        OrderDialog dialog = new OrderDialog((Frame) SwingUtilities.getWindowAncestor(this), table, null);
        dialog.setVisible(true);
        refreshTables();
        if (onTableSelected != null) onTableSelected.run();
    }

    private void editOrderDialog(Table table, Order order) {
        OrderDialog dialog = new OrderDialog((Frame) SwingUtilities.getWindowAncestor(this), table, order);
        dialog.setVisible(true);
        refreshTables();
        if (onTableSelected != null) onTableSelected.run();
    }

    public void refreshTables() {
        List<Table> tables = tableDAO.getAllTables();
        for (Table table : tables) {
            TableButton btn = tableButtons.get(table.getId());
            if (btn != null) {
                btn.setStatus(table.getStatus());
            }
        }
        revalidate();
        repaint();
    }
}