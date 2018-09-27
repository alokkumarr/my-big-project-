import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { WorkbenchService } from '../../../services/workbench.service';

const template = require('./datapods-card-page.component.html');
const style = require('./datapods-card-page.component.scss');

@Component({
  selector: 'datapods-card-page',
  template,
  styles: [style]
})
export class DatapodsCardPageComponent implements OnInit {
  @Input()
  searchTerm: string;
  @Input()
  updater: BehaviorSubject<any>;
  private updaterSubscribtion: any;
  private dataPods: Array<any> = [];

  constructor(
    public dialog: MatDialog,
    private workbench: WorkbenchService
  ) {}

  ngOnInit() {
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
    }, 1000);
  }

  viewDetails(metadata) {
    // this.workbench.navigateToDetails(metadata);
  }
}
