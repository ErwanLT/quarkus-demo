package fr.eletutour.tavern.vaadin.view.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import fr.eletutour.tavern.vaadin.model.MapLocation;

import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;

/**
 * A custom Map component using LitElement and Leaflet.js.
 */
@Tag("tavern-map")
@JsModule("./tavern-map.ts")
public class CustomMap extends Component implements HasSize {

    public CustomMap() {
    }

    public void setLocations(List<MapLocation> locations) {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        ArrayNode array = factory.arrayNode();
        
        for (MapLocation loc : locations) {
            ObjectNode node = factory.objectNode();
            node.put("name", loc.name());
            node.put("latitude", loc.latitude());
            node.put("longitude", loc.longitude());
            node.put("description", loc.description());
            node.put("type", loc.type());
            array.add(node);
        }

        getElement().setPropertyJson("locations", array);
    }
}
