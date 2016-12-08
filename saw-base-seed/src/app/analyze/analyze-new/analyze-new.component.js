import template from './analyze-new.component.html';
import style from './analyze-new.component.scss';

export const AnalyzeNewComponent = {
  template,
  styles: [style],
  controller: class AnalyzeNewController {
    constructor($mdDialog, $log, AnalyzeService) {
      this.$mdDialog = $mdDialog;
      this.$log = $log;
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
      this.$mdDialog.hide('new Analysis');
    }
  }
};
