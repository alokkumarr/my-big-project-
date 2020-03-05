import { Component, ViewChild, Input } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { DxDataGridComponent } from 'devextreme-angular/ui/data-grid';
import * as orderBy from 'lodash/orderBy';
import * as isEmpty from 'lodash/isEmpty';
import * as values from 'lodash/values';
import * as map from 'lodash/map';
import * as get from 'lodash/get';
import * as reverse from 'lodash/reverse';
import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import * as set from 'lodash/set';
import * as moment from 'moment';
import { HeaderProgressService } from './../../../common/services';
import {
  setReverseProperty,
  shouldReverseChart
} from './../../../common/utils/dataFlattener';

import { ChartService } from '../../services';
import {
  ArtifactColumnReport,
  AnalysisDSL,
  AnalysisChartDSL,
  isDSLAnalysis,
  ChartOptions
} from '../../types';

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

/**
 * Converts datafield notation like 'sum@@double' to human
 * friendly notation like 'sum(double)'
 *
 * Takes an array of data rows. Replaces keys in datafield
 * notation to described human friendly notation.
 *
 * @param {{ [key: string]: any }} data
 */
export const dataFieldToHuman = (dataFieldName: string) => {
  if (/\w@@\w/.test(dataFieldName)) {
    const [agg, col] = dataFieldName.split('@@');
    return `${agg}(${col})`;
  }

  return dataFieldName;
};

@Component({
  selector: 'chart-grid',
  templateUrl: 'chart-grid.component.html',
  styleUrls: ['chart-grid.component.scss']
})
export class ChartGridComponent {
  @Input() updater: BehaviorSubject<Object[]>;
  @Input() actionBus: Subject<Object[]>;
  @Input('analysis')
  set setAnalysis(analysis: AnalysisDSL) {
    this.analysis = analysis;
    this.initChartOptions(analysis);
  }
  @Input('data')
  set setData(data: any[]) {
    this._headerProgress.show();
    this.toggleToGrid = false;
    const processedData = this.reverseDataIfNeeded(
      this.analysis.sipQuery,
      data
    );
    this.updates = this.getChartUpdates(processedData, this.analysis);
    setTimeout(() => {
      // defer updating the chart so that the chart has time to initialize
      this.updater.next(this.updates);
      this.data = processedData;
      this._headerProgress.hide();
    });
  }
  @ViewChild(DxDataGridComponent, { static: false })
  dataGrid: DxDataGridComponent;

  public analysis: AnalysisDSL;
  public chartOptions: ChartOptions;
  public toggleToGrid = false;
  public chartToggleData: any;
  public updates: any;
  public data: any[];

  constructor(
    private _chartService: ChartService,
    private _headerProgress: HeaderProgressService
  ) {
    this.customizeColumns = this.customizeColumns.bind(this);
  }

  reverseDataIfNeeded(sipQuery, data) {
    if (shouldReverseChart(sipQuery)) {
      return reverse(data);
    }
    return data;
  }

  customizeColumns(columns) {
    forEach(columns, (col: ReportGridField) => {
      col.alignment = 'left';
    });
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
    const chartType =
      analysis.type === 'chart'
        ? (<AnalysisChartDSL>analysis).chartOptions.chartType
        : get(<AnalysisChartDSL>analysis, 'mapOptions.mapType');
    this.chartOptions = this._chartService.getChartConfigFor(
      isDSLAnalysis(analysis) ? chartType : analysis.chartType,
      { chart, legend }
    );
    this.chartOptions = setReverseProperty(
      this.chartOptions,
      analysis.sipQuery
    );
  }

  fetchColumnData(axisName, value) {
    let alias = axisName;
    const columns = this.analysis.sipQuery.artifacts[0].fields;
    const isDataField = /@@/.test(axisName);
    forEach(columns, column => {
      const isMatchingColumn = isDataField
        ? axisName === column.dataField
        : axisName === column.name ||
          axisName === column.columnName.split('.keyword')[0];
      if (isMatchingColumn) {
        const columnFormat =
          column.type === 'date' ? column.dateFormat : column.format;

        /* If this is a data field, make it look user friendly */
        alias =
          column.alias ||
          (isDataField
            ? dataFieldToHuman(column.dataField)
            : column.displayName);

        const {
          dateFormat,
          momentFormat
        } = this._chartService.getMomentDateFormat(
          columnFormat,
          get(<AnalysisChartDSL>this.analysis, 'chartOptions.chartType') ===
            'comparison'
            ? column.groupInterval
            : null
        );
        value =
          column.type === 'date'
            ? moment
                .utc(value, dateFormat)
                .format(
                  momentFormat ||
                    (columnFormat === 'MMM d YYYY'
                      ? 'MMM DD YYYY'
                      : columnFormat === 'MMMM d YYYY, h:mm:ss a'
                      ? 'MMMM DD YYYY, h:mm:ss a'
                      : columnFormat)
                )
            : value;
        if (
          value &&
          (column.aggregate === 'percentage' || column.aggregate === 'avg')
        ) {
          value =
            (parseFloat(value) || 0).toFixed(2) +
            (column.aggregate === 'percentage' ? '%' : '');
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
      ).dateFormat;
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
    const chartData = orderedData || data;

    this.chartToggleData = this.trimKeyword(chartData);
    const updates = [
      ...this._chartService.dataToChangeConfig(
        analysis.type === 'chart'
          ? (<AnalysisChartDSL>analysis).chartOptions.chartType
          : get(<AnalysisChartDSL>analysis, 'mapOptions.mapType'),
        analysis.sipQuery,
        chartData,
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
    // Setting the series color if any present in updater obj.
    const seriesData = find(updates, ({ path }) => {
      return path === 'series';
    });
    const { fields } = analysis.sipQuery.artifacts[0];
    forEach(fields, serie => {
      const matchedObj = find(seriesData.data, ({ dataType, aggregate }) => {
        return dataType === serie.type && aggregate === serie.aggregate;
      });
      set(matchedObj, 'color', serie.seriesColor);
    });
    return updates;
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
          chartHeight =
            val.data -
            (get(<AnalysisChartDSL>this.analysis, 'chartOptions.chartType') ===
            'comparison'
              ? 24
              : 0);
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
