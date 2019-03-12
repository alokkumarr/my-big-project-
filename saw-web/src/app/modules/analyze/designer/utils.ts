import * as get from 'lodash/get';
import * as has from 'lodash/has';
import { TYPE_ICONS_OBJ, TYPE_MAP } from './consts';
import { ArtifactColumn } from '../types';

export function getArtifactColumnGeneralType(
  artifactColumn: ArtifactColumn,
  chartType?: string
) {
  const { type } = artifactColumn;
  const hasGeoType = has(artifactColumn, 'geoType');
  const isGeoType = chartType === 'geo';

  if (hasGeoType && isGeoType && type === 'string') {
    return 'geo';
  }
  return get(TYPE_MAP, type);
}

export function getArtifactColumnTypeIcon(
  artifactColumn: ArtifactColumn,
  chartType?: string
) {
  const generalType = getArtifactColumnGeneralType(artifactColumn, chartType);
  return get(TYPE_ICONS_OBJ, `${generalType}.icon`);
}
