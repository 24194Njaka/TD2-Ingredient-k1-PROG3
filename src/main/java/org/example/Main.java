package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();
        try (Connection connection = DBConnection.getDBConnection()) {
            System.out.println("Connection successful");
        } catch (RuntimeException | SQLException e) {
            System.err.println(e.getMessage());
        }
        //  Test : récupérer le plat avec l'id = 1
        Dish dish = dataRetriever.findDishById(1);

        if (dish == null) {
            System.out.println("Aucun plat trouvé.");
            return;
        }

        System.out.println("Plat : " + dish.getName());
        System.out.println("Type : " + dish.getDishType());
        System.out.println("Prix : " + dish.getPrice());

        System.out.println("Ingrédients :");
        for (Ingredient ingredient : dish.getIngredients()) {
            System.out.println("- " + ingredient.getName()
                    + " | " + ingredient.getCategory()
                    + " | " + ingredient.getPrice());
        }











    }




    }





















