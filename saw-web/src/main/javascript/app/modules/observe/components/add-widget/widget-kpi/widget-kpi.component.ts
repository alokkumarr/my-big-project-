import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormBuilder,
  Validators
} from '@angular/forms';

import * as get from 'lodash/get';
import * as assign from 'lodash/assign';
import * as find from 'lodash/find';

import { WIDGET_ACTIONS } from '../widget.model';

const template = require('./widget-kpi.component.html');
require('./widget-kpi.component.scss');

@Component({
  selector: 'widget-kpi',
  template
})
export class WidgetKPIComponent implements OnInit {
  _kpi: any;
  _metric: any;

  @Output() onKPIAction = new EventEmitter();

  dateFilters = [{ name: 'Year to Date', value: 'YTD' }];
  aggregations = [{ name: 'Sum', value: 'sum' }];

  kpiForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.createForm();
  }

  ngOnInit() {}

  createForm() {
    this.kpiForm = this.fb.group({
      name: ['', Validators.required],
      dateField: ['', Validators.required],
      filter: [this.dateFilters[0].value, Validators.required],
      aggregate: [this.aggregations[0].value, Validators.required]
    });
  }

  @Input()
  set metric(data: any) {
    if (!data) return;
    this._metric = data;
    const kpiDateField = get(this._kpi, 'filters.0.columnName');

    this.kpiForm
      .get('dateField')
      .setValue(kpiDateField || data.dateColumns[0].columnName);
  }

  @Input()
  set kpi(data: any) {
    if (!data) return;

    this._kpi = data;

    data.name && this.kpiForm.get('name').setValue(data.name);

    const dateField = get(data, 'filters.0.columnName');
    dateField && this.kpiForm.get('dateField').setValue(dateField);

    const filt = get(data, 'filters.0.model.preset');
    this.kpiForm.get('filter').setValue(filt || this.dateFilters[0].value);

    const primaryAggregate = get(data, 'dataFields.0.aggregate.0');
    this.kpiForm
      .get('aggregate')
      .setValue(primaryAggregate || this.aggregations[0].value);
  }

  applyKPI() {
    const dataField = get(this._kpi, 'dataFields.0');
    const dateField = find(
      this._metric.dateColumns,
      col => col.columnName === this.kpiForm.get('dateField').value
    );

    this.onKPIAction.emit({
      kpi: assign(this._kpi, {
        name: this.kpiForm.get('name').value,
        dataFields: [
          {
            columnName: dataField.columnName,
            name: dataField.name,
            aggregate: [this.kpiForm.get('aggregate').value]
          }
        ],
        filters: [
          {
            type: dateField.type,
            columnName: dateField.columnName,
            model: {
              preset: this.kpiForm.get('filter').value
            }
          }
        ]
      })
    });
  }
}
