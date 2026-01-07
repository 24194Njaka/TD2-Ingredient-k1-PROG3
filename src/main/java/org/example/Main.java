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
        System.out.println("\n=== TEST b) findIngredients avec pagination ===");

// Test page 2, size 2 (exemple du TD)
        List<Ingredient> page2 = dataRetriever.findIngredients(2, 2);
        System.out.println("Page 2, size 2 :");
        if (page2.isEmpty()) {
            System.out.println("Liste vide");
        } else {
            page2.forEach(i -> System.out.println(i.getId() + " - " + i.getName()));
        }

// Test page 3, size 5 (devrait être vide si moins d'ingrédients)
        List<Ingredient> page3 = dataRetriever.findIngredients(3, 5);
        System.out.println("Page 3, size 5 :");
        System.out.println(page3.isEmpty() ? "Liste vide" : page3);




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
                new Ingredient(0,"ronono", 800.0, CategoryEnum.VEGETABLE, null)
        );

        try {
            List<Ingredient> created = dataRetriever.createIngredients(duplicateIngredients);
            created.forEach(i -> System.out.println("Créé : " + i.getName()));
        } catch (RuntimeException e) {
            System.out.println("Exception attendue : " + e.getMessage());
        }
        System.out.println("=== TEST d) saveDish ===");

        Dish newDish = new Dish(
                0,
                "Soupe de légumes",
                DishTypeEnum.START,
                Arrays.asList(
                        new Ingredient(2, null, 0.0, null, null)
                )
        );

        Dish savedDish = dataRetriever.saveDish(newDish);
        System.out.println("Créé : " + savedDish.getName() + " (id=" + savedDish.getId());


        Dish updateDish = new Dish(
                1,
                "Salade de fromage",
                DishTypeEnum.START,
                Arrays.asList(
                        new Ingredient(5, null, 0.0, null, null)
                )
        );

        Dish updatedDish = dataRetriever.saveDish(updateDish);

        System.out.println("Mis à jour : " + updatedDish.getName() + " (id=" + updatedDish.getId());;
        updatedDish.getIngredients().forEach(i ->
                System.out.println(" - ingredient id=" + i.getId())
        );



        System.out.println("=== TEST e) findDishsByIngredientName ===");

        List<Dish> dishesWithChoc = dataRetriever.findDishsByIngredientName("choc");

        for (Dish d : dishesWithChoc) {
            System.out.println("Dish: " + d.getName() + " (id=" + d.getId() + ")");
        }


        System.out.println("=== TEST f) findIngredientsByCriteria ===");

// Ex 1 : Tous les légumes
        List<Ingredient> vegIngredients = dataRetriever.findIngredientsByCriteria(
                null, CategoryEnum.VEGETABLE, null, 1, 10
        );
        vegIngredients.forEach(i -> System.out.println(i.getName()));

// Ex 2 : Chocolat lié à gâteau
        List<Ingredient> chocGâteau = dataRetriever.findIngredientsByCriteria(
                "cho", null, "gâteau", 1, 10
        );
        chocGâteau.forEach(i -> System.out.println(i.getName()));




    }





}





