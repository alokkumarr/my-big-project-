import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as filter from 'lodash/filter';
import {
  ArtifactColumnPivot,
  ArtifactColumns
} from '../types';
import {
  NUMBER_TYPES,
  DATE_TYPES,
  TYPE_ICONS_OBJ,
  MAX_POSSIBLE_FIELDS_OF_SAME_AREA,
  AGGREGATE_TYPES,
  DEFAULT_AGGREGATE_TYPE,
  AGGREGATE_TYPES_OBJ,
  AREA_TYPES,
  AREA_TYPES_OBJ,
  DATE_INTERVALS,
  DEFAULT_DATE_INTERVAL,
  DATE_INTERVALS_OBJ
} from '../../../consts';

const template = require('./old-designer-settings.component.html');
require('./old-designer-settings.component.scss');

@Component({
  selector: 'old-designer-settings',
  template
})
export class OldDesignerSettingsComponent {
  @Output() public onSettingsChange: EventEmitter<ArtifactColumnPivot[]> = new EventEmitter();
  @Input() public artifactColumns: ArtifactColumns;

  public TYPE_ICONS_OBJ = TYPE_ICONS_OBJ;
  public DATE_TYPES = DATE_TYPES;
  public AREA_TYPES = AREA_TYPES;
  public AREA_TYPES_OBJ = AREA_TYPES_OBJ;
  public AGGREGATE_TYPES = AGGREGATE_TYPES;
  public AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;
  public DATE_INTERVALS = DATE_INTERVALS;
  public DATE_INTERVALS_OBJ = DATE_INTERVALS_OBJ;

  canBeChecked(artifactColumn: ArtifactColumnPivot) {
    // only 5 fields of the same type can be selected at a time
    const columnsWithSameArea = filter(this.artifactColumns,
      ({area, checked}) => checked && (artifactColumn.area === area));
    return columnsWithSameArea.length <= MAX_POSSIBLE_FIELDS_OF_SAME_AREA;
  }

  onCheckToggle(artifactColumn: ArtifactColumnPivot) {
    if (!this.canBeChecked(artifactColumn)) {
      return;
    }
    artifactColumn.checked = !artifactColumn.checked;
    if (artifactColumn.checked) {
      this.transform(artifactColumn);
    } else {
      this.reverseTransform(artifactColumn);
    }
    this.onSettingsChange.emit();
  }

  transform(artifactColumn: ArtifactColumnPivot) {
    if (NUMBER_TYPES.includes(artifactColumn.type)) {
      artifactColumn.area = 'data';
      artifactColumn.aggregate = DEFAULT_AGGREGATE_TYPE.value;
    } else {
      artifactColumn.area = 'column';
      if (DATE_TYPES.includes(artifactColumn.type)) {
        artifactColumn.dateInterval = DEFAULT_DATE_INTERVAL.value;
      }
    }
  }

  reverseTransform(artifactColumn: ArtifactColumnPivot) {
    artifactColumn.area = null;
    artifactColumn.aggregate = null;
    artifactColumn.dateInterval = null;
  }

  onSelectAreaType(areaType, artifactColumn: ArtifactColumnPivot) {
    artifactColumn.area = areaType;
    this.onSettingsChange.emit();
  }

  onSelectAggregateType(aggregateType, artifactColumn: ArtifactColumnPivot) {
    artifactColumn.aggregate = aggregateType;
    this.onSettingsChange.emit();
  }

  onSelectDateInterval(dateInterval, artifactColumn: ArtifactColumnPivot) {
    artifactColumn.dateInterval = dateInterval;
    this.onSettingsChange.emit();
  }

  menuTrackByFn(_, item) {
    return item.value;
  }
}
