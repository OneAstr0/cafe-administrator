package model;

import java.math.BigDecimal;

public class Dish {
    private int id;
    private String name;
    private int categoryId;
    private BigDecimal price;
    private String weightVolume;

    public Dish() {}

    public Dish(int id, String name, int categoryId, BigDecimal price, String weightVolume) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.price = price;
        this.weightVolume = weightVolume;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getWeightVolume() { return weightVolume; }
    public void setWeightVolume(String weightVolume) { this.weightVolume = weightVolume; }

    @Override
    public String toString() {
        return name + " - " + price + " ₽ (" + weightVolume + ")";
    }
}