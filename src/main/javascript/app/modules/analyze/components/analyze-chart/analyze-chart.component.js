import {BehaviorSubject} from 'rxjs';
import {NUMBER_TYPES} from '../../services/filter.service';
import {
  assign,
  findIndex,
  find,
  get,
  set,
  isEmpty,
  map,
  values,
  clone,
  filter
} from 'lodash';

import template from './analyze-chart.component.html';
import style from './analyze-chart.component.scss';
import {ANALYZE_FILTER_SIDENAV_IDS} from '../analyze-filter/analyze-filter-sidenav.component';

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

      this.chartOptions = this._ChartService.getChartConfigFor(this.model.chartType, {legend: this.legend});
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

      this._AnalyzeService.createAnalysis(this.model.artifactsId, 'chart').then(analysis => {
        this.model = assign(this.model, analysis);
        this.initChart();
      });
    }

    $onDestroy() {
      this._FilterService.offApplyFilters();
      this._FilterService.offClearAllFilters();
    }

    initChart() {
      this.fillSettings(this.model.artifacts, this.model);

      if (isEmpty(this.mode) || isEmpty(this.model.chart)) {
        return;
      }

      const chart = this.model.chart;
      this.labels.tempX = this.labels.x = get(chart, 'xAxis.title', null);
      this.labels.tempY = this.labels.y = get(chart, 'yAxis.title', null);
      this.filters.selected = map(
        get(this.model, 'sqlBuilder.filters', []),
        beFilter => this._FilterService.getBackEnd2FrontEndFilterMapper()(beFilter)
      );
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

    mergeArtifactsWithSettings(artifacts, record) {
      const id = findIndex(artifacts, a => {
        return a.columnName === record.columnName &&
        a.tableName === record.tableName;
      });

      if (id >= 0) {
        record.checked = true;
        artifacts.splice(id, 1, record);
      }

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

    fillSettings(artifacts, model) {
      /* Flatten the artifacts into a single array */
      const attributes = artifacts.reduce((res, metric) => {
        return res.concat(map(metric.columns, attr => {
          attr.tableName = metric.artifactName;
          return attr;
        }));
      }, []);

      /* Based on data type, divide the artifacts between axes. */
      const yaxis = filter(attributes, attr => (
        attr.columnName &&
        NUMBER_TYPES.indexOf(attr.type) >= 0
      ));
      const xaxis = filter(attributes, attr => (
        attr.columnName &&
        (attr.type === 'string' || attr.type === 'String')
      ));
      const groupBy = map(xaxis, clone);

      this.mergeArtifactsWithSettings(xaxis, get(model, 'sqlBuilder.groupBy', {}));
      this.mergeArtifactsWithSettings(yaxis, get(model, 'dataColumns.[0]', {}));
      this.mergeArtifactsWithSettings(groupBy, get(model, 'sqlBuilder.splitBy', {}));

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

      this.filterGridData().then(() => {
        this.reloadChart(this.settings, this.filteredGridData);
      });
    }

    onFilterRemoved(filter) {
      filter.model = null;
      this.filters.selected = this._FilterService.getSelectedFilterMapper()(this.filters.possible);
      this.filterGridData();
      this.reloadChart(this.settings, this.filteredGridData);
    }

    filterGridData() {
      const payload = this.generatePayload(this.model);
      return this._AnalyzeService.getDataBySettings(payload).then(data => {
        this.gridData = this.filteredGridData = data;
      });
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
      this._FilterService.openFilterSidenav(this.filters.possible, ANALYZE_FILTER_SIDENAV_IDS.designer);
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
            scope.model = this.chartOptions;
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

      set(result, 'sqlBuilder.filters', map(
        this.filters.selected,
        feFilter => this._FilterService.getFrontEnd2BackEndFilterMapper()(feFilter)
      ));

      set(result, 'sqlBuilder.groupBy', find(this.settings.xaxis, x => x.checked));
      set(result, 'sqlBuilder.splitBy', find(this.settings.groupBy, x => x.checked));
      set(result, 'dataColumns', [find(this.settings.yaxis, x => x.checked)]);

      set(result, 'chart', {
        xAxis: {title: this.labels.x},
        yAxis: {title: this.labels.y},
        legend: {
          align: this.legend.align,
          layout: this.legend.layout
        }
      });

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
        }).then(successfullySaved => {
          if (successfullySaved) {
            this.onAnalysisSaved(successfullySaved);
          }
        });
    }

    onAnalysisSaved(successfullySaved) {
      this.$dialog.hide(successfullySaved);
    }
  }

};
