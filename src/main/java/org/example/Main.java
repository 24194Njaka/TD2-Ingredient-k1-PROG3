package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();
        Instant now = Instant.now();
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



//        Ingredient laitue = new Ingredient(1, "Laitue", 100.0, CategoryEnum.VEGETABLE, null);
//        Ingredient tomate = new Ingredient(2, "Tomate", 75.0, CategoryEnum.VEGETABLE, null);
//
//        // Pour Poulet Grillé (Coût 4500): Poulet (4500) * 1 = 4500
//        Ingredient poulet = new Ingredient(3, "Poulet", 4500.0, CategoryEnum.ANIMAL, null);
//
//        // 2. Création du Plat : Salade Fraîche
//        Dish salade = new Dish(1, "Salade fraîche", DishTypeEnum.STARTER);
//        salade.setPrice(3500.0); // Prix selon l'image 1
//        salade.addDishIngredient(new DishIngredient(null, salade, laitue, 1.0, UnitType.PCS));
//        salade.addDishIngredient(new DishIngredient(null, salade, tomate, 2.0, UnitType.PCS));
//
//        // 3. Création du Plat : Poulet Grillé
//        Dish pouletGrille = new Dish(2, "Poulet grillé", DishTypeEnum.MAIN);
//        pouletGrille.setPrice(12000.0); // Prix selon l'image 1
//        pouletGrille.addDishIngredient(new DishIngredient(null, pouletGrille, poulet, 1.0, UnitType.KG));
//
//        // 4. Création du Plat : Riz aux légumes (Prix NULL)
//        Dish riz = new Dish(3, "Riz aux légumes", DishTypeEnum.MAIN);
//        riz.setPrice(null); // Doit lever une exception pour la marge
//
//        // --- SAUVEGARDE ET TESTS ---
//
//        System.out.println("=== TEST DES RÉSULTATS ATTENDUS ===\n");
//
//        testPlat(salade);
//        testPlat(pouletGrille);
//        testPlat(riz);
//    }
//
//    private static void testPlat(Dish dish) {
//        System.out.println("Plat : " + dish.getName());
//        System.out.println("  > Coût calculé : " + dish.getDishCost()); // Attendu: 250.0 pour salade
//
//        try {
//            System.out.println("  > Marge brute  : " + dish.getGrossMargin()); // Attendu: 3250.0 pour salade
//        } catch (IllegalStateException e) {
//            System.out.println("  > Marge brute  : EXCEPTION (Prix NULL)");
//        }
//        System.out.println("-----------------------------------");





//        Ingredient laitue = new Ingredient(1, "Laitue", 800.0, CategoryEnum.VEGETABLE);
//
//        // 2. Création des mouvements de stock (selon le tableau du sujet)
//        // Mouvement 1 : Entrée de 5.0 KG le 2024-01-05
//        StockMovement mvtIn = new StockMovement();
//        mvtIn.setId(1);
//        mvtIn.setType(MovementTypeEnum.IN);
//        mvtIn.setValue(new StockValue(5.0, UnitType.KG));
//        mvtIn.setCreationDatetime(LocalDateTime.of(2024, 1, 5, 8, 0)
//                .toInstant(ZoneOffset.UTC));
//
//        // Mouvement 2 : Sortie de 0.2 KG le 2024-01-06 à 12h00
//        StockMovement mvtOut = new StockMovement();
//        mvtOut.setId(6); // ID 6 selon le tableau de test
//        mvtOut.setType(MovementTypeEnum.OUT);
//        mvtOut.setValue(new StockValue(0.2, UnitType.KG));
//        mvtOut.setCreationDatetime(LocalDateTime.of(2024, 1, 6, 12, 0)
//                .toInstant(ZoneOffset.UTC));
//
//        // 3. Ajout des mouvements à l'ingrédient
//        laitue.addStockMovement(mvtIn);
//        laitue.addStockMovement(mvtOut);
//        dr.saveIngredient(laitue);
//
//        // 5. TEST DU CALCUL (Instant t = 2024-01-06 12:00)
//        Instant tTest = LocalDateTime.of(2024, 1, 6, 12, 0).toInstant(ZoneOffset.UTC);
//        StockValue stockActuel = laitue.getStockValueAt(tTest);
//
//        System.out.println("=== TEST GESTION DE STOCKS ===");
//        System.out.println("Ingrédient : " + laitue.getName());
//        System.out.println("Date du test : 2024-01-06 12:00");
//        System.out.println("Stock calculé : " + stockActuel.getQuantity() + " " + stockActuel.getUnit());
//
//        // Vérification du résultat attendu
//        if (stockActuel.getQuantity() == 4.8) {
//            System.out.println(" SUCCÈS : Le stock correspond au calcul (5.0 - 0.2 = 4.8)");
//        } else {
//            System.out.println(" ERREUR : Valeur attendue 4.8, obtenue " + stockActuel.getQuantity());
//        }

        // --- 1. CONFIGURATION DES INGRÉDIENTS ET STOCKS ---
