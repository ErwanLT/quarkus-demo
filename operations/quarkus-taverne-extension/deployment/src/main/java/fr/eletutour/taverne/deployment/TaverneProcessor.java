package fr.eletutour.taverne.deployment;

import fr.eletutour.taverne.runtime.TaverneInterceptor;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.devui.spi.page.CardPageBuildItem;
import io.quarkus.devui.spi.page.Page;

/**
 * La forge elle-même. Chaque méthode annotée {@code @BuildStep} est un coup
 * de marteau, exécuté une seule fois, à la construction du royaume (le build),
 * jamais au runtime.
 */
class TaverneProcessor {

    private static final String FEATURE = "taverne";

    /**
     * Premier coup de marteau : déclarer que l'extension existe.
     * C'est ce BuildStep qui fait apparaître "taverne" dans la liste des
     * fonctionnalités au démarrage de l'application (et dans le rapport de build).
     */
    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    /**
     * Deuxième coup de marteau : forger la pièce qui manque.
     *
     * <p>L'annotation {@code @Taverne} est bien un {@code @InterceptorBinding},
     * découverte automatiquement par ArC. Mais {@code TaverneInterceptor} vit
     * dans le jar runtime de l'extension, hors du bean archive de l'application
     * consommatrice : sans intervention, personne ne saurait qu'il existe.
     *
     * <p>On le déclare donc explicitement comme bean additionnel : c'est la
     * pièce forgée (le {@code BuildItem}) que la forge remet au royaume.
     */
    @BuildStep
    AdditionalBeanBuildItem enregistrerLeSortilege() {
        return AdditionalBeanBuildItem.builder()
                .addBeanClass(TaverneInterceptor.class)
                .setUnremovable()
                .build();
    }

    @BuildStep(onlyIf = io.quarkus.deployment.IsLocalDevelopment.class)
    CardPageBuildItem carteDeLaTaverne() {

        CardPageBuildItem card = new CardPageBuildItem();

        card.addPage(Page.webComponentPageBuilder()
                .title("La carte de la Taverne")
                .icon("font-awesome-solid:map")
                .componentLink("qwc-taverne-map.js"));

        return card;
    }
}
