package fr.eletutour.tavern.vaadin.view.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import fr.eletutour.tavern.vaadin.model.AnalyticsData;

import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

/**
 * A custom Chart component using LitElement and Chart.js.
 */
@Tag("tavern-chart")
@JsModule("./tavern-chart.ts")
public class CustomChart extends Component implements HasSize {

    public CustomChart() {
    }

    public void setData(AnalyticsData data) {
        // Convert record to Jackson ObjectNode for property sync
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ObjectNode json = factory.objectNode();

        json.put("title", data.title());
        json.put("chartType", data.chartType());

        ArrayNode labels = factory.arrayNode();
        data.labels().forEach(labels::add);
        json.set("labels", labels);

        ArrayNode values = factory.arrayNode();
        data.values().forEach(values::add);
        json.set("values", values);

        getElement().setPropertyJson("data", json);
    }
}

