package org.example;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.math.BigDecimal;
import java.util.Set;

public class DataRetriever {

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
                            CategoryEnum.valueOf(rs.getString("ingredient_category"))
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

    public Dish saveDish(Dish dish) {
        // 1. Correction du Cast (?::dish_type_enum) et du nom de colonne (selling_price)
        String insertDishSql = "INSERT INTO dish (name, dish_type, selling_price) VALUES (?, ?::dish_type_enum, ?) RETURNING id";
        String updateDishSql = "UPDATE dish SET name = ?, dish_type = ?::dish_type_enum, selling_price = ? WHERE id = ?";
        String insertDishIngredientSql = "INSERT INTO dishingredient (id_dish, id_ingredient, quantity_required, unit) VALUES (?, ?, ?, ?::unit_type)";

        try (Connection conn = DBConnection.getDBConnection()) {
            conn.setAutoCommit(false);
            try {
                if (dish.getId() == null) {
                    try (PreparedStatement ps = conn.prepareStatement(insertDishSql)) {
                        ps.setString(1, dish.getName());
                        ps.setString(2, dish.getDishType().name());
                        if (dish.getPrice() != null) ps.setBigDecimal(3, BigDecimal.valueOf(dish.getPrice()));
                        else ps.setNull(3, Types.DECIMAL);

                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) dish.setId(rs.getInt(1));
                    }
                } else {
                    try (PreparedStatement ps = conn.prepareStatement(updateDishSql)) {
                        ps.setString(1, dish.getName());
                        ps.setString(2, dish.getDishType().name());
                        if (dish.getPrice() != null) ps.setBigDecimal(3, BigDecimal.valueOf(dish.getPrice()));
                        else ps.setNull(3, Types.DECIMAL);
                        ps.setInt(4, dish.getId());
                        ps.executeUpdate();
                    }
                }

                // 2. Nettoyage et insertion de la composition
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM dishingredient WHERE id_dish = ?")) {
                    ps.setInt(1, dish.getId());
                    ps.executeUpdate();
                }

                if (!dish.getIngredients().isEmpty()) {
                    try (PreparedStatement ps = conn.prepareStatement(insertDishIngredientSql)) {
                        for (DishIngredient di : dish.getIngredients()) {
                            ps.setInt(1, dish.getId());
                            ps.setInt(2, di.getIngredient().getId());
                            ps.setDouble(3, di.getQuantityRequired());
                            ps.setString(4, di.getUnit().name());
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }

                conn.commit();
                return dish;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la sauvegarde du plat : " + dish.getName(), e);
        }
    }

    public Ingredient saveIngredient(Ingredient ingredient) {
        // 1. Sauvegarde/Mise à jour de l'ingrédient (code déjà fait précédemment)

        // 2. Sauvegarde des mouvements de stock
        String sqlMvt = "INSERT INTO stock_movement (id, id_ingredient, quantity, type, unit, creation_datetime) " +
                "VALUES (?, ?, ?, ?::mouvement_type, ?::unit_type, ?) " +
                "ON CONFLICT (id) DO NOTHING";

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sqlMvt)) {

            for (StockMovement sm : ingredient.getStockMovementList()) {
                ps.setInt(1, sm.getId());
                ps.setInt(2, ingredient.getId());
                ps.setDouble(3, sm.getValue().getQuantity());
                ps.setString(4, sm.getType().name());
                ps.setString(5, sm.getValue().getUnit().name());
                ps.setTimestamp(6, Timestamp.from(sm.getCreationDatetime()));
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur sauvegarde mouvements", e);
        }
        return ingredient;
    }

