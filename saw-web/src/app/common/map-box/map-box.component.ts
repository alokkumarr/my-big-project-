import { Component, Input, OnChanges } from '@angular/core';
import map from 'lodash/map';
import first from 'lodash/first';
import split from 'lodash/split';
import reduce from 'lodash/reduce';
import get from 'lodash/get';
import fpPipe from 'lodash/fp/pipe';
import fpMap from 'lodash/fp/map';
import fpFilter from 'lodash/fp/filter';
import fpToPairs from 'lodash/fp/toPairs';
import { MarkerDataPoint } from './types';

@Component({
  selector: 'map-box',
  templateUrl: './map-box.component.html',
  styleUrls: ['./map-box.component.scss']
})
export class MapBoxComponent implements OnChanges {
  public selectedPoint: GeoJSON.Feature<GeoJSON.Point>;
  dataFields: any[];
  coordinateField: any;
  center: number[];
  mapStyle: string;
  geoJson: GeoJSON.GeoJSON;

  @Input('mapSettings') set setMapSettings(settings) {
    this.mapStyle = settings.mapStyle;
  }

  @Input('sqlBuilder') set setSqlBuilder(sqlBuilder) {
    this.coordinateField = first(sqlBuilder.nodeFields);
    this.dataFields = sqlBuilder.dataFields;
  }

  @Input() data: any[];

  constructor() {
    // preserveDrawingBuffer has to be set to true so that when downloading the dashboard,
    // so that when using canvas.toDataURL() it still has the data preserved
    HTMLCanvasElement.prototype.getContext = (function(origFn) {
      return function(type, attributes) {
        if (type === 'webgl') {
          attributes = Object.assign({}, attributes, {
            preserveDrawingBuffer: true
          });
        }
        return origFn.call(this, type, attributes);
      };
    })(HTMLCanvasElement.prototype.getContext);
  }

  ngOnChanges(changes) {
    if (this.data && this.coordinateField && this.dataFields) {
      setTimeout(() => {
        this.setGeoJson(this.data, this.coordinateField, this.dataFields);
      }, 10);
    }
  }

  setGeoJson(data, coordinateField, dataFields) {
    const features = this.data2geoJsonFeatures(
      data,
      coordinateField,
      dataFields
    );

    this.geoJson = {
      type: 'FeatureCollection',
      features
    };

    // set center if possible
    if (features.length > 0) {
      const centerIndex = features.length / 2;
      this.center = features[centerIndex].geometry['coordinates'];
    }
  }

  selectPoint(event: MouseEvent, point) {
    event.stopPropagation(); // This is needed, otherwise the popup will close immediately
    // Change the ref, to trigger mgl-popup onChanges (when the user click on the same cluster)
    this.selectedPoint = { ...point };
  }

  data2geoJsonFeatures(
    data,
    coordinateField,
    dataFields
  ): Array<GeoJSON.Feature> {
    const allFields = [coordinateField, ...dataFields];
    const fieldsMap = reduce(
      allFields,
      (acc, field) => {
        acc[field.columnName] = field;
        return acc;
      },
      {}
    );
    return map(data, datum => {
      const coordinatesKey = coordinateField.columnName;
      const [lng, lat] = split(datum[coordinatesKey], ',');
      const lnglat = [parseFloat(lng), parseFloat(lat)];
      const aggregates: MarkerDataPoint = fpPipe(
        fpToPairs,
        fpFilter(([key]) => key !== coordinatesKey),
        fpMap(([key, value]) => {
          const alias = get(fieldsMap, `${key}.alias`);
          const displayName = get(fieldsMap, `${key}.displayName`);
          const aggregate: MarkerDataPoint = {
            key,
            value,
            label: alias || displayName
          };
          return aggregate;
        })
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
