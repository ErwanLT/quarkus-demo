package fr.eletutour.tavern.vaadin.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SecurityService {

    @Inject
    SecurityIdentity securityIdentity;

    public SecurityIdentity getAuthenticatedUser() {
        return securityIdentity;
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation("/");
        VaadinServletRequest.getCurrent().getHttpServletRequest().getSession().invalidate();
    }
}
