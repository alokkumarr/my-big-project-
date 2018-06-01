import {
  Component,
  OnInit,
  Input,
  Output,
  OnDestroy,
  ViewChild,
  EventEmitter
} from '@angular/core';
import * as moment from 'moment';
import * as get from 'lodash/get';
import * as isEmpty from 'lodash/isEmpty';
import * as toNumber from 'lodash/toNumber';
import * as defaults from 'lodash/defaults';

import { DATE_PRESETS_OBJ, BULLET_CHART_OPTIONS } from '../../consts';
import { ObserveService } from '../../services/observe.service';
import { DashboardService } from '../../services/dashboard.service';
import { ChartComponent } from '../../../../common/components/charts/chart.component';

import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

const template = require('./observe-kpi-bullet.component.html');
require('./observe-kpi-bullet.component.scss');

@Component({
  selector: 'observe-kpi-bullet',
  template
})
export class ObserveKPIBulletComponent implements OnInit, OnDestroy {
  _kpi: any;
  _executedKPI: any;

  datePresetObj = DATE_PRESETS_OBJ;
  primaryResult: { current?: number; prior?: number; change?: string } = {};
  kpiFilterSubscription: Subscription;
  chartOptions = BULLET_CHART_OPTIONS;
  private chartUpdater = new BehaviorSubject([]);
  private requesterSubscription: Subscription;

  @Input()
  set bulletKpi(data) {
    if (isEmpty(data)) return;
    this._kpi = data;
    this.executeKPI(this._kpi);
  }
  @Input() analysis: any;
  @Input() item: any;
  @Input() enableChartDownload: boolean;
  @Input('updater') requester: BehaviorSubject<Array<any>>;
  @Output() onRefresh = new EventEmitter<any>();
  @ViewChild(ChartComponent) chartComponent: ChartComponent;

  constructor(
    private observe: ObserveService,
    private dashboardService: DashboardService
  ) {}

  ngOnInit() {
    this.kpiFilterSubscription = this.dashboardService.onFilterKPI.subscribe(
      this.onFilterKPI.bind(this)
    );
    this.subscribeToRequester();
  }

  ngOnDestroy() {
    this.kpiFilterSubscription && this.kpiFilterSubscription.unsubscribe();
    this.requesterSubscription.unsubscribe();
  }

  subscribeToRequester() {
    this.requesterSubscription = this.requester.subscribe(data => {
      if (!isEmpty(data)) {
        this.reloadChart(data);
      }
    });
  }

  ngAfterViewInit() {
    this.initChart();
  }

  initChart() {
    this.item && this.onRefresh.emit(this.item);
  }

  reloadChart(changes) {
    this.chartUpdater.next(changes);
  }

  onFilterKPI(filterModel) {
    if (!this._kpi || !filterModel) return;

    if (!filterModel.preset) return this.executeKPI(this._kpi);

    const filter = defaults(
      {},
      {
        model: filterModel
      },
      get(this._kpi, 'filters.0')
    );
    const kpi = defaults({}, { filters: [filter] }, this._kpi);

    return this.executeKPI(kpi);
  }

  filterLabel() {
    if (!this._executedKPI && !this._kpi) return '';

    const preset = get(
      this._executedKPI || this._kpi,
      'filters.0.model.preset'
    );
    const filter = get(this.datePresetObj, `${preset}.label`);
    if (filter === 'Custom') {
      const gte = moment(
        get(this._executedKPI || this._kpi, 'filters.0.model.gte'),
        'YYYY-MM-DD HH:mm:ss'
      ).format('YYYY/MM/DD');
      const lte = moment(
        get(this._executedKPI || this._kpi, 'filters.0.model.lte'),
        'YYYY-MM-DD HH:mm:ss'
      ).format('YYYY/MM/DD');
      return `${gte} - ${lte}`;
    } else {
      return filter;
    }
  }

  executeKPI(kpi, changes = []) {
    this._executedKPI = kpi;
    const dataFieldName = get(kpi, 'dataFields.0.name');
    const kpiTitle = get(kpi, 'name');
    const kpiFilter = this.filterLabel();
    const primaryAggregate = get(kpi, 'dataFields.0.aggregate', []);
    const categories = get(kpi, 'dataFields.0.displayName', '');
    this.observe.executeKPI(kpi).subscribe(res => {
      const count: number = get(
        res,
        `data.current.${dataFieldName}._${primaryAggregate}`
      );
      const { plotBands, seriesData } = this.observe.buildPlotBandsForBullet(
        this.item.bullet.bulletPalette,
        this.item.bullet.measure1,
        this.item.bullet.measure2,
        toNumber(count),
        this.item.bullet.target
      );
      changes.push(
        {
          path: 'title.text',
          data: kpiTitle
        },
        {
          path: 'subtitle.text',
          data: kpiFilter
        },
        {
          path: 'xAxis.categories',
          data: [categories]
        },
        {
          path: 'yAxis.plotBands',
          data: plotBands
        },
        {
          path: 'series[0].data',
          data: seriesData
        }
      );
      this.reloadChart(changes);
    });
  }
}
