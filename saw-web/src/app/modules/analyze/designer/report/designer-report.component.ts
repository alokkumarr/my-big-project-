import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import { Artifact, DesignerChangeEvent, Sort, Filter } from '../types';
import { DesignerStates } from '../consts';

@Component({
  selector: 'designer-report',
  templateUrl: './designer-report.component.html',
  styleUrls: ['./designer-report.component.scss']
})
export class DesignerReportComponent {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() artifacts: Artifact[];
  @Input() sorts: Sort[];
  @Input() analysis;
  @Input() isInQueryMode;
  @Input() filters: Filter[];
  @Input() designerState: DesignerStates;
  @Input('data')
  set _data(val) {
    this.data = val;
    this.currentDataCount = Math.min(this.totalDataCount, (val || []).length);
  }

  @Input('dataCount')
  set dataCount(count) {
    this.totalDataCount = count || 0;
    this.currentDataCount = Math.min(
      this.totalDataCount,
      (this.data || []).length
    );
  }

  public data;
  public DesignerStates = DesignerStates;
  public isEmpty = isEmpty;
  public totalDataCount: number;
  public currentDataCount: number;

  onReportGridChange(event) {
    this.change.emit(event);
  }

  onRemoveFilter(index) {
    this.filters.splice(index, 1);
    this.change.emit({ subject: 'filterRemove' });
  }

  onRemoveFilterAll() {
    this.filters.splice(0, this.filters.length);
    this.change.emit({ subject: 'filterRemove' });
  }
}
