import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  OnInit
} from '@angular/core';
import * as fpFilter from 'lodash/fp/filter';
import * as fpSort from 'lodash/fp/sortBy';
import * as fpPipe from 'lodash/fp/pipe';
import * as debounce from 'lodash/debounce';
import * as isEmpty from 'lodash/isEmpty';
import { PerfectScrollbarConfigInterface } from 'ngx-perfect-scrollbar';
import * as filter from 'lodash/filter';
import * as every from 'lodash/every';
import * as findIndex from 'lodash/findIndex';
import * as some from 'lodash/some';
import * as map from 'lodash/map';
import * as mapValues from 'lodash/mapValues';
import * as isBoolean from 'lodash/isBoolean';
import { Select, Store } from '@ngxs/store';
import { Observable } from 'rxjs';

import { DesignerState } from '../../state/designer.state';
import {
  DesignerAddColumnToGroupAdapter,
  DesignerRemoveColumnFromGroupAdapter
} from '../../actions/designer.actions';
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

import {
  getArtifactColumnTypeIcon,
  getArtifactColumnGeneralType,
  getFilterTypes
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
export class DesignerSettingsSingleTableComponent implements OnChanges, OnInit {
  @Output()
  public change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input('artifacts')
  public set setArtifactColumns(artifacts: Artifact[]) {
    if (!isEmpty(artifacts)) {
      this.artifact = artifacts[0];
      this.artifactColumns = this.artifact.columns;
      this.unselectedArtifactColumns = this.getUnselectedArtifactColumns();
    }
  }
  @Input() analysisType: string;
  @Input() analysisSubtype: string;

  public dropListContainer;
  public typeIcons = [];
  public isEmpty: (any) => boolean = isEmpty;
  public artifactColumns: ArtifactColumns;
  private artifact: Artifact;
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
      geo: false,
      coordinate: false
    },
    adapters: [
      false, // first adapter
      false // first adapter
    ]
  };

  public config: PerfectScrollbarConfigInterface = {};

  public groupsThatCanRecieveColumn: IDEsignerSettingGroupAdapter[];
  menuVisibleFor: string;

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
    this.typeIcons = getFilterTypes(this.analysisType, this.analysisSubtype);
  }

  ngOnChanges(changes) {
    const subType = changes.analysisSubtype;
    if (
      subType &&
      !subType.firstChange &&
      subType.currentValue !== subType.previousValue
    ) {
      this.filterObj.types = mapValues(this.filterObj.types, () => false);
      this.typeIcons = getFilterTypes(this.analysisType, this.analysisSubtype);
    }
  }

  get isDerivedMetricSupported(): boolean {
    return (
      this.analysisType && !['report', 'pivot'].includes(this.analysisType)
    );
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
    const unselectedArtifactColumns = fpPipe(
      fpFilter(artifactColumn => {
        const { checked, alias, displayName } = artifactColumn;
        return (
          !checked &&
          this.hasKeyword(alias || displayName, keyword) &&
          this.passesTypeFilter(types, artifactColumn)
        );
      }),
      fpSort(artifactColumn => artifactColumn.displayName)
    )(this.artifactColumns);

    this.dropListContainer = { artifactColumns: unselectedArtifactColumns };
    return unselectedArtifactColumns;
  }

  passesTypeFilter(types, artifactColumn) {
    if (isEmpty(this.groupAdapters)) {
      return true;
    }
    const adapterFilterResults = map(
      this.filterObj.adapters,
      (toggled, index) => {
        const adapter = this.groupAdapters[index];
        const acceptFn = adapter.canAcceptArtifactColumnOfType;
        return toggled ? acceptFn(artifactColumn) : null;
      }
    );

    const generalType = this.getGeneralType(artifactColumn);
    const onlyFilterResults = filter(adapterFilterResults, isBoolean);

    if (every(types, toggled => !toggled) && isEmpty(onlyFilterResults)) {
      return true;
    }

    return some(onlyFilterResults) || types[generalType];
  }

  getGeneralType(artifactColumn) {
    return getArtifactColumnGeneralType(
      artifactColumn,
      this.analysisType,
      this.analysisSubtype
    );
  }

  getArtifactColumnTypeIcon(artifactColumn) {
    return getArtifactColumnTypeIcon(
      artifactColumn,
      this.analysisType,
      this.analysisSubtype
    );
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
    const columnIndex = findIndex(
      groupAdapter.artifactColumns,
      ({ columnName }) => artifactColumn.columnName === columnName
    );
    const adapterIndex = this.groupAdapters.indexOf(groupAdapter);
    this._store.dispatch(
      new DesignerRemoveColumnFromGroupAdapter(columnIndex, adapterIndex)
    );
    // this._designerService.removeArtifactColumnFromGroup(
    //   artifactColumn,
    //   groupAdapter
    // );
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
    this.menuVisibleFor = artifactColumn.displayName;
    this.groupsThatCanRecieveColumn = this._designerService.getGroupsThatCanRecieve(
      artifactColumn,
      this.groupAdapters
    );
  }

  onMenuClosed() {
    this.menuVisibleFor = '';
  }
}
