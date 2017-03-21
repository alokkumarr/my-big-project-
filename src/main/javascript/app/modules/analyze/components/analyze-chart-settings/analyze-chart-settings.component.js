import template from './analyze-chart-settings.component.html';

const AXIS_MAX_COUNT = 10;

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

    $onInit() {
      console.log(this.settings);
    }

    inputChanged() {
      this.onChange({settings: this.settings});
    }
  }
};