//        // Laitue : Prix 800, Stock Initial 5.0 [cite: 56]
//        Ingredient laitue = new Ingredient(1, "Laitue", 800.0, CategoryEnum.VEGETABLE);
//        StockMovement mvtLaitue = new StockMovement();
//        mvtLaitue.setId(101);
//        mvtLaitue.setType(MovementTypeEnum.IN);
//        mvtLaitue.setValue(new StockValue(5.0, UnitType.KG));
//        mvtLaitue.setCreationDatetime(Instant.now());
//        laitue.addStockMovement(mvtLaitue);
//
//        // Tomate : Prix 500, Stock Initial 4.0 [cite: 56]
//        Ingredient tomate = new Ingredient(2, "Tomate", 500.0, CategoryEnum.VEGETABLE);
//        StockMovement mvtTomate = new StockMovement();
//        mvtTomate.setId(102);
//        mvtTomate.setType(MovementTypeEnum.IN);
//        mvtTomate.setValue(new StockValue(4.0, UnitType.KG));
//        mvtTomate.setCreationDatetime(Instant.now());
//        tomate.addStockMovement(mvtTomate);
//
//        // --- 2. CRÉATION DU PLAT ---
//        // Salade fraîche : Prix de vente 3500
//        Dish salade = new Dish(1, "Salade fraîche", DishTypeEnum.STARTER);
//        salade.setPrice(3500.0);
//
//        // Composition : 1kg Laitue + 0.5kg Tomate
//        salade.addDishIngredient(new DishIngredient(null, salade, laitue, 1.0, UnitType.KG));
//        salade.addDishIngredient(new DishIngredient(null, salade, tomate, 0.5, UnitType.KG));
//
//        // --- 3. CRÉATION D'UNE COMMANDE ---
//        Order maCommande = new Order();
//        maCommande.setReference("ORD00002");
//        maCommande.setCreationDatetime(Instant.now());
//
//        DishOrder ligne1 = new DishOrder(null, salade, 3);
//        maCommande.addDishOrder(ligne1);
//
//        // --- 4. EXÉCUTION DU TEST ---
//        System.out.println("=== TEST DE SAUVEGARDE DE COMMANDE ===");
//        System.out.println("Référence : " + maCommande.getReference());
//        System.out.println("Montant HT : " + maCommande.getTotalAmountWithoutVAT() + " Ar");
//        System.out.println("Montant TTC (20%) : " + maCommande.getTotalAmountWithVAT() + " Ar");
//
//        try {
//            // Tentative de sauvegarde avec vérification de stock intégrée
//            dr.saveOrder(maCommande);
//            System.out.println("Commande enregistrée avec succès !");
//        } catch (RuntimeException e) {
//            System.err.println("ÉCHEC : " + e.getMessage());
//        }
//
//        // --- 5. TEST DE RUPTURE DE STOCK ---
//        System.out.println("\n=== TEST DE RUPTURE DE STOCK ===");
//        Order commandeTropGrosse = new Order();
//        commandeTropGrosse.setReference("ORD00003");
//        commandeTropGrosse.setCreationDatetime(Instant.now());
//
//        // On commande 10 Salades (Besoin 10kg Laitue > Stock 5.0kg)
//        commandeTropGrosse.addDishOrder(new DishOrder(null, salade, 10));
//
//        try {
//            dr.saveOrder(commandeTropGrosse);
//        } catch (RuntimeException e) {
//            System.out.println("Test réussi, exception levée : " + e.getMessage());
//        }



