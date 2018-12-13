import { Component, OnInit, EventEmitter, Input, Output } from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';
import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { AnalyzeActionsService } from '../../actions';
import { generateSchedule } from '../../../../common/utils/cron2Readable';
import {
  ExecuteService,
  EXECUTION_STATES
} from '../../services/execute.service';
import { DesignerSaveEvent } from '../../designer/types';
import { Analysis, AnalyzeViewActionEvent } from '../types';
import { JwtService } from '../../../../common/services';

@Component({
  selector: 'analyze-list-view',
  templateUrl: './analyze-list-view.component.html',
  styleUrls: ['./analyze-list-view.component.scss']
})
export class AnalyzeListViewComponent implements OnInit {
  @Output() action: EventEmitter<AnalyzeViewActionEvent> = new EventEmitter();
  @Input('analyses')
  set setAnalyses(analyses: Analysis[]) {
    this.analyses = analyses;
    if (!isEmpty(analyses)) {
      this.canUserFork = this._jwt.hasPrivilege('FORK', {
        subCategoryId: analyses[0].categoryId
      });
    }
  }
  @Input() analysisType: string;
  @Input() searchTerm: string;
  @Input() cronJobs: any;

  public config: any;
  public canUserFork = false;
  public analyses: Analysis[];
  public executions = {};
  public executingState = EXECUTION_STATES.EXECUTING;

  constructor(
    public _DxDataGridService: DxDataGridService,
    public _analyzeActionsService: AnalyzeActionsService,
    public _jwt: JwtService,
    public _executeService: ExecuteService
  ) {}

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

  afterEdit({ analysis, requestExecution }: DesignerSaveEvent) {
    this.action.emit({
      action: 'edit',
      analysis,
      requestExecution
    });
  }

  fork(an) {
    this._analyzeActionsService.fork(an);
  }

  getRowType(rowData) {
    let analysisType = rowData.type;
    if (analysisType === 'esReport') {
      analysisType = 'REPORT';
    }
    return analysisType.toUpperCase();
  }

  getGridConfig() {
    const columns = [
      {
        caption: 'NAME',
        dataField: 'name',
        width: '36%',
        cellTemplate: 'linkCellTemplate',
        cssClass: 'branded-column-name'
      },
      {
        caption: 'METRICS',
        dataField: 'metrics',
        width: '21%',
        calculateCellValue: rowData =>
          rowData.metricName || (rowData.metrics || []).join(', '),
        cellTemplate: 'highlightCellTemplate'
      },
      {
        caption: 'SCHEDULED',
        calculateCellValue: rowData => {
          const cron = this.cronJobs[rowData.id];
          if (!cron) {
            return '';
          }
          const {cronExpression, activeTab} = cron.jobDetails;
          return generateSchedule(cronExpression, activeTab);
        },
        width: '12%'
      },
      {
        caption: 'TYPE',
        dataField: 'type',
        width: '8%',
        calculateCellValue: rowData => this.getRowType(rowData),
        cellTemplate: 'typeCellTemplate'
      },
      {
        caption: 'CREATOR',
        dataField: 'userFullName',
        width: '20%',
        calculateCellValue: rowData =>
          (rowData.userFullName || '').toUpperCase(),
        cellTemplate: 'highlightCellTemplate'
      },
      {
        caption: 'CREATED',
        dataField: 'createdTimestamp',
        width: '8%',
        cellTemplate: 'dateCellTemplate'
      },
      {
        caption: '',
        cellTemplate: 'actionCellTemplate'
      }
    ];
    return this._DxDataGridService.mergeWithDefaultConfig({
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
      customizeColumns: cols => {
        const last = cols.length - 1;
        forEach(cols, (col, i) => {
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
