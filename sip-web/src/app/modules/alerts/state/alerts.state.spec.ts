import { TestBed, async } from '@angular/core/testing';
import { NgxsModule, Store } from '@ngxs/store';
import { AlertsState } from './alerts.state';
import { AlertsService } from '../services/alerts.service';
import {
  // AlertFilterModel,
  AlertChartData,
  AlertDateSeverity,
  AlertDateCount
} from '../alerts.interface';
import {
  // ApplyAlertFilters,
  LoadAllAlertCount,
  LoadAllAlertSeverity
} from './alerts.actions';
import { of } from 'rxjs';

// const selectedAlertFilter: AlertFilterModel = {
//   preset: 'YTD',
//   groupBy: 'StartTime'
// };

// const defaultAlertFilters: AlertFilterModel = {
//   preset: 'TW',
//   groupBy: 'StartTime'
// };

const alertCountArray: AlertDateCount[] = [
  {
    count: '1',
    date: '2019-01-01'
  },
  {
    count: '2',
    date: '2019-01-02'
  },
  {
    count: '3',
    date: '2019-01-03'
  },
  {
    count: '4',
    date: '2019-01-04'
  }
];

const alertCountChartData: AlertChartData = {
  x: ['2019-01-01', '2019-01-02', '2019-01-03', '2019-01-04'],
  y: [1, 2, 3, 4]
};

const alertSeverityArray: AlertDateSeverity[] = [
  {
    count: '1',
    alertSeverity: 'critical'
  },
  {
    count: '2',
    alertSeverity: 'medium'
  },
  {
    count: '3',
    alertSeverity: 'low'
  },
  {
    count: '4',
    alertSeverity: 'warning'
  }
];

const alertSeverityChartData: AlertChartData = {
  x: ['critical', 'medium', 'low', 'warning'],
  y: [1, 2, 3, 4]
};

class AlertsServiceStub {
  getAllAlertsCount() {
    return of(alertCountArray);
  }

  getAlertCountById(id, alertFilter) {
    return of(alertCountArray);
  }

  getAllAlertsSeverity() {
    return of(alertSeverityArray);
  }
}

describe('Alerts actions', () => {
  let store: Store;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [NgxsModule.forRoot([AlertsState])],
      providers: [{ provide: AlertsService, useClass: AlertsServiceStub }]
    }).compileComponents();
    store = TestBed.get(Store);
  }));

  // it('should have a default date filter', () => {
  //   store
  //     .selectOnce(state => state.alerts.alertFilter)
  //     .subscribe((alertsFilters: AlertFilterModel) => {
  //       if (alertsFilters) {
  //         expect(alertsFilters).toEqual(
  //           jasmine.objectContaining(defaultAlertFilters)
  //         );
  //       }
  //     });
  // });

  // it('should create an action and add an item', () => {
  //   store.dispatch(new ApplyAlertFilters());
  //   store
  //     .selectOnce(state => state.alerts.alertFilter)
  //     .subscribe((alertsFilters: AlertFilterModel) => {
  //       if (alertsFilters) {
  //         expect(alertsFilters).toEqual(
  //           jasmine.objectContaining(selectedAlertFilter)
  //         );
  //       }
  //     });
  // });

  it('should load all the alert counts chart data', () => {
    store.dispatch(new LoadAllAlertCount());
    store
      .selectOnce(state => state.alerts.allAlertsCountChartData)
      .subscribe((allAlertsCountChartData: AlertChartData) => {
        if (allAlertsCountChartData) {
          expect(allAlertsCountChartData).toEqual(alertCountChartData);
        }
      });
  });

  it('should load the alert severity chart data', () => {
    store.dispatch(new LoadAllAlertSeverity());
    store
      .selectOnce(state => state.alerts.allAlertsSeverityChartData)
      .subscribe((allAlertsSeverityChartData: AlertChartData) => {
        if (allAlertsSeverityChartData) {
          expect(allAlertsSeverityChartData).toEqual(alertSeverityChartData);
        }
      });
  });
});
