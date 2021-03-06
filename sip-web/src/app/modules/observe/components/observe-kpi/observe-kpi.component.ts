import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material';
import * as get from 'lodash/get';
import * as find from 'lodash/find';
import * as map from 'lodash/map';
import * as defaults from 'lodash/defaults';
import * as upperCase from 'lodash/upperCase';
import * as isEmpty from 'lodash/isEmpty';
import * as round from 'lodash/round';
import * as trim from 'lodash/trim';
import * as isUndefined from 'lodash/isUndefined';
import * as isFinite from 'lodash/isFinite';
import * as debounce from 'lodash/debounce';
import * as moment from 'moment';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';
import * as cloneDeep from 'lodash/cloneDeep';

import { DATE_PRESETS_OBJ, KPI_BG_COLORS } from '../../consts';
import { ObserveService } from '../../services/observe.service';
import { GlobalFilterService } from '../../services/global-filter.service';
import { Subscription } from 'rxjs';
import { map as mapObservable } from 'rxjs/operators';
import { WidgetFiltersComponent } from './../add-widget/widget-filters/widget-filters.component';

@Component({
  selector: 'observe-kpi',
  templateUrl: './observe-kpi.component.html',
  styleUrls: ['./observe-kpi.component.scss']
})
export class ObserveKPIComponent implements OnInit, OnDestroy {
  _kpi: any;
  _executedKPI: any;
  primaryChange: number;
  bgColor: string;
  countToChange: string;
  countToCurrent: string;
  dataFormat: any;

  /* Used to dynamically adjust font-size based on tile height */
  fontMultipliers = {
    primary: 1,
    secondary: 1
  };

  datePresetObj = DATE_PRESETS_OBJ;
  primaryResult: { current?: number; prior?: number; change?: string } = {};
  secondaryResult: Array<{ name: string; value: string | number }> = [];
  kpiFilterSubscription: Subscription;
  filterLabel: string;

  constructor(
    public observe: ObserveService,
    public globalFilterService: GlobalFilterService,
    public dialog: MatDialog
  ) {
    this.executeKPI = debounce(this.executeKPI, 100);
  }

  ngOnInit() {
    this.kpiFilterSubscription = this.globalFilterService.onApplyKPIFilter.subscribe(
      this.onFilterKPI.bind(this)
    );
  }

  ngOnDestroy() {
    this.kpiFilterSubscription && this.kpiFilterSubscription.unsubscribe();
  }

  @Input()
  set kpi(data) {
    if (isEmpty(data)) {
      return;
    }

    this._kpi = data;
    this.dataFormat = get(this._kpi, 'dataFields.0.format');
    this.executeKPI(data);
  }

  @Input()
  set dimensions(data) {
    if (data && data.height > 0) {
      this.fontMultipliers.primary = data.height / 160;
      this.fontMultipliers.secondary = Math.min(2, data.height / 100);
    }
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
    const globalFilters =
      kpiFilters.length === 1
        ? // Check backward compatibility
          fpPipe(
            fpMap(filt => {
              filt.model = model;
              return filt;
            })
          )(kpiFilters)
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
    let primaryFilter = get(executedKpi, 'filters.0.model');
    let preset = get(executedKpi, 'filters.0.model.preset');
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

  displayFilters() {
    return this.dialog.open(WidgetFiltersComponent, {
      width: 'auto',
      height: 'auto',
      autoFocus: false,
      data: this._kpi
    } as MatDialogConfig);
  }

  executeKPI(kpi) {
    this._executedKPI = kpi;
    this.filterLabel = this.getFilterLabel();
    this.bgColor = isUndefined(kpi.kpiBgColor)
      ? '#0f61c8'
      : get(find(KPI_BG_COLORS, ['label', kpi.kpiBgColor]), 'value');
    const dataFieldName = get(kpi, 'dataFields.0.name');
    const [primaryAggregate, ...secondaryAggregates] = get(
      kpi,
      'dataFields.0.aggregate',
      []
    );
    this.observe
      .executeKPI(kpi)
      /* Parse kpi execution results into primary and secondary aggregation results */
      .pipe(
        mapObservable(res => {
          const primary = {
            current: get(
              res,
              `data.current.${dataFieldName}._${primaryAggregate}`
            ),
            prior: get(res, `data.prior.${dataFieldName}._${primaryAggregate}`)
          };

          const secondary = map(secondaryAggregates || [], ag => ({
            name: upperCase(ag),
            value: round(
              parseFloat(get(res, `data.current.${dataFieldName}._${ag}`)) || 0,
              2
            )
          }));
          return { primary, secondary };
        })
      )
      /* Parse and calculate percentage change for primary aggregations */
      .subscribe(({ primary, secondary }) => {
        const currentParsed = parseFloat(primary.current) || 0;
        const priorParsed = parseFloat(primary.prior);
        let change =
          round(((currentParsed - priorParsed) * 100) / priorParsed) || 0;
        change = isFinite(change) ? change : 0;
        this.primaryChange = change;
        this.primaryResult = {
          current: currentParsed,
          prior: priorParsed,
          change: trim(change, '-')
        };

        this.secondaryResult = secondary;
      });
  }

  // Accepts a Number value to be formatted based on the data format options already saved during creation
  // or edition of a KPI.

  /**
   * Called everytime when each KPI is loaded on a dashboard.
   *
   * @param {Number} value
   * returns a foramtted value based on the properties set during creation or edition of a dashboard.
   */
  fetchValueAsPerFormat(value) {
    let formattedValue;
    if (isUndefined(value)) {
      return;
    }
    if (isUndefined(this.dataFormat)) {
      return value;
    }
    formattedValue = value.toFixed(this.dataFormat.precision);
    formattedValue = this.dataFormat.comma
      ? this.fetchCommaValue(formattedValue)
      : formattedValue;
    formattedValue = `${this.dataFormat.prefix} ${formattedValue} ${this.dataFormat.suffix}`;
    return formattedValue;
  }

  fetchCommaValue(val) {
    const part = val.toString().split('.');
    part[0] = part[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    return part.join('.');
  }
}
