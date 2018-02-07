import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormControl } from '@angular/forms';

import { Observable } from 'rxjs/Observable';
import { startWith } from 'rxjs/operators/startWith';
import { map } from 'rxjs/operators/map';

const template = require('./string-filter.component.html');

@Component({
  selector: 'g-string-filter',
  template
})

export class GlobalStringFilterComponent implements OnInit {
  @Output() onModelChange = new EventEmitter();

  filterCtrl: FormControl;
  private _filter;
  private value: Array<string>;
  private suggestions = [
    'Text 1',
    'Text 2',
    'Text 3'
  ];
  private filteredSuggestions: Observable<any[]>;
  constructor() {
    this.filterCtrl = new FormControl();
    this.filteredSuggestions = this.filterCtrl.valueChanges.pipe(
      startWith(''),
      map(state => state ? this.filterSuggestions(state) : this.suggestions.slice())
    );
  }

  ngOnInit() { }

  @Input() set filter(data) {
    this._filter = data;

    this.value = [];
  }

  onKeyup(evt) {
    if (evt.keyCode === 13) { // on pressing 'Enter'
      this.value.push(this.filterCtrl.value);
      this.filterCtrl.setValue('');
      this.filterChanged();
    }
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

  filterChanged() {
    const payload = {
      ...this._filter,
      ...{
        model: {
          modelValues: this.value
        }
      }
    };
    this.onModelChange.emit({data: payload, valid: this.value && this.value.length});
  }
}

