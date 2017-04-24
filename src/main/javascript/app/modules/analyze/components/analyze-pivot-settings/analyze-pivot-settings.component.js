import template from './analyze-pivot-settings.component.html';
import style from './analyze-pivot-settings.component.scss';

export const ANALYZE_PIVOT_SETTINGS_SIDENAV_ID = 'ANALYZE_PIVOT_SETTINGS_SIDENAV_ID';

export const AnalyzePivotSettingsComponent = {
  template,
  styles: [style],
  bindings: {
    onApplyFieldSettings: '&',
    fieldChooserOptions: '<'
  },
  controller: class AnalyzePivotSettingsController {
    constructor(AnalyzeService, FilterService, $mdSidenav, $timeout) {
      'ngInject';

      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;
      this._$mdSidenav = $mdSidenav;
      this._$timeout = $timeout;
      this.ANALYZE_PIVOT_SETTINGS_SIDENAV_ID = ANALYZE_PIVOT_SETTINGS_SIDENAV_ID;
      this.summaryTypes = ['sum', 'min', 'max', 'avg'];
    }

    $onInit() {
      this._$timeout(() => {
        this._$mdSidenav(ANALYZE_PIVOT_SETTINGS_SIDENAV_ID).onClose(() => {
          this.onApplyFieldSettings();
        });
      });
    }

    inputChanged(field) {
      this.onChange({field});
    }

    applySettings() {
      this._$mdSidenav(ANALYZE_PIVOT_SETTINGS_SIDENAV_ID).close();
    }
  }
};
