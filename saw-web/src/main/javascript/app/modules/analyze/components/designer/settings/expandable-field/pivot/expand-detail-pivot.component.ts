declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as moment from 'moment';
import {
  ArtifactColumnPivot,
  Format
}  from '../../../types';
import {
  AGGREGATE_TYPES,
  DATE_INTERVALS,
  DATE_TYPES,
  FLOAT_TYPES,
  DATE_FORMATS_OBJ,
  DEFAULT_DATE_FORMAT
} from '../../../../../consts';
import { AnalyzeDialogService } from '../../../../../services/analyze-dialog.service'
import {
  formatNumber,
  isFormatted
} from '../../../../../../../common/utils/numberFormatter';

import { FieldChangeEvent } from '../../single';

const template = require('./expand-detail-pivot.component.html');

const FLOAT_SAMPLE = 1000.33333;
const INT_SAMPLE = 1000;
@Component({
  selector: 'expand-detail-pivot',
  template
})
export class ExpandDetailPivotComponent {
  @Output() public change: EventEmitter<FieldChangeEvent> = new EventEmitter();

  @Input() public artifactColumn: ArtifactColumnPivot;

  public AGGREGATE_TYPES = AGGREGATE_TYPES;
  public DATE_INTERVALS = DATE_INTERVALS;
  public DATE_FORMATS_OBJ = DATE_FORMATS_OBJ;
  public isDataField: boolean = false;
  public hasDateInterval: boolean = false;
  public numberSample: string;
  public dateSample: string;
  public isFloat: boolean;

  constructor(
    private _analyzeDialogService: AnalyzeDialogService
  ) {}

  ngOnInit() {
    const type = this.artifactColumn.type;
    this.isDataField = this.artifactColumn.area === 'data';
    this.hasDateInterval = DATE_TYPES.includes(type);
    this.isFloat = FLOAT_TYPES.includes(type);
    this.changeSample();
  }

  onAliasChange(value) {
    this.artifactColumn.aliasName = value;
    this.change.emit({requiresDataChange: false});
  }

  onDateIntervalChange(value) {
    this.artifactColumn.dateInterval = value;
    if (this.artifactColumn.dateInterval !== 'day') {
      this.artifactColumn.format = DEFAULT_DATE_FORMAT.value;
    }
    this.change.emit({requiresDataChange: true});
  }

  onFormatChange(format: Format | string) {
    if (format) {
      this.artifactColumn.format = format;
      this.changeSample();
      this.change.emit({requiresDataChange: false});
    }
  }

  openDataFormatDialog() {
    this._analyzeDialogService.openDataFormatDialog(<Format>this.artifactColumn.format, this.artifactColumn.type)
      .afterClosed().subscribe(format => this.onFormatChange(format));
  }

  openDateFormatDialog() {
    this._analyzeDialogService.openDateFormatDialog(<string>this.artifactColumn.format)
      .afterClosed().subscribe(format => this.onFormatChange(format));
  }

  changeSample() {
    if (this.isDataField) {
      this.changeNumberSample();
    } else if (this.hasDateInterval) {
      this.changeDateSample();
    }
  }

  changeNumberSample() {
    const format = this.artifactColumn.format
    const sampleNr = this.isFloat ? FLOAT_SAMPLE : INT_SAMPLE;

    if ( format && isFormatted(<Format>format)) {
      this.numberSample = formatNumber(sampleNr, <Format>format);
    } else {
      this.numberSample = null;
    }
  }

  changeDateSample() {
    const format = <string>this.artifactColumn.format
    if (format) {
      this.dateSample = moment.utc().format(format);
    }
  }
}
