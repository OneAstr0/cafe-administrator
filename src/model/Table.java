package model;

public class Table {
    private int id;
    private int tableNumber;
    private int seats;
    private String status;

    public Table() {}

    public Table(int id, int tableNumber, int seats, String status) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.seats = seats;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getTableNumber() { return tableNumber; }
    public void setTableNumber(int tableNumber) { this.tableNumber = tableNumber; }
    public int getSeats() { return seats; }
    public void setSeats(int seats) { this.seats = seats; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Стол " + tableNumber + " (" + seats + " мест) - " + status;
    }
}