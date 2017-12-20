import { Pipe, PipeTransform } from '@angular/core';
import * as filter from 'lodash/filter';

import { PivotArea } from '../types';
import {NUMBER_TYPES} from '../../../consts';

@Pipe({
  name: 'pivotAreaFilter',
  pure: true
})
export class PivotAreaFilterPipe implements PipeTransform {
  transform(areaTypes: PivotArea, type: string): any {
    if (!areaTypes || !filter) {
      return areaTypes;
    }

    return filter(areaTypes, ({value}) => {
      if (value === 'data' &&
        !NUMBER_TYPES.includes(type)) {
        return false;
      }
      return true;
    });
  }
}
