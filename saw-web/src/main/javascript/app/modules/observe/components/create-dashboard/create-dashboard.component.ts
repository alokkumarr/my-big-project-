declare const require: any;

import { Component, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog, MatSidenav } from '@angular/material';
import { UIRouter } from '@uirouter/angular';
import { SaveDashboardComponent } from '../save-dashboard/save-dashboard.component';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { MenuService } from '../../../../common/services/menu.service';
import { ObserveService } from '../../services/observe.service';
import { Dashboard } from '../../models/dashboard.interface';
import { animations } from './create-dashboard.animations';

import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import * as map from 'lodash/map';
import * as get from 'lodash/get';
import * as findIndex from 'lodash/findIndex';

const template = require('./create-dashboard.component.html');
require('./create-dashboard.component.scss');

const MARGIN_BETWEEN_TILES = 10;

@Component({
  selector: 'create-dashboard',
  template,
  animations
})
export class CreateDashboardComponent {
  public fillState = 'empty';
  public dashboard: Dashboard;
  public requester = new BehaviorSubject({});
  public mode = 'create';

  @ViewChild('widgetChoice') widgetSidenav: MatSidenav;

  constructor(public dialogRef: MatDialogRef<CreateDashboardComponent>,
    private dialog: MatDialog,
    private router: UIRouter,
    private menu: MenuService,
    private observe: ObserveService,
    @Inject(MAT_DIALOG_DATA) public dialogData: any
  ) {
    this.dashboard = get(this.dialogData, 'dashboard');
    this.mode = get(this.dialogData, 'mode');
    this.checkEmpty(this.dashboard);
  }

  checkEmpty(dashboard) {
    this.fillState = get(dashboard, 'tiles', []).length > 0 ? 'filled' : 'empty';
  }

  onDashboardChange(data) {
    if (data.changed) {
      this.checkEmpty(data.dashboard);
    } else if (data.save) {
      this.openSaveDialog(data.dashboard);
    }
  }

  ngOnInit() {
  }

  exitCreator(data) {
    this.dialogRef.close(data);
  }

  chooseAnalysis() {
    this.widgetSidenav.open();
  }

  onAnalysisAction(action, data) {
    switch(action) {
    case 'ADD':
      if (!data) return;
      const item = { cols: 1, rows: 1, analysis: data, updater: new BehaviorSubject({}) };
      this.requester.next({action: 'add', data: item})
      break;
    }
  }

  onKPIAction(action, data) {
    // TODO
  }

  onWidgetAction({widget, action, data}) {
    switch(widget) {
    case 'ANALYSIS':
      this.onAnalysisAction(action, data);
      break;
    case 'KPI':
      this.onKPIAction(action, data);
      break;
    }
  }

  saveDashboard() {
    this.requester.next({action: 'get'});
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
        this.dialogRef.afterClosed().subscribe(() => {
          this.updateSideMenu(result);
          this.router.stateService.go('observe.dashboard', {
            dashboard: result.entityId,
            subCategory: result.categoryId
          }, {
            reload: true
          });
        });
        this.dialogRef.close();
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
