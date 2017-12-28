import { Component, Inject, ViewChild } from '@angular/core';
import { UIRouter } from '@uirouter/angular';
import { MdDialogRef, MD_DIALOG_DATA, MdDialog } from '@angular/material'; import { SaveDashboardComponent } from '../save-dashboard/save-dashboard.component';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { MenuService } from '../../../../common/services/menu.service';
import { Dashboard } from '../../models/dashboard.interface';
import {
  trigger,
  state,
  style,
  animate,
  transition
} from '@angular/animations';

import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import * as map from 'lodash/map';
import * as get from 'lodash/get';
import * as findIndex from 'lodash/findIndex';

import { AnalysisChoiceComponent } from '../analysis-choice/analysis-choice.component';

const template = require('./create-dashboard.component.html');
require('./create-dashboard.component.scss');

const MARGIN_BETWEEN_TILES = 10;

@Component({
  selector: 'create-dashboard',
  template,
  animations: [
    trigger('moveButton', [
      state('empty', style({
        bottom: '50%',
        right: '50%',
        transform: 'translateX(50%)'
      })),
      state('filled', style({
        bottom: '30px',
        right: '30px',
        transform: 'translateX(0%)'
      })),
      transition('* => *', animate('500ms ease-out'))
    ]),
    trigger('hideHelp', [
      state('empty', style({
        opacity: 1
      })),
      state('filled', style({
        opacity: 0
      })),
      transition('* => *', animate('250ms ease-out'))
    ])
  ]
})
export class CreateDashboardComponent {
  public fillState = 'empty';
  public dashboard: Dashboard;
  public requester = new BehaviorSubject({});
  public mode = 'create';

  constructor(public dialogRef: MdDialogRef<CreateDashboardComponent>,
    private dialog: MdDialog,
    private router: UIRouter,
    private menu: MenuService,
    @Inject(MD_DIALOG_DATA) public dialogData: any
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
    const dialogRef = this.dialog.open(AnalysisChoiceComponent);

    dialogRef.afterClosed().subscribe(analysis => {
      if (!analysis) {
        return;
      }

      const item = { cols: 1, rows: 1, analysis, updater: new BehaviorSubject({}) };
      this.requester.next({action: 'add', data: item})
    });
  }

  saveDashboard() {
    this.requester.next({action: 'get'});
  }

  openSaveDialog(dashboard: Dashboard) {
    const dialogRef = this.dialog.open(SaveDashboardComponent, {
      data: {
        dashboard,
        mode: 'create'
      }
    });

    dialogRef.afterClosed().subscribe((result: Dashboard) => {
      if (result) {
        this.dialogRef.afterClosed().subscribe(() => {
          this.updateSideMenu(result);
          this.router.stateService.go('observe.dashboard', {
            dashboard: result.entityId,
            subCategory: result.categoryId
          });
        });
        this.dialogRef.close();
      }
    });
  }

  /* After successful save, update the sidemenu with the dashboard. This saves a network
     request because we already have all the data available to us. */
  updateSideMenu(dashboard: Dashboard) {
    const menu = this.menu.getCachedMenu('OBSERVE') || [];
    let subCategory;
    forEach(menu, category => {
      subCategory = subCategory || find(category.children, subCat => subCat.id.toString() === dashboard.categoryId.toString());
    });

    if (subCategory) {
      subCategory.children = subCategory.children || [];
      const menuEntry = {
        id: dashboard.entityId,
        name: dashboard.name,
        url: `#!/observe/${dashboard.categoryId}?dashboard=${dashboard.entityId}`,
        data: dashboard
      }
      const existing = findIndex(subCategory.children, dash => dash.id === dashboard.entityId);

      if (existing >= 0) {
        subCategory.children.splice(existing, 1, menuEntry);
      } else {
        subCategory.children.push(menuEntry);
      }
    }

    this.menu.updateMenu(menu, 'OBSERVE');
  }
}
