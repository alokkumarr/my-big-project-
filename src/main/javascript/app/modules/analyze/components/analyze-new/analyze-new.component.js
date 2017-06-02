import set from 'lodash/set';
import forEach from 'lodash/forEach';
import find from 'lodash/find';

import template from './analyze-new.component.html';
import style from './analyze-new.component.scss';
import emptyTemplate from './analyze-new-empty.html';

import {AnalyseTypes} from '../../consts';

export const AnalyzeNewComponent = {
  template,
  bindings: {
    metrics: '<',
    subCategory: '@'
  },
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
      this.selectedMetric = null;

      this._AnalyzeService.getMethods()
        .then(methods => {
          this.methods = methods;
        });
    }

    onMetricSelected() {
      this.setAvailableAnalysisMethods(this.methods, this.selectedMetric.supports);
      // unselect the method, so only supported methods can be selected
      this.selectedAnalysisMethod = '';
    }

    setAvailableAnalysisMethods(methods, supportedMethods) {
      forEach(methods, methodCategory => {
        const supportedMethodCategory = find(supportedMethods, ({category}) => category === methodCategory.category);

        forEach(methodCategory.children, method => {
          const isSupported = supportedMethodCategory ?
            find(supportedMethodCategory.children, ({type}) => type === method.type) :
            false;
          set(method, 'disabled', !isSupported);
        });
      });
    }

    createAnalysis() {
      let tpl;
      let model;
      let type;
      const metricId = this.selectedMetric.id;
      const metricName = this.selectedMetric.metricName;

      switch (this.selectedAnalysisMethod) {
        case 'table:report':
          tpl = '<analyze-report model="model"></analyze-report>';
          model = {
            type: AnalyseTypes.Report,
            name: 'Untitled Analysis',
            description: '',
            categoryId: this.subCategory,
            semanticId: metricId,
            metricName,
            scheduled: null
          };
          break;
        case 'table:pivot':
          tpl = '<analyze-pivot model="model"></analyze-pivot>';
          model = {
            type: AnalyseTypes.Pivot,
            name: 'Untitled Analysis',
            description: '',
            categoryId: this.subCategory,
            semanticId: this.selectedMetric,
            scheduled: null
          };
          break;
        case 'chart:column':
        case 'chart:line':
        case 'chart:stack':
        case 'chart:pie':
        case 'chart:donut':
          type = this.selectedAnalysisMethod.split(':')[1];
          tpl = '<analyze-chart model="model"></analyze-chart>';
          model = {
            type: AnalyseTypes.Chart,
            chartType: type,
            name: 'Untitled Chart',
            metricName,
            description: '',
            categoryId: this.subCategory,
            semanticId: this.selectedMetric,
            scheduled: null
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
      }).then(successfullySaved => {
        if (successfullySaved) {
          this.$dialog.hide(successfullySaved);
        }
      });
    }
  }
};
