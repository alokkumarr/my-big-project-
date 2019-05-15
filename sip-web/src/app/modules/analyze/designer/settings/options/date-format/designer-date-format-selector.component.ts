import { Component, Input, EventEmitter, Output } from '@angular/core';
import * as get from 'lodash/get';
import { AnalyzeDialogService } from '../../../../services/analyze-dialog.service';
import { DesignerUpdateArtifactColumn } from '../../../actions/designer.actions';
import { Store } from '@ngxs/store';
import {
  ArtifactColumnChart,
  Format,
  DesignerChangeEvent
} from '../../../types';
import {
  CHART_DATE_FORMATS,
  // DATE_FORMATS,
  // DATE_FORMATS_OBJ,
  CHART_DATE_FORMATS_OBJ,
  PIVOT_DATE_FORMATS,
  PIVOT_DATE_FORMATS_OBJ
} from '../../../../consts';

const dateFormatsMap = {
  pivot: {
    array: PIVOT_DATE_FORMATS,
    obj: PIVOT_DATE_FORMATS_OBJ
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
export class DesignerDateFormatSelectorComponent {
  @Output() public change: EventEmitter<
    DesignerChangeEvent
  > = new EventEmitter();
  @Input() public artifactColumn: ArtifactColumnChart;
  @Input() public analysisType: string;

  public dateSample: string;

  constructor(
    private _analyzeDialogService: AnalyzeDialogService,
    private store: Store
  ) {}

  onFormatChange(format: Format | string) {
    if (format) {
      switch (this.analysisType) {
      case 'chart':
        this.artifactColumn.dateFormat = <string>format;
        const groupInterval = dateFormatsMap.chart.obj[format].groupInterval;

        this.store.dispatch(
          new DesignerUpdateArtifactColumn({
            columnName: this.artifactColumn.columnName,
            table: this.artifactColumn.table || this.artifactColumn.table,
            dateFormat: <string>format,
            groupInterval
          })
        );
        break;

      case 'pivot':
      this.artifactColumn.dateFormat = <string>format;

      this.store.dispatch(
        new DesignerUpdateArtifactColumn({
          columnName: this.artifactColumn.columnName,
          table: this.artifactColumn.table || this.artifactColumn.table,
          dateFormat: <string>format
        })
      );
      break;

      default:
      this.store.dispatch(
        new DesignerUpdateArtifactColumn({
          columnName: this.artifactColumn.columnName,
          table: this.artifactColumn.table || this.artifactColumn.table,
          format
        })
      );
      }
      this.change.emit({ subject: 'format' });
    }
  }

  getDateLabel(artifactColumn) {
    const dateFormatsObj = get(dateFormatsMap, `${this.analysisType}.obj`);
    return get(
      dateFormatsObj,
      `[${artifactColumn.dateFormat || artifactColumn.format}].label`,
      ''
    );
  }

  openDateFormatDialog() {
    const columnFormat = this.artifactColumn.dateFormat;
    const dateFormats = get(dateFormatsMap, `${this.analysisType}.array`);
    this._analyzeDialogService
      .openDateFormatDialog(<string>columnFormat, dateFormats)
      .afterClosed()
      .subscribe(format => this.onFormatChange(format));
  }
}
