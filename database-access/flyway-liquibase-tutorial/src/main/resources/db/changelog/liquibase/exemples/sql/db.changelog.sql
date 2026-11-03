--liquibase formatted sql

--changeset taverne:1-creation-du-grand-livre
CREATE SEQUENCE ligne_de_compte_SEQ START WITH 1 INCREMENT BY 50;

CREATE TABLE ligne_de_compte (
    id BIGINT NOT NULL PRIMARY KEY,
    aventurier VARCHAR(255) NOT NULL,
    libelle VARCHAR(255) NOT NULL,
    montant_pieces_or INTEGER NOT NULL,
    enregistre_le TIMESTAMP WITH TIME ZONE NOT NULL
);

--changeset taverne:2-ajout-de-la-devise
ALTER TABLE ligne_de_compte ADD COLUMN devise VARCHAR(8);
UPDATE ligne_de_compte SET devise = 'PIECE_OR' WHERE devise IS NULL;
ALTER TABLE ligne_de_compte ALTER COLUMN devise SET NOT NULL;
