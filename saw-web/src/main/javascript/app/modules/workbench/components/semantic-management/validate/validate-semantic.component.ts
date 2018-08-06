import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { UIRouter } from '@uirouter/angular';
import { DxDataGridComponent } from 'devextreme-angular';

import { HeaderProgressService } from '../../../../../common/services/header-progress.service';
import { WorkbenchService } from '../../../services/workbench.service';

import * as filter from 'lodash/filter';
import * as cloneDeep from 'lodash/cloneDeep';

const template = require('./validate-semantic.component.html');
require('./validate-semantic.component.scss');

@Component({
  selector: 'validate-semantic',
  styles: [],
  template: template
})
export class ValidateSemanticComponent implements OnDestroy {
  private selectedDS: any;

  constructor(
    private router: UIRouter,
    private workBench: WorkbenchService,
    private headerProgress: HeaderProgressService
  ) {
    this.selectedDS = this.workBench.getDataFromLS('selectedDS');
  }

  @ViewChild('dsGrid') dataGrid: DxDataGridComponent;

  ngOnInit() {}

  ngOnDestroy() {
    this.workBench.removeDataFromLS('selectedDS');
  }

  showDSList() {
    this.router.stateService.go('workbench.createSemantic');
  }
}
