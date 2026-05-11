package fr.eletutour.tavern.vaadin.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.vertx.http.runtime.security.FormAuthenticationMechanism;

@ApplicationScoped
public class SecurityService {

    @Inject
    SecurityIdentity securityIdentity;

    public SecurityIdentity getAuthenticatedUser() {
        return securityIdentity;
    }

    public void logout() {

        FormAuthenticationMechanism.logout(securityIdentity);

        var request = VaadinServletRequest.getCurrent()
                .getHttpServletRequest();

        request.getSession().invalidate();
        VaadinSession.getCurrent().close();

        UI.getCurrent().getPage().setLocation("/login");
    }
}
