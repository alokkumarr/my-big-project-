import { Component, Inject, ViewChild } from '@angular/core';
import { MdDialogRef, MD_DIALOG_DATA, MdDialog } from '@angular/material';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { GridsterConfig, GridsterItem, GridsterComponent } from 'angular-gridster2';

import * as forEach from 'lodash/forEach';

import { AnalysisChoiceComponent } from '../analysis-choice/analysis-choice.component';

const template = require('./create-dashboard.component.html');
require('./create-dashboard.component.scss');

const MARGIN_BETWEEN_TILES = 10;

@Component({
  selector: 'create-dashboard',
  template
})
export class CreateDashboardComponent {
  @ViewChild('gridster') gridster: GridsterComponent;

  public columns = 4;
  public options: GridsterConfig;
  public dashboard: Array<GridsterItem>;
  public chartUpdater = new BehaviorSubject({});

  constructor(public dialogRef: MdDialogRef<CreateDashboardComponent>,
    public dialog: MdDialog,
    @Inject(MD_DIALOG_DATA) public layout: any) {
  }

  itemChange(item, itemComponent) {
    window['mygridster'] = this.gridster;
    setTimeout(() => {
      if (this.gridster.columns !== this.columns) {
        this.refreshAllTiles();
      } else {
        this.refreshTile(item);
      }
      this.columns = this.gridster.columns;
    }, 500)
  }

  getDimensions(item) {
    return {
      width: this.gridster.curColWidth * item.cols - MARGIN_BETWEEN_TILES,
      height: this.gridster.curRowHeight * item.rows - MARGIN_BETWEEN_TILES
    };
  }

  refreshTile(item) {
    const dimensions = this.getDimensions(item);
    item.updater.next([
      {path: 'chart.height', data: dimensions.height},
      {path: 'chart.width', data: dimensions.width}
    ])
  }

  refreshAllTiles() {
    forEach(this.dashboard, this.refreshTile.bind(this));
  }

  ngOnInit() {
    this.options = {
      gridType: 'scrollVertical',
      minCols: this.columns,
      maxCols: 100,
      margin: MARGIN_BETWEEN_TILES,
      minRows: 4,
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
