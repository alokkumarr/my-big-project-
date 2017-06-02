import find from 'lodash/find';
import forEach from 'lodash/forEach';
import template from './analyze-chart-settings.component.html';

export const AnalyzeChartSettingsComponent = {
  template,
  bindings: {
    settings: '<?',
    onChange: '&'
  },
  controller: class AnalyzeChartSettingsController {
    constructor(AnalyzeService, FilterService, $mdSidenav, $scope) {
      'ngInject';

      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;
      this._$mdSidenav = $mdSidenav;
      this._$scope = $scope;
      this.selected = {};
    }

    $onInit() {
      this._clearWatcher = this._$scope.$watch(() => this.settings, newVal => {
        if (newVal) {
          this.markSelected();
          this._clearWatcher();
        }
      });
    }

    markSelected() {
      this.selected.y = find(this.settings.yaxis, attr => attr.checked);
      this.selected.x = find(this.settings.xaxis, attr => attr.checked);
      this.selected.z = find(this.settings.groupBy, attr => attr.checked);
    }

    inputChanged(axisOptions, selectedAttr) {

      forEach(axisOptions, attr => {
        attr.checked = selectedAttr === attr;
      });

      this.onChange({settings: this.settings});
    }
  }
};
