package fr.eletutour.tavern.editique.model;

import java.math.BigDecimal;

/**
 * Un règlement de l'addition : un client peut payer en plusieurs fois et de plusieurs façons
 * (pièces d'or, troc, ardoise...).
 */
public class Paiement {

    private final String mode;
    private final BigDecimal montant;

    public Paiement(String mode, BigDecimal montant) {
        this.mode = mode;
        this.montant = montant;
    }

    public String getMode() {
        return mode;
    }

    public BigDecimal getMontant() {
        return montant;
    }
}
