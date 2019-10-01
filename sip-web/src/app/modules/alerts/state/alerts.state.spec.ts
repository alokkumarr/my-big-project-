import { TestBed, async } from '@angular/core/testing';
import { NgxsModule, Store } from '@ngxs/store';
import { AlertsState, defaultAlertFilters } from './alerts.state';
import { AlertsService } from '../services/alerts.service';
import {
  AlertFilterModel,
  AlertChartData,
  AlertDateSeverity,
  AlertDateCount
} from '../alerts.interface';
import {
  ApplyAlertFilters,
  EditAlertFilter,
  LoadAllAlertCount,
  LoadAllAlertSeverity
} from './alerts.actions';
import { of } from 'rxjs';

const yesterdayDateFilter = {
  preset: 'Yesterday',
  fieldName: 'starttime',
  type: 'date'
};

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

  it('should have a default date filter', () => {
    store
      .selectOnce(state => state.alerts.alertFilters)
      .subscribe((alertsFilters: AlertFilterModel[]) => {
        if (alertsFilters) {
          expect(alertsFilters).toEqual(defaultAlertFilters);
        }
      });
  });

  it('should modify a filter', () => {
    store.dispatch(new EditAlertFilter(yesterdayDateFilter, 0));
    store
      .selectOnce(state => state.alerts.alertFilter)
      .subscribe((alertsFilters: AlertFilterModel[]) => {
        if (alertsFilters) {
          expect(alertsFilters[0]).toEqual(yesterdayDateFilter);
        }
      });
  });
  it('should apply modified filters', () => {
    store.dispatch(new ApplyAlertFilters());
    store
      .selectOnce(state => ({
        alertFilters: state.alerts.alertFilters,
        editedAlertFilters: state.alerts.editedAlertFilters
      }))
      .subscribe(({ alertFilters, editedAlertFilters }) => {
        if (alertFilters && editedAlertFilters) {
          expect(alertFilters).toEqual(editedAlertFilters);
        }
      });
  });

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
