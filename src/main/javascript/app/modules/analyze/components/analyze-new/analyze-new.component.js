import template from './analyze-new.component.html';
import style from './analyze-new.component.scss';
import emptyTemplate from './analyze-new-empty.html';

export const AnalyzeNewComponent = {
  template,
  styles: [style],
  controller: class AnalyzeNewController {
    constructor($mdDialog, $log, $document, $scope, AnalyzeService) {
      'ngInject';

      this.$mdDialog = $mdDialog;
      this.$log = $log;
      this._$scope = $scope;
      this.$document = $document;
      this.analyzeService = AnalyzeService;
      this.selectedAnalysisMethod = '';
    }

    $onInit() {
      this.analyzeService.getMethods()
        .then(methods => {
          this.methods = methods;
        });

      this.analyzeService.getMetrics()
        .then(metrics => {
          this.metrics = metrics;
        });
    }

    onMetricToggle() {
      const supportedMethods = this.analyzeService.getSupportedMethods(this.metrics);

      this.metrics = this.analyzeService.setAvailableMetrics(this.metrics, supportedMethods);
      this.methods = this.analyzeService.setAvailableAnalysisMethods(this.methods, supportedMethods);

      // unselect the method, so only supported methods can be selected
      this.selectedAnalysisMethod = '';
    }

    onAnalysisMethodSelected() {
      this.$log.info('Selected method: ', this.selectedAnalysisMethod);
    }

    cancel() {
      this.$mdDialog.cancel();
    }

    createAnalysis() {
      let tpl;

      switch (this.selectedAnalysisMethod) {
        case 'table:report':
          tpl = '<analyze-report analysis="$ctrl.newAnalysis"></analyze-report>';
          break;
        default:
          tpl = emptyTemplate;
          break;
      }

      this.$mdDialog.show({
        template: tpl,
        controller: scope => {
          scope.$ctrl.newAnalysis = {
            name: 'Untitled'
          };
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
