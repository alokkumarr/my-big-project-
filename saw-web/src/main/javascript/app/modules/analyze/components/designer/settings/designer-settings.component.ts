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
  ArtifactColumnPivot,
  IMoveFieldToEvent,
  IMoveFieldFromEvent
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
  @Output() public settingsChange: EventEmitter<ArtifactColumnPivot[]> = new EventEmitter();
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

  private _isDragging: boolean = false;
  private _moveEventAccumulator: {
    to: IMoveFieldToEvent,
    from: IMoveFieldFromEvent
  } = {
    to: null,
    from: null
  };

  public dndSortableContainerObj = {
    // the zone can be any value as long as it's different than the zone of the sortables in it
    // so that you can't sort the unselected artifactColumns
    zone: 'zone'
  };

  constructor(private _designerService: DesignerService) {}

  ngOnInit() {
    this.groupAdapters = this._designerService.getPivotGroupAdapters(this.artifactColumns);
  }

  ngOnChanges() {
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
  }

  changeSettings() {
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
    this.settingsChange.emit();
  }

  hideUnselectedSection() {
    this.isUnselectedExpanded = false;
  }

  expandUnselectedSection() {
    if (this._isDragging) {
      // don't expand unselected section while dragging
      return;
    }
    this.isUnselectedExpanded = true;
  }

  onDrag() {
    this._isDragging = true;
    this.hideUnselectedSection();
  }

  onDragEnd(event) {
    this._isDragging = false;
    if (event.isDropSuccessful && event.didContainerChange) {
      const artifactColumn = <ArtifactColumn> event.data;
      this.onMove({
        name: 'moveFrom',
        artifactColumn,
        fromGroup: null
      });
    }
    if (event.isDropSuccessful) {
      this.changeSettings();
    }
  }

  onMove(event) {
    // because the onDragEnd event fires after the onDrop event
    // the moveFrom coms after the moveTo event
    // however we need the information from the moveFrom event first to take out the element
    // from the old group and then insert it into the new one
    switch (event.name) {
    case 'moveTo':
      this._moveEventAccumulator.to = event;
      break;
    case 'moveFrom':
      this._moveEventAccumulator.from = event;
      break;
    }
    if (this._moveEventAccumulator.from && this._moveEventAccumulator.to) {
      const fromGroup = this._moveEventAccumulator.from.fromGroup;
      const {
        toGroup,
        toIndex,
        artifactColumn
      } = this._moveEventAccumulator.to;
      // remove from old group, if it was dragged from a group
      // do nothing if it was dragged from the unselected fields
      if (fromGroup) {
        this._designerService.removeArtifactColumnFromGroup(
          artifactColumn,
          fromGroup
        );
      }
      // add to new group
      this._designerService.addArtifactColumnIntoGroup(
        artifactColumn,
        toGroup,
        toIndex
      );
      // clear event Acumulator
      this._moveEventAccumulator = {
        to: null,
        from: null
      };
      this.changeSettings();
    }
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

  /**
   * Add artifactColumn to the first group that can accept it, if possible
   */
  addToGroupIfPossible(artifactColumn: ArtifactColumn) {
    const isAddSuccessful = this._designerService.addArtifactColumnIntoAGroup(
      artifactColumn,
      this.groupAdapters
    );
    if (isAddSuccessful) {
      this.changeSettings();
    }
  }

  removeFromGroup(artifactColumn: ArtifactColumn, groupAdapter: IDEsignerSettingGroupAdapter) {
    this._designerService.removeArtifactColumnFromGroup(
      artifactColumn,
      groupAdapter
    );
    this.changeSettings();
  }

}
