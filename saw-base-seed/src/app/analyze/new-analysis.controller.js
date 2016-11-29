export class newAnalysisController {
  constructor(newAnalysisService, $mdDialog) {
    this.$mdDialog = $mdDialog;
    this.newAnalysisService = newAnalysisService;

    newAnalysisService.getMethods().then(methods => {
      this.analysisMethods = methods;
    });
    newAnalysisService.getMetrics().then(metrics => {
      this.metrics = metrics;
    });
  }

  onMetricToggle() {
    const supportedMethods = this.newAnalysisService.getSupportedMethods(this.metrics);
    this.metrics = this.newAnalysisService.setAvailableMetrics(this.metrics, supportedMethods);
    this.analysisMethods = this.newAnalysisService.setAvailableAnalysisMethods(this.analysisMethods, supportedMethods);
  }

  cancel() {
    this.$mdDialog.cancel();
  }

  createAnalysis() {
    this.$mdDialog.hide('new Analysis');
  }
}
