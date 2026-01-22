package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();
        DataRetriever dataRetriever = new DataRetriever();
        try (Connection connection = DBConnection.getDBConnection()) {
            System.out.println("Connection successful");
        } catch (RuntimeException | SQLException e) {
            System.err.println(e.getMessage());
        }


        // ======= findDishByid =====

//        //  Test : récupérer le plat avec l'id = 1
//        Dish dish = dataRetriever.findDishById(1);
//
//        if (dish == null) {
//            System.out.println("Aucun plat trouvé.");
//            return;
//        }
//
//        System.out.println("Plat : " + dish.getName());
//        System.out.println("Type : " + dish.getDishType());
//        System.out.println("Prix : " + dish.getPrice());
//
//        System.out.println("Ingrédients :");
//        for (Ingredient ingredient : dish.getIngredients()) {
//            System.out.println("- " + ingredient.getName()
//                    + " | " + ingredient.getCategory()
//                    + " | " + ingredient.getPrice());
//        }


       // =============== findIngredients ==============

//
//        int page = 1;
//        int size = 5;
//
//        List<Ingredient> ingredients = dataRetriever.findIngredients(page, size);
//
//        for (Ingredient i : ingredients) {
//            System.out.println(
//                    i.getName() + " | " +
//                            i.getCategory() + " | " +
//                            i.getPrice() + " | Plat: " +
//                            (i.getDish() != null ? i.getDish().getName() : "Aucun")
//            );
//        }




     // ============ CREATEINGREDIENTS ===========


//
//        Dish pouletGrille = new Dish(
//                2,
//                "Poulet grillé",
//                DishTypeEnum.MAIN
//        );
//
//        List<Ingredient> ingredients = List.of(
//                new Ingredient(null, "Ail", 500.00, CategoryEnum.VEGETABLE, pouletGrille),
//                new Ingredient(null, "Poivre", 300.00, CategoryEnum.OTHER, pouletGrille)
//        );
//
//        try {
//            List<Ingredient> created = dr.createIngredients(ingredients);
//            created.forEach(System.out::println);
//        } catch (RuntimeException e) {
//            System.err.println("Échec de l’opération");
//            e.printStackTrace();
//        }




        // ============== SaveDish =======



        try {
            // 1️⃣ Création d'un nouveau plat
            Dish saladeExotique = new Dish(null, "Salade exotique", DishTypeEnum.STARTER, 1500.00);

            // Création des ingrédients
            List<Ingredient> ingredients = new ArrayList<>();
            ingredients.add(new Ingredient(null, "Mangue", 300.0, CategoryEnum.VEGETABLE, saladeExotique));
            ingredients.add(new Ingredient(null, "Avocat", 400.0, CategoryEnum.VEGETABLE, saladeExotique));

            saladeExotique.setIngredients(ingredients);

            // Sauvegarde du plat (insertion)
            Dish createdDish = dr.saveDish(saladeExotique);
            System.out.println("Plat créé : " + createdDish);

            // 2️⃣ Mise à jour du plat : ajout et suppression d'ingrédients
            List<Ingredient> updatedIngredients = new ArrayList<>();
            updatedIngredients.add(new Ingredient(null, "Mangue", 300.0, CategoryEnum.VEGETABLE, createdDish));
            updatedIngredients.add(new Ingredient(null, "Crevettes", 500.0, CategoryEnum.ANIMAL, createdDish));

            createdDish.setIngredients(updatedIngredients);
            createdDish.setPrice(1800.0); // mise à jour du prix

            // Sauvegarde du plat (update)
            Dish updatedDish = dr.saveDish(createdDish);
            System.out.println("Plat mis à jour : " + updatedDish);

        } catch (RuntimeException e) {
            System.err.println("❌ Échec de la création ou mise à jour du plat");
            e.printStackTrace();
        }










    }












}








































