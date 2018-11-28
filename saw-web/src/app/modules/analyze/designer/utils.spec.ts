import * as map from 'lodash/map';
import * as forEach from 'lodash/forEach';
import {
  getArtifactColumnGeneralType,
  getArtifactColumnTypeIcon
} from './utils';

import { ArtifactColumn } from '../types';
import { NUMBER_TYPES, DATE_TYPES } from '../consts';

const numberColumns = map(NUMBER_TYPES, type => ({type} as ArtifactColumn));
const dateColumns = map(DATE_TYPES, type => ({type} as ArtifactColumn));
const stringColumn = {type: 'string'} as ArtifactColumn;
const geoColumn = {type: 'string', geoType: 'state'} as ArtifactColumn;

describe('Analyze utils', () => {

  it('should return the general type of an artifactColumn', () => {
    forEach(numberColumns, col =>
      expect(getArtifactColumnGeneralType(col)).toBe('number'));
    forEach(dateColumns, col =>
      expect(getArtifactColumnGeneralType(col)).toBe('date'));
    expect(getArtifactColumnGeneralType(stringColumn)).toBe('string');
    expect(getArtifactColumnGeneralType(geoColumn)).toBe('geo');
  });

  it('should return the icon of an artifactColumn', () => {
    forEach(numberColumns, col =>
      expect(getArtifactColumnTypeIcon(col)).toBe('icon-number-type'));
    forEach(dateColumns, col =>
      expect(getArtifactColumnTypeIcon(col)).toBe('icon-calendar'));
    expect(getArtifactColumnTypeIcon(stringColumn)).toBe('icon-string-type');
    expect(getArtifactColumnTypeIcon(geoColumn)).toBe('icon-geo-type');
  });
});
