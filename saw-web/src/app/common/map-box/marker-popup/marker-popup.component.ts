import { Component, Input } from '@angular/core';
import { MarkerDataPoint } from '../types';
@Component({
  // tslint:disable-next-line:component-selector
  selector: 'marker-popup',
  template: `
    <ul>
      <li *ngFor="let aggregate of aggregates">
        <strong>{{ aggregate.label || aggregate.key }}</strong
        >: {{ aggregate.value | round: 2 }}
      </li>
    </ul>
  `,
  styleUrls: ['./marker-popup.component.scss']
})
export class MarkerPopupComponent {
  public aggregates: Array<MarkerDataPoint>;

  @Input('selectedPoint') set setLabels(
    selectedPoint: GeoJSON.Feature<GeoJSON.Point>
  ) {
    this.aggregates = selectedPoint.properties.aggregates;
  }
}
