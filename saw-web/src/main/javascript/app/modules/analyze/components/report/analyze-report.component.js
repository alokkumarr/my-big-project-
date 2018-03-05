import * as defaultsDeep from 'lodash/defaultsDeep';
import * as fpFilter from 'lodash/fp/filter';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpGet from 'lodash/fp/get';
import * as first from 'lodash/first';
import * as map from 'lodash/map';
import * as keys from 'lodash/keys';
import * as forEach from 'lodash/forEach';
import * as clone from 'lodash/clone';
import * as reduce from 'lodash/reduce';
import * as sortBy from 'lodash/sortBy';
import * as filter from 'lodash/filter';
import * as assign from 'lodash/assign';
import * as uniqBy from 'lodash/uniqBy';
import * as isUndefined from 'lodash/isUndefined';

import {FieldModel} from '../../../../common/components/jsPlumb/models/fieldModel';
import * as template from './analyze-report.component.html';
import style from './analyze-report.component.scss';
import AbstractDesignerComponentController from '../analyze-abstract-designer-component';
import {DEFAULT_BOOLEAN_CRITERIA} from '../../services/filter.service';
import {ENTRY_MODES, AnalyseTypes} from '../../consts';

const DEBOUNCE_INTERVAL = 500; // milliseconds

export const AnalyzeReportComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    mode: '@'
  },
  controller: class AnalyzeReportController extends AbstractDesignerComponentController {
    constructor($componentHandler, $timeout, AnalyzeService, FilterService, $injector) {
      'ngInject';
      super($injector);
      this._$componentHandler = $componentHandler;
      this._$timeout = $timeout;
      this._AnalyzeService = AnalyzeService;
      this._FilterService = FilterService;
      this._reloadTimer = null;
      this._modelLoaded = null;
      this.artifacts = null;

      this._modelPromise = new Promise(resolve => {
        this._modelLoaded = resolve;
      });

      this.DESIGNER_MODE = 'designer';
      this.QUERY_MODE = 'query';

      this.showFiltersButton = false;

      this.states = {
        sqlMode: this.DESIGNER_MODE,
        disable: {
          designer: false
        },
        detailsExpanded: false
      };

      this.gridData = [];
      this.gridDataTotalCount = 0;
      this.columns = [];

      this._unregisterCanvasHandlers = [];
    }

    $onInit() {
      if (this.mode === ENTRY_MODES.FORK) {
        this.model.id = null;
      }

      if (this.model.edit) {
        // give designer mode chance to load, then switch to query mode
        this._$timeout(() => {
          this.setSqlMode(this.QUERY_MODE);
        }, 100);
      }

      if (this.mode === ENTRY_MODES.EDIT) {
        this.artifacts = this.model.artifacts;
        this._modelLoaded(true);
      } else {
        this._AnalyzeService.createAnalysis(this.model.semanticId, this.model.type).then(analysis => {
          this.artifacts = analysis.artifacts;
          this.model = assign(this.model, {
            id: analysis.id,
            metric: analysis.metric,
            createdTimestamp: analysis.createdTimestamp,
            userId: analysis.userId,
            userFullName: analysis.userFullName,
            metricName: analysis.metricName
          });

          if (analysis.esRepository) {
            this.model.esRepository = analysis.esRepository;
          }

          if (this.mode !== ENTRY_MODES.FORK) {
            this.model = defaultsDeep(this.model, {
              artifacts: analysis.artifacts,
              groupByColumns: [],
              sqlBuilder: {
                booleanCriteria: DEFAULT_BOOLEAN_CRITERIA.value,
                filters: [],
                joins: [],
                orderByColumns: []
              }
            });
          }

          this._modelLoaded(true);
        });
      }

      this.unregister = this._$componentHandler.on('$onInstanceAdded', e => {
        if (e.key === 'ard-canvas') {
          this._modelPromise.then(() => {
            this.initCanvas(e.instance);
          });
        }
      });
    }

    $onDestroy() {
      if (this.unregister) {
        this.unregister();
      }

      if (this._reloadTimer) {
        this._$timeout.cancel(this._reloadTimer);
        this._reloadTimer = null;
      }

      /* Unregister all the canvas's eventemitter handlers */
      forEach(this._unregisterCanvasHandlers, unRegister => {
        unRegister();
      });
    }

    generateQuery() {
      if (this.model.query) {
        return;
      }
      this._AnalyzeService.generateQuery({})
        .then(result => {
          this.model.query = result.query;
        });
    }

    // END requests

    toggleDetailsPanel(forceOpen) {
      if (forceOpen === true || forceOpen === false) {
        this.states.detailsExpanded = forceOpen;
      } else {
        this.states.detailsExpanded = !this.states.detailsExpanded;
      }

      if (this.states.detailsExpanded) {
        this._$timeout(() => {
          this.reloadPreviewGrid();
        }, 500);
      }
    }

    /**
     * Returns whether query mode is supported.
     * Example: Query is not supported for elasticsearch reports.
     */
    supportsQuery() {
      return this.model.type === AnalyseTypes.Report;
    }

    initCanvas(canvas) {
      this.canvas = canvas;

      if (!this.model.artifacts) {
        this.model.artifacts = [];
      } else {
        this.fillCanvas(this.model.artifacts);
      }

      if (this.mode) {
        this.reloadPreviewGrid(true);
        this.endDraftMode();
      }

      this._unregisterCanvasHandlers = this._unregisterCanvasHandlers.concat([

        this.canvas._$eventEmitter.on('changed', () => {
          this.canvas.model.updateFields();
          this.reloadPreviewGrid(true);
        }),

        this.canvas._$eventEmitter.on('sortChanged', () => {
          this.didAnalysisChange = true;
          this.startDraftMode();
          this.onRefreshData();
        }),

        this.canvas._$eventEmitter.on('groupingChanged', groups => {
          this.addGroupColumns(groups);
          this.reloadPreviewGrid(true);
        }),

        this.canvas._$eventEmitter.on('joinChanged', () => {
          this.reloadPreviewGrid(true);
        })

      ]);
    }

    /* NOTE: This will clear existing groups from model.
       Make sure you supply the entire new groups array
       as argument. */
    addGroupColumns(groups) {
      if (!angular.isArray(groups)) {
        groups = [groups];
      }

      const model = this.canvas.model;

      model.clearGroups();

      forEach(groups, group => {
        model.addGroup({
          table: group.table.name,
          field: group.name
        });
      });
    }

    fillCanvas(data) {
      const model = this.canvas.model;
      let defaultArtifactX = 20;
      const defaultSpacing = 300; // canvas pixels

      model.clear();
      forEach(this.model.sqlBuilder.dataFields, aggregates => {
        forEach(data[0].columns, column => {
          if (aggregates.columnName === column.columnName) {
            column.aggregate = aggregates.aggregate;
          }
          column.reportType = this.model.type;
        });
      });

      /* eslint-disable camelcase */
      forEach(data, itemA => {
        forEach(itemA.columns, column => {
          column.table = itemA.artifactName;
        });

        const table = model.addTable(itemA.artifactName);

        if (!itemA.artifactPosition) {
          itemA.artifactPosition = [defaultArtifactX, 0];
          defaultArtifactX += defaultSpacing;
        } else {
          defaultArtifactX += itemA.artifactPosition[0] + defaultSpacing;
        }

        table.setMeta(itemA);
        table.setPosition(itemA.artifactPosition[0], itemA.artifactPosition[1]);

        /* Show join eligible fields on top for easy access */
        const sortedForJoin = sortBy(itemA.columns, [
          c => !c.joinEligible,
          c => c.displayName
        ]);

        forEach(sortedForJoin, itemB => {
          const field = table.addField(itemB.columnName);

          field.setMeta(itemB);
          if (itemB.aggregate) {
            field.aggregate = itemB.aggregate;
          }
          field.reportType = this.model.type;
          field.displayName = itemB.displayName;
          field.alias = itemB.aliasName;
          field.type = itemB.type;
          field.format = itemB.format;
          field.checked = itemB.checked;
          field.visibleIndex = itemB.visibleIndex;
          field.isHidden = Boolean(itemB.hide);
          field.isJoinEligible = Boolean(itemB.joinEligible);
          if (field.isJoinEligible) {
            field.addEndpoint('right');
            field.addEndpoint('left');
          }
          field.isFilterEligible = Boolean(itemB.filterEligible);
        });
      });

      forEach(this.model.sqlBuilder.joins, itemB => {
        const tableA = itemB.criteria[0].tableName;
        const tableB = itemB.criteria[1].tableName;

        if (tableA !== tableB) {
          model.addJoin(itemB.type, {
            table: tableA,
            field: itemB.criteria[0].columnName,
            side: itemB.criteria[0].side
          }, {
            table: tableB,
            field: itemB.criteria[1].columnName,
            side: itemB.criteria[1].side
          });
        }
      });

      const sorts = this.model.sqlBuilder.orderByColumns || this.model.sqlBuilder.sorts;
      forEach(sorts, itemB => {
        model.addSort({
          table: itemB.tableName,
          field: itemB.columnName,
          order: itemB.order
        });
      });

      forEach(this.model.groupByColumns, itemB => {
        model.addGroup({
          table: itemB.tableName,
          field: itemB.columnName
        });
      });

      forEach(this.model.sqlBuilder.filters, backEndFilter => {
        model.addFilter(this._FilterService.backend2FrontendFilter(this.model.artifacts)(backEndFilter));
      });

      this.filters = model.filters;
      /* eslint-enable camelcase */
    }

    /**
     * This is a hack. Adds .keyword suffix back to names of columns if they're string
     * and this is an es report.
     *
     * @param {any} name
     * @param {any} dataType
     * @returns
     */
    addKeywordTo(name, dataType) {
      /* eslint-disable angular/typecheck-string */
      if (this.model.type === 'report' || dataType !== 'string' || /\.keyword/.test(name)) {
      /* eslint:enable */
        return name;
      }

      return `${name}.keyword`;
    }

    generatePayload() {
      /* eslint-disable camelcase */
      const model = this.canvas.model;
      let columnString = '';
      const result = {
        artifacts: [],
        groupByColumns: [],
        sqlBuilder: {
          booleanCriteria: this.model.sqlBuilder.booleanCriteria,
          orderByColumns: [],
          joins: [],
          filters: []
        }
      };

      if (this.model.type === AnalyseTypes.ESReport) {
        result.sqlBuilder.sorts = [];
        result.sqlBuilder.dataFields = [];

        delete result.sqlBuilder.orderByColumns;
      }

      forEach(model.tables, table => {
        const tableArtifact = {
          artifactName: table.name,
          artifactPosition: [table.x, table.y],
          columns: [],
          data: []
        };

        result.artifacts.push(tableArtifact);

        forEach(table.fields, field => {
          const fieldArtifact = {
            columnName: this.addKeywordTo(field.meta.columnName, field.meta.type),
            displayName: field.meta.displayName,
            table: table.name,
            aliasName: field.alias,
            format: field.format,
            type: field.meta.type,
            hide: field.isHidden,
            joinEligible: field.meta.joinEligible,
            filterEligible: field.meta.filterEligible,
            checked: field.checked,
            visibleIndex: field.visibleIndex
          };

          tableArtifact.columns.push(fieldArtifact);

          if (result.sqlBuilder.dataFields && fieldArtifact.checked) {
            columnString = fieldArtifact.columnName;
            if (field.type === 'string' && !/\.keyword/.test(fieldArtifact.columnName)) {
              columnString = `${fieldArtifact.columnName}.keyword`;
            }
            if (field.aggregate) {
              result.sqlBuilder.dataFields.push({
                columnName: columnString,
                type: fieldArtifact.type,
                name: this.getColumnName(field.meta.columnName),
                aggregate: field.meta.aggregate
              });
            } else {
              result.sqlBuilder.dataFields.push({
                columnName: columnString,
                type: fieldArtifact.type
              });
            }
          }
        });

        const joins = filter(model.joins, join => {
          return join.leftSide.table.name === table.name;
        });

        forEach(joins, join => {
          const joinArtifact = {
            type: join.type,
            criteria: []
          };

          joinArtifact.criteria.push({
            tableName: join.leftSide.table.name,
            columnName: join.leftSide.field.name,
            side: join.leftSide.side
          });

          joinArtifact.criteria.push({
            tableName: join.rightSide.table.name,
            columnName: join.rightSide.field.name,
            side: join.rightSide.side
          });

          result.sqlBuilder.joins.push(joinArtifact);
        });

        const sorts = filter(model.sorts, sort => {
          return sort.field.table === table;
        });

        forEach(sorts, sort => {
          const sortArtifact = {
            tableName: tableArtifact.artifactName,
            type: sort.field.type,
            columnName: this.addKeywordTo(sort.field.name, sort.field.type),
            order: sort.order
          };

          (result.sqlBuilder.sorts || result.sqlBuilder.orderByColumns).push(sortArtifact);
        });

        const groups = filter(model.groups, group => {
          return group.table === table;
        });

        forEach(groups, group => {
          result.groupByColumns.push({
            tableName: group.table.name,
            columnName: group.field.name
          });
        });
      });
      /* eslint-enable camelcase */
      result.sqlBuilder.filters = map(this.filters, this._FilterService.frontend2BackendFilter());

      result.sqlBuilder.filters.forEach(filt => {
        filt.columnName = this.addKeywordTo(filt.columnName, filt.type);
      });

      return result;
    }

    getColumns(columnNames = []) {
      const fields = fpFlatMap(table => table.fields, this.canvas.model.tables);
      let table = null;

      const columns = reduce(fields, (col, field) => {
        table = table || field.table;
        const index = columnNames.indexOf(field.name);
        if (index >= 0) {
          col.splice(index, 1, field);
        }
        return col;
      }, columnNames);

      return uniqBy(
        map(columns, col => {
          if (angular.isString(col)) {
            const customColumn = new FieldModel(table, col);
            customColumn.checked = true;
            return customColumn;
          }
          col.checked = true;
          return col;
        }),
        column => column.name
      );
    }

    onSaveQuery(analysis) {
      this.startProgress();
      this._AnalyzeService.getDataBySettings(clone(analysis))
        .then(({analysis, data, count}) => {
          this.gridData = data;
          this.model.query = analysis.queryManual || analysis.query;
          this.gridDataTotalCount = count;
          const columnNames = keys(fpGet('[0]', data));
          this.columns = this.getColumns(columnNames);
          this.applyDataToGrid(this.columns, [], [], this.gridData);
          this.endProgress();
          this.toggleDetailsPanel(true);
        }, () => {
          this.endProgress();
        });
    }

    /**
     * Removes .keyword suffix from column names if it exists. This is required
     * because the grid data coming from backend doesn't have that suffix in
     * its datafields.
     *
     * Returns a new clone of columns array with each column cloned as well.
     *
     * @param {any} columns
     * @returns
     */
    checkColumnName(columns) {

      forEach(columns, field => {
        field.name = this.getColumnName(field.name);
        field.meta.name = this.getColumnName(field.meta.name);
        field.meta.columnName = this.getColumnName(field.meta.columnName);
      });

      return columns;
    }

    getColumnName(columnName) {
      // take out the .keyword form the columnName
      // if there is one
      if (!isUndefined(columnName)) {
        const split = columnName.split('.');
        if (split[1]) {
          return split[0];
        }
        return columnName;
      }
    }

    onRefreshData() {
      this.startProgress();
      this.model = assign(this.model, this.generatePayload());

      const sorts = map(this.canvas.model.sorts, sort => {
        return {
          column: sort.field.name,
          direction: sort.order
        };
      });

      const groups = map(this.canvas.model.groups, group => (
        {column: group.field.name, table: group.table.name}
      ));

      this._AnalyzeService.getDataBySettings(clone(this.model))
        .then(({analysis, data, count}) => {
          this.gridData = data;
          this.gridDataTotalCount = count;
          this.model.query = analysis.queryManual || analysis.query;
          this.applyDataToGrid(this.columns, sorts, groups, this.gridData);
          this.analysisSynched();
          this.endProgress();
          this.toggleDetailsPanel(true);
        }, () => {
          this.endProgress();
        });
    }

    applyDataToGrid(columns, sorts, groups, data) {
      const grid = first(this._$componentHandler.get('ard-grid-container'));
      if (grid) {
        this.columns = this.checkColumnName(this.columns);
        grid.updateColumns(this.columns);
        grid.updateSorts(sorts);
        grid.updateSource(data);
        forEach(groups, group => grid.groupByColumn(group.column, false));
        this._$timeout(() => {
          // Delay refreshing the grid a bit to counter
          // aria errors from dev extreme
          // Need to find a better fix for this
          grid.refreshGrid();
        }, 100);
      }
    }

    reloadPreviewGrid(refresh = false) {
      if (refresh) {
        this.startDraftMode();
      }

      if (this.states.sqlMode === this.QUERY_MODE) {
        this.applyDataToGrid(this.columns, [], [], this.gridData);
        return;
      }

      const doReload = () => {
        return this._$timeout(() => {
          this._reloadTimer = null;
          this.columns = this.getSelectedColumns(this.canvas.model.tables);

          const sorts = map(this.canvas.model.sorts, sort => {
            return {
              column: sort.field.name,
              direction: sort.order
            };
          });

          const groups = map(this.canvas.model.groups, group => {
            return {
              column: group.field.name,
              table: group.table.name
            };
          });

          if (!refresh) {
            this.applyDataToGrid(this.columns, sorts, groups, this.gridData);
            return;
          }

          if (this.columns.length === 0) {
            this.gridData = [];
            this.applyDataToGrid(this.columns, sorts, groups, this.gridData);
          } else {
            this.analysisUnSynched();
          }

        }, DEBOUNCE_INTERVAL);
      };

      if (this._reloadTimer) {
        this._$timeout.cancel(this._reloadTimer);
        this._reloadTimer = doReload();
      } else {
        this._reloadTimer = doReload();
      }
    }

    getSelectedColumns(tables) {
      return fpPipe(
        fpFlatMap(fpGet('fields')),
        fpFilter(fpGet('checked'))
      )(tables);
    }

    setSqlMode(mode) {
      if (this.states.sqlMode !== mode) {
        this.states.sqlMode = mode;
      }

      this.model.query = this.model.query || this.model.queryManual;

    }

    hasSelectedColumns() {
      return this.columns.length > 0;
    }

    isSortDisabled() {
      return !this.hasSelectedColumns();
    }

    isPreviewDisabled() {
      return !this.hasSelectedColumns();
    }

    openReportPreviewModal(ev) {
      this.onRefreshData();
      const tpl = '<analyze-report-preview model="model"></analyze-report-preview>';
      this.model = assign(this.model, this.generatePayload());
      this.openPreviewModal(tpl, ev, {
        report: this.model,
        columns: this.columns,
        data: this.gridData,
        sorts: map(this.canvas.model.sorts, sort => {
          return {
            column: sort.field.name,
            direction: sort.order
          };
        })
      });
    }

    updateJoins(name, obj) {
      if (name !== 'joinChanged') {
        return;
      }

      this.canvas._$eventEmitter.emit('joinChanged', obj);
    }

    openReportSortModal(ev) {

      this.states.detailsExpanded = true;

      this._$timeout(() => {
        this.reloadPreviewGrid(false);
      });

      this.openSortModal(ev, {
        fields: this.canvas.model.getSelectedFields(),
        sorts: map(this.canvas.model.sorts, sort => clone(sort))
      })
        .then(sorts => {
          this.canvas.model.sorts = sorts;
          this.canvas._$eventEmitter.emit('sortChanged');
          this.analysisUnSynched();
          this.startDraftMode();
        });
    }

    openSaveReportModal(ev) {
      if (!this.canvas) {
        return;
      }

      this.model = assign(this.model, this.generatePayload());
      this.openSaveModal(ev, this.model);
    }
  }
};
