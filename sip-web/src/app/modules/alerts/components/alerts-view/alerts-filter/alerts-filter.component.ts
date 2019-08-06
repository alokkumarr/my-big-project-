import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import * as moment from 'moment';
import { requireIf } from '../../../../../common/validators/index';
import { Subscription } from 'rxjs';
import { Store } from '@ngxs/store';
import {
  ApplyAlertFilters,
  ResetAlertFilters
} from '../../../state/alerts.actions';

import {
  DATE_FORMAT,
  CUSTOM_DATE_PRESET_VALUE,
  DATE_PRESETS
} from '../../../consts';
import { AlertFilterModel } from '../../../alerts.interface';

@Component({
  selector: 'alerts-filter',
  templateUrl: './alerts-filter.component.html',
  styleUrls: ['./alerts-filter.component.scss']
})
export class AlertsFilterComponent implements OnInit, OnDestroy {
  datePresets = DATE_PRESETS;
  alertFilterForm: FormGroup;
  datePresetSubscription: Subscription;
  showDateFields = false;

  @Input('dateFilter') set setDateFilter(dateFilter: AlertFilterModel) {
    const { preset, startTime, endTime } = dateFilter;
    const isCustomDate = preset === CUSTOM_DATE_PRESET_VALUE;
    this.alertFilterForm.setValue({
      datePreset: dateFilter.preset,
      gte: isCustomDate ? moment(startTime) : null,
      lte: isCustomDate ? moment(endTime) : null
    });
  }

  constructor(private fb: FormBuilder, private store: Store) {
    this.createForm();
  }

  ngOnInit() {}

  ngOnDestroy() {
    if (this.datePresetSubscription) {
      this.datePresetSubscription.unsubscribe();
    }
  }

  createForm() {
    this.alertFilterForm = this.fb.group({
      datePreset: [this.datePresets[2].value, Validators.required],
      gte: [
        moment(),
        [requireIf('datePreset', val => val === CUSTOM_DATE_PRESET_VALUE)]
      ],
      lte: [
        moment(),
        [requireIf('datePreset', val => val === CUSTOM_DATE_PRESET_VALUE)]
      ]
    });

    /* Only show date inputs if custom filter is selected */
    this.datePresetSubscription = this.alertFilterForm
      .get('datePreset')
      .valueChanges.subscribe(data => {
        this.alertFilterForm.get('lte').updateValueAndValidity();
        this.alertFilterForm.get('gte').updateValueAndValidity();
        this.showDateFields = data === CUSTOM_DATE_PRESET_VALUE;
      });
  }

  prepareDateFilterModel() {
    const preset = this.alertFilterForm.get('datePreset').value;
    const groupBy = 'StartTime';

    if (preset !== CUSTOM_DATE_PRESET_VALUE) {
      return {
        preset,
        groupBy
      };
    }

    return {
      preset,
      endTime:
        this.alertFilterForm.get('lte').value.format(DATE_FORMAT.YYYY_MM_DD) +
        ' 23:59:59',
      startTime:
        this.alertFilterForm.get('gte').value.format(DATE_FORMAT.YYYY_MM_DD) +
        ' 00:00:00',
      groupBy
    };
  }

  applyFilters() {
    const filters = this.prepareDateFilterModel();
    this.store.dispatch(new ApplyAlertFilters(filters));
  }

  resetFilters() {
    this.store.dispatch(new ResetAlertFilters());
  }
}
