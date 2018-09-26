import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material';

import { LocalSearchService } from '../../../../common/services/local-search.service';
import { WorkbenchService } from '../../services/workbench.service';
import { ToastService } from '../../../../common/services/toastMessage.service';

const template = require('./datasource-page.component.html');
require('./datasource-page.component.scss');

@Component({
  selector: 'datasource-page',
  template,
  styles: []
})
export class DatasourceComponent implements OnInit, OnDestroy {
  constructor(
    public dialog: MatDialog,
    private LocalSearch: LocalSearchService,
    private workBench: WorkbenchService,
    private _toastMessage: ToastService
  ) {}

  ngOnInit() {}

  ngOnDestroy() {}
}
