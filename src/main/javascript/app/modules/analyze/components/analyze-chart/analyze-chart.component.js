import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import findIndex from 'lodash/findIndex';
import forEach from 'lodash/forEach';
import find from 'lodash/find';
import get from 'lodash/get';
import sortBy from 'lodash/sortBy';
import flatMap from 'lodash/flatMap';
import isEmpty from 'lodash/isEmpty';
import assign from 'lodash/assign';
import map from 'lodash/map';
import values from 'lodash/values';
import clone from 'lodash/clone';
import filter from 'lodash/filter';
import set from 'lodash/set';
import cloneDeep from 'lodash/cloneDeep';
import {DEFAULT_BOOLEAN_CRITERIA} from '../../services/filter.service';

import {ENTRY_MODES, NUMBER_TYPES} from '../../consts';

import template from './analyze-chart.component.html';
import style from './analyze-chart.component.scss';

export const AnalyzeChartComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    mode: '@?'
  },
  controller: class AnalyzeChartController {
    constructor($componentHandler, $mdDialog, $scope, $timeout, AnalyzeService, ChartService, FilterService, $mdSidenav) {
      'ngInject';

      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;
      this._ChartService = ChartService;
      this._$mdSidenav = $mdSidenav;
      this._$mdDialog = $mdDialog;
      this._$timeout = $timeout;

      this.legend = {
        align: get(this.model, 'legend.align', 'right'),
        layout: get(this.model, 'legend.layout', 'vertical'),
        options: {
          align: values(this._ChartService.LEGEND_POSITIONING),
          layout: values(this._ChartService.LAYOUT_POSITIONS)
        }
      };

      this.updateChart = new BehaviorSubject({});
      this.settings = null;
      this.gridData = this.filteredGridData = [];
      this.showProgress = false;
      this.analysisChanged = false;
      this.labels = {
        tempY: '', tempX: '', y: '', x: ''
      };

      this.filters = [];

      this.chartOptions = this._ChartService.getChartConfigFor(this.model.chartType, {legend: this.legend});
    }

    toggleLeft() {
      this._$mdSidenav('left').toggle();
    }

    $onInit() {
      if (this.mode === ENTRY_MODES.FORK) {
        delete this.model.id;
      }

      if (this.mode === ENTRY_MODES.EDIT) {
        this.initChart();
        this.refreshChartData();
      } else {
        this._AnalyzeService.createAnalysis(this.model.semanticId, 'chart').then(analysis => {
          this.model = assign(analysis, this.model);
          set(this.model, 'sqlBuilder.booleanCriteria', DEFAULT_BOOLEAN_CRITERIA.value);
          this.initChart();
        });
      }
    }

    initChart() {
      this.fillSettings(this.model.artifacts, this.model);

      if (isEmpty(this.mode)) {
        return;
      }

      this.labels.tempX = this.labels.x = get(this.model, 'xAxis.title', null);
      this.labels.tempY = this.labels.y = get(this.model, 'yAxis.title', null);
      this.filters = map(
        get(this.model, 'sqlBuilder.filters', []),
        this._FilterService.backend2FrontendFilter()
      );
      this.onSettingsChanged();
      this._$timeout(() => {
        this.updateLegendPosition();
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
      forEach(artifacts, a => {
        a.checked = a.columnName === record.columnName &&
          a.tableName === record.tableName;
      });

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
      let attributes = flatMap(artifacts, metric => {
        return map(metric.columns, attr => {
          attr.tableName = metric.artifactName;
          return attr;
        });
      });

      attributes = sortBy(attributes, [attr => attr.columnName]);

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
      this.mergeArtifactsWithSettings(yaxis, get(model, 'sqlBuilder.dataFields.[0]', {}));
      this.mergeArtifactsWithSettings(groupBy, get(model, 'sqlBuilder.splitBy', {}));

      this.settings = {
        yaxis,
        xaxis,
        groupBy
      };
      this.reloadChart(this.settings, this.filteredGridData);
    }

    onSettingsChanged() {
      this.analysisChanged = true;
    }

    clearFilters() {
      this.filters = [];
      this.analysisChanged = true;
    }

    onFilterRemoved(index) {
      this.filters.splice(index, 1);
      this.analysisChanged = true;
    }

    onApplyFilters({filters, filterBooleanCriteria} = {}) {
      if (filters) {
        this.filters = filters;
        this.analysisChanged = true;
      }
      if (filterBooleanCriteria) {
        this.model.sqlBuilder.booleanCriteria = filterBooleanCriteria;
      }
    }

    refreshChartData() {
      this.showProgress = true;
      const payload = this.generatePayload(this.model);
      return this._AnalyzeService.getDataBySettings(payload).then(({data}) => {
        this.gridData = this.filteredGridData = data || this.filteredGridData;
        this.analysisChanged = false;
        this.showProgress = false;
        this.reloadChart(this.settings, this.filteredGridData);
      }, () => {
        this.showProgress = false;
      });
    }

    openFiltersModal(ev) {
      const tpl = '<analyze-filter-modal filters="filters" artifacts="artifacts" filter-boolean-criteria="booleanCriteria"></analyze-filter-modal>';
      this._$mdDialog.show({
        template: tpl,
        controller: scope => {
          scope.filters = cloneDeep(this.filters);
          scope.artifacts = this.model.artifacts;
          scope.booleanCriteria = this.model.sqlBuilder.booleanCriteria;
        },
        targetEvent: ev,
        fullscreen: true,
        autoWrap: false,
        multiple: true
      }).then(this.onApplyFilters.bind(this));
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
        this.filters,
        this._FilterService.frontend2BackendFilter()
      ));

      const y = find(this.settings.yaxis, x => x.checked);

      delete result.supports;
      set(result, 'sqlBuilder.sorts', []);
      set(result, 'sqlBuilder.groupBy', find(this.settings.xaxis, x => x.checked));
      set(result, 'sqlBuilder.splitBy', find(this.settings.groupBy, x => x.checked));
      set(result, 'sqlBuilder.dataFields', [assign({aggregate: 'sum'}, y)]);
      set(result, 'sqlBuilder.booleanCriteria', this.model.sqlBuilder.booleanCriteria);
      set(result, 'xAxis', {title: this.labels.x});
      set(result, 'yAxis', {title: this.labels.y});
      set(result, 'legend', {
        align: this.legend.align,
        layout: this.legend.layout
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
