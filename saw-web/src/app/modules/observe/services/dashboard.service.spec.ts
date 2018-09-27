
import { TestBed } from '@angular/core/testing';
import { DashboardService } from './dashboard.service';

describe('Dashboard Service', () => {
  let dashboardService;
  beforeEach(done => {
    TestBed.configureTestingModule({
      providers: [DashboardService]
    });

    dashboardService = TestBed.get(DashboardService);
    done();
  });

  it('should exist', () => {
    expect(dashboardService).not.toBeNull();
  });

  it('should add a dashboard auto refresh if enabled', () => {
    dashboardService.setAutoRefresh({
      entityId: 'abc',
      autoRefreshEnabled: true,
      refreshIntervalSeconds: 1000
    });
    expect(dashboardService.getAutoRefreshSubject('abc')).not.toBeNull();
    dashboardService.unsetAutoRefresh('abc');
  });

  it('should not add dashboard auto refresh if disabled', () => {
    dashboardService.setAutoRefresh({
      entityId: 'xyz',
      autoRefreshEnabled: false,
      refreshIntervalSeconds: 1000
    });
    expect(dashboardService.getAutoRefreshSubject('xyz')).toBeNull();
    dashboardService.unsetAutoRefresh('xyz');
  });

  it('should unset auto refresh correctly', () => {
    dashboardService.setAutoRefresh({
      entityId: '123',
      autoRefreshEnabled: true,
      refreshIntervalSeconds: 1000
    });
    expect(dashboardService.getAutoRefreshSubject('123')).not.toBeNull();
    dashboardService.unsetAutoRefresh('123');
    expect(dashboardService.getAutoRefreshSubject('123')).toBeNull();
  });
});
