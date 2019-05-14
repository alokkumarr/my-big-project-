import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import * as moment from 'moment';
import { requireIf } from '../../../../../common/validators/index';
import { Subscription } from 'rxjs';
import { Store } from '@ngxs/store';
import { ApplyAlertFilters } from '../state/alerts.actions';

import {
  DATE_FORMAT,
  CUSTOM_DATE_PRESET_VALUE,
  DATE_PRESETS
} from '../../../consts';

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

  constructor(private fb: FormBuilder, private store: Store) {
    this.createForm();
    setTimeout(() => {
      this.applyFilters();
    });
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
    const model = {
      preset: this.alertFilterForm.get('datePreset').value,
      groupBy: 'StartTime'
    };

    if (model.preset !== CUSTOM_DATE_PRESET_VALUE) {
      return model;
    }

    return {
      preset: 'BTW',
      endTime:
        this.alertFilterForm.get('lte').value.format(DATE_FORMAT.YYYY_MM_DD) +
        ' 23:59:59',
      startTime:
        this.alertFilterForm.get('gte').value.format(DATE_FORMAT.YYYY_MM_DD) +
        ' 00:00:00',
      groupBy: 'StartTime'
    };
  }

  applyFilters() {
    const filters = this.prepareDateFilterModel();
    this.store.dispatch(new ApplyAlertFilters(filters));
  }
}
