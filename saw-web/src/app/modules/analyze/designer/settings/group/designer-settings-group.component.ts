import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {
  ArtifactColumn,
  ArtifactColumns,
  IDEsignerSettingGroupAdapter,
  DesignerChangeEvent
} from '../../types';
import { TYPE_ICONS_OBJ } from '../../../consts';
import { DesignerService } from '../../designer.service';

@Component({
  selector: 'designer-settings-group',
  templateUrl: './designer-settings-group.component.html',
  styleUrls: ['./designer-settings-group.component.scss']
})
export class DesignerSettingsGroupComponent implements OnInit {
  @Output() public fieldsChange: EventEmitter<null> = new EventEmitter();
  @Output()
  public fieldPropChange: EventEmitter<
    DesignerChangeEvent
  > = new EventEmitter();
  @Output()
  public removeField: EventEmitter<ArtifactColumn> = new EventEmitter();
  @Input() public artifactColumns: ArtifactColumns;
  @Input() public groupAdapter: IDEsignerSettingGroupAdapter;
  @Input() public groupAdapters: Array<IDEsignerSettingGroupAdapter>;
  @Input() fieldCount: number;
  @Input() analysisSubtype: string;
  @Input() public sqlBuilder;

  public dndSortableContainerObj = {};
  public allowDropFn;

  public TYPE_ICONS_OBJ = TYPE_ICONS_OBJ;

  public removeFromCallback = function(payload, index, container) {
    this._designerService.removeArtifactColumnFromGroup(payload, container);
    this.fieldsChange.emit();
  }.bind(this);

  public addToCallback = function(payload, index, container) {
    this._designerService.addArtifactColumnIntoGroup(payload, container, index);
    this.fieldsChange.emit();
  }.bind(this);

  constructor(public _designerService: DesignerService) {}

  ngOnInit() {
    this.allowDropFn = this.groupAdapter.canAcceptArtifactColumn(
      this.groupAdapter,
      this.groupAdapters
    );
  }

  onRemoveField(artifactColumn: ArtifactColumn) {
    artifactColumn.aliasName = '';
    this.removeField.emit(artifactColumn);
  }

  trackByFn(_, artifactColumn: ArtifactColumn) {
    return artifactColumn.columnName;
  }
}
