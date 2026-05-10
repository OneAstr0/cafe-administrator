package model;

public class Waiter {
    private int id;
    private String fullName;
    private String phone;
    private String shift;

    public Waiter() {}

    public Waiter(int id, String fullName, String phone, String shift) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.shift = shift;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    @Override
    public String toString() {
        return fullName + " (" + shift + ")";
    }
}