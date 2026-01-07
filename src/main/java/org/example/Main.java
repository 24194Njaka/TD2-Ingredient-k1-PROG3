package org.example;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.example.Dish;
import org.example.Ingredient;
import org.example.DataRetriever;
import org.example.CategoryEnum;
import org.example.DishTypeEnum;


public class Main {

    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever();
            try (Connection connection = DBConnection.getDBConnection()) {
                System.out.println("Connection successful");
            } catch (RuntimeException | SQLException e) {
                System.err.println(e.getMessage());
            }


//         a) findDishById(id = 1)
        System.out.println("=== TEST a) findDishById(1) ===");
        try {
            Dish dish = dataRetriever.findDishById(1);
            System.out.println(dish.getName());
            dish.getIngredients().forEach(i -> System.out.println(" - " + i.getName()));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }

        // b) findDishById(id = 999)
        System.out.println("=== TEST b) findDishById(page =2 , size=2) ===");
        try {
            dataRetriever.findDishById(999);
        } catch (RuntimeException e) {
            System.out.println("Exception attendue");
       }

        // ===== Test c) createIngredients =====
        System.out.println("=== TEST c) createIngredients - SUCCESS ===");

        List<Ingredient> newIngredients = Arrays.asList(
                new Ingredient(0,"Fromage", 1200.0, CategoryEnum.DAIRY, null),
                new Ingredient(0,"Oignon", 500.0, CategoryEnum.VEGETABLE, null)
        );

        try {
            List<Ingredient> created = dataRetriever.createIngredients(newIngredients);
            created.forEach(i -> System.out.println("Créé : " + i.getName() + " (id=" + i.getId() + ")"));
        } catch (RuntimeException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        // ===== Test c) createIngredients - DUPLICATE =====
        System.out.println("=== TEST c) createIngredients - DUPLICATE ===");

        List<Ingredient> duplicateIngredients = Arrays.asList(
                new Ingredient(0,"Carotte", 2000.0, CategoryEnum.VEGETABLE, null),
                new Ingredient(0,"ronono", 800.0, CategoryEnum.VEGETABLE, null) // déjà existante
        );

        try {
            List<Ingredient> created = dataRetriever.createIngredients(duplicateIngredients);
            created.forEach(i -> System.out.println("Créé : " + i.getName()));
        } catch (RuntimeException e) {
            System.out.println("Exception attendue : " + e.getMessage());
        }
        System.out.println("=== TEST d) saveDish ===");

        Dish newDish = new Dish(
                0, // 0 ou null → INSERT
                "Soupe de légumes",
                DishTypeEnum.START,
                Arrays.asList(
                        new Ingredient(2, null, 0.0, null, null) // Oignon EXISTANT (id=2)
                )
        );

        Dish savedDish = dataRetriever.saveDish(newDish);
        System.out.println("Créé : " + savedDish.getName() + " (id=" + savedDish.getId());


        // Mise à jour d'un plat existant

        Dish updateDish = new Dish(
                1, // EXISTANT → UPDATE
                "Salade de fromage",
                DishTypeEnum.START,
                Arrays.asList(
                        new Ingredient(5, null, 0.0, null, null) // Fromage EXISTANT (id=5)
                )
        );

        Dish updatedDish = dataRetriever.saveDish(updateDish);

        System.out.println("Mis à jour : " + updatedDish.getName() + " (id=" + updatedDish.getId());;
        updatedDish.getIngredients().forEach(i ->
                System.out.println(" - ingredient id=" + i.getId())
        );


    }




}





