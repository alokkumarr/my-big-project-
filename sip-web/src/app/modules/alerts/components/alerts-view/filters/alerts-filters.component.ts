import { Component, OnInit } from '@angular/core';
import { Select, Store } from '@ngxs/store';
import {
  EditAlertFilter,
  ApplyAlertFilters,
  ResetAlertFilters,
  EditAlertValidity
} from '../../../state/alerts.actions';
import { AlertsState } from '../../../state/alerts.state';
import { Observable } from 'rxjs';
import { AlertFilterModel, AlertFilterEvent } from '../../../alerts.interface';
@Component({
  selector: 'alerts-filters',
  templateUrl: 'alerts-filters.component.html'
})
export class AlertsFiltersComponent implements OnInit {
  @Select(AlertsState.getEditedAlertFilters) editedFilters$: Observable<
    AlertFilterModel[]
  >;
  @Select(AlertsState.areEditedAlertsValid) areEditedAlertsValid$: Observable<
    boolean
  >;
  @Select(AlertsState.areFiltersApplied) areFiltersApplied$: Observable<
    boolean
  >;
  constructor(private store: Store) {}

  ngOnInit() {}

  applyFilters() {
    this.store.dispatch(new ApplyAlertFilters());
  }

  resetFilters() {
    this.store.dispatch(new ResetAlertFilters());
  }

  onFilterChange(index, { filter, isValid }: AlertFilterEvent) {
    this.store.dispatch([
      new EditAlertFilter(filter, index),
      new EditAlertValidity(isValid, index)
    ]);
  }
}
