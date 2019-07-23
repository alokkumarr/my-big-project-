import {
  Component,
  OnInit,
  Input,
  Output,
  OnDestroy,
  AfterViewInit,
  ViewChild,
  EventEmitter
} from '@angular/core';
import * as moment from 'moment';
import * as get from 'lodash/get';
import * as isEmpty from 'lodash/isEmpty';
import * as toNumber from 'lodash/toNumber';
import * as round from 'lodash/round';
import * as find from 'lodash/find';
import * as debounce from 'lodash/debounce';

import * as defaults from 'lodash/defaults';

import { DATE_PRESETS_OBJ, BULLET_CHART_COLORS } from '../../consts';
import { ObserveService } from '../../services/observe.service';
import { GlobalFilterService } from '../../services/global-filter.service';
import { ChartComponent } from '../../../../common/components/charts/chart.component';

import { Subscription, BehaviorSubject } from 'rxjs';

@Component({
  selector: 'observe-kpi-bullet',
  templateUrl: 'observe-kpi-bullet.component.html'
})
export class ObserveKPIBulletComponent
  implements OnInit, OnDestroy, AfterViewInit {
  _kpi: any;
  _executedKPI: any;

  datePresetObj = DATE_PRESETS_OBJ;
  primaryResult: { current?: number; prior?: number; change?: string } = {};
  kpiFilterSubscription: Subscription;
  public chartUpdater = new BehaviorSubject([]);
  public requesterSubscription: Subscription;
  public kpiHeight: number;
  public kpiWidth: number;
  public kpiValue: number;
  public kpiColorPalette: string[] = [];
  public kpiTitle = '';
  public kpiSubTitle = '';

  @Input()
  set bulletKpi(data) {
    if (isEmpty(data)) {
      return;
    }
    this._kpi = data;
    this.executeKPI(this._kpi);
  }
  @Input() analysis: any;
  @Input() item: any;
  @Input() enableChartDownload: boolean;
  @Input() updater: BehaviorSubject<Array<any>>;
  @Output() onRefresh = new EventEmitter<any>();
  @ViewChild(ChartComponent) chartComponent: ChartComponent;

  constructor(
    public observe: ObserveService,
    public globalFilterService: GlobalFilterService
  ) {
    this.setKpiSize = debounce(this.setKpiSize, 10);
  }

  ngOnInit() {
    this.kpiFilterSubscription = this.globalFilterService.onApplyKPIFilter.subscribe(
      this.onFilterKPI.bind(this)
    );
    this.subscribeToRequester();
  }

  ngOnDestroy() {
    this.kpiFilterSubscription && this.kpiFilterSubscription.unsubscribe();
    this.requesterSubscription.unsubscribe();
  }

  subscribeToRequester() {
    this.requesterSubscription = this.updater.subscribe(data => {
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
    const title = find(changes, change => change.path === 'title.text');
    const subTitle = find(changes, change => change.path === 'subtitle.text');
    if (title) {
      this.kpiTitle = get(title, 'data');
    }
    if (subTitle) {
      this.kpiSubTitle = get(subTitle, 'data');
    }
    const heightChange = find(
      changes,
      change => change.path === 'chart.height'
    );
    const widthChange = find(changes, change => change.path === 'chart.width');
    this.setKpiSize(heightChange, widthChange);
    this.chartUpdater.next(changes);
  }

  setKpiSize(heightChange, widthChange) {
    this.kpiHeight = get(heightChange, 'data');
    this.kpiWidth = get(widthChange, 'data');
  }

  onFilterKPI(filterModel) {
    if (!this._kpi || !filterModel) {
      return;
    }

    if (!filterModel.preset) {
      return this.executeKPI(this._kpi);
    }

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
    if (!this._executedKPI && !this._kpi) {
      return '';
    }

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
      this.kpiValue = round(toNumber(count), 2);
      const { b1, b2, b3 } = find(BULLET_CHART_COLORS, {
        value: this.item.bullet.bulletPalette
      });
      this.kpiColorPalette = [b1, b2, b3];
      const { plotBands, seriesData } = this.observe.buildPlotBandsForBullet(
        this.item.bullet.bulletPalette,
        this.item.bullet.measure1,
        this.item.bullet.measure2,
        round(toNumber(count), 2),
        this.item.bullet.target
      );

      const serie = {
        data: seriesData
      };

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
          path: 'series[0]',
          data: serie
        }
      );

      this.reloadChart(changes);
      this.item && this.onRefresh.emit(this.item);
    });
  }
}
