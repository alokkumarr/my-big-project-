import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
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
import { reduce } from 'lodash';

let chartGridUpdater = [];
let oldChartGridUpdater = [];
@Component({
  selector: 'zoom-analysis',
  templateUrl: './zoom-analysis.component.html',
  styleUrls: ['./zoom-analysis.component.scss']
})
export class ZoomAnalysisComponent implements OnInit, OnDestroy {
  public analysisData: Array<any>;
  public nameMap;

  constructor(
    private _dialogRef: MatDialogRef<ZoomAnalysisComponent>,
    @Inject(MAT_DIALOG_DATA) public data
  ) {}

  ngOnInit() {
    this.nameMap = reduce(
      this.data.origAnalysis.artifacts,
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

    /* By default the updater of zoom-analysis component is taking value of chart-grid component.
    Since the chart height in chart-grid is lesser as compared to zoom-analysis, so chart height was getting
    reduced. In this piece of code zoom-analysis updater (i.e. this.data.updater) is updated without 'chart.height' path.
    So that chart height in zoom-analysis component is not reduced. When user close the dialog box then again
    zoom-analysis updater (i.e. this.data.updater) is updated with chart-grid updater value.
    */
    this.data.updater.asObservable().source.forEach(val => {
      chartGridUpdater = val;
    });
    let zoomAnalysisUpdater = [];
    oldChartGridUpdater = [];
    chartGridUpdater.forEach(val => {
      oldChartGridUpdater.push(val);
      if (val.path != 'chart.height') {
        zoomAnalysisUpdater.push(val);
      }
    });
    this.data.updater.next(zoomAnalysisUpdater);
  }

  ngOnDestroy(): void {
    // Just making sure that when the dialog box is closed,
    // updater should update it's value with chart-grid updater.
    this.data.updater.next(oldChartGridUpdater);
  }

  getDisplayName(filter: Filter) {
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

  refreshTile(e) {
    return;
  }
}
