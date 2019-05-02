import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ArtifactColumnPivot, DesignerChangeEvent } from '../../../types';
import { DATE_INTERVALS, DEFAULT_DATE_FORMAT } from '../../../../consts';
import { DesignerUpdatePivotGroupIntreval } from './../../../actions/designer.actions';
import { Store } from '@ngxs/store';

@Component({
  selector: 'designer-date-interval-selector',
  templateUrl: 'designer-date-interval-selector.component.html'
})
export class DesignerDateIntervalSelectorComponent implements OnInit {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() artifactColumn: ArtifactColumnPivot;
  public DATE_INTERVALS = DATE_INTERVALS;
  constructor(
    private _store: Store,
  ) {}

  ngOnInit() {
  }

  onDateIntervalChange(value) {
    this.artifactColumn.dateInterval = value;
    if (this.artifactColumn.dateInterval !== 'day') {
      this.artifactColumn.format = DEFAULT_DATE_FORMAT.value;
    }
    this._store.dispatch(
      new DesignerUpdatePivotGroupIntreval(this.artifactColumn)
    );
    this.change.emit({ subject: 'dateInterval' });
  }
}
