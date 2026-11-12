package fr.eletutour.tavern.barde.repertoire;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.List;

/**
 * Le répertoire du barde, lu depuis {@code ballades/repertoire.json}.
 *
 * <p>Ce record n'est jamais renvoyé par un endpoint REST : Quarkus ne le voit donc pas
 * passer et ne l'enregistre pas automatiquement pour la réflexion. Seul
 * {@code ObjectMapper.readValue(...)} l'utilise. Sans {@link RegisterForReflection},
 * le binaire natif échoue avec « Cannot construct instance of Repertoire ».</p>
 */
@RegisterForReflection
public record Repertoire(String barde, List<String> ballades) {
}
