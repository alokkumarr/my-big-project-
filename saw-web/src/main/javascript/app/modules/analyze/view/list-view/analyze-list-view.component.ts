import { Component, EventEmitter, Input, Output } from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';
import { dxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { AnalyzeActionsService } from '../../actions';
import { generateSchedule } from '../../cron';
import {
  ExecuteService,
  EXECUTION_STATES
} from '../../services/execute.service';
import { DesignerSaveEvent } from '../../designer/types';
import { Analysis, AnalyzeViewActionEvent } from '../types';
import { JwtService } from '../../../../../login/services/jwt.service';


const template = require('./analyze-list-view.component.html');
require('./analyze-list-view.component.scss');

@Component({
  selector: 'analyze-list-view',
  template
})
export class AnalyzeListViewComponent {

  @Output() action: EventEmitter<AnalyzeViewActionEvent> = new EventEmitter();
  @Input('analyses') set setAnalyses(analyses: Analysis[]) {
    this.analyses = analyses;
    if (!isEmpty(analyses)) {
      this.canUserFork = this._jwt.hasPrivilege('FORK', {
        subCategoryId: analyses[0].categoryId
      });
    }
  };
  @Input() analysisType: string;
  @Input() searchTerm: string;
  @Input() cronJobs: any;

  public config: any;
  public canUserFork = false;
  public analyses: Analysis[];
  public executions = {};
  public executingState = EXECUTION_STATES.EXECUTING;

  constructor(
    private _dxDataGridService: dxDataGridService,
    private _analyzeActionsService: AnalyzeActionsService,
    private _jwt: JwtService,
    private _executeService: ExecuteService
  ) { }

  ngOnInit() {
    this.config = this.getGridConfig();
    this.onExecutionEvent = this.onExecutionEvent.bind(this);
    this._executeService.subscribeToAllExecuting(this.onExecutionEvent);
  }

  onExecutionEvent(e) {
    this.executions = e;
  }

  afterDelete(analysis) {
    this.action.emit({
      action: 'delete',
      analysis
    });
  }

  afterExecute(analysis) {
    this.action.emit({
      action: 'execute',
      analysis
    });
  }

  afterPublish(analysis) {
    this.action.emit({
      action: 'publish',
      analysis
    });
  }

  afterEdit({analysis, requestExecution}: DesignerSaveEvent) {
    this.action.emit({
      action: 'edit',
      analysis,
      requestExecution
    });
  }

  fork(analysis) {
    this._analyzeActionsService.fork(analysis).then(({analysis, requestExecution}: DesignerSaveEvent) => {
      this.action.emit({
        action: 'fork',
        analysis,
        requestExecution
      });
    });
  }

  getRowType(rowData) {
    let analysisType = rowData.type;
    if (analysisType === 'esReport') {
      analysisType = 'REPORT';
    }
    return analysisType.toUpperCase();
  }

  getGridConfig() {
    const columns = [{
      caption: 'NAME',
      dataField: 'name',
      width: '36%',
      cellTemplate: 'linkCellTemplate',
      cssClass: 'branded-column-name'
    }, {
      caption: 'METRICS',
      dataField: 'metrics',
      width: '21%',
      calculateCellValue: rowData => (
        rowData.metricName ||
        (rowData.metrics || []).join(', ')
      ),
      cellTemplate: 'highlightCellTemplate'
    }, {
      caption: 'SCHEDULED',
      calculateCellValue: rowData => generateSchedule(this.cronJobs, rowData.id),
      width: '12%'
    }, {
      caption: 'TYPE',
      dataField: 'type',
      width: '8%',
      calculateCellValue: rowData => this.getRowType(rowData),
      cellTemplate: 'typeCellTemplate'
    }, {
      caption: 'CREATOR',
      dataField: 'userFullName',
      width: '20%',
      calculateCellValue: rowData => (rowData.userFullName || '').toUpperCase(),
      cellTemplate: 'highlightCellTemplate'
    }, {
      caption: 'CREATED',
      dataField: 'createdTimestamp',
      width: '8%',
      cellTemplate: 'dateCellTemplate'
    }, {
      caption: '',
      cellTemplate: 'actionCellTemplate'
    }];
    return this._dxDataGridService.mergeWithDefaultConfig({
      columns,
      paging: {
        pageSize: 10
      },
      pager: {
        showPageSizeSelector: true,
        showInfo: true
      },
      width: '100%',
      height: '100%',
      customizeColumns: columns => {
        const last = columns.length - 1;
        forEach(columns, (col, i) => {
          if (i === last) {
            col.allowSorting = false;
            col.alignment = 'center';
          } else {
            col.allowSorting = true;
            col.alignment = 'left';
          }
        });
      }
    });
  }
}
