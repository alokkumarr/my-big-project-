import { Component, OnInit, Input } from '@angular/core';
import { DATA } from './map-data';

import map from 'lodash/map';

@Component({
  selector: 'map-box',
  templateUrl: './map-box.component.html',
  styleUrls: ['./map-box.component.scss']
})
export class MapBoxComponent implements OnInit {

  data: any[];
  dataFields: any[];
  center;
  mapStyle: string;

  @Input('mapSettings') set setMapSettings(settings) {
    this.mapStyle = settings.mapStyle;
  }

  @Input('sqlBuilder') set setSqlBuilder(sqlBuilder) {
    console.log('sqlBuilder', sqlBuilder);
    this.dataFields = sqlBuilder.dataFields;
  }

  @Input('data') set setData(data) {
    setTimeout(() => {
      const { features } = DATA;
      this.data = map(data, (datum, index) => {
        const { coordinates } = features[index].geometry;
        return {...datum, lnglat: coordinates};
      });
      this.center = this.data[0].lnglat;
    }, 10);
  }

  ngOnInit() {
  }
}
