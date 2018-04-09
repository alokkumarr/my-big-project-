import { Component, OnInit, Input } from '@angular/core';
import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as upperCase from 'lodash/upperCase';
import * as isEmpty from 'lodash/isEmpty';
import * as round from 'lodash/round';
import { Observable } from 'rxjs/Observable';

import { DATE_PRESETS_OBJ } from '../../consts';
import { ObserveService } from '../../services/observe.service';

const template = require('./observe-kpi.component.html');
require('./observe-kpi.component.scss');

@Component({
  selector: 'observe-kpi',
  template
})
export class ObserveKPIComponent implements OnInit {
  _kpi: any;
  datePresetObj = DATE_PRESETS_OBJ;
  primaryResult: { current?: number; prior?: number; change?: string } = {};
  secondaryResult: Array<{ name: string; value: string | number }> = [];
  constructor(private observe: ObserveService) {}

  ngOnInit() {}

  @Input()
  set kpi(data) {
    if (isEmpty(data)) return;
    this._kpi = data;
    this.executeKPI();
  }

  get filterLabel() {
    if (!this._kpi) return '';

    const preset = get(this._kpi, 'filters.0.model.preset');
    return get(this.datePresetObj, `${preset}.label`);
  }

  executeKPI() {
    const dataFieldName = get(this._kpi, 'dataFields.0.name');
    const [primaryAggregate, ...secondaryAggregates] = get(
      this._kpi,
      'dataFields.0.aggregate'
    );
    this.observe
      .executeKPI(this._kpi)
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
          value: get(res, `data.current.${dataFieldName}._${ag}`)
        }));
        return { primary, secondary };
      })
      /* Parse and calculate percentage change for primary aggregations */
      .subscribe(({ primary, secondary }) => {
        const currentParsed = parseFloat(primary.current);
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
