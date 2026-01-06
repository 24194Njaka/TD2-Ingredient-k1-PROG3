package org.example;

public class Dish {


    private int id;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;

    public Dish(int id, String name, DishTypeEnum dishType, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DishTypeEnum getDishType() { return dishType; }
    public void setDishType(DishTypeEnum dishType) { this.dishType = dishType; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }

    // Calcul du prix total du plat en additionnant le prix de tous ses ingrédients
    public double getDishPrice() {
        double totalPrice = 0.0;
        if (ingredients != null) {
            for (Ingredient ingredient : ingredients) {
                totalPrice += ingredient.getPrice();
            }
        }
        return totalPrice;
    }
}
