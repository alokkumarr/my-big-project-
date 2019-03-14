import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { CdkDragDrop, CdkDrag } from '@angular/cdk/drag-drop';
import { Subject, Observable } from 'rxjs';
import { bufferCount, take } from 'rxjs/operators';
import { Select, Store } from '@ngxs/store';

import * as isEmpty from 'lodash/isEmpty';
import * as debounce from 'lodash/debounce';
import * as every from 'lodash/every';
import * as map from 'lodash/map';
import * as reduce from 'lodash/reduce';

import { AGGREGATE_TYPES_OBJ } from '../../../../../common/consts';
import { DesignerService } from '../../designer.service';
import { DndPubsubService, DndEvent } from '../../../../../common/services';
import { getArtifactColumnGeneralType } from '../../utils';
import {
  IDEsignerSettingGroupAdapter,
  Artifact,
  ArtifactColumn,
  ArtifactColumns,
  Filter,
  DesignerChangeEvent
} from '../../types';
import { DesignerState } from '../../state/designer.state';
import {
  DesignerInitGroupAdapters,
  DesignerAddColumnToGroupAdapter,
  DesignerMoveColumnInGroupAdapter,
  DesignerRemoveColumnFromGroupAdapter
} from '../../actions/designer.actions';

const SETTINGS_CHANGE_DEBOUNCE_TIME = 500;
// const FILTER_CHANGE_DEBOUNCE_TIME = 300;
@Component({
  selector: 'designer-selected-fields',
  templateUrl: 'designer-selected-fields.component.html',
  styleUrls: ['designer-selected-fields.component.scss']
})
export class DesignerSelectedFieldsComponent implements OnInit, OnDestroy {
  @Output()
  public change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Output() removeFilter = new EventEmitter();
  @Input() analysisType: string;
  @Input() analysisSubtype: string;
  @Input() filters: Filter[];
  public groupAdapters: IDEsignerSettingGroupAdapter[];
  public artifactColumns: ArtifactColumns;
  private _dndSubscription;
  @Input('artifacts')
  public set setArtifactColumns(artifacts: Artifact[]) {
    if (!isEmpty(artifacts)) {
      this.artifactColumns = artifacts[0].columns;
      if (every(this.artifactColumns, col => !col.checked)) {
        // TODO sed action form container when resetting chart type
        /**
         * this._store.dispatch(new DesignerInitGroupAdapters(
           this.artifactColumns,
           this.analysisType,
           this.analysisSubtype
         ));
         */
      }
    }
    this.nameMap = reduce(
      artifacts,
      (acc, artifact: Artifact) => {
        acc[artifact.artifactName] = reduce(
          artifact.columns,
          (accum, col: ArtifactColumn) => {
            accum[col.columnName] = col.displayName;
            return accum;
          },
          {}
        );
        return acc;
      },
      {}
    );
  }

  @Select(DesignerState.groupAdapters) groupAdapters$: Observable<
    IDEsignerSettingGroupAdapter[]
  >;

  public nameMap;
  public isDragInProgress = false;
  // array of booleans to show if the corresponding group addapterg can accept the field that is being dragged
  public openGroupArray: boolean[];
  private _acceptEventStream$: Subject<boolean> = new Subject<boolean>();
  public AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;

  constructor(
    private _designerService: DesignerService,
    private _dndPubsub: DndPubsubService,
    private _store: Store
  ) {
    this._changeSettingsDebounced = debounce(
      this._changeSettingsDebounced,
      SETTINGS_CHANGE_DEBOUNCE_TIME
    );
    this.changeOpenGroupArray = this.changeOpenGroupArray.bind(this);
  }

  ngOnInit() {
    this._store.dispatch(
      new DesignerInitGroupAdapters(
        this.artifactColumns,
        this.analysisType,
        this.analysisSubtype
      )
    );
    this._dndSubscription = this._dndPubsub.subscribe(
      this.onDndEvent.bind(this)
    );
    this.groupAdapters$.subscribe(adapters => {
      this.openGroupArray = map(adapters, () => false);
      this.groupAdapters = adapters;
    });
  }

  ngOnDestroy() {
    this._dndSubscription.unsubscribe();
  }

  getDisplayName(filter: Filter) {
    return this.nameMap[filter.tableName][filter.columnName];
  }

  onDndEvent(event: DndEvent) {
    switch (event) {
      case 'dragStart':
        this.isDragInProgress = true;
        this._acceptEventStream$
          .pipe(
            bufferCount(3),
            take(1)
          )
          .subscribe(this.changeOpenGroupArray);
        break;
      case 'dragEnd':
        this.isDragInProgress = false;
        break;
    }
  }

  changeOpenGroupArray(array) {
    this.openGroupArray = array;
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

  onFieldsChange() {
    this._changeSettingsDebounced({ subject: 'selectedFields' });
  }

  _changeSettingsDebounced(event: DesignerChangeEvent) {
    this.change.emit(event);
  }

  drop(event: CdkDragDrop<IDEsignerSettingGroupAdapter>, adapterIndex) {
    const previousAdapter = event.previousContainer.data;
    const isComingFromUnselectedFields = !previousAdapter.title;
    const shouldMoveInSameGroupAdapter =
      event.previousContainer === event.container;
    if (shouldMoveInSameGroupAdapter) {
      this._store.dispatch(
        new DesignerMoveColumnInGroupAdapter(
          event.previousIndex,
          event.currentIndex,
          adapterIndex
        )
      );
    } else {
      const column = previousAdapter.artifactColumns[event.previousIndex];
      if (isComingFromUnselectedFields) {
        // remove from unselected fields
        previousAdapter.artifactColumns.splice(event.previousIndex, 1);
      } else {
        // remove from previousAdapter
        this._store.dispatch(
          new DesignerRemoveColumnFromGroupAdapter(
            event.previousIndex,
            adapterIndex
          )
        );
      }
      this._store.dispatch(
        new DesignerAddColumnToGroupAdapter(
          column,
          event.currentIndex,
          adapterIndex
        )
      );
    }
    this.onFieldsChange();
  }

  acceptPredicateFor(adapter: IDEsignerSettingGroupAdapter) {
    return (item: CdkDrag<ArtifactColumn>) => {
      const canAcceptFn = adapter.canAcceptArtifactColumn(
        adapter,
        this.groupAdapters
      );
      const artifactColumn = item.data;
      const canAccept = canAcceptFn(artifactColumn);
      this._acceptEventStream$.next(canAccept);
      return canAccept;
    };
  }

  getFieldItemClass(col) {
    const type = getArtifactColumnGeneralType(col);
    switch (type) {
      case 'string':
        return 'string-type-chip-color';
      case 'number':
        return 'number-type-chip-color';
      case 'geo':
        return 'geo-type-chip-color';
      case 'date':
        return 'date-type-chip-color';
    }
  }
}
