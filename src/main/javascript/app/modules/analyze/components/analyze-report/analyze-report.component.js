import defaultsDeep from 'lodash/defaultsDeep';
import fpFilter from 'lodash/fp/filter';
import fpFlatMap from 'lodash/fp/flatMap';
import fpPipe from 'lodash/fp/pipe';
import fpGet from 'lodash/fp/get';
import first from 'lodash/first';
import map from 'lodash/map';
import keys from 'lodash/keys';
import forEach from 'lodash/forEach';
import clone from 'lodash/clone';
import cloneDeep from 'lodash/cloneDeep';
import sortBy from 'lodash/sortBy';
import filter from 'lodash/filter';
import assign from 'lodash/assign';
import uniqBy from 'lodash/uniqBy';

import template from './analyze-report.component.html';
import style from './analyze-report.component.scss';
import {DEFAULT_BOOLEAN_CRITERIA} from '../../services/filter.service';
import {ENTRY_MODES} from '../../consts';

const DEBOUNCE_INTERVAL = 500; // milliseconds

export const AnalyzeReportComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    mode: '@'
  },
  controller: class AnalyzeReportController {
    constructor($componentHandler, $mdDialog, $scope, $timeout, AnalyzeService, FilterService) {
      'ngInject';

      this._$componentHandler = $componentHandler;
      this._$mdDialog = $mdDialog;
      this._$scope = $scope;
      this._$timeout = $timeout;
      this._AnalyzeService = AnalyzeService;
      this._FilterService = FilterService;
      this._reloadTimer = null;
      this._modelLoaded = null;
      this.showProgress = false;

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
      this.columns = [];
      this.filters = [];

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
        this._modelLoaded(true);
      } else {
        this._AnalyzeService.createAnalysis(this.model.semanticId, 'report').then(analysis => {
          this.model = assign(this.model, {
            id: analysis.id,
            metric: analysis.metric,
            createdTimestamp: analysis.createdTimestamp,
            userId: analysis.userId,
            userFullName: analysis.userFullName,
            metricName: analysis.metricName
          });

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

    // filters section
    showFiltersButtonIfDataIsReady() {
      if (this.canvas && this.gridData) {
        this.showFiltersButton = true;
      }
    }

    onApplyFilters({filters, filterBooleanCriteria} = {}) {
      if (filters) {
        this.filters = filters;
        this.analysisChanged = true;
      }
      if (filterBooleanCriteria) {
        this.model.sqlBuilder.booleanCriteria = filterBooleanCriteria;
      }
    }

    onClearAllFilters() {
      this.filters = [];
      this.analysisChanged = true;
    }

    onFilterRemoved(index) {
      this.filters.splice(index, 1);
      this.analysisChanged = true;
    }
    // END filters section

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

    initCanvas(canvas) {
      this.canvas = canvas;

      if (!this.model.artifacts) {
        this.model.artifacts = [];
      } else {
        this.fillCanvas(this.model.artifacts);
        this.showFiltersButtonIfDataIsReady();
      }

      if (this.mode) {
        this.reloadPreviewGrid(true);
      }

      this._unregisterCanvasHandlers = this._unregisterCanvasHandlers.concat([

        this.canvas._$eventEmitter.on('changed', () => {
          this.canvas.model.updateFields();
          this.reloadPreviewGrid(true);
        }),

        this.canvas._$eventEmitter.on('sortChanged', () => {
          this.reloadPreviewGrid(true);
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
          c => c.columnName
        ]);

        forEach(sortedForJoin, itemB => {
          const field = table.addField(itemB.columnName);

          field.setMeta(itemB);
          field.displayName = itemB.displayName;
          field.alias = itemB.aliasName;
          field.type = itemB.type;
          field.checked = itemB.checked;
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

      forEach(this.model.sqlBuilder.orderByColumns, itemB => {
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

    generatePayload() {
      /* eslint-disable camelcase */
      const model = this.canvas.model;
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
            columnName: field.meta.columnName,
            displayName: field.meta.displayName,
            table: table.name,
            aliasName: field.alias,
            type: field.meta.type,
            hide: field.isHidden,
            joinEligible: field.meta.joinEligible,
            filterEligible: field.meta.filterEligible,
            checked: field.checked
          };

          tableArtifact.columns.push(fieldArtifact);
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
            columnName: sort.field.name,
            order: sort.order
          };

          result.sqlBuilder.orderByColumns.push(sortArtifact);
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

      return result;
    }

    getColumns(columnNames = []) {
      const fields = fpFlatMap(table => table.fields, this.canvas.model.tables);

      const columns = uniqBy(
        fpFilter(field => columnNames.indexOf(field.name) >= 0, fields),
        column => column.name
      );

      return map(columns, col => {
        col.checked = true;
        return col;
      });
    }

    onSaveQuery(analysis) {
      this.showProgress = true;
      this._AnalyzeService.getDataBySettings(clone(analysis))
        .then(({analysis, data}) => {
          this.gridData = data;
          this.model.query = analysis.queryManual || analysis.query;

          const columnNames = keys(fpGet('[0]', data));
          this.columns = this.getColumns(columnNames);
          this.applyDataToGrid(this.columns, [], [], this.gridData);
          this.showProgress = false;
        }, () => {
          this.showProgress = false;
        });
    }

    refreshGridData() {
      this.showProgress = true;
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
        .then(({analysis, data}) => {
          this.gridData = data;
          this.model.query = analysis.queryManual || analysis.query;
          this.applyDataToGrid(this.columns, sorts, groups, this.gridData);
          this.analysisChanged = false;
          this.showProgress = false;
          this.toggleDetailsPanel(true);
        }, () => {
          this.showProgress = false;
        });
    }

    applyDataToGrid(columns, sorts, groups, data) {
      this.showFiltersButtonIfDataIsReady();
      const grid = first(this._$componentHandler.get('ard-grid-container'));
      if (grid) {
        grid.updateColumns(columns);
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
            this.analysisChanged = true;
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

    openFiltersModal(ev) {
      const tpl = '<analyze-filter-modal filters="filters" artifacts="artifacts" filter-boolean-criteria="booleanCriteria"></analyze-filter-modal>';
      this._$mdDialog.show({
        template: tpl,
        controller: scope => {
          scope.filters = cloneDeep(this.filters);
          scope.artifacts = this.model.artifacts;
          scope.booleanCriteria = this.model.sqlBuilder.booleanCriteria;
        },
        targetEvent: ev,
        fullscreen: true,
        autoWrap: false,
        multiple: true
      }).then(this.onApplyFilters.bind(this));
    }

    openPreviewModal(ev) {
      const tpl = '<analyze-report-preview model="model"></analyze-report-preview>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.model = {
              report: this.model,
              columns: this.columns,
              sorts: map(this.canvas.model.sorts, sort => {
                return {
                  column: sort.field.name,
                  direction: sort.order
                };
              })
            };
          },
          targetEvent: ev,
          fullscreen: true,
          autoWrap: false,
          multiple: true
        });
    }

    updateJoins(name, obj) {
      if (name !== 'joinChanged') {
        return;
      }

      this.canvas._$eventEmitter.emit('joinChanged', obj);
    }

    openSortModal(ev) {
      this.states.detailsExpanded = true;

      this._$timeout(() => {
        this.reloadPreviewGrid(false);
      });

      const tpl = '<analyze-report-sort model="model"></analyze-report-sort>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.model = {
              fields: this.canvas.model.getSelectedFields(),
              sorts: map(this.canvas.model.sorts, sort => {
                return clone(sort);
              })
            };
          },
          targetEvent: ev,
          fullscreen: true,
          autoWrap: false,
          multiple: true
        })
        .then(sorts => {
          this.canvas.model.sorts = sorts;
          this.canvas._$eventEmitter.emit('sortChanged');
          this.analysisChanged = true;
        });
    }

    openDescriptionModal(ev) {
      const tpl = '<analyze-report-description model="model" on-save="onSave($data)"></analyze-report-description>';

      this._$mdDialog.show({
        template: tpl,
        controller: scope => {
          scope.model = {
            description: this.model.description
          };

          scope.onSave = data => {
            this.model.description = data.description;
          };
        },
        autoWrap: false,
        focusOnOpen: false,
        multiple: true,
        targetEvent: ev,
        clickOutsideToClose: true
      });
    }

    openSaveModal(ev) {
      if (!this.canvas) {
        return;
      }

      if (this.states.sqlMode === this.DESIGNER_MODE) {
        this.model.query = '';
      }

      this.model = assign(this.model, this.generatePayload());
      const tpl = '<analyze-report-save model="model" on-save="onSave($data)"></analyze-report-save>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.model = clone(this.model);

            scope.onSave = data => {
              this.model.id = data.id;
              this.model.name = data.name;
              this.model.description = data.description;
              this.model.categoryId = data.categoryId;
            };
          },
          autoWrap: false,
          fullscreen: true,
          focusOnOpen: false,
          multiple: true,
          targetEvent: ev,
          clickOutsideToClose: true
        }).then(successfullySaved => {
          if (successfullySaved) {
            this.onAnalysisSaved(successfullySaved);
          }
        });
    }

    onAnalysisSaved(successfullySaved) {
      this.$dialog.hide(successfullySaved);
    }
  }
};
