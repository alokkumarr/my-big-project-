import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  ArtifactColumn,
  ArtifactColumns,
  IDEsignerSettingGroupAdapter
}  from '../../types';
import { TYPE_ICONS_OBJ } from '../../../../consts';

const template = require('./designer-settings-group.component.html');
require('./designer-settings-group.component.scss');

@Component({
  selector: 'designer-settings-group',
  template
})
export default class DesignerSettingsGroupComponent {
  // @Output() public onSettingsChange: EventEmitter<ArtifactColumns[]> = new EventEmitter();
  @Output() public removeField: EventEmitter<ArtifactColumn> = new EventEmitter();
  @Input() public artifactColumns :ArtifactColumns;
  @Input() public groupAdapter :IDEsignerSettingGroupAdapter;

  public TYPE_ICONS_OBJ = TYPE_ICONS_OBJ;

  onMoveField(direction, index) {

  }

  onRemoveField(artifactColumn: ArtifactColumn) {
    this.removeField.emit(artifactColumn);
  }
}
