package org.example;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RestaurantTable {
    private Integer id;
    private Integer number;
    private List<TableOrder> orders = new ArrayList<>(); // Historique des occupations

    // verification ve hoe malalaka ve le table intant
    public boolean isAvailableAt(Instant t) {
        for (TableOrder to : orders) {
            // occupeé sa tsia
            if (!t.isBefore(to.getArrivalDatetime()) &&
                    (to.getDepartureDatetime() == null || t.isBefore(to.getDepartureDatetime()))) {
                return false;
            }
        }
        return true;
    }
    // Getters et Setters...


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public List<TableOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<TableOrder> orders) {
        this.orders = orders;
    }
}