package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private FloorPlanPanel floorPlan;
    private JTextArea statsArea;

    public MainFrame() {
        setTitle("🍽️ SELECT * FROM Menu - Система учёта заказов");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Левая панель - карта зала
        floorPlan = new FloorPlanPanel();
        floorPlan.setOnTableSelected(this::refreshStats);
        add(floorPlan, BorderLayout.CENTER);

        // Правая панель - статистика
        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(statsArea);
        scrollPane.setPreferredSize(new Dimension(300, 0));
        scrollPane.setBorder(BorderFactory.createTitledBorder("📊 Сводка"));
        add(scrollPane, BorderLayout.EAST);

        // Верхняя панель - заголовок
        JLabel titleLabel = new JLabel("SELECT * FROM Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(100, 70, 40));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Нижняя панель - кнопки управления
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("🔄 Обновить");
        refreshBtn.addActionListener(e -> {
            floorPlan.refreshTables();
            refreshStats();
        });
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshStats();
    }

    private void refreshStats() {
        // TODO: заполнить статистику из БД
        statsArea.setText("Загрузка статистики...\n\n" +
                "Всего столов: 8\n" +
                "Свободных: -\n" +
                "Занятых: -\n\n" +
                "Активные заказы: -\n\n" +
                "Официанты на смене:\n" +
                "• -\n\n" +
                "Выручка за сегодня:\n" +
                "- ₽");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}