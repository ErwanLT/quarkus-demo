package fr.eletutour.tavern.flammes.corbeaux;

/**
 * Etat de la voliere, l'equivalent local d'un consumer lag Kafka.
 *
 * @param corbeauxLaches  corbeaux envoyes depuis le demarrage
 * @param corbeauxTraites corbeaux dont le pli a ete lu jusqu'au bout
 * @param corbeauxEnVol   corbeaux encore dans les buffers : c'est ce qu'il faut vidanger a l'arret
 */
public record EtatVoliere(long corbeauxLaches, long corbeauxTraites, long corbeauxEnVol) {
}
