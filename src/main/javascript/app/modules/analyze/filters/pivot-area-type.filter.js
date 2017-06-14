import filter from 'lodash/filter';

import {NUMBER_TYPES} from '../consts';

export function pivotAreaTypeFilter() {
  return (areaTypes, type) => {
    return filter(areaTypes, areaType => {
      if (areaType.value === 'data' &&
        !NUMBER_TYPES.includes(type)) {
        return false;
      }
      return true;
    });
  };
}
