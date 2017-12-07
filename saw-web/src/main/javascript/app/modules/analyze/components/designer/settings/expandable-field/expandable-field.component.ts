import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  ArtifactColumn,
  IDEsignerSettingGroupAdapter
}  from '../../types';
import { TYPE_ICONS_OBJ } from '../../../../consts';

const template = require('./expandable-field.component.html');
require('./expandable-field.component.scss');

@Component({
  selector: 'expandable-field',
  template
})
export class ExpandableFieldComponent {
  // @Output() public onSettingsChange: EventEmitter<ArtifactColumns[]> = new EventEmitter();
  @Output() public moveRequest: EventEmitter<'up' | 'down'> = new EventEmitter();
  @Output() public removeRequest: EventEmitter<null> = new EventEmitter();
  @Input() public artifactColumn :ArtifactColumn;

  public isExpanded = false;
  public TYPE_ICONS_OBJ = TYPE_ICONS_OBJ;

  toggleExpansion() {
    this.isExpanded = !this.isExpanded;
  }
}
