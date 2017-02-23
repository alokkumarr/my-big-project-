import template from './analyze-published-detail.component.html';
import style from './analyze-published-detail.component.scss';

export const AnalyzePublishedDetailComponent = {
  template,
  styles: [style],
  controller: class AnalyzePublishedDetailController {
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
      this._AnalyzeService.getAnalysisById(this._$state.params.publishId)
        .then(analysis => {
          this.analysis = analysis;
          console.log(analysis);
        });
    }

  }
};
