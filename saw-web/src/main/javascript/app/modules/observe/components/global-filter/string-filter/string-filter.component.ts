declare const require: any;

import { Component, OnInit, OnDestroy, Input, Output, EventEmitter, ViewChild } from '@angular/core';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete'
import { FormControl } from '@angular/forms';

import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import { startWith } from 'rxjs/operators/startWith';
import { map } from 'rxjs/operators/map';

import { ObserveService } from '../../../services/observe.service';

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
  private value: Array<string>;
  private suggestions = [];
  private valueChangeListener: Subscription;
  private filteredSuggestions: Observable<any[]>;
  constructor(
    private observe: ObserveService
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
  }

  ngOnDestroy() {
    this.valueChangeListener.unsubscribe();
  }

  @Input() set filter(data) {
    this._filter = data;

    this.loadSuggestions();
    this.value = [];
  }

  onInputChange(evt) {
    if (evt.keyCode === 13) { // on pressing 'Enter'
      this.value.push(this.filterCtrl.value);
      this.filterCtrl.setValue('');
    }

    this.filterChanged();
  }

  loadSuggestions() {
    this.observe.getModelValues(this._filter).subscribe((data: Array<string>) => {
      this.suggestions = data;

      this.filterCtrl.reset();
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

