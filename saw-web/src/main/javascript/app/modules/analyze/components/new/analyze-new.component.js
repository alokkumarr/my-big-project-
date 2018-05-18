import * as set from 'lodash/set';
import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import * as isArray from 'lodash/isArray';

import * as template from './analyze-new.component.html';
import style from './analyze-new.component.scss';
import emptyTemplate from './analyze-new-empty.html';

import {ANALYSIS_METHODS, Events} from '../../consts';

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

    /**
     * Temporary method to force add esReport support for development
     * Should be not be used in production
     */
    addEsReportSupport(methods) {
      const tables = find(methods, method => method.category === 'table');
      if (!tables) {
        return;
      }

      tables.children = tables.children || [];
      tables.children.push({
        icon: 'icon-report',
        label: 'Report',
        type: 'table:esReport'
      });
    }

    setAvailableAnalysisMethods(methods, supportedMethods) {
      // this.addEsReportSupport(supportedMethods); // this should not be running in production

      forEach(methods, methodCategory => {
        const supportedMethodCategory = find(supportedMethods, ({category}) => category === methodCategory.category);

        forEach(methodCategory.children, method => {
          const isSupported = supportedMethodCategory ?
            find(supportedMethodCategory.children, ({type}) => this.isOfType(method, type) ||
              this.isOfType(method, 'chart:pie') ||
              this.isOfType(method, 'chart:combo') ||
              this.isOfType(method, 'chart:area') ||
              this.isOfType(method, 'chart:tsspline') ||
              this.isOfType(method, 'chart:tsPane')) :
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

    isOfType(method, inputType) {
      const referenceType = method.supportedTypes || method.type;

      if (isArray(referenceType)) {
        const match = find(referenceType, type => type === inputType);
        method.type = match || method.type;
        return Boolean(match);
      }

      return referenceType === inputType;
    }

    createAnalysis() {
      let tpl;
      let model;

      switch (this.selectedAnalysisMethod) {
      /* eslint-disable no-fallthrough */
      case 'table:esReport':
      case 'table:report':
      case 'table:pivot':
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
      case 'chart:tsPane':
        this.openUpgradedModal();
        return;
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
