declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  ArtifactColumnPivot,
  Format
}  from '../../../types';
import {
  TYPE_ICONS_OBJ,
  AGGREGATE_TYPES,
  DATE_INTERVALS,
  DATE_TYPES,
  FLOAT_TYPES
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
  public isDataField: boolean = false;
  public hasDateInterval: boolean = false;
  public sample: string;
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
    this.change.emit({requiresDataChange: true});
  }

  onFormatChange(format: Format) {
    if (format) {
      this.artifactColumn.format = format;
      this.changeSample();
      this.change.emit({requiresDataChange: false});
    }
  }

  openFormatDialog() {
    this._analyzeDialogService.openDataFormatDialog(this.artifactColumn.format, this.artifactColumn.type)
      .afterClosed().subscribe(format => this.onFormatChange(format));
  }

  changeSample() {
    const format = this.artifactColumn.format
    const sampleNr = this.isFloat ? FLOAT_SAMPLE : INT_SAMPLE;

    if ( format && isFormatted(format)) {
      this.sample = formatNumber(sampleNr, format);
    } else {
      this.sample = null;
    }
  }
}
