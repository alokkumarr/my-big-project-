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
  @Input() column: any;
  @Input() metric: any;
  @Output() onKPIAction = new EventEmitter();

  dateFilters = [{ name: 'Month to Date', value: 'MTD' }];
  aggregations = [{ name: 'Sum', value: 'sum' }];

  kpiForm: FormGroup;

  constructor(private fb: FormBuilder) {
    this.createForm();
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

  applyKPI() {
    this.onKPIAction.emit({
      action: WIDGET_ACTIONS.ADD,
      kpi: this.kpiForm.value
    });
  }

  ngOnInit() {
    this.kpiForm.get('columnName').setValue(this.column.columnName);
    this.kpiForm.get('name').setValue(this.column.displayName);
    this.kpiForm
      .get('dateField')
      .setValue(this.metric.dateColumns[0].columnName);
  }
}
