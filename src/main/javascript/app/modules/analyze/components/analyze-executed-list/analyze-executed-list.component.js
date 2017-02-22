import template from './analyze-executed-list.component.html';
// import style from './analyze-executed-list.component.scss';

export const AnalyzeExecutedListComponent = {
  template,
  // styles: [style],
  controller: class AnalyzeExecutedListController {
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
      this._AnalyzeService.getExecutedAnalysesByAnalysisId(this._$state.params.analysisId)
        .then(analyses => {
          this.analyses = analyses;
          console.log(analyses);
        });
    }

  }
};
