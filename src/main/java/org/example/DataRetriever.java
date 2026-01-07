package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    public Dish findDishById(Integer id) {

        String dishSql = """
        SELECT d.id, d.name, d.dish_type,
               i.id AS ingredient_id, i.name AS ingredient_name,
               i.price, i.category
        FROM dish d
        LEFT JOIN dish_ingredient di ON d.id = di.dish_id
        LEFT JOIN ingredient i ON di.ingredient_id = i.id
        WHERE d.id = ?
    """;

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(dishSql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            Dish dish = null;
            List<Ingredient> ingredients = new ArrayList<>();

            while (rs.next()) {
                if (dish == null) {
                    dish = new Dish(
                            rs.getInt("id"),
                            rs.getString("name"),
                            DishTypeEnum.valueOf(rs.getString("dish_type")),
                            ingredients
                    );
                }

                if (rs.getInt("ingredient_id") != 0) {
                    ingredients.add(new Ingredient(
                            rs.getInt("ingredient_id"),
                            rs.getString("ingredient_name"),
                            rs.getDouble("price"),
                            CategoryEnum.valueOf(rs.getString("category")),
                            dish
                    ));
                }
            }

            if (dish == null) {
                throw new RuntimeException("Dish not found");
            }

            return dish;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ingredient> findIngredients(int page, int size) {

        String sql = """
        SELECT * FROM ingredient
        ORDER BY id
        LIMIT ? OFFSET ?
    """;

        int offset = (page - 1) * size;
        List<Ingredient> result = new ArrayList<>();

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, offset);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result.add(new Ingredient(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        CategoryEnum.valueOf(rs.getString("category")),
                        null
                ));
            }

            return result;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {

        String checkSql = "SELECT id FROM ingredient WHERE name = ?";
        String insertSql = """
        INSERT INTO ingredient(name, price, category)
        VALUES (?, ?, ?::ingredient_category_enum)
    """;

        Connection conn = null;

        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false); // désactive autocommit pour la transaction

            // Vérification des doublons dans la DB
            for (Ingredient ing : newIngredients) {
                try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                    check.setString(1, ing.getName());
                    if (check.executeQuery().next()) {
                        throw new RuntimeException("Ingredient already exists: " + ing.getName());
                    }
                }
            }

            for (Ingredient ing : newIngredients) {
                try (PreparedStatement insert = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    insert.setString(1, ing.getName());
                    insert.setDouble(2, ing.getPrice());
                    insert.setString(3, ing.getCategory().name());
                    insert.executeUpdate();

                    try (ResultSet keys = insert.getGeneratedKeys()) {
                        if (keys.next()) {
                            ing.setId(keys.getInt(1));
                        }
                    }
                }
            }

            conn.commit();
            return newIngredients;

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Transaction annulée: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn);
        }
    }

    public Dish saveDish(Dish dishToSave) {

        String insertDishSql = """
        INSERT INTO dish(name, dish_type)
        VALUES (?, ?::dish_type_enum)
        RETURNING id
    """;

        String updateDishSql = """
        UPDATE dish
        SET name = ?, dish_type = ?::dish_type_enum
        WHERE id = ?
    """;

        String deleteIngredientsLinkSql =
                "DELETE FROM dish_ingredient WHERE dish_id = ?";

        String insertIngredientsLinkSql =
                "INSERT INTO dish_ingredient(dish_id, ingredient_id) VALUES (?, ?)";

        Connection conn = null;

        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false);

            int dishId = dishToSave.getId();

            // ===== INSERT =====
            if (dishId == 0) {
                try (PreparedStatement ps = conn.prepareStatement(insertDishSql)) {
                    ps.setString(1, dishToSave.getName());
                    ps.setString(2, dishToSave.getDishType().name());

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            dishId = rs.getInt("id");
                            dishToSave.setId(dishId);
                        }
                    }
                }
            }
            // ===== UPDATE =====
            else {
                try (PreparedStatement ps = conn.prepareStatement(updateDishSql)) {
                    ps.setString(1, dishToSave.getName());
                    ps.setString(2, dishToSave.getDishType().name());
                    ps.setInt(3, dishId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(deleteIngredientsLinkSql)) {
                    ps.setInt(1, dishId);
                    ps.executeUpdate();
                }
            }

            // ===== LINK INGREDIENTS =====
            if (dishToSave.getIngredients() != null && !dishToSave.getIngredients().isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(insertIngredientsLinkSql)) {
                    for (Ingredient ing : dishToSave.getIngredients()) {
                        ps.setInt(1, dishId);
                        ps.setInt(2, ing.getId());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            conn.commit();
            return dishToSave;

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            throw new RuntimeException("Transaction annulée: " + e.getMessage(), e);
        } finally {
            DBConnection.close(conn);
        }
    }



    public List<Dish> findDishsByIngredientName(String ingredientName) {
        if (ingredientName == null || ingredientName.isEmpty()) {
            throw new IllegalArgumentException("ingredientName ne peut pas être vide");
        }

        String sql = """
        SELECT DISTINCT d.id, d.name, d.dish_type
        FROM dish d
        JOIN dish_ingredient di ON d.id = di.dish_id
        JOIN ingredient i ON di.ingredient_id = i.id
        WHERE i.name ILIKE ?
    """;

        List<Dish> dishes = new ArrayList<>();

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + ingredientName + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    DishTypeEnum type = DishTypeEnum.valueOf(rs.getString("dish_type"));

                    // Ici on ne charge pas encore les ingrédients pour simplifier
                    dishes.add(new Dish(id, name, type, new ArrayList<>()));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des plats : " + e.getMessage(), e);
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
        SELECT i.id, i.name, i.price, i.category, i.id_dish
        FROM ingredient i
        LEFT JOIN dish d ON i.id_dish = d.id
        WHERE 1=1
    """);

        List<Object> params = new ArrayList<>();

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
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    CategoryEnum cat = CategoryEnum.valueOf(rs.getString("category"));
                    Integer dishId = rs.getInt("id_dish");

                    ingredients.add(new Ingredient(id, name, price, cat, new Dish(dishId, null, null, null)));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ingrédients : " + e.getMessage(), e);
        }

        return ingredients;
    }




}
