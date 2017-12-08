import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  ArtifactColumn,
  ArtifactColumns,
  IDEsignerSettingGroupAdapter,
  IMoveFieldToEvent,
  IMoveFieldFromEvent
}  from '../../types';
import { TYPE_ICONS_OBJ } from '../../../../consts';
import { DesignerService } from '../../designer.service';

const template = require('./designer-settings-group.component.html');
require('./designer-settings-group.component.scss');

@Component({
  selector: 'designer-settings-group',
  template
})
export default class DesignerSettingsGroupComponent {
  // @Output() public onSettingsChange: EventEmitter<ArtifactColumns[]> = new EventEmitter();
  @Output() public moveFrom: EventEmitter<IMoveFieldFromEvent> = new EventEmitter();
  @Output() public moveTo: EventEmitter<IMoveFieldToEvent> = new EventEmitter();
  @Output() public removeField: EventEmitter<ArtifactColumn> = new EventEmitter();
  @Input() public artifactColumns :ArtifactColumns;
  @Input() public groupAdapter :IDEsignerSettingGroupAdapter;

  public dndSortableContainerObj = {};

  public TYPE_ICONS_OBJ = TYPE_ICONS_OBJ;

  constructor(private _designerService: DesignerService) {}

  ngOnInit() {
    this.dndSortableContainerObj = {
      allowDropFn: this.groupAdapter.canAcceptArtifactColumn
    };
  }

  /**
   * Drop event for sortable dropped in this group
   */
  onDrop(event) {
    if (event.didContainerChange) {
      const artifactColumn = <ArtifactColumn> event.data;
      this.moveTo.emit({
        name: 'moveTo',
        artifactColumn,
        toIndex: event.index,
        toGroup: this.groupAdapter
      });
    }
  }

  /**
   * Dragend event from a sortable taken from this group
   */
  onDragEnd(event) {
    if (event.isDropSuccessful && event.didContainerChange) {
      const artifactColumn = <ArtifactColumn> event.data;
      this.moveFrom.emit({
        name: 'moveFrom',
        artifactColumn,
        fromGroup: this.groupAdapter
      });
    }
  }

  onRemoveField(artifactColumn: ArtifactColumn) {
    this.removeField.emit(artifactColumn);
  }

  trackByFn(_, artifactColumn: ArtifactColumn) {
    return artifactColumn.columnName;
  }
}
