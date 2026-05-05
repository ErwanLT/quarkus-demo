package fr.eletutour.tavern.vaadin;

import com.vaadin.quarkus.QuarkusVaadinServlet;
import jakarta.servlet.annotation.WebServlet;

@WebServlet(urlPatterns = "/*", name = "AdminServlet", asyncSupported = true, loadOnStartup = 1)
public class AdminServlet extends QuarkusVaadinServlet {
}
