import angular from 'angular';

import 'devextreme/ui/data_grid';
import 'devextreme/dist/css/dx.common.css';
import 'devextreme/dist/css/dx.light.css';

import template from './analyze-view.component.html';
import style from './analyze-view.component.scss';

export const AnalyzeViewComponent = {
  template,
  styles: [style],
  controller: class AnalyzeViewController {
    constructor($log, $mdDialog, $document) {
      'ngInject';

      this.$log = $log;
      this.$mdDialog = $mdDialog;
      this.$document = $document;

      this.transactionVolumeChartData = {
        Alpha: [
          [0.3, 5],
          [2.1, 25],
          [3.5, 10],
          [4.5, 11],
          [5.6, 6],
          [6.5, 21],
          [7.1, 20],
          [7.8, 29],
          [8.7, 35],
          [9, 29],
          [9.5, 5],
          [11.1, 20]
        ],
        Bravo: [
          [0.3, 2],
          [4.8, 13],
          [6.2, 35],
          [8.9, 10],
          [10.6, 22],
          [11.1, 10]
        ]
      };
      this.transactionVolumeChartOptions = {
        xAxis: {
          categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
          startOnTick: true,
          title: {
            text: 'Months'
          }
        },
        yAxis: {
          title: {
            text: 'Revenue'
          }
        },
        legend: {
          align: 'right',
          verticalAlign: 'top',
          layout: 'vertical',
          x: 0,
          y: 100
        },
        chart: {
          marginRight: 120
        },
        plotOptions: {
          line: {
            pointPlacement: -0.5
          }
        }
      };

      this.LIST_VIEW = 'list';
      this.CARD_VIEW = 'card';

      this.states = {
        reportView: 'card',
        reportType: null
      };

      this.dxGridOptions = {
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

      this.getGridData = () => {
        return Object.assign(this.dxGridOptions, {
          dataSource: this.reports,
          columns: [
            {
              caption: 'ID',
              dataField: 'id',
              allowSorting: true,
              sortOrder: 'desc',
              sortIndex: 0,
              visible: false
            },
            {
              caption: 'NAME',
              dataField: 'name',
              alignment: 'left',
              allowSorting: true,
              sortOrder: 'desc',
              sortIndex: 1,
              width: '30%'
            },
            {
              caption: 'METRICS',
              dataField: 'metrics',
              alignment: 'left',
              allowSorting: true,
              sortOrder: 'desc',
              sortIndex: 2,
              width: '30%'
            },
            {
              caption: 'SCHEDULED',
              dataField: 'scheduled',
              alignment: 'left',
              allowSorting: true,
              sortOrder: 'desc',
              sortIndex: 3,
              width: '25%'
            },
            {
              caption: 'TYPE',
              dataField: 'type',
              alignment: 'left',
              allowSorting: true,
              sortOrder: 'desc',
              sortIndex: 4,
              cssClass: 'analyze-view_grid_cell',
              cellTemplate: '#gridTypeCell',
              width: '15%'
            }
          ],
          onInitialized: instance => {
            this.__gridListInstance = instance.component;
          }
        });
      };

      this.filterReports = item => {
        if (this.states.reportType !== 'all') {
          return this.states.reportType === item.type;
        }

        return true;
      };

      this.reports = [
        {
          type: 'chart',
          name: 'Order Revenue By Customer',
          metrics: ['Orders', 'Revenue'],
          scheduled: 'Every Friday at 12:00pm',
          chart: {
            options: this.transactionVolumeChartOptions,
            data: this.transactionVolumeChartData
          }
        },
        {
          type: 'report',
          name: 'Shipper Usage',
          metrics: ['Orders'],
          scheduled: 'Daily',
          report: {
            options: {
              dataSource: [{
                id: 1,
                shipper: 'Aaron\'s Towing',
                order: '12bc',
                total: '$600'
              }, {
                id: 2,
                shipper: 'Aaron\'s Towing',
                order: '12bd',
                total: '$650'
              }, {
                id: 3,
                shipper: 'Aaron\'s Towing',
                order: '12be',
                total: '$550'
              }, {
                id: 4,
                shipper: 'Aaron\'s Towing',
                order: '12bf',
                total: '$700'
              }],
              columns: ['shipper', 'order', 'total'],
              columnAutoWidth: true,
              showBorders: true,
              showColumnHeaders: true,
              showColumnLines: true,
              showRowLines: true,
              width: 500,
              scrolling: {
                mode: 'virtual'
              },
              sorting: {
                mode: 'none'
              },
              paging: {
                pageSize: 10
              },
              pager: {
                showPageSizeSelector: true,
                showInfo: true
              }
            }
          }
        }
      ];
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
        // controller: newAnalysisController,
        // controllerAs: '$ctrl',
        // template: newAnalysisTemplate,
        template: '<analyze-new></analyze-new>',
        parent: angular.element(this.$document.body),
        targetEvent: ev,
        clickOutsideToClose: true,
        fullscreen: true // Only for -xs, -sm breakpoints.
      })
        .then(answer => {
          this.$log.info(`You created the analysis: "${answer}".`);
        }, () => {
          this.$log.info('You cancelled new Analysis modal.');
        });
    }
  }
};
