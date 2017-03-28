import template from './analyze-view.component.html';
import style from './analyze-view.component.scss';

import cloneDeep from 'lodash/cloneDeep';
import remove from 'lodash/remove';

import {Events, AnalyseTypes} from '../../consts';
import AbstractComponentController from 'app/lib/common/components/abstractComponent';

export const AnalyzeViewComponent = {
  template,
  styles: [style],
  controller: class AnalyzeViewController extends AbstractComponentController {
    constructor($injector, $compile, AnalyzeService, dxDataGridService, $state, $mdDialog) {
      'ngInject';
      super($injector);

      this._$compile = $compile;
      this._AnalyzeService = AnalyzeService;
      this._dxDataGridService = dxDataGridService;
      this._$state = $state;
      this._$mdDialog = $mdDialog;

      this.LIST_VIEW = 'list';
      this.CARD_VIEW = 'card';

      this.states = {
        reportView: 'card',
        reportType: 'all',
        searchTerm: ''
      };
    }

    $onInit() {
      this._destroyHandler = this.on(Events.AnalysesRefresh, () => {
        this.loadAnalyses();
      });

      this.loadCategory();
      this.loadAnalyses();
    }

    $onDestroy() {
      this._destroyHandler();
    }

    loadCategory() {
      return this._AnalyzeService.getCategory(this.$state.params.id)
        .then(category => {
          this.category = category;
        });
    }

    execute(analysisId) {
      this._AnalyzeService.executeAnalysis(analysisId)
        .then(analysis => {
          this.goToAnalysis(analysis.analysisId, analysis.publishedAnalysisId);
        });
    }

    goToAnalysis(analysisId, publishId) {
      this._$state.go('analyze.publishedDetail', {analysisId, publishId});
    }

    goToLastPublishedAnalysis(analysisId) {
      this._$state.go('analyze.publishedDetailLast', {analysisId});
    }

    loadAnalyses() {
      return this._AnalyzeService.getAnalyses(this.$state.params.id, {
        filter: this.states.searchTerm
      }).then(analyses => {
        this.reports = analyses;
        this.reloadDataGrid(this.reports);
      });
    }

    reloadDataGrid(analyses) {
      if (this.states.reportView === this.LIST_VIEW) {
        this._gridListInstance.option('dataSource', analyses);
        this._gridListInstance.refresh();
      }
    }

    applySearchFilter() {
      this.loadAnalyses();
    }

    getGridConfig() {
      const dataSource = this.reports || [];
      const columns = [{
        caption: 'NAME',
        dataField: 'name',
        allowSorting: true,
        alignment: 'left',
        width: '50%',
        cellTemplate: 'nameCellTemplate'
      }, {
        caption: 'METRICS',
        dataField: 'metrics',
        allowSorting: true,
        alignment: 'left',
        width: '20%',
        calculateCellValue: rowData => {
          return (rowData.metrics || []).join(', ');
        },
        cellTemplate: 'metricsCellTemplate'
      }, {
        caption: 'SCHEDULED',
        dataField: 'scheduled',
        allowSorting: true,
        alignment: 'left',
        width: '15%'
      }, {
        caption: 'TYPE',
        dataField: 'type',
        allowSorting: true,
        alignment: 'left',
        width: '10%',
        calculateCellValue: rowData => {
          return (rowData.type || '').toUpperCase();
        },
        cellTemplate: 'typeCellTemplate'
      }, {
        caption: '',
        cellTemplate: 'actionCellTemplate'
      }];

      return this._dxDataGridService.mergeWithDefaultConfig({
        onInitialized: this.onGridInitialized.bind(this),
        columns,
        dataSource,
        paging: {
          pageSize: 10
        },
        pager: {
          showPageSizeSelector: true,
          showInfo: true
        }
      });
    }

    onGridInitialized(e) {
      this._gridListInstance = e.component;
    }

    countGridSelectedRows() {
      return this._gridListInstance.getSelectedRowKeys().length;
    }

    onReportTypeChange() {
      if (this.states.reportView === this.LIST_VIEW) {
        if (this.states.reportType === 'all') {
          this._gridListInstance.clearFilter();
        } else {
          this._gridListInstance.filter(['type', '=', this.states.reportType]);
        }
      }
    }

    openDeleteModal(analysis) {
      const confirm = this._$mdDialog.confirm()
            .title('Are you sure you want to delete this analysis?')
        .textContent('Any published analyses will also be deleted.')
        .ok('Delete')
        .cancel('Cancel');

      this._$mdDialog.show(confirm).then(() => {
        return this._AnalyzeService.deleteAnalysis(analysis.id);
      }).then(data => {
        this.onCardAction('delete', data);
      });
    }

    openNewAnalysisModal() {
      this.showDialog({
        template: '<analyze-new></analyze-new>',
        fullscreen: true
      });
    }

    filterReports(item) {
      let isIncluded = true;

      if (this.states.reportType !== 'all') {
        isIncluded = this.states.reportType === item.type;
      }

      return isIncluded;
    }

    removeAnalysis(model) {
      remove(this.reports, report => {
        return report.id === model.id;
      });
      this.reloadDataGrid(this.reports);
    }

    onCardAction(actionType, model) {
      if (actionType === 'fork' || actionType === 'edit') {
        const clone = cloneDeep(model);
        this.openEditModal(actionType, clone);
      } else if (actionType === 'delete') {
        this.removeAnalysis(model);
      }
    }

    fork(report) {
      this.openEditModal('fork', report);
    }

    edit(report) {
      this.openEditModal('edit', report);
    }

    openEditModal(mode, model) {
      const openModal = template => {
        this.showDialog({
          template,
          controller: scope => {
            scope.model = model;
          },
          multiple: true
        });
      };

      if (model.type === AnalyseTypes.Report) {
        openModal(`<analyze-report model="model" mode="${mode}"></analyze-report>`);
      } else if (model.type === AnalyseTypes.Chart) {
        openModal(`<analyze-chart model="model" mode="${mode}"></analyze-chart>`);
      }
    }
  }
};
