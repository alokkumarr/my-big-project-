import { Component, OnInit } from '@angular/core';
import { AlertsService } from '../../services/alerts.service';
@Component({
  selector: 'alerts-view',
  templateUrl: './alerts-view.component.html',
  styleUrls: ['./alerts-view.component.scss']
})
export class AlertsViewComponent implements OnInit {
  public alertsDataLoader: (options: {}) => Promise<{
    data: any[];
    totalCount: number;
  }>;
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
