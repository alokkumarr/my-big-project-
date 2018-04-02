
import { Component, OnInit, Input } from '@angular/core';
import { UIRouter } from '@uirouter/angular';

import { SqlExecutorComponent } from '../sql-executor/sql-executor.component';
import { WorkbenchService } from '../../services/workbench.service';

const template = require('./dataset-actions.component.html');
require('./dataset-actions.component.scss');
@Component({
  selector: 'dataset-actions',
  template,
  styles: []
})

export class DatasetActionsComponent implements OnInit {
  @Input() dsMetadata: any;

  constructor(
    private router: UIRouter,
    private workBench: WorkbenchService
  ) { }

  ngOnInit() { }

  openSQLEditor(): void {
    this.workBench.setDataToLS('dsMetadata', this.dsMetadata);
    this.router.stateService.go('workbench.sql');
  }
}