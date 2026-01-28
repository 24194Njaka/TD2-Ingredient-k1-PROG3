insert into Dish(id,name,dish_type) values (1,'Salade frâche','STARTER'),
                                           (2,'Poulet grillé','MAIN'),
                                           (3,'Riz aux légumes','MAIN'),
                                           (4,'Gâteau au chocolat','DESSERT'),
                                           (5,'Salade de fruits','DESSERT');
insert into Ingredient (id,name,price,category,id_dish) values (1,'Laitue',800.00,'VEGETABLE',1),
                                                               (2,'Tomate',600.00,'VEGETABLE',1),
                                                               (3,'Poulet',4500.00,'ANIMAL',2),
                                                               (4,'Chocolat',3000.00,'OTHER',4),
                                                               (5,'Beurre',2500.00,'DAIRY',4);
    alter table dish add column if not exist price numeric(10,2);
UPDATE dish SET price = 2000 WHERE name = 'Salade fraîche';
UPDATE dish SET price = 6000 WHERE name = 'Poulet grillé';
UPDATE dish SET price = NULL WHERE name IN ('Riz au légume', 'Gâteau au chocolat', 'Salade de fruit');


insert into dishingredient (id,id_dish,id_ingredient,quantity_required,unit) values (1,1,1,0.20,'KG'),
                                                                                    (2,1,2,0.15,'KG'),
                                                                                    (3,2,3,1.00,'KG'),
                                                                                    (4,4,4,0.30,'KG'),
                                                                                    (5,4,5,0.20,'KG');
UPDATE dish SET name = 'Salade fraîche', dish_type = 'START', price = 3500.00 WHERE id = 1;
UPDATE dish SET price = 12000.00 WHERE id = 2;
UPDATE dish SET price = NULL WHERE id = 3;
UPDATE dish SET name = 'Gâteau au chocolat', dish_type = 'DESSERT', price = 8000.00 WHERE id = 4;
UPDATE dish SET name = 'Salade de fruits', dish_type = 'DESSERT', price = NULL WHERE id = 5;
DELETE FROM dish WHERE id > 5;