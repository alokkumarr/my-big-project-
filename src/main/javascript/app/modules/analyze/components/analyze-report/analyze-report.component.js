import forEach from 'lodash/forEach';

import template from './analyze-report.component.html';
import style from './analyze-report.component.scss';

export const AnalyzeReportComponent = {
  template,
  styles: [style],
  controller: class AnalyzeReportController {
    constructor($componentHandler, $mdDialog, AnalyzeService) {
      this._$mdDialog = $mdDialog;
      this._AnalyzeService = AnalyzeService;

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

      this.metadata = [];

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

      this._AnalyzeService.getDataByQuery()
        .then(data => {
          this.metadata = data;
        });

      $componentHandler.events.on('$onInstanceAdded', e => {
        if (e.key === 'ard-canvas') {
          this.initCanvas(e.instance);
        }
      });
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    toggleDetailsPanel() {
      this.states.detailsExpanded = !this.states.detailsExpanded;
    }

    initCanvas(canvas) {
      this._AnalyzeService.getArtifacts()
        .then(data => {
          forEach(data, itemA => {
            const table = canvas.model.addTable(itemA._artifact_name);

            table.setPosition(itemA._artifact_position[0], itemA._artifact_position[1]);

            forEach(itemA._artifact_attributes, itemB => {
              const field = table.addField(itemB['_actual_col-name'], itemB._display_name, itemB._alias_name);

              field.setType(itemB._type);
            });
          });

          forEach(data, itemA => {
            forEach(itemA._sql_builder.joins, itemB => {
              const tableA = itemB.criteria[0]['table-name'];
              const tableB = itemB.criteria[1]['table-name'];

              if (tableA !== tableB) {
                canvas.model.addJoin(itemB.type, {
                  table: tableA,
                  field: itemB.criteria[0]['column-name'],
                  side: itemB.criteria[0].side
                }, {
                  table: tableB,
                  field: itemB.criteria[1]['column-name'],
                  side: itemB.criteria[1].side
                });
              }
            });
          });
        });
    }
  }
};
