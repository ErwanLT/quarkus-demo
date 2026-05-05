package fr.eletutour.tavern.vaadin;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

/**
 * The AppShellConfigurator implementation is the main entry point for configuring
 * the Vaadin application. The @Theme annotation points to the custom theme
 * folder in src/main/frontend/themes/falling-whale.
 */
@Push
@PWA(name = "The Falling Whale", shortName = "Whale")
@Theme("falling-whale")
public class TavernAppShell implements AppShellConfigurator {
}
