-- Ingrédients
INSERT INTO Ingredient (id, name, unit, cost) VALUES (1, 'Queue de Phénix', 'unité', 150.0);
INSERT INTO Ingredient (id, name, unit, cost) VALUES (2, 'Malt de Nain', 'kg', 5.0);
INSERT INTO Ingredient (id, name, unit, cost) VALUES (3, 'Eau de source elfique', 'litre', 2.5);
INSERT INTO Ingredient (id, name, unit, cost) VALUES (4, 'Basilic séché', 'gramme', 0.5);

-- Ajustement de la séquence par défaut de PanacheEntity
-- Selon la config, c'est souvent 'hibernate_sequence' ou 'Ingredient_SEQ' si spécifié
-- Ici on s'aligne sur ce que génère PanacheEntity par défaut
ALTER SEQUENCE Ingredient_SEQ RESTART WITH 5;

-- Recettes
INSERT INTO Recipe (id, title, description) VALUES (1, 'Hydromel de l''Elfe', 'Une boisson rafraîchissante et légèrement magique.');
INSERT INTO Recipe (id, title, description) VALUES (2, 'Ragoût de Basilic', 'Un plat consistant pour les guerriers fatigués.');

-- Liaison Recettes / Ingrédients
INSERT INTO Recipe_Ingredient (Recipe_id, ingredients_id) VALUES (1, 2);
INSERT INTO Recipe_Ingredient (Recipe_id, ingredients_id) VALUES (1, 3);
INSERT INTO Recipe_Ingredient (Recipe_id, ingredients_id) VALUES (2, 4);
