import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter,
  ViewChild
} from '@angular/core';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { FormControl } from '@angular/forms';

import { Observable, Subscription } from 'rxjs';
import { startWith, map } from 'rxjs/operators';

import { ObserveService } from '../../../services/observe.service';
import {
  GlobalFilterService,
  isValidStringFilter as isValid
} from '../../../services/global-filter.service';

@Component({
  selector: 'g-string-filter',
  templateUrl: 'string-filter.component.html'
})
export class GlobalStringFilterComponent implements OnInit, OnDestroy {
  @Output() onModelChange = new EventEmitter();
  @ViewChild(MatAutocompleteTrigger, { static: true }) trigger: MatAutocompleteTrigger;

  filterCtrl: FormControl;
  public _filter;
  public value: Array<string> = [];
  public filterCache: { values?: Array<string>; textValue?: string };
  public suggestions = [];
  public valueChangeListener: Subscription;
  public clearFiltersListener: Subscription;
  public applyFiltersListener: Subscription;
  public closeFiltersListener: Subscription;
  public filteredSuggestions: Observable<any[]>; // tslint:disable-line

  constructor(
    public observe: ObserveService,
    public filters: GlobalFilterService
  ) {
    this.filterCtrl = new FormControl();
    this.filteredSuggestions = this.filterCtrl.valueChanges.pipe(
      startWith(''),
      map(state =>
        state ? this.filterSuggestions(state) : this.suggestions.slice()
      )
    );
  }

  ngOnInit() {
    this.valueChangeListener = this.filterCtrl.valueChanges.subscribe(() => {
      this.filterChanged();
    });

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
    this.valueChangeListener.unsubscribe();
    this.clearFiltersListener.unsubscribe();
    this.applyFiltersListener.unsubscribe();
    this.closeFiltersListener.unsubscribe();
  }

  @Input()
  set filter(data) {
    this._filter = data;

    if (data.model && data.model.modelValues) {
      this.value = [...data.model.modelValues];
      this.cacheFilters();
      this.loadSuggestions(true);
    } else {
      this.loadSuggestions();
    }
  }

  filterSuggestions(str: string) {
    return this.suggestions.filter(
      sugg => sugg.toLowerCase().indexOf(str.toLowerCase()) === 0
    );
  }

  onInputChange(evt) {
    const val = (this.filterCtrl.value || '').replace(/;$/, ''); // remove trailing semicolon

    if ([13, 186].includes(evt.keyCode) && val) {
      // on pressing 'Enter' or semicolon (;)
      this.value.push(val);
      this.filterCtrl.setValue('');
    }

    this.filterChanged();
  }

  removeInput(str) {
    const id = this.value.indexOf(str);
    if (id >= 0) {
      this.value.splice(id, 1);
      this.filterChanged();
    }
  }

  /**
   * Loads autocomplete suggestions from backend
   *
   * @memberof GlobalStringFilterComponent
   */
  loadSuggestions(useCache: boolean = false) {
    this.observe
      .getModelValues(this._filter)
      .subscribe((data: Array<string>) => {
        this.suggestions = data;

        this.loadDefaults(useCache);
        this.cacheFilters();
      });
  }

  /**
   * Caches filter in-memory. Cache is used to revert to last
   * applied / default values when needed.
   *
   * @memberof GlobalStringFilterComponent
   */
  cacheFilters() {
    this.filterCache = {
      values: this.value.slice(0),
      textValue: this.filterCtrl.value
    };
  }

  /**
   * Loads default or cached filter values to the bound model
   *
   * @param {boolean} [fromCache=false]
   * @returns
   * @memberof GlobalStringFilterComponent
   */
  loadDefaults(fromCache = false) {
    if (fromCache && !this.filterCache) {
      return;
    }

    if (fromCache && this.filterCache.textValue) {
      this.filterCtrl.setValue(this.filterCache.textValue);
    } else {
      this.filterCtrl.reset();
    }

    this.value = fromCache ? this.filterCache.values.slice(0) : [];
    this.filterChanged();
  }

  /**
   * Communicates the filter value to the parent.
   *
   * @memberof GlobalStringFilterComponent
   */
  filterChanged() {
    const payload = {
      ...this._filter,
      ...{
        model: {
          modelValues: this.filterCtrl.value
            ? [this.filterCtrl.value, ...this.value]
            : this.value,

          // We don't allow anything else than 'is in' sql operation on strings from dashboards for now
          operator: 'ISIN'
        }
      }
    };
    this.onModelChange.emit({ data: payload, valid: isValid(payload) });
  }
}
