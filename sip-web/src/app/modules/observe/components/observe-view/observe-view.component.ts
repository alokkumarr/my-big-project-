import {
  Component,
  OnInit,
  OnDestroy,
  ViewChild,
  ElementRef
} from '@angular/core';
import { MatDialog, MatSidenav } from '@angular/material';
import * as html2pdf from 'html2pdf.js';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import * as Bowser from 'bowser';

import { saveAs } from 'file-saver/FileSaver';
import { Dashboard } from '../../models/dashboard.interface';
import { ConfirmDialogComponent } from '../dialogs/confirm-dialog/confirm-dialog.component';
import { DashboardService } from '../../services/dashboard.service';
import { GlobalFilterService } from '../../services/global-filter.service';
import { ObserveService } from '../../services/observe.service';
import { FirstDashboardGuard } from '../../guards';
import {
  JwtService,
  ConfigService,
  ToastService
} from '../../../../common/services';
import { PREFERENCES } from '../../../../common/services/configuration.service';
import { dataURItoBlob } from '../../../../common/utils/dataURItoBlob';

import { BehaviorSubject, Observable, Subscription, of } from 'rxjs';
import { map, catchError, flatMap } from 'rxjs/operators';
import * as get from 'lodash/get';
import * as filter from 'lodash/filter';
import * as isEmpty from 'lodash/isEmpty';
import * as lodashMap from 'lodash/map';
import * as forEach from 'lodash/forEach';

function downloadDataUrlFromJavascript(filename, dataUrl) {
  const blob = dataURItoBlob(dataUrl);
  saveAs(blob, filename);
}

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

  imageUrl2DataUrl(imageUrl) {
    return fetch(imageUrl)
      .then(response => response.blob())
      .then(
        blob =>
          new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onloadend = () => resolve(reader.result);
            reader.onerror = reject;
            reader.readAsDataURL(blob);
          })
      );
  }

  changeMapCanvasesToImage() {
    const mapBoxComponents = Array.from(
      this.downloadContainer.nativeElement.getElementsByTagName('map-box')
    );

    const backupImgCanvasPairs = [];
    const onImgLoadPromises = lodashMap(mapBoxComponents, comp => {
      const imageUrl = comp.dataset['imageUrl'];
      const canvasContainer = comp.querySelector('.mapboxgl-canvas-container');
      const canvas = canvasContainer.querySelector('canvas');
      const { height, width } = canvas.style;
      const imageElem = document.createElement('img');

      return this.imageUrl2DataUrl(imageUrl).then((dataUrl: string) => {
        imageElem.src = dataUrl;
        imageElem.crossOrigin = 'anonymous';
        imageElem.height = parseInt(height, 10);
        imageElem.width = parseInt(width, 10);
        backupImgCanvasPairs.push({ canvas, imageElem });
        canvas.replaceWith(imageElem);
      });
    });

    if (isEmpty(onImgLoadPromises)) {
      return Promise.resolve([]);
    } else {
      return Promise.all(onImgLoadPromises).then(() => {
        return backupImgCanvasPairs;
      });
    }
  }

  downloadDashboard() {
    const mapBoxComponents = Array.from(
      this.downloadContainer.nativeElement.getElementsByTagName('map-box')
    );

    if (browser !== 'Chrome' && !isEmpty(mapBoxComponents)) {
      const title = 'Browser not supported.';
      const msg =
        'Downloading dashboard with map analyses only works with Chrome browser at the moment.';
      this._toastMessage.warn(msg, title);
      return;
    }

    this.changeMapCanvasesToImage().then(backupImgCanvasPairs => {
      this.turnHtml2pdf().then(() => {
        forEach(backupImgCanvasPairs, ({ canvas, imageElem }) => {
          imageElem.replaceWith(canvas);
        });
      });
    });
  }

  turnHtml2pdf() {
    const FILE_NAME = this.dashboard.name;
    const elem = this.downloadContainer.nativeElement.getElementsByTagName(
      'gridster'
    )[0];

    /* Set overflow to visible manually to fix safari's bug. Without this,
     * safari downloads a blank image */
    const overflow = elem.style.overflow;
    elem.style.overflow = 'visible';

    return (
      html2pdf()
        .from(elem)
        .set({
          margin: 1,
          filename: `${FILE_NAME}.pdf`,
          image: { type: 'jpeg', quality: 1 },
          html2canvas: {
            backgroundColor: '#f4f5f4',
            scale: 2,
            width: elem.scrollWidth,
            height: elem.scrollHeight,
            windowWidth: elem.scrollWidth,
            windowHeight: elem.scrollHeight,
            onclone: cloned => {
              // need this for html2pdf download to wirk with gridster library
              // https://github.com/niklasvh/html2canvas/issues/1720
              const gridsterItems = Array.from(
                cloned.querySelectorAll('gridster-item')
              );
              forEach(gridsterItems, item => {
                item.style.transition = 'unset';
              });
            }
          },
          jsPDF: {
            unit: 'px',
            orientation: 'landscape',
            format: [elem.scrollHeight + 50, elem.scrollWidth]
          }
        })
        // .save(); // comment this and uncomment following lines if png is needed instead of pdf
        .outputImg('datauristring')
        .then(uri => downloadDataUrlFromJavascript(`${FILE_NAME}.png`, uri))
        .then(() => {
          elem.style.overflow = overflow;
        })
    );
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
    if (!isEmpty(data.analysisFilters)) {
      this.filters.onApplyFilter.next(data.analysisFilters);
    }
    if (!isEmpty(data.kpiFilters)) {
      this.filters.onApplyKPIFilter.next(data.kpiFilters);
    }
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
