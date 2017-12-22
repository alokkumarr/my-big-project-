import { Component, Input, ViewChild, OnInit, OnChanges } from '@angular/core';
import { GridsterConfig, GridsterItem, GridsterComponent } from 'angular-gridster2';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import * as get from 'lodash/get';
import * as forEach from 'lodash/forEach';

import { Dashboard } from '../../models/dashboard.interface';
import { AnalyzeService } from '../../../analyze/services/analyze.service';

const template = require('./dashboard-grid.component.html');
require('./dashboard-grid.component.scss');

const MARGIN_BETWEEN_TILES = 10;

export const DASHBOARD_MODES = {
  EDIT: 'edit',
  VIEW: 'view'
};

@Component({
  selector: 'dashboard-grid',
  template
})
export class DashboardGridComponent implements OnInit, OnChanges {
  @ViewChild('gridster') gridster: GridsterComponent;

  @Input() model: Dashboard;
  @Input() mode: string;

  public fillState = 'empty';
  public columns = 4;
  public options: GridsterConfig;
  public dashboard: Array<GridsterItem>;

  constructor(private analyze: AnalyzeService) {}

  isViewMode() {
    return this.mode === DASHBOARD_MODES.VIEW;
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

  ngOnInit() {
    this.options = {
      gridType: 'scrollVertical',
      minCols: this.columns,
      maxCols: 100,
      margin: MARGIN_BETWEEN_TILES,
      minRows: 4,
      maxRows: 100,
      draggable: {
        enabled: !this.isViewMode()
      },
      resizable: {
        enabled: !this.isViewMode()
      }
    };

    this.dashboard = [];
  }

  ngOnChanges() {
    if (!this.model) {
      return;
    }

    this.dashboard = [];
    forEach(get(this.model, 'tiles', []), tile => {
      this.analyze.readAnalysis(tile.id).then(data => {
        tile.analysis = data;
        tile.updater = new BehaviorSubject({});
        this.dashboard.push(tile);
        this.refreshTile(tile);
      });
    });
  }
}
