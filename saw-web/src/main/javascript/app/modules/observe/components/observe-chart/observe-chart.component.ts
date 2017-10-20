import { Component, Input } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ChartService } from '../../../analyze/services/chart.service';
import { AnalyzeService } from '../../../analyze/services/analyze.service';

import * as get from 'lodash/get';
import * as set from 'lodash/set';
import * as map from 'lodash/map';
import * as values from 'lodash/values';
import * as isEmpty from 'lodash/isEmpty';
import * as clone from 'lodash/clone';
import * as filter from 'lodash/filter';
import * as find from 'lodash/find';
import * as forEach from 'lodash/forEach';
import * as remove from 'lodash/remove';
import * as concat from 'lodash/concat';

import { NUMBER_TYPES } from '../../../analyze/consts';

const template = require('./observe-chart.component.html');

@Component({
  selector: 'observe-chart',
  template
})
export class ObserveChartComponent {
  @Input() analysis: any;
  @Input('updater') chartUpdater: BehaviorSubject<Array<any>>;

  public legend: any;
  public chartOptions: any;;
  public settings: any;
  public labels: any;
  public gridData: Array<any>;

  constructor(public chartService: ChartService, public analyzeService: AnalyzeService) {
  }

  ngOnInit() {
    this.chartOptions = this.chartService.getChartConfigFor(this.analysis.chartType, { legend: this.legend });
  }

  ngAfterViewInit() {
    // this.chartUpdater.next([{ 'path': 'series', 'data': [{ 'name': 'Total Failed Bytes', 'data': [{ 'name': 'ANDROID', 'y': 31738882905685, 'drilldown': 'ANDROID' }, { 'name': 'UNKNOWN', 'y': 18794631884548, 'drilldown': 'UNKNOWN' }, { 'name': 'IOS', 'y': 30719183764427, 'drilldown': 'IOS' }, { 'name': 'WP8.1', 'y': 18555545758, 'drilldown': 'WP8.1' }, { 'name': 'BLACKBERRY', 'y': 17692721302, 'drilldown': 'BLACKBERRY' }], 'dataLabels': { 'enabled': true } }] }]);
    this.initChart();
  }

  initChart() {
    this.labels = {x: null, y: null, tempX: null, tempY: null};
    this.labels.tempX = this.labels.x = get(this.analysis, 'xAxis.title', null);
    this.labels.tempY = this.labels.y = get(this.analysis, 'yAxis.title', null);

    this.legend = {
      align: get(this.analysis, 'legend.align'),
      layout: get(this.analysis, 'legend.layout')
    };

    this.settings = this.chartService.fillSettings(this.analysis.artifacts, this.analysis);
    this.chartService.updateAnalysisModel(this.analysis);

    this.onRefreshData().then(data => {
      this.gridData = data;
      this.reloadChart(this.settings, this.gridData, this.labels);
    });
  }

  isNodeField(field) {
    return field && (!NUMBER_TYPES.includes(field.type) || field.checked === 'x');
  }

  isDataField(field) {
    return field && NUMBER_TYPES.includes(field.type) && field.checked !== 'x';
  }

  reloadChart(settings, gridData, labels) {
    if (isEmpty(gridData)) {
      /* Making sure empty data refreshes chart and shows no data there.  */
      this.chartUpdater.next([{ path: 'series', data: [] }]);
      return;
    }

    const changes = this.chartService.dataToChangeConfig(
      this.analysis.chartType,
      settings,
      gridData,
      { labels, labelOptions: this.analysis.labelOptions, sorts: [] }
    );

    changes.concat(this.getLegend());
    this.chartUpdater.next(changes);
  }

  onRefreshData() {
    const payload = this.generatePayload(this.analysis);
    return this.analyzeService.getDataBySettings(payload).then(({ data }) => {
      const parsedData = this.chartService.parseData(data, payload.sqlBuilder);
      return parsedData || [];
    });
  }

  generatePayload(source) {
    const payload = clone(source);

    set(payload, 'sqlBuilder.filters', []);

    const g = find(this.settings.groupBy, g => g.checked === 'g');
    const x = find(this.settings.xaxis, x => x.checked === 'x');
    const y = filter(this.settings.yaxis, y => y.checked === 'y');
    const z = find(this.settings.zaxis, z => z.checked === 'z');

    const allFields = [g, x, ...y, z];

    let nodeFields = filter(allFields, this.isNodeField);
    const dataFields = filter(allFields, this.isDataField);

    if (payload.chartType === 'scatter') {
      const xFields = remove(dataFields, ({ checked }) => checked === 'x');
      nodeFields = concat(xFields, nodeFields);
    }

    forEach(dataFields, field => {
      if (!field.aggregate) {
        field.aggregate = 'sum';
      }
    });

    set(payload, 'sqlBuilder.dataFields', dataFields);
    set(payload, 'sqlBuilder.nodeFields', nodeFields);

    delete payload.supports;
    set(payload, 'sqlBuilder.sorts', []);
    set(payload, 'sqlBuilder.booleanCriteria', this.analysis.sqlBuilder.booleanCriteria);
    set(payload, 'xAxis', { title: this.labels.x });
    set(payload, 'yAxis', { title: this.labels.y });
    set(payload, 'legend', {
      align: this.legend.align,
      layout: this.legend.layout
    });

    return payload;
  }

  getLegend(): Array<any> {
    const align = this.chartService.LEGEND_POSITIONING[this.legend.align];
    const layout = this.chartService.LAYOUT_POSITIONS[this.legend.layout];

    if (!align || !layout) {
      return [];
    }

    return ([
      {
        path: 'legend.align',
        data: align.align
      },
      {
        path: 'legend.verticalAlign',
        data: align.verticalAlign
      },
      {
        path: 'legend.layout',
        data: layout.layout
      }
    ]);
  }
}
