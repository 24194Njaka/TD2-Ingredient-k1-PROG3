package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class DataRetriever {
    public Dish findDishById(Integer id) {

        String sql = """
        SELECT
            d.id AS dish_id,
            d.name AS dish_name,
            d.dish_type,
            d.price AS dish_price,

            i.id AS ingredient_id,
            i.name AS ingredient_name,
            i.price AS ingredient_price,
            i.category AS ingredient_category
        FROM Dish d
        LEFT JOIN Ingredient i ON d.id = i.id_dish
        WHERE d.id = ?
    """;

        Dish dish = null;

        try (Connection connection = DBConnection.getDBConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    // Création du Dish une seule fois
                    if (dish == null) {
                        BigDecimal priceBD = rs.getBigDecimal("dish_price");
                        Double price = (priceBD != null) ? priceBD.doubleValue() : null;

                        dish = new Dish(
                                rs.getInt("dish_id"),
                                rs.getString("dish_name"),
                                DishTypeEnum.valueOf(rs.getString("dish_type")),
                                price
                        );
                    }

                    // Création des ingrédients (si existants)
                    int ingredientId = rs.getInt("ingredient_id");

                    if (!rs.wasNull()) {
                        Ingredient ingredient = new Ingredient(
                                ingredientId,
                                rs.getString("ingredient_name"),
                                rs.getDouble("ingredient_price"),
                                CategoryEnum.valueOf(rs.getString("ingredient_category")),
                                dish
                        );

                        dish.addIngredient(ingredient);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du plat avec id = " + id, e);
        }

        return dish;
    }






    public List<Ingredient> findIngredients(int page, int size) {

        String sql = """
            SELECT i.id AS ingredient_id,
                   i.name AS ingredient_name,
                   i.price,
                   i.category,
                   d.id AS dish_id,
                   d.name AS dish_name,
                   d.dish_type
            FROM Ingredient i
            LEFT JOIN Dish d ON i.id_dish = d.id
            ORDER BY i.id
            LIMIT ? OFFSET ?
        """;

        List<Ingredient> ingredients = new ArrayList<>();

        int offset = (page - 1) * size;

        try (Connection connection = DBConnection.getDBConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Dish dish = null;
                int dishId = rs.getInt("dish_id");
                if (!rs.wasNull()) {
                    dish = new Dish(
                            dishId,
                            rs.getString("dish_name"),
                            DishTypeEnum.valueOf(rs.getString("dish_type"))
                    );
                }

                Ingredient ingredient = new Ingredient(
                        rs.getInt("ingredient_id"),
                        rs.getString("ingredient_name"),
                        rs.getDouble("price"),
                        CategoryEnum.valueOf(rs.getString("category")),
                        dish
                );

                ingredients.add(ingredient);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ingrédients", e);
        }

        return ingredients;
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {

        //Vérifier doublons dans la liste fournie
        for (int i = 0; i < newIngredients.size(); i++) {
            Ingredient ing1 = newIngredients.get(i);
            for (int j = i + 1; j < newIngredients.size(); j++) {
                Ingredient ing2 = newIngredients.get(j);
                if (ing1.getName().equalsIgnoreCase(ing2.getName())) {
                    throw new RuntimeException(
                            "Doublon détecté dans la nouvelle liste : " + ing1.getName());
                }
            }
        }
        String sql = """
            INSERT INTO Ingredient(name, price, category, id_dish)
            VALUES (?, ?, ?::ingredient_category_enum, ?)
            RETURNING id
        """;

        List<Ingredient> createdIngredients = new ArrayList<>();

        try (Connection connection = DBConnection.getDBConnection()) {

            // ⚡ Commencer la transaction
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sql)) {

                for (Ingredient i : newIngredients) {

                    System.out.println(" Tentative d'insertion : "
                            + i.getName()
                            + " | prix=" + i.getPrice()
                            + " | catégorie=" + i.getCategory()
                            + " | plat=" + (i.getDish() != null ? i.getDish().getId() : "NULL"));

                    ps.setString(1, i.getName());
                    ps.setDouble(2, i.getPrice());
                    ps.setString(3, i.getCategory().name()); // String → cast en ENUM via SQL
                    ps.setObject(4, i.getDish() != null ? i.getDish().getId() : null);

                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        i.setId(rs.getInt("id"));
                        createdIngredients.add(i);
                        System.out.println("Ingrédient inséré avec id=" + i.getId());
                    }
                }

                connection.commit();
                System.out.println("Tous les ingrédients ont été insérés avec succès.");

            } catch (SQLException e) {
                connection.rollback();
                System.out.println(" Erreur lors de l'insertion, rollback effectué.");
                e.printStackTrace();
                throw new RuntimeException("Erreur lors de l'insertion des ingrédients, opération annulée", e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur de connexion à la base", e);
        }

        return createdIngredients;
    }

    public Dish saveDish(Dish dishToSave) {

        String insertDishSQL = """
            INSERT INTO Dish(name, dish_type)
            VALUES (?, ?::dish_type_enum)
            RETURNING id
        """;

        String updateDishSQL = """
            UPDATE Dish
            SET name = ?, dish_type = ?::dish_type_enum
            WHERE id = ?
        """;

        String deleteIngredientSQL = """
            DELETE FROM Ingredient
            WHERE id = ?
        """;

        String insertIngredientSQL = """
            INSERT INTO Ingredient(name, price, category, id_dish)
            VALUES (?, ?, ?::ingredient_category_enum, ?)
            RETURNING id
        """;

        try (Connection connection = DBConnection.getDBConnection()) {
            connection.setAutoCommit(false); // transaction

            try {
                //  Vérifier si le plat existe
                Dish existingDish = findDishById(dishToSave.getId());

                if (existingDish == null) {
                    //  Insérer nouveau plat
                    try (PreparedStatement ps = connection.prepareStatement(insertDishSQL)) {
                        ps.setString(1, dishToSave.getName());
                        ps.setString(2, dishToSave.getDishType().name());

                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            dishToSave.setId(rs.getInt("id"));
                        }
                    }
                } else {
                    //  Mettre à jour plat existant
                    try (PreparedStatement ps = connection.prepareStatement(updateDishSQL)) {
                        ps.setString(1, dishToSave.getName());
                        ps.setString(2, dishToSave.getDishType().name());
                        ps.setInt(3, dishToSave.getId());
                        ps.executeUpdate();
                    }

                    //  Supprimer les ingrédients dissociés
                    List<Ingredient> toRemove = new ArrayList<>();
                    for (Ingredient oldIng : existingDish.getIngredients()) {
                        boolean stillExists = false;
                        for (Ingredient newIng : dishToSave.getIngredients()) {
                            if (oldIng.getId() == newIng.getId()) {
                                stillExists = true;
                                break;
                            }
                        }
                        if (!stillExists) {
                            toRemove.add(oldIng);
                        }
                    }

                    try (PreparedStatement psDelete = connection.prepareStatement(deleteIngredientSQL)) {
                        for (Ingredient ing : toRemove) {
                            psDelete.setInt(1, ing.getId());
                            psDelete.executeUpdate();
                        }
                    }
                }

                //Ajouter ou mettre à jour les ingrédients
                try (PreparedStatement psInsert = connection.prepareStatement(insertIngredientSQL)) {
                    for (Ingredient ing : dishToSave.getIngredients()) {
                        // Si nouvel ingrédient (id == 0) -> insérer
                        if (ing.getId() == 0) {
                            psInsert.setString(1, ing.getName());
                            psInsert.setDouble(2, ing.getPrice());
                            psInsert.setString(3, ing.getCategory().name());
                            psInsert.setInt(4, dishToSave.getId());

                            ResultSet rs = psInsert.executeQuery();
                            if (rs.next()) {
                                ing.setId(rs.getInt("id"));
                            }
                        }
                    }
                }

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                throw new RuntimeException("Erreur lors de la sauvegarde du plat, rollback effectué", e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur de connexion à la base", e);
        }

        return dishToSave;
    }

    public List<Dish> findDishesByIngredientName(String ingredientName) {
        List<Dish> dishes = new ArrayList<>();

        String sql = """
            SELECT d.id AS dish_id, d.name AS dish_name, d.dish_type,
                   i.id AS ing_id, i.name AS ing_name, i.price, i.category
            FROM Dish d
            JOIN Ingredient i ON i.id_dish = d.id
            WHERE i.name ILIKE ?
            ORDER BY d.id
        """;

        try (Connection connection = DBConnection.getDBConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            // Paramètre avec wildcards pour rechercher "contient"
            ps.setString(1, "%" + ingredientName + "%");

            ResultSet rs = ps.executeQuery();

            Dish currentDish = null;
            int currentDishId = -1;

            while (rs.next()) {
                int dishId = rs.getInt("dish_id");

                // Créer un nouveau plat si on change d'id
                if (currentDish == null || dishId != currentDishId) {
                    currentDishId = dishId;
                    String dishName = rs.getString("dish_name");
                    DishTypeEnum dishType = DishTypeEnum.valueOf(rs.getString("dish_type"));

                    currentDish = new Dish(dishId, dishName, dishType);
                    dishes.add(currentDish);
                }

                // Ajouter l'ingrédient au plat courant
                int ingId = rs.getInt("ing_id");
                String ingName = rs.getString("ing_name");
                double price = rs.getDouble("price");
                CategoryEnum category = CategoryEnum.valueOf(rs.getString("category"));

                Ingredient ing = new Ingredient(ingId, ingName, price, category, currentDish);
                currentDish.getIngredients().add(ing);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la recherche des plats par ingrédient", e);
        }

        return dishes;
    }


    public List<Ingredient> findIngredientsByCriteria(
            String ingredientName,
            CategoryEnum category,
            String dishName,
            int page,
            int size
    ) {
        List<Ingredient> ingredients = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT i.id AS ing_id, i.name AS ing_name, i.price, i.category,
                   d.id AS dish_id, d.name AS dish_name, d.dish_type
            FROM Ingredient i
            JOIN Dish d ON i.id_dish = d.id
            WHERE 1=1
        """);

        // Liste des paramètres à passer à PreparedStatement
        List<Object> params = new ArrayList<>();

        // Ajouter filtre sur nom d'ingrédient si fourni
        if (ingredientName != null && !ingredientName.isEmpty()) {
            sql.append(" AND i.name ILIKE ? ");
            params.add("%" + ingredientName + "%");
        }

        //Ajouter filtre sur catégorie si fourni
        if (category != null) {
            sql.append(" AND i.category = ?::ingredient_category_enum ");
            params.add(category.name());
        }

        // Ajouter filtre sur nom de plat si fourni
        if (dishName != null && !dishName.isEmpty()) {
            sql.append(" AND d.name ILIKE ? ");
            params.add("%" + dishName + "%");
        }

        //Pagination
        sql.append(" ORDER BY i.id ");
        sql.append(" LIMIT ? OFFSET ? ");
        params.add(size);
        params.add((page - 1) * size);

        try (Connection connection = DBConnection.getDBConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            // Passer les paramètres dynamiques
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                } else {
                    ps.setObject(i + 1, param);
                }
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Construire le plat
                int dishId = rs.getInt("dish_id");
                String dName = rs.getString("dish_name");
                DishTypeEnum dType = DishTypeEnum.valueOf(rs.getString("dish_type"));
                Dish dish = new Dish(dishId, dName, dType);

                // Construire l'ingrédient
                int ingId = rs.getInt("ing_id");
                String ingName = rs.getString("ing_name");
                double price = rs.getDouble("price");
                CategoryEnum cat = CategoryEnum.valueOf(rs.getString("category"));

                Ingredient ing = new Ingredient(ingId, ingName, price, cat, dish);
                dish.getIngredients().add(ing);

                ingredients.add(ing);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la recherche des ingrédients par critères", e);
        }

        return ingredients;
    }
}

