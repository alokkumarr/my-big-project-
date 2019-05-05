import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { CdkDragDrop, CdkDrag } from '@angular/cdk/drag-drop';
import { Subject, Observable, Subscription } from 'rxjs';
import { takeWhile, last, tap } from 'rxjs/operators';
import { Select, Store } from '@ngxs/store';

import * as findIndex from 'lodash/findIndex';
import * as debounce from 'lodash/debounce';
import * as has from 'lodash/has';
import * as reduce from 'lodash/reduce';
import { AGGREGATE_TYPES_OBJ } from '../../../../../common/consts';
import { DndPubsubService, DndEvent } from '../../../../../common/services';
import { getArtifactColumnGeneralType } from '../../utils';
import {
  IDEsignerSettingGroupAdapter,
  Artifact,
  ArtifactColumn,
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
import { getFilterValue } from '../../filter/chips-u';
const SETTINGS_CHANGE_DEBOUNCE_TIME = 500;

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
  private subscriptions: Subscription[] = [];

  @Select(DesignerState.groupAdapters) groupAdapters$: Observable<
    IDEsignerSettingGroupAdapter[]
  >;

  public nameMap;
  public isDragInProgress = false;
  // map of booleans to show wichi adapter can accept a field, { [adapter.title]: adapter.canAcceptArtifact }
  public canAcceptMap: Object;
  private _acceptEventStream$ = new Subject<{
    adapterTitle: string;
    canAccept: boolean;
  }>();
  public AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;

  constructor(private _dndPubsub: DndPubsubService, private _store: Store) {
    this._changeSettingsDebounced = debounce(
      this._changeSettingsDebounced,
      SETTINGS_CHANGE_DEBOUNCE_TIME
    );
  }

  ngOnInit() {
    this._store.dispatch(new DesignerInitGroupAdapters());
    this.subscribeToMetrics();
    const subscription = this._dndPubsub.subscribe(this.onDndEvent.bind(this));
    this.subscriptions.push(subscription);
    this.groupAdapters$.subscribe(adapters => {
      this.canAcceptMap = reduce(
        adapters,
        (acc, adapter) => {
          acc[adapter.title] = false;
          return acc;
        },
        {}
      );
      this.groupAdapters = adapters;
    });
  }

  subscribeToMetrics() {
    const subscription = this._store
      .select(state => state.designerState.metric)
      .pipe(
        tap(metric => {
          this.nameMap = reduce(
            metric.artifacts,
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
        })
      )
      .subscribe();
    this.subscriptions.push(subscription);
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  getDisplayName(filter) {
    const table = filter.artifactsName || filter.tableName;
    return this.nameMap[table][filter.columnName] || [filter.columnName];
  }

  onDndEvent(event: DndEvent) {
    switch (event) {
      case 'dragStart':
        const canAcceptMap = {};
        this.isDragInProgress = true;
        this._acceptEventStream$
          .pipe(
            takeWhile(({ adapterTitle, canAccept }) => {
              const isInAcceptMap = !has(canAcceptMap, adapterTitle);
              canAcceptMap[adapterTitle] = canAccept;
              return isInAcceptMap;
            }),
            last()
          )
          .subscribe(() => {
            this.canAcceptMap = canAcceptMap;
          });
        break;
      case 'dragEnd':
        this.isDragInProgress = false;
        break;
    }
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
    this.onFieldsChange();
    // this._designerService.removeArtifactColumnFromGroup(
    //   artifactColumn,
    //   groupAdapter
    // );
    // this.onFieldsChange();
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
        const previousAdapterIndex = this.groupAdapters.indexOf(
          previousAdapter
        );
        this._store.dispatch(
          new DesignerRemoveColumnFromGroupAdapter(
            event.previousIndex,
            previousAdapterIndex
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
      this._acceptEventStream$.next({ adapterTitle: adapter.title, canAccept });
      return canAccept;
    };
  }

  getFieldItemClass(col) {
    const type = getArtifactColumnGeneralType(
      col,
      this.analysisType,
      this.analysisSubtype
    );
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

  dragStarted() {
    this._dndPubsub.emit('dragStart');
  }

  dragReleased() {
    this._dndPubsub.emit('dragEnd');
  }

  getFilterValue(filter) {
    return getFilterValue(filter);
  }
}
