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
  executionResult: Observable<any>;
  constructor(private observe: ObserveService) {}

  ngOnInit() {}

  @Input()
  set kpi(data) {
    if (isEmpty(data)) return;
    this._kpi = data;
    this.executeKPI();
  }

  executeKPI() {
    this.executionResult = this.observe
      .executeKPI(this._kpi)
      .map(res => {
        return get(res, `data.${this._kpi.columnName}._${this._kpi.aggregate}`);
      })
      .map(({ current, prior }) => {
        const currentParsed = parseFloat(current);
        const priorParsed = parseFloat(prior);
        const change = round((currentParsed - priorParsed) / currentParsed, 2);

        return {
          current: round(currentParsed, 2),
          prior: priorParsed,
          change
        };
      });
  }
}
