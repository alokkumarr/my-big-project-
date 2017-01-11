import filter from 'lodash/fp/filter';
import flatMap from 'lodash/fp/flatMap';
import pipe from 'lodash/fp/pipe';
import get from 'lodash/fp/get';

import descriptionTemplate from '../analyze-report-description/analyze-description.tmpl.html';
import {DescriptionController} from '../analyze-report-description/analyze-description.controller';
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
          this.canvas.model.precess(data);
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
        this.data.query = this.canvas.model.generateQuery();
      }
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

    openDescriptionModal() {

      return this._$mdDialog.show({
        controller: DescriptionController,
        template: descriptionTemplate,
        fullscreen: false,
        skipHide: true,
        locals: {description: 'Description...'},
        clickOutsideToClose:true
      })
    }

    editDescription() {
      this.openDescriptionModal()
        .then(description => {
          // console.log(description);
        });
    }
  }
};
