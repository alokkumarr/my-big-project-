
import { Component, OnInit } from '@angular/core';
import { UIRouter } from '@uirouter/angular';

import { HeaderProgressService } from '../../../../../common/services/header-progress.service';
import { WorkbenchService } from '../../../services/workbench.service';

const template = require('./create-semantic.component.html');
require('./create-semantic.component.scss');

@Component({
  selector: 'create-semantic',
  styles: [],
  template: template
})
export class CreateSemanticComponent {
  private availableDS: any;

  constructor(
    private router: UIRouter,
    private workBench: WorkbenchService,
    private headerProgress: HeaderProgressService
  ) { }


  ngOnInit() {
    this.headerProgress.show();
    this.workBench.getDatasets().subscribe((data: any[]) => {
      this.availableDS = data;
      this.headerProgress.hide();
    });
  }

  backToDS() {
    this.router.stateService.go('workbench.datasets');
  }
};
