import * as get from 'lodash/get';
import * as has from 'lodash/has';
import { TYPE_ICONS_OBJ, TYPE_MAP } from './consts';
import { ArtifactColumn } from '../types';

export function getArtifactColumnGeneralType(artifactColumn: ArtifactColumn) {
  const { type } = artifactColumn;
  const hasGeoType = has(artifactColumn, 'geoType');

  if ( hasGeoType && type === 'string') {
    return 'geo';
  }
  return get(TYPE_MAP, type);
}

export function getArtifactColumnTypeIcon(artifactColumn: ArtifactColumn) {
  const generalType = getArtifactColumnGeneralType(artifactColumn);
  return get(TYPE_ICONS_OBJ, `${generalType}.icon`);
}
