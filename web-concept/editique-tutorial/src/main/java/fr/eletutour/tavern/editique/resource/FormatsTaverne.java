package fr.eletutour.tavern.editique.resource;

import io.quarkus.qute.TemplateExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Extensions Qute de la taverne : {@code {montant.po}} affiche une somme en pièces d'or,
 * avec le même format que les PDF JasperReports (Locale.FRANCE, motif {@code #,##0.00}).
 */
@TemplateExtension
public class FormatsTaverne {

    private FormatsTaverne() {
    }

    static String po(BigDecimal montant) {
        // DecimalFormat n'est pas thread-safe : une instance par appel.
        DecimalFormat format = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.FRANCE));
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format.format(montant) + " po";
    }
}
