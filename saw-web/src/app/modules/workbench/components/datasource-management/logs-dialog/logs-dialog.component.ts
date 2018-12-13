
import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { DatasourceService } from '../../../services/datasource.service';
import { generateSchedule } from '../../../../../common/utils/cron2Readable';

interface ILogObject {
  pid: string; /* don't show */
  routeSysId: number; /* don't show */
  channelSysId: number; /* don't show */
  channelType: string; /* don't show */
  filePattern: string;
  fileName: string;
  actualFileRecDate: number;
  recdFileName: string;
  recdFileSize: number;
  mflFileStatus: string;
  bisProcessState: string;
  modifiedDate: number;
  createdDate: number;
}
@Component({
  selector: 'logs-dialog',
  templateUrl: './logs-dialog.component.html',
  styleUrls: ['./logs-dialog.component.scss']
})
export class LogsDialogComponent implements OnInit {

  public logs: ILogObject[];
  public lastFireTime: number;
  public nextFireTime: number;

  constructor(
    private _dialogRef: MatDialogRef<LogsDialogComponent>,
    private _datasourceService: DatasourceService,
    @Inject(MAT_DIALOG_DATA) public routeData: any
  ) {}

  ngOnInit() {
    const { bisChannelSysId, bisRouteSysId } = this.routeData;
    this._datasourceService.getRoutesLogs(bisChannelSysId, bisRouteSysId).subscribe(resp => {
      this.logs = resp.logs;
      this.lastFireTime = resp.lastFireTime;
      this.nextFireTime = resp.nextFireTime;
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
