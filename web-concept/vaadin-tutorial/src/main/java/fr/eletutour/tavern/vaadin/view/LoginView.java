package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.inject.Inject;

@Route("login")
@PageTitle("Login | The Falling Whale")
public class LoginView extends VerticalLayout {

    private final LoginForm login = new LoginForm();

    @Inject
    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");

        add(new H1("The Falling Whale"), login);

        getElement().getStyle().set("background", "var(--whale-parchment)");
        getElement().getStyle().set("background-image", "var(--parchment-texture)");
    }
}
