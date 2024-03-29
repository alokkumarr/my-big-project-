import * as map from 'lodash/map';
import * as forEach from 'lodash/forEach';
import {
  getArtifactColumnGeneralType,
  getArtifactColumnTypeIcon,
  getFilterTypes
} from './utils';

import { ArtifactColumn } from '../types';
import { NUMBER_TYPES, DATE_TYPES } from '../consts';

const numberColumns = map(NUMBER_TYPES, type => ({ type } as ArtifactColumn));
const dateColumns = map(DATE_TYPES, type => ({ type } as ArtifactColumn));
const stringColumn = { type: 'string' } as ArtifactColumn;
const geoColumn = { type: 'string', geoType: 'state' } as ArtifactColumn;
const lngLatColumn = { type: 'string', geoType: 'lngLat' } as ArtifactColumn;

describe('Analyze utils', () => {
  it('should return the general type of an artifactColumn', () => {
    forEach(numberColumns, col =>
      expect(getArtifactColumnGeneralType(col, 'pivot')).toBe('number')
    );
    forEach(dateColumns, col =>
      expect(getArtifactColumnGeneralType(col, 'pivot')).toBe('date')
    );
    expect(getArtifactColumnGeneralType(stringColumn, 'pivot')).toBe('string');
    expect(getArtifactColumnGeneralType(geoColumn, 'map', 'chart')).toBe('geo');
    expect(getArtifactColumnGeneralType(lngLatColumn, 'map', 'map')).toBe(
      'coordinate'
    );
    expect(
      getArtifactColumnGeneralType({ expression: 'abc' } as any, 'pivot')
    ).toEqual('derived');

    expect(
      getArtifactColumnGeneralType(
        { type: 'double', geoType: 'lngLat' } as any,
        'map',
        'chart'
      )
    ).toEqual('number');

    expect(
      getArtifactColumnGeneralType(
        { type: 'double', geoType: 'testing' } as any,
        'map',
        'chart'
      )
    ).toEqual('geo');

    expect(
      getArtifactColumnGeneralType({ type: 'double' } as any, 'map', 'chart')
    ).toEqual('number');
  });

  it('should return the icon of an artifactColumn', () => {
    forEach(numberColumns, col =>
      expect(getArtifactColumnTypeIcon(col, 'pivot')).toBe('icon-number-type')
    );
    forEach(dateColumns, col =>
      expect(getArtifactColumnTypeIcon(col, 'pivot')).toBe('icon-calendar')
    );
    expect(getArtifactColumnTypeIcon(stringColumn, 'pivot')).toBe(
      'icon-string-type'
    );
    expect(getArtifactColumnTypeIcon(geoColumn, 'map', 'chart')).toBe(
      'icon-geo-chart'
    );
    expect(getArtifactColumnTypeIcon(lngLatColumn, 'map', 'map')).toBe(
      'icon-geo-chart'
    );
  });

  it('should return the filter type', () => {
    const pivotIcons = getFilterTypes('pivot', '');
    expect(pivotIcons.find(icon => icon.value === 'geo')).toBeUndefined();

    const chart = getFilterTypes('chart', '');
    expect(chart.find(icon => icon.value === 'geo')).toBeUndefined();

    const mapChart = getFilterTypes('map', 'chart');
    expect(mapChart.find(icon => icon.value === 'coordinate')).toBeUndefined();

    const mapIcons = getFilterTypes('map', 'map');
    expect(mapIcons.find(icon => icon.value === 'geo')).toBeUndefined();
  });
});
