import filter from 'lodash/fp/filter';
import flatMap from 'lodash/fp/flatMap';
import pipe from 'lodash/fp/pipe';
import find from 'lodash/fp/find';
import get from 'lodash/fp/get';
import set from 'lodash/fp/set';
import first from 'lodash/first';
import map from 'lodash/fp/map';
import forEach from 'lodash/forEach';
import clone from 'lodash/clone';

import template from './analyze-report.component.html';
import style from './analyze-report.component.scss';
import {DEFAULT_FILTER_OPERATOR} from '../../services/filter.service';

export const AnalyzeReportComponent = {
  template,
  styles: [style],
  bindings: {
    analysis: '<'
  },
  controller: class AnalyzeReportController {
    constructor($componentHandler, $mdDialog, $scope, $timeout, $log, AnalyzeService, FilterService) {
      'ngInject';

      this._$componentHandler = $componentHandler;
      this._$mdDialog = $mdDialog;
      this._$scope = $scope;
      this._$timeout = $timeout;
      this._$log = $log;
      this._AnalyzeService = AnalyzeService;
      this._FilterService = FilterService;

      this.DESIGNER_MODE = 'designer';
      this.QUERY_MODE = 'query';

      this.showFiltersButton = false;

      this.states = {
        sqlMode: this.DESIGNER_MODE,
        detailsExpanded: false
      };

      this.data = {
        category: null,
        title: 'Untitled Report',
        description: '',
        query: ''
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

      if (this.analysis.name) {
        this.data.title = this.analysis.name;
      }

      if (this.analysis.description) {
        this.data.description = this.analysis.description;
      }

      this.unregister = this._$componentHandler.events.on('$onInstanceAdded', e => {
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
        });
    }

    getArtifacts() {
      this._AnalyzeService.getArtifacts()
        .then(data => {
          this.fillCanvas(data);
          this.reloadPreviewGrid();
          this.showFiltersButtonIfDataIsReady();
        });
    }

    generateQuery() {
      this._AnalyzeService.generateQuery({})
        .then(result => {
          this.data.query = result.query;
        });
    }

    // end requests

// filters section
    openFilterSidenav() {
      this._FilterService.openFilterSidenav(this.filters.possible);
    }

    generateFiltersOnCanvasChange() {
      this.filters.possible = this.generateFilters(this.canvas.model.getSelectedFields(), this.gridData);
      this.clearFilters();
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

    cancel() {
      this._$mdDialog.cancel();
    }

    toggleDetailsPanel() {
      this.states.detailsExpanded = !this.states.detailsExpanded;
    }

    initCanvas(canvas) {
      this.canvas = canvas;

      this.getArtifacts();

      this.canvas._$eventHandler.on('changed', () => {
        this.generateFiltersOnCanvasChange();
        this.reloadPreviewGrid();
      });

      this.canvas._$eventHandler.on('sortChanged', () => {
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

        forEach(itemA.sql_builder.filters, itemB => {
          model.addFilter({
            table: itemA.artifact_name,
            field: itemB.column_name,
            booleanCriteria: itemB.boolean_criteria,
            operator: itemB.operator,
            searchConditions: itemB.search_conditions
          });
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

        const joins = filter(join => {
          return join.leftSide.table === table;
        }, model.joins);

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

        const sorts = filter(sort => {
          return sort.table === table;
        }, model.sorts);

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

        tableArtifact.sql_builder.filters = pipe(
          filter(artifactFilter => artifactFilter.tableName === tableArtifact.artifact_name),
          this._FilterService.getFrontEnd2BackEndFilterMapper()
        )(this.filters.selected);
      });
      /* eslint-enable camelcase */

      return {
        _artifacts: tableArtifacts
      };
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
        grid.refreshGrid();
      }
    }

    getSelectedColumns(tables) {
      return pipe(
        flatMap(get('fields')),
        filter(get('checked'))
      )(tables);
    }

    setSqlMode(mode) {
      this.states.sqlMode = mode;

      if (mode === this.QUERY_MODE) {
        this.generateQuery();
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
      const tpl = '<analyze-report-preview model="$ctrl.model"></analyze-report-preview>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.$ctrl.model = {
              gridData: this.gridData,
              columns: this.columns,
              title: this.data.title,
              sorts: map(this.canvas.model.sorts, sort => {
                return {
                  column: sort.field.name,
                  direction: sort.order
                };
              })
            };
          },
          controllerAs: '$ctrl',
          targetEvent: ev,
          fullscreen: true,
          autoWrap: false,
          multiple: true
        });
    }

    openSortModal(ev) {
      this.states.detailsExpanded = true;

      const tpl = '<analyze-report-sort model="$ctrl.model"></analyze-report-sort>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.$ctrl.model = {
              fields: this.canvas.model.getSelectedFields(),
              sorts: map(this.canvas.model.sorts, sort => {
                return clone(sort);
              })
            };
          },
          controllerAs: '$ctrl',
          targetEvent: ev,
          fullscreen: true,
          multiple: true
        })
        .then(sorts => {
          this.canvas.model.sorts = sorts;
          this.canvas._$eventHandler.emit('sortChanged');
        });
    }

    openDescriptionModal(ev) {
      const tpl = '<analyze-report-description model="$ctrl.model" on-save="$ctrl.onSave($data)"></analyze-report-description>';

      this._$mdDialog.show({
        template: tpl,
        controller: scope => {
          scope.$ctrl.model = {
            description: this.data.description
          };

          scope.$ctrl.onSave = data => {
            this.data.description = data.description;
          };
        },
        controllerAs: '$ctrl',
        autoWrap: false,
        focusOnOpen: false,
        multiple: true,
        targetEvent: ev,
        clickOutsideToClose: true
      });
    }

    openExportModal() {
    }

    openSaveModal(ev) {
      if (!this.canvas) {
        return;
      }

      const tpl = '<analyze-report-save model="$ctrl.model" on-save="$ctrl.onSave($data)"></analyze-report-save>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.$ctrl.model = {
              artifacts: this.generatePayload(),
              category: this.data.category,
              title: this.data.title,
              description: this.data.description
            };

            scope.$ctrl.onSave = data => {
              this.data.category = data.category;
              this.data.title = data.title;
              this.data.description = data.description;

              this._$log.log(data);
            };
          },
          controllerAs: '$ctrl',
          autoWrap: false,
          fullscreen: true,
          focusOnOpen: false,
          multiple: true,
          targetEvent: ev,
          clickOutsideToClose: true
        });
    }

    openPublishModal() {
    }
  }
};
