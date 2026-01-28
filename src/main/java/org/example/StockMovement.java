package org.example;

import java.time.Instant;

public class StockMovement {
    private Integer id;
    private StockValue value; // Contient quantité et unité
    private MovementTypeEnum type; // IN ou OUT
    private Instant creationDatetime;

    public StockMovement(Integer id, StockValue value, MovementTypeEnum type, Instant creationDatetime) {
        this.id = id;
        this.value = value;
        this.type = type;
        this.creationDatetime = creationDatetime;
    }

    public StockMovement() {

    }

    public Integer getId() {
        return id;
    }

    public StockValue getValue() {
        return value;
    }

    public MovementTypeEnum getType() {
        return type;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setValue(StockValue value) {
        this.value = value;
    }

    public void setType(MovementTypeEnum type) {
        this.type = type;
    }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }
}
