import {
  Component,
  OnInit,
  OnDestroy,
  ViewChild,
  ElementRef
} from '@angular/core';
import { MatDialog, MatSidenav } from '@angular/material';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import * as Bowser from 'bowser';

import { Dashboard } from '../../models/dashboard.interface';
import { ConfirmDialogComponent } from '../dialogs/confirm-dialog/confirm-dialog.component';
import { DashboardService } from '../../services/dashboard.service';
import { GlobalFilterService } from '../../services/global-filter.service';
import { ObserveService } from '../../services/observe.service';
import { FirstDashboardGuard } from '../../guards';
import {
  JwtService,
  ConfigService,
  ToastService,
  HtmlDownloadService
} from '../../../../common/services';
import { PREFERENCES } from '../../../../common/services/configuration.service';

import { BehaviorSubject, Observable, Subscription, of } from 'rxjs';
import { map, catchError, flatMap } from 'rxjs/operators';
import * as get from 'lodash/get';
import * as filter from 'lodash/filter';
import * as isEmpty from 'lodash/isEmpty';

const browser = get(
  Bowser.getParser(window.navigator.userAgent).getBrowser(),
  'name'
);

@Component({
  selector: 'observe-view',
  styleUrls: ['./observe-view.component.scss'],
  templateUrl: './observe-view.component.html',
  providers: [DashboardService, GlobalFilterService]
})
export class ObserveViewComponent implements OnInit, OnDestroy {
  public dashboardId: string;
  public subCategoryId: string;
  public dashboard: Dashboard;
  public requester = new BehaviorSubject({});
  private listeners: Array<Subscription> = [];
  public hasAutoRefresh = false;
  public shouldAutoRefresh = true;
  private isDefault = false;
  public privileges = {
    create: false,
    delete: false,
    edit: false,
    download: false
  };
  @ViewChild('filterSidenav') sidenav: MatSidenav;
  @ViewChild('downloadContainer') downloadContainer: ElementRef;
  hasKPIs = false;

  constructor(
    public dialog: MatDialog,
    private observe: ObserveService,
    private _downloadService: HtmlDownloadService,
    private guard: FirstDashboardGuard,
    private dashboardService: DashboardService,
    private router: Router,
    private filters: GlobalFilterService,
    private configService: ConfigService,
    private jwt: JwtService,
    private _route: ActivatedRoute,
    public _toastMessage: ToastService
  ) {
    const navigationListener = this.router.events.subscribe((e: any) => {
      if (e instanceof NavigationEnd) {
        this.initialise();
      }
    });

    this.listeners.push(navigationListener);
  }

  ngOnInit() {}

  initialise() {
    const snapshot = this._route.snapshot;
    const { subCategory } = snapshot.params;
    const { dashboard } = snapshot.queryParams;
    this.dashboardId = dashboard;
    this.subCategoryId = subCategory;
    this.dashboard = null;

    this.loadPrivileges();
    if (this.dashboardId) {
      this.loadDashboard().subscribe(() => {
        this.startAutoRefresh();
      });
    }

    this.checkDefaultDashboard();
  }

  ngOnDestroy() {
    this.dashboardService.unsetAutoRefresh(
      (this.dashboard || { entityId: null }).entityId
    );
    this.listeners.forEach(l => l.unsubscribe());
  }

  toggleDefault() {
    this.isDefault = !this.isDefault;
    const payload = [
      {
        key: PREFERENCES.DEFAULT_DASHBOARD,
        value: this.dashboardId
      },
      {
        key: PREFERENCES.DEFAULT_DASHBOARD_CAT,
        value: this.subCategoryId
      }
    ];

    const action = this.isDefault
      ? this.configService.saveConfig(payload)
      : this.configService.deleteConfig(payload, false);

    action.subscribe(
      () => this.checkDefaultDashboard(),
      () => this.checkDefaultDashboard()
    );
  }

