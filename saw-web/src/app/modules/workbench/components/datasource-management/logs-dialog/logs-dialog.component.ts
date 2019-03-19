import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { DatasourceService } from '../../../services/datasource.service';
import { generateSchedule } from '../../../../../common/utils/cron2Readable';

interface ILogObject {
  pid: string /* don't show */;
  routeSysId: number /* don't show */;
  channelSysId: number /* don't show */;
  channelType: string /* don't show */;
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
    this._datasourceService
      .getRoutesLogs(bisChannelSysId, bisRouteSysId)
      .subscribe(resp => {
        this.logs = resp.logs;
        this.lastFireTime = resp.lastFireTime;
        this.nextFireTime = resp.nextFireTime;
      });
  }

  close() {
    this._dialogRef.close();
  }

  getSchedule(schedulerExpression) {
    const { cronexp, activeTab, timezone } = schedulerExpression;
    return generateSchedule(cronexp, activeTab, timezone);
  }

  /**
   * Returns height of logs grid calculated from container.
   * This is necessary because in safari, the implementation of CSS's calc
   * is buggy.
   *
   * @returns {number}
   * @memberof LogsDialogComponent
   */
  customGridHeight(): number {
    const container = document.getElementById(
      'route-log__dialog-content-container'
    );
    return container.scrollHeight - container.children[0].scrollHeight;
  }
}
