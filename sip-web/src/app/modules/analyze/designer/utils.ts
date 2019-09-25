import * as get from 'lodash/get';
import * as reject from 'lodash/reject';
import * as startsWith from 'lodash/startsWith';

import { TYPE_ICONS_OBJ, TYPE_MAP } from './consts';
import { ArtifactColumn } from '../types';
import { TYPE_ICONS } from '../consts';

export function getArtifactColumnGeneralType(
  artifactColumn: ArtifactColumn,
  analysisType: string,
  analysisSubType?: string
) {
  if (!!artifactColumn.expression) {
    return 'derived';
  }

  const { type } = artifactColumn;
  const geoType = get(artifactColumn, 'geoType');
  const hasGeoType = Boolean(geoType);
  const isLngLat = geoType === 'lngLat';

  switch (analysisType) {
    case 'pivot':
    case 'chart':
      return get(TYPE_MAP, type);
    case 'map':
      if (hasGeoType) {
        if (startsWith(analysisSubType, 'chart')) {
          if (isLngLat) {
            return get(TYPE_MAP, type);
          }
          return 'geo';
        }

        if (analysisSubType === 'map') {
          if (isLngLat) {
            return 'coordinate';
          }
        }
      }
      return get(TYPE_MAP, type);
  }
}

export function getArtifactColumnTypeIcon(
  artifactColumn: ArtifactColumn,
  analysisType: string,
  analysisSubType?: string
) {
  const generalType = getArtifactColumnGeneralType(
    artifactColumn,
    analysisType,
    analysisSubType
  );
  return get(TYPE_ICONS_OBJ, `${generalType}.icon`);
}

export function getFilterTypes(analysisType: string, analysisSubtype: string) {
  let rejectExpression;
  switch (analysisType) {
    case 'pivot':
      rejectExpression = type =>
        ['geo', 'coordinate', 'derived'].includes(type.value);
      break;
    case 'chart':
      rejectExpression = type => ['geo', 'coordinate'].includes(type.value);
      break;
    case 'map':
      if (analysisSubtype === 'map') {
        rejectExpression = type =>
          ['string', 'date', 'geo'].includes(type.value);
      } else {
        rejectExpression = type =>
          ['string', 'date', 'coordinate'].includes(type.value);
      }
      break;
  }
  return reject(TYPE_ICONS, rejectExpression);
}
