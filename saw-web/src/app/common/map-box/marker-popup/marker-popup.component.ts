import { Component, Input } from '@angular/core';
import { MarkerDataPoint } from '../types';
@Component({
  // tslint:disable-next-line:component-selector
  selector: 'marker-popup',
  templateUrl: './marker-popup.component.html',
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
