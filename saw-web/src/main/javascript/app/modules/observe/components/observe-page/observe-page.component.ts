declare function require(string): string;

import { Inject, OnInit } from '@angular/core';
import { MdDialog, MdIconRegistry } from '@angular/material';

import * as forEach from 'lodash/forEach';
import * as map from 'lodash/map';

import { CreateDashboardComponent } from '../create-dashboard/create-dashboard.component';
import { ObserveService } from '../../services/observe.service';
import { MenuService } from '../../../../common/services/menu.service';
import { AnalyzeService } from '../../../analyze/services/analyze.service';

import { Dashboard } from '../../models/dashboard.interface';


const template = require('./observe-page.component.html');
require('./observe-page.component.scss');
// import * as template from './observe-page.component.html';
// import * as style from './observe-page.component.scss';
// import {OBSERVE_FILTER_SIDENAV_ID} from '../filter-sidenav/filter-sidenav.component';

import { Component } from '@angular/core';

@Component({
  selector: 'observe-page',
  styles: [],
  template: template
})
export class ObservePageComponent implements OnInit {
  private dashboardId: string;
  private dashboard: Dashboard;

  constructor(public dialog: MdDialog,
    private iconRegistry: MdIconRegistry,
    private analyze: AnalyzeService,
    private menu: MenuService,
    private observe: ObserveService,
    @Inject('$stateParams') private $stateParams,
    @Inject('$componentHandler') private $componentHandler
  ) {
    this.iconRegistry.setDefaultFontSetClass('icomoon');
    this.dashboardId = this.$stateParams.dashboardId;
  }

  createDashboard() {
    this.dialog.open(CreateDashboardComponent, {
      panelClass: 'full-screen-dialog'
    });
  }

  ngOnInit() {
    const leftSideNav = this.$componentHandler.get('left-side-nav')[0];

    // const data = [
    //   {
    //     id: 1,
    //     name: 'My Dashboards',
    //     children: [
    //       { id: 2, name: 'Testing', url: `#!/observe?dashboardId=d8939bf3-d8f4-4ee7-89c4-f2a4fd4abca9::PortalDataSet::1513945502617`}
    //     ]
    //   }
    // ];

    // leftSideNav.update(data, 'OBSERVE');

    this.menu.getMenu('OBSERVE')
      .then(data => {

        forEach(data, category => {
          this.observe.getDashboardsForCategory(category.id).subscribe((dashboards: Array<Dashboard>) => {
            category.children = category.children || [];
            category.children = category.children.concat(map(dashboards, dashboard => ({
              id: dashboard.entityId,
              name: dashboard.name,
              url: `#!/observe?dashboardId=${dashboard.entityId}`,
              data: dashboard
            })));
            leftSideNav.update(data, 'OBSERVE');
          });
        });

        this.analyze.updateMenu(data);

        if (this.dashboardId) {
          this.loadDashboard();
        }
      });
  }

  loadDashboard() {
    this.observe.getDashboard(this.dashboardId).subscribe(data => {
      this.dashboard = data;
    })
  }

};
