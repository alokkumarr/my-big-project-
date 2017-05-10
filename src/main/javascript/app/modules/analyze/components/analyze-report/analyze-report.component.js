import fpFilter from 'lodash/fp/filter';
import fpFlatMap from 'lodash/fp/flatMap';
import fpPipe from 'lodash/fp/pipe';
import fpGet from 'lodash/fp/get';
import fpMap from 'lodash/fp/map';
import first from 'lodash/first';
import map from 'lodash/map';
import forEach from 'lodash/forEach';
import clone from 'lodash/clone';
import isEmpty from 'lodash/isEmpty';
import filter from 'lodash/filter';

import template from './analyze-report.component.html';
import style from './analyze-report.component.scss';

import {ANALYZE_FILTER_SIDENAV_IDS} from '../analyze-filter-sidenav/analyze-filter-sidenav.component';

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
      this.filteredGridData = [];
      this.columns = [];
      this.filters = {
        // array of strings with the columns displayName that the filter is based on
        selected: [],
        // possible filters shown in the sidenav, generated from the checked columns
        // of the jsPlumb canvas.model
        possible: []
      };

      this.getDataByQuery();
    }

    $onInit() {
      this._FilterService.onApplyFilters(filters => this.onApplyFilters(filters));
      this._FilterService.onClearAllFilters(() => this.onClearAllFilters());

      if (this.mode === 'fork') {
        this.model.id = null;
      }

      if (this.model.query) {
        // give designer mode chance to load, then switch to query mode
        this._$timeout(() => {
          this.setSqlMode(this.QUERY_MODE);
        }, 100);
      }

      this.unregister = this._$componentHandler.on('$onInstanceAdded', e => {
        if (e.key === 'ard-canvas') {
          this.initCanvas(e.instance);
        }
      });
    }

    $onDestroy() {
      this._FilterService.offApplyFilters();
      this._FilterService.offClearAllFilters();

      if (this.unregister) {
        this.unregister();
      }
    }

    // requests
    getDataByQuery() {
      this._AnalyzeService.getDataByQuery()
        .then(data => {
          this.gridData = data;
          this.filteredGridData = data;
          this.reloadPreviewGrid();
          this.showFiltersButtonIfDataIsReady();
          this.canvas._$eventEmitter.emit('changed');
          this.canvas._$eventEmitter.emit('sortChanged');
        });
    }

    getArtifacts() {
      this._AnalyzeService.getArtifacts()
        .then(data => {
          this.fillCanvas(data);
          this.reloadPreviewGrid();
          this.showFiltersButtonIfDataIsReady();
          this.filters.possible = this.generateFilters(this.canvas.model.getSelectedFields(), this.gridData);
          if (!isEmpty(this.canvas.model.filters)) {
            this.filters.selected = this.canvas.model.filters;
            this._FilterService.mergeCanvasFiltersWithPossibleFilters(this.canvas.model.filters, this.filters.possible);
            this.onApplyFilters(this.filters.possible);
          }
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
    openFilterSidenav() {
      this._FilterService.openFilterSidenav(this.filters.possible, ANALYZE_FILTER_SIDENAV_IDS.designer);
    }

    generateFiltersOnCanvasChange() {
      this.filters.possible = this.generateFilters(this.canvas.model.getSelectedFields(), this.gridData);
      if (!isEmpty(this.canvas.model.filters)) {
        this.filters.selected = this.canvas.model.filters;
        this._FilterService.mergeCanvasFiltersWithPossibleFilters(this.canvas.model.filters, this.filters.possible);
        this.onApplyFilters(this.filters.possible);
      }
      // this.clearFilters();
    }

    showFiltersButtonIfDataIsReady() {
      if (this.canvas && this.gridData) {
        this.showFiltersButton = true;
      }
    }

    onApplyFilters(filters) {
      this.filters.possible = filters;
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(filters);

      this.filterGridData();
    }

    filterGridData() {
      this.filteredGridData = this._FilterService.getGridDataFilter(this.filters.selected)(this.gridData);

      this.reloadPreviewGrid();
    }

    onClearAllFilters() {
      this.clearFilters();
    }

    clearFilters() {
      this.filters.possible = this._FilterService.getFilterClearer()(this.filters.possible);
      this.filters.selected = [];
      this.filteredGridData = this.gridData;
      this.reloadPreviewGrid();
    }

    onFilterRemoved(filter) {
      filter.model = null;
      this.filterGridData();
    }

    generateFilters(selectedFields, gridData) {
      return this._FilterService.getCanvasFieldsToFiltersMapper(gridData)(selectedFields);
    }
    // END filters section

    toggleDetailsPanel() {
      this.states.detailsExpanded = !this.states.detailsExpanded;

      if (this.states.detailsExpanded) {
        this._$timeout(() => {
          this.reloadPreviewGrid();
        }, 500);
      }
    }

    initCanvas(canvas) {
      this.canvas = canvas;

      if (!this.model.artifacts) {
        this.getArtifacts();
      } else {
        this.fillCanvas(this.model.artifacts);
        this.reloadPreviewGrid();
        this.showFiltersButtonIfDataIsReady();
        this.filters.possible = this.generateFilters(this.canvas.model.getSelectedFields(), this.gridData);
        if (!isEmpty(this.canvas.model.filters)) {
          this.filters.selected = this.canvas.model.filters;
          this._FilterService.mergeCanvasFiltersWithPossibleFilters(this.canvas.model.filters, this.filters.possible);
          this.onApplyFilters(this.filters.possible);
        }
      }

      this.canvas._$eventEmitter.on('changed', () => {
        this.generateFiltersOnCanvasChange();
        this.reloadPreviewGrid();
      });

      this.canvas._$eventEmitter.on('sortChanged', () => {
        this.reloadPreviewGrid();
      });
    }

    fillCanvas(data) {
      const model = this.canvas.model;

      model.clear();

      /* eslint-disable camelcase */
      forEach(data, itemA => {
        const table = model.addTable(itemA.artifact_name);

        table.setMeta(itemA);
        table.setPosition(itemA.artifact_position[0], itemA.artifact_position[1]);

        forEach(itemA.artifact_attributes, itemB => {
          const field = table.addField(itemB.column_name);

          field.setMeta(itemB);
          field.displayName = itemB.display_name;
          field.alias = itemB.alias_name;
          field.type = itemB.type;
          field.checked = itemB.checked;
          field.isHidden = Boolean(itemB.hide);
          field.isJoinEligible = Boolean(itemB.join_eligible);
          field.isFilterEligible = Boolean(itemB.filter_eligible);
        });
      });

      forEach(data, itemA => {
        forEach(itemA.sql_builder.joins, itemB => {
          const tableA = itemB.criteria[0].table_name;
          const tableB = itemB.criteria[1].table_name;

          if (tableA !== tableB) {
            model.addJoin(itemB.type, {
              table: tableA,
              field: itemB.criteria[0].column_name,
              side: itemB.criteria[0].side
            }, {
              table: tableB,
              field: itemB.criteria[1].column_name,
              side: itemB.criteria[1].side
            });
          }
        });

        forEach(itemA.sql_builder.order_by_columns, itemB => {
          model.addSort({
            table: itemA.artifact_name,
            field: itemB.column_name,
            order: itemB.order
          });
        });

        forEach(itemA.sql_builder.group_by_columns, itemB => {
          model.addGroup({
            table: itemA.artifact_name,
            field: itemB
          });
        });

        forEach(itemA.sql_builder.filters, backEndFilter => {
          model.addFilter(this._FilterService.getBackEnd2FrontEndFilterMapper()(backEndFilter));
        });
      });
      /* eslint-enable camelcase */
    }

    generatePayload() {
      const model = this.canvas.model;
      const tableArtifacts = [];

      /* eslint-disable camelcase */
      forEach(model.tables, table => {
        const tableArtifact = {
          artifact_name: table.name,
          artifact_position: [table.x, table.y],
          artifact_attributes: [],
          sql_builder: {
            group_by_columns: [],
            order_by_columns: [],
            joins: [],
            filters: []
          },
          data: []
        };

        tableArtifacts.push(tableArtifact);

        forEach(table.fields, field => {
          const fieldArtifact = {
            column_name: field.meta.column_name,
            display_name: field.meta.display_name,
            alias_name: field.alias,
            type: field.meta.type,
            hide: field.isHidden,
            join_eligible: field.meta.join_eligible,
            filter_eligible: field.meta.filter_eligible,
            checked: field.checked
          };

          tableArtifact.artifact_attributes.push(fieldArtifact);
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
            table_name: join.leftSide.table.name,
            column_name: join.leftSide.field.name,
            side: join.leftSide.side
          });

          joinArtifact.criteria.push({
            table_name: join.rightSide.table.name,
            column_name: join.rightSide.field.name,
            side: join.rightSide.side
          });

          tableArtifact.sql_builder.joins.push(joinArtifact);
        });

        const sorts = filter(model.sorts, sort => {
          return sort.field.table === table;
        });

        forEach(sorts, sort => {
          const sortArtifact = {
            column_name: sort.field.name,
            order: sort.order
          };

          tableArtifact.sql_builder.order_by_columns.push(sortArtifact);
        });

        const groups = filter(group => {
          return group.table === table;
        }, model.groups);

        forEach(groups, group => {
          tableArtifact.sql_builder.group_by_columns.push(group.field.name);
        });

        tableArtifact.sql_builder.filters = fpPipe(
          filter(artifactFilter => artifactFilter.tableName === tableArtifact.artifact_name),
          fpMap(this._FilterService.getFrontEnd2BackEndFilterMapper())
        )(this.filters.selected);
      });
      /* eslint-enable camelcase */

      return tableArtifacts;
    }

    reloadPreviewGrid() {
      this.columns = this.getSelectedColumns(this.canvas.model.tables);

      const sorts = map(this.canvas.model.sorts, sort => {
        return {
          column: sort.field.name,
          direction: sort.order
        };
      });

      const grid = first(this._$componentHandler.get('ard-grid-container'));

      if (grid) {
        grid.updateColumns(this.columns);
        grid.updateSorts(sorts);
        grid.updateSource(this.filteredGridData);
        this._$timeout(() => {
          // Delay refreshing the grid a bit to counter
          // aria errors from dev extreme
          // Need to find a better fix for this
          grid.refreshGrid();
        }, 100);
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

        if (mode === this.QUERY_MODE) {
          this.generateQuery();
        }
      }
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

    openSortModal(ev) {
      this.states.detailsExpanded = true;

      this._$timeout(() => {
        this.reloadPreviewGrid();
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

      this.model.artifacts = this.generatePayload();
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
              this.model.category = data.category;
            };
          },
          autoWrap: false,
          fullscreen: true,
          focusOnOpen: false,
          multiple: true,
          targetEvent: ev,
          clickOutsideToClose: true
        }).then(successfullySaved => {
          this.onAnalysisSaved(successfullySaved);
        });
    }

    onAnalysisSaved(successfullySaved) {
      this.$dialog.hide(successfullySaved);
    }
  }
};
