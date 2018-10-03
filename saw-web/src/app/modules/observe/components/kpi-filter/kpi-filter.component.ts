import {
  Component,
  OnInit,
  OnDestroy,
  Output,
  EventEmitter
} from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';

import { requireIf } from '../../validators/required-if.validator';
import {
  CUSTOM_DATE_PRESET_VALUE,
  DATE_PRESETS,
  DATE_FORMAT
} from '../../consts';

import { GlobalFilterService } from '../../services/global-filter.service';
import { Subscription } from 'rxjs/Subscription';
import * as moment from 'moment';
import 'rxjs/add/operator/debounceTime';

interface KPIFilterType {
  preset: string;
  lte?: string;
  gte?: string;
}

@Component({
  selector: 'kpi-filter',
  templateUrl: './kpi-filter.component.html',
  styleUrls: ['./kpi-filter.component.scss']
})
export class KPIFilterComponent implements OnInit, OnDestroy {
  kpiFilterForm: FormGroup;
  filterCache: KPIFilterType = {
    preset: '',
    gte: moment().format(DATE_FORMAT.YYYY_MM_DD),
    lte: moment().format(DATE_FORMAT.YYYY_MM_DD)
  };

  dateFilters = DATE_PRESETS;
  datePresetSubscription: Subscription;
  listeners: Array<Subscription> = [];
  showDateFields = false;
  @Output() onModelChange = new EventEmitter();

  constructor(
    public fb: FormBuilder,
    public globalFilterService: GlobalFilterService
  ) {
    this.createForm();
  }

  ngOnInit() {
    const clearFiltersListener = this.globalFilterService.onClearAllFilters.subscribe(
      () => {
        this.loadDefaults();
        this.cacheFilters();
      }
    );

    const applyFiltersListener = this.globalFilterService.onApplyFilter.subscribe(
      () => {
        this.cacheFilters();
      }
    );

    const closeFiltersListener = this.globalFilterService.onSidenavStateChange.subscribe(
      state => {
        if (!state) {
          this.loadDefaults(true); // load cached filter data since last apply
        }
      }
    );
  }

  ngOnDestroy() {
    this.listeners.forEach(l => l.unsubscribe());
  }

  cacheFilters() {
    this.filterCache = this.getFilterModel();
  }

  loadDefaults(fromCache = false) {
    if (fromCache && !this.filterCache) {
      return;
    }

    /* prettier-ignore */
    this.kpiFilterForm.setValue(
      fromCache ? {
        preset: this.filterCache.preset,
        gte: moment(this.filterCache.gte, DATE_FORMAT.YYYY_MM_DD),
        lte: moment(this.filterCache.lte, DATE_FORMAT.YYYY_MM_DD)
      } : {
        preset: '',
        gte: moment(),
        lte: moment()
      }
    );

    this.onModelChange.emit(this.getFilterModel());
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
    const datePresetSubscription = this.kpiFilterForm
      .get('preset')
      .valueChanges.subscribe(data => {
        this.kpiFilterForm.get('lte').updateValueAndValidity();
        this.kpiFilterForm.get('gte').updateValueAndValidity();
        this.showDateFields = data === CUSTOM_DATE_PRESET_VALUE;
      });
    this.listeners.push(datePresetSubscription);

    const kpiFilterSubscription = this.kpiFilterForm.valueChanges
      .debounceTime(500)
      .subscribe(data => {
        this.kpiFilterForm.valid &&
          this.onModelChange.emit(this.getFilterModel());
      });

    this.listeners.push(kpiFilterSubscription);
  }

  /**
   * getFilterModel - Returns the filter model. If no custom preset selected,
   * also includes dates in the result.
   *
   * @returns {undefined}
   */
  getFilterModel(): KPIFilterType {
    const model = {
      preset: this.kpiFilterForm.get('preset').value
    };

    if (model.preset !== CUSTOM_DATE_PRESET_VALUE) {
      return model;
    }

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
