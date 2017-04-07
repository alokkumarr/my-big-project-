import {get, isEmpty, map, values, clone, filter} from 'lodash';

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
    constructor($componentHandler, $mdDialog, $scope, $timeout, AnalyzeService, ChartService, FilterService, $mdSidenav, $window) {
      'ngInject';

      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;
      this._ChartService = ChartService;
      this._$mdSidenav = $mdSidenav;
      this._$mdDialog = $mdDialog;

      this.legend = {
        align: get(this.model, 'chart.legend.align', 'right'),
        layout: get(this.model, 'chart.legend.layout', 'vertical'),
        options: {
          align: values(this._ChartService.LEGEND_POSITIONING),
          layout: values(this._ChartService.LAYOUT_POSITIONS)
        }
      };

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

      this.barChartOptions = this._ChartService.getChartConfigFor(this.model.chartType, {legend: this.legend});
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

    updateLegendPosition() {
      const align = this._ChartService.LEGEND_POSITIONING[this.legend.align];
      const layout = this._ChartService.LAYOUT_POSITIONS[this.legend.layout];

      this.updateChart.next([
        {
          path: 'legend.align',
          data: align.align
        },
        {
          path: 'legend.verticalAlign',
          data: align.verticalAlign
        },
        {
          path: 'legend.layout',
          data: layout.layout
        }
      ]);
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
      this.reloadChart(this.settings, this.filteredGridData);
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
      const changes = this._ChartService.dataToChangeConfig(
        this.model.chartType,
        settings,
        filteredGridData,
        {labels: this.labels}
      );

      this.updateChart.next(changes);
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
        },
        legend: {
          align: this.legend.align,
          layout: this.legend.layout
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
