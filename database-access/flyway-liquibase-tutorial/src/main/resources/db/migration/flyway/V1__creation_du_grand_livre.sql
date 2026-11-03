CREATE SEQUENCE ligne_de_compte_SEQ START WITH 1 INCREMENT BY 50;

CREATE TABLE ligne_de_compte (
    id BIGINT NOT NULL PRIMARY KEY,
    aventurier VARCHAR(255) NOT NULL,
    libelle VARCHAR(255) NOT NULL,
    montant_pieces_or INTEGER NOT NULL,
    enregistre_le TIMESTAMP WITH TIME ZONE NOT NULL
);
