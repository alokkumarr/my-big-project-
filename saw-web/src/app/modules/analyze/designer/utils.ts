import * as get from 'lodash/get';
import { TYPE_ICONS_OBJ, TYPE_MAP } from './consts';

export function getArtifactColumnGeneralType(artifactColumn) {
  const { geoType, type } = artifactColumn;
  if (geoType && type === 'string') {
    return 'geo';
  }
  return get(TYPE_MAP, type);
}

export function getArtifactColumnTypeIcon(artifactColumn) {
  const generalType = getArtifactColumnGeneralType(artifactColumn);
  return get(TYPE_ICONS_OBJ, `${generalType}.icon`);
}
