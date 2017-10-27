import { Component, Inject, ViewChild } from '@angular/core';
import { MdDialogRef, MD_DIALOG_DATA, MdDialog } from '@angular/material';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { GridsterConfig, GridsterItem, GridsterComponent } from 'angular-gridster2';

import * as forEach from 'lodash/forEach';

import { AnalysisChoiceComponent } from '../analysis-choice/analysis-choice.component';

const template = require('./create-dashboard.component.html');
require('./create-dashboard.component.scss');

@Component({
  selector: 'create-dashboard',
  template
})
export class CreateDashboardComponent {
  @ViewChild('gridster') gridster: GridsterComponent;

  public options: GridsterConfig;
  public dashboard: Array<GridsterItem>;
  public chartUpdater = new BehaviorSubject({});

  constructor(public dialogRef: MdDialogRef<CreateDashboardComponent>,
    public dialog: MdDialog,
    @Inject(MD_DIALOG_DATA) public layout: any) {
  }

  itemChange(item, itemComponent) {
    setTimeout(() => {
      if (this.gridster.columns !== this.columns) {
        this.refreshAllTiles();
      } else {
      item.updater.next([]);
      }
      this.columns = this.gridster.columns;
    }, 500)
  }

  refreshAllTiles() {
    forEach(this.dashboard, i => i.updater.next([]))
  }

  ngOnInit() {
    this.options = {
      gridType: 'scrollVertical',
      minCols: 2,
      maxCols: 100,
      minRows: 2,
      maxRows: 100,
      itemChangeCallback: this.itemChange.bind(this),
      draggable: {
        enabled: true
      },
      resizable: {
        enabled: true
      }
    };

    this.dashboard = [];
  }

  removeTile(item: GridsterItem) {
    this.dashboard.splice(this.dashboard.indexOf(item), 1);
  }

  exitCreator(data) {
    this.dialogRef.close(data);
  }

  chooseAnalysis() {
    const dialogRef = this.dialog.open(AnalysisChoiceComponent);

    dialogRef.afterClosed().subscribe(analysis => {
      if (!analysis) {
        return;
      }

      const item = { cols: 1, rows: 1, analysis, updater: new BehaviorSubject({}) };
      this.dashboard.push(item);
    });
  }
}
