package org.example;


import java.util.ArrayList;
import java.util.List;

public class Dish {

    private int id;
    private String name;
    private  Double price ;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients = new ArrayList<>();

    public Dish(int id, String name, DishTypeEnum dishType) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
    }

    public Dish(int id,  String name, DishTypeEnum dishType,Double price) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.price = price;
    }

    //Calcul du prix total du plat
    public Double getDishCost() {
        return ingredients.stream()
                .mapToDouble(Ingredient::getPrice)
                .sum();
    }

    public Double getPrice() {
        return price;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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



}

