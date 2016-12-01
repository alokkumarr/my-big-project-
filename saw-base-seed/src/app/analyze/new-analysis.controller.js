export class newAnalysisController {
  constructor(newAnalysisService, $mdDialog, $log) {
    this.$log = $log;
    this.$mdDialog = $mdDialog;
    this.newAnalysisService = newAnalysisService;
    this.selectedAnalysisMethod = '';

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
    // unselect the method, so only supported methods can be selected
    this.selectedAnalysisMethod = '';
  }

  cancel() {
    this.$mdDialog.cancel();
  }

  onAnalysisMethodSelected() {
    this.$log.info('Selected method: ', this.selectedAnalysisMethod);
  }

  createAnalysis() {
    this.$mdDialog.hide('new Analysis');
  }
}
