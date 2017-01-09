import template from './analyze-report.component.html';
import style from './analyze-report.component.scss';

export const AnalyzeReportComponent = {
  template,
  styles: [style],
  controller: class AnalyzeReportController {
    constructor($componentHandler, $mdDialog, $scope, AnalyzeService) {
      this._$mdDialog = $mdDialog;
      this._$scope = $scope;
      this._AnalyzeService = AnalyzeService;

      this.DESIGNER_MODE = 'designer';
      this.QUERY_MODE = 'query';

      this.states = {
        sqlMode: this.DESIGNER_MODE,
        detailsExpanded: false
      };

      this.data = {
        query: ''
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
      this.uiGridData = {}

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
          this.uiGridData.data = data;
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
      this.canvas = canvas;

      this._AnalyzeService.getArtifacts()
        .then(data => {
          this.canvas.model.precess(data);
          this.uiGridData.artifacts = this.canvas.model.tables;
        });
    }

    setSqlMode(mode) {
      this.states.sqlMode = mode;

      if (mode === this.QUERY_MODE) {
        this.data.query = this.canvas.model.generateQuery();
      }
    }

    openSortModal(ev) {
      const scope = this._$scope.$new();

      scope.model = {
        fields: this.canvas.model.getSelectedFields(),
        sorts: this.canvas.model.sorts
      };

      this._$mdDialog
        .show({
          template: '<analyze-report-sort model="model"></analyze-report-sort>',
          targetEvent: ev,
          fullscreen: true,
          skipHide: true,
          scope: scope
        });
    }
  }
};
