import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Select } from '@ngxs/store';

import { Filter, Artifact, ArtifactColumn } from './../../../analyze/types';
import { isDSLAnalysis } from './../../../analyze/types';
import {
  NUMBER_TYPES,
  DATE_TYPES,
  CUSTOM_DATE_PRESET_VALUE,
  BETWEEN_NUMBER_FILTER_OPERATOR,
  STRING_FILTER_OPERATORS_OBJ,
  NUMBER_FILTER_OPERATORS_OBJ
} from './../../../analyze/consts';
import { reduce } from 'lodash';
import * as forEach from 'lodash/forEach';
import moment from 'moment';
import { Observable, Subscription } from 'rxjs';
import { map, tap } from 'rxjs/operators';

@Component({
  selector: 'zoom-analysis',
  templateUrl: './zoom-analysis.component.html',
  styleUrls: ['./zoom-analysis.component.scss']
})
export class ZoomAnalysisComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];
  public analysisData: Array<any>;
  public nameMap;
  public filters: Filter;
  @Select(state => state.common.metrics) metrics$: Observable<{
    [metricId: string]: any;
  }>;
  displayNameBuilder$ = this.metrics$.pipe(
    map(metrics => metrics[this.data.analysis.semanticId]),
    tap(metric => {
      const queryBuilder = isDSLAnalysis(this.data.analysis)
        ? this.data.analysis.sipQuery
        : this.data.analysis.sqlBuilder;
      this.filters = isDSLAnalysis(this.data.analysis)
        ? this.generateDSLDateFilters(queryBuilder.filters)
        : queryBuilder.filters;
      this.nameMap = reduce(
        metric.artifacts,
        (acc, artifact: Artifact) => {
          acc[artifact.artifactName || artifact['artifactsName']] = reduce(
            artifact.columns || artifact['fields'],
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
    })
  );

  constructor(
    private _dialogRef: MatDialogRef<ZoomAnalysisComponent>,
    @Inject(MAT_DIALOG_DATA) public data
  ) {}

  ngOnInit() {
    const sub = this.displayNameBuilder$.subscribe();
    this.subscriptions.push(sub);
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  generateDSLDateFilters(filters) {
    forEach(filters, filtr => {
      if (
        !filtr.isRuntimeFilter &&
        (filtr.type === 'date' && filtr.model.operator === 'BTW')
      ) {
        filtr.model.gte = moment(filtr.model.value).format('YYYY-MM-DD');
        filtr.model.lte = moment(filtr.model.otherValue).format('YYYY-MM-DD');
        filtr.model.preset = CUSTOM_DATE_PRESET_VALUE;
      }
    });
    return filters;
  }

  getDisplayName(filter: Filter) {
    return this.nameMap[filter.tableName || filter.artifactsName][
      filter.columnName
    ];
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

  refreshTile(e) {
    return;
  }
}
