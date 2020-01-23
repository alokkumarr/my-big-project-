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
import { AnalyzeService } from '../../../analyze/services/analyze.service';
import {
  CUSTOM_DATE_PRESET_VALUE,
  AGGREGATE_TYPES_OBJ,
  getFilterValue
} from './../../../analyze/consts';
import * as forEach from 'lodash/forEach';
import moment from 'moment';
import { Observable, Subscription } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { isDSLAnalysis } from 'src/app/common/types';

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
  @Select(state => state.common.metrics) metrics$: Observable<{
    [metricId: string]: any;
  }>;
  filters$ = this.metrics$.pipe(
    map(() => {
      const queryBuilder = isDSLAnalysis(this.data.analysis)
        ? this.data.analysis.sipQuery
        : this.data.analysis.sqlBuilder;
      return isDSLAnalysis(this.data.analysis)
        ? this.generateDSLDateFilters(queryBuilder.filters)
        : queryBuilder.filters;
    })
  );
  filterCount$ = this.filters$.pipe(map(filters => (filters || []).length));
  displayNameBuilder$ = this.metrics$.pipe(
    map(metrics => metrics[this.data.analysis.semanticId]),
    tap(metric => {
      this.nameMap = this.analyzeService.calcNameMap(metric.artifacts);
    })
  );

  @ViewChild('zoomAnalysisChartContainer', { static: true })
  chartContainer: ElementRef;

  constructor(
    private _dialogRef: MatDialogRef<ZoomAnalysisComponent>,
    private analyzeService: AnalyzeService,
    @Inject(MAT_DIALOG_DATA) public data
  ) {}

  ngOnInit() {
    const sub = this.displayNameBuilder$.subscribe();
    this.subscriptions.push(sub);
    setTimeout(() => {
      // defer updating the chart so that the chart has time to initialize
      fpPipe(
        fpMap(val => {
          if (val.path === 'chart.height') {
            initialChartHeight = val.data;
          }
        })
      )(this.data.updater.getValue());
    });
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
    if (this.data.analysis.type !== 'map') {
      this.subscriptions.forEach(sub => sub.unsubscribe());
      this.data.updater.next([
        {
          path: 'chart.height',
          data: initialChartHeight
        }
      ]);
    }
  }

  generateDSLDateFilters(filters) {
    forEach(filters, filtr => {
      if (
        !filtr.isRuntimeFilter &&
        filtr.type === 'date' &&
        filtr.model &&
        filtr.model.operator === 'BTW'
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
    const columnName = this.nameMap[filter.tableName || filter.artifactsName][
      filter.columnName
    ];
    const filterName =
      filter.isAggregationFilter && filter.aggregate
        ? `${
            AGGREGATE_TYPES_OBJ[filter.aggregate].designerLabel
          }(${columnName})`
        : columnName;
    return filterName + getFilterValue(filter);
  }

  close() {
    this._dialogRef.close();
  }

  refreshTile(e) {
    return;
  }
}
