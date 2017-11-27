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
import { TYPE_ICONS_OBJ } from '../../../../consts';

const template = require('./designer-settings-group.component.html');
require('./designer-settings-group.component.scss');

@Component({
  selector: 'designer-settings-group',
  template
})
export default class DesignerSettingsGroupComponent {
  // @Output() public onSettingsChange: EventEmitter<ArtifactColumns[]> = new EventEmitter();
  @Input() public artifactColumns :ArtifactColumns;
  @Input() public groupAdapter :IDEsignerSettingGroupAdapter;

  public sortableContainerOptions = {};
  public TYPE_ICONS_OBJ = TYPE_ICONS_OBJ;

  ngOnInit() {
    this.sortableContainerOptions = {
      allowDropFn: this.groupAdapter.canAcceptArtifactColumn
    }
  }

  onDrop(artifactColumn) {
    // verify if acceptable
    // add new Artifactcolumn
    this.groupAdapter.transform(artifactColumn);
  }
}
