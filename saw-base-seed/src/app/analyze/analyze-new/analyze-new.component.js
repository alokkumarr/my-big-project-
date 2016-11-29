import template from './analyze-new.component.html';
import style from './analyze-new.component.scss';

export const AnalyzeNewComponent = {
  template,
  styles: [style],
  controller: class AnalyzeNewController {
    constructor($mdDialog, AnalyzeService) {
      this.$mdDialog = $mdDialog;
      this.analyzeService = AnalyzeService;
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
    }

    cancel() {
      this.$mdDialog.cancel();
    }

    createAnalysis() {
      this.$mdDialog.hide('new Analysis');
    }
  }
};
