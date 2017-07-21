import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import findIndex from 'lodash/findIndex';
import find from 'lodash/find';
import filter from 'lodash/filter';
import get from 'lodash/get';
import isEmpty from 'lodash/isEmpty';
import assign from 'lodash/assign';
import map from 'lodash/map';
import values from 'lodash/values';
import clone from 'lodash/clone';
import set from 'lodash/set';
import cloneDeep from 'lodash/cloneDeep';
import {DEFAULT_BOOLEAN_CRITERIA} from '../../services/filter.service';

import {ENTRY_MODES, NUMBER_TYPES} from '../../consts';

import template from './analyze-chart.component.html';
import style from './analyze-chart.component.scss';

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
  controller: class AnalyzeChartController {
    constructor($componentHandler, $mdDialog, $scope, $timeout, AnalyzeService,
                ChartService, FilterService, $mdSidenav, $translate, toastMessage) {
      'ngInject';

      this._FilterService = FilterService;
      this._AnalyzeService = AnalyzeService;
      this._ChartService = ChartService;
      this._$mdSidenav = $mdSidenav;
      this._$mdDialog = $mdDialog;
      this._$timeout = $timeout;
      this._$translate = $translate;
      this._toastMessage = toastMessage;
      this.BAR_COLUMN_OPTIONS = BAR_COLUMN_OPTIONS;
      this.draftMode = false;

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

      this.barColumnChoice = '';
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
      this.settings = this._ChartService.fillSettings(this.model.artifacts, this.model);
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
        this.draftMode = false;
      });
    }

    goBack() {
      if (!this.draftMode) {
        this.$dialog.hide();
        return;
      }

      const confirm = this._$mdDialog.confirm()
            .title('There are unsaved changes')
            .textContent('Do you want to discard unsaved changes and go back?')
        .multiple(true)
        .ok('Discard')
        .cancel('Cancel');

      this._$mdDialog.show(confirm).then(() => {
        this.$dialog.hide();
      }, err => {
        if (err) {
          this._$log.error(err);
        }
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

      this.draftMode = true;
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
      this.draftMode = true;
      this.reloadChart(this.settings);
    }

    onSettingsChanged() {
      this.analysisChanged = true;
      this.draftMode = true;
    }

    clearFilters() {
      this.filters = [];
      this.analysisChanged = true;
      this.draftMode = true;
    }

    onFilterRemoved(index) {
      this.filters.splice(index, 1);
      this.analysisChanged = true;
      this.draftMode = true;
    }

    onApplyFilters({filters, filterBooleanCriteria} = {}) {
      if (filters) {
        this.filters = filters;
        this.analysisChanged = true;
        this.draftMode = true;
      }
      if (filterBooleanCriteria) {
        this.model.sqlBuilder.booleanCriteria = filterBooleanCriteria;
      }
    }

    refreshChartData() {
      if (!this.checkModelValidity()) {
        return;
      }
      this.showProgress = true;
      const payload = this.generatePayload(this.model);
      return this._AnalyzeService.getDataBySettings(payload).then(({data}) => {
        const parsedData = this._ChartService.parseData(data, payload.sqlBuilder);
        this.gridData = this.filteredGridData = parsedData || this.filteredGridData;
        this.analysisChanged = false;
        this.showProgress = false;
        this.reloadChart(this.settings, this.filteredGridData);
      }, () => {
        this.showProgress = false;
      });
    }

    /** check the parameters, before sending the request for the cahrt data */
    checkModelValidity() {
      let isValid = true;
      const errors = [];
      const x = find(this.settings.xaxis, x => x.checked === 'x');
      const y = find(this.settings.yaxis, y => y.checked === 'y');
      const z = find(this.settings.zaxis, z => z.checked === 'z');

      switch (this.model.chartType) {
        case 'bubble':
        // x, y and z axes are mandatory
        // grouping is optional
          if (!x || !y || !z) {
            errors[0] = 'ERROR_X_Y_SIZEBY_AXES_REQUIRED';
            isValid = false;
          }
          break;
        default:
        // x and y axes are mandatory
        // grouping is optional
          if (!x || !y) {
            errors[0] = 'ERROR_X_Y_AXES_REQUIRED';
            isValid = false;
          }
      }

      if (!isValid) {
        this._$translate(errors).then(translations => {
          this._toastMessage.error(values(translations).join('\n'), '', {
            timeOut: 3000
          });
        });
      }

      return isValid;
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
      if (isEmpty(filteredGridData)) {
        return;
      }
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
      const tpl = '<analyze-description-dialog model="model" on-save="onSave($data)"></analyze-description-dialog>';

      this._$mdDialog.show({
        template: tpl,
        controller: scope => {
          scope.model = {
            description: this.model.description
          };

          scope.onSave = data => {
            this.draftMode = true;
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
      const y = find(this.settings.yaxis, y => y.checked === 'y');
      const z = find(this.settings.zaxis, z => z.checked === 'z');

      const allFields = [g, x, y, z];

      const nodeFields = filter(allFields, this.isStringField);
      const dataFields = filter(allFields, this.isDataField).map(field => {
        field.aggregate = 'sum';
        return field;
      });

      set(payload, 'sqlBuilder.dataFields', dataFields);
      set(payload, 'sqlBuilder.nodeFields', nodeFields);

      delete payload.supports;
      set(payload, 'sqlBuilder.sorts', []);
      set(payload, 'sqlBuilder.booleanCriteria', this.model.sqlBuilder.booleanCriteria);
      set(payload, 'xAxis', {title: this.labels.x});
      set(payload, 'yAxis', {title: this.labels.y});
      set(payload, 'legend', {
        align: this.legend.align,
        layout: this.legend.layout
      });

      return payload;
    }

    openSaveModal(ev) {
      const payload = this.generatePayload(this.model);

      const tpl = '<analyze-save-dialog model="model" on-save="onSave($data)"></analyze-save-dialog>';

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
      this.draftMode = false;
      this.$dialog.hide(successfullySaved);
    }
  }

};