  /**
   * Checks if current dashboard is set as default dashboard
   *
   * @returns {undefined}
   */
  checkDefaultDashboard() {
    this.isDefault =
      this.configService.getPreference(PREFERENCES.DEFAULT_DASHBOARD) ===
      this.dashboardId;
  }

  stopAutoRefresh() {
    this.dashboardService.unsetAutoRefresh(this.dashboard.entityId);
  }

  modifyAutoRefresh({ checked }) {
    if (checked) {
      this.startAutoRefresh();
      this.refreshDashboard();
    } else {
      this.stopAutoRefresh();
    }
  }

  startAutoRefresh() {
    if (!this.dashboard.autoRefreshEnabled) {
      this.hasAutoRefresh = false;
      this.shouldAutoRefresh = false;
      this.dashboardService.unsetAutoRefresh(this.dashboard.entityId);
      return;
    }

    this.hasAutoRefresh = true;
    this.shouldAutoRefresh = true;
    this.dashboardService.setAutoRefresh(this.dashboard);
    const autoRefreshListener = this.dashboardService
      .getAutoRefreshSubject(this.dashboard.entityId)
      .subscribe(({ dashboardId }) => {
        if (dashboardId !== this.dashboard.entityId) {
          return;
        }
        this.refreshDashboard();
      });
    this.listeners.push(autoRefreshListener);
  }

  refreshDashboard() {
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
    const dashboardId = this.dashboard.entityId;
    this.observe
      .deleteDashboard(this.dashboard)
      .pipe(
        flatMap(() => {
          if (
            this.configService.getPreference(PREFERENCES.DEFAULT_DASHBOARD) ===
            dashboardId
          ) {
            return this.configService.deleteConfig(
              [
                {
                  key: PREFERENCES.DEFAULT_DASHBOARD,
                  value: dashboardId
                }
              ],
              true
            );
          } else {
            return of(true);
          }
        })
      )
      .subscribe(() => {
        this.observe.reloadMenu().subscribe(menu => {
          this.observe.updateSidebar(menu);
          this.guard.redirectToFirstDash(null, menu, true);
        });
      });
  }

  downloadDashboard() {
    const nativeElement = this.downloadContainer.nativeElement;
    const fileName = this.dashboard.name;
    const mapBoxComponents = Array.from(
      nativeElement.getElementsByTagName('map-box')
    );

    if (browser !== 'Chrome' && !isEmpty(mapBoxComponents)) {
      const title = 'Browser not supported.';
      const msg =
        'Downloading dashboard with map analyses only works with Chrome browser at the moment.';
      this._toastMessage.warn(msg, title);
      return;
    }

    const elemToDownload = nativeElement.getElementsByTagName('gridster')[0];

    this._downloadService.downloadDashboard(elemToDownload, fileName);
  }

  editDashboard(): void {
    this.router.navigate(['observe/designer'], {
      queryParams: {
        dashboardId: this.dashboard.entityId,
        mode: 'edit',
        categoryId: this.subCategoryId
      }
    });
  }

  createDashboard(): void {
    this.router.navigate(['observe/designer'], {
      queryParams: { mode: 'create', categoryId: this.subCategoryId }
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
  onApplyGlobalFilter(data): void {
    if (!data) {
      this.sidenav.close();
      return;
    }

    this.filters.onApplyFilter.next(data.analysisFilters);
    this.filters.onApplyKPIFilter.next(data.kpiFilters);
    this.sidenav.close();
  }

  loadDashboard(): Observable<Dashboard> {
    return this.observe.getDashboard(this.dashboardId).pipe(
      map((data: Dashboard) => {
        this.dashboard = data;
        this.loadPrivileges();
        this.checkForKPIs();
        return data;
      }),
      catchError(error => {
        return of(error);
      })
    );
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

    this.privileges.download = this.jwt.hasPrivilege('EXPORT', {
      module: moduleName,
      subCategoryId: this.subCategoryId,
      creatorId: this.dashboard.createdBy
    });
  }
}