    public Order saveOrder(Order orderToSave) {
        // 1. Vérification des stocks avant toute action (Logique métier)
        for (DishOrder doObj : orderToSave.getDishOrders()) {
            for (DishIngredient di : doObj.getDish().getIngredients()) {
                double totalNeeded = doObj.getQuantity() * di.getQuantityRequired();
                double currentStock = di.getIngredient().getStockValueAt(Instant.now()).getQuantity();

                if (currentStock < totalNeeded) {
                    // Conformément au sujet : levée d'exception avec le nom de l'ingrédient
                    throw new RuntimeException("Stock insuffisant pour l'ingrédient : " +
                            di.getIngredient().getName());
                }
            }
        }

        // 2. Sauvegarde en base de données
        String insertOrderSql = "INSERT INTO \"order\" (reference, creation_datetime) VALUES (?, ?) RETURNING id";
        String insertDishOrderSql = "INSERT INTO dish_order (id_order, id_dish, quantity) VALUES (?, ?, ?)";

        // Déclaration de la connexion à l'extérieur pour qu'elle soit accessible au bloc catch
        Connection conn = null;
        try {
            conn = DBConnection.getDBConnection();
            conn.setAutoCommit(false); // Début de la transaction

            // Insertion de la Commande (Order)
            try (PreparedStatement ps = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, orderToSave.getReference());
                ps.setTimestamp(2, Timestamp.from(orderToSave.getCreationDatetime() != null ?
                        orderToSave.getCreationDatetime() : Instant.now()));
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderToSave.setId(rs.getInt(1));
                    }
                }
            }

            // Insertion des lignes de commande (DishOrder)
            try (PreparedStatement ps = conn.prepareStatement(insertDishOrderSql)) {
                for (DishOrder doObj : orderToSave.getDishOrders()) {
                    ps.setInt(1, orderToSave.getId());
                    ps.setInt(2, doObj.getDish().getId());
                    ps.setInt(3, doObj.getQuantity());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit(); // Validation de la transaction
            return orderToSave;

        } catch (SQLException e) {
            // Gestion du Rollback : On vérifie que la connexion existe avant d'annuler
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'insertion de la commande", e);
        } finally {
            // Fermeture manuelle indispensable puisque nous n'utilisons plus le try-with-resources ici
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Dish findDishById(Integer id) {
        String sql = """
        SELECT d.id, d.name, d.selling_price, d.dish_type,
               i.id AS ing_id, i.name AS ing_name,
               i.price AS ing_price, i.category,
               di.quantity_required, di.unit
        FROM dish d
        LEFT JOIN dishingredient di ON d.id = di.id_dish
        LEFT JOIN ingredient i ON di.id_ingredient = i.id
        WHERE d.id = ?
    """;

        try (Connection conn = DBConnection.getDBConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            Dish dish = null;

            while (rs.next()) {
                if (dish == null) {
                    dish = new Dish(
                            rs.getInt("id"),
                            rs.getString("name"),
                            DishTypeEnum.valueOf(rs.getString("dish_type"))
                    );
                    BigDecimal sellingPriceBD = rs.getBigDecimal("selling_price");
                    if (sellingPriceBD != null) dish.setPrice(sellingPriceBD.doubleValue());
                }

                Integer ingId = rs.getObject("ing_id", Integer.class);
                if (ingId != null) {
                    Ingredient ing = new Ingredient(
                            ingId,
                            rs.getString("ing_name"),
                            rs.getDouble("ing_price"),
                            CategoryEnum.valueOf(rs.getString("category"))
                    );

                    DishIngredient di = new DishIngredient(
                            null, // ID auto-généré ou non nécessaire ici
                            dish,
                            ing,
                            rs.getDouble("quantity_required"),
                            UnitType.valueOf(rs.getString("unit"))
                    );
                    dish.addDishIngredient(di);
                }
            }
            return dish;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du plat ID: " + id, e);
        }
    }
















//    public List<Dish> findDishesByIngredientName(String ingredientName) {
//        String sql = """
//        SELECT d.id AS dish_id, d.name AS dish_name, d.dish_type, d.price,
//               i.id AS ingredient_id, i.name AS ingredient_name, i.price AS ingredient_price, i.category
//        FROM dish d
//        LEFT JOIN ingredient i ON i.id_dish = d.id
//        WHERE i.name ILIKE ?
//        ORDER BY d.id
//    """;
//
//        List<Dish> dishes = new ArrayList<>();
//        try (Connection conn = DBConnection.getDBConnection();
//             PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            // Paramètre pour le LIKE (recherche insensible à la casse)
//            ps.setString(1, "%" + ingredientName + "%");
//
//            try (ResultSet rs = ps.executeQuery()) {
//                Dish currentDish = null;
//                while (rs.next()) {
//                    int dishId = rs.getInt("dish_id");
//
//                    if (currentDish == null || !currentDish.getId().equals(dishId)) {
//                        // Nouveau plat
//                        currentDish = new Dish(
//                                dishId,
//                                rs.getString("dish_name"),
//                                DishTypeEnum.valueOf(rs.getString("dish_type")),
//                                rs.getDouble("price")
//                        );
//                        dishes.add(currentDish);
//                    }
//
//                    // Ajouter l'ingrédient si présent
//                    int ingredientId = rs.getInt("ingredient_id");
//                    if (!rs.wasNull()) {
//                        Ingredient ingredient = new Ingredient(
//                                ingredientId,
//                                rs.getString("ingredient_name"),
//                                rs.getDouble("ingredient_price"),
//                                CategoryEnum.valueOf(rs.getString("category")),
//                                currentDish
//                        );
//                        currentDish.addIngredient(ingredient);
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Erreur lors de la récupération des plats par ingrédient", e);
//        }
//
//        return dishes;
//    }
//
//    public List<Ingredient> findIngredientsByCriteria(
//            String ingredientName,
//            CategoryEnum category,
//            String dishName,
//            int page,
//            int size
//    ) {
//        List<Ingredient> result = new ArrayList<>();
//
//        StringBuilder sql = new StringBuilder("""
//            SELECT i.id, i.name, i.price, i.category, i.id_dish, d.name as dish_name
//            FROM ingredient i
//            LEFT JOIN dish d ON i.id_dish = d.id
//            WHERE 1=1
//        """);
//
//        List<Object> params = new ArrayList<>();
//
//        // Filtrage dynamique
//        if (ingredientName != null && !ingredientName.isEmpty()) {
//            sql.append(" AND i.name ILIKE ?");
//            params.add("%" + ingredientName + "%");
//        }
//
//        if (category != null) {
//            sql.append(" AND i.category = ?::ingredient_category_enum");
//            params.add(category.name());
//        }
//
//        if (dishName != null && !dishName.isEmpty()) {
//            sql.append(" AND d.name ILIKE ?");
//            params.add("%" + dishName + "%");
//        }
//
//        // Pagination
//        sql.append(" ORDER BY i.id ASC LIMIT ? OFFSET ?");
//        params.add(size);
//        params.add((page - 1) * size);
//
//        try (Connection conn = DBConnection.getDBConnection();
//             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
//
//            // Remplissage des paramètres
//            for (int i = 0; i < params.size(); i++) {
//                Object param = params.get(i);
//                if (param instanceof String) {
//                    ps.setString(i + 1, (String) param);
//                } else if (param instanceof Integer) {
//                    ps.setInt(i + 1, (Integer) param);
//                } else {
//                    ps.setObject(i + 1, param);
//                }
//            }
//
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    Integer id = rs.getInt("id");
//                    String name = rs.getString("name");
//                    Double price = rs.getBigDecimal("price") != null
//                            ? rs.getBigDecimal("price").doubleValue() : null;
//                    CategoryEnum cat = CategoryEnum.valueOf(rs.getString("category"));
//                    Integer dishId = rs.getObject("id_dish", Integer.class);
//                    String dishNameFromDb = rs.getString("dish_name");
//
//                    Dish dish = dishId != null ? new Dish(dishId, dishNameFromDb, null) : null;
//
//                    Ingredient ingredient = new Ingredient(id, name, price, cat, dish);
//                    result.add(ingredient);
//                }
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException("Erreur lors de la récupération des ingrédients", e);
//        }
//
//        return result;
//    }
}

