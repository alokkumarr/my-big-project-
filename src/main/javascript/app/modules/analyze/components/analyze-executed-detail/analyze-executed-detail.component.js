import template from './analyze-executed-detail.component.html';
import style from './analyze-executed-detail.component.scss';

export const AnalyzeExecutedDetailComponent = {
  template,
  styles: [style],
  controller: class AnalyzeExecutedDetailController {
    constructor(AnalyzeService, $state, $window) {
      'ngInject';
      this._AnalyzeService = AnalyzeService;
      this._$state = $state;
      this._$window = $window;
    }

    $onInit() {
      this.loadAnalysis();
    }

    loadAnalysis() {
      this._AnalyzeService.getAnalysisById(this._$state.params.executedInstanceId)
        .then(analysis => {
          this.analysis = analysis;
          console.log(analysis);
        });
    }

  }
};
