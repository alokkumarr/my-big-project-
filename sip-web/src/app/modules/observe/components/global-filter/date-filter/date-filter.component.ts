import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as get from 'lodash/get';
import * as floor from 'lodash/floor';
import * as moment from 'moment';

import { Subscription } from 'rxjs';

import { ObserveService } from '../../../services/observe.service';
import { GlobalFilterService } from '../../../services/global-filter.service';

import {
  CUSTOM_DATE_PRESET_VALUE,
  DATE_PRESETS
} from '../../../../analyze/consts';

@Component({
  selector: 'g-date-filter',
  templateUrl: 'date-filter.component.html'
})
export class GlobalDateFilterComponent implements OnInit, OnDestroy {
  public _filter;
  public model: any = {};
  public filterCache: { gte?; lte?; preset? };
  public presets = DATE_PRESETS; // tslint:disable-line
  public defaults: { min; max };
  public showDateFields: boolean; // tslint:disable-line
  public clearFiltersListener: Subscription;
  public applyFiltersListener: Subscription;
  public closeFiltersListener: Subscription;

  @Output() onModelChange = new EventEmitter();

  constructor(
    public observe: ObserveService,
    public filters: GlobalFilterService
  ) {}

  ngOnInit() {
    this.clearFiltersListener = this.filters.onClearAllFilters.subscribe(() => {
      this.loadDefaults();
      this.cacheFilters();
    });

    this.applyFiltersListener = this.filters.onApplyFilter.subscribe(() => {
      this.cacheFilters();
    });

    this.closeFiltersListener = this.filters.onSidenavStateChange.subscribe(
      state => {
        if (!state) {
          this.loadDefaults(true); // load cached filter data since last apply
        }
      }
    );
  }

  ngOnDestroy() {
    this.clearFiltersListener.unsubscribe();
    this.applyFiltersListener.unsubscribe();
    this.closeFiltersListener.unsubscribe();
  }

  @Input()
  set filter(data) {
    this._filter = data;

    this.model.preset =
      get(this._filter, 'model.preset') || CUSTOM_DATE_PRESET_VALUE;
    this.model.gte = moment(get(this._filter, 'model.gte'));
    this.model.lte = moment(get(this._filter, 'model.lte'));
    if (data.model) {
      this.cacheFilters();
      this.loadDateRange(true);
    } else {
      this.loadDateRange(false);
    }

    this.onPresetChange({ value: this.model.preset });
  }

  /**
   * Caches filter in-memory. Cache is used to revert to last
   * applied / default values when needed.
   *
   * @memberof GlobalDateFilterComponent
   */
  cacheFilters() {
    this.filterCache = {
      preset: this.model.preset,
      gte: this.model.gte,
      lte: this.model.lte
    };
  }

  /**
   * Queries the min and max dates present in db for a field and updates the model with it
   *
   * @memberof GlobalDateFilterComponent
   */
  loadDateRange(useCache: boolean = false) {
    this.observe
      .getModelValues(this._filter)
      .subscribe((data: { _min: string; _max: string }) => {
        this.defaults = {
          min: moment(floor(parseFloat(data._min))),
          max: moment(floor(parseFloat(data._max)))
        };

        this.loadDefaults(useCache);
        this.cacheFilters();
      });
  }

  /**
   * Resets the date and preset value to default state or last cached
   * state.
   *
   * @param {boolean} [fromCache=false]
   * @returns
   * @memberof GlobalDateFilterComponent
   */
  loadDefaults(fromCache = false) {
    if ((!fromCache && !this.defaults) || (fromCache && !this.filterCache)) {
      return;
    }

    /* prettier-ignore */
    const loadData = fromCache
      ? this.filterCache
      : {
        preset: CUSTOM_DATE_PRESET_VALUE,
        gte: this.defaults.min,
        lte: this.defaults.max
      };

    this.onPresetChange({ value: loadData.preset });
    this.onDateChange('gte', {
      value: loadData.gte
    });
    this.onDateChange('lte', {
      value: loadData.lte
    });
  }

  onPresetChange(data) {
    this.model.preset = data.value;
    this.showDateFields = this.model.preset === CUSTOM_DATE_PRESET_VALUE;
    this.onFilterChange();
  }

  onDateChange(field, data) {
    this.model[field] = data.value;
    this.onFilterChange();
  }

  /**
   * Checks whether filter value is valid.
   *
   * @param {any} filt
   * @returns {boolean}
   * @memberof GlobalDateFilterComponent
   */
  isValid(filt): boolean {
    return (
      filt.model.preset !== CUSTOM_DATE_PRESET_VALUE ||
      (filt.model.lte && filt.model.gte)
    );
  }

  /**
   * Gets the filter model together and communicates the
   * updated filter to the parent.
   *
   * @memberof GlobalDateFilterComponent
   */
  onFilterChange() {
    const payload = {
      ...this._filter,
      ...{
        model: {
          preset: this.model.preset
        }
      }
    };

    if (this.model.preset === CUSTOM_DATE_PRESET_VALUE) {
      payload.model.lte = this.model.lte.format('YYYY-MM-DD');
      payload.model.gte = this.model.gte.format('YYYY-MM-DD');
    } else {
      delete payload.model.lte;
      delete payload.model.gte;
    }

    this.onModelChange.emit({ data: payload, valid: this.isValid(payload) });
  }
}
