import { LitElement, html, css, PropertyValues } from 'lit';
import { customElement, property } from 'lit/decorators.js';

@customElement('tavern-map')
export class TavernMap extends LitElement {
  @property({ type: Array })
  locations: any[] = [];

  // Disable Shadow DOM for Leaflet compatibility
  createRenderRoot() {
    return this;
  }

  private map: any;

  connectedCallback() {
    super.connectedCallback();
    this.injectGlobalStyles();
    this.initMap();
  }

  private injectGlobalStyles() {
    if (document.getElementById('tavern-map-styles')) return;
    const style = document.createElement('style');
    style.id = 'tavern-map-styles';
    style.innerHTML = `
      .tavern-map-container {
        width: 100%;
        height: 100%;
        min-height: 400px;
        border: 3px double var(--whale-wood-dark, #3e2723);
        box-shadow: var(--medieval-shadow);
      }
      .tavern-marker {
        background: var(--whale-blood, #8b0000);
        border: 2px solid var(--whale-gold, #d4af37);
        border-radius: 50%;
        width: 24px;
        height: 24px;
        box-shadow: 2px 2px 4px rgba(0,0,0,0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        color: white;
        font-weight: bold;
        font-size: 14px;
        cursor: pointer;
      }
      .tavern-marker.POI { background: var(--whale-gold, #d4af37); color: black; }
      .tavern-marker.Danger { background: black; border-color: red; }
    `;
    document.head.appendChild(style);
  }

  render() {
    return html`
      <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css">
      <div id="map" class="tavern-map-container"></div>
    `;
  }

  protected updated(changedProperties: PropertyValues) {
    super.updated(changedProperties);
    if (changedProperties.has('locations')) {
      this.updateMarkers();
    }
  }

  private initMap() {
    const loadLeaflet = () => {
      if (typeof (window as any).L === 'undefined') {
        if (!document.getElementById('leaflet-js')) {
          const script = document.createElement('script');
          script.id = 'leaflet-js';
          script.src = "https://unpkg.com/leaflet@1.9.4/dist/leaflet.js";
          script.onload = () => this.createMap();
          document.head.appendChild(script);
        }
      } else {
        this.createMap();
      }
    };
    loadLeaflet();
  }

  private createMap() {
    const L = (window as any).L;
    const mapContainer = this.querySelector('#map');
    if (!mapContainer || this.map || !L) return;

    // --- LE CORRECTIF POUR LE MARQUEUR 404 ---
    // On supprime la résolution de chemin par défaut qui échoue
    delete (L.Icon.Default.prototype as any)._getIconUrl;

    // On force Leaflet à utiliser le CDN pour ses propres images
    L.Icon.Default.mergeOptions({
      iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
      iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
      shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
    });
    // ----------------------------------------

    this.map = L.map(mapContainer).setView([48.8566, 2.3522], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors'
    }).addTo(this.map);

    setTimeout(() => {
      this.map.invalidateSize();
      this.updateMarkers();
    }, 100);
  }

  private updateMarkers() {
    const L = (window as any).L;
    if (!this.map || !L || !this.locations || !Array.isArray(this.locations)) {
      return;
    }

    // Nettoyage des anciens marqueurs
    this.map.eachLayer((layer: any) => {
      if (layer instanceof L.Marker) {
        this.map.removeLayer(layer);
      }
    });

    // Création des marqueurs par défaut
    this.locations.forEach((loc: any) => {
      // L.marker utilise maintenant automatiquement l'icône par défaut corrigée via le CDN
      L.marker([loc.latitude, loc.longitude])
          .addTo(this.map)
          .bindPopup(`<b>${loc.name}</b><br>${loc.description}`);
    });
  }
}
