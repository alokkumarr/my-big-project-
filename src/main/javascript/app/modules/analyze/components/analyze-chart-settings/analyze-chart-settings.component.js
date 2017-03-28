import template from './analyze-chart-settings.component.html';

export const AnalyzeChartSettingsComponent = {
  template,
  bindings: {
    settings: '<?',
    onChange: '&'
  },
  controller: class AnalyzeChartSettingsController {
    constructor(AnalyzeService, FilterService, $mdSidenav) {
      'ngInject';

      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;
      this._$mdSidenav = $mdSidenav;
    }

    inputChanged() {
      this.onChange({settings: this.settings});
    }
  }
};
