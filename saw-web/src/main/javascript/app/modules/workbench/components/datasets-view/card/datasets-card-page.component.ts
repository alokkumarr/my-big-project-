
import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { UIRouter } from '@uirouter/angular';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { HeaderProgressService } from '../../../../../common/services/header-progress.service';
import { SqlExecutorComponent } from '../../sql-executor/sql-executor.component';
import { WorkbenchService } from '../../../services/workbench.service';

const template = require('./datasets-card-page.component.html');
require('./datasets-card-page.component.scss');

@Component({
  selector: 'datasets-card-page',
  template,
  styles: []
})

export class DatasetsCardPageComponent implements OnInit {
  @Input() searchTerm: string;
  @Input() updater: BehaviorSubject<any>;
  private updaterSubscribtion: any;
  private dataSets: Array<any> = [];

  constructor(
    private router: UIRouter,
    public dialog: MatDialog,
    private headerProgress: HeaderProgressService,
    private workbench: WorkbenchService
  ) {  }

  ngOnInit() {
    this.headerProgress.show();
    this.updaterSubscribtion = this.updater.subscribe(data => {
      this.onUpdate(data)
    });
  }

  ngOnDestroy() {
    this.updaterSubscribtion.unsubscribe();
  }

  onUpdate(data) {
    this.dataSets = data;
    if (this.dataSets.length > 0) {

    }
    setTimeout(() => {
      this.headerProgress.hide();
    }, 3000);
  }

  viewDetails(metadata) {
    this.workbench.navigateToDetails(metadata);
  }
}
