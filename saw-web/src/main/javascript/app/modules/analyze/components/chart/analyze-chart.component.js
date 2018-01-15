import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import * as findIndex from 'lodash/findIndex';
import * as find from 'lodash/find';
import * as forEach from 'lodash/forEach';
import * as filter from 'lodash/filter';
import * as get from 'lodash/get';
import * as isEmpty from 'lodash/isEmpty';
import * as assign from 'lodash/assign';
import * as map from 'lodash/map';
import * as clone from 'lodash/clone';
import * as set from 'lodash/set';
import * as orderBy from 'lodash/orderBy';
import * as concat from 'lodash/concat';
import * as remove from 'lodash/remove';
import * as every from 'lodash/every';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';
import * as fpFilter from 'lodash/fp/filter';
import * as fpCompact from 'lodash/fp/compact';

import * as template from './analyze-chart.component.html';
import style from './analyze-chart.component.scss';
import AbstractDesignerComponentController from '../analyze-abstract-designer-component';
import {DEFAULT_BOOLEAN_CRITERIA} from '../../services/filter.service';
import {ENTRY_MODES, NUMBER_TYPES, COMBO_TYPES, COMBO_TYPES_OBJ, TSCOMBO_TYPES, TSCOMBO_TYPES_OBJ, CHART_TYPES_OBJ} from '../../consts';

const INVERTING_OPTIONS = [{
  label: 'TOOLTIP_INVERTED',
  type: true,
  icon: {font: 'icon-hor-bar-chart'}
}, {
  label: 'TOOLTIP_NOT_INVERTED',
  type: false,
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
      this.INVERTING_OPTIONS = INVERTING_OPTIONS;
      this.COMBO_TYPES = COMBO_TYPES;
      this.COMBO_TYPES_OBJ = COMBO_TYPES_OBJ;
      this.TSCOMBO_TYPES = TSCOMBO_TYPES;
      this.TSCOMBO_TYPES_OBJ = TSCOMBO_TYPES_OBJ;
      this.CHART_TYPES_OBJ = CHART_TYPES_OBJ;
      this.sortFields = [];
      this.sorts = [];
      // Initializing DD values for legend based on chart type.
      this.legend = this._ChartService.initLegend(this.model);
      this.chartHgt = {
        height: 500
      };

      this.updateChart = new BehaviorSubject({});
      this.isStockChart = this.model.chartType.substring(0, 2) === 'ts';
      this.settings = null;
      this.gridData = this.filteredGridData = [];
      this.labels = {
        tempY: '', tempX: '', y: '', x: ''
      };

      this.chartOptions = this._ChartService.getChartConfigFor(this.model.chartType, {chart: this.chartHgt, legend: this.legend});

      this.isInverted = false;
      this.chartViewOptions = ChartService.getViewOptionsFor(this.model.chartType);
      this.comboableCharts = ['column', 'bar', 'line', 'area', 'combo'];
      this.comboableTSCharts = ['tsspline', 'tsPane'];
      this.invertableCharts = [...this.comboableCharts, 'stack'];
      this.multyYCharts = [...this.invertableCharts, ...this.comboableTSCharts];

      this.designerStates = {
        noSelection: 'no-selection',
        noData: 'no-data-for-selection',
        hasData: 'selection-has-data'
      };
      this.designerState = this.designerStates.noSelection;
    }

    gotData(data) {
      if (!isEmpty(data)) {
        this.designerState = this.designerStates.noData;
      }
      this.designerState = this.designerStates.hasData;
    }

    toggleLeft() {
      this._$mdSidenav('left').toggle();
    }

    $onInit() {
      if (this.mode === ENTRY_MODES.NEW && this.model.chartType === 'bar') {
        this.isInverted = true;
      }

      if (this.mode === ENTRY_MODES.FORK) {
        this.isInverted = this.model.isInverted;
        delete this.model.id;
      }

      if (this.mode === ENTRY_MODES.EDIT) {
        this.isInverted = this.model.isInverted;
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

    onSelectComboType(attributeColumn, comboType) {
      attributeColumn.comboType = comboType.value;
    }

    onSelectTSComboType(attributeColumn, comboType) {
      attributeColumn.comboType = comboType.label;
    }

    toggleChartInversion() {
      this.updateChart.next([{
        path: 'chart.inverted',
        data: this.isInverted
      }]);
    }

    updateLegendPosition() {
      const align = this._ChartService.LEGEND_POSITIONING[this.legend.align];
      const layout = this._ChartService.LAYOUT_POSITIONS[this.legend.layout];

      if (!align || !layout) {
        return;
      }

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
      this.sorts = this._SortService.filterInvalidSorts(this.sorts, this.sortFields);
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
        this.gotData(this.gridData);
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
        /* Making sure empty data refreshes chart and shows no data there.  */
        this.updateChart.next([{path: 'series', data: []}]);
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
        {labels: this.labels, labelOptions: this.model.labelOptions, sorts: this.sorts}
      );
      changes.push({
        path: 'chart.inverted',
        data: this.isInverted
      });
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

    isNodeField(field) {
      return field && (!NUMBER_TYPES.includes(field.type) || field.checked === 'x');
    }

    isDataField(field) {
      return field && NUMBER_TYPES.includes(field.type) && field.checked !== 'x';
    }

    xAndYSelected() {
      if (!this.settings) {
        return false;
      }
      const selectedY = !isEmpty(filter(this.settings.yaxis, ({checked}) => checked === 'y'));
      const selectedX = Boolean(find(this.settings.xaxis, ({checked}) => checked === 'x'));
      return selectedY || selectedX;
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

      let nodeFields = filter(allFields, this.isNodeField);
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

      if (isEmpty(this.sorts)) {
        forEach(nodeFields, node => {
          this.sorts.push({
            field: {
              type: node.type,
              label: node.displayName,
              dataField: node.columnName
            },
            order: 'asc'
          });
        });
      }

      set(payload, 'sqlBuilder.dataFields', dataFields);
      set(payload, 'sqlBuilder.nodeFields', nodeFields);

      delete payload.supports;
      set(payload, 'sqlBuilder.sorts', this._SortService.mapFrontend2BackendSort(this.sorts));
      set(payload, 'sqlBuilder.booleanCriteria', this.model.sqlBuilder.booleanCriteria);
      set(payload, 'xAxis', {title: this.labels.x});
      set(payload, 'yAxis', {title: this.labels.y});
      set(payload, 'isInverted', this.isInverted);
      set(payload, 'isStockChart', this.isStockChart);
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
      this.model.chartType = this.getNewChartType(this.model);
      const payload = this.generatePayload(this.model);
      this.openSaveModal(ev, payload);
    }

    getNewChartType(model) {
      const types = fpPipe(
        fpFilter(({checked}) => checked === 'y'),
        fpMap('comboType'),
        fpCompact
      )(model.artifacts[0].columns);

      if (types.length >= 1 &&
        every(types, type => type === 'column') &&
        (this.isInverted || model.isInverted)) {
        return 'bar';
      }
      if (!this.comboableCharts.includes(model.chartType)) {
        return model.chartType;
      }
      if (isEmpty(types)) {
        return model.chartType;
      }
      const firstType = types[0];
      const typesAreTheSame = every(types, type => type === firstType);
      return typesAreTheSame ? firstType : 'combo';
    }
  }

};
