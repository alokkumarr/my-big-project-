import { Component, OnInit, Input } from '@angular/core';
import map from 'lodash/map';
import first from 'lodash/first';
import split from 'lodash/split';
import fpPipe from 'lodash/fp/pipe';
import fpMap from 'lodash/fp/map';
import fpFilter from 'lodash/fp/filter';
import fpToPairs from 'lodash/fp/toPairs';
import fpJoin from 'lodash/fp/join';

@Component({
  selector: 'map-box',
  templateUrl: './map-box.component.html',
  styleUrls: ['./map-box.component.scss']
})
export class MapBoxComponent implements OnInit {

  data: any[];
  dataFields: any[];
  coordinateField: any;
  center;
  mapStyle: string;
  source: any;

  @Input('mapSettings') set setMapSettings(settings) {
    this.mapStyle = settings.mapStyle;
  }

  @Input('sqlBuilder') set setSqlBuilder(sqlBuilder) {
    console.log('sqlBuilder', sqlBuilder);
    this.dataFields = sqlBuilder.dataFields;
    this.coordinateField = first(sqlBuilder.nodeFields);
  }

  @Input('data') set setData(data) {
    setTimeout(() => {
      const features = map(data, datum => {
        const coordinatesKey = this.coordinateField.columnName;
        const [lng, lat] = split(datum[coordinatesKey], ',');
        const lnglat = [parseFloat(lng), parseFloat(lat)];
        const label = fpPipe(
          fpToPairs,
          fpFilter(([key]) => key !== coordinatesKey),
          fpMap(([key, val]) => `${key}: ${val}`),
          fpJoin('\n')
        )(datum);

        return {
          type: 'Feature',
          properties: {
            label
          },
          geometry: {
            type: 'Point',
            coordinates: lnglat
          }
        };
      });

      this.source = {
        type: 'FeatureCollection',
        features
      };
      const centerIndex = features.length / 2;
      this.center = features[centerIndex].geometry.coordinates;
    }, 10);
  }

  ngOnInit() {
  }

  selectCluster() {
  }
}
