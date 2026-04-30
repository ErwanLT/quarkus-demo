package fr.eletutour.tavern.model;

import java.util.List;

public class Aventurier {

    private Long id;
    private String nom;
    private String classe;
    private Integer niveau;
    private List<Quete> quetes;

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

    public List<Quete> getQuetes() {
        return quetes;
    }

    public void setQuetes(List<Quete> quetes) {
        this.quetes = quetes;
    }
}