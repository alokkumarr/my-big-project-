import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { UIRouter } from '@uirouter/angular';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { HeaderProgressService } from '../../../../../common/services/header-progress.service';
import { SqlExecutorComponent } from '../../sql-executor/sql-executor.component';
import { WorkbenchService } from '../../../services/workbench.service';

const template = require('./datapods-card-page.component.html');
require('./datapods-card-page.component.scss');

@Component({
  selector: 'datapods-card-page',
  template,
  styles: []
})
export class DatapodsCardPageComponent implements OnInit {
  @Input()
  searchTerm: string;
  @Input()
  updater: BehaviorSubject<any>;
  private updaterSubscribtion: any;
  private dataPods: Array<any> = [];

  constructor(
    private router: UIRouter,
    public dialog: MatDialog,
    private headerProgress: HeaderProgressService,
    private workbench: WorkbenchService
  ) {}

  ngOnInit() {
    this.headerProgress.show();
    this.updaterSubscribtion = this.updater.subscribe(data => {
      this.onUpdate(data);
    });
  }

  ngOnDestroy() {
    this.updaterSubscribtion.unsubscribe();
  }

  onUpdate(data) {
    this.dataPods = data;
    setTimeout(() => {
      this.headerProgress.hide();
    }, 1000);
  }

  viewDetails(metadata) {
    // this.workbench.navigateToDetails(metadata);
  }
}
