import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { MatDialog, MatSidenav } from '@angular/material';
import { Transition } from '@uirouter/angular';

import { Dashboard } from '../../models/dashboard.interface';
import { ConfirmDialogComponent } from '../dialogs/confirm-dialog/confirm-dialog.component';
import { CreateDashboardComponent } from '../create-dashboard/create-dashboard.component';
import { DashboardService } from '../../services/dashboard.service';
import { GlobalFilterService } from '../../services/global-filter.service';
import { ObserveService } from '../../services/observe.service';
import { JwtService } from '../../../../../login/services/jwt.service';
import { HeaderProgressService } from '../../../../common/services/header-progress.service';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import 'rxjs/add/observable/of';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import * as get from 'lodash/get';
import * as filter from 'lodash/filter';

const template = require('./observe-view.component.html');

@Component({
  selector: 'observe-view',
  template,
  providers: [DashboardService, GlobalFilterService]
})
export class ObserveViewComponent implements OnInit, OnDestroy {
  private dashboardId: string;
  private subCategoryId: string;
  private dashboard: Dashboard;
  public requester = new BehaviorSubject({});
  private listeners: Array<Subscription> = [];
  private privileges = {
    create: false,
    delete: false,
    edit: false
  };
  @ViewChild('filterSidenav') sidenav: MatSidenav;
  hasKPIs: boolean = false;

  constructor(
    public dialog: MatDialog,
    private observe: ObserveService,
    private dashboardService: DashboardService,
    private filters: GlobalFilterService,
    private headerProgress: HeaderProgressService,
    private jwt: JwtService,
    private transition: Transition
  ) {
    this.dashboardId = this.transition.params().dashboard;
    this.subCategoryId = this.transition.params().subCategory;

    this.loadPrivileges();
  }

  ngOnInit() {
    if (this.dashboardId) {
      this.loadDashboard().subscribe(() => {
        this.startAutoRefresh();
      });
    }
  }

  ngOnDestroy() {
    this.dashboardService.unsetAutoRefresh(
      (this.dashboard || { entityId: null }).entityId
    );
    this.listeners.forEach(l => l.unsubscribe());
  }

  stopAutoRefresh() {
    this.dashboardService.unsetAutoRefresh(this.dashboard.entityId);
  }

  startAutoRefresh() {
    if (!this.dashboard.autoRefreshEnabled) {
      this.dashboardService.unsetAutoRefresh(this.dashboard.entityId);
      return;
    }

    this.dashboardService.setAutoRefresh(this.dashboard);
    const autoRefreshListener = this.dashboardService
      .getAutoRefreshSubject(this.dashboard.entityId)
      .subscribe(({ dashboardId }) => {
        if (dashboardId !== this.dashboard.entityId) return;
        this.refreshDashboard();
      });
    this.listeners.push(autoRefreshListener);
  }

  refreshDashboard() {
    console.log(`refreshing at ${new Date()}`);
    this.requester.next({ action: 'refresh' });
  }

  confirmDelete() {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        message: 'Are you sure you want to delete this dashboard?',
        actionButton: 'Delete',
        actionColor: 'warn'
      }
    });

    dialogRef.afterClosed().subscribe((result: Boolean) => {
      if (!result) {
        return;
      }

      this.deleteDashboard();
    });
  }

  deleteDashboard(): void {
    this.headerProgress.show();
    this.observe.deleteDashboard(this.dashboard).subscribe(
      () => {
        this.observe.reloadMenu().subscribe(
          menu => {
            this.headerProgress.hide();
            this.observe.updateSidebar(menu);
            this.observe.redirectToFirstDash(menu, true);
          },
          () => {
            this.headerProgress.hide();
          }
        );
      },
      () => {
        this.headerProgress.hide();
      }
    );
  }

  editDashboard(): void {
    this.dialog.open(CreateDashboardComponent, {
      panelClass: 'full-screen-dialog',
      data: {
        dashboard: this.dashboard,
        mode: 'edit'
      },
      maxWidth: '1600px'
    });
  }

  createDashboard(): void {
    this.dialog.open(CreateDashboardComponent, {
      panelClass: 'full-screen-dialog',
      data: {
        mode: 'create'
      },
      maxWidth: '1600px'
    });
  }

  /**
   * Pushes a new event to global filter service which
   * individual analyses are listening to. This triggers
   * updates in those analyses.
   *
   * Closes the sidebar.
   *
   * @param {any} globalFilters
   * @returns {void}
   * @memberof ObserveViewComponent
   */
  onApplyGlobalFilter(globalFilters): void {
    if (!globalFilters) {
      this.sidenav.close();
      return;
    }

    this.filters.onApplyFilter.next(globalFilters);
    this.sidenav.close();
  }

  loadDashboard(): Observable<Dashboard> {
    this.headerProgress.show();
    return this.observe
      .getDashboard(this.dashboardId)
      .map((data: Dashboard) => {
        this.dashboard = data;
        this.loadPrivileges();
        this.checkForKPIs();
        this.headerProgress.hide();
        return data;
      })
      .catch(error => {
        this.headerProgress.hide();
        return Observable.of(error);
      });
  }

  /**
   * checkForKPIs - Checks if dashboard has any KPI tiles
   *
   * @returns {void}
   */
  checkForKPIs(): void {
    const tiles = get(this.dashboard, 'tiles', []);
    const kpis = filter(tiles, t => t.type === 'kpi');
    this.hasKPIs = kpis && kpis.length > 0;
  }

  filterSidenavStateChange(data) {
    this.filters.onSidenavStateChange.next(data);
  }

  loadPrivileges(): void {
    if (!this.subCategoryId) {
      return;
    }

    const moduleName = 'OBSERVE';

    this.privileges.create = this.jwt.hasPrivilege('CREATE', {
      module: moduleName,
      subCategoryId: this.subCategoryId
    });

    if (!this.dashboard) {
      return;
    }

    this.privileges.edit = this.jwt.hasPrivilege('EDIT', {
      module: moduleName,
      subCategoryId: this.subCategoryId,
      creatorId: this.dashboard.createdBy
    });

    this.privileges.delete = this.jwt.hasPrivilege('DELETE', {
      module: moduleName,
      subCategoryId: this.subCategoryId,
      creatorId: this.dashboard.createdBy
    });
  }
}
