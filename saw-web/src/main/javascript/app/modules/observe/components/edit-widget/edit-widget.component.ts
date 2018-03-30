import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { Observable } from 'rxjs/Observable';
import * as map from 'lodash/map';
import * as clone from 'lodash/clone';
import * as filter from 'lodash/filter';
import * as find from 'lodash/find';

import { AnalyzeService } from '../../../analyze/services/analyze.service';
const template = require('./edit-widget.component.html');
require('./edit-widget.component.scss');

@Component({
  selector: 'edit-widget',
  template
})
export class EditWidgetComponent implements OnInit {
  editItem: any;
  _model: any;

  @Input() container: MatSidenav;
  @Output() onWidgetAction = new EventEmitter();

  constructor(private analyze: AnalyzeService) {}

  ngOnInit() {}

  @Input()
  set model(data) {
    if (!data) return;
    this._model = data;
    if (data.kpi) {
      this.prepareKPI(data);
    }
  }

  onKPIAction({ kpi }) {
    this._model.kpi = kpi;
    try {
      this.container.close();
    } catch (err) {
      throw new Error(
        'Container is either missing or does not support close method.'
      );
    }
  }

  prepareKPI(model) {
    Observable.fromPromise(this.analyze.getSemanticLayerData())
      .map((data: Array<any>) => {
        return find(data, metric => metric.id === model.kpi.semanticId);
      })
      .map(metric => {
        if (!metric) return;
        metric.kpiColumns = [
          {
            columnName: 'AVAILABLE_MB',
            displayName: 'Available MB',
            type: 'integer'
          }
        ];

        metric.dateColumns = [
          {
            columnName: 'TRANSFER_DATE',
            displayName: 'Transfer Date',
            type: 'date'
          }
        ];

        return metric;
      })
      .subscribe(metric => {
        if (!metric) return;

        this.editItem = {
          kpi: clone(model.kpi),
          column: find(
            metric.kpiColumns,
            col => col.columnName === model.kpi.columnName
          ),
          metric: metric
        };
      });
  }
}
