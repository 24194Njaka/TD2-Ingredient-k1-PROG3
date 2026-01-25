package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

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

        if (page < 1) {
            page = 1;
        }

        String sql = """
        SELECT
            i.id AS ingredient_id,
            i.name AS ingredient_name,
            i.price AS ingredient_price,
            i.category AS ingredient_category,

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

            try (ResultSet rs = ps.executeQuery()) {

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
                            rs.getDouble("ingredient_price"),
                            CategoryEnum.valueOf(rs.getString("ingredient_category")),
                            dish
                    );

                    ingredients.add(ingredient);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération paginée des ingrédients", e);
        }

        return ingredients;
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {

        //  Vérification des doublons dans la liste fournie
        Set<String> names = new HashSet<>();
        for (Ingredient i : newIngredients) {
            if (!names.add(i.getName().toLowerCase())) {
                throw new RuntimeException(
                        "Doublon détecté dans la nouvelle liste : " + i.getName()
                );
            }
        }

        String sql = """
        INSERT INTO Ingredient(name, price, category, id_dish)
        VALUES (?, ?, ?::ingredient_category_enum, ?)
        RETURNING id
    """;

        List<Ingredient> createdIngredients = new ArrayList<>();

        try (Connection connection = DBConnection.getDBConnection()) {

            // Démarrer la transaction
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sql)) {

                for (Ingredient i : newIngredients) {

                    ps.setString(1, i.getName());
                    ps.setBigDecimal(2, BigDecimal.valueOf(i.getPrice()));
                    ps.setString(3, i.getCategory().name());

                    // Gestion du Dish potentiellement null
                    if (i.getDish() != null) {
                        ps.setInt(4, i.getDish().getId());
                    } else {
                        ps.setNull(4, Types.INTEGER);
                    }

                    // Exécuter et récupérer l'ID auto-généré
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            i.setId(rs.getInt("id"));
                            createdIngredients.add(i);
                        }
                    }
                }

                //  Tout est OK → commit
                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(
                        "Erreur lors de la création des ingrédients, opération annulée",
                        e
                );
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion à la base de données", e);
        }

        return createdIngredients;
    }

    public Dish saveDish(Dish toSave) {
        String upsertDishSql = """
            INSERT INTO dish (name, dish_type, price)
            VALUES (?, ?::dish_type_enum, ?)
            ON CONFLICT (id) DO UPDATE
            SET name = EXCLUDED.name,
                dish_type = EXCLUDED.dish_type,
                price = EXCLUDED.price
            RETURNING id
        """;

        try (Connection conn = DBConnection.getDBConnection()) {
            conn.setAutoCommit(false);

            //   update du plat
            Integer dishId;
            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                ps.setString(1, toSave.getName());
                ps.setString(2, toSave.getDishType().name());
                if (toSave.getPrice() != null) {
                    ps.setBigDecimal(3, BigDecimal.valueOf(toSave.getPrice()));
                } else {
                    ps.setNull(3, Types.NUMERIC);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        dishId = rs.getInt(1);
                        toSave.setId(dishId);
                    } else {
                        throw new RuntimeException("Impossible de récupérer l'ID du plat");
                    }
                }
            }

            // ====Gestion des ingrédients===
            List<Ingredient> ingredients = toSave.getIngredients();
            if (ingredients == null) ingredients = new ArrayList<>();

            for (Ingredient ing : ingredients) {
                String upsertIngredientSql = """
                    INSERT INTO ingredient (name, price, category, id_dish)
                    VALUES (?, ?, ?::ingredient_category_enum, ?)
                    ON CONFLICT (id) DO UPDATE
                    SET name = EXCLUDED.name,
                        price = EXCLUDED.price,
                        category = EXCLUDED.category,
                        id_dish = EXCLUDED.id_dish
                    RETURNING id
                """;

                try (PreparedStatement ps = conn.prepareStatement(upsertIngredientSql)) {
                    ps.setString(1, ing.getName());
                    if (ing.getPrice() != null) {
                        ps.setBigDecimal(2, BigDecimal.valueOf(ing.getPrice()));
                    } else {
                        ps.setNull(2, Types.NUMERIC);
                    }
                    if (ing.getCategory() != null) {
                        ps.setString(3, ing.getCategory().name());
                    } else {
                        ps.setNull(3, Types.VARCHAR);
                    }
                    ps.setInt(4, dishId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            ing.setId(rs.getInt(1));
                        } else {
                            throw new RuntimeException("Impossible de récupérer l'ID de l'ingrédient");
                        }
                    }
                }
            }

            conn.commit();
            return findDishById(dishId);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du plat", e);
        }
    }






    public List<Dish> findDishesByIngredientName(String ingredientName) {
        String sql = """
        SELECT d.id AS dish_id, d.name AS dish_name, d.dish_type, d.price,
               i.id AS ingredient_id, i.name AS ingredient_name, i.price AS ingredient_price, i.category
        FROM dish d
        LEFT JOIN ingredient i ON i.id_dish = d.id
        WHERE i.name ILIKE ?
        ORDER BY d.id
    """;

        List<Dish> dishes = new ArrayList<>();
        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Paramètre pour le LIKE (recherche insensible à la casse)
            ps.setString(1, "%" + ingredientName + "%");

            try (ResultSet rs = ps.executeQuery()) {
                Dish currentDish = null;
                while (rs.next()) {
                    int dishId = rs.getInt("dish_id");

                    if (currentDish == null || !currentDish.getId().equals(dishId)) {
                        // Nouveau plat
                        currentDish = new Dish(
                                dishId,
                                rs.getString("dish_name"),
                                DishTypeEnum.valueOf(rs.getString("dish_type")),
                                rs.getDouble("price")
                        );
                        dishes.add(currentDish);
                    }

                    // Ajouter l'ingrédient si présent
                    int ingredientId = rs.getInt("ingredient_id");
                    if (!rs.wasNull()) {
                        Ingredient ingredient = new Ingredient(
                                ingredientId,
                                rs.getString("ingredient_name"),
                                rs.getDouble("ingredient_price"),
                                CategoryEnum.valueOf(rs.getString("category")),
                                currentDish
                        );
                        currentDish.addIngredient(ingredient);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des plats par ingrédient", e);
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
        List<Ingredient> result = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT i.id, i.name, i.price, i.category, i.id_dish, d.name as dish_name
            FROM ingredient i
            LEFT JOIN dish d ON i.id_dish = d.id
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        // Filtrage dynamique
        if (ingredientName != null && !ingredientName.isEmpty()) {
            sql.append(" AND i.name ILIKE ?");
            params.add("%" + ingredientName + "%");
        }

        if (category != null) {
            sql.append(" AND i.category = ?::ingredient_category_enum");
            params.add(category.name());
        }

        if (dishName != null && !dishName.isEmpty()) {
            sql.append(" AND d.name ILIKE ?");
            params.add("%" + dishName + "%");
        }

        // Pagination
        sql.append(" ORDER BY i.id ASC LIMIT ? OFFSET ?");
        params.add(size);
        params.add((page - 1) * size);

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Remplissage des paramètres
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

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Integer id = rs.getInt("id");
                    String name = rs.getString("name");
                    Double price = rs.getBigDecimal("price") != null
                            ? rs.getBigDecimal("price").doubleValue() : null;
                    CategoryEnum cat = CategoryEnum.valueOf(rs.getString("category"));
                    Integer dishId = rs.getObject("id_dish", Integer.class);
                    String dishNameFromDb = rs.getString("dish_name");

                    Dish dish = dishId != null ? new Dish(dishId, dishNameFromDb, null) : null;

                    Ingredient ingredient = new Ingredient(id, name, price, cat, dish);
                    result.add(ingredient);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ingrédients", e);
        }

        return result;
    }
}

