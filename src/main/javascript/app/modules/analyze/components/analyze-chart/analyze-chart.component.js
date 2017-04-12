import {findIndex, forEach, get, isEmpty, map, values, clone, filter} from 'lodash';

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
      this._AnalyzeService.getArtifacts().then(artifacts => {
        if (isEmpty(this.mode) || isEmpty(this.model.chart)) {
          this.fillSettings(artifacts);
          return;
        }

        this.fillSettings(artifacts, this.model.artifacts);

        const chart = this.model.chart;
        this.labels.tempX = this.labels.x = get(chart, 'xAxis.title', null);
        this.labels.tempY = this.labels.y = get(chart, 'yAxis.title', null);
        this.filters.selected = map(
          chart.filters,
          beFilter => this._FilterService.getBackEnd2FrontEndFilterMapper()(beFilter)
        );
        this.onSettingsChanged(this.settings);
      });
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

    mergeArtifactsWithSettings(artifacts, record) {
      const id = findIndex(artifacts, a => {
        return a.column_name === record.column_name &&
        a.tableName === (record.tableName || record.table_name);
      });

      record.checked = true;
      artifacts.splice(id, 1, record);
      return artifacts;
    }

    updateCustomLabels() {
      this.labels.x = this.labels.tempX;
      this.labels.y = this.labels.tempY;
      this.reloadChart(this.settings, this.filteredGridData);
    }

    getDataByQuery() {
      return this._AnalyzeService.getDataByQuery()
        .then(data => {
          this.gridData = data;
          this.filteredGridData = data;
        });
    }

    fillSettings(artifacts, settings = []) {
      /* Flatten the artifacts into a single array */
      const attributes = artifacts.reduce((res, metric) => {
        return res.concat(map(metric.artifact_attributes, attr => {
          attr.tableName = metric.artifact_name;
          return attr;
        }));
      }, []);

      /* Based on data type, divide the artifacts between axes. */
      const yaxis = filter(attributes, attr => attr.type === 'int' || attr.type === 'Int');
      const xaxis = filter(attributes, attr => attr.type === 'string' || attr.type === 'String');
      const groupBy = map(xaxis, clone);

      forEach(settings, selection => {
        if (selection['x-axis'] === true) {
          this.mergeArtifactsWithSettings(xaxis, selection);
        } else if (selection['y-axis'] === true) {
          this.mergeArtifactsWithSettings(yaxis, selection);
        } else if (selection['z-axis'] === true) {
          this.mergeArtifactsWithSettings(groupBy, selection);
        }
      });

      this.settings = {
        yaxis,
        xaxis,
        groupBy
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

    getSelectedSettingsFor(axis, artifacts) {
      const id = findIndex(artifacts, a => a.checked === true);
      if (id >= 0) {
        artifacts[id][axis] = true;
      }
      return artifacts[id];
    }

    generatePayload(source) {
      const result = clone(source);
      result.chart = {
        filters: map(
          this.filters.selected,
          feFilter => this._FilterService.getFrontEnd2BackEndFilterMapper()(feFilter)
        ),
        xAxis: {title: this.labels.x},
        yAxis: {title: this.labels.y},
        legend: {
          align: this.legend.align,
          layout: this.legend.layout
        }
      };

      result.artifacts = filter([
        this.getSelectedSettingsFor('x-axis', this.settings.xaxis),
        this.getSelectedSettingsFor('y-axis', this.settings.yaxis),
        this.getSelectedSettingsFor('z-axis', this.settings.groupBy)
      ], x => x);

      return result;
    }

    openSaveModal(ev) {
      const payload = this.generatePayload(this.model);

      const tpl = '<analyze-report-save model="model" on-save="onSave($data)"></analyze-report-save>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.model = payload;

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
