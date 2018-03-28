declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as filter from 'lodash/filter';
import * as debounce from 'lodash/debounce';
import * as get from 'lodash/get';
import * as isEmpty from 'lodash/isEmpty';

import { DesignerService } from '../../designer.service';
import {
  IDEsignerSettingGroupAdapter,
  Artifact,
  ArtifactColumn,
  ArtifactColumns,
  ArtifactColumnFilter,
  ArtifactColumnPivot,
  DesignerChangeEvent
} from '../../types';
import {
  TYPE_ICONS_OBJ,
  TYPE_ICONS,
  TYPE_MAP
} from '../../../../consts';

const template = require('./designer-settings-single-table.component.html');
require('./designer-settings-single-table.component.scss');

const SETTINGS_CHANGE_DEBOUNCE_TIME = 500;
const FILTER_CHANGE_DEBOUNCE_TIME = 300;

/**
 * Designer settings component for designers with a single set of artifactColumns like
 * pivot or chart
 */
@Component({
  selector: 'designer-settings-single-table',
  template
})
export class DesignerSettingsSingleTableComponent {
  @Output() public settingsChange: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input('artifacts') public set setArtifactColumns(artifacts: Artifact[]) {
    if (!isEmpty(artifacts)) {
      this.artifactColumns = artifacts[0].columns;
      this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
    }
  };

  public TYPE_ICONS_OBJ = TYPE_ICONS_OBJ;
  public TYPE_ICONS = TYPE_ICONS;
  public isEmpty = isEmpty;
  public artifactColumns: ArtifactColumns;
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

  onFieldsChange() {
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
    this._changeSettingsDebounced({subject: 'selectedFields'});
  }

  onFieldPropChange(event: DesignerChangeEvent) {
    this._changeSettingsDebounced(event);
  }

  _changeSettingsDebounced(event: DesignerChangeEvent) {
    this.settingsChange.emit(event);
  }

  getUnselectedArtifactColumns() {
    const { types, keyword } = this.filterObj;
    return filter(this.artifactColumns, ({checked, type, alias, displayName}) => {
      return !checked &&
        this.hasType(type, types) &&
        this.hasKeyword(alias || displayName, keyword)
    });
  }

  hasType(type, filterTypes) {

    switch (TYPE_MAP[type]) {
    case 'number':
      return filterTypes.includes('number');
    case 'date':
      return filterTypes.includes('date');
    case 'string':
      return filterTypes.includes('string');
    default:
      return true;
    }
  }

  hasKeyword(name, keyword) {
    if (!keyword) {
      return true;
    }
    const regexp = new RegExp(keyword, 'i');
    return name && name.match(regexp);
  }

  onTextFilterChange(value) {
    this.filterObj.keyword = value;
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
  }

  onTypeFilterChange(event) {
    const value = event.value;
    const checked = event.source.checked;

    if (!checked) {
      this.filterObj.types = filter(this.filterObj.types, type => type !== value);
    } else {
      this.filterObj.types = [...this.filterObj.types, value];
    }
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
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
