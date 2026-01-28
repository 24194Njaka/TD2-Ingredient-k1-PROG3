package org.example;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Ingredient {
    private Integer id;
    private String name;
    private Double price;
    private CategoryEnum category;
    private   Dish dish;
    private List<StockMovement> stockMovementList = new ArrayList<>();


    public Ingredient(Integer id, String name, Double price, CategoryEnum category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dish = dish;
    }

    // Méthode demandée explicitement
    public String getDishName() {
        return dish != null ? dish.getName() : null;
    }

    // Getters & Setters
    public Integer getId() {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        this.price = price;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }



    public StockValue getStockValueAt(Instant t) {
        double totalQuantityInKg = 0.0;

        // On parcourt la liste des mouvements de CET ingrédient
        for (StockMovement sm : this.stockMovementList) {
            if (!sm.getCreationDatetime().isAfter(t)) {
                // On utilise le nom de CET ingrédient (this.name) pour la conversion
                double quantityInKg = UnitConverter.convertToKg(
                        this.name,
                        sm.getValue().getQuantity(),
                        sm.getValue().getUnit()
                );

                if (sm.getType() == MovementTypeEnum.IN) {
                    totalQuantityInKg += quantityInKg;
                } else {
                    totalQuantityInKg -= quantityInKg;
                }
            }
        }
        return new StockValue(totalQuantityInKg, UnitType.KG);
    }

    public List<StockMovement> getStockMovementList() {
        return stockMovementList;
    }

    public void addStockMovement(StockMovement sm) {
        this.stockMovementList.add(sm);
    }



    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", dish=" + (dish != null ? dish.getName() : null) +
                '}';
    }


}

