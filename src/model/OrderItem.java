package model;

import java.math.BigDecimal;

public class OrderItem {
    private int orderId;
    private int dishId;
    private String dishName;
    private int quantity;
    private BigDecimal priceAtOrder;

    public OrderItem() {}

    public OrderItem(int orderId, int dishId, String dishName, int quantity, BigDecimal priceAtOrder) {
        this.orderId = orderId;
        this.dishId = dishId;
        this.dishName = dishName;
        this.quantity = quantity;
        this.priceAtOrder = priceAtOrder;
    }

    // Геттеры и сеттеры
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getDishId() { return dishId; }
    public void setDishId(int dishId) { this.dishId = dishId; }
    public String getDishName() { return dishName; }
    public void setDishName(String dishName) { this.dishName = dishName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getPriceAtOrder() { return priceAtOrder; }
    public void setPriceAtOrder(BigDecimal priceAtOrder) { this.priceAtOrder = priceAtOrder; }

    public BigDecimal getTotal() {
        return priceAtOrder.multiply(BigDecimal.valueOf(quantity));
    }
}