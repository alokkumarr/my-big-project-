
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { DatasourceService } from '../../../services/datasource.service';
import { generateSchedule } from '../../../../../common/utils/cron2Readable';

@Component({
  selector: 'logs-dialog',
  templateUrl: './logs-dialog.component.html',
  styleUrls: ['./logs-dialog.component.scss']
})
export class LogsDialogComponent implements OnInit {

  constructor(
    private _dialogRef: MatDialogRef<LogsDialogComponent>,
    private _datasourceService: DatasourceService,
    @Inject(MAT_DIALOG_DATA) public routeData: any
  ) {
    console.log('routeData', routeData);
  }

  ngOnInit() {
    const { bisChannelSysId, bisRouteSysId } = this.routeData;
    this._datasourceService.getRoutesLogs(bisChannelSysId, bisRouteSysId).subscribe(logs => {
      console.log('logs', logs);
    });
  }

  close() {
    this._dialogRef.close();
  }

  getSchedule(schedulerExpression) {
    const { cronexp, activeTab } = schedulerExpression;
    return generateSchedule(cronexp, activeTab);
  }

}
