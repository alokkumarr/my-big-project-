
import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { WorkbenchService } from '../../../services/workbench.service';

const template = require('./datasets-card-page.component.html');
const style = require('./datasets-card-page.component.scss');

@Component({
  selector: 'datasets-card-page',
  template,
  styles: [
    `:host {
      width: 100%;
      height: 100%;
      max-height: 100%;
    }`,
    style
  ]
})

export class DatasetsCardPageComponent implements OnInit {
  @Input() searchTerm: string;
  @Input() updater: BehaviorSubject<any>;
  private updaterSubscribtion: any;
  private dataSets: Array<any> = [];

  constructor(
    public dialog: MatDialog,
    private workbench: WorkbenchService
  ) {  }

  ngOnInit() {
    this.updaterSubscribtion = this.updater.subscribe(data => {
      this.onUpdate(data);
    });
  }

  ngOnDestroy() {
    this.updaterSubscribtion.unsubscribe();
  }

  onUpdate(data) {
    this.dataSets = data;
  }

  viewDetails(metadata) {
    this.workbench.navigateToDetails(metadata);
  }
}
