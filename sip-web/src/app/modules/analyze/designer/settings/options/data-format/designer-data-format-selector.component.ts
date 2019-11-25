import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import * as cloneDeep from 'lodash/cloneDeep';
import {
  formatNumber,
  isFormatted
} from '../../../../../../common/utils/numberFormatter';
import {
  ArtifactColumnChart,
  Format,
  DesignerChangeEvent
} from '../../../types';
import { AnalyzeDialogService } from '../../../../services/analyze-dialog.service';

const FLOAT_SAMPLE = 1000.33333;
const INT_SAMPLE = 1000;

@Component({
  selector: 'designer-data-format-selector',
  templateUrl: 'designer-data-format-selector.component.html'
})
export class DesignerDataFormatSelectorComponent implements OnInit {
  @Output() public change: EventEmitter<
    DesignerChangeEvent
  > = new EventEmitter();
  @Input() public artifactColumn: ArtifactColumnChart;

  public numberSample: string;
  public isFloat: boolean;
  constructor(private _analyzeDialogService: AnalyzeDialogService) {}

  ngOnInit() {
    this.changeNumberSample();
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

  onFormatChange(format: Format | string) {
    if (format) {
      this.artifactColumn.format = format;
      this.changeNumberSample();
      this.change.emit({ subject: 'format' });
    }
  }

  openDataFormatDialog() {
    const clonedFormat = cloneDeep(this.artifactColumn.format);
    this._analyzeDialogService
      .openDataFormatDialog(clonedFormat, this.artifactColumn.type)
      .afterClosed()
      .subscribe(format => this.onFormatChange(format));
  }
}
