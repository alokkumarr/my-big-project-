import {
  Component,
  ViewChild,
  OnDestroy,
  OnInit,
  AfterContentInit
} from '@angular/core';
import { Location } from '@angular/common';
import { MatDialog, MatSidenav } from '@angular/material';
import { Router, ActivatedRoute } from '@angular/router';
import { SaveDashboardComponent } from '../save-dashboard/save-dashboard.component';
import { BehaviorSubject, Subscription } from 'rxjs';
import { MenuService } from '../../../../common/services/menu.service';
import { WIDGET_ACTIONS } from '../add-widget/widget.model';
import { ObserveService } from '../../services/observe.service';
import { DashboardService } from '../../services/dashboard.service';
import { Dashboard } from '../../models/dashboard.interface';
import { animations } from './create-dashboard.animations';

import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';
import * as get from 'lodash/get';
import { GlobalFilterService } from '../../services/global-filter.service';

@Component({
  selector: 'create-dashboard',
  templateUrl: './create-dashboard.component.html',
  styleUrls: ['./create-dashboard.component.scss'],
  animations,
  providers: [DashboardService, GlobalFilterService]
})
export class CreateDashboardComponent
  implements OnDestroy, OnInit, AfterContentInit {
  public fillState = 'empty';
  public dashboard: Dashboard;
  public requester = new BehaviorSubject({});
  public mode = 'create';
  public sidebarWidget: string;
  public editItem: any;

  editSubscription: Subscription;

  @ViewChild('widgetChoice') widgetSidenav: MatSidenav;

  constructor(
    public dialog: MatDialog,
    public router: Router,
    public menu: MenuService,
    public globalFilterService: GlobalFilterService,
    public dashboardService: DashboardService,
    public observe: ObserveService,
    private locationService: Location,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    const { dashboardId, mode, categoryId } = this.route.snapshot.queryParams;
    this.mode = mode;
    if (this.mode === 'create') {
      this.dashboard = {
        entityId: null,
        categoryId,
        autoRefreshEnabled: false,
        name: '',
        description: '',
        tiles: [],
        filters: []
      };
      this.checkEmpty(this.dashboard);
    } else {
      this.observe
        .getDashboard(dashboardId)
        .subscribe((dashboard: Dashboard) => {
          this.dashboard = dashboard;
          this.checkEmpty(this.dashboard);
        });
    }
  }

  ngOnDestroy() {
    this.editSubscription && this.editSubscription.unsubscribe();
  }

  ngAfterContentInit() {
    this.subscribeToEdits();
  }

  subscribeToEdits() {
    this.editSubscription = this.dashboardService.onEditItem.subscribe(data => {
      if (isEmpty(data)) {
        return;
      }

      this.sidebarWidget = 'edit';
      this.editItem = data;

      this.widgetSidenav.open();
    });
  }

  openFilters() {
    this.sidebarWidget = 'filter';
    this.widgetSidenav.open();
  }

  checkEmpty(dashboard) {
    this.fillState =
      get(dashboard, 'tiles', []).length > 0 ? 'filled' : 'empty';
  }

  onDashboardChange(data) {
    if (data.changed) {
      this.checkEmpty(data.dashboard);
      this.updateWidgetLog(data.dashboard);
    } else if (data.save) {
      this.openSaveDialog(data.dashboard);
    }
  }

  sidenavStateChange(data) {
    if (this.sidebarWidget === 'filter') {
      this.globalFilterService.onSidenavStateChange.next(data);
    }
  }

  onApplyGlobalFilter(data): void {
    if (!data) {
      this.widgetSidenav.close();
      return;
    }
    if (!isEmpty(data.analysisFilters)) {
      this.globalFilterService.onApplyFilter.next(data.analysisFilters);
    }
    if (!isEmpty(data.kpiFilters)) {
      this.globalFilterService.onApplyKPIFilter.next(data.kpiFilters);
    }
    this.widgetSidenav.close();
  }

  updateWidgetLog(dashboard) {
    const newLog = {};
    forEach(dashboard.tiles, tile => {
      newLog[tile.id] = { type: tile.type };
    });

    this.dashboardService.dashboardWidgets.next(newLog);
  }

  exitCreator() {
    this.locationService.back();
  }

  chooseAnalysis() {
    this.sidebarWidget = 'add';
    this.widgetSidenav.open();
  }

  onAnalysisAction(action, data) {
    /* prettier-ignore */
    switch (action) {
    case 'ADD':
      if (!data) { return; }
      const item = {
        cols: 16,
        rows: 16,
        analysis: data,
        updater: new BehaviorSubject({})
      };
      this.requester.next({ action: 'add', data: item });
      break;
    case 'REMOVE':
      if (!data) { return; }
      this.requester.next({ action: 'remove', data });
      break;
    }
  }

  onKPIAction(action, data) {
    /* prettier-ignore */
    switch (action) {
    case WIDGET_ACTIONS.ADD:
      if (!data) { return; }

      const item = {
        cols: 13,
        rows: 6,
        kpi: data
      };
      this.requester.next({action: 'add', data: item});
      break;
    }
  }

  onBulletAction(action, data) {
    /* prettier-ignore */
    switch (action) {
    case WIDGET_ACTIONS.ADD:
      if (!data) { return; }

      const item = {
        cols: 20,
        rows: 6,
        bullet: data,
        updater: new BehaviorSubject({})
      };
      this.requester.next({action: 'add', data: item});
      break;
    }
  }

  onWidgetAction({ widget, action, data }) {
    /* prettier-ignore */
    switch (widget) {
    case 'ANALYSIS':
      this.onAnalysisAction(action, data);
      break;
    case 'KPI':
      this.onKPIAction(action, data);
      break;
    case 'BULLET':
      this.onBulletAction(action, data);
      break;
    }
  }

  saveDashboard() {
    this.requester.next({ action: 'get' });
  }

  openSaveDialog(dashboard: Dashboard) {
    const dialogRef = this.dialog.open(SaveDashboardComponent, {
      data: {
        dashboard,
        mode: this.mode
      }
    });

    dialogRef.afterClosed().subscribe((result: Dashboard) => {
      if (result) {
        this.updateSideMenu(result);
        this.router.navigate(['observe', result.categoryId], {
          queryParams: { dashboard: result.entityId }
        });
      }
    });
  }

  /* After successful save, update the sidemenu with the dashboard. This saves a network
     request because we already have all the data available to us. */
  updateSideMenu(dashboard: Dashboard) {
    this.observe.reloadMenu().subscribe(menu => {
      this.observe.updateSidebar(menu);
    });
  }
}
