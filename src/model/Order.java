package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Order {
    private int id;
    private int tableId;
    private int waiterId;
    private Timestamp openedAt;
    private Timestamp closedAt;
    private BigDecimal totalSum;
    private String status;

    public Order() {}

    public Order(int id, int tableId, int waiterId, Timestamp openedAt,
                 Timestamp closedAt, BigDecimal totalSum, String status) {
        this.id = id;
        this.tableId = tableId;
        this.waiterId = waiterId;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.totalSum = totalSum;
        this.status = status;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }
    public int getWaiterId() { return waiterId; }
    public void setWaiterId(int waiterId) { this.waiterId = waiterId; }
    public Timestamp getOpenedAt() { return openedAt; }
    public void setOpenedAt(Timestamp openedAt) { this.openedAt = openedAt; }
    public Timestamp getClosedAt() { return closedAt; }
    public void setClosedAt(Timestamp closedAt) { this.closedAt = closedAt; }
    public BigDecimal getTotalSum() { return totalSum; }
    public void setTotalSum(BigDecimal totalSum) { this.totalSum = totalSum; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}