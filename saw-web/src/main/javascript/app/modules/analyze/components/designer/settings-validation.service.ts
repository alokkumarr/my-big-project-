import { Injectable } from '@angular/core';
import * as groupBy from 'lodash/groupBy';
import * as forEach from 'lodash/forEach';

import {
  ArtifactColumns
} from './types';
import {
  NUMBER_TYPES
} from '../../consts';

const MAX_POSSIBLE_FIELDS_OF_SAME_AREA = 5;

@Injectable()
export class SettingsValidationService {


  checkValidPivotStates(artifactColumns: ArtifactColumns) {
    const grouped = groupBy(artifactColumns, 'area');
    let valid = true;
    const errors = [];
    const interpolationValues = {
      fieldNr: MAX_POSSIBLE_FIELDS_OF_SAME_AREA,
      area: null
    };

    if (grouped.column && grouped.column.length > MAX_POSSIBLE_FIELDS_OF_SAME_AREA) {
      errors[0] = 'ERROR_PIVOT_MAX_FIELDS';
      interpolationValues.area = 'column';
      valid = false;
    }
    if (grouped.row && grouped.row.length > MAX_POSSIBLE_FIELDS_OF_SAME_AREA) {
      errors[0] = 'ERROR_PIVOT_MAX_FIELDS';
      interpolationValues.area = 'row';
      valid = false;
    }
    if (grouped.data && grouped.data.length > MAX_POSSIBLE_FIELDS_OF_SAME_AREA) {
      errors[0] = 'ERROR_PIVOT_MAX_FIELDS';
      interpolationValues.area = 'data';
      valid = false;
    }

    forEach(grouped.data, dataColumn => {
      if (!NUMBER_TYPES.includes(dataColumn.type)) {
        errors[1] = 'ERROR_PIVOT_DATA_FIELD';
        valid = false;
      }
    });

    if (!valid) {
      console.log('interpoaltions', interpolationValues);
    }

    return valid;
  }
}
