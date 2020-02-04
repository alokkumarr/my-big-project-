import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA, Component, Input } from '@angular/core';
import get from 'lodash/get';
import split from 'lodash/split';

import { MapBoxComponent, DEFAULT_MAP_BOX_ZOOM } from './map-box.component';

const lng = -10.22;
const lat = 24.343;
const coordinateField = { columnName: 'COORDINATES' };
const dataFields = [
  {
    columnName: 'ORDER_COUNT',
    alias: 'order count',
    displayName: 'OrderCount',
    aggregate: 'sum'
  }
];
const data = [
  {
    COORDINATES: `${lng},${lat}`,
    ORDER_COUNT: 12.32
  }
];

@Component({
  selector: 'map-box',
  template: '<div>map box</div>'
})
class MapBoxStubComponent {
  @Input() options: any;
}

describe('Map Box Component', () => {
  let fixture: ComponentFixture<MapBoxComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [MapBoxComponent, MapBoxStubComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(MapBoxComponent);
        fixture.detectChanges();
      });
  }));

  it('should transform data into geojson', () => {
    const component = fixture.componentInstance;
    const geoJsonFeatures = component.data2geoJsonFeatures(
      data,
      coordinateField,
      dataFields
    );
    const coordinatesPath = ['0', 'geometry', 'coordinates'];
    const coordinates = get(geoJsonFeatures, coordinatesPath);
    expect(coordinates[0]).toEqual(lng);
    expect(coordinates[1]).toEqual(lat);
  });

  it('should set geojson and center properties', () => {
    const component = fixture.componentInstance;
    component.setGeoJson(data, coordinateField, dataFields);
    const { geoJson, center } = component;
    expect(center[0]).toEqual(lng);
    expect(center[1]).toEqual(lat);
    expect(geoJson).toBeTruthy();
  });

  it('should set the correct imageUrl', () => {
    const component = fixture.componentInstance;
    const width = 100;
    const height = 200;
    const zoom = DEFAULT_MAP_BOX_ZOOM;
    const mapStyle = 'mapstyle';
    component.mapStyle = mapStyle;
    component.zoom = zoom;
    component['_elemRef'] = {
      nativeElement: { querySelector: () => ({ style: { width, height } }) }
    };
    component.setGeoJson(data, coordinateField, dataFields);
    component.setImageUrl();
    const imgUrlArray = split(component.imageUrl, '/');
    expect(imgUrlArray[6]).toEqual(mapStyle);
    const [lngInUrl, latInUrl, zoomInUrl] = split(imgUrlArray[8], ',');
    expect(parseFloat(lngInUrl)).toBe(lng);
    expect(parseFloat(latInUrl)).toBe(lat);
    expect(parseFloat(zoomInUrl)).toBe(zoom);
    const [size] = split(imgUrlArray[9], '?');
    expect(size).toEqual(`${width}x${height}`);
  });
});
