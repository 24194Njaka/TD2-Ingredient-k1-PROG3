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



//        Dish saladeExotique = new Dish(
//                null, // ID null → sera auto-généré
//                "Salade exotique",
//                DishTypeEnum.STARTER,
//                1500.0
//        );
//
//        // Création des ingrédients
//        List<Ingredient> ingredients = new ArrayList<>();
//        ingredients.add(new Ingredient(null, "Mangue", 500.0, CategoryEnum.VEGETABLE, saladeExotique));
//        ingredients.add(new Ingredient(null, "Avocat", 700.0, CategoryEnum.VEGETABLE, saladeExotique));
//        ingredients.add(new Ingredient(null, "Crevettes", 800.0, CategoryEnum.ANIMAL, saladeExotique));
//
//        saladeExotique.setIngredients(ingredients);
//
//        // Sauvegarde du plat + ingrédients
//        try {
//            Dish savedDish = dr.saveDish(saladeExotique);
//            System.out.println("Plat sauvegardé avec succès :");
//            System.out.println(savedDish);
//        } catch (RuntimeException e) {
//            System.err.println(" Échec de la création ou mise à jour du plat");
//            e.printStackTrace();
//        }
//
//        // Optionnel : mise à jour du plat
//        saladeExotique.setPrice(1800.0);
//        try {
//            Dish updatedDish = dr.saveDish(saladeExotique);
//            System.out.println(" Plat mis à jour avec succès :");
//            System.out.println(updatedDish);
//        } catch (RuntimeException e) {
//            System.err.println(" Échec de la mise à jour du plat");
//            e.printStackTrace();
//        }





        //======== findDishsByIngredientName ========


        String searchIngredient = "Ail";

        try {
            List<Dish> dishes = dr.findDishesByIngredientName(searchIngredient);

            if (dishes.isEmpty()) {
                System.out.println("Aucun plat ne contient l'ingrédient : " + searchIngredient);
            } else {
                System.out.println("Plats contenant l'ingrédient '" + searchIngredient + "' :");
                for (Dish dish : dishes) {
                    System.out.println(dish);  // utilise le toString de Dish
                }
            }

        } catch (RuntimeException e) {
            System.err.println(" Échec de la récupération des plats");
            e.printStackTrace();
        }
    }






    }

































































