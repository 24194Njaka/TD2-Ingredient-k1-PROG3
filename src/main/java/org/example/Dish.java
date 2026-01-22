package org.example;

import java.util.ArrayList;
import java.util.List;

public class Dish {

    private Integer id;
    private String name;
    private Double price;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients = new ArrayList<>();

    public Dish(Integer id, String name, DishTypeEnum dishType) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
    }

    public Dish(Integer id, String name, DishTypeEnum dishType, Double price) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.price = price;
    }

    // Calcul du prix total du plat
    public Double getDishCost() {
        return ingredients.stream()
                .mapToDouble(i -> i.getPrice() != null ? i.getPrice() : 0.0)
                .sum();
    }

    // Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        ingredient.setDish(this);
    }

    // Marge brute = prix de vente - coût total des ingrédients

    public Double getGrossMargin() {
        if (price == null) {
            throw new IllegalStateException(
                    "Le prix de vente n'est pas encore fixé, impossible de calculer la marge brute"
            );
        }

        return price - getDishCost();
    }




    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", dishType=" + dishType +
                ", ingredients=" + ingredients +
                '}';
    }
}
