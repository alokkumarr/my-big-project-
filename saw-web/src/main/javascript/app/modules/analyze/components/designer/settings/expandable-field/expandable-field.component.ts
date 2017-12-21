import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  ArtifactColumn,
  AnalysisType
}  from '../../types';
import { TYPE_ICONS_OBJ } from '../../../../consts';

const template = require('./expandable-field.component.html');
require('./expandable-field.component.scss');

@Component({
  selector: 'expandable-field',
  template
})
export class ExpandableFieldComponent {
  @Output() public change: EventEmitter<ArtifactColumn> = new EventEmitter();
  @Output() public removeRequest: EventEmitter<null> = new EventEmitter();
  @Input() public artifactColumn: ArtifactColumn;
  @Input() public analysisType: AnalysisType;

  public isExpanded = false;
  public TYPE_ICONS_OBJ = TYPE_ICONS_OBJ;

  toggleExpansion() {
    this.isExpanded = !this.isExpanded;
  }
}
