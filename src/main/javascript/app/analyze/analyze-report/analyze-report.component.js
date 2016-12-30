import template from './analyze-report.component.html';
import style from './analyze-report.component.scss';

export const AnalyzeReportComponent = {
  template,
  styles: [style],
  controller: class AnalyzeReportController {
    constructor($mdDialog, AnalyzeService) {
      this.$mdDialog = $mdDialog;
      this.AnalyzeService = AnalyzeService;

      this.DESIGNER_MODE = 'designer';
      this.QUERY_MODE = 'query';

      this.states = {
        sqlMode: this.DESIGNER_MODE,
        detailsExpanded: false
      };

      this.data = {
        query: 'Select * From Orders'
      };

      this.jsPlumbOptions = {
        endpoints: {
          source: {
            endpoint: 'Dot',
            isSource: true,
            isTarget: true,
            maxConnections: 1,
            connector: ['Flowchart', {
              cornerRadius: 10
            }],
            endpointStyle: {
              radius: 9,
              stroke: '#B0BFC8',
              strokeWidth: 3
            },
            connectorStyle: {
              stroke: '#B0BFC8',
              strokeWidth: 3,
              outlineStroke: 'white',
              outlineWidth: 2
            },
            connectorHoverStyle: {
              stroke: '#B0BFC8'
            },
            endpointHoverStyle: {
              stroke: '#B0BFC8'
            }
          },
          target: {
            endpoint: 'Dot',
            isTarget: true,
            maxConnections: -1,
            endpointStyle: {
              radius: 9,
              stroke: '#B0BFC8',
              strokeWidth: 3
            },
            endpointHoverStyle: {
              stroke: '#B0BFC8'
            }
          }
        }
      };

      this.dxGridOptions = {
        columnAutoWidth: true,
        showColumnHeaders: true,
        showColumnLines: false,
        showRowLines: false,
        showBorders: true,
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
        width: 800
      };

      this.tables = [];
      this.metadata = [];

      this.getSqlData = () => {
        return {
          settings: this.jsPlumbOptions,
          tables: this.tables
        };
      };

      this.getGridData = () => {
        return Object.assign(this.dxGridOptions, {
          dataSource: this.metadata,
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
              caption: 'CUSTOMER NAME',
              dataField: 'customerName',
              alignment: 'left',
              allowSorting: true,
              sortOrder: 'desc',
              sortIndex: 1,
              width: '30%'
            },
            {
              caption: 'TOTAL PRICE',
              dataField: 'price',
              alignment: 'left',
              allowSorting: true,
              sortOrder: 'desc',
              sortIndex: 2,
              width: '30%'
            },
            {
              caption: 'NAME',
              dataField: 'name',
              alignment: 'left',
              allowSorting: true,
              sortOrder: 'desc',
              sortIndex: 3,
              width: '25%'
            }
          ]
        });
      };
      this.AnalyzeService.getTables()
        .then(data => {
          this.tables = data;
        });

      this.AnalyzeService.getDataByQuery()
        .then(data => {
          this.metadata = data;
        });
    }

    $onInit() {

    }

    cancel() {
      this.$mdDialog.cancel();
    }

    toggleDetailsPanel() {
      this.states.detailsExpanded = !this.states.detailsExpanded;
    }
  }
};
