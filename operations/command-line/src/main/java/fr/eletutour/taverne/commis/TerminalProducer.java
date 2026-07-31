package fr.eletutour.taverne.commis;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

/**
 * Allume la lanterne du commis une bonne fois pour toutes, plutot que d'en
 * rallumer une nouvelle a chaque mission. Le terminal devient ainsi un bean
 * CDI ordinaire, injectable aussi bien dans la boucle principale que dans
 * n'importe quelle mission qui a besoin d'afficher un sablier ou une jauge.
 */
@ApplicationScoped
public class TerminalProducer {

    @Produces
    @ApplicationScoped
    public Terminal creerTerminal() throws IOException {
        return TerminalBuilder.builder().system(true).build();
    }

    public void fermerTerminal(@Disposes Terminal terminal) throws IOException {
        terminal.close();
    }
}
