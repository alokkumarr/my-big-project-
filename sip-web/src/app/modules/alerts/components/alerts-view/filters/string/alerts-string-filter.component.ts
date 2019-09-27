import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { Select, Store } from '@ngxs/store';
import { LoadAllAttributeValues } from '../../../../state/alerts.actions';
import {
  AlertFilterModel,
  AlertFilterEvent
} from '../../../../alerts.interface';
import { Observable } from 'rxjs';
import { AlertsState } from '../../../../state/alerts.state';

@Component({
  selector: 'alerts-string-filter',
  templateUrl: './alerts-string-filter.component.html',
  styleUrls: ['./alerts-string-filter.component.scss']
})
export class AlertsStringFilterComponent implements OnInit {
  alertFilterForm: FormGroup;
  @Select(AlertsState.getAllAttributeValues)
  attributeValues$: Observable<string[]>;
  filter: AlertFilterModel;
  @Output() change = new EventEmitter<AlertFilterEvent>();
  @Input('stringFilter') set setStringFilter(stringFilter: AlertFilterModel) {
    const { modelValues } = stringFilter;
    const [value] = modelValues;
    this.filter = stringFilter;
    this.alertFilterForm.setValue({ value }, { emitEvent: false });
  }

  constructor(private fb: FormBuilder, private store: Store) {
    this.createForm();
  }

  ngOnInit() {
    this.store.dispatch(new LoadAllAttributeValues());
  }

  createForm() {
    this.alertFilterForm = this.fb.group({
      value: ['']
    });
    this.alertFilterForm.valueChanges.subscribe(({ value }) => {
      const formIsValid = !this.alertFilterForm.invalid;
      if (formIsValid) {
        console.log('isValid');
        this.filter.modelValues = [value];
        this.change.emit({ filter: this.filter, isValid: formIsValid });
      }
    });
  }
}
