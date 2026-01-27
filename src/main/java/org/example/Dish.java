package org.example;

import java.util.ArrayList;
import java.util.List;

public class Dish {

    private Integer id;
    private String name;
    private Double price;
    private DishTypeEnum dishType;
    private List<DishIngredient> ingredients = new ArrayList<>();

    public Dish(Integer id, String name, DishTypeEnum dishType) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }

    public DishTypeEnum getDishType() { return dishType; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) {
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Prix négatif interdit");
        }
        this.price = price;
    }

    public List<DishIngredient> getIngredients() {
        return ingredients;
    }

    public void addDishIngredient(DishIngredient di) {
        ingredients.add(di);
    }

    // Méthode métier
    public Double getGrossMargin() {
        if (price == null) {
            throw new IllegalStateException("Prix non défini");
        }

        double cost = ingredients.stream()
                .mapToDouble(di ->
                        di.getIngredient().getPrice()
                                * di.getQuantityRequired())
                .sum();

        return price - getDishCost();
    }

    public Double getDishCost() {
        return ingredients.stream()
                .mapToDouble(di -> di.getIngredient().getPrice() * di.getQuantityRequired())
                .sum();
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", dishType=" + dishType +
                '}';
    }
}
