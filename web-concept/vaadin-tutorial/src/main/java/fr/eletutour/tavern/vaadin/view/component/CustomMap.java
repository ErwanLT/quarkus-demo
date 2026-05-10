package fr.eletutour.tavern.vaadin.view.component;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.Div;
import fr.eletutour.tavern.vaadin.model.MapLocation;

import java.util.List;

/**
 * A custom Map component using Leaflet.js (Open Source alternative to Vaadin Map).
 */
@Tag("div")
@StyleSheet("https://unpkg.com/leaflet@1.9.4/dist/leaflet.css")
public class CustomMap extends Div implements HasSize {

    private final String mapId;

    public CustomMap() {
        this.mapId = "map-" + System.currentTimeMillis();
        setId(mapId);
        getStyle().set("border", "3px double var(--whale-wood-dark)");
        getStyle().set("box-shadow", "var(--medieval-shadow)");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        // Load Leaflet JS from CDN
        attachEvent.getUI().getPage().addJavaScript("https://unpkg.com/leaflet@1.9.4/dist/leaflet.js");
        
        // Initialize Map after JS is loaded
        getElement().executeJs("""
            const el = this;
            const loadLeaflet = () => {
              if (typeof L === 'undefined') {
                setTimeout(loadLeaflet, 100);
                return;
              }
              if (el._map) { return; } // Avoid re-initialization
              const map = L.map(el).setView([48.8566, 2.3522], 13);
              L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; OpenStreetMap contributors'
              }).addTo(map);
              el._map = map;
              if (el._pendingLocations) {
                el._pendingLocations.forEach(loc => {
                  L.marker([loc.lat, loc.lng]).addTo(el._map).bindPopup('<b>' + loc.name + '</b><br>' + loc.desc);
                });
                delete el._pendingLocations;
              }
            };
            loadLeaflet();
            """
        );
    }

    public void setLocations(List<MapLocation> locations) {
        for (MapLocation loc : locations) {
            getElement().executeJs("""
                const el = this;
                if (el._map) {
                  L.marker([$0, $1]).addTo(el._map).bindPopup('<b>' + $2 + '</b><br>' + $3);
                } else {
                  if (!el._pendingLocations) el._pendingLocations = [];
                  el._pendingLocations.push({lat: $0, lng: $1, name: $2, desc: $3});
                }
                """,
                loc.latitude(), loc.longitude(), loc.name(), loc.description()
            );
        }
    }
}
