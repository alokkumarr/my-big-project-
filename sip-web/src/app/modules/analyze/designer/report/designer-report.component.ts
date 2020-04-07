import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import { Artifact, DesignerChangeEvent, Sort, Filter } from '../types';
import { DesignerStates } from '../consts';
import { Store } from '@ngxs/store';

@Component({
  selector: 'designer-report',
  templateUrl: './designer-report.component.html',
  styleUrls: ['./designer-report.component.scss']
})
export class DesignerReportComponent {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Output() filterClick = new EventEmitter();
  @Input() artifacts: Artifact[];
  @Input() sorts: Sort[];
  @Input() analysis;
  @Input() hasSIPQuery: boolean;
  @Input() isInQueryMode;
  @Input() filters: Filter[];
  @Input() designerState: DesignerStates;
  @Input('data')
  set _data(val) {
    this.data = val;
    this.currentDataCount = Math.min(this.totalDataCount, (val || []).length);
  }

  constructor(public store: Store) {}

  get analysisArtifacts() {
    return this.analysis.designerEdit
      ? null
      : this.hasSIPQuery
      ? this.store.selectSnapshot(
          state => state.designerState.analysis.sipQuery.artifacts
        )
      : this.artifacts;
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
    this.change.emit({ subject: 'filterRemove', data: index.data });
  }

  onRemoveFilterAll() {
    this.filters.splice(0, this.filters.length);
    this.change.emit({ subject: 'filterRemove' });
  }

  onFiltersClick() {
    this.filterClick.emit();
  }
}
