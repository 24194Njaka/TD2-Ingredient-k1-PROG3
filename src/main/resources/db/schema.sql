

CREATE TYPE ingredient_category_enum AS ENUM (
    'VEGETABLE',
    'ANIMAL',
    'MARINE',
    'DAIRY',
    'OTHER'
);

CREATE TYPE dish_type_enum AS ENUM (
    'STARTER',
    'MAIN',
    'DESSERT'
);

CREATE TYPE unit_type AS ENUM (
    'PCS',
    'KG',
    'L'
)
CREATE TABLE Dish (
                      id SERIAL PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      dish_type dish_type_enum NOT NULL
);

CREATE TABLE Ingredient (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            price NUMERIC(10,2) NOT NULL CHECK (price >= 0),
                            category ingredient_category_enum NOT NULL,
                            id_dish INT,
                            CONSTRAINT fk_dish
                                FOREIGN KEY (id_dish)
                                    REFERENCES Dish(id)
                                    ON DELETE SET NULL
);


CREATE TABLE DishIngredient (
    id SERIAL PRIMARY KEY ,
    id_dish INT not null ,
    CONSTRAINT fk_dish
        FOREIGN KEY (id_dish)
            REFERENCES Dish(id)
            ON DELETE SET NULL,
    id_ingredient INT not null ,
        FOREIGN KEY (id_ingredient)
             REFERENCES Ingredient(id)
             ON DELETE set null,
    quantity_required numeric,
    unit unit_type
)
