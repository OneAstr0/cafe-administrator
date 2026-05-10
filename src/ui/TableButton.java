package ui;

import model.Table;
import javax.swing.JButton;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TableButton extends JButton {
    private final Table table;
    private final int tableWidth;
    private final int tableHeight;
    private boolean isHovered = false;
    private Color baseColor;
    private static final Color HOVER_BORDER_COLOR = new Color(255, 193, 7);  // золотистый
    private static final int BORDER_THICKNESS = 4;

    public TableButton(Table table, int width, int height) {
        this.table = table;
        this.tableWidth = width;
        this.tableHeight = height;

        setText(String.valueOf(table.getTableNumber()));
        setFont(new Font("Arial", Font.BOLD, 16));
        setPreferredSize(new Dimension(width, height));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);

        updateColor();

        // Добавляем обработчик наведения мыши
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Рисуем закруглённый прямоугольник
        int w = getWidth();
        int h = getHeight();
        int arc = 20;

        // Цвет фона
        if ("free".equals(table.getStatus())) {
            baseColor = new Color(76, 175, 80);
        } else {
            baseColor = new Color(244, 67, 54);
        }
        g2d.setColor(baseColor);
        g2d.fillRoundRect(0, 0, w, h, arc, arc);

        // Если наведены — рисуем жирную обводку
        if (isHovered) {
            g2d.setColor(HOVER_BORDER_COLOR);
            g2d.setStroke(new BasicStroke(BORDER_THICKNESS));
            g2d.drawRoundRect(BORDER_THICKNESS / 2, BORDER_THICKNESS / 2,
                    w - BORDER_THICKNESS, h - BORDER_THICKNESS, arc, arc);

            // Добавляем лёгкое свечение
            g2d.setColor(new Color(255, 193, 7, 50));
            for (int i = 1; i <= 3; i++) {
                g2d.setStroke(new BasicStroke(BORDER_THICKNESS - i));
                g2d.drawRoundRect(BORDER_THICKNESS / 2 + i, BORDER_THICKNESS / 2 + i,
                        w - BORDER_THICKNESS - i * 2, h - BORDER_THICKNESS - i * 2, arc, arc);
            }
        }

        g2d.dispose();

        // Рисуем текст поверх
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Отключаем стандартную границу
    }

    public void updateColor() {
        repaint();
    }

    public Table getTable() {
        return table;
    }

    public void setStatus(String status) {
        table.setStatus(status);
        updateColor();
    }
}