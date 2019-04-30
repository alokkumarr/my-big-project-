import { Component, OnInit } from '@angular/core';
import { AlertsService } from '../../services/alerts.service';
import { GridData } from '../../alerts.interface';

@Component({
  selector: 'alerts-view',
  templateUrl: './alerts-view.component.html',
  styleUrls: ['./alerts-view.component.scss']
})
export class AlertsViewComponent implements OnInit {
  public alertsDataLoader: (options: {}) => Promise<GridData>;

  constructor(public _alertService: AlertsService) {
    this.alertsDataLoader = (options: {}) => {
      return this._alertService
        .getAlertsStatesForGrid(options)
        .then(result => ({
          data: result.data,
          totalCount: result.totalCount
        }));
    };
  }

  ngOnInit() {}
}
