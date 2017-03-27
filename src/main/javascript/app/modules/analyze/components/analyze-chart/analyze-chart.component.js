import {isEmpty, map, keys, clone, reduce, filter, uniq} from 'lodash';

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
    constructor($componentHandler, $mdDialog, $scope, $timeout, AnalyzeService, FilterService, $mdSidenav) {
      'ngInject';

      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;
      this._$mdSidenav = $mdSidenav;
      this._$mdDialog = $mdDialog;

      this.updateChart = new BehaviorSubject({});
      this.settings = null;
      this.gridData = this.filteredGridData = [];
      this.labels = {
        tempY: '', tempX: '', y: '', x: ''
      };

      this.filters = {
        // array of strings with the columns displayName that the filter is based on
        selected: [],
        // possible filters shown in the sidenav, generated from the checked columns
        // of the jsPlumb canvas.model
        possible: []
      };

      this.barChartOptions = this.getDefaultChartConfig();
    }

    getDefaultChartConfig() {
      return {
        chart: {
          type: this.model.chartType || 'column',
          spacingLeft: 45,
          spacingBottom: 45,
          spacingTop: 45,
          width: 650
        },
        legend: {
          align: 'right'
        },
        series: [{
          name: 'Series 1',
          data: [0, 0, 0, 0, 0]
        }],
        xAxis: {
          categories: ['A', 'B', 'C', 'D', 'E'],
          title: {
            y: 25
          }
        },
        yAxis: {
          title: {
            x: -25
          }
        }
      };
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

      this.getDataByQuery().then(() => {
        this.initChart();
      });
    }

    $onDestroy() {
      this._FilterService.offApplyFilters();
      this._FilterService.offClearAllFilters();
    }

    initChart() {
      if (isEmpty(this.mode) || isEmpty(this.model.chart)) {
        this.getArtifacts();
        return;
      }

      const chart = this.model.chart;
      this.labels.tempX = this.labels.x = chart.xAxis.label;
      this.labels.tempY = this.labels.y = chart.yAxis.label;
      this.settings = {
        yaxis: chart.yAxis.artifacts,
        xaxis: chart.xAxis.artifacts,
        groupBy: chart.groupBy.artifacts
      };
      this.filters.selected = chart.filters || [];
      this.onSettingsChanged(this.settings);
    }

    updateCustomLabels() {
      this.labels.x = this.labels.tempX;
      this.labels.y = this.labels.tempY;
      this.reloadChart(this.settings, this.filteredGridData);
    }

    getArtifacts() {
      this._AnalyzeService.getArtifacts()
        .then(data => {
          this.fillSettings(data);
        });
    }

    getDataByQuery() {
      return this._AnalyzeService.getDataByQuery()
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
        yaxis: filter(attributes, attr => attr['y-axis']),
        xaxis: filter(attributes, attr => attr['x-axis']),
        groupBy: map(filter(attributes, attr => attr['x-axis']), clone)
      };
    }

    onSettingsChanged(settings) {
      // On changes to x or y axis parameters, run the new parameters
      // through filters.
      const attributes = settings.yaxis.concat(settings.xaxis).concat(settings.groupBy);
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
      const group = filter(this.settings.groupBy, attr => attr.checked)[0] || {};

      const {xCategories, ySeries} = this.gridToChart(xaxis, yaxis, group, filteredGridData);

      this.updateChart.next([
        {
          path: 'xAxis.title.text',
          data: this.labels.x || xaxis.display_name
        },
        {
          path: 'xAxis.categories',
          data: xCategories
        },
        {
          path: 'yAxis.title.text',
          data: this.labels.y || yaxis.display_name
        },
        {
          path: 'series',
          data: ySeries
        }
      ]);
    }

    gridToChart(x, y, g, grid) {
      const defaultSeriesName = 'Series 1';
      const categories = uniq(grid.map(row => row[x.column_name]));

      const defaultSeries = () => reduce(categories, (obj, c) => {
        obj[c] = 0;
        return obj;
      }, {});

      const res = reduce(grid, (obj, row) => {
        const category = row[x.column_name];
        const series = row[g.column_name] || defaultSeriesName;
        obj[series] = obj[series] || defaultSeries();
        obj[series][category] += row[y.column_name];
        return obj;
      }, {});

      const xCategories = keys(defaultSeries());
      return {
        xCategories,
        ySeries: keys(res).map(k => ({
          name: k,
          data: xCategories.map(c => res[k][c])
        }))
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

    generatePayload() {
      const result = {
        filters: this.filters.selected,
        xAxis: {
          label: this.labels.x,
          artifacts: this.settings.xaxis
        },
        yAxis: {
          label: this.labels.y,
          artifacts: this.settings.yaxis
        },
        groupBy: {
          artifacts: this.settings.groupBy
        }
      };
      return result;
    }

    openSaveModal(ev) {
      this.model.chart = this.generatePayload();

      const tpl = '<analyze-report-save model="model" on-save="onSave($data)"></analyze-report-save>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.model = clone(this.model);

            scope.onSave = data => {
              this.model.id = data.id;
              this.model.name = data.name;
              this.model.description = data.description;
              this.model.category = data.category;
            };
          },
          autoWrap: false,
          fullscreen: true,
          focusOnOpen: false,
          multiple: true,
          targetEvent: ev,
          clickOutsideToClose: true
        });
    }
  }

};
