
import { Component } from '@angular/core';
import { UIRouter } from '@uirouter/angular';

import { HeaderProgressService } from '../../../../common/services/header-progress.service';
import { MenuService } from '../../../../common/services/menu.service';

const template = require('./workbench-page.component.html');
require('./workbench-page.component.scss');

@Component({
  selector: 'workbench-page',
  styles: [],
  template: template
})
export class WorkbenchPageComponent {
  constructor(
    private headerProgress: HeaderProgressService,
    private router: UIRouter,
    private menu: MenuService
  ) { }


  ngOnInit() {
    this.headerProgress.show();
    this.menu.getMenu('WORKBENCH')
      .then(data => {
        this.menu.updateMenu({}, 'WORKBENCH');
      });
    /**
    * Temporary fix for routing of SQL component until the issue of editor initialization is figured out.
    * So on refresh re-routin to listing page 
    */
    if (this.router.stateService.current.name === 'workbench' || this.router.stateService.current.name === 'workbench.sql') {
      this.router.stateService.go('workbench.datasets');
    }
    this.headerProgress.hide();
  }
};
