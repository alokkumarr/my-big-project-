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
import { AGGREGATE_TYPES_OBJ } from '../../common/consts';

interface GeoFeature {
  index: number;
  type: 'Feature';
  properties: {
    aggregates: any[];
  };
  geometry: {
    type: 'Point';
    coordinates: number[];
  };
}
interface GeoJson {
  type: 'FeatureCollection';
  features: GeoFeature[];
}

@Component({
  selector: 'map-box',
  templateUrl: './map-box.component.html',
  styleUrls: ['./map-box.component.scss']
})
export class MapBoxComponent implements OnChanges {
  public selectedPoint: GeoFeature;
  dataFields: any[];
  coordinateField: any;
  center: number[];
  mapStyle: string;
  geoJson: GeoJson;

  @Input('mapSettings') set setMapSettings(settings) {
    this.mapStyle = settings.mapStyle;
  }

  @Input('sqlBuilder') set setSqlBuilder(sqlBuilder) {
    this.coordinateField = first(sqlBuilder.nodeFields);
    this.dataFields = sqlBuilder.dataFields;
  }

  @Input() data: any[];

  ngOnChanges() {
    if (this.data && this.coordinateField && this.dataFields) {
      setTimeout(() => {
        this.setGeoJson(this.data, this.coordinateField, this.dataFields);
        if (this.selectedPoint) {
          this.selectedPoint = {
            ...this.geoJson.features[this.selectedPoint.index]
          };
        }
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

  data2geoJsonFeatures(data, coordinateField, dataFields): Array<GeoFeature> {
    const allFields = [coordinateField, ...dataFields];
    const fieldsMap = reduce(
      allFields,
      (acc, field) => {
        acc[field.columnName] = field;
        return acc;
      },
      {}
    );
    return map(data, (datum, index) => {
      const coordinatesKey = coordinateField.columnName;
      const [lng, lat] = split(datum[coordinatesKey], ',');
      const lnglat = [parseFloat(lng), parseFloat(lat)];
      const aggregates: MarkerDataPoint = fpPipe(
        fpToPairs,
        fpFilter(([key]) => key !== coordinatesKey),
        fpMap(([key, value]) => {
          const { alias, displayName, aggregate } = get(fieldsMap, key);
          const aggregateFun = AGGREGATE_TYPES_OBJ[aggregate].designerLabel;
          return {
            key,
            value,
            label: `${aggregateFun}(${alias || displayName})`
          };
        })
      )(datum);

      return {
        index,
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
