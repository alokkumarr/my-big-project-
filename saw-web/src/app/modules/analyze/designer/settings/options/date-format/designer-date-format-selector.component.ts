import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import * as moment from 'moment';
import * as get from 'lodash/get';
import { AnalyzeDialogService } from '../../../../services/analyze-dialog.service';
import {
  ArtifactColumnChart,
  Format,
  DesignerChangeEvent
} from '../../../types';
import {
  CHART_DATE_FORMATS,
  DATE_FORMATS,
  DATE_FORMATS_OBJ,
  CHART_DATE_FORMATS_OBJ
} from '../../../../consts';

const dateFormatsMap = {
  pivot: {
    array: DATE_FORMATS,
    obj: DATE_FORMATS_OBJ
  },
  chart: {
    array: CHART_DATE_FORMATS,
    obj: CHART_DATE_FORMATS_OBJ
  }
};
@Component({
  selector: 'designer-date-format-selector',
  templateUrl: 'designer-date-format-selector.component.html'
})
export class DesignerDateFormatSelectorComponent implements OnInit {
  @Output() public change: EventEmitter<
    DesignerChangeEvent
  > = new EventEmitter();
  @Input() public artifactColumn: ArtifactColumnChart;
  @Input() public analysisType: string;

  public dateSample: string;

  constructor(private _analyzeDialogService: AnalyzeDialogService) {}

  ngOnInit() {
    this.changeDateSample();
  }

  onFormatChange(format: Format | string) {
    if (format) {
      console.log('format', format);
      this.artifactColumn.format = format;
      this.changeDateSample();
      this.change.emit({ subject: 'format' });
    }
  }

  getDateLabel(artifactColumn) {
    const dateFormatsObj = get(dateFormatsMap, `${this.analysisType}.obj`);
    // const format = this.analysisType === 'pivot' ? 'format' : 'dateFormat';
    return get(dateFormatsObj, `[${artifactColumn.format}].label`, '');
  }

  openDateFormatDialog() {
    const dateFormats = get(dateFormatsMap, `${this.analysisType}.array`);
    this._analyzeDialogService
      .openDateFormatDialog(<string>this.artifactColumn.format, dateFormats)
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
