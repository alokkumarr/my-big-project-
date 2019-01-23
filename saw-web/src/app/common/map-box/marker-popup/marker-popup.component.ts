import { Component, Input } from '@angular/core';

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'marker-popup',
  template: `
      <ul>
        <li *ngFor="let aggregate of aggregates">
        <strong>{{aggregate.key}}</strong>: {{aggregate.value}}
        </li>
      </ul>
    `,
  styleUrls: ['./marker-popup.component.scss']
})
export class MarkerPopupComponent {
  public aggregates: Array<{key: string, value: string}>;

  @Input('selectedPoint') set setLabels(selectedPoint: GeoJSON.Feature<GeoJSON.Point>) {
    this.aggregates = selectedPoint.properties.aggregates;
  }

}
