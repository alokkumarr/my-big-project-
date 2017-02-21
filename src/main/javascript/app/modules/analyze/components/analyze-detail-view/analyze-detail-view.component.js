import template from './analyze-detail-view.component.html';
import style from './analyze-detail-view.component.scss';

export const AnalyzeDetailViewComponent = {
  template,
  styles: [style],
  controller: class AnalyzeDetailViewController {
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
      this._AnalyzeService.getAnalysisById(this._$state.params.id)
        .then(analysis => {
          this.analysis = analysis;
          console.log(analysis);
        });
    }

  }
};
