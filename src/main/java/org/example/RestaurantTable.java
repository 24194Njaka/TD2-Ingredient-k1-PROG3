package org.example;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RestaurantTable {
    // Attributs obligatoires selon le SI
    private Integer id; // Identifiant unique
    private Integer number; // Numéro de table
    private List<TableOrder> orders = new ArrayList<>(); // Liste des commandes associées

    // Constructeur pour initialiser les données et éviter le "null"
    public RestaurantTable(Integer id, Integer number) {
        this.id = id;
        this.number = number;
    }

    /**
     * Vérifie si la table est disponible à un instant donné.
     * @param t L'instant à vérifier (généralement Instant.now())
     * @return true si aucune commande n'occupe la table à cet instant
     */
    public boolean isAvailableAt(Instant t) {
        for (TableOrder to : orders) {
            // Une table est occupée si 't' est entre l'arrivée et le départ
            if (!t.isBefore(to.getArrivalDatetime()) &&
                    (to.getDepartureDatetime() == null || t.isBefore(to.getDepartureDatetime()))) {
                return false;
            }
        }
        return true;
    }

    // --- Getters et Setters ---

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