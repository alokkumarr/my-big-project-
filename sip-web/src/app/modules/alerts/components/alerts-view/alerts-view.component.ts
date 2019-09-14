import { Component, OnInit } from '@angular/core';
import { Select, Store } from '@ngxs/store';
import { Observable } from 'rxjs';

import {
  LoadAllAlertCount,
  LoadAllAlertSeverity
} from '../../state/alerts.actions';
import { AlertsService } from '../../services/alerts.service';
import {
  GridData,
  AlertChartData,
  AlertFilterModel
} from '../../alerts.interface';
import { AlertsState } from '../../state/alerts.state';

@Component({
  selector: 'alerts-view',
  templateUrl: './alerts-view.component.html',
  styleUrls: ['./alerts-view.component.scss']
})
export class AlertsViewComponent implements OnInit {
  public alertsDataLoader: (options: {}) => Promise<GridData>;
  @Select(AlertsState.getAlertFilterString) filterString$: Observable<string>;
  @Select(AlertsState.getAlertFilter) filter$: Observable<AlertFilterModel>;
  @Select(AlertsState.getAllAlertsCountChartData)
  allAlertCountChartData$: Observable<AlertChartData>;
  @Select(AlertsState.getAllAlertsSeverityChartData)
  allAlertSeverityChartData$: Observable<AlertChartData>;

  public additionalCountChartOptions = {
    chart: {
      type: 'areaspline'
    }
  };

  public additionalSeverityChartOptions = {
    chart: {
      type: 'bar'
    },
    colors: ['#e4524c', '#ffbe00', '#24b18c', '#a5b7ce'],
    plotOptions: {
      series: {
        colorByPoint: true
      },
      bar: {
        dataLabels: {
          enabled: true
        }
      }
    },
    xAxis: {
      lineWidth: 0,
      tickWidth: 0,
      gridLineWidth: 0
    },
    yAxis: {
      gridLineWidth: 0,
      min: 0,
      labels: {
        enabled: false
      },
      visible: false
    }
  };

  constructor(private _alertService: AlertsService, private _store: Store) {
    this.setAlertLoaderForGrid();
  }

  ngOnInit() {
    this.filterString$.subscribe(() => {
      this.setAlertLoaderForGrid();
      this._store.dispatch(new LoadAllAlertCount());
      this._store.dispatch(new LoadAllAlertSeverity());
    });
  }

  fetchLateshAlerts() {
    this.setAlertLoaderForGrid();
    this._store.dispatch(new LoadAllAlertCount());
    this._store.dispatch(new LoadAllAlertSeverity());
  }

  setAlertLoaderForGrid() {
    this.alertsDataLoader = options => {
      return this._alertService
        .getAlertsStatesForGrid(options)
        .then(result => ({
          data: result.data,
          totalCount: result.totalCount
        }));
    };
  }
}
