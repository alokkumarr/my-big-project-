import { Component, OnInit, Input } from '@angular/core';
import * as get from 'lodash/get';
import * as isEmpty from 'lodash/isEmpty';
import * as round from 'lodash/round';
import { Observable } from 'rxjs/Observable';

import { ObserveService } from '../../services/observe.service';

const template = require('./observe-kpi.component.html');
require('./observe-kpi.component.scss');

@Component({
  selector: 'observe-kpi',
  template
})
export class ObserveKPIComponent implements OnInit {
  _kpi: any;
  executionResult: { current?: number; prior?: number; change?: string } = {};
  constructor(private observe: ObserveService) {}

  ngOnInit() {}

  @Input()
  set kpi(data) {
    if (isEmpty(data)) return;
    this._kpi = data;
    this.executeKPI();
  }

  executeKPI() {
    const dataFieldName = get(this._kpi, 'dataFields.0.name');
    const primaryAggregate = get(this._kpi, 'dataFields.0.aggregate.0');
    this.observe
      .executeKPI(this._kpi)
      .map(res => {
        return {
          current: get(
            res,
            `data.current.${dataFieldName}._${primaryAggregate}`
          ),
          prior: get(res, `data.prior.${dataFieldName}._${primaryAggregate}`)
        };
      })
      .subscribe(({ current, prior }) => {
        const currentParsed = parseFloat(current);
        const priorParsed = parseFloat(prior);
        const change =
          round((currentParsed - priorParsed) * 100 / priorParsed, 2) || 0;

        this.executionResult = {
          current: round(currentParsed, 2),
          prior: priorParsed,
          change: change >= 0 ? `+${change}` : `${change}`
        };
      });
  }
}
