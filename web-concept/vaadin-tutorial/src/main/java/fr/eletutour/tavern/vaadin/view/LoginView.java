package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.eletutour.tavern.vaadin.service.SecurityService;
import jakarta.inject.Inject;

@Route("login")
@PageTitle("Login | The Falling Whale")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();
    private final SecurityService securityService;

    @Inject
    public LoginView(SecurityService securityService) {
        this.securityService = securityService;
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");

        add(new H1("The Falling Whale"), login);

        getElement().getStyle().set("background", "var(--whale-parchment)");
        getElement().getStyle().set("background-image", "var(--parchment-texture)");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (!securityService.getAuthenticatedUser().isAnonymous()) {
            beforeEnterEvent.forwardTo(DashboardView.class);
        }

        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}
