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


//        String searchIngredient = "Ail";
//
//        try {
//            List<Dish> dishes = dr.findDishesByIngredientName(searchIngredient);
//
//            if (dishes.isEmpty()) {
//                System.out.println("Aucun plat ne contient l'ingrédient : " + searchIngredient);
//            } else {
//                System.out.println("Plats contenant l'ingrédient '" + searchIngredient + "' :");
//                for (Dish dish : dishes) {
//                    System.out.println(dish);  // utilise le toString de Dish
//                }
//            }
//
//        } catch (RuntimeException e) {
//            System.err.println(" Échec de la récupération des plats");
//            e.printStackTrace();
//        }


        //======== findIngredientsByCriteria========


//        try {
//            List<Ingredient> ingredients1 = dr.findIngredientsByCriteria("Ail", CategoryEnum.VEGETABLE, null, 1, 10);
//            System.out.println("=== Résultat 1 ===");
//            ingredients1.forEach(System.out::println);
//        } catch (RuntimeException e) {
//            System.err.println(" Échec de la recherche d'ingrédients 1");
//            e.printStackTrace();
//        }
//
//        // Exemple 2 : tous les ingrédients du plat "Poulet grillé", peu importe le nom ou catégorie
//        try {
//            List<Ingredient> ingredients2 = dr.findIngredientsByCriteria(null, null, "Poulet grillé", 1, 10);
//            System.out.println("=== Résultat 2 ===");
//            ingredients2.forEach(System.out::println);
//        } catch (RuntimeException e) {
//            System.err.println("Échec de la recherche d'ingrédients 2");
//            e.printStackTrace();
//        }
//
//        // Exemple 3 : tous les ingrédients de catégorie OTHER, page 2 avec 5 éléments par page
//        try {
//            List<Ingredient> ingredients3 = dr.findIngredientsByCriteria(null, CategoryEnum.OTHER, null, 2, 5);
//            System.out.println("=== Résultat 3 ===");
//            ingredients3.forEach(System.out::println);
//        } catch (RuntimeException e) {
//            System.err.println("Échec de la recherche d'ingrédients 3");
//            e.printStackTrace();
//        }
//
//        // Exemple 4 : tous les ingrédients, sans filtre, page 1, 20 éléments
//        try {
//            List<Ingredient> ingredients4 = dr.findIngredientsByCriteria(null, null, null, 1, 20);
//            System.out.println("=== Résultat 4 ===");
//            ingredients4.forEach(System.out::println);
//        } catch (RuntimeException e) {
//            System.err.println("Échec de la recherche d'ingrédients 4");
//            e.printStackTrace();
//        }



        Ingredient laitue = new Ingredient(1, "Laitue", 100.0, CategoryEnum.VEGETABLE, null);
        Ingredient tomate = new Ingredient(2, "Tomate", 75.0, CategoryEnum.VEGETABLE, null);

        // Pour Poulet Grillé (Coût 4500): Poulet (4500) * 1 = 4500
        Ingredient poulet = new Ingredient(3, "Poulet", 4500.0, CategoryEnum.ANIMAL, null);

        // 2. Création du Plat : Salade Fraîche
        Dish salade = new Dish(1, "Salade fraîche", DishTypeEnum.STARTER);
        salade.setPrice(3500.0); // Prix selon l'image 1
        salade.addDishIngredient(new DishIngredient(null, salade, laitue, 1.0, UnitType.PCS));
        salade.addDishIngredient(new DishIngredient(null, salade, tomate, 2.0, UnitType.PCS));

        // 3. Création du Plat : Poulet Grillé
        Dish pouletGrille = new Dish(2, "Poulet grillé", DishTypeEnum.MAIN);
        pouletGrille.setPrice(12000.0); // Prix selon l'image 1
        pouletGrille.addDishIngredient(new DishIngredient(null, pouletGrille, poulet, 1.0, UnitType.KG));

        // 4. Création du Plat : Riz aux légumes (Prix NULL)
        Dish riz = new Dish(3, "Riz aux légumes", DishTypeEnum.MAIN);
        riz.setPrice(null); // Doit lever une exception pour la marge

        // --- SAUVEGARDE ET TESTS ---

        System.out.println("=== TEST DES RÉSULTATS ATTENDUS ===\n");

        testPlat(salade);
        testPlat(pouletGrille);
        testPlat(riz);
    }

    private static void testPlat(Dish dish) {
        System.out.println("Plat : " + dish.getName());
        System.out.println("  > Coût calculé : " + dish.getDishCost()); // Attendu: 250.0 pour salade

        try {
            System.out.println("  > Marge brute  : " + dish.getGrossMargin()); // Attendu: 3250.0 pour salade
        } catch (IllegalStateException e) {
            System.out.println("  > Marge brute  : EXCEPTION (Prix NULL)");
        }
        System.out.println("-----------------------------------");
    }



    }






























































































