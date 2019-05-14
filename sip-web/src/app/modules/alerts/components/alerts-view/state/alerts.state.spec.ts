import { TestBed, async } from '@angular/core/testing';
import { NgxsModule, Store } from '@ngxs/store';
import { AlertsFilterState } from './alerts.state';
import { ApplyAlertFilters } from './alerts.actions';
import { AlertFiltersModel } from './alerts.model';

const ALERT_FILTER_PAYLOAD: AlertFiltersModel = {
  preset: 'YTD',
  groupBy: 'StartTime'
};

describe('Alerts actions', () => {
  let store: Store;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [NgxsModule.forRoot([AlertsFilterState])]
    }).compileComponents();
    store = TestBed.get(Store);
  }));

  it('should create an action and add an item', () => {
    store.dispatch(new ApplyAlertFilters(ALERT_FILTER_PAYLOAD));
    store
      .selectOnce(state => state.alertsFilters)
      .subscribe((alertsFilters: AlertFiltersModel) => {
        expect(alertsFilters).toEqual(
          jasmine.objectContaining(ALERT_FILTER_PAYLOAD)
        );
      });
  });
});
