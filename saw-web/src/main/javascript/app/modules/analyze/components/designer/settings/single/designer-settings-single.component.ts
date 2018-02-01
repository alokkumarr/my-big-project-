import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as filter from 'lodash/filter';
import * as debounce from 'lodash/debounce';
import * as get from 'lodash/get';

import { DesignerService } from '../../designer.service';
import {
  IDEsignerSettingGroupAdapter,
  ArtifactColumn,
  ArtifactColumns,
  ArtifactColumnFilter,
  ArtifactColumnPivot
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
  public unselectedArtifactColumns: ArtifactColumns;
  public groupAdapters: IDEsignerSettingGroupAdapter[];
  public filterObj: ArtifactColumnFilter = {
    keyword: '',
    types: ['number', 'date', 'string']
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

  ngOnChanges(changes) {
    if (get(changes, 'artifactColumns.currentValue')) {
      this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
    }
  }

  onFieldsChange() {
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
    this._changeSettingsDebounced();
  }

  onFieldPropChange() {
    this._changeSettingsDebounced();
  }

  _changeSettingsDebounced() {
    this.settingsChange.emit();
  }

  getUnselectedArtifactColumns() {
    return filter(this.artifactColumns, ({checked}) => !checked);
  }

  onTextFilterChange(value) {
    this.filterObj.keyword = value;
  }

  onTypeFilterChange(event) {
    const value = event.value;
    const checked = event.source.checked;

    if (!checked) {
      this.filterObj.types = filter(this.filterObj.types, type => type !== value);
    } else {
      this.filterObj.types = [...this.filterObj.types, value];
    }
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
      this.onFieldsChange();
    }
  }

  removeFromGroup(artifactColumn: ArtifactColumn, groupAdapter: IDEsignerSettingGroupAdapter) {
    this._designerService.removeArtifactColumnFromGroup(
      artifactColumn,
      groupAdapter
    );
    this.onFieldsChange();
  }

}
