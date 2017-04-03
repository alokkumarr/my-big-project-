import template from './analyze-pivot-settings.component.html';
import style from './analyze-pivot-settings.component.scss';

export const AnalyzePivotSettingsComponent = {
  template,
  styles: [style],
  bindings: {
    settings: '<?',
    onChange: '&'
  },
  controller: class AnalyzePivotSettingsController {
    constructor(AnalyzeService, FilterService, $mdSidenav) {
      'ngInject';

      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;
      this._$mdSidenav = $mdSidenav;
      this.summaryTypes = ['sum', 'min', 'max', 'avg'];
    }

    inputChanged(field) {
      this.onChange({field});
    }
  }
};
