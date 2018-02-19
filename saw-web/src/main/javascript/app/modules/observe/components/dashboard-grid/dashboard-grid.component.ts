declare const require: any;

import {
  Component,
  Input,
  Output,
  ViewChild,
  EventEmitter,
  AfterViewInit,
  OnInit,
  OnChanges,
  OnDestroy
} from '@angular/core';
import { GridsterConfig, GridsterItem, GridsterComponent } from 'angular-gridster2';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subscription } from 'rxjs/Subscription';

import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as filter from 'lodash/filter';
import * as unionWith from 'lodash/unionWith';
import * as forEach from 'lodash/forEach';

import { Dashboard } from '../../models/dashboard.interface';
import { GlobalFilterService } from '../../services/global-filter.service';
import { SideNavService } from '../../../../common/services/sidenav.service';
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
export class DashboardGridComponent implements OnInit, OnChanges, AfterViewInit, OnDestroy {
  @ViewChild('gridster') gridster: GridsterComponent;

  @Input() model: Dashboard;
  @Input() requester: BehaviorSubject<any>;
  @Input() mode: string;

  @Output() getDashboard = new EventEmitter();

  public fillState = 'empty';
  public columns = 4;
  public options: GridsterConfig;
  public dashboard: Array<GridsterItem> = [];
  private sidenavEventSubscription: Subscription;
  private globalFiltersSubscription: Subscription;
  private requesterSubscription: Subscription;
  public initialised = false;

  constructor(
    private analyze: AnalyzeService,
    private filters: GlobalFilterService,
    private sidenav: SideNavService) { }

  ngOnInit() {
    this.subscribeToRequester();

    this.options = {
      gridType: 'scrollVertical',
      minCols: this.columns,
      maxCols: 100,
      margin: MARGIN_BETWEEN_TILES,
      minRows: 4,
      maxRows: 100,
      initCallback: this.onGridInit.bind(this),
      itemChangeCallback: this.itemChange.bind(this),
      draggable: {
        enabled: !this.isViewMode()
      },
      resizable: {
        enabled: !this.isViewMode()
      }
    };
  }

  ngAfterViewInit() {
    setTimeout(_ => this.initialiseDashboard(), 100);
  }

  ngOnChanges() {
    this.initialiseDashboard();
  }

  ngOnDestroy() {
    this.requesterSubscription && this.requesterSubscription.unsubscribe();
    this.sidenavEventSubscription && this.sidenavEventSubscription.unsubscribe();
    this.globalFiltersSubscription && this.globalFiltersSubscription.unsubscribe();
  }

  onGridInit() {
    if (this.mode === DASHBOARD_MODES.VIEW) {
      this.sidenavEventSubscription = this.sidenav.sidenavEvent.subscribe(val => {
        setTimeout(_ => {
          this.gridster.resize();
        });
        setTimeout(_ => {
          this.refreshAllTiles();
        }, 500);
      });

      this.globalFiltersSubscription = this.filters.onApplyFilter.subscribe(data => {
        this.onApplyGlobalFilters(data);
      });
    }
  }

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

  addGlobalFilters(analysis) {
    if(this.mode === DASHBOARD_MODES.VIEW) {
      const filters = get(analysis, 'sqlBuilder.filters', []);

      this.filters.addFilter(filter(
        map(filters, flt => ({
          ...flt,
          ...{
            semanticId: analysis.semanticId,
            esRepository: analysis.esRepository
          }
        })),
        f => f.isGlobalFilter
      ));
    }
  }

  /**
   * Merges the globalfilters with existing analysis filters.
   * Creates a new analysis object and keeps the origAnalysis intact.
   * This is required so that we can apply original filters on
   * removing global filters.
   *
   * @param {any} filterGroup Filters grouped by semantic id
   * @memberof DashboardGridComponent
   */
  onApplyGlobalFilters(filterGroup) {
    this.dashboard.forEach((tile, id) => {
      const gFilters = filterGroup[tile.analysis.semanticId] || [];

      const filters = unionWith(
        gFilters,
        tile.origAnalysis.sqlBuilder.filters,
        (gFilt, filt) => (
          gFilt.tableName === filt.tableName &&
          gFilt.columnName === filt.columnName
        )
      );

      const sqlBuilder = {...tile.origAnalysis.sqlBuilder, ...{filters}};
      tile.analysis = {...tile.origAnalysis, ...{sqlBuilder}};

      this.dashboard.splice(id, 1, {...tile});
    });
  }

  initialiseDashboard() {
    if (!this.model || this.initialised) {
      return;
    }

    forEach(get(this.model, 'tiles', []), tile => {
      this.analyze.readAnalysis(tile.id).then(data => {
        tile.analysis = data;
        tile.origAnalysis = data;
        this.addGlobalFilters(data);
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
      this.requesterSubscription = this.requester.subscribe((req: any = {}) => {
        switch(req.action) {
        case 'add':
          this.dashboard.push(req.data);
          this.getDashboard.emit({changed: true, dashboard: this.prepareDashboard()});
          break;
        case 'get':
          this.getDashboard.emit({save: true, dashboard: this.prepareDashboard()});
          break;
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
}
