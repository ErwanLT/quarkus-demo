package fr.eletutour.tavern.federation.aventurier.model;

import io.smallrye.graphql.api.federation.FieldSet;
import io.smallrye.graphql.api.federation.Key;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.Id;

/**
 * Type "propriétaire" de l'entité fédérée : c'est ce service qui définit
 * Aventurier, les autres subgraphs (comme quete-service) l'étendent via
 * @Extends en ne connaissant que sa clé (id).
 */
@Key(fields = @FieldSet("id"))
@Description("Un aventurier de la taverne")
public class Aventurier {

    @Id
    private Long id;

    private String nom;

    private String classe;

    private Integer niveau;

    private Integer soldeOr;

    public Aventurier() {
    }

    public Aventurier(Long id, String nom, String classe, Integer niveau, Integer soldeOr) {
        this.id = id;
        this.nom = nom;
        this.classe = classe;
        this.niveau = niveau;
        this.soldeOr = soldeOr;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public Integer getNiveau() {
        return niveau;
    }

    public void setNiveau(Integer niveau) {
        this.niveau = niveau;
    }

    public Integer getSoldeOr() {
        return soldeOr;
    }

    public void setSoldeOr(Integer soldeOr) {
        this.soldeOr = soldeOr;
    }
}
