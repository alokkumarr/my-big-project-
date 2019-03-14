import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import * as fpFilter from 'lodash/fp/filter';
import * as fpSort from 'lodash/fp/sortBy';
import * as fpPipe from 'lodash/fp/pipe';
import * as debounce from 'lodash/debounce';
import * as isEmpty from 'lodash/isEmpty';
import { PerfectScrollbarConfigInterface } from 'ngx-perfect-scrollbar';
import * as filter from 'lodash/filter';
import * as every from 'lodash/every';
import * as map from 'lodash/map';
import { Select, Store } from '@ngxs/store';
import { Observable } from 'rxjs';

import { DesignerState } from '../../state/designer.state';
import { DesignerAddColumnToGroupAdapter } from '../../actions/designer.actions';
import { DesignerService } from '../../designer.service';
import { DndPubsubService } from '../../../../../common/services';
import {
  IDEsignerSettingGroupAdapter,
  Artifact,
  ArtifactColumn,
  ArtifactColumns,
  ArtifactColumnFilter,
  DesignerChangeEvent
} from '../../types';
import { TYPE_ICONS } from '../../consts';
import {
  getArtifactColumnTypeIcon,
  getArtifactColumnGeneralType
} from '../../utils';

const SETTINGS_CHANGE_DEBOUNCE_TIME = 500;
const FILTER_CHANGE_DEBOUNCE_TIME = 300;

/**
 * Designer settings component for designers with a single set of artifactColumns like
 * pivot or chart
 */
@Component({
  selector: 'designer-settings-single-table',
  templateUrl: './designer-settings-single-table.component.html',
  styleUrls: ['./designer-settings-single-table.component.scss']
})
export class DesignerSettingsSingleTableComponent implements OnInit {
  @Output()
  public change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input('artifacts')
  public set setArtifactColumns(artifacts: Artifact[]) {
    if (!isEmpty(artifacts)) {
      this.artifactColumns = artifacts[0].columns;
      this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
    }
  }
  @Input() analysisType: string;
  @Input() analysisSubtype: string;
  @Input() fieldCount: number;
  @Input() public sqlBuilder;

  public dropListContainer;
  public typeIcons = TYPE_ICONS;
  public isEmpty: Function = isEmpty;
  public artifactColumns: ArtifactColumns;
  public unselectedArtifactColumns: ArtifactColumns;
  public groupAdapters: IDEsignerSettingGroupAdapter[];
  @Select(DesignerState.groupAdapters) groupAdapters$: Observable<
    IDEsignerSettingGroupAdapter[]
  >;
  public filterObj: ArtifactColumnFilter = {
    keyword: '',
    types: {
      number: false,
      date: false,
      string: false,
      geo: false
    },
    adapters: [
      false, // first adapter
      false // first adapter
    ]
  };

  public config: PerfectScrollbarConfigInterface = {};

  public groupsThatCanRecieveColumn: IDEsignerSettingGroupAdapter[];

  constructor(
    private _designerService: DesignerService,
    private _dndPubsub: DndPubsubService,
    private _store: Store
  ) {
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

    this.groupAdapters$.subscribe(adapters => {
      this.groupAdapters = adapters;
    });
  }

  ngOnInit() {
    this.typeIcons = filter(TYPE_ICONS, type => {
      if (type.value === 'geo') {
        return this.analysisType === 'chart' && this.analysisSubtype === 'geo';
      }
      return true;
    });
  }

  trackByIndex(index) {
    return index;
  }

  onFieldsChange() {
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
    this._changeSettingsDebounced({ subject: 'selectedFields' });
  }

  onFieldPropChange(event: DesignerChangeEvent) {
    this._changeSettingsDebounced(event);
  }

  _changeSettingsDebounced(event: DesignerChangeEvent) {
    this.change.emit(event);
  }

  getUnselectedArtifactColumns() {
    const { types, keyword } = this.filterObj;
    const toggleFilter = this.hasAllowedType(types);
    const unselectedArtifactColumns = fpPipe(
      fpFilter(artifactColumn => {
        const { checked, alias, displayName } = artifactColumn;
        return (
          !checked &&
          toggleFilter(artifactColumn) &&
          this.hasKeyword(alias || displayName, keyword) &&
          this.filterByAdapters(artifactColumn)
        );
      }),
      fpSort(artifactColumn => artifactColumn.displayName)
    )(this.artifactColumns);

    this.dropListContainer = { artifactColumns: unselectedArtifactColumns };
    return unselectedArtifactColumns;
  }

  filterByAdapters(artifactColumn) {
    if (isEmpty(this.groupAdapters)) {
      return true;
    }
    const filterResults = map(this.filterObj.adapters, (toggled, index) => {
      const adapter = this.groupAdapters[index];
      const acceptFn = adapter.canAcceptArtifactColumn(
        adapter,
        this.groupAdapters
      );
      return toggled ? acceptFn(artifactColumn) : true;
    });

    return every(filterResults);
  }

  hasAllowedType(filterTypes) {
    return artifactColumn => {
      const generalType = this.getGeneralType(artifactColumn);
      if (every(filterTypes, toggled => !toggled)) {
        return true;
      }
      return filterTypes[generalType];
    };
  }

  getGeneralType(artifactColumn) {
    return getArtifactColumnGeneralType(artifactColumn, this.analysisSubtype);
  }

  getArtifactColumnTypeIcon(artifactColumn) {
    return getArtifactColumnTypeIcon(artifactColumn, this.analysisSubtype);
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

  onTypeFilterChange(value) {
    this.filterObj.types[value] = !this.filterObj.types[value];
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
  }

  onFilterChange(index) {
    this.filterObj.adapters[index] = !this.filterObj.adapters[index];
    this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
  }

  addToGroup(
    artifactColumn: ArtifactColumn,
    columnIndex: number,
    adapter: IDEsignerSettingGroupAdapter
  ) {
    const index = adapter.artifactColumns.length;
    const adapterIndex = this.groupAdapters.indexOf(adapter);
    // remove from unselected fields
    this.unselectedArtifactColumns.splice(columnIndex, 1);
    this._store.dispatch(
      new DesignerAddColumnToGroupAdapter(artifactColumn, index, adapterIndex)
    );
    this.onFieldsChange();
  }

  removeFromGroup(
    artifactColumn: ArtifactColumn,
    groupAdapter: IDEsignerSettingGroupAdapter
  ) {
    this._designerService.removeArtifactColumnFromGroup(
      artifactColumn,
      groupAdapter
    );
    this.onFieldsChange();
  }

  noReturnPredicate() {
    return false;
  }

  dragStarted() {
    this._dndPubsub.emit('dragStart');
  }

  dragReleased() {
    this._dndPubsub.emit('dragEnd');
  }

  onAddToGroupMenuOpened(artifactColumn) {
    this.groupsThatCanRecieveColumn = this._designerService.getGroupsThatCanRecieve(
      artifactColumn,
      this.groupAdapters
    );
  }
}
