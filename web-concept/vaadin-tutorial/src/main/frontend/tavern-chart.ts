import { LitElement, html, css } from 'lit';
import { customElement, property } from 'lit/decorators.js';

@customElement('tavern-chart')
export class TavernChart extends LitElement {
  @property({ type: Object })
  data: any = null;

  static styles = css`
    :host {
      display: block;
      width: 100%;
      height: 100%;
      background: var(--whale-parchment-light);
      padding: 1rem;
      border: 1px solid #d2b48c;
      box-shadow: var(--medieval-shadow);
      box-sizing: border-box;
    }
    canvas {
      width: 100% !important;
      height: 100% !important;
    }
  `;

  private chart: any;

  render() {
    return html`<canvas></canvas>`;
  }

  firstUpdated() {
    this.initChartLib();
  }

  updated(changedProperties: Map<string, any>) {
    if (changedProperties.has('data') && this.data) {
      this.updateChart();
    }
  }

  private initChartLib() {
    if (typeof (window as any).Chart === 'undefined') {
      const script = document.createElement('script');
      script.src = "https://cdn.jsdelivr.net/npm/chart.js";
      script.onload = () => this.updateChart();
      document.head.appendChild(script);
    } else {
      this.updateChart();
    }
  }

  private updateChart() {
    const Chart = (window as any).Chart;
    const canvas = this.shadowRoot?.querySelector('canvas');
    if (!canvas || !Chart || !this.data) return;

    if (this.chart) {
      this.chart.destroy();
    }

    const ctx = canvas.getContext('2d');
    this.chart = new Chart(ctx, {
      type: this.data.chartType,
      data: {
        labels: this.data.labels,
        datasets: [{
          label: this.data.title,
          data: this.data.values,
          backgroundColor: [
            'rgba(139, 0, 0, 0.7)', 'rgba(212, 175, 55, 0.7)', 
            'rgba(45, 90, 39, 0.7)', 'rgba(62, 39, 35, 0.7)',
            'rgba(166, 124, 0, 0.7)', 'rgba(44, 44, 44, 0.7)'
          ],
          borderColor: 'rgba(62, 39, 35, 1)',
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { labels: { color: '#1a1a1a', font: { family: 'Almendra', size: 14 } } }
        },
        scales: this.data.chartType === 'bar' ? {
          y: { beginAtZero: true, grid: { color: 'rgba(0,0,0,0.1)' } },
          x: { grid: { display: false } }
        } : {}
      }
    });
  }
}
