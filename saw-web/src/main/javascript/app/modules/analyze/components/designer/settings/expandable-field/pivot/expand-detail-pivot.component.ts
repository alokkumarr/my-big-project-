import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  ArtifactColumnPivot
}  from '../../../types';
import {
  TYPE_ICONS_OBJ,
  AGGREGATE_TYPES,
  DATE_INTERVALS,
  DATE_TYPES
} from '../../../../../consts';

const template = require('./expand-detail-pivot.component.html');

@Component({
  selector: 'expand-detail-pivot',
  template
})
export class ExpandDetailPivotComponent {
  @Output() public change: EventEmitter<ArtifactColumnPivot> = new EventEmitter();

  @Input() public artifactColumn: ArtifactColumnPivot;

  public AGGREGATE_TYPES = AGGREGATE_TYPES;
  public DATE_INTERVALS = DATE_INTERVALS;
  public hasAggregate: boolean = false;
  public hasDateInterval: boolean = false;

  ngOnInit() {
    this.hasAggregate = this.artifactColumn.area === 'data';
    this.hasDateInterval = DATE_TYPES.includes(this.artifactColumn.type);
  }

  onAliasChange(value) {
    this.artifactColumn.aliasName = value;
    this.change.emit(this.artifactColumn);
  }

  onDateIntervalChange(value) {
    this.artifactColumn.dateInterval = value;
    this.change.emit(this.artifactColumn);
  }
}
