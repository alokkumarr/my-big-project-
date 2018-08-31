import { Component, Input, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { HeaderProgressService } from '../../../../../common/services/header-progress.service';

const template = require('./datapods-grid-page.component.html');
require('./datapods-grid-page.component.scss');

@Component({
  selector: 'datapods-grid-page',
  template,
  styles: []
})
export class DatapodsGridPageComponent implements OnInit {
  @Input()
  searchTerm: string;
  @Input()
  updater: BehaviorSubject<any>;
  private gridData: Array<any>;
  private updaterSubscribtion: any;

  constructor(private headerProgress: HeaderProgressService) {}

  ngOnInit() {
    this.updaterSubscribtion = this.updater.subscribe(data => {
      this.onUpdate(data);
    });
  }

  ngOnDestroy() {
    this.updaterSubscribtion.unsubscribe();
  }

  onUpdate(data) {
    if (data.length != 0) {
      setTimeout(() => {
        this.reloadDataGrid(data);
      });
    }
  }

  reloadDataGrid(data) {
    this.gridData = data;
    setTimeout(() => {
      this.headerProgress.hide();
    }, 1000);
  }

  viewDetails(metadata) {
    // this.workbench.navigateToDetails(metadata);
  }
}
