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
import { MatDialog, MatDialogConfig } from '@angular/material';
import * as get from 'lodash/get';
import * as isEmpty from 'lodash/isEmpty';
import * as toNumber from 'lodash/toNumber';
import * as round from 'lodash/round';
import * as find from 'lodash/find';
import * as some from 'lodash/some';
import * as debounce from 'lodash/debounce';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';
import * as cloneDeep from 'lodash/cloneDeep';
import * as isUndefined from 'lodash/isUndefined';

import * as defaults from 'lodash/defaults';

import { DATE_PRESETS_OBJ, BULLET_CHART_COLORS } from '../../consts';
import { ObserveService } from '../../services/observe.service';
import { GlobalFilterService } from '../../services/global-filter.service';
import { ChartComponent } from '../../../../common/components/charts/chart.component';
import { WidgetFiltersComponent } from './../add-widget/widget-filters/widget-filters.component';

import { Subscription, BehaviorSubject } from 'rxjs';

export const ACTUAL_VS_TARGET_KPI_MIN_DIMENSIONS = {
  gauge: {
    cols: 12,
    rows: 12
  },
  bullet: {
    cols: 20,
    rows: 6
  }
};

@Component({
  selector: 'observe-kpi-bullet',
  templateUrl: 'observe-kpi-bullet.component.html',
  styleUrls: ['observe-kpi-bullet.component.scss']
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

  public isTileSizeOk = false;

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
  @ViewChild(ChartComponent, { static: false }) chartComponent: ChartComponent;

  constructor(
    public observe: ObserveService,
    public globalFilterService: GlobalFilterService,
    public dialog: MatDialog
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
      const tileSizeChanged = some(data, ({ path }) => path === 'chart.height');
      if (tileSizeChanged) {
        const { rows, cols, bullet } = this.item;
        const isTileSizeOk = this.areTileDimensionsOk(
          bullet.kpiDisplay || 'bullet',
          cols,
          rows
        );
        setTimeout(() => {
          this.isTileSizeOk = isTileSizeOk;
        });
      }
      if (!isEmpty(data)) {
        this.reloadChart(data);
      }
    });
  }

  areTileDimensionsOk(kpiDisplay, cols, rows) {
    const {
      cols: minCols,
      rows: minRows
    } = ACTUAL_VS_TARGET_KPI_MIN_DIMENSIONS[kpiDisplay];
    if (minCols > cols || minRows > rows) {
      return false;
    }
    return true;
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

    const kpiFilters = cloneDeep(this._kpi);
    const filter = this.constructGlobalFilter(filterModel, kpiFilters.filters);
    const kpi = defaults({}, { filters: filter }, kpiFilters);

    return this.executeKPI(kpi);
  }

  constructGlobalFilter(model, kpiFilters) {
    const globalFilters = this._kpi.filters.length === 1
    // Check backward compatibility
    ? fpPipe(
      fpMap(filt => {
        filt.model = model;
        return filt;
      })
    )(this._kpi.filters)
    : fpPipe(
      fpMap(filt => {
        if (filt.primaryKpiFilter) {
          filt.model = model;
        }
        return filt;
      })
    )(kpiFilters);
    return globalFilters;
  }

  getFilterLabel() {
    if (!this._executedKPI && !this._kpi) {
      return '';
    }
    const executedKpi = this._executedKPI || this._kpi;
    let primaryFilter = get(
      executedKpi,
      'filters.0.model'
    );
    let preset = get(
      executedKpi,
      'filters.0.model.preset'
    );
    // For backward compatibility. identify the primary filter
    // after adding new filters for already existing KPIs
    if (isUndefined(preset)) {
      executedKpi.filters.forEach(filt => {
        if (filt.primaryKpiFilter) {
          preset = filt.model.preset;
          primaryFilter = filt.model;
          return;
        }
      });
    }
    const filter = get(this.datePresetObj, `${preset}.label`);
    if (filter === 'Custom') {
      const gte = moment(
        get(primaryFilter, 'gte'),
        'YYYY-MM-DD HH:mm:ss'
      ).format('YYYY/MM/DD');
      const lte = moment(
        get(primaryFilter, 'lte'),
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
    const kpiFilter = this.getFilterLabel();
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

  displayFilters() {
    return this.dialog.open(WidgetFiltersComponent, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data: this._kpi
    } as MatDialogConfig);
  }
}
