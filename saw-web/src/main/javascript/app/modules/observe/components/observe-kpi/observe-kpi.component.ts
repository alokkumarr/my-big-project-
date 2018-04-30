import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as defaults from 'lodash/defaults';
import * as upperCase from 'lodash/upperCase';
import * as isEmpty from 'lodash/isEmpty';
import * as round from 'lodash/round';
import { Observable } from 'rxjs/Observable';

import { DATE_PRESETS_OBJ } from '../../consts';
import { ObserveService } from '../../services/observe.service';
import { DashboardService } from '../../services/dashboard.service';
import { Subscription } from 'rxjs/Subscription';

const template = require('./observe-kpi.component.html');
require('./observe-kpi.component.scss');

@Component({
  selector: 'observe-kpi',
  template
})
export class ObserveKPIComponent implements OnInit, OnDestroy {
  _kpi: any;
  _executedKPI: any;

  /* Used to dynamically adjust font-size based on tile height */
  fontMultipliers = {
    primary: 1,
    secondary: 1
  };

  datePresetObj = DATE_PRESETS_OBJ;
  primaryResult: { current?: number; prior?: number; change?: string } = {};
  secondaryResult: Array<{ name: string; value: string | number }> = [];
  kpiFilterSubscription: Subscription;

  constructor(
    private observe: ObserveService,
    private dashboardService: DashboardService
  ) {}

  ngOnInit() {
    this.kpiFilterSubscription = this.dashboardService.onFilterKPI.subscribe(
      this.onFilterKPI.bind(this)
    );
  }

  ngOnDestroy() {
    this.kpiFilterSubscription && this.kpiFilterSubscription.unsubscribe();
  }

  @Input()
  set kpi(data) {
    if (isEmpty(data)) return;
    this._kpi = data;
    this.executeKPI(this._kpi);
  }

  @Input()
  set dimensions(data) {
    if (data && data.height > 0) {
      this.fontMultipliers.primary = data.height / 100;
      this.fontMultipliers.secondary = Math.min(2, data.height / 100);
    }
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

  get filterLabel() {
    if (!this._executedKPI && !this._kpi) return '';

    const preset = get(
      this._executedKPI || this._kpi,
      'filters.0.model.preset'
    );
    return get(this.datePresetObj, `${preset}.label`);
  }

  executeKPI(kpi) {
    this._executedKPI = kpi;
    const dataFieldName = get(kpi, 'dataFields.0.name');
    const [primaryAggregate, ...secondaryAggregates] = get(
      kpi,
      'dataFields.0.aggregate',
      []
    );
    this.observe
      .executeKPI(kpi)
      /* Parse kpi execution results into primary and secondary aggregation results */
      .map(res => {
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
      /* Parse and calculate percentage change for primary aggregations */
      .subscribe(({ primary, secondary }) => {
        const currentParsed = parseFloat(primary.current) || 0;
        const priorParsed = parseFloat(primary.prior);
        const change =
          round((currentParsed - priorParsed) * 100 / priorParsed, 2) || 0;

        this.primaryResult = {
          current: round(currentParsed, 2),
          prior: priorParsed,
          change: change >= 0 ? `+${change}` : `${change}`
        };

        this.secondaryResult = secondary;
      });
  }
}
