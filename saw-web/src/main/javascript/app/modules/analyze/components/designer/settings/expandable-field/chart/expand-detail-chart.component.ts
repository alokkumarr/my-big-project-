declare const require: any;
import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as moment from 'moment';
import * as isUndefined from 'lodash/isUndefined';
import { ArtifactColumnChart, Format } from '../../../types';
import {
  DATE_INTERVALS,
  DATE_TYPES,
  DEFAULT_DATE_FORMAT,
  CHART_DATE_FORMATS,
  CHART_DATE_FORMATS_OBJ
} from '../../../../../consts';
import { AnalyzeDialogService } from '../../../../../services/analyze-dialog.service';
import {
  formatNumber,
  isFormatted
} from '../../../../../../../common/utils/numberFormatter';

import { DesignerChangeEvent } from '../../../types';

const template = require('./expand-detail-chart.component.html');

const FLOAT_SAMPLE = 1000.33333;
const INT_SAMPLE = 1000;
@Component({
  selector: 'expand-detail-chart',
  template
})
export class ExpandDetailChartComponent {
  @Output()
  public change: EventEmitter<DesignerChangeEvent> = new EventEmitter();

  @Input() public artifactColumn: ArtifactColumnChart;
  @Input() public fieldCount: any;

  public DATE_INTERVALS = DATE_INTERVALS;
  public DATE_FORMATS_OBJ = CHART_DATE_FORMATS_OBJ;
  public isDataField: boolean = false;
  public hasDateInterval: boolean = false;
  public numberSample: string;
  public dateSample: string;
  public isFloat: boolean;
  public limitType;
  public limitValue;

  constructor(private _analyzeDialogService: AnalyzeDialogService) {}

  ngOnInit() {
    const type = this.artifactColumn.type;
    this.limitType = this.artifactColumn.limitValue === null ? '' : this.artifactColumn.limitType;
    this.limitValue = this.artifactColumn.limitValue;

    this.isDataField = ['y', 'z'].includes(this.artifactColumn.area);
    this.hasDateInterval = DATE_TYPES.includes(type);
    this.changeSample();
  }

  onAliasChange(value) {
    this.artifactColumn.aliasName = value;
    this.change.emit({ subject: 'aliasName' });
  }

  onFormatChange(format: Format | string) {
    if (format) {
      this.artifactColumn.format = format;
      this.artifactColumn.dateFormat = format as string;
      this.changeSample();
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

  changeSample() {
    if (this.isDataField) {
      this.changeNumberSample();
    } else if (this.hasDateInterval) {
      this.changeDateSample();
    }
  }

  changeNumberSample() {
    const format = this.artifactColumn.format;
    const sampleNr = this.isFloat ? FLOAT_SAMPLE : INT_SAMPLE;

    if (format && isFormatted(<Format>format)) {
      this.numberSample = formatNumber(sampleNr, <Format>format);
    } else {
      this.numberSample = null;
    }
  }

  changeDateSample() {
    const format = <string>this.artifactColumn.format;
    if (format) {
      this.dateSample = moment.utc().format(format);
    }
  }

  onLimitDataChange() {
    this.limitValue = this.limitValue < 0 ? '' : this.limitValue;
    if (this.limitValue < 0 || isUndefined(this.limitType) || this.limitType === null) {
      return false;
    }
    if (this.limitValue === null || isUndefined(this.limitValue)) {
      delete this.artifactColumn.limitValue;
      delete this.artifactColumn.limitType;
    }
    this.artifactColumn.limitValue = this.limitValue;
    this.artifactColumn.limitType = this.limitType;
    this.change.emit({ subject: 'fetchLimit' });
  }
}
