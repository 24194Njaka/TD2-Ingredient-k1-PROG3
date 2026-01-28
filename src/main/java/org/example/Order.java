package org.example;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private Integer id;
    private String reference;
    private Instant creationDatetime;
    private List<DishOrder> dishOrders = new ArrayList<>();

    public Order() {
    }

    // ✅ Calcul du montant total HT
    public Double getTotalAmountWithoutVAT() {
        return dishOrders.stream()
                .mapToDouble(doObj -> doObj.getDish().getPrice() * doObj.getQuantity())
                .sum();
    }

    // ✅ Calcul du montant total TTC (exemple à 20% de taxe)
    public Double getTotalAmountWithVAT() {
        return getTotalAmountWithoutVAT() * 1.20;
    }

    // ✅ Getters et Setters indispensables
    public List<DishOrder> getDishOrders() {
        return dishOrders;
    }

    public void addDishOrder(DishOrder dishOrder) {
        this.dishOrders.add(dishOrder);
    }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Instant getCreationDatetime() { return creationDatetime; }
    public void setCreationDatetime(Instant creationDatetime) { this.creationDatetime = creationDatetime; }
}