
import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { HeaderProgressService } from '../../../../../common/services/header-progress.service';
import { SqlExecutorComponent } from '../../sql-executor/sql-executor.component';


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
    public dialog: MatDialog,
    private headerProgress: HeaderProgressService
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
    }, 1000);
  }

  openSQLEditor(): void {
    this.dialog.open(SqlExecutorComponent, {
      panelClass: 'full-screen-dialog',
      autoFocus: false,
      data: {
        id: ''
      }
    });
  }
}
