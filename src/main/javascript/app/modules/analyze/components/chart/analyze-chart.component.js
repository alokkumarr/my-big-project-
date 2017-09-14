import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import findIndex from 'lodash/findIndex';
import find from 'lodash/find';
import forEach from 'lodash/forEach';
import filter from 'lodash/filter';
import get from 'lodash/get';
import isEmpty from 'lodash/isEmpty';
import assign from 'lodash/assign';
import map from 'lodash/map';
import values from 'lodash/values';
import clone from 'lodash/clone';
import set from 'lodash/set';
import orderBy from 'lodash/orderBy';
import concat from 'lodash/concat';
import remove from 'lodash/remove';

import template from './analyze-chart.component.html';
import style from './analyze-chart.component.scss';
import AbstractDesignerComponentController from '../analyze-abstract-designer-component';
import {DEFAULT_BOOLEAN_CRITERIA} from '../../services/filter.service';
import {ENTRY_MODES, NUMBER_TYPES} from '../../consts';

const BAR_COLUMN_OPTIONS = [{
  label: 'TOOLTIP_BAR_CHART',
  type: 'bar',
  icon: {font: 'icon-hor-bar-chart'}
}, {
  label: 'TOOLTIP_COLUMN_CHART',
  type: 'column',
  icon: {font: 'icon-vert-bar-chart'}
}];

