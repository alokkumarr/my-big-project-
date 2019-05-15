import { Component, OnInit, ViewChild, Input } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { DxDataGridComponent } from 'devextreme-angular/ui/data-grid';
import * as orderBy from 'lodash/orderBy';
import * as isEmpty from 'lodash/isEmpty';
import * as values from 'lodash/values';
import * as map from 'lodash/map';
import * as get from 'lodash/get';
import * as forEach from 'lodash/forEach';
import * as moment from 'moment';

import { ChartService } from '../../services';
import {
  ArtifactColumnReport,
  AnalysisDSL,
  SqlBuilderChart,
  AnalysisChartDSL
} from '../../types';
import { isDSLAnalysis } from 'src/app/modules/analyze/types';

interface ReportGridField {
  caption: string;
  dataField: string;
  dataType: string;
  type: string;
  visibleIndex: number;
  payload: ArtifactColumnReport;
  visible: boolean;
  allowSorting?: boolean;
  alignment?: 'center' | 'left' | 'right';
  format?: string | object;
  sortOrder?: 'asc' | 'desc';
  sortIndex?: number;
  changeColumnProp: Function;
  headerCellTemplate: string;
}

@Component({
  selector: 'chart-grid',
  templateUrl: 'chart-grid.component.html',
  styleUrls: ['chart-grid.component.scss']
})
export class ChartGridComponent implements OnInit {
  @Input() updater: BehaviorSubject<Object[]>;
  @Input('analysis')
  set setAnalysis(analysis: AnalysisDSL) {
    this.analysis = analysis;
    this.initChartOptions(analysis);
  }
  @Input('data')
  set setData(data: any[]) {
    this.toggleToGrid = false;
    this.updates = this.getChartUpdates(data, this.analysis);
    setTimeout(() => {
      // defer updating the chart so that the chart has time to initialize
      this.updater.next(this.updates);
    });
    this.data = data;
  }
  @ViewChild(DxDataGridComponent) dataGrid: DxDataGridComponent;

  public analysis: AnalysisDSL;
  public chartOptions: Object;
  public toggleToGrid = false;
  public chartToggleData: any;
  public updates: any;
  public data: any[];

  constructor(private _chartService: ChartService) {
    this.customizeColumns = this.customizeColumns.bind(this);
  }

  ngOnInit() {}

  customizeColumns(columns) {
    forEach(columns, (col: ReportGridField) => {
      col.alignment = 'left';
    });
  }

  /**
   * Converts sipQuery to sqlBuilder like object for use in chart service.
   * This is a non-ideal work-around made until we can locate all the places
   * we need to change.
   *
   * @param {*} queryOrBuilder
   * @returns {SqlBuilderChart}
   * @memberof DesignerChartComponent
   */
  sipQueryToSQLBuilderFields(queryOrBuilder): SqlBuilderChart {
    if (queryOrBuilder.nodeFields || queryOrBuilder.dataFields) {
      return queryOrBuilder;
    }

    const builderLike: SqlBuilderChart = {
      dataFields: [],
      nodeFields: [],
      filters: queryOrBuilder.filters,
      sorts: queryOrBuilder.sorts,
      orderByColumns: queryOrBuilder.orderByColumns,
      booleanCriteria: queryOrBuilder.booleanCriteria
    };

    (queryOrBuilder.artifacts || []).forEach(table => {
      (table.fields || []).forEach(column => {
        if (['y', 'z'].includes(column.area)) {
          builderLike.dataFields.push(column);
        } else {
          builderLike.nodeFields.push(column);
        }
      });
    });

    return builderLike;
  }

  initChartOptions(analysis) {
    this.toggleToGrid = false;
    const { LEGEND_POSITIONING, LAYOUT_POSITIONS } = this._chartService;
    const legend = {
      align: get(
        analysis,
        isDSLAnalysis(analysis) ? 'chartOptions.legend.align' : 'legend.align',
        'right'
      ),
      layout: get(
        analysis,
        isDSLAnalysis(analysis)
          ? 'chartOptions.legend.layout'
          : 'legend.layout',
        'vertical'
      ),
      options: {
        align: values(LEGEND_POSITIONING),
        layout: values(LAYOUT_POSITIONS)
      }
    };
    const chart = {
      height: 580
    };
    this.chartOptions = this._chartService.getChartConfigFor(
      isDSLAnalysis(analysis)
        ? (<AnalysisChartDSL>analysis).chartOptions.chartType
        : analysis.chartType,
      { chart, legend }
    );
  }

