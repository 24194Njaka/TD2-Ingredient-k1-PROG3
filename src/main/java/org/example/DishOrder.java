package org.example;

public class DishOrder {

    private Integer id;
    private Dish dish;
    private Integer quantity; // Le nombre de fois que ce plat est commandé

    public DishOrder(Integer id, Dish dish, Integer quantity) {
        this.id = id;
        this.dish = dish;
        this.quantity = quantity;
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "DishOrder{" +
                "dish=" + (dish != null ? dish.getName() : "null") +
                ", quantity=" + quantity +
                '}';
    }
}
