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
import {
  TYPE_ICONS_OBJ,
  TYPE_ICONS
} from '../../../consts';

const template = require('./designer-settings.component.html');
require('./designer-settings.component.scss');

@Component({
  selector: 'designer-settings',
  template
})
export default class DesignerSettingsComponent {
  @Output() public onSettingsChange: EventEmitter<ArtifactColumnPivot[]> = new EventEmitter();
  @Input() public artifactColumns: ArtifactColumns;

  public unselectedArtifactColumns: ArtifactColumns
  public TYPE_ICONS_OBJ = TYPE_ICONS_OBJ;
  public TYPE_ICONS = TYPE_ICONS;
  public isUnselectedExpanded: boolean = false;
  public groupAdapters: IDEsignerSettingGroupAdapter[];
  public filterObj: ArtifactColumnFilter = {
    keyword: '',
    type: ''
  };

  constructor(private _designerService: DesignerService) {}

  ngOnInit() {
    this.groupAdapters = this._designerService.getPivotGroupAdapters(this.artifactColumns);
  }

  ngOnChanges() {
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
  }

  hideUnselectedSection() {
    this.isUnselectedExpanded = false;
  }

  expandUnselectedSection() {
    this.isUnselectedExpanded = true;
  }

  getUnselectedArtifactColumns() {
    return filter(this.artifactColumns, ({checked}) => !checked);
  }

  onTextFilterChange(change) {
    console.log('Textchange', change);
  }

  onTypeFilterChange(change) {
    console.log('Typechange', change);

  }

  addToGroupIfPossible(artifactColumn: ArtifactColumn) {
    const isAddSuccessful = this._designerService.addArtifactColumnIntoGroup(
      artifactColumn,
      this.groupAdapters
    );
    if (isAddSuccessful) {
      this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
      this.onSettingsChange.emit();
    }
  }

  removeFromGroup(artifactColumn: ArtifactColumn, groupAdapter: IDEsignerSettingGroupAdapter) {
    console.log('remove this shit', artifactColumn);
    this._designerService.removeArtifactColumnFromGroup(
      artifactColumn,
      groupAdapter
    );
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
    this.onSettingsChange.emit();
  }

}
