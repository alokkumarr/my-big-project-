import {
  Component,
  ViewChild,
  OnInit,
  EventEmitter,
  Input,
  Output
} from '@angular/core';
import { Router } from '@angular/router';
import * as forEach from 'lodash/forEach';
import * as isEmpty from 'lodash/isEmpty';
import * as isNil from 'lodash/isNil';
import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { AnalyzeActionsService } from '../../actions';
import { generateSchedule } from '../../../../common/utils/cron2Readable';
import { DxDataGridComponent } from 'devextreme-angular/ui/data-grid';
import {
  ExecuteService,
  EXECUTION_STATES
} from '../../services/execute.service';
import { DesignerSaveEvent, isDSLAnalysis } from '../../designer/types';
import { Analysis, AnalysisDSL, AnalyzeViewActionEvent } from '../types';
import { JwtService } from '../../../../common/services';

@Component({
  selector: 'analyze-list-view',
  templateUrl: './analyze-list-view.component.html',
  styleUrls: ['./analyze-list-view.component.scss']
})
export class AnalyzeListViewComponent implements OnInit {
  @ViewChild('analysesGrid', { static: true }) analysesGrid: DxDataGridComponent;
  @Output() action: EventEmitter<AnalyzeViewActionEvent> = new EventEmitter();
  @Input('analyses')
  set setAnalyses(analyses: Array<Analysis | AnalysisDSL>) {
    this.analyses = analyses;
    if (!isEmpty(analyses)) {
      const firstAnalysis = analyses[0];
      const subCategoryId = isDSLAnalysis(firstAnalysis)
        ? firstAnalysis.category
        : firstAnalysis.categoryId;
      this.canUserFork =
        this._jwt.hasPrivilege('FORK', { subCategoryId }) &&
        this._jwt.hasPrivilegeForDraftsFolder('FORK');
    }
  }
  @Input() analysisType: string;
  @Input() searchTerm: string;
  @Input() category;
  @Input('cronJobs') set _cronJobs(value) {
    if (isNil(value)) {
      return;
    }
    this.cronJobs = value;
    if (this.analysesGrid && this.analysesGrid.instance && this.analyses) {
      // this.analyses = [...this.analyses];
      this.analysesGrid.instance.refresh();
    }
  }

  cronJobs: any;
  public config: any;
  public canUserFork = false;
  public analyses: (Analysis | AnalysisDSL)[];
  public executions = {};
  public executingState = EXECUTION_STATES.EXECUTING;

  constructor(
    public _DxDataGridService: DxDataGridService,
    public _analyzeActionsService: AnalyzeActionsService,
    public _jwt: JwtService,
    public _executeService: ExecuteService,
    private router: Router
  ) {}

  ngOnInit() {
    this.config = this.getGridConfig();
    this.onExecutionEvent = this.onExecutionEvent.bind(this);
    this._executeService.subscribeToAllExecuting(this.onExecutionEvent);
  }

  navigate(data) {
    this.router.navigate([`/analyze/analysis/${data.id}/executed`], {
      queryParams: { isDSL: !!data.sipQuery }
    });
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
        width: '20%',
        cellTemplate: 'linkCellTemplate',
        cssClass: 'branded-column-name'
      },
      {
        caption: 'METRICS',
        dataField: 'metrics',
        width: '20%',
        calculateCellValue: rowData =>
          rowData.metricName || (rowData.metrics || []).join(', '),
        cellTemplate: 'highlightCellTemplate'
      },
      {
        caption: 'SCHEDULED',
        calculateCellValue: rowData => {
          const cron = this.cronJobs ? this.cronJobs[rowData.id] : '';
          if (!cron) {
            return '';
          }
          const { cronExpression, activeTab, timezone } = cron.jobDetails;
          return generateSchedule(cronExpression, activeTab, timezone);
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
        caption: 'LAST MODIFIED BY',
        width: '20%',
        calculateCellValue: rowData =>
          (
            rowData.updatedUserName ||
            rowData.modifiedBy ||
            rowData.createdBy ||
            rowData.userFullName ||
            ''
          ).toUpperCase(),
        cellTemplate: 'highlightCellTemplate'
      },
      {
        caption: 'LAST MODIFIED ON',
        width: '10%',
        calculateCellValue: rowData =>
          this.secondsToMillis(
            rowData.updatedTimestamp ||
              rowData.modifiedTime ||
              rowData.createdTimestamp ||
              rowData.createdTime ||
              null
          ),
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

  secondsToMillis(timestamp: string | number): number | string {
    const secondsOrMillis = parseInt((timestamp || '').toString(), 10);
    if (!secondsOrMillis) {
      // NaN condition
      return timestamp;
    }

    // Millisecond timestamp consists of 13 digits.
    return secondsOrMillis.toString().length < 13
      ? secondsOrMillis * 1000
      : secondsOrMillis;
  }
}
