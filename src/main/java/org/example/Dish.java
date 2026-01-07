package org.example;

import java.util.List;

public class Dish {

    private int id;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;
    private Double price; // nouvelle colonne : prix de vente

    // Constructeur
    public Dish(int id, String name, DishTypeEnum dishType, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
        this.price = price;
    }

    // Getter / Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    // Coût total des ingrédients
    public double getDishCost() {
        double totalCost = 0.0;
        if (ingredients != null) {
            for (Ingredient ingredient : ingredients) {
                totalCost += ingredient.getPrice();
            }
        }
        return totalCost;
    }

    // Calcul de la marge brute : prix de vente - coût des ingrédients
    public double getGrossMargin() {
        if (price == null) {
            throw new RuntimeException("Prix de vente non défini, impossible de calculer la marge brute.");
        }
        return price - getDishCost();
    }
}
