import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA, Component, Input } from '@angular/core';
import get from 'lodash/get';

import { MapBoxComponent } from './map-box.component';

const lng = -10.22;
const lat = 24.343;
const coordinateField = { columnName: 'COORDINATES' };
const data = [{
  'COORDINATES': `${lng},${lat}`,
  'ORDER_COUNT': 12.32
}];

@Component({
  selector: 'map-box',
  template: '<div>map box</div>'
})
class MapBoxStubComponent {
  @Input() options: any;
}

describe('Map Box Component', () => {
  let fixture: ComponentFixture<MapBoxComponent>;

  beforeEach(
    async(() => {
      TestBed.configureTestingModule({
        declarations: [MapBoxComponent, MapBoxStubComponent],
        schemas: [CUSTOM_ELEMENTS_SCHEMA]
      })
        .compileComponents()
        .then(() => {
          fixture = TestBed.createComponent(MapBoxComponent);
          fixture.detectChanges();
        });
    })
  );

  it('should transform data into geojson', () => {
    const component = fixture.componentInstance;
    const geoJsonFeatures = component.data2geoJsonFeatures(data, coordinateField);
    const coordinatesPath = ['0', 'geometry', 'coordinates'];
    const coordinates = get(geoJsonFeatures, coordinatesPath);
    expect(coordinates[0]).toEqual(lng);
    expect(coordinates[1]).toEqual(lat);
  });
});
