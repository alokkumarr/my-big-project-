import filter from 'lodash/filter';
import map from 'lodash/map';

import template from './analyze-new.component.html';
import style from './analyze-new.component.scss';
import emptyTemplate from './analyze-new-empty.html';

import {AnalyseTypes} from '../../consts';

export const AnalyzeNewComponent = {
  template,
  styles: [style],
  controller: class AnalyzeNewController {
    constructor($scope, $mdDialog, AnalyzeService) {
      'ngInject';
      this._$scope = $scope;
      this._$mdDialog = $mdDialog;
      this._AnalyzeService = AnalyzeService;
    }

    $onInit() {
      this.selectedAnalysisMethod = '';

      this._AnalyzeService.getMethods()
        .then(methods => {
          this.methods = methods;
        });

      this._AnalyzeService.getMetrics()
        .then(metrics => {
          this.metrics = metrics;
        });
    }

    onMetricToggle() {
      const supportedMethods = this._AnalyzeService.getSupportedMethods(this.metrics);

      this.metrics = this._AnalyzeService.setAvailableMetrics(this.metrics, supportedMethods);
      this.methods = this._AnalyzeService.setAvailableAnalysisMethods(this.methods, supportedMethods);

      // unselect the method, so only supported methods can be selected
      this.selectedAnalysisMethod = '';
    }

    getSelectedMetrics() {
      const metrics = filter(this.metrics, metric => {
        return metric.checked;
      });

      return map(metrics, metric => {
        return metric.name;
      });
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    createAnalysis() {
      let tpl;
      let model;
      let type;

      switch (this.selectedAnalysisMethod) {
        case 'table:report':
          tpl = '<analyze-report model="model"></analyze-report>';
          model = {
            type: AnalyseTypes.Report,
            name: 'Untitled Analysis',
            description: '',
            category: null,
            metrics: this.getSelectedMetrics(),
            scheduled: null,
            artifacts: null
          };
          break;
        case 'table:pivot':
          tpl = '<analyze-pivot model="model"></analyze-pivot>';
          model = {
            type: AnalyseTypes.Pivot,
            name: 'Untitled Analysis',
            description: '',
            category: null,
            metrics: this.getSelectedMetrics(),
            scheduled: null,
            artifacts: null
          };
          break;
        case 'chart:column':
        case 'chart:line':
        case 'chart:stacked':
          type = this.selectedAnalysisMethod.split(':')[1];
          tpl = '<analyze-chart model="model"></analyze-chart>';
          model = {
            type: AnalyseTypes.Chart,
            chartType: type,
            name: 'Untitled Chart',
            description: '',
            category: null,
            metrics: this.getSelectedMetrics(),
            scheduled: null,
            artifacts: null
          };
          break;
        default:
          tpl = emptyTemplate;
          break;
      }

      this._$mdDialog.show({
        template: tpl,
        controller: scope => {
          scope.model = model;
        },
        controllerAs: '$ctrl',
        autoWrap: false,
        focusOnOpen: false,
        multiple: true,
        clickOutsideToClose: true
      });
    }
  }
};
