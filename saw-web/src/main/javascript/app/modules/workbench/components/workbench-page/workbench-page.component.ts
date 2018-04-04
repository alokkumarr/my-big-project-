
import { Component, Inject, OnInit } from '@angular/core';
import { UIRouter } from '@uirouter/angular';

import { MenuService } from '../../../../common/services/menu.service';
import { HeaderProgressService } from '../../../../common/services/header-progress.service';

const template = require('./workbench-page.component.html');
require('./workbench-page.component.scss');

@Component({
  selector: 'workbench-page',
  styles: [],
  template: template
})
export class WorkbenchPageComponent {
  constructor(
    private menu: MenuService,
    private headerProgress: HeaderProgressService,
    private router: UIRouter,
    @Inject('$componentHandler') private $componentHandler
  ) {  }


  ngOnInit() {
    this.headerProgress.show();
    this.router.stateService.go('workbench.datasets');
    this.headerProgress.hide();
  }
};
