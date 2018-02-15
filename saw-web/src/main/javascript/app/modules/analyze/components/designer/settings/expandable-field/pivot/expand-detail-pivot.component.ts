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
  DATE_TYPES
} from '../../../../../consts';
import { AnalyzeDialogService } from '../../../../../services/analyze-dialog.service'
import { formatNumber } from '../../../../../../../common/utils/numberFormatter';

const template = require('./expand-detail-pivot.component.html');

const SAMPLE_NUMBER = 1000.33333;
@Component({
  selector: 'expand-detail-pivot',
  template
})
export class ExpandDetailPivotComponent {
  @Output() public change: EventEmitter<ArtifactColumnPivot> = new EventEmitter();

  @Input() public artifactColumn: ArtifactColumnPivot;

  public AGGREGATE_TYPES = AGGREGATE_TYPES;
  public DATE_INTERVALS = DATE_INTERVALS;
  public isDataField: boolean = false;
  public hasDateInterval: boolean = false;
  public sample: string;

  constructor(
    private _analyzeDialogService: AnalyzeDialogService
  ) {}

  ngOnInit() {
    this.isDataField = this.artifactColumn.area === 'data';
    this.hasDateInterval = DATE_TYPES.includes(this.artifactColumn.type);
    this.changeSample();
  }

  onAliasChange(value) {
    this.artifactColumn.aliasName = value;
    this.change.emit(this.artifactColumn);
  }

  onDateIntervalChange(value) {
    this.artifactColumn.dateInterval = value;
    this.change.emit(this.artifactColumn);
  }

  onFormatChange(format: Format) {
    if (format) {
      this.artifactColumn.format = format;
      this.changeSample();
      this.change.emit(this.artifactColumn);
    }
  }

  openFormatDialog() {
    this._analyzeDialogService.openFormatDialog(this.artifactColumn.format)
      .afterClosed().subscribe(format => this.onFormatChange(format));
  }

  changeSample() {
    this.sample = formatNumber(SAMPLE_NUMBER, this.artifactColumn.format);
  }
}
