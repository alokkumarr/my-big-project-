import filter from 'lodash/fp/filter';
import flatMap from 'lodash/fp/flatMap';
import pipe from 'lodash/fp/pipe';
import get from 'lodash/fp/get';
import set from 'lodash/fp/set';
import first from 'lodash/first';
import map from 'lodash/map';

import template from './analyze-report.component.html';
import style from './analyze-report.component.scss';
import {DEFAULT_FILTER_OPERATOR} from '../../services/filter.service';

export const AnalyzeReportComponent = {
  template,
  styles: [style],
  controller: class AnalyzeReportController {
    constructor($componentHandler, $mdDialog, $scope, $timeout, AnalyzeService, FilterService) {
      this._$componentHandler = $componentHandler;
      this._$mdDialog = $mdDialog;
      this._$scope = $scope;
      this._$timeout = $timeout;
      this._AnalyzeService = AnalyzeService;
      this._FilterService = FilterService;

      this.DESIGNER_MODE = 'designer';
      this.QUERY_MODE = 'query';

      this.states = {
        sqlMode: this.DESIGNER_MODE,
        detailsExpanded: false
      };

      this.data = {
        query: ''
      };

      this.gridData = [];
      this.columns = [];

      this._AnalyzeService.getDataByQuery()
        .then(data => {
          this.gridData = data;
          this.reloadPreviewGrid();
        });

      $componentHandler.events.on('$onInstanceAdded', e => {
        if (e.key === 'ard-canvas') {
          this.initCanvas(e.instance);
        }
      });

      this.filters = [{
        label: 'Affiliates',
        type: 'string',
        items: ['DIRECT TV', 'Red Ventures', 'ClearLink', 'All Connect', 'Q-ology', 'Acceller'],
        model: null,
        operator: DEFAULT_FILTER_OPERATOR
      }, {
        label: 'Regions',
        type: 'number',
        model: null,
        operator: DEFAULT_FILTER_OPERATOR
      }];
    }

    $onInit() {
      this._FilterService.onApplyFilters(this.onApplyFilters.bind(this));
      this._FilterService.onClearAllFilters(this.onClearAllFilters.bind(this));
    }

    $onDestroy() {
      this._FilterService.offApplyFilters();
      this._FilterService.offClearAllFilters();
    }

// filters section
    openFilterSidenav() {
      this._FilterService.openFilterSidenav(this.filters);
    }

    onApplyFilters(event, filters) {
      this.filters = filters;
    }

    onClearAllFilters() {
      this.filters = map(this.filters, pipe(
        set('model', null),
        set('operator', DEFAULT_FILTER_OPERATOR)
        ));
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

      this._AnalyzeService.getArtifacts()
        .then(data => {
          this.canvas.model.fill(data);
          this.reloadPreviewGrid();
        });

      this.canvas._$eventHandler.on('changed', () => {
        this.reloadPreviewGrid();
      });
    }

    reloadPreviewGrid() {
      this._$timeout(() => {
        this.columns = this.getSelectedColumns(this.canvas.model.tables);

        const grid = first(this._$componentHandler.get('ard-grid-container'));

        if (grid) {
          grid.reload(this.columns, this.gridData);
        }
      });
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
        this._AnalyzeService.generateQuery({})
          .then(result => {
            this.data.query = result.query;
          });
      }
    }

    openPreviewModal(ev) {
      const scope = this._$scope.$new();

      scope.model = {
        gridData: this.gridData,
        columns: this.columns
      };

      this._$mdDialog
        .show({
          template: '<analyze-report-preview model="model"></analyze-report-preview>',
          targetEvent: ev,
          fullscreen: true,
          autoWrap: false,
          skipHide: true,
          scope: scope
        });
    }

    openSortModal(ev) {
      const scope = this._$scope.$new();

      scope.model = {
        fields: this.canvas.model.getSelectedFields(),
        sorts: this.canvas.model.sorts
      };

      this._$mdDialog
        .show({
          template: '<analyze-report-sort model="model"></analyze-report-sort>',
          targetEvent: ev,
          fullscreen: true,
          skipHide: true,
          scope: scope
        });
    }

    export() {

    }

    save() {
      if (!this.canvas) {
        return;
      }

      this.$dialog.showLoader();

      const payload = this.canvas.model.generatePayload();

      this._AnalyzeService.saveReport(payload)
        .finally(() => {
          this.$dialog.hideLoader();
        });
    }

    publish() {

    }
  }
};
