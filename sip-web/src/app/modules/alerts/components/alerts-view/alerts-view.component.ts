import { Component, OnInit } from '@angular/core';
import { AlertsService } from '../../services/alerts.service';
import { GridData } from '../../alerts.interface';
import { Select } from '@ngxs/store';
import { AlertsFilterState } from './state/alerts.state';
import { Observable } from 'rxjs';

@Component({
  selector: 'alerts-view',
  templateUrl: './alerts-view.component.html',
  styleUrls: ['./alerts-view.component.scss']
})
export class AlertsViewComponent implements OnInit {
  public alertsDataLoader: (options: {}) => Promise<GridData>;
  @Select(AlertsFilterState.getAlertFilters) filters$: Observable<any>;

  constructor(public _alertService: AlertsService) {
    this.setAlertLoaderForGrid();
  }

  ngOnInit() {}

  fetchLateshAlerts() {
    this.setAlertLoaderForGrid();
  }

  setAlertLoaderForGrid() {
    this.alertsDataLoader = (options: {}) => {
      return this._alertService
        .getAlertsStatesForGrid(options)
        .then(result => ({
          data: result.data,
          totalCount: result.totalCount
        }));
    };
  }
}
