package fr.eletutour.tavern.vaadin.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import fr.eletutour.tavern.vaadin.model.AnalyticsData;
import fr.eletutour.tavern.vaadin.model.CellarStock;
import fr.eletutour.tavern.vaadin.service.StockUpdatedEvent;
import fr.eletutour.tavern.vaadin.service.TavernBroadcaster;
import fr.eletutour.tavern.vaadin.service.TavernService;
import fr.eletutour.tavern.vaadin.view.component.CustomChart;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.function.Consumer;

@RolesAllowed("ADMIN")
@PageTitle("Analytiques de la Taverne")
@Route(value = "analytics", layout = MainLayout.class)
public class AnalyticsView extends VerticalLayout {

    private final TavernService tavernService;
    private final TavernBroadcaster broadcaster;
    private final CustomChart liveStockChart = new CustomChart();
    private Consumer<StockUpdatedEvent> listener;

    public AnalyticsView(TavernService tavernService, TavernBroadcaster broadcaster) {
        this.tavernService = tavernService;
        this.broadcaster = broadcaster;

        addClassName("view-shell");
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "transparent");

        add(TavernComponents.createPageLayout("Analytiques",
                "Performance et Tendances",
                "Visualisez la santé financière et la popularité de vos services en temps réel."));

        // Live Stocks Section
        liveStockChart.setWidthFull();
        liveStockChart.setHeight("300px");
        updateLiveStockChart(tavernService.getCellarBoard().stocks());
        add(TavernComponents.createSection("Niveaux des stocks en direct", liveStockChart));

        HorizontalLayout chartsLayout = new HorizontalLayout();
        chartsLayout.setWidthFull();
        chartsLayout.setHeight("400px");
        chartsLayout.setSpacing(true);

        CustomChart revenueChart = new CustomChart();
        revenueChart.setWidthFull();
        revenueChart.setHeightFull();
        revenueChart.setData(tavernService.getRevenueData());

        CustomChart popularityChart = new CustomChart();
        popularityChart.setWidthFull();
        popularityChart.setHeightFull();
        popularityChart.setData(tavernService.getDrinkPopularity());

        chartsLayout.add(revenueChart, popularityChart);

        add(TavernComponents.createSection("Historique et répartition", chartsLayout));
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        listener = event -> attachEvent.getUI().access(() -> {
            updateLiveStockChart(event.stocks());
        });
        broadcaster.registerStockListener(listener);
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        if (listener != null) {
            broadcaster.unregisterStockListener(listener);
            listener = null;
        }
    }

    private void updateLiveStockChart(List<CellarStock> stocks) {
        AnalyticsData data = new AnalyticsData(
                "Quantités disponibles",
                stocks.stream().map(CellarStock::productName).toList(),
                stocks.stream().map(s -> (double) s.currentLevel()).toList(),
                "bar"
        );
        liveStockChart.setData(data);
    }
}

