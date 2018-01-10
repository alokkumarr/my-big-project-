import * as set from 'lodash/set';
import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';

import * as template from './analyze-new.component.html';
import style from './analyze-new.component.scss';
import emptyTemplate from './analyze-new-empty.html';

import {AnalyseTypes, ENTRY_MODES, ANALYSIS_METHODS, Events} from '../../consts';

export const AnalyzeNewComponent = {
  template,
  bindings: {
    metrics: '<',
    subCategory: '@'
  },
  styles: [style],
  controller: class AnalyzeNewController {
    constructor($scope, $mdDialog, AnalyzeService, AnalyzeDialogService, $eventEmitter) {
      'ngInject';
      this._$scope = $scope;
      this._$mdDialog = $mdDialog;
      this._AnalyzeService = AnalyzeService;
      this.methods = ANALYSIS_METHODS;
      this._AnalyzeDialogService = AnalyzeDialogService;
      this._$eventEmitter = $eventEmitter;
    }

    $onInit() {
      this.selectedAnalysisMethod = '';
      this.selectedMetric = null;
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
            find(supportedMethodCategory.children, ({type}) => type === method.type ||
              method.type === 'chart:pie' ||
              method.type === 'chart:combo' ||
              method.type === 'chart:area' || method.type === 'chart:tsspline') :
            false;
          set(method, 'disabled', !isSupported);
        });
      });
    }

    openUpgradedModal() {
      const semanticId = this.selectedMetric.id;
      const metricName = this.selectedMetric.metricName;
      const method = this.selectedAnalysisMethod.split(':');
      const isChartType = method[0] === 'chart';
      const type = isChartType ? method[0] : method[1];
      const chartType = isChartType ? method[1] : null;
      const model = {
        type,
        chartType,
        categoryId: this.subCategory,
        semanticId,
        metricName,
        name: 'Untitled Analysis',
        description: '',
        scheduled: null
      };
      this._AnalyzeDialogService.openNewAnalysisDialog(model)
        .afterClosed().subscribe(successfullySaved => {
          if (successfullySaved) {
            this.$dialog.hide(successfullySaved);
            this._$eventEmitter.emit(Events.AnalysesRefresh);
          }
        });
    }

    createAnalysis() {
      let tpl;
      let model;
      let type;
      const semanticId = this.selectedMetric.id;
      const metricName = this.selectedMetric.metricName;
      const mode = ENTRY_MODES.NEW;

      switch (this.selectedAnalysisMethod) {
      case 'table:report':
        tpl = `<analyze-report model="model" mode="${mode}"></analyze-report>`;
        model = {
          type: AnalyseTypes.Report,
          name: 'Untitled Analysis',
          description: '',
          categoryId: this.subCategory,
          semanticId,
          metricName,
          scheduled: null
        };
        break;
      case 'table:pivot':
        tpl = `<analyze-pivot model="model" mode="${mode}"></analyze-pivot>`;
        model = {
          type: AnalyseTypes.Pivot,
          name: 'Untitled Analysis',
          description: '',
          categoryId: this.subCategory,
          semanticId,
          scheduled: null
        };
        break;
      case 'chart:column':
      case 'chart:bar':
      case 'chart:line':
      case 'chart:stack':
      case 'chart:pie':
      case 'chart:donut':
      case 'chart:scatter':
      case 'chart:bubble':
      case 'chart:area':
      case 'chart:combo':
      case 'chart:tsspline':
        type = this.selectedAnalysisMethod.split(':')[1];
        tpl = `<analyze-chart model="model" mode="${mode}"></analyze-chart>`;
        model = {
          type: AnalyseTypes.Chart,
          chartType: type,
          name: 'Untitled Chart',
          metricName,
          semanticId,
          description: '',
          categoryId: this.subCategory,
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
        clickOutsideToClose: true,
        hasBackdrop: false
      }).then(successfullySaved => {
        if (successfullySaved) {
          this.$dialog.hide(successfullySaved);
          this._$eventEmitter.emit(Events.AnalysesRefresh);
        }
      });
    }
  }
};
