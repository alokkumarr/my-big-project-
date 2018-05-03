import {
  Component,
  Input
} from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as moment from 'moment';
import * as isEmpty from 'lodash/isEmpty';
import cronstrue from 'cronstrue';

import { Analysis } from '../../types';
import { AnalyzeService } from '../../services/analyze.service';
import { dxDataGridService } from '../../../../common/services/dxDataGrid.service';

const template = require('./analyze-list-view.component.html');
require('./analyze-list-view.component.scss');

@Component({
  selector: 'analyze-list-view-u',
  template
})
export class AnalyzeListViewComponent {

  @Input() analyses: Analysis[];
  @Input() analysisType: string;
  @Input() searchTerm: string;
  @Input() cronJobs: any;

  public config: any;

  constructor(
    private _dxDataGridService: dxDataGridService,
    private _AnalyzeService: AnalyzeService
  ) { }

  ngOnInit() {
    this.config = this.getGridConfig();
  }

  showExecutingFlag(analysisId) {
    return analysisId && this._AnalyzeService.isExecuting(analysisId);
  }

  generateSchedule(rowData) {
    let scheduleHuman = '';
    forEach(this.cronJobs, cron => {
      if (cron.jobDetails.analysisID === rowData.id && !isEmpty(cron.jobDetails.cronExpression)) {
        if (cron.jobDetails.activeTab === 'hourly') {
          // there is no time stamp in hourly cron hence converting to utc and local is not required.
          const localMinuteCron = this.extractMinute(cron.jobDetails.cronExpression);
          scheduleHuman = cronstrue.toString(localMinuteCron);

        } else {
          const localCron = this.convertToLocal(cron.jobDetails.cronExpression);
          scheduleHuman = cronstrue.toString(localCron);
        }
      }
    });
    return scheduleHuman;
  }

  extractMinute(CronUTC) {
    const splitArray = CronUTC.split(' ');
    const date = new Date();
    date.setUTCHours(moment().format('HH'), splitArray[1]);
    const UtcTime = moment.utc(date).local().format('mm').split(' ');
    splitArray[1] = UtcTime[0];
    return splitArray.join(' ');
  }

  convertToLocal(CronUTC) {
    const splitArray = CronUTC.split(' ');
    const date = new Date();
    date.setUTCHours(splitArray[2], splitArray[1]);
    const UtcTime = moment.utc(date).local().format('mm HH').split(' ');
    splitArray[1] = UtcTime[0];
    splitArray[2] = UtcTime[1];
    return splitArray.join(' ');

  }

  checkRowType(rowData) {
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
      calculateCellValue: rowData => this.generateSchedule(rowData),
      width: '12%'
    }, {
      caption: 'TYPE',
      dataField: 'type',
      width: '8%',
      calculateCellValue: rowData => this.checkRowType(rowData),
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
