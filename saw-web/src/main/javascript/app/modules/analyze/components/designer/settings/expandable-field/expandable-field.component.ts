declare const require: any;
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { ArtifactColumn, ArtifactColumnChart, AnalysisType } from '../../types';
import {
  TYPE_ICONS_OBJ,
  AGGREGATE_TYPES,
  AGGREGATE_TYPES_OBJ,
  COMBO_TYPES,
  COMBO_TYPES_OBJ
} from '../../../../consts';

const template = require('./expandable-field.component.html');
require('./expandable-field.component.scss');
import { FieldChangeEvent } from '../single';
@Component({
  selector: 'expandable-field',
  template
})
export class ExpandableFieldComponent {
  @Output() public change: EventEmitter<FieldChangeEvent> = new EventEmitter();
  @Output() public removeRequest: EventEmitter<null> = new EventEmitter();
  @Input() public artifactColumn: ArtifactColumn;
  @Input() public analysisType: AnalysisType;

  COMBO_TYPES_OBJ = COMBO_TYPES_OBJ;
  COMBO_TYPES = COMBO_TYPES;
  public isExpanded = false;
  public TYPE_ICONS_OBJ = TYPE_ICONS_OBJ;
  public AGGREGATE_TYPES = AGGREGATE_TYPES;
  public AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;

  toggleExpansion() {
    this.isExpanded = !this.isExpanded;
  }

  onAggregateChange(value) {
    this.artifactColumn.aggregate = value;
    this.change.emit({ requiresDataChange: true });
  }

  onComboTypeChange(comboType) {
    (this.artifactColumn as ArtifactColumnChart).comboType = comboType;
    this.change.emit({ requiresDataChange: false });
  }
}
