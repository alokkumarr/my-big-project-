declare function require(string): string;

import { Inject, OnInit } from '@angular/core';
import { MatIconRegistry } from '@angular/material';
import { UIRouter } from '@uirouter/angular';

import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import * as map from 'lodash/map';

import { ObserveService } from '../../services/observe.service';
import { MenuService } from '../../../../common/services/menu.service';
import { HeaderProgressService } from '../../../../common/services/header-progress.service';
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

  constructor(
    private iconRegistry: MatIconRegistry,
    private analyze: AnalyzeService,
    private menu: MenuService,
    private observe: ObserveService,
    private headerProgress: HeaderProgressService,
    private router: UIRouter,
    @Inject('$componentHandler') private $componentHandler
  ) {
    // this.iconRegistry.setDefaultFontSetClass('icomoon');
  }


  ngOnInit() {
    this.headerProgress.show();

    /* Needed to get the analyze service working correctly */
    this.menu.getMenu('ANALYZE')
      .then(data => {
        this.analyze.updateMenu(data);
      });

    this.observe.reloadMenu().subscribe((menu) => {
      this.headerProgress.hide();
      this.observe.updateSidebar(menu);
      this.observe.redirectToFirstDash(menu);
    }, () => {
      this.headerProgress.hide();
    })
  }
};
