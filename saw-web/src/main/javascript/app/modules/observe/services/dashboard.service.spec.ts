import { expect } from 'chai';

import { configureTests } from '../../../../../../test/javascript/helpers/configureTests';
import { TestBed } from '@angular/core/testing';
import { DashboardService } from './dashboard.service';

configureTests();

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
    expect(dashboardService).to.not.be.null;
  });

  it('should add a dashboard auto refresh if enabled', () => {
    dashboardService.setAutoRefresh({
      entityId: 'abc',
      autoRefreshEnabled: true,
      refreshIntervalSeconds: 1000
    });
    expect(dashboardService.getAutoRefreshSubject('abc')).to.not.be.null;
    dashboardService.unsetAutoRefresh('abc');
  });

  it('should not add dashboard auto refresh if disabled', () => {
    dashboardService.setAutoRefresh({
      entityId: 'xyz',
      autoRefreshEnabled: false,
      refreshIntervalSeconds: 1000
    });
    expect(dashboardService.getAutoRefreshSubject('xyz')).to.be.null;
    dashboardService.unsetAutoRefresh('xyz');
  });

  it('should unset auto refresh correctly', () => {
    dashboardService.setAutoRefresh({
      entityId: '123',
      autoRefreshEnabled: true,
      refreshIntervalSeconds: 1000
    });
    expect(dashboardService.getAutoRefreshSubject('123')).to.not.be.null;
    dashboardService.unsetAutoRefresh('123');
    expect(dashboardService.getAutoRefreshSubject('123')).to.be.null;
  });
});
