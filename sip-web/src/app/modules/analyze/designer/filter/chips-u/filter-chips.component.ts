import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as reduce from 'lodash/reduce';
import * as uniq from 'lodash/uniq';
import * as map from 'lodash/map';
import { Filter, Artifact, ArtifactColumn } from '../../types';
import {
  NUMBER_TYPES,
  DATE_TYPES,
  CUSTOM_DATE_PRESET_VALUE,
  BETWEEN_NUMBER_FILTER_OPERATOR,
  STRING_FILTER_OPERATORS_OBJ,
  NUMBER_FILTER_OPERATORS_OBJ
} from '../../../consts';

import { ArtifactDSL } from '../../../../../models/analysis-dsl.model';
import moment from 'moment';

export const getFilterValue = (filter: Filter) => {
  const { type } = filter;
  if (!filter.model) {
    return '';
  }

  const {
    modelValues,
    value,
    operator,
    otherValue,
    preset,
    lte,
    gte
  } = filter.model;

  if (type === 'string') {
    const operatoLabel = STRING_FILTER_OPERATORS_OBJ[operator].label;
    return `: ${operatoLabel} ${modelValues.join(', ')}`;
  } else if (NUMBER_TYPES.includes(type)) {
    const operatoLabel = NUMBER_FILTER_OPERATORS_OBJ[operator].label;
    if (operator !== BETWEEN_NUMBER_FILTER_OPERATOR.value) {
      return `: ${operatoLabel} ${value}`;
    }
    return `: ${otherValue} ${operatoLabel} ${value}`;
  } else if (DATE_TYPES.includes(type)) {
    if (preset === CUSTOM_DATE_PRESET_VALUE ) {
      return `: From ${gte} To ${lte}`;
    } else if (operator === 'BTW') {
      return `: From ${moment(value).format('YYYY-MM-DD')} to ${moment(otherValue).format('YYYY-MM-DD')}`;
    }
    return `: ${preset}`;
  }
};

@Component({
  selector: 'filter-chips-u',
  templateUrl: './filter-chips.component.html',
  styleUrls: ['./filter-chips.component.scss']
})
export class FilterChipsComponent {
  @Output() remove: EventEmitter<number> = new EventEmitter();
  @Output() removeAll: EventEmitter<null> = new EventEmitter();
  @Input() filters: Filter[];
  @Input('artifacts')
  set artifacts(artifacts: Artifact[] | ArtifactDSL[]) {
    if (!artifacts) {
      return;
    }
    this.nameMap = reduce(
      artifacts,
      (acc, artifact: Artifact | ArtifactDSL) => {
        // This is a fail safe. Metric's table names differ between columns, tables etc.
        // This is a data error from backend. We create maps for all the uniq table names
        // found in metric to accomodate everything.
        const allArtifactNames = uniq([
          (<Artifact>artifact).artifactName ||
            (<ArtifactDSL>artifact).artifactsName,
          ...map(
            (<Artifact>artifact).columns || (<ArtifactDSL>artifact).fields,
            field =>
              field.table ||
              field.tableName ||
              field.artifactName ||
              field.artifactsName
          )
        ]);
        allArtifactNames.forEach(artifactName => {
          acc[artifactName] = reduce(
            (<Artifact>artifact).columns || (<ArtifactDSL>artifact).fields,
            (accum, col: ArtifactColumn) => {
              accum[col.columnName] = col.displayName;
              return accum;
            },
            {}
          );
        });
        return acc;
      },
      {}
    );
  }
  @Input() readonly: boolean;

  public nameMap;

  getDisplayName(filter: Filter) {
    return this.nameMap[filter.tableName || filter.artifactsName][
      filter.columnName
    ];
  }

  onRemove(index) {
    this.remove.emit(index);
  }

  getFilterValue(filter: Filter) {
    return getFilterValue(filter);
  }

  onRemoveAll() {
    this.removeAll.emit();
  }
}
