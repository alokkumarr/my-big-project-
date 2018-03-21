declare function require(string): string;

import { Component } from '@angular/core';
import { UIRouter } from '@uirouter/angular';

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
    private headerProgress: HeaderProgressService,
    private router: UIRouter
  ) {  }


  ngOnInit() {
    this.headerProgress.show();
    this.router.stateService.go('workbench.datasets');
    this.headerProgress.hide();
  }
};
