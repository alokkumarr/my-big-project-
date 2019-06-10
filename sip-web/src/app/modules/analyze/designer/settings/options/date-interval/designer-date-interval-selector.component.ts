import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';
import {ArtifactColumnPivot, DesignerChangeEvent} from '../../../types';
import {DATE_INTERVALS, PIVOT_DATE_FORMATS} from '../../../../consts';
import {DesignerUpdateArtifactColumn} from './../../../actions/designer.actions';
import {Store} from '@ngxs/store';

@Component({
  selector: 'designer-date-interval-selector',
  templateUrl: 'designer-date-interval-selector.component.html'
})
export class DesignerDateIntervalSelectorComponent implements OnInit {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() artifactColumn: ArtifactColumnPivot;
  public DATE_INTERVALS = DATE_INTERVALS;

  constructor(private _store: Store) {
  }

  ngOnInit() {
  }

  onDateIntervalChange(groupInterval) {
    // if (this.artifactColumn.dateInterval !== 'day') {
    //   format = DEFAULT_DATE_FORMAT.value;
    // }
    const monthFormat = PIVOT_DATE_FORMATS[4].value;
    this._store.dispatch(
      new DesignerUpdateArtifactColumn({
        columnName: this.artifactColumn.columnName,
        table: this.artifactColumn.table,
        groupInterval,
        ...{dateFormat: groupInterval === 'month' ? monthFormat : null}
      })
    );
    this.change.emit({subject: 'dateInterval'});
  }
}
