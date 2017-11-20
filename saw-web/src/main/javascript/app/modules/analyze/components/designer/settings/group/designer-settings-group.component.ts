import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  ArtifactColumns,
  IDEsignerSettingGroupAdapter
}  from '../../types';

const template = require('./designer-settings-group.component.html');
require('./designer-settings-group.component.scss');

@Component({
  selector: 'designer-settings-group',
  template
})
export default class DesignerSettingsGroupComponent {
  // @Output() public onSettingsChange: EventEmitter<ArtifactColumns[]> = new EventEmitter();
  @Input() public artifactColumns :ArtifactColumns;
  @Input() public title :string;

  onDrop(artifactColumn) {
    // verify if acceptable
    // add new Artifactcolumn
  }
}
