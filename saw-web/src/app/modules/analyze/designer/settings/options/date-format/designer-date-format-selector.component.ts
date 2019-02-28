import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import * as moment from 'moment';

import { AnalyzeDialogService } from '../../../../services/analyze-dialog.service';
import {
  ArtifactColumnChart,
  Format,
  DesignerChangeEvent
} from '../../../types';
import {
  CHART_DATE_FORMATS
} from '../../../../consts';

@Component({
  selector: 'designer-date-format-selector',
  templateUrl: 'designer-date-format-selector.component.html'
})
export class DesignerDateFormatSelectorComponent implements OnInit {
  @Output() public change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() public artifactColumn: ArtifactColumnChart;

  public dateSample: string;

  constructor(private _analyzeDialogService: AnalyzeDialogService) { }

  ngOnInit() {
    this.changeDateSample();
  }

  onFormatChange(format: Format | string) {
    if (format) {
      this.artifactColumn.format = format;
      this.artifactColumn.dateFormat = format as string;
      this.changeDateSample();
      this.change.emit({ subject: 'format' });
    }
  }

  openDateFormatDialog() {
    this._analyzeDialogService
      .openDateFormatDialog(
        <string>this.artifactColumn.dateFormat,
        CHART_DATE_FORMATS
      )
      .afterClosed()
      .subscribe(format => this.onFormatChange(format));
  }

  changeDateSample() {
    const format = <string>this.artifactColumn.format;
    if (format) {
      this.dateSample = moment.utc().format(format);
    }
  }
}
