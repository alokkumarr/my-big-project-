import {
  Component,
  Input,
  Output,
  ViewChild,
  ViewChildren,
  QueryList,
  EventEmitter,
  AfterViewInit,
  OnInit,
  OnChanges,
  OnDestroy
} from '@angular/core';
import {
  GridsterConfig,
  GridsterItem,
  GridsterComponent
} from 'angular-gridster2';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subscription } from 'rxjs/Subscription';

import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as unionWith from 'lodash/unionWith';
import * as flatMap from 'lodash/flatMap';
import * as values from 'lodash/values';
import * as forEach from 'lodash/forEach';

import { ObserveChartComponent } from '../observe-chart/observe-chart.component';
import { Dashboard } from '../../models/dashboard.interface';
import { GlobalFilterService } from '../../services/global-filter.service';
import { DashboardService } from '../../services/dashboard.service';
import { SideNavService } from '../../../../common/services/sidenav.service';
import { AnalyzeService } from '../../../analyze/services/analyze.service';

const style = require('./dashboard-grid.component.scss');

const MARGIN_BETWEEN_TILES = 10;

export const DASHBOARD_MODES = {
  EDIT: 'edit',
  VIEW: 'view',
  CREATE: 'create'
};

@Component({
  selector: 'dashboard-grid',
  templateUrl: './dashboard-grid.component.html',
  styles: [
    `:host {
      display: block;
      height: 100%;
      width: 100%;
    }`,
    style
  ]
})
export class DashboardGridComponent
  implements OnInit, OnChanges, AfterViewInit, OnDestroy {
  @ViewChild('gridster') gridster: GridsterComponent;
  @ViewChildren(ObserveChartComponent) charts: QueryList<ObserveChartComponent>;

  @Input() model: Dashboard;
  @Input() requester: BehaviorSubject<any>;
  @Input() mode: string;

  @Output() getDashboard = new EventEmitter();

  public fillState = 'empty';
  public enableChartDownload: boolean;
  public columns = 16;
  public options: GridsterConfig;
  public dashboard: Array<GridsterItem> = [];
  public listeners: Array<Subscription> = [];
  public initialised = false;

  constructor(
    public analyze: AnalyzeService,
    public filters: GlobalFilterService,
    public dashboardService: DashboardService,
    public sidenav: SideNavService
  ) {}

  ngOnInit() {
    this.subscribeToRequester();

    this.columns = this.getMinColumns();
    this.options = {
      disableWindowResize: true,
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

    this.enableChartDownload = this.isViewMode();
    this.filters.initialise();
  }

  ngAfterViewInit() {
    setTimeout(_ => this.initialiseDashboard(), 100);
  }

  ngOnChanges() {
    this.initialiseDashboard();
  }

  ngOnDestroy() {
    this.listeners.forEach(l => l.unsubscribe());
  }

  getMinColumns() {
    if (this.mode === DASHBOARD_MODES.CREATE) { return 64; }

    const savedMinCols = get(this.model, 'options.0.minCols');
    return savedMinCols ? savedMinCols : 4;
  }

  onGridInit() {
    if (this.mode === DASHBOARD_MODES.VIEW) {
      const sidenavEventSubscription = this.sidenav.sidenavEvent.subscribe(
        val => {
          setTimeout(_ => {
            this.gridster.resize();
          });
          setTimeout(_ => {
            this.refreshAllTiles();
          }, 500);
        }
      );
      this.listeners.push(sidenavEventSubscription);
    }

    const globalFiltersSubscription = this.filters.onApplyFilter.subscribe(
      data => {
        this.onApplyGlobalFilters(data);
      }
    );
    this.listeners.push(globalFiltersSubscription);
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
      this.getDashboard.emit({
        changed: true,
        dashboard: this.prepareDashboard()
      });
    }, 500);
  }

  removeTile(item: GridsterItem) {
    this.dashboard.splice(this.dashboard.indexOf(item), 1);
    this.getDashboard.emit({
      changed: true,
      dashboard: this.prepareDashboard()
    });
    if (item.analysis) {
      this.onAnalysisRemove();
    }
  }

  editTile(item: GridsterItem) {
    if (!item.kpi && !item.bullet) { return; }

    this.dashboardService.onEditItem.next(item);
  }

  getDimensions(item) {
    return {
      width: this.gridster.curColWidth * item.cols - MARGIN_BETWEEN_TILES,
      height: this.gridster.curRowHeight * item.rows - MARGIN_BETWEEN_TILES
    };
  }

  refreshTile(item) {
    const dimensions = this.getDimensions(item);
    if (item.kpi) {
      item.dimensions = dimensions;
      return;
    }
    item.updater.next([
      { path: 'chart.height', data: dimensions.height },
      { path: 'chart.width', data: dimensions.width }
    ]);
  }

  refreshAllTiles() {
    forEach(this.dashboard, this.refreshTile.bind(this));
  }

  addGlobalFilters(analysis) {
    const columns = flatMap(analysis.artifacts, table => table.columns);

    const filters = get(analysis, 'sqlBuilder.filters', []);

    this.filters.addFilter(
      filter(
        map(filters, flt => {
          /* Find if a global filter already exists in dashboard with same attributes */
          const existingFilter = find(
            get(this.model, 'filters') || [],
            dashFilt =>
              dashFilt.semanticId === analysis.semanticId &&
              dashFilt.tableName === flt.tableName &&
              dashFilt.columnName === flt.columnName &&
              flt.isGlobalFilter
          );

          if (existingFilter) {
            existingFilter.isGlobalFilter = true;
          }

          return {
            ...(existingFilter || flt),
            ...{
              semanticId: analysis.semanticId,
              metricName: analysis.metricName,
              esRepository: analysis.esRepository,
              displayName: this.filters.getDisplayNameFor(
                columns,
                flt.columnName,
                flt.tableName
              )
            }
          };
        }),
        f => f.isGlobalFilter
      )
    );
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
  onApplyGlobalFilters(filterGroup = {}) {
    this.dashboard.forEach((tile, id) => {
      // Only applies to analysis type tiles
      if (this.tileType(tile) !== 'analysis') { return; }

      const gFilters = filterGroup[tile.analysis.semanticId] || [];

      const filters = unionWith(
        // Global filters are being ignored by backend. Set that property
        // false to make them execute properly.
        map(gFilters, f => {
          if (f.model) {
            f.isGlobalFilter = false;
          }
          return f;
        }),

        tile.origAnalysis.sqlBuilder.filters,
        (gFilt, filt) =>
          gFilt.tableName === filt.tableName &&
          gFilt.columnName === filt.columnName
      );

      const sqlBuilder = { ...tile.origAnalysis.sqlBuilder, ...{ filters } };
      tile.analysis = {
        ...tile.origAnalysis,
        ...{ sqlBuilder },
        _executeTile: true
      };

      this.dashboard.splice(id, 1, { ...tile });
    });
  }

  refreshKPIs() {
    this.filters.onApplyKPIFilter.next(
      this.filters.onApplyKPIFilter.getValue() || { preset: '' }
    );
  }

  initialiseDashboard() {
    if (!this.model || this.initialised) {
      return;
    }

    let length = get(this.model, 'tiles', []).length;

    const tileLoaded = () => {
      if (--length > 0) {
        return;
      }

      /* All tiles have been initialised with data. Apply global filters
       * if necessary */
      setTimeout(() => {
        this.onApplyGlobalFilters(this.filters.globalFilters);
      }, 500);
    };

    forEach(get(this.model, 'tiles', []), tile => {
      if (tile.bullet) {
        tile.updater = new BehaviorSubject({});
      }
      if (tile.kpi || tile.bullet) {
        this.dashboard.push(tile);
        tileLoaded();
        this.getDashboard.emit({ changed: true, dashboard: this.model });
        setTimeout(() => {
          this.refreshTile(tile);
        }, 100);
        return;
      }

      this.analyze.readAnalysis(tile.id).then(data => {
        tile.analysis = data;
        this.addAnalysisTile(tile);
        tileLoaded();
        this.getDashboard.emit({ changed: true, dashboard: this.model });
        this.refreshTile(tile);
      });
    });

    this.initialised = true;
  }

  addAnalysisTile(tile, executeTile = false) {
    if (!tile.analysis) {
      return;
    }

    tile.analysis = { ...tile.analysis, _executeTile: executeTile };
    tile.origAnalysis = tile.analysis;
    this.addGlobalFilters(tile.analysis);
    tile.updater = tile.updater || new BehaviorSubject({});
    this.dashboard.push(tile);
  }

  refreshDashboard() {
    this.onApplyGlobalFilters(this.filters.globalFilters);
    this.refreshKPIs();
  }

  onAnalysisRemove() {
    const analysisTiles = filter(this.dashboard, tile => tile.analysis);
    const globalFilters = filter(
      flatMap(analysisTiles, tile =>
        map(
          get(tile.origAnalysis || tile.analysis, 'sqlBuilder.filters') || [],
          f => {
            f.semanticId = tile.analysis.semanticId;
            return f;
          }
        )
      ),
      f => f.isGlobalFilter
    );

    this.filters.removeInvalidFilters(globalFilters);
  }

  /* Enables 2 way communication. The parent can request dashboard or send updates
     with this */
  subscribeToRequester() {
    if (this.requester) {
      const requesterSubscription = this.requester.subscribe(
        (req: any = {}) => {
          /* prettier-ignore */
          switch (req.action) {
          case 'add':
            if (req.data && req.data.analysis) {
              this.addAnalysisTile(req.data, true);
            } else {
              this.dashboard.push(req.data);
            }
            this.getDashboard.emit({
              changed: true,
              dashboard: this.prepareDashboard()
            });
            break;

          case 'remove':
            const tiles = filter(
              this.dashboard,
              tile => get(tile, 'analysis.id') === get(req, 'data.id')
            );
            forEach(tiles, this.removeTile.bind(this));
            break;

          case 'get':
            this.getDashboard.emit({
              save: true,
              dashboard: this.prepareDashboard()
            });
            break;

          case 'refresh':
            this.refreshDashboard();
            break;

          default:
            this.getDashboard.emit({ dashboard: this.prepareDashboard() });
          }
        }
      );
      this.listeners.push(requesterSubscription);
    }
  }

  tileType(tile) {
    if (tile.analysis) {
      return 'analysis';
    } else if (tile.kpi) {
      return 'kpi';
    } else if (tile.bullet) {
      return 'bullet';
    }

    return 'custom';
  }

  prepareDashboard(): Dashboard {
    this.model = this.model;
    return {
      entityId: get(this.model, 'entityId', ''),
      categoryId: get(this.model, 'categoryId', ''),
      autoRefreshEnabled: get(this.model, 'autoRefreshEnabled', false),
      refreshIntervalSeconds: get(this.model, 'refreshIntervalSeconds'),
      name: get(this.model, 'name', ''),
      description: get(this.model, 'description', ''),
      createdBy: get(this.model, 'createdBy', ''),
      createdAt: get(this.model, 'createdAt', ''),
      updatedBy: get(this.model, 'updatedBy', ''),
      updatedAt: get(this.model, 'updatedAt', ''),
      tiles: map(this.dashboard, tile => ({
        type: this.tileType(tile),
        id: get(tile, 'analysis.id', ''),
        x: tile.x,
        y: tile.y,
        cols: tile.cols,
        rows: tile.rows,
        kpi: tile.kpi,
        bullet: tile.bullet
      })),
      filters: flatMap(values(this.filters.globalFilters)),
      options: [
        {
          minCols:
            get(this.model, 'options.0.minCols') || get(this.options, 'minCols')
        }
      ]
    };
  }
}