export const AnalyzeChartComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    mode: '@?'
  },
  controller: class AnalyzeChartController extends AbstractDesignerComponentController {
    constructor($componentHandler, $timeout, AnalyzeService, SortService,
                ChartService, FilterService, $mdSidenav, $translate, toastMessage, $injector) {
      'ngInject';
      super($injector);
      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;
      this._ChartService = ChartService;
      this._SortService = SortService;
      this._$mdSidenav = $mdSidenav;
      this._$timeout = $timeout;
      this._$translate = $translate;
      this._toastMessage = toastMessage;
      this.BAR_COLUMN_OPTIONS = BAR_COLUMN_OPTIONS;
      this.sortFields = [];
      this.sorts = [];

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
      this.labels = {
        tempY: '', tempX: '', y: '', x: ''
      };

      this.chartOptions = this._ChartService.getChartConfigFor(this.model.chartType, {legend: this.legend});

      this.barColumnChoice = '';
      this.chartViewOptions = ChartService.getViewOptionsFor(this.model.chartType);
    }

    toggleLeft() {
      this._$mdSidenav('left').toggle();
    }

    $onInit() {
      const chartType = this.model.chartType;
      // used only for bar or column type charts
      this.barColumnChoice = ['bar', 'column'].includes(chartType) ? chartType : '';

      if (this.mode === ENTRY_MODES.FORK) {
        delete this.model.id;
      }

      if (this.mode === ENTRY_MODES.EDIT) {
        this.initChart();
        this.onRefreshData();
      } else {
        this._AnalyzeService.createAnalysis(this.model.semanticId, 'chart').then(analysis => {
          this.model = assign(analysis, this.model);
          set(this.model, 'sqlBuilder.booleanCriteria', DEFAULT_BOOLEAN_CRITERIA.value);
          this.initChart();
        });
      }
    }

    initChart() {
      this._ChartService.updateAnalysisModel(this.model);
      this.settings = this._ChartService.fillSettings(this.model.artifacts, this.model);
      this.sortFields = this._SortService.getArtifactColumns2SortFieldMapper()(this.model.artifacts[0].columns);
      this.sorts = this.model.sqlBuilder.sorts ?
        this._SortService.mapBackend2FrontendSort(this.model.sqlBuilder.sorts, this.sortFields) : [];
      this.reloadChart(this.settings, this.filteredGridData);

      if (isEmpty(this.mode)) {
        return;
      }

      this.labels.tempX = this.labels.x = get(this.model, 'xAxis.title', null);
      this.labels.tempY = this.labels.y = get(this.model, 'yAxis.title', null);
      this.filters = map(
        get(this.model, 'sqlBuilder.filters', []),
        this._FilterService.backend2FrontendFilter(this.model.artifacts)
      );
      this.onSettingsChanged();
      this._$timeout(() => {
        this.updateLegendPosition();
        this.endDraftMode();
      });
    }

    toggleBarColumn() {
      if (this.model.chartType === 'bar') {
        this.model.chartType = 'column';
      } else if (this.model.chartType === 'column') {
        this.model.chartType = 'bar';
      }
      this.updateChart.next([{
        path: 'chart.type',
        data: this.model.chartType
      }]);
    }

    updateLegendPosition() {
      const align = this._ChartService.LEGEND_POSITIONING[this.legend.align];
      const layout = this._ChartService.LAYOUT_POSITIONS[this.legend.layout];

      this.startDraftMode();
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

    updateLabelOptions() {
      this.startDraftMode();
      this.reloadChart(this.settings, this.filteredGridData);
    }

    updateCustomLabels() {
      this.labels.x = this.labels.tempX;
      this.labels.y = this.labels.tempY;
      this.startDraftMode();
      this.onRefreshData();
    }

    onSettingsChanged() {
      this.sortFields = this._SortService.getArtifactColumns2SortFieldMapper()(this.model.artifacts[0].columns);
      this.analysisUnSynched();
      this.startDraftMode();
    }

    onRefreshData() {
      if (!this.checkModelValidity()) {
        return null;
      }
      this.startProgress();
      const payload = this.generatePayload(this.model);
      return this._AnalyzeService.getDataBySettings(payload).then(({data}) => {
        const parsedData = this._ChartService.parseData(data, payload.sqlBuilder);
        this.gridData = this.filteredGridData = parsedData || this.filteredGridData;
        this.analysisSynched();
        this.endProgress();
        this.reloadChart(this.settings, this.filteredGridData);
      }, () => {
        this.endProgress();
      });
    }

    /** check the parameters, before sending the request for the cahrt data */
    checkModelValidity() {
      let isValid = true;
      const errors = [];
      const present = {
        x: find(this.settings.xaxis, x => x.checked === 'x'),
        y: find(this.settings.yaxis, y => y.checked === 'y'),
        z: find(this.settings.zaxis, z => z.checked === 'z'),
        g: find(this.settings.groupBy, g => g.checked === 'g')
      };

      forEach(this.chartViewOptions.required, (v, k) => {
        if (v && !present[k]) {
          errors.push(`'${this.chartViewOptions.axisLabels[k]}'`);
        }
      });

      if (errors.length) {
        isValid = false;
        this._toastMessage.error(`${errors.join(', ')} required`, '', {
          timeOut: 3000
        });
      }

      return isValid;
    }

    reloadChart(settings, filteredGridData) {
      if (isEmpty(filteredGridData)) {
        return;
      }
      if (!isEmpty(this.sorts)) {
        filteredGridData = orderBy(
          filteredGridData,
          map(this.sorts, 'field.dataField'),
          map(this.sorts, 'order')
        );
      }
      const changes = this._ChartService.dataToChangeConfig(
        this.model.chartType,
        settings,
        filteredGridData,
        {labels: this.labels, labelOptions: this.model.labelOptions}
      );

      this.updateChart.next(changes);
    }

    openChartPreviewModal(ev) {
      const tpl = '<analyze-chart-preview model="model"></analyze-chart-preview>';
      this.openPreviewModal(tpl, ev, {
        chartOptions: this.chartOptions,
        settings: this.settings,
        chart: this.generatePayload(this.model),
        legend: this.legend,
        labels: this.labels
      });
    }

    getSelectedSettingsFor(axis, artifacts) {
      const id = findIndex(artifacts, a => a.checked === true);
      if (id >= 0) {
        artifacts[id][axis] = true;
      }
      return artifacts[id];
    }

    isStringField(field) {
      return field && !NUMBER_TYPES.includes(field.type);
    }

    isDataField(field) {
      return field && NUMBER_TYPES.includes(field.type);
    }

    generatePayload(source) {
      const payload = clone(source);

      set(payload, 'sqlBuilder.filters', map(
        this.filters,
        this._FilterService.frontend2BackendFilter()
      ));

      const g = find(this.settings.groupBy, g => g.checked === 'g');
      const x = find(this.settings.xaxis, x => x.checked === 'x');
      const y = filter(this.settings.yaxis, y => y.checked === 'y');
      const z = find(this.settings.zaxis, z => z.checked === 'z');

      const allFields = [g, x, ...y, z];

      let nodeFields = filter(allFields, this.isStringField);
      const dataFields = filter(allFields, this.isDataField);

      if (payload.chartType === 'scatter') {
        const xFields = remove(dataFields, ({checked}) => checked === 'x');
        nodeFields = concat(xFields, nodeFields);
      }

      forEach(dataFields, field => {
        if (!field.aggregate) {
          field.aggregate = 'sum';
        }
      });

      set(payload, 'sqlBuilder.dataFields', dataFields);
      set(payload, 'sqlBuilder.nodeFields', nodeFields);

      delete payload.supports;
      set(payload, 'sqlBuilder.sorts', this._SortService.mapFrontend2BackendSort(this.sorts));
      set(payload, 'sqlBuilder.booleanCriteria', this.model.sqlBuilder.booleanCriteria);
      set(payload, 'xAxis', {title: this.labels.x});
      set(payload, 'yAxis', {title: this.labels.y});
      set(payload, 'legend', {
        align: this.legend.align,
        layout: this.legend.layout
      });

      return payload;
    }

    openChartSortModal(ev) {
      this.openSortModal(ev, {
        fields: this.sortFields,
        sorts: map(this.sorts, sort => clone(sort))
      }).then(sorts => {
        this.sorts = sorts;
        this.startDraftMode();
        this.onRefreshData();
      });
    }

    openSaveChartModal(ev) {
      const payload = this.generatePayload(this.model);
      this.openSaveModal(ev, payload);
    }
  }

};
