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
import { AnalysisChart, ArtifactColumnReport } from '../../types';

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
  set setAnalysis(analysis: AnalysisChart) {
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
  }
  @ViewChild(DxDataGridComponent) dataGrid: DxDataGridComponent;

  public analysis: AnalysisChart;
  public chartOptions: Object;
  public toggleToGrid = false;
  public chartToggleData: any;
  public updates: any;

  constructor(private _chartService: ChartService) {
    this.customizeColumns = this.customizeColumns.bind(this);
  }

  ngOnInit() { }

  customizeColumns(columns) {
    forEach(columns, (col: ReportGridField) => {
      col.alignment = 'left';
    });
  }

  initChartOptions(analysis) {
    this.toggleToGrid = false;
    const { LEGEND_POSITIONING, LAYOUT_POSITIONS } = this._chartService;
    const legend = {
      align: get(analysis, 'legend.align', 'right'),
      layout: get(analysis, 'legend.layout', 'vertical'),
      options: {
        align: values(LEGEND_POSITIONING),
        layout: values(LAYOUT_POSITIONS)
      }
    };
    const chart = {
      height: 580
    };
    this.chartOptions = this._chartService.getChartConfigFor(
      analysis.chartType,
      { chart, legend }
    );
  }

  fetchColumnData(axisName, value) {
    let aliasName = axisName;
    forEach(this.analysis.artifacts[0].columns, column => {
      if (axisName === column.name) {
        aliasName = column.aliasName || column.displayName;
        value =
          column.type === 'date'
            ? moment
                .utc(value)
                .format(
                  column.dateFormat === 'MMM d YYYY'
                    ? 'MMM DD YYYY'
                    : column.dateFormat === 'MMMM d YYYY, h:mm:ss a'
                      ? 'MMMM DD YYYY, h:mm:ss a'
                      : column.dateFormat
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
    return { aliasName, value };
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
          obj[trimKey.aliasName] = trimKey.value;
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

  getChartUpdates(data, analysis) {
    const settings = this._chartService.fillSettings(
      analysis.artifacts,
      analysis
    );
    const sorts = analysis.sqlBuilder.sorts;
    const labels = {
      x: get(analysis, 'xAxis.title', null),
      y: get(analysis, 'yAxis.title', null)
    };
    let orderedData;
    if (!isEmpty(sorts)) {
      orderedData = orderBy(
        data,
        map(sorts, 'columnName'),
        map(sorts, 'order')
      );
    }

    this.chartToggleData = this.trimKeyword(data);

    return [
      ...this._chartService.dataToChangeConfig(
        analysis.chartType,
        settings,
        analysis.sqlBuilder,
        orderedData || data,
        { labels, labelOptions: analysis.labelOptions, sorts }
      ),
      { path: 'title.exportFilename', data: analysis.name },
      { path: 'chart.inverted', data: analysis.isInverted }
    ];
  }

  viewToggle(value) {
    if (!value) {
      this.updater.next(this.updates);
    }
    this.toggleToGrid = value;
  }
}
