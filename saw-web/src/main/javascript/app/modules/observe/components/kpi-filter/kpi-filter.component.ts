import { Component, OnDestroy, Output, EventEmitter } from '@angular/core';
import {
  FormGroup,
  FormControl,
  FormBuilder,
  Validators
} from '@angular/forms';

import { requireIf } from '../../validators/required-if.validator';
import {
  CUSTOM_DATE_PRESET_VALUE,
  DATE_PRESETS,
  DATE_PRESETS_OBJ,
  DATE_FORMAT
} from '../../consts';

import { DashboardService } from '../../services/dashboard.service';
import { Subscription } from 'rxjs/Subscription';
import * as moment from 'moment';
import 'rxjs/add/operator/debounceTime';

const template = require('./kpi-filter.component.html');
require('./kpi-filter.component.scss');

@Component({
  selector: 'kpi-filter',
  template
})
export class KPIFilter implements OnDestroy {
  kpiFilterForm: FormGroup;
  dateFilters = DATE_PRESETS;
  datePresetSubscription: Subscription;
  showDateFields: boolean = false;

  constructor(
    private fb: FormBuilder,
    private dashboardService: DashboardService
  ) {
    this.createForm();
  }

  ngOnDestroy() {
    this.datePresetSubscription && this.datePresetSubscription.unsubscribe();
  }

  createForm() {
    this.kpiFilterForm = this.fb.group({
      gte: [
        moment(),
        [requireIf('preset', val => val === CUSTOM_DATE_PRESET_VALUE)]
      ],
      lte: [
        moment(),
        [requireIf('preset', val => val === CUSTOM_DATE_PRESET_VALUE)]
      ],
      preset: ['']
    });

    /* Only show date inputs if custom filter is selected */
    this.datePresetSubscription = this.kpiFilterForm
      .get('preset')
      .valueChanges.subscribe(data => {
        this.kpiFilterForm.get('lte').updateValueAndValidity();
        this.kpiFilterForm.get('gte').updateValueAndValidity();
        this.showDateFields = data === CUSTOM_DATE_PRESET_VALUE;
      });

    this.kpiFilterForm.valueChanges.debounceTime(500).subscribe(data => {
      this.kpiFilterForm.valid &&
        this.dashboardService.onFilterKPI.next(this.getFilterModel());
    });
  }

  getFilterModel() {
    const model = {
      preset: this.kpiFilterForm.get('preset').value
    };

    if (model.preset !== CUSTOM_DATE_PRESET_VALUE) return model;

    // Adding static time signatures until we allow users to choose time
    // for `to` and `from` fields.
    return {
      ...model,
      lte:
        this.kpiFilterForm.get('lte').value.format(DATE_FORMAT.YYYY_MM_DD) +
        ' 23:59:59',
      gte:
        this.kpiFilterForm.get('gte').value.format(DATE_FORMAT.YYYY_MM_DD) +
        ' 00:00:00'
    };
  }
}
