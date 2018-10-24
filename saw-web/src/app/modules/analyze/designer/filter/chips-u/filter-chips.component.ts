import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as reduce from 'lodash/reduce';
import { Filter, Artifact, ArtifactColumn } from '../../types';
import {
  NUMBER_TYPES,
  DATE_TYPES,
  CUSTOM_DATE_PRESET_VALUE,
  BETWEEN_NUMBER_FILTER_OPERATOR,
  STRING_FILTER_OPERATORS_OBJ,
  NUMBER_FILTER_OPERATORS_OBJ
} from '../../../consts';

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
  set artifacts(artifacts: Artifact[]) {
    if (!artifacts) {
      return;
    }
    this.nameMap = reduce(
      artifacts,
      (acc, artifact: Artifact) => {
        acc[artifact.artifactName] = reduce(
          artifact.columns,
          (accum, col: ArtifactColumn) => {
            accum[col.columnName] = col.displayName;
            return accum;
          },
          {}
        );
        return acc;
      },
      {}
    );
  }
  @Input() readonly: boolean;

  public nameMap;

  getDisplayName(filter: Filter) {
    return this.nameMap[filter.tableName][filter.columnName];
  }

  onRemove(index) {
    this.remove.emit(index);
  }

  getFilterValue(filter: Filter) {
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
      if (preset === CUSTOM_DATE_PRESET_VALUE) {
        return `: From ${gte} To ${lte}`;
      }
      return `: ${preset}`;
    }
  }

  onRemoveAll() {
    this.removeAll.emit();
  }
}
