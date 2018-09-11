declare function require(string): string;

import { ObserveService } from '../../services/observe.service';
import { MenuService } from '../../../../common/services/menu.service';
import { HeaderProgressService } from '../../../../common/services/header-progress.service';
import { AnalyzeService } from '../../../analyze/services/analyze.service';

const template = require('./observe-page.component.html');
require('./observe-page.component.scss');

import { Component } from '@angular/core';

@Component({
  selector: 'observe-page',
  styles: [],
  template: template
})
export class ObservePageComponent {
  constructor(
    private analyze: AnalyzeService,
    private menu: MenuService,
    private observe: ObserveService,
    private headerProgress: HeaderProgressService
  ) {}


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
