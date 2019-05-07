import {
  Component,
  Inject,
  OnInit,
  OnDestroy,
  ViewChild,
  ElementRef
} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';

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

let initialChartHeight = 0;
@Component({
  selector: 'zoom-analysis',
  templateUrl: './zoom-analysis.component.html',
  styleUrls: ['./zoom-analysis.component.scss']
})
export class ZoomAnalysisComponent implements OnInit, OnDestroy {
  public analysisData: Array<any>;
  public nameMap;

  @ViewChild('zoomAnalysisChartContainer') chartContainer: ElementRef;

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

    fpPipe(
      fpMap(val => {
        if (val.path === 'chart.height') {
          initialChartHeight = val.data;
        }
      })
    )(this.data.updater.getValue());

    // map-chart-viewer component is floating to left
    // When analysis is loaded for the fisrt time.
    // Due to which updating the map-chart-viewer height here.
    if (this.data.analysis.type === 'map') {
      this.data.updater.next([
        {
          path: 'chart.height',
          data: 500
        }
      ]);
    }
  }

  ngAfterViewInit(): void {
    if (this.data.analysis.type !== 'map') {
      setTimeout(() => {
        this.data.updater.next([
          {
            path: 'chart.height',
            data: this.getChartHeight(initialChartHeight)
          }
        ]);
      });
    }
  }

  ngOnDestroy(): void {
    this.data.updater.next([
      {
        path: 'chart.height',
        data: initialChartHeight
      }
    ]);
  }

  getChartHeight(chartHeight) {
    return Math.max(
      chartHeight,
      this.chartContainer.nativeElement.offsetHeight > 500
        ? 500
        : this.chartContainer.nativeElement.offsetHeight
    );
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
