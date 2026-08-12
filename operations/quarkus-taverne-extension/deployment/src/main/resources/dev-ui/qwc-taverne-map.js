import { LitElement, html } from 'lit';

export class TaverneMap extends LitElement {

    render() {
        return html`
            <div>
                <h1>La carte de la Taverne</h1>
                <p>Bienvenue à The Falling Whale.</p>
            </div>
        `;
    }
}

customElements.define('qwc-taverne-map', TaverneMap);