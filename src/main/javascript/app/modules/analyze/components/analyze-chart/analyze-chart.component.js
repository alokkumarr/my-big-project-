import fpFilter from 'lodash/fp/filter';
import fpFlatMap from 'lodash/fp/flatMap';
import fpPipe from 'lodash/fp/pipe';
import fpGet from 'lodash/fp/get';
import fpMap from 'lodash/fp/map';
import first from 'lodash/first';
import map from 'lodash/map';
import forEach from 'lodash/forEach';
import clone from 'lodash/clone';
import isEmpty from 'lodash/isEmpty';
import filter from 'lodash/filter';

import template from './analyze-chart.component.html';
import style from './analyze-chart.component.scss';
import {BehaviorSubject} from 'rxjs';

export const AnalyzeChartComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    mode: '@?'
  },
  controller: class AnalyzeChartController {
    constructor($componentHandler, $mdDialog, $scope, $timeout, AnalyzeService, FilterService, $window, $mdSidenav) {
      'ngInject';

      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;
      this._$mdSidenav = $mdSidenav;
      this._$mdDialog = $mdDialog;

      this.updateChart = new BehaviorSubject({});
      this.settings = null;
      this.gridData = this.filteredGridData = [];
      this.filters = {
        // array of strings with the columns displayName that the filter is based on
        selected: [],
        // possible filters shown in the sidenav, generated from the checked columns
        // of the jsPlumb canvas.model
        possible: []
      };

      this.getDataByQuery();

      this.barChartOptions = {
        chart: {
          type: 'column',
          spacingLeft: 45,
          spacingBottom: 45,
          width: 700
        },
        legend: {
          align: 'right',
          layout: 'vertical'
        },
        series: [{
          name: 'Data',
          data: [100, 25, 45, 100, 22]
        }],
        xAxis: {
          categories: ['A', 'B', 'C', 'D', 'E'],
          title: {
            text: 'Customer',
            y: 25
          }
        },
        yAxis: {
          title: {
            text: 'Revenue (millions)',
            x: -25
          }
        }
      };

      $window.chartctrl = this;
    }

    toggleLeft() {
      this._$mdSidenav('left').toggle();
    }

    $onInit() {
      this._FilterService.onApplyFilters(filters => this.onApplyFilters(filters));
      // this._FilterService.onClearAllFilters(() => this.onClearAllFilters());

      if (this.mode === 'fork') {
        this.model.id = null;
      }

      this.getArtifacts();
    }

    getArtifacts() {
      this._AnalyzeService.getArtifacts()
        .then(data => {
          this.fillSettings(data);
        });
    }

    getDataByQuery() {
      this._AnalyzeService.getDataByQuery()
        .then(data => {
          this.gridData = data;
          this.filteredGridData = data;
        });
    }

    fillSettings(data) {
      const attributes = data.reduce((res, metric) => res.concat(metric.artifact_attributes), []);
      this.settings = {
        type: 'bar',
        yaxis: attributes,
        xaxis: attributes
      };
    }

    onSettingsChanged(settings) {
      this.filters.possible = this.generateFilters(settings.yaxis.filter(x => x.checked));
    }

    onApplyFilters(filters) {
      this.filters.possible = filters;
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(filters);

      this.filterGridData();
    }

    filterGridData() {
      this.filteredGridData = this._FilterService.getGridDataFilter(this.filters.selected)(this.gridData);

      console.log(this.gridData.length, this.filterGridData.length);
      // reload chart here
    }

    generateFilters(selectedFields) {
      return this._FilterService.getChartSetttingsToFiltersMapper(this.gridData)(selectedFields);
    }

    settingsToChart(settings) {
    }

    // filters section
    openFilterSidenav() {
      this._FilterService.openFilterSidenav(this.filters.possible);
    }

    openDescriptionModal(ev) {
      const tpl = '<analyze-report-description model="model" on-save="onSave($data)"></analyze-report-description>';

      this._$mdDialog.show({
        template: tpl,
        controller: scope => {
          scope.model = {
            description: this.model.description
          };

          scope.onSave = data => {
            this.model.description = data.description;
          };
        },
        autoWrap: false,
        focusOnOpen: false,
        multiple: true,
        targetEvent: ev,
        clickOutsideToClose: true
      });
    }

    openPreviewModal(ev) {
      const tpl = '<analyze-chart-preview model="model"></analyze-chart-preview>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.model = this.barChartOptions;
          },
          targetEvent: ev,
          fullscreen: true,
          autoWrap: false,
          multiple: true
        });
    }
  }
};
