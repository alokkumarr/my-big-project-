import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as filter from 'lodash/filter';
import * as debounce from 'lodash/debounce';

import { DesignerService } from '../../designer.service';
import {
  IDEsignerSettingGroupAdapter,
  ArtifactColumn,
  ArtifactColumns,
  ArtifactColumnFilter,
  ArtifactColumnPivot,
  IMoveFieldToEvent,
  IMoveFieldFromEvent
} from '../../types';
import {
  TYPE_ICONS_OBJ,
  TYPE_ICONS
} from '../../../../consts';

const template = require('./designer-settings-single.component.html');
require('./designer-settings-single.component.scss');

const SETTINGS_CHANGE_DEBOUNCE_TIME = 500;
const FILTER_CHANGE_DEBOUNCE_TIME = 300;

/**
 * Designer settings component for designers with a single set of artifactColumns like
 * pivot or chart
 */
@Component({
  selector: 'designer-settings-single',
  template
})
export class DesignerSettingsSingleComponent {
  @Output() public settingsChange: EventEmitter<ArtifactColumnPivot[]> = new EventEmitter();
  @Input() public artifactColumns: ArtifactColumns;

  public TYPE_ICONS_OBJ = TYPE_ICONS_OBJ;
  public TYPE_ICONS = TYPE_ICONS;
  public unselectedArtifactColumns: ArtifactColumns
  public isUnselectedExpanded: boolean = false;
  public groupAdapters: IDEsignerSettingGroupAdapter[];
  public filterObj: ArtifactColumnFilter = {
    keyword: '',
    type: ''
  };
  public dndSortableContainerObj = {
    // the zone can be any value as long as it's different than the zone of the sortables in it
    // so that you can't sort the unselected artifactColumns
    zone: 'zone'
  };
  private _isDragging: boolean = false;
  private _moveEventAccumulator: {
    to: IMoveFieldToEvent,
    from: IMoveFieldFromEvent
  } = {
    to: null,
    from: null
  };


  constructor(private _designerService: DesignerService) {
    // we have to debounce settings change
    // so that the pivot grid or chart designer
    // doesn't have to process everything with every quick change
    this._changeSettingsDebounced = debounce(
      this._changeSettingsDebounced,
      SETTINGS_CHANGE_DEBOUNCE_TIME
    );

    this.onTextFilterChange = debounce(
      this.onTextFilterChange,
      FILTER_CHANGE_DEBOUNCE_TIME
    );
  }

  ngOnInit() {
    this.groupAdapters = this._designerService.getPivotGroupAdapters(this.artifactColumns);
  }

  ngOnChanges() {
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
  }

  _changeSettings() {
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
    this._changeSettingsDebounced();
  }

  _changeSettingsDebounced() {
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
    if (event.isDropSuccessful) {
      const artifactColumn = <ArtifactColumn> event.data;
      this.onMove({
        name: 'moveFrom',
        artifactColumn,
        fromGroup: null
      });
    }
    // if (event.isDropSuccessful) {
    //   this._changeSettings();
    // }
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
      const {
        fromGroup,
        fromIndex
      } = this._moveEventAccumulator.from;
      const {
        toGroup,
        toIndex,
        artifactColumn
      } = this._moveEventAccumulator.to;
      // remove from old group, if it was dragged from a group
      // do nothing if it was dragged from the unselected fields
      if (fromGroup) {
        console.log('remove');
        this._designerService.removeArtifactColumnFromGroup(
          artifactColumn,
          fromGroup
        );
      }
      // add to new group
      console.log('add to ', toIndex);
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
      this._changeSettings();
    }
  }

  getUnselectedArtifactColumns() {
    return filter(this.artifactColumns, ({checked}) => !checked);
  }

  onTextFilterChange(value) {
    this.filterObj.keyword = value;
  }

  onTypeFilterChange(change) {
    this.filterObj.type = change.value;
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
      this._changeSettings();
    }
  }

  removeFromGroup(artifactColumn: ArtifactColumn, groupAdapter: IDEsignerSettingGroupAdapter) {
    this._designerService.removeArtifactColumnFromGroup(
      artifactColumn,
      groupAdapter
    );
    this._changeSettings();
  }

}
