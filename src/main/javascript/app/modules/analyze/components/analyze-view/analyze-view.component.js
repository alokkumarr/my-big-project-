import template from './analyze-view.component.html';
import style from './analyze-view.component.scss';

export const AnalyzeViewComponent = {
  template,
  styles: [style],
  controller: class AnalyzeViewController {
    constructor($log, $mdDialog, $document, AnalyzeService) {
      'ngInject';

      this.$log = $log;
      this.$mdDialog = $mdDialog;
      this.$document = $document;
      this._AnalyzeService = AnalyzeService;

      this.LIST_VIEW = 'list';
      this.CARD_VIEW = 'card';

      this.states = {
        reportView: 'card',
        reportType: null
      };

      this._AnalyzeService.getAnalyses()
        .then(analyses => {
          this.reports = analyses;
        });

      this.filterReports = item => {
        if (this.states.reportType !== 'all') {
          return this.states.reportType === item.type;
        }

        return true;
      };
    }

    onReportTypeChange() {
      if (this.states.reportView === this.LIST_VIEW) {
        const inst = this.__gridListInstance;

        if (this.states.reportType === 'all') {
          inst.clearFilter();
        } else {
          inst.filter(['type', '=', this.states.reportType]);
        }
      }
    }

    openNewAnalysisModal(ev) {
      this.$mdDialog.show({
        template: '<analyze-new></analyze-new>',
        targetEvent: ev,
        fullscreen: true
      })
        .then(answer => {
          this.$log.info(`You created the analysis: "${answer}".`);
        }, () => {
          this.$log.info('You cancelled new Analysis modal.');
        });
    }

    getGridData() {
      return Object.assign(this.getDxGridOptions(), {
        dataSource: this.reports,
        onInitialized: instance => {
          this.__gridListInstance = instance.component;
        }
      });
    }

    getDxGridOptions() {
      return {
        columnAutoWidth: true,
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
        },
        selection: {
          mode: 'multiple',
          allowSelectAll: false,
          showCheckBoxesMode: 'always'
        }
      };
    }
  }
};
