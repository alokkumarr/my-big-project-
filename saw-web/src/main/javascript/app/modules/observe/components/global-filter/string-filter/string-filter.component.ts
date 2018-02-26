declare const require: any;

import { Component, OnInit, OnDestroy, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete'
import { FormControl } from '@angular/forms';

import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import { startWith } from 'rxjs/operators/startWith';
import { map } from 'rxjs/operators/map';

import { ObserveService } from '../../../services/observe.service';
import { GlobalFilterService } from '../../../services/global-filter.service';

const template = require('./string-filter.component.html');

@Component({
  selector: 'g-string-filter',
  template
})

export class GlobalStringFilterComponent implements OnInit, OnDestroy {
  @Output() onModelChange = new EventEmitter();
  @ViewChild(MatAutocompleteTrigger) trigger: MatAutocompleteTrigger;

  filterCtrl: FormControl;
  private _filter;
  private value: Array<string> = [];
  private suggestions = [];
  private valueChangeListener: Subscription;
  private clearFiltersListener: Subscription;
  private filteredSuggestions: Observable<any[]>;

  constructor(
    private observe: ObserveService,
    private filters: GlobalFilterService
  ) {
    this.filterCtrl = new FormControl();
    this.filteredSuggestions = this.filterCtrl.valueChanges.pipe(
      startWith(''),
      map(state => state ? this.filterSuggestions(state) : this.suggestions.slice())
    );
  }

  ngOnInit() {
    this.valueChangeListener = this.filterCtrl.valueChanges.subscribe(() => {
      this.filterChanged();
    });

    this.clearFiltersListener = this.filters.onClearAllFilters.subscribe(() => {
      this.loadDefaults();
    });
  }

  ngOnDestroy() {
    this.valueChangeListener.unsubscribe();
    this.clearFiltersListener.unsubscribe();
  }

  @Input() set filter(data) {
    this._filter = data;

    this.loadSuggestions();
  }

  onInputChange(evt) {
    const val = (this.filterCtrl.value || '').replace(/;$/, ''); // remove trailing semicolon

    if ([13, 186].includes(evt.keyCode) && val) { // on pressing 'Enter' or semicolon (;)
      this.value.push(val);
      this.filterCtrl.setValue('');
    }

    this.filterChanged();
  }

  loadDefaults() {
    this.filterCtrl.reset();
    this.value = [];
    this.filterChanged();
  }

  /**
   * Loads autocomplete suggestions from backend
   *
   * @memberof GlobalStringFilterComponent
   */
  loadSuggestions() {
    this.observe.getModelValues(this._filter).subscribe((data: Array<string>) => {
      this.suggestions = data;

      this.loadDefaults();
    });
  }

  filterSuggestions(str: string) {
    return this.suggestions.filter(sugg =>
      sugg.toLowerCase().indexOf(str.toLowerCase()) === 0);
  }

  removeInput(str) {
    const id = this.value.indexOf(str);
    if (id >= 0) {
      this.value.splice(id, 1);
      this.filterChanged();
    }
  }

  isValid() {
    return this.filterCtrl.value || (
      this.value &&
      this.value.length
    );
  }

  filterChanged() {
    const payload = {
      ...this._filter,
      ...{
        model: {
          modelValues: this.filterCtrl.value ? [this.filterCtrl.value, ...this.value] : this.value,

          // We don't allow anything else than 'is in' sql operation on strings from dashboards for now
          operator: 'ISIN'
        }
      }
    };
    this.onModelChange.emit({data: payload, valid: this.isValid()});
  }
}

