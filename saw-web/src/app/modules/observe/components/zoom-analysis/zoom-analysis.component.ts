import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import { Filter, Artifact, ArtifactColumn } from './../../../analyze/types';
import {
  NUMBER_TYPES,
  DATE_TYPES,
  CUSTOM_DATE_PRESET_VALUE,
  BETWEEN_NUMBER_FILTER_OPERATOR,
  STRING_FILTER_OPERATORS_OBJ,
  NUMBER_FILTER_OPERATORS_OBJ
} from './../../../analyze/consts';
import * as reduce from 'lodash/reduce';

@Component({
  selector: 'zoom-analysis',
  templateUrl: './zoom-analysis.component.html',
  styleUrls: ['./zoom-analysis.component.scss']
})

export class ZoomAnalysisComponent implements OnInit {
  public analysisData: Array<any>;
  public nameMap;

  constructor(
    private _dialogRef: MatDialogRef<ZoomAnalysisComponent>,
    @Inject(MAT_DIALOG_DATA) public data
  ) {}

  ngOnInit() {
    this.nameMap = reduce(
      this.data.analysis.artifacts,
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

  getDisplayName(filter: Filter) {
    if (filter.isGlobalFilter) {
      console.log(filter);
    }

    return this.nameMap[filter.tableName][filter.columnName];
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

  close() {
    this._dialogRef.close();
  }
}
