import { Pipe, PipeTransform } from '@angular/core';
import * as clone from 'lodash/clone';
import * as split from 'lodash/split';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';
import * as fpMapKeys from 'lodash/fp/mapKeys';
import * as fpFilter from 'lodash/fp/filter';

import {
  ArtifactColumns,
  ArtifactColumn,
  ArtifactColumnFilter
} from '../types';
import {
  NUMBER_TYPES,
  DATE_TYPES
} from '../../../consts';

const ARTIFACT_COLUMN_2_PIVOT_FIELD = {
  displayName: 'caption',
  columnName: 'dataField',
  aggregate: 'summaryType'
};

@Pipe({
  name: 'artifactColumns2PivotFields',
  pure: true
})
export class ArtifactColumns2PivotFieldsPipe implements PipeTransform {
  transform(items: ArtifactColumns): any {
    if (!items) {
      return items;
    }
    return fpPipe(
      fpFilter(field => field.checked && field.area),
      fpMap((artifactColumn) => {
        const cloned = clone(artifactColumn);

        if (NUMBER_TYPES.includes(cloned.type)) {
          cloned.dataType = 'number';
          cloned.format = {
            type: 'fixedPoint',
            precision: 2
          };
        } else {
          cloned.dataType = cloned.type;
        }

        if (cloned.type === 'string') {
          cloned.columnName = split(cloned.columnName, '.')[0];
        }

        return cloned;
      }),
      fpMap(fpMapKeys(key => {
        const newKey = ARTIFACT_COLUMN_2_PIVOT_FIELD[key];
        return newKey || key;
      }))
    )(items);
  }
}
