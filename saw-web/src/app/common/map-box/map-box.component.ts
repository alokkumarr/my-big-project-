import { Component, Input } from '@angular/core';
import map from 'lodash/map';
import first from 'lodash/first';
import split from 'lodash/split';
import fpPipe from 'lodash/fp/pipe';
import fpMap from 'lodash/fp/map';
import fpFilter from 'lodash/fp/filter';
import fpToPairs from 'lodash/fp/toPairs';

@Component({
  selector: 'map-box',
  templateUrl: './map-box.component.html',
  styleUrls: ['./map-box.component.scss']
})
export class MapBoxComponent {

  public selectedPoint: GeoJSON.Feature<GeoJSON.Point>;
  data: any[];
  dataFields: any[];
  coordinateField: any;
  center: number[];
  mapStyle: string;
  geoJson: GeoJSON.GeoJSON;

  @Input('mapSettings') set setMapSettings(settings) {
    this.mapStyle = settings.mapStyle;
  }

  @Input('sqlBuilder') set setSqlBuilder(sqlBuilder) {
    this.dataFields = sqlBuilder.dataFields;
    this.coordinateField = first(sqlBuilder.nodeFields);
  }

  @Input('data') set setData(data) {
    if (!data) {
      return;
    }
    setTimeout(() => {
      const features = this.data2geoJsonFeatures(data, this.coordinateField);
      this.geoJson = {
        type: 'FeatureCollection',
        features
      };

      // set center if possible
      if (features.length > 0) {
        const centerIndex = features.length / 2;
        this.center = features[centerIndex].geometry['coordinates'];
      }
    }, 10);
  }

  selectPoint(event: MouseEvent, point) {
    event.stopPropagation(); // This is needed, otherwise the popup will close immediately
    // Change the ref, to trigger mgl-popup onChanges (when the user click on the same cluster)
    this.selectedPoint = { ...point };
  }

  data2geoJsonFeatures(data, coordinateField): Array<GeoJSON.Feature> {
    return map(data, datum => {
      const coordinatesKey = coordinateField.columnName;
      const [lng, lat] = split(datum[coordinatesKey], ',');
      const lnglat = [parseFloat(lng), parseFloat(lat)];
      const aggregates = fpPipe(
        fpToPairs,
        fpFilter(([key]) => key !== coordinatesKey),
        fpMap(([key, value]) => ({key, value}))
      )(datum);

      return {
        type: 'Feature',
        properties: {
          aggregates
        },
        geometry: {
          type: 'Point',
          coordinates: lnglat
        }
      };
    });
  }
}
