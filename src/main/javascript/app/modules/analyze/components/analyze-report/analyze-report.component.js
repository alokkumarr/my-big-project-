import filter from 'lodash/fp/filter';
import flatMap from 'lodash/fp/flatMap';
import pipe from 'lodash/fp/pipe';
import get from 'lodash/fp/get';

import template from './analyze-report.component.html';
import style from './analyze-report.component.scss';

export const AnalyzeReportComponent = {
  template,
  styles: [style],
  controller: class AnalyzeReportController {
    constructor($componentHandler, $mdDialog, $scope, AnalyzeService) {
      this._$mdDialog = $mdDialog;
      this._$scope = $scope;
      this._AnalyzeService = AnalyzeService;

      this.DESIGNER_MODE = 'designer';
      this.QUERY_MODE = 'query';

      this.states = {
        sqlMode: this.DESIGNER_MODE,
        detailsExpanded: false
      };

      this.data = {
        query: ''
      };

      this.gridData = []

      this._AnalyzeService.getDataByQuery()
        .then(data => {
          this.gridData = data;
        });

      $componentHandler.events.on('$onInstanceAdded', e => {
        if (e.key === 'ard-canvas') {
          this.initCanvas(e.instance);
        }
      });
    }

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
          this.columns = this.getSelectedColumns(this.canvas.model.tables);
        });
    }

    getSelectedColumns(tables) {
      return this.selectedCulomns = pipe(
        flatMap(get('fields')),
        filter(get('selected'))
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

      scope.model = {};

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
