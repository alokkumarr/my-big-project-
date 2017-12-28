import { Component, Input, Output, ViewChild, OnInit, OnChanges, AfterViewChecked, EventEmitter } from '@angular/core';
import { GridsterConfig, GridsterItem, GridsterComponent } from 'angular-gridster2';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as forEach from 'lodash/forEach';

import { Dashboard } from '../../models/dashboard.interface';
import { AnalyzeService } from '../../../analyze/services/analyze.service';

const template = require('./dashboard-grid.component.html');
require('./dashboard-grid.component.scss');

const MARGIN_BETWEEN_TILES = 10;

export const DASHBOARD_MODES = {
  EDIT: 'edit',
  VIEW: 'view',
  CREATE: 'create'
};

@Component({
  selector: 'dashboard-grid',
  template
})
export class DashboardGridComponent implements OnInit, OnChanges, AfterViewChecked {
  @ViewChild('gridster') gridster: GridsterComponent;

  @Input() model: Dashboard;
  @Input() requester: BehaviorSubject<any>;
  @Input() mode: string;

  @Output() getDashboard = new EventEmitter();

  public fillState = 'empty';
  public columns = 4;
  public options: GridsterConfig;
  public dashboard: Array<GridsterItem> = [];
  public initialised = false;

  constructor(private analyze: AnalyzeService) {}

  isViewMode() {
    return this.mode === DASHBOARD_MODES.VIEW;
  }

  itemChange(item, itemComponent) {
    setTimeout(() => {
      if (this.gridster.columns !== this.columns) {
        this.refreshAllTiles();
      } else {
        this.refreshTile(item);
      }
      this.columns = this.gridster.columns;
      this.getDashboard.emit({changed: true, dashboard: this.prepareDashboard()});
    }, 500)
  }

  removeTile(item: GridsterItem) {
    this.dashboard.splice(this.dashboard.indexOf(item), 1);
    this.getDashboard.emit({changed: true, dashboard: this.prepareDashboard()});
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
    window['myview'] = this;
    this.subscribeToRequester();
    this.options = {
      gridType: 'scrollVertical',
      minCols: this.columns,
      maxCols: 100,
      margin: MARGIN_BETWEEN_TILES,
      minRows: 4,
      maxRows: 100,
      itemChangeCallback: this.itemChange.bind(this),
      draggable: {
        enabled: !this.isViewMode()
      },
      resizable: {
        enabled: !this.isViewMode()
      }
    };

  }

  ngAfterViewChecked() {
    setTimeout(_ => this.initialiseDashboard());
  }

  initialiseDashboard() {
    if (!this.model || this.initialised) {
      return;
    }

    forEach(get(this.model, 'tiles', []), tile => {
      this.analyze.readAnalysis(tile.id).then(data => {
        tile.analysis = data;
        tile.updater = new BehaviorSubject({});
        this.dashboard.push(tile);
        this.getDashboard.emit({changed: true, dashboard: this.model});
        this.refreshTile(tile);
      });
    });
    this.initialised = true;
  }

  /* Enables 2 way communication. The parent can request dashboard or send updates
     with this */
  subscribeToRequester() {
    if (this.requester) {
      this.requester.subscribe((req = {}) => {
        switch(req.action) {
        case 'add':
          this.dashboard.push(req.data);
          this.getDashboard.emit({changed: true, dashboard: this.prepareDashboard()});
          break;
        case 'get':
          this.getDashboard.emit({save: true, dashboard: this.prepareDashboard()});
        default:
          this.getDashboard.emit({dashboard: this.prepareDashboard()});
        }
      });
    }
  }

  prepareDashboard(): Dashboard {
    this.model = this.model;
    return {
      entityId: get(this.model, 'entityId', ''),
      categoryId: get(this.model, 'categoryId', ''),
      name: get(this.model, 'name', ''),
      description: get(this.model, 'description', ''),
      createdBy: get(this.model, 'createdBy', ''),
      createdAt: get(this.model, 'createdAt', ''),
      updatedBy: get(this.model, 'updatedBy', ''),
      updatedAt: get(this.model, 'updatedAt', ''),
      tiles: map(this.dashboard, tile => ({
        type: 'analysis',
        id: get(tile, 'analysis.id', ''),
        x: tile.x,
        y: tile.y,
        cols: tile.cols,
        rows: tile.rows
      })),
      filters: []
    }
  }

  ngOnChanges() {
    this.initialiseDashboard();
  }
}
