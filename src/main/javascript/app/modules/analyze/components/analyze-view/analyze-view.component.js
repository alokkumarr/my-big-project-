import template from './analyze-view.component.html';
import style from './analyze-view.component.scss';

import {Events, AnalyseTypes} from '../../consts';
import AbstractComponentController from 'app/lib/common/components/abstractComponent';

export const AnalyzeViewComponent = {
  template,
  styles: [style],
  controller: class AnalyzeViewController extends AbstractComponentController {
    constructor($injector, $compile, AnalyzeService) {
      'ngInject';
      super($injector);

      this._$compile = $compile;
      this._AnalyzeService = AnalyzeService;

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

      this.loadAnalyses();
    }

    $onDestroy() {
      this._destroyHandler();
    }

    loadAnalyses() {
      this._AnalyzeService.getAnalyses(this.$state.params.id, {
        filter: this.states.searchTerm
      }).then(analyses => {
        this.reports = analyses;
      });
    }

    getGridConfig() {
      const dataSource = this.reports || [];
      const columns = [{
        caption: 'NAME',
        dataField: 'name',
        allowSorting: true,
        alignment: 'left',
        width: '50%'
      }, {
        caption: 'METRICS',
        dataField: 'metrics',
        allowSorting: true,
        alignment: 'left',
        width: '20%',
        calculateCellValue: rowData => {
          return (rowData.metrics || []).join(', ');
        }
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

      return {
        onInitialized: this.onGridInitialized.bind(this),
        columns,
        dataSource,
        columnAutoWidth: true,
        allowColumnReordering: true,
        allowColumnResizing: true,
        showColumnHeaders: true,
        showColumnLines: false,
        showRowLines: false,
        showBorders: false,
        rowAlternationEnabled: true,
        hoverStateEnabled: true,
        scrolling: {
          mode: 'virtual'
        },
        sorting: {
          mode: 'multiple'
        },
        paging: {
          pageSize: 10
        },
        pager: {
          showPageSizeSelector: true,
          showInfo: true
        }
      };
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

    onCardAction(actionType, model) {
      if (actionType === 'fork' || actionType === 'edit') {
        this.openEditModal(actionType, model);
      }
    }

    fork(report) {
      this.openEditModal('fork', report);
    }

    edit(report) {
      this.openEditModal('edit', report);
    }

    openEditModal(mode, model) {
      if (model.type === AnalyseTypes.Report) {
        this.showDialog({
          template: `<analyze-report model="model" mode="${mode}"></analyze-report>`,
          controller: scope => {
            scope.model = model;
          },
          multiple: true
        });
      }
    }
  }
};