//        // 1. INITIALISATION DES DONNÉES
//        Ingredient laitue = new Ingredient(1, "Laitue", 800.0, CategoryEnum.VEGETABLE);
//        laitue.addStockMovement(new StockMovement(1, new StockValue(5.0, UnitType.KG), MovementTypeEnum.IN, Instant.now()));
//        Ingredient tomate = new Ingredient(2, "Tomate", 500.0, CategoryEnum.VEGETABLE);
//        tomate.addStockMovement(new StockMovement(2, new StockValue(4.0, UnitType.KG), MovementTypeEnum.IN, Instant.now()));
//        Ingredient chocolat = new Ingredient(3, "Chocolat", 1200.0, CategoryEnum.VEGETABLE);
//
//
//        chocolat.addStockMovement(new StockMovement(3, new StockValue(3.0, UnitType.KG), MovementTypeEnum.IN, Instant.now()));
//        laitue.addStockMovement(new StockMovement(4, new StockValue(2.0, UnitType.PCS), MovementTypeEnum.OUT, Instant.now()));
//        tomate.addStockMovement(new StockMovement(5, new StockValue(5.0, UnitType.PCS), MovementTypeEnum.OUT, Instant.now()));
//        chocolat.addStockMovement(new StockMovement(6, new StockValue(1.0, UnitType.L), MovementTypeEnum.OUT, Instant.now()));
//        printResult("Laitue", laitue.getStockValueAt(Instant.now()), 4.0);
//        printResult("Tomate", tomate.getStockValueAt(Instant.now()), 3.5);
//        printResult("Chocolat", chocolat.getStockValueAt(Instant.now()), 2.6);
//    }
//
//    private static void printResult(String name, StockValue result, double expected) {
//        System.out.printf("Ingrédient: %s | Obtenu: %.1f KG | Attendu: %.1f KG | %s%n",
//                name, result.getQuantity(), expected,
//                (Math.abs(result.getQuantity() - expected) < 0.01 ? " OK" : "ERREUR"));


        List<RestaurantTable> allTables = new ArrayList<>();

        RestaurantTable t1 = new RestaurantTable(1, 1); // ID 1, Numéro 1
        RestaurantTable t2 = new RestaurantTable(2, 2); // ID 2, Numéro 2
        RestaurantTable t3 = new RestaurantTable(3, 3); // ID 3, Numéro 3

        allTables.add(t1);
        allTables.add(t2);
        allTables.add(t3);

        // --- 2. SIMULATION D'UNE TABLE DÉJÀ OCCUPÉE ---
        // On simule que la Table 1 est occupée actuellement
        TableOrder occupationExistante = new TableOrder(t1, now.minusSeconds(3600), null);
        t1.getOrders().add(occupationExistante);

        // --- 3. CONFIGURATION D'UN PLAT ET STOCK (Rappel Annexe 2) ---
        Ingredient laitue = new Ingredient(1, "Laitue", 800.0, CategoryEnum.VEGETABLE);
        laitue.addStockMovement(new StockMovement(1, new StockValue(5.0, UnitType.KG), MovementTypeEnum.IN, now));

        Dish salade = new Dish(1, "Salade", DishTypeEnum.STARTER);
        salade.addDishIngredient(new DishIngredient(null, salade, laitue, 1.0, UnitType.KG));

        // --- 4. CRÉATION D'UNE COMMANDE DE TEST ---
        Order maCommande = new Order();
        maCommande.setReference("ORD-TEST-TABLE");
        maCommande.setCreationDatetime(now);
        maCommande.addDishOrder(new DishOrder(null, salade, 1));

        // --- 5. EXÉCUTION DES TESTS ---

        System.out.println("=== TEST 1 : TENTATIVE SUR TABLE OCCUPÉE (Table 1) ===");
        try {
            // On essaie de réserver la Table 1 qui est déjà prise
            dr.saveOrder(maCommande, t1, allTables);
        } catch (RuntimeException e) {
            // Doit afficher : "La table n'est pas fournie. Tables libres : [2, 3]"
            System.out.println("Résultat attendu reçu : " + e.getMessage());
        }

        System.out.println("\n=== TEST 2 : TENTATIVE SUR TABLE LIBRE (Table 2) ===");
        try {
            dr.saveOrder(maCommande, t2, allTables);
            System.out.println(" Succès : Commande enregistrée sur la Table 2 !");
        } catch (RuntimeException e) {
            System.err.println("Erreur imprévue : " + e.getMessage());
        }

        // --- 6. TEST DE RECHERCHE (findOrderByReference) ---
        System.out.println("\n=== TEST 3 : RECHERCHE DE COMMANDE ===");
        Order found = dr.findOrderByReference("ORD-TEST-TABLE");
        if (found != null) {
            System.out.println("Commande trouvée en base : " + found.getReference());
        } else {
            System.out.println(" Commande non trouvée.");
        }
    }






  }











































































































