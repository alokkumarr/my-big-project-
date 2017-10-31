declare function require(string): string;

import { MdDialog, MdIconRegistry } from '@angular/material';
import { CreateDashboardComponent } from '../create-dashboard/create-dashboard.component';
import { MenuService } from '../../../../common/services/menu.service';
import { AnalyzeService } from '../../../analyze/services/analyze.service';

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
export class ObservePageComponent {
  constructor(public dialog: MdDialog,
    private iconRegistry: MdIconRegistry,
    private analyze: AnalyzeService,
    private menu: MenuService
  ) {
    // this.$componentHandler = $componentHandler;
    // this.MenuService = MenuService;
    this.iconRegistry.setDefaultFontSetClass('icomoon');
    this.menu.getMenu('ANALYZE').then(data => {
      this.analyze.updateMenu(data);
    });
  }

  createDashboard() {
    this.dialog.open(CreateDashboardComponent, {
      panelClass: 'full-screen-dialog'
    });
  }

  // $onInit() {
  //   const leftSideNav = this.$componentHandler.get('left-side-nav')[0];

  //   this.MenuService.getMenu('OBSERVE')
  //     .then(data => {
  //       leftSideNav.update(data, 'OBSERVE');
  //     });
  // }
};
