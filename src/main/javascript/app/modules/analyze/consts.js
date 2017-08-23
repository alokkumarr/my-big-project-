import fpGroupBy from 'lodash/fp/groupBy';
import fpPipe from 'lodash/fp/pipe';
import fpMapValues from 'lodash/fp/mapValues';

import {NUMBER_TYPES, DATE_TYPES} from '../../common/consts.js';

export const Events = {
  AnalysesRefresh: 'Analyses:Refresh'
};

export const AnalyseTypes = {
  Report: 'report',
  Chart: 'chart',
  Pivot: 'pivot'
};

export const ENTRY_MODES = {
  EDIT: 'edit',
  FORK: 'fork',
  NEW: 'new'
};

export const LAST_ANALYSES_CATEGORY_ID = 'lastAnalysesListId';

export {NUMBER_TYPES, DATE_TYPES};

export const MAX_POSSIBLE_FIELDS_OF_SAME_AREA = 5;

export const PRIVILEGES = {
  ACCESS: 'ACCESS',
  CREATE: 'CREATE',
  EXECUTE: 'EXECUTE',
  PUBLISH: 'PUBLISH',
  FORK: 'FORK',
  EDIT: 'EDIT',
  EXPORT: 'EXPORT',
  DELETE: 'DELETE',
  ALL: 'ALL'
};

export const AGGREGATE_TYPES = [{
  label: 'Total',
  value: 'sum',
  icon: 'icon-Sum'
}, {
  label: 'Average',
  value: 'avg',
  icon: 'icon-AVG'
}, {
  label: 'Mininum',
  value: 'min',
  icon: 'icon-MIN'
}, {
  label: 'Maximum',
  value: 'max',
  icon: 'icon-MAX'
}, {
  label: 'Count',
  value: 'count',
  icon: 'icon-Count'
}];

export const DEFAULT_AGGREGATE_TYPE = AGGREGATE_TYPES[0];

export const AGGREGATE_TYPES_OBJ = fpPipe(
  fpGroupBy('value'),
  fpMapValues(v => v[0])
)(AGGREGATE_TYPES);
