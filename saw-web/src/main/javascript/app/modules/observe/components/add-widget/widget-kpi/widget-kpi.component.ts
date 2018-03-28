import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {
  FormControl,
  FormGroup,
  FormBuilder,
  Validators
} from '@angular/forms';
import { WIDGET_ACTIONS } from '../widget.model';

const template = require('./widget-kpi.component.html');
require('./widget-kpi.component.scss');

@Component({
  selector: 'widget-kpi',
  template
})
export class WidgetKPIComponent implements OnInit {
  _column: any;
  _metric: any;

  @Output() onKPIAction = new EventEmitter();

  dateFilters = [{ name: 'Month to Date', value: 'MTD' }];
  aggregations = [{ name: 'Sum', value: 'sum' }];

  kpiForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.createForm();
  }

  ngOnInit() {
  }

  createForm() {
    this.kpiForm = this.fb.group({
      columnName: ['', Validators.required],
      name: ['', Validators.required],
      dateField: ['', Validators.required],
      filter: [this.dateFilters[0].value, Validators.required],
      aggregate: [this.aggregations[0].value, Validators.required]
    });
  }

  @Input() set column (data: any) {
    if(!data) return;
    this._column = data;
    this.kpiForm.get('columnName').setValue(data.columnName);
    this.kpiForm.get('name').setValue(data.displayName);
  }

  @Input() set metric (data: any) {
    if(!data) return;
    this._metric = data;
    this.kpiForm
      .get('dateField')
      .setValue(data.dateColumns[0].columnName);
  }

  applyKPI() {
    this.onKPIAction.emit({
      action: WIDGET_ACTIONS.ADD,
      kpi: this.kpiForm.value
    });
  }
}
