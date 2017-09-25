declare function require(string): string;

import { MdDialog, MdIconRegistry } from '@angular/material';
import { NewDashboardComponent } from '../new-dashboard/new-dashboard.component';

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
  constructor(public dialog: MdDialog, private iconRegistry: MdIconRegistry) {
    // this.$componentHandler = $componentHandler;
    // this.MenuService = MenuService;
    this.iconRegistry.setDefaultFontSetClass('icomoon');

  }

  openDialog() {
    this.dialog.open(NewDashboardComponent);
  }

  // $onInit() {
  //   const leftSideNav = this.$componentHandler.get('left-side-nav')[0];

  //   this.MenuService.getMenu('OBSERVE')
  //     .then(data => {
  //       leftSideNav.update(data, 'OBSERVE');
  //     });
  // }
};
