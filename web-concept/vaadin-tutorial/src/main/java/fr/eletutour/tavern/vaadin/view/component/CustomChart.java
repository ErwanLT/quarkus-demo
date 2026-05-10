package fr.eletutour.tavern.vaadin.view.component;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import fr.eletutour.tavern.vaadin.model.AnalyticsData;

import java.util.List;

/**
 * A custom Chart component using Chart.js (Open Source alternative to Vaadin Charts).
 */
@Tag("div")
public class CustomChart extends Div implements HasSize {

    private final String chartId;
    private final String canvasId;

    public CustomChart() {
        this.chartId = "chart-" + System.currentTimeMillis();
        this.canvasId = "canvas-" + this.chartId;
        setId(chartId);
        
        getElement().executeJs("this.innerHTML = '<canvas id=\"" + canvasId + "\"></canvas>';");
        
        getStyle().set("background", "var(--whale-parchment-light)");
        getStyle().set("padding", "1rem");
        getStyle().set("border", "1px solid #d2b48c");
        getStyle().set("box-shadow", "var(--medieval-shadow)");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        attachEvent.getUI().getPage().addJavaScript("https://cdn.jsdelivr.net/npm/chart.js");
    }

    public void setData(AnalyticsData data) {
        // We use a helper JS function to create or update the chart
        getElement().executeJs(
            "const el = this;" +
            "const initChart = () => {" +
            "  if (typeof Chart === 'undefined') {" +
            "    setTimeout(initChart, 100);" +
            "    return;" +
            "  }" +
            "  const canvas = el.querySelector('canvas');" +
            "  if (!canvas) return;" +
            "  const ctx = canvas.getContext('2d');" +
            "  if (el._chart) { el._chart.destroy(); }" +
            "  el._chart = new Chart(ctx, {" +
            "    type: $0," +
            "    data: {" +
            "      labels: $1," +
            "      datasets: [{" +
            "        label: $2," +
            "        data: $3," +
            "        backgroundColor: [" +
            "          'rgba(139, 0, 0, 0.7)', 'rgba(212, 175, 55, 0.7)', " +
            "          'rgba(45, 90, 39, 0.7)', 'rgba(62, 39, 35, 0.7)'," +
            "          'rgba(166, 124, 0, 0.7)', 'rgba(44, 44, 44, 0.7)'" +
            "        ]," +
            "        borderColor: 'rgba(62, 39, 35, 1)'," +
            "        borderWidth: 1" +
            "      }]" +
            "    }," +
            "    options: {" +
            "      responsive: true," +
            "      maintainAspectRatio: false," +
            "      plugins: {" +
            "        legend: { labels: { color: '#1a1a1a', font: { family: 'Almendra', size: 14 } } }" +
            "      }," +
            "      scales: $0 === 'bar' ? {" +
            "        y: { beginAtZero: true, grid: { color: 'rgba(0,0,0,0.1)' } }," +
            "        x: { grid: { display: false } }" +
            "      } : {}" +
            "    }" +
            "  });" +
            "};" +
            "initChart();",
            data.chartType(), data.labels(), data.title(), data.values()
        );
    }
}
