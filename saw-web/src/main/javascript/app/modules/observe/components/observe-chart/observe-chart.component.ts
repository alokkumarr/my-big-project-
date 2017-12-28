import { Component, Input } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ChartService } from '../../../analyze/services/chart.service';
import { AnalyzeService } from '../../../analyze/services/analyze.service';
import { SortService } from '../../../analyze/services/sort.service';
import { FilterService } from '../../../analyze/services/filter.service';
import * as isUndefined from 'lodash/isUndefined';
import * as get from 'lodash/get';
import * as set from 'lodash/set';
import * as isEmpty from 'lodash/isEmpty';
import * as clone from 'lodash/clone';
import * as filter from 'lodash/filter';
import * as map from 'lodash/map';
import * as orderBy from 'lodash/orderBy';
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
  @Input('updater') requester: BehaviorSubject<Array<any>>;

  private chartUpdater = new BehaviorSubject([]);
  public legend: any;
  public chartOptions: any;;
  public settings: any;
  public labels: any;
  public gridData: Array<any>;
  public sorts: Array<any>;
  public filters: Array<any>;
  public isStockChart: boolean;

  constructor(public chartService: ChartService,
    public analyzeService: AnalyzeService,
    public sortService: SortService,
    public filterService: FilterService
  ) { }

  ngOnInit() {
    this.chartOptions = this.chartService.getChartConfigFor(this.analysis.chartType, { legend: this.legend });
    this.isStockChart = isUndefined(this.analysis.isStockChart) ? false : this.analysis.isStockChart;
    this.subscribeToRequester();
  }

  ngOnDestroy() {
    this.requester.unsubscribe();
  }

  /* Accept changes from parent component and pass those on to chart.
     Having separate requester and chartUpdater allows transforming
     changes coming from parent before passing them on. */
  subscribeToRequester() {
    this.requester.subscribe(data => {
      let changes = this.getChangeConfig(this.settings, this.gridData, this.labels);
      changes = changes.concat(data);
      this.chartUpdater.next(changes);
    });
  }

  ngAfterViewInit() {
    this.initChart();
  }

  initChart() {
    this.labels = {x: null, y: null, tempX: null, tempY: null};
    this.labels.tempX = this.labels.x = get(this.analysis, 'xAxis.title', null);
    this.labels.tempY = this.labels.y = get(this.analysis, 'yAxis.title', null);

    const sortFields = this.sortService.getArtifactColumns2SortFieldMapper()(this.analysis.artifacts[0].columns);
    this.sorts = this.analysis.sqlBuilder.sorts ?
      this.sortService.mapBackend2FrontendSort(this.analysis.sqlBuilder.sorts, sortFields) : [];

    this.filters = map(
      get(this.analysis, 'sqlBuilder.filters', []),
      this.filterService.backend2FrontendFilter(this.analysis.artifacts)
    );

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
    const changes = this.getChangeConfig(settings, gridData, labels);
    this.chartUpdater.next(changes);
  }

  getChangeConfig(settings, gridData, labels): Array<any> {
    if (isEmpty(gridData)) {
      /* Making sure empty data refreshes chart and shows no data there.  */
      return [{ path: 'series', data: [] }];
    }

    if (!isEmpty(this.sorts)) {
      gridData = orderBy(
        gridData,
        map(this.sorts, 'field.dataField'),
        map(this.sorts, 'order')
      );
    }

    let changes = this.chartService.dataToChangeConfig(
      this.analysis.chartType,
      settings,
      gridData,
      { labels, labelOptions: this.analysis.labelOptions, sorts: this.sorts }
    );

    changes = changes.concat(this.getLegend());
    changes = changes.concat([
      {path: 'title.text', data: this.analysis.name},
      {path: 'title.y', data: -10}
    ]);

    return changes;
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

    set(payload, 'sqlBuilder.filters', map(
      this.filters,
      this.filterService.frontend2BackendFilter()
    ));


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
    set(payload, 'sqlBuilder.sorts', this.sortService.mapFrontend2BackendSort(this.sorts));
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
