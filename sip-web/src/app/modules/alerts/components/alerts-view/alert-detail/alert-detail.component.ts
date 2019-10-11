import { Component, Input, OnInit } from '@angular/core';
import { Select, Store } from '@ngxs/store';
import { Observable } from 'rxjs';

import {
  AlertConfig,
  AlertIds,
  AlertChartData
} from '../../../alerts.interface';
import {
  LoadSelectedAlertRuleDetails,
  LoadSelectedAlertCount
} from '../../../state/alerts.actions';
import { AlertsState } from '../../../state/alerts.state';

@Component({
  selector: 'alert-detail',
  templateUrl: './alert-detail.component.html',
  styleUrls: ['./alert-detail.component.scss']
})
export class AlertDetailComponent implements OnInit {
  constructor(private _store: Store) {}
  public alertRuleDetails: AlertConfig;

  @Select(AlertsState.getAlertDateFilterString) filterStrings$: Observable<
    string
  >;

  @Select(AlertsState.getSelectedAlertRuleDetails)
  alertRuleDetails$: Observable<AlertConfig>;

  @Select(AlertsState.getSelectedAlertCountChartData)
  selectedAlertCountChartData$: Observable<AlertChartData>;

  @Input('alertIds')
  set setAlertIds(alertIds: AlertIds) {
    this.alertIds = alertIds;
    if (alertIds) {
      this._store.dispatch([
        new LoadSelectedAlertRuleDetails(alertIds.alertRulesSysId),
        new LoadSelectedAlertCount(alertIds.alertRulesSysId)
      ]);
    }
  }

  public alertIds;

  public additionalCountChartOptions = {
    chart: {
      type: 'area'
    }
  };

  ngOnInit() {
    this.alertRuleDetails$.subscribe(alertRuleDetails => {
      this.alertRuleDetails = alertRuleDetails;
    });
  }
}
