ALTER TABLE ligne_de_compte ADD COLUMN devise VARCHAR(8);

UPDATE ligne_de_compte SET devise = 'PIECE_OR' WHERE devise IS NULL;

ALTER TABLE ligne_de_compte ALTER COLUMN devise SET NOT NULL;
