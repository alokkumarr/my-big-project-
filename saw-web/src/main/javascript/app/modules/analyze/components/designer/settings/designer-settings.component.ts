import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as filter from 'lodash/filter';

import { DesignerService } from '../designer.service';
import {
  IDEsignerSettingGroupAdapter,
  ArtifactColumn,
  ArtifactColumns,
  ArtifactColumnFilter,
  ArtifactColumnPivot
} from '../types';
import { TYPE_ICONS_OBJ } from '../../../consts';

const template = require('./designer-settings.component.html');
require('./designer-settings.component.scss');

@Component({
  selector: 'designer-settings',
  template
})
export default class DesignerSettingsComponent {
  @Output() public onSettingsChange: EventEmitter<ArtifactColumnPivot[]> = new EventEmitter();
  @Input() public artifactColumns: ArtifactColumns;

  private _isDraggingInProgress = false;
  public unselectedArtifactColumns: ArtifactColumns
  public TYPE_ICONS_OBJ = TYPE_ICONS_OBJ;
  public isUnselectedExpanded: boolean = false;
  public groupAdapters: IDEsignerSettingGroupAdapter[];
  public sortableContainerOptions = {};
  public filterObj: ArtifactColumnFilter = {
    keyword: '',
    type: ''
  };

  constructor(private _designerService: DesignerService) {}

  ngOnInit() {
    this.groupAdapters = this._designerService.getPivotGroupAdapters(this.artifactColumns);
    this.sortableContainerOptions = {
      // this can be anything as long az the soortable unselected artifactColumns
      // don't have the same zone, so that the elements cannot be dropped back here.
      zone: 'zone'
    }
  }

  ngOnChanges() {
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
  }

  onDrag() {
    this._isDraggingInProgress = true;
    this.hideUnselectedSection();
  }

  onDragEnd(event) {
    this._isDraggingInProgress = false;
    if (event.isDropSuccessful && event.isSortableDroppedInOtherContainer) {
      this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
    }
  }

  hideUnselectedSection() {
    this.isUnselectedExpanded = false;
  }

  expandUnselectedSection() {
    // fix a bug with unselectedSection expanding while
    // dragging is in progress
    if (this._isDraggingInProgress) {
      return;
    }
    this.isUnselectedExpanded = true;
  }

  getUnselectedArtifactColumns() {
    return filter(this.artifactColumns, ({checked}) => !checked);
  }

  onSuccessfulSelect() {
    this.onSettingsChange.emit();
  }

  addToGroupIfPossible(artifactColumn: ArtifactColumn) {
    this._designerService.addArtifactColumnIntoGroup(
      artifactColumn,
      this.groupAdapters
    );
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
  }

}
