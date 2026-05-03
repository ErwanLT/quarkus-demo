-- Ingrédients
INSERT INTO Ingredient (id, name, unit, cost) VALUES (1, 'Queue de Phénix', 'unité', 150.0);
INSERT INTO Ingredient (id, name, unit, cost) VALUES (2, 'Malt de Nain', 'kg', 5.0);
INSERT INTO Ingredient (id, name, unit, cost) VALUES (3, 'Eau de source elfique', 'litre', 2.5);
INSERT INTO Ingredient (id, name, unit, cost) VALUES (4, 'Basilic séché', 'gramme', 0.5);

-- Ajustement de la séquence
ALTER SEQUENCE Ingredient_SEQ RESTART WITH 5;

-- Recettes
INSERT INTO Recipe (id, title, description) VALUES (1, 'Hydromel de l''Elfe', 'Une boisson rafraîchissante et légèrement magique.');
INSERT INTO Recipe (id, title, description) VALUES (2, 'Ragoût de Basilic', 'Un plat consistant pour les guerriers fatigués.');

-- Liaison Recettes / Ingrédients (utilisation de la JoinTable explicite)
INSERT INTO RECIPE_INGREDIENT (recipe_id, ingredient_id) VALUES (1, 2);
INSERT INTO RECIPE_INGREDIENT (recipe_id, ingredient_id) VALUES (1, 3);
INSERT INTO RECIPE_INGREDIENT (recipe_id, ingredient_id) VALUES (2, 4);
