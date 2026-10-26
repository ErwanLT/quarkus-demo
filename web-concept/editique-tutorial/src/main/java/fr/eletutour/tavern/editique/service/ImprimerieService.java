package fr.eletutour.tavern.editique.service;

import fr.eletutour.tavern.editique.model.Commande;
import fr.eletutour.tavern.editique.model.FactureFournisseur;
import fr.eletutour.tavern.editique.model.ReleveFournisseur;
import jakarta.enterprise.context.ApplicationScoped;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.pdf.JRPdfExporter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * L'imprimerie de la taverne : transforme les registres en parchemins (PDF) et en tableurs (XLSX).
 * <p>
 * Les rapports {@code .jrxml} de {@code src/main/jasperreports} sont compilés en {@code .jasper}
 * au build par l'extension Quarkus, puis déposés sur le classpath dans {@code jasperreports/}.
 * Ils ne sont donc chargés qu'une seule fois, ici, à la demande.
 * </p>
 */
@ApplicationScoped
public class ImprimerieService {

    public static final String NOM_TAVERNE = "The Falling Whale";
    public static final String ADRESSE_TAVERNE = "Quai des Baleiniers, Port-Brumeux";

    /** Dossier du classpath où l'extension dépose les rapports compilés. */
    private static final String DOSSIER_COMPILE = "jasperreports/";

    /**
     * Logo de la taverne dédié à l'impression (src/main/resources) : recadré sur l'écusson
     * et réduit à 260 px, il pèse une trentaine de Ko au lieu des 700 Ko du logo d'origine.
     */
    private static final String LOGO = "logo-impression.jpg";

    private final Map<String, JasperReport> rapports = new ConcurrentHashMap<>();
    private final Map<String, byte[]> images = new ConcurrentHashMap<>();

    /**
     * Imprime le ticket de caisse (rouleau thermique 80 mm) d'une commande.
     */
    public byte[] imprimerTicket(Commande commande) {
        Map<String, Object> parametres = parametresTaverne();
        parametres.put("NUMERO", commande.getNumero());
        parametres.put("TABLE", commande.getTable());
        parametres.put("SERVEUR", commande.getServeur());
        parametres.put("DATE_COMMANDE", commande.getDate());
        parametres.put("PAIEMENTS", commande.getPaiements());
        JasperPrint print = remplir("ticket-de-caisse", parametres,
                new JRBeanCollectionDataSource(commande.getLignes()));
        return exporterPdf(print);
    }

    /**
     * Imprime la facture d'un fournisseur au format PDF (maître + sous-rapports).
     */
    public byte[] imprimerFacture(FactureFournisseur facture) {
        return exporterPdf(remplirFacture(facture));
    }

    /**
     * Exporte la facture d'un fournisseur sous forme de tableur XLSX.
     */
    public byte[] exporterFactureXlsx(FactureFournisseur facture) {
        JasperPrint print = remplirFacture(facture);
        try (ByteArrayOutputStream sortie = new ByteArrayOutputStream()) {
            JRXlsxExporter exporteur = new JRXlsxExporter();
            exporteur.setExporterInput(new SimpleExporterInput(print));
            exporteur.setExporterOutput(new SimpleOutputStreamExporterOutput(sortie));
            exporteur.exportReport();
            return sortie.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Le tableur de la facture " + facture.getNumero()
                    + " n'a pu être exporté", e);
        }
    }

    /**
     * Imprime le relevé des fournisseurs d'une période : leurs factures et le total dû.
     */
    public byte[] imprimerReleve(List<ReleveFournisseur> releves, String periode) {
        Map<String, Object> parametres = parametresTaverne();
        parametres.put("PERIODE", periode);
        return exporterPdf(remplir("releve-fournisseurs", parametres, new JRBeanCollectionDataSource(releves)));
    }

    /**
     * Le sous-rapport des lignes est passé déjà compilé ; celui de la TVA est résolu
     * par le rapport maître lui-même, via son chemin sur le classpath.
     */
    private JasperPrint remplirFacture(FactureFournisseur facture) {
        Map<String, Object> parametres = parametresTaverne();
        parametres.put("SOUS_RAPPORT_LIGNES", charger("facture-lignes"));
        return remplir("facture-fournisseur", parametres,
                new JRBeanCollectionDataSource(List.of(facture)));
    }

    private Map<String, Object> parametresTaverne() {
        Map<String, Object> parametres = new HashMap<>();
        parametres.put(JRParameter.REPORT_LOCALE, Locale.FRANCE);
        parametres.put("NOM_TAVERNE", NOM_TAVERNE);
        parametres.put("ADRESSE_TAVERNE", ADRESSE_TAVERNE);
        // Les rapports A4 reçoivent le logo déjà chargé ; le ticket, lui, le résout par son chemin
        parametres.put("LOGO", image(LOGO));
        return parametres;
    }

    private JasperPrint remplir(String rapport, Map<String, Object> parametres,
                                JRBeanCollectionDataSource source) {
        try {
            return JasperFillManager.fillReport(charger(rapport), parametres, source);
        } catch (JRException e) {
            throw new IllegalStateException("Le rapport « " + rapport + " » n'a pu être rempli", e);
        }
    }

    private byte[] exporterPdf(JasperPrint print) {
        try (ByteArrayOutputStream sortie = new ByteArrayOutputStream()) {
            JRPdfExporter exporteur = new JRPdfExporter();
            exporteur.setExporterInput(new SimpleExporterInput(print));
            exporteur.setExporterOutput(new SimpleOutputStreamExporterOutput(sortie));
            exporteur.exportReport();
            return sortie.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Le parchemin PDF n'a pu être scellé", e);
        }
    }

    /**
     * Charge (et met en cache) un rapport compilé par l'extension au build.
     */
    private JasperReport charger(String nom) {
        return rapports.computeIfAbsent(nom, n -> {
            String emplacement = DOSSIER_COMPILE + n + ".jasper";
            try (InputStream flux = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(emplacement)) {
                if (flux == null) {
                    throw new IllegalStateException("Rapport compilé introuvable : " + emplacement);
                }
                return (JasperReport) JRLoader.loadObject(flux);
            } catch (JRException | IOException e) {
                throw new IllegalStateException("Rapport compilé introuvable : " + emplacement, e);
            }
        });
    }

    /**
     * Charge (et met en cache) une image du classpath, transmise aux rapports sous forme d'octets.
     */
    private byte[] image(String nom) {
        return images.computeIfAbsent(nom, n -> {
            try (InputStream flux = Thread.currentThread().getContextClassLoader().getResourceAsStream(n)) {
                if (flux == null) {
                    throw new IllegalStateException("Image introuvable : " + n);
                }
                return flux.readAllBytes();
            } catch (IOException e) {
                throw new IllegalStateException("Impossible de charger l'image : " + n, e);
            }
        });
    }
}
