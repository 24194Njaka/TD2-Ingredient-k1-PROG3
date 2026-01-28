package org.example;

import java.util.ArrayList;
import java.util.List;

public class Dish {
    private Integer id;
    private String name;
    private Double price;
    private DishTypeEnum dishType;
    private List<DishIngredient> dishIngredients = new ArrayList<>();

    public Dish(Integer id, String name, DishTypeEnum dishType) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
    }

    public Dish() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }

    public List<DishIngredient> getIngredients() {
        return dishIngredients;
    }

    public void addDishIngredient(DishIngredient di) {
        this.dishIngredients.add(di);
    }

    public double getDishCost() {
        double total = 0;
        for (DishIngredient di : dishIngredients) {
            total += di.getCost();
        }
        return total;
    }

    public Double getGrossMargin() {
        if (price == null) {
            throw new IllegalStateException("Marge impossible : prix de vente nul pour " + name);
        }
        return price - getDishCost();
    }

    @Override
    public String toString() {
        return String.format("Dish[ID=%d, Nom=%s, Prix=%.2f, Coût=%.2f, Marge=%.2f]",
                id, name, price, getDishCost(), (price != null ? getGrossMargin() : 0));
    }
}