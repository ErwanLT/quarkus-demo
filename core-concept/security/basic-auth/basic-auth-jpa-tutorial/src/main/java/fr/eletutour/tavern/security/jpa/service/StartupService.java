package fr.eletutour.tavern.security.jpa.service;

import fr.eletutour.tavern.security.jpa.model.TavernUser;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

import java.util.Set;

@ApplicationScoped
public class StartupService {

    @Transactional
    void onStart(@Observes StartupEvent event) {
        if (TavernUser.count() > 0) {
            return;
        }

        TavernUser keeper = new TavernUser();
        keeper.username = "keeper";
        keeper.password = BcryptUtil.bcryptHash("keeper123");
        keeper.roles = Set.of("keeper");
        keeper.persist();

        TavernUser supplier = new TavernUser();
        supplier.username = "supplier";
        supplier.password = BcryptUtil.bcryptHash("supplier123");
        supplier.roles = Set.of("supplier");
        supplier.persist();
    }
}
