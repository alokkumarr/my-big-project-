import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Select, Store } from '@ngxs/store';
import {
  // ApplyAlertFilters,
  // ResetAlertFilters,
  LoadAllAttributeValues
} from '../../../../state/alerts.actions';
import { AlertFilterModel } from '../../../../alerts.interface';
import { Observable } from 'rxjs';
import { AlertsState } from '../../../../state/alerts.state';

@Component({
  selector: 'alerts-string-filter',
  templateUrl: './alerts-string-filter.component.html'
  // styleUrls: ['./alerts-string-filter.component.scss']
})
export class AlertsStringFilterComponent implements OnInit {
  alertFilterForm: FormGroup;
  @Select(AlertsState.getAllAttributeValues)
  attributeValues$: Observable<string[]>;

  @Output() change = new EventEmitter<AlertFilterModel>();
  @Input('stringFilter') set setStringFilter(stringFilter: AlertFilterModel) {
    const { modelValues } = stringFilter;
    const [value] = modelValues;
    this.alertFilterForm.setValue({ value });
  }

  constructor(private fb: FormBuilder, private store: Store) {
    this.createForm();
  }

  ngOnInit() {
    this.store.dispatch(new LoadAllAttributeValues());
  }

  createForm() {
    this.alertFilterForm = this.fb.group({
      value: ['', Validators.required]
    });
  }

  // applyFilters() {
  //   const filterValue = this.alertFilterForm.get('value').value;
  //   const filters = [filterValue];
  //   this.store.dispatch(new ApplyAlertFilters(filters));
  // }

  // resetFilters() {
  //   this.store.dispatch(new ResetAlertFilters());
  // }
}
