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
import * as get from 'lodash/get';

import { Filter } from './../../../analyze/types';
import { AnalyzeService } from '../../../analyze/services/analyze.service';
import {
  getFilterDisplayName
} from './../../../analyze/consts';
import { Observable, Subscription } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { isDSLAnalysis } from 'src/app/common/types';
import { DskFiltersService } from '../../../../common/services/dsk-filters.service';

let initialChartHeight = 0;
@Component({
  selector: 'zoom-analysis',
  templateUrl: './zoom-analysis.component.html',
  styleUrls: ['./zoom-analysis.component.scss']
})
export class ZoomAnalysisComponent implements OnInit, OnDestroy, AfterViewInit {
  private subscriptions: Subscription[] = [];
  public analysisData: Array<any>;
  public previewString;
  public aggregatePreview;
  public nameMap;
  @Select(state => state.common.metrics) metrics$: Observable<{
    [metricId: string]: any;
  }>;
  filters$ = this.metrics$.pipe(
    map(() => {
      const queryBuilder = isDSLAnalysis(this.data.analysis)
        ? this.data.analysis.sipQuery
        : this.data.analysis.sqlBuilder;
        this.previewString = this.datasecurityService.generatePreview(
          this.changeIndexToNames(queryBuilder.filters, 'booleanQuery', 'filters'), 'ANALYZE'
        );

        const aggregatedFilters = queryBuilder.filters.filter(option => {
          return option.isAggregationFilter === true;
        });

        this.aggregatePreview = aggregatedFilters.map(field => {
          if (field.model.operator === 'BTW') {
            return `<span ${field.isRuntimeFilter ? 'class="prompt-filter"' : ''}>${field.columnName.split('.keyword')[0]}</span> <span class="operator">${
              field.model.operator
            }</span> <span [attr.e2e]="'ffilter-model-value'">[${get(field, 'model.otherValue')} and ${get(field, 'model.value')}]</span>`;
          } else {
            return `<span ${field.isRuntimeFilter ? 'class="prompt-filter"' : ''}>${field.columnName.split('.keyword')[0]}</span> <span class="operator">${
              field.model.operator || ''
            }</span> <span [attr.e2e]="'ffilter-model-value'">[${[get(field, 'model.value')]}]</span>`;
          }
        })


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
    private datasecurityService: DskFiltersService,
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
    const flattenedFilter = this.analyzeService.flattenAndFetchFiltersChips(filters, [])
    return flattenedFilter.filter((thing, index) => {
      const _thing = JSON.stringify(thing);
      return index === flattenedFilter.findIndex(obj => {
        return JSON.stringify(obj) === _thing;
      });
    });
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
    return getFilterDisplayName(this.nameMap, filter);
  }

  close() {
    this._dialogRef.close();
  }

  refreshTile(e) {
    return;
  }

  changeIndexToNames(dskObject, source, target) {
    const convertToString = JSON.stringify(dskObject);
    const replaceIndex = convertToString.replace(/"filters":/g, '"booleanQuery":');
    const convertToJson = JSON.parse(replaceIndex);
    return convertToJson[0];
  }
}
