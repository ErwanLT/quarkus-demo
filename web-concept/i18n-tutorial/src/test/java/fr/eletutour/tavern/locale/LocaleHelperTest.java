package fr.eletutour.tavern.locale;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocaleHelperTest {

    private final LocaleHelper localeHelper = new LocaleHelper();

    @Test
    void shouldUsePluralForFrenchWhenCountIsZero() {
        assertEquals("aventuriers", localeHelper.plurielAventurier(0, Locale.FRENCH));
    }

    @Test
    void shouldUsePluralForEnglishWhenCountIsZero() {
        assertEquals("adventurers", localeHelper.plurielAventurier(0, Locale.ENGLISH));
    }

    @Test
    void shouldUseSingularWhenCountIsOne() {
        assertEquals("aventurier", localeHelper.plurielAventurier(1, Locale.FRENCH));
        assertEquals("adventurer", localeHelper.plurielAventurier(1, Locale.ENGLISH));
    }

    @Test
    void shouldFormatLatinCurrencyWithDnSuffix() {
        String latinPrice = localeHelper.formatMonnaieAvecLatin(3.5, Locale.forLanguageTag("la-LA"));
        assertEquals("3,50 dn.", latinPrice);
    }
}
