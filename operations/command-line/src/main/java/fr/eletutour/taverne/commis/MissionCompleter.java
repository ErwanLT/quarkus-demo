package fr.eletutour.taverne.commis;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import picocli.CommandLine;
import picocli.CommandLine.Model.OptionSpec;

import java.util.List;
import java.util.Map;

/**
 * Complète les noms de missions et leurs options à partir du modèle Picocli
 * de la commande racine, pour ne jamais avoir à maintenir une liste de
 * complétion séparée de la définition réelle des commandes.
 *
 * <p>Premier mot de la ligne : nom de mission. Mots suivants : options de la
 * mission déjà identifiée en premier mot.</p>
 */
public class MissionCompleter implements Completer {

    private static final List<String> MOTS_CLES = List.of("help", "quitter", "exit", "bonne-nuit");

    private final CommandLine commandeRacine;

    public MissionCompleter(CommandLine commandeRacine) {
        this.commandeRacine = commandeRacine;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        String motCourant = line.word();

        if (line.wordIndex() == 0) {
            completerPremierMot(motCourant, candidates);
            return;
        }

        completerOptionsDeLaMission(line.words().get(0), motCourant, candidates);
    }

    private void completerPremierMot(String prefixe, List<Candidate> candidates) {
        for (String nomMission : commandeRacine.getSubcommands().keySet()) {
            if (nomMission.startsWith(prefixe)) {
                candidates.add(new Candidate(nomMission));
            }
        }
        for (String motCle : MOTS_CLES) {
            if (motCle.startsWith(prefixe)) {
                candidates.add(new Candidate(motCle));
            }
        }
    }

    private void completerOptionsDeLaMission(String nomMission, String prefixe, List<Candidate> candidates) {
        Map<String, CommandLine> missions = commandeRacine.getSubcommands();
        CommandLine mission = missions.get(nomMission);
        if (mission == null) {
            return;
        }

        for (OptionSpec option : mission.getCommandSpec().options()) {
            for (String nomOption : option.names()) {
                if (nomOption.startsWith(prefixe)) {
                    candidates.add(new Candidate(nomOption));
                }
            }
        }
    }
}
