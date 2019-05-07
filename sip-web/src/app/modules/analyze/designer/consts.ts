import * as startCase from 'lodash/startCase';

export {
  FLOAT_TYPES,
  DEFAULT_PRECISION,
  DATE_TYPES,
  NUMBER_TYPES,
  CHART_TYPES_OBJ,
  TYPE_ICONS_OBJ,
  TYPE_ICONS,
  TYPE_MAP
} from '../consts';

import { MapSettings } from './types';

export enum DesignerStates {
  WAITING_FOR_COLUMNS,
  NO_SELECTION,
  SELECTION_WAITING_FOR_DATA,
  SELECTION_WITH_NO_DATA,
  SELECTION_WITH_DATA,
  SELECTION_OUT_OF_SYNCH_WITH_DATA
}

export const DEFAULT_MAP_SETTINGS: MapSettings = {
  mapStyle: 'mapbox://styles/mapbox/streets-v9'
};

export const MAP_STYLES = ['basic', 'streets', 'bright', 'light', 'dark', 'satellite']
  .map(style => ({
    label: startCase(style),
    value: `mapbox://styles/mapbox/${style}-v9`
  }));