  fetchColumnData(axisName, value) {
    let alias = axisName;
    const columns = this.analysis.sipQuery.artifacts[0].fields;
    forEach(columns, column => {
      if (axisName === column.name) {
        const columnFormat =
          column.type === 'date' ? column.dateFormat : column.format;
        alias = column.alias || column.displayName;
        value =
          column.type === 'date'
            ? moment
            .utc(
              value,
              this._chartService.getMomentDateFormat(columnFormat)
            )
            .format(
              columnFormat === 'MMM d YYYY'
                ? 'MMM DD YYYY'
                : columnFormat === 'MMMM d YYYY, h:mm:ss a'
                ? 'MMMM DD YYYY, h:mm:ss a'
                : columnFormat
            )
            : value;
        if (
          value &&
          (column.aggregate === 'percentage' || column.aggregate === 'avg')
        ) {
          value =
            value.toFixed(2) + (column.aggregate === 'percentage' ? '%' : '');
        }
        value = value === 'Undefined' ? '' : value;
      }
    });
    return { alias, value };
  }

  trimKeyword(data) {
    if (!data) {
      return;
    }
    const trimData = map(data, row => {
      const obj = {};
      for (const key in row) {
        if (row.hasOwnProperty(key)) {
          const trimKey = this.fetchColumnData(key.split('.')[0], row[key]);
          obj[trimKey.alias] = trimKey.value;
        }
      }
      return obj;
    });
    return trimData;
  }

  exportGridToExcel() {
    if (!isEmpty(this.chartToggleData)) {
      this.dataGrid.instance.exportToExcel(false);
    }
  }

  get chartTitle() {
    return get(this.analysis, 'chartOptions.chartTitle') || this.analysis.name;
  }

  /**
   * Returns a mapping of columnNames to date formats.
   * Used to parse date data to make sure we sort correctly.
   * Date formats are needed, because some formats can't be parsed
   * by moment correctly without explicit format string.
   *
   * @param {AnalysisDSL} analysis
   * @returns {{ [columnName: string]: string }}
   * @memberof ChartGridComponent
   */
  getDateFormats(analysis: AnalysisDSL): { [columnName: string]: string } {
    return analysis.sipQuery.artifacts[0].fields.reduce((res, field) => {
      if (field.type !== 'date') {
        return res;
      }
      res[field.columnName] = this._chartService.getMomentDateFormat(
        field.dateFormat
      );
      return res;
    }, {});
  }

  getChartUpdates(data, analysis: AnalysisDSL) {
    const sorts = analysis.sipQuery.sorts;
    const dateFormats = this.getDateFormats(analysis);
    const labels = {
      x: get(analysis, 'xAxis.title', null),
      y: get(analysis, 'yAxis.title', null)
    };
    let orderedData;
    if (!isEmpty(sorts)) {
      orderedData = orderBy(
        data,
        map(sorts, sort =>
          sort.type === 'date'
            ? row =>
              /* If the sort is for date column, parse dates to timestamp to make sure sorting is correct */
              moment(
                row[sort.columnName],
                dateFormats[sort.columnName]
              ).valueOf()
            : /* Otherwise, sort normally */
            row => row[sort.columnName]
        ),
        map(sorts, 'order')
      );
    }

    this.chartToggleData = this.trimKeyword(orderedData);

    return [
      ...this._chartService.dataToChangeConfig(
        get(analysis, 'chartOptions.chartType'),
        analysis.sipQuery,
        orderedData || data,
        {
          labels,
          labelOptions: get(analysis, 'chartOptions.labelOptions'),
          sorts
        }
      ),
      { path: 'title.exportFilename', data: analysis.name },
      {
        path: 'chart.inverted',
        data: get(analysis, 'chartOptions.isInverted')
      },
      {
        path: 'chart.height',
        data: this.getChartHeight()
      }
    ];
  }

  getChartHeight() {
    let parentUpdater = [];

    this.updater.asObservable().source.forEach(val => {
      parentUpdater = val;
    });
    let chartHeight = 400; // Default chart height for 'execute-chart-view' and 'zoom-analysis' components.
    if (parentUpdater.length > 0) {
      parentUpdater.forEach(val => {
        if (val.path === 'chart.height') {
          chartHeight = val.data;
        }
      });
    }
    return chartHeight;
  }

  viewToggle(value) {
    if (!value) {
      this.updater.next(this.updates);
    }
    this.toggleToGrid = value;
  }
}
