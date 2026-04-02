package fr.eletutour.tavern.security.jwt.jpa.service;

import fr.eletutour.tavern.security.jwt.jpa.model.TavernUser;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import io.quarkus.runtime.StartupEvent;
import jakarta.transaction.Transactional;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class StartupService {

    @Transactional
    void init(@Observes StartupEvent event) {
        if (TavernUser.count() > 0) {
            return;
        }

        TavernUser accountant = new TavernUser();
        accountant.username = "elora";
        accountant.password = BcryptUtil.bcryptHash("ledger123");
        accountant.role = "accountant";
        accountant.persist();

        TavernUser treasurer = new TavernUser();
        treasurer.username = "borin";
        treasurer.password = BcryptUtil.bcryptHash("vault123");
        treasurer.role = "treasurer";
        treasurer.persist();
    }
}
