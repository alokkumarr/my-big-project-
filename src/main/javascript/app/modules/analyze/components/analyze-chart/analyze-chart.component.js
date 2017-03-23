import {map, keys, reduce, filter, uniq} from 'lodash';

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
      this._FilterService.onClearAllFilters(() => this.clearFilters());

      if (this.mode === 'fork') {
        this.model.id = null;
      }

      this.getArtifacts();
    }

    $onDestroy() {
      this._FilterService.offApplyFilters();
      this._FilterService.offClearAllFilters();
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
      const attributes = data.reduce((res, metric) => {
        return res.concat(map(metric.artifact_attributes, attr => {
          attr.tableName = metric.artifact_name;
          return attr;
        }));
      }, []);
      this.settings = {
        type: 'bar',
        yaxis: filter(attributes, attr => attr['y-axis']),
        xaxis: filter(attributes, attr => attr['x-axis'])
      };
    }

    onSettingsChanged(settings) {
      // On changes to x or y axis parameters, run the new parameters
      // through filters.
      const attributes = settings.yaxis.concat(settings.xaxis);
      this.filters.possible = this.generateFilters(filter(attributes, x => x.checked));
      this._FilterService.mergeCanvasFiltersWithPossibleFilters(this.filters.selected, this.filters.possible);
      this.onApplyFilters(this.filters.possible);
    }

    clearFilters() {
      this.onApplyFilters(
        this._FilterService.getFilterClearer()(this.filters.possible)
      );
    }

    onApplyFilters(filters) {
      this.filters.possible = filters;
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(filters);

      this.filterGridData();
      this.reloadChart(this.settings, this.filteredGridData);
    }

    filterGridData() {
      this.filteredGridData = this._FilterService.getGridDataFilter(this.filters.selected)(this.gridData);
    }

    generateFilters(selectedFields) {
      return this._FilterService.getChartSetttingsToFiltersMapper(this.gridData)(selectedFields);
    }

    reloadChart(settings, filteredGridData) {
      const xaxis = filter(this.settings.xaxis, attr => attr.checked)[0] || {};
      const yaxis = filter(this.settings.yaxis, attr => attr.checked)[0] || {};

      const {xCategories, ySeries} = this.gridToChart(xaxis, yaxis, filteredGridData);

      this.updateChart.next([
        {
          path: 'xAxis.title.text',
          data: xaxis.display_name
        },
        {
          path: 'xAxis.categories',
          data: xCategories
        },
        {
          path: 'yAxis.title.text',
          data: yaxis.display_name
        },
        {
          path: 'series',
          data: map(ySeries, s => ({name: xaxis.display_name, data: s}))
        }
      ]);
    }

    gridToChart(x, y, grid) {
      const res = reduce(grid, (obj, row) => {
        const category = row[x.column_name];
        obj[category] = obj[category] || 0;
        obj[category] += row[y.column_name];
        return obj;
      }, {});

      const xCategories = keys(res);
      return {
        xCategories,
        ySeries: [xCategories.map(c => res[c])]
      };
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
