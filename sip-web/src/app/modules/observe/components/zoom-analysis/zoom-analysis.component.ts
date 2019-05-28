import {
  Component,
  Inject,
  OnInit,
  OnDestroy,
  AfterViewInit,
  ViewChild,
  ElementRef
} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { Select } from '@ngxs/store';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';

import { Filter } from './../../../analyze/types';
import { isDSLAnalysis } from './../../../analyze/types';
import { AnalyzeService } from '../../../analyze/services/analyze.service';
import {
  NUMBER_TYPES,
  DATE_TYPES,
  CUSTOM_DATE_PRESET_VALUE,
  BETWEEN_NUMBER_FILTER_OPERATOR,
  STRING_FILTER_OPERATORS_OBJ,
  NUMBER_FILTER_OPERATORS_OBJ
} from './../../../analyze/consts';
import * as forEach from 'lodash/forEach';
import moment from 'moment';
import { Observable, Subscription } from 'rxjs';
import { map, tap } from 'rxjs/operators';

let initialChartHeight = 0;
@Component({
  selector: 'zoom-analysis',
  templateUrl: './zoom-analysis.component.html',
  styleUrls: ['./zoom-analysis.component.scss']
})
export class ZoomAnalysisComponent implements OnInit, OnDestroy, AfterViewInit {
  private subscriptions: Subscription[] = [];
  public analysisData: Array<any>;
  public nameMap;
  public filters: Filter[];
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
      this.nameMap = this.analyzeService.calcNameMap(metric.artifacts);
    })
  );

  @ViewChild('zoomAnalysisChartContainer') chartContainer: ElementRef;

  constructor(
    private _dialogRef: MatDialogRef<ZoomAnalysisComponent>,
    private analyzeService: AnalyzeService,
    @Inject(MAT_DIALOG_DATA) public data
  ) {}

  ngOnInit() {
    const sub = this.displayNameBuilder$.subscribe();
    this.subscriptions.push(sub);

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

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.data.updater.next([
      {
        path: 'chart.height',
        data: initialChartHeight
      }
    ]);
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

  getChartHeight(chartHeight) {
    return Math.max(
      chartHeight,
      this.chartContainer.nativeElement.offsetHeight > 500
        ? 500
        : this.chartContainer.nativeElement.offsetHeight
    );
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
