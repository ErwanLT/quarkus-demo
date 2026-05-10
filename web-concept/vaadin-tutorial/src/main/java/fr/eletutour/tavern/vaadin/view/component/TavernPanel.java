package fr.eletutour.tavern.vaadin.view.component;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.util.List;

/**
 * A custom component for a panel in the tavern theme.
 */
public class TavernPanel extends Div {

    public TavernPanel(String title, String body) {
        this(title);
        Paragraph text = new Paragraph(body);
        text.setClassName("panel-copy");
        add(text);
    }

    public TavernPanel(String title, List<String> listItems) {
        this(title);
        VerticalLayout list = new VerticalLayout();
        list.setPadding(false);
        list.setSpacing(false);
        list.setClassName("stack-list");
        listItems.stream().map(this::createListItem).forEach(list::add);
        add(list);
    }

    private TavernPanel(String title) {
        setClassName("whale-panel");
        H3 heading = new H3(title);
        heading.setClassName("panel-title");
        add(heading);
    }

    private Paragraph createListItem(String item) {
        Paragraph row = new Paragraph(item);
        row.setClassName("stack-item");
        return row;
    }
}
