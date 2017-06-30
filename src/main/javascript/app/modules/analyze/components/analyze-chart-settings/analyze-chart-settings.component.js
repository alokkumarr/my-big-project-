import find from 'lodash/find';
import forEach from 'lodash/forEach';
import template from './analyze-chart-settings.component.html';
import style from './analyze-chart-settings.component.scss';

export const AnalyzeChartSettingsComponent = {
  template,
  styles: [style],
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
      this.groupRemovalAction = {
        icon: 'icon-close',
        isVisible: () => this.selected.g,
        callback: () => {
          this.selected.g = null;
          forEach(this.settings.groupBy, attr => {
            attr.checked = false;
          });
        }
      };
    }

    markSelected() {
      this.selected.y = find(this.settings.yaxis, attr => attr.checked === 'y');
      this.selected.x = find(this.settings.xaxis, attr => attr.checked === 'x');
      this.selected.g = find(this.settings.groupBy, attr => attr.checked === 'g');
      this.selected.z = find(this.settings.zaxis, attr => attr.checked === 'z');
    }

    inputChanged(axisOptions, selectedAttr, marker) {

      forEach(axisOptions, attr => {
        if (selectedAttr === attr) {
          attr.checked = marker;
        } else if (attr.checked === marker) {
          attr.checked = false;
        }
      });

      this.onChange({settings: this.settings});
    }
  }
};
