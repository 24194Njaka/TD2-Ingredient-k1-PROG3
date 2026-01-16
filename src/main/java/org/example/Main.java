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
            System.out.println("Aucun plat trouvé avec cet ID.");
            return;
        }

        System.out.println("Plat : " + dish.getName());
        System.out.println("Type : " + dish.getDishType());
        System.out.println("Ingrédients :");

        if (dish.getIngredients().isEmpty()) {
            System.out.println("  Aucun ingrédient.");
        } else {
            dish.getIngredients().forEach(ingredient ->
                    System.out.println("  - " + ingredient.getName()
                            + " | Prix : " + ingredient.getPrice()
                            + " | Catégorie : " + ingredient.getCategory()
                            + " | Plat : " + ingredient.getDishName())
            );
        }

        System.out.println(" Prix total du plat : " + dish.getDishPrice());


        DataRetriever dr = new DataRetriever();

        int page = 1;
        int size = 3;

        List<Ingredient> ingredients = dr.findIngredients(page, size);

        for (Ingredient i : ingredients) {
            System.out.println("Ingrédient : " + i.getName()
                    + " | Prix : " + i.getPrice()
                    + " | Catégorie : " + i.getCategory()
                    + " | Plat : " + (i.getDish() != null ? i.getDishName() : "Aucun"));
        }
        Dish dish1 = dr.findDishById(1);

        List<Ingredient> newIngredients = List.of(
                new Ingredient(0, "Oignon", 500.0, CategoryEnum.VEGETABLE, dish1),
                new Ingredient(0, "Ail", 300.0, CategoryEnum.VEGETABLE, dish1)
        );

        try {
            List<Ingredient> created = dr.createIngredients(newIngredients);
            System.out.println("Ingrédients créés avec succès :");
            created.forEach(i -> System.out.println(i.getName() + " | ID=" + i.getId()));
        } catch (RuntimeException e) {
            System.out.println("Opération annulée : " + e.getMessage());
        }


        // Modifier le nom et ajouter un nouvel ingrédient
        dish.setName("Salade deluxe");
        dish.getIngredients().add(
                new Ingredient(0, "Avocat", 1200.0, CategoryEnum.VEGETABLE, dish)
        );

// Supprimer un ingrédient existant
        dish.getIngredients().removeIf(ing -> ing.getName().equalsIgnoreCase("Tomate"));

        try {
            Dish saved = dr.saveDish(dish);
            System.out.println("Plat sauvegardé : " + saved.getName());
            System.out.println("Ingrédients actuels :");
            saved.getIngredients().forEach(ing ->
                    System.out.println(" - " + ing.getName() + " | ID=" + ing.getId()));
        } catch (RuntimeException e) {
            System.out.println("Erreur : " + e.getMessage());
        }


        String search = "Tomate";
        List<Dish> dishes = dr.findDishesByIngredientName(search);

        System.out.println("Plats contenant un ingrédient avec '" + search + "':");
        for (Dish d : dishes) {
            System.out.println("Plat : " + d.getName() + " | Type : " + d.getDishType());
            System.out.println("Ingrédients :");
            for (Ingredient i : d.getIngredients()) {
                System.out.println(" - " + i.getName() + " | Prix : " + i.getPrice() + " | Catégorie : " + i.getCategory());
            }
        }



        // recherche paginée d'ingrédients avec critères
        List<Ingredient> filteredIngredients = dr.findIngredientsByCriteria(
                "Tomate",               // nom ingrédient (filtre)
                CategoryEnum.VEGETABLE, // catégorie (filtre)
                "Salade",               // nom plat (filtre)
                1,                      // page
                5                       // taille
        );

        System.out.println("Résultats de la recherche paginée :");
        for (Ingredient i : filteredIngredients) {
            System.out.println("Ingrédient : " + i.getName() +
                    " | Prix : " + i.getPrice() +
                    " | Catégorie : " + i.getCategory() +
                    " | Plat : " + i.getDishName());
        }

    }


    }


















