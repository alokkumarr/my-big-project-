import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { ArtifactColumnPivot, DesignerChangeEvent } from '../../../types';
import { DATE_INTERVALS, DATE_INTERVALS_OBJ } from '../../../../consts';
import { DesignerUpdateArtifactColumn } from './../../../actions/designer.actions';
import { Store } from '@ngxs/store';

@Component({
  selector: 'designer-date-interval-selector',
  templateUrl: 'designer-date-interval-selector.component.html'
})
export class DesignerDateIntervalSelectorComponent implements OnInit {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() artifactColumn: ArtifactColumnPivot;
  public DATE_INTERVALS = DATE_INTERVALS;

  constructor(private _store: Store) {}

  ngOnInit() {}

  onDateIntervalChange(groupInterval) {
    const dateFormat = DATE_INTERVALS_OBJ[groupInterval].formatForBackEnd;
    this._store.dispatch(
      new DesignerUpdateArtifactColumn({
        columnName: this.artifactColumn.columnName,
        dataField: this.artifactColumn.dataField,
        table: this.artifactColumn.table,
        groupInterval,
        dateFormat
      })
    );
    this.change.emit({ subject: 'dateInterval' });
  }
}
