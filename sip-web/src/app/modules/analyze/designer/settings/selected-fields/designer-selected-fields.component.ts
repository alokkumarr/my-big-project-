import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter,
  ViewChildren
} from '@angular/core';
import { CdkDragDrop, CdkDrag } from '@angular/cdk/drag-drop';
import { Subject, Observable, Subscription } from 'rxjs';
import { takeWhile, last, tap } from 'rxjs/operators';
import { Select, Store } from '@ngxs/store';

import * as findIndex from 'lodash/findIndex';
import * as debounce from 'lodash/debounce';
import * as has from 'lodash/has';
import * as cloneDeep from 'lodash/cloneDeep';
import * as reduce from 'lodash/reduce';
import { AGGREGATE_TYPES_OBJ } from '../../../../../common/consts';
import { DndPubsubService, DndEvent } from '../../../../../common/services';
import { getArtifactColumnGeneralType } from '../../utils';
import {
  IDEsignerSettingGroupAdapter,
  ArtifactColumn,
  Filter,
  DesignerChangeEvent,
  ArtifactColumnDSL
} from '../../types';
import { AnalyzeService } from '../../../services/analyze.service';
import { DesignerState } from '../../state/designer.state';
import {
  DesignerInitGroupAdapters,
  DesignerAddColumnToGroupAdapter,
  DesignerMoveColumnInGroupAdapter,
  DesignerRemoveColumnFromGroupAdapter
} from '../../actions/designer.actions';
import { displayNameWithoutAggregateFor } from 'src/app/common/services/tooltipFormatter';
import { getFilterDisplayName } from './../../../consts';
import { PerfectScrollbarComponent } from 'ngx-perfect-scrollbar';
const SETTINGS_CHANGE_DEBOUNCE_TIME = 500;

@Component({
  selector: 'designer-selected-fields',
  templateUrl: 'designer-selected-fields.component.html',
  styleUrls: ['designer-selected-fields.component.scss']
})
export class DesignerSelectedFieldsComponent implements OnInit, OnDestroy {
  @ViewChildren(PerfectScrollbarComponent) scrollbars;
  @Output()
  public change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Output() removeFilter = new EventEmitter();
  @Input() analysisType: string;
  @Input() analysisSubtype: string;
  filters;
  // @Input() filters: Filter[];
  @Input('filters') set setFilters(filters: Filter[]) {
    if (!filters) {
      return;
    }
    this.filters = filters;
    this.flattenedfilters = this.analyzeService.flattenAndFetchFiltersChips(filters, []);
  }

  public groupAdapters: IDEsignerSettingGroupAdapter[];
  private subscriptions: Subscription[] = [];
  flattenedfilters;

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

  constructor(
    private _dndPubsub: DndPubsubService,
    private _store: Store,
    private analyzeService: AnalyzeService
  ) {
    this._changeSettingsDebounced = debounce(
      this._changeSettingsDebounced,
      SETTINGS_CHANGE_DEBOUNCE_TIME
    );
  }

  ngOnInit() {
    const self = this;
    this._store.dispatch(new DesignerInitGroupAdapters());
    this.subscribeToMetrics();

    const dndSub = this._dndPubsub.subscribe(this.onDndEvent.bind(this));
    this.subscriptions.push(dndSub);

    const adapterSub = this.groupAdapters$.subscribe(adapters => {
      this.canAcceptMap = reduce(
        adapters,
        (acc, adapter) => {
          acc[adapter.title] = false;
          return acc;
        },
        {}
      );
      this.groupAdapters = adapters;
      /* Update the scroll bars in case fields have exceeded the bounds */
      if (self.scrollbars) {
        self.scrollbars.forEach(bar => {
          bar.directiveRef.update();
        });
      }
    });
    this.subscriptions.push(adapterSub);
  }

  subscribeToMetrics() {
    const subscription = this._store
      .select(state => state.designerState.metric)
      .pipe(
        tap(metric => {
          this.nameMap = this.analyzeService.calcNameMap(metric.artifacts);
        })
      )
      .subscribe();
    this.subscriptions.push(subscription);
  }

  ngOnDestroy() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  getDisplayNameForFilter(filter) {
    return getFilterDisplayName(this.nameMap, filter);
  }

  getDisplayNameForColumn(column: ArtifactColumnDSL) {
    return displayNameWithoutAggregateFor(column);
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
    artifactColumn: ArtifactColumnDSL,
    groupAdapter: IDEsignerSettingGroupAdapter
  ) {
    artifactColumn.alias = '';
    const columnIndex = findIndex(
      groupAdapter.artifactColumns,
      ({ columnName, dataField }) =>
        dataField
          ? dataField === artifactColumn.dataField
          : columnName === artifactColumn.columnName
    );
    const adapterIndex = this.groupAdapters.indexOf(groupAdapter);
    this._store.dispatch(
      new DesignerRemoveColumnFromGroupAdapter(columnIndex, adapterIndex)
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
      if (previousAdapter.reverseTransform) {
        // clean up column, before moving it to a different place
        previousAdapter.reverseTransform(column);
      }
      this._store.dispatch(
        new DesignerAddColumnToGroupAdapter(
          cloneDeep(column),
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
      case 'derived':
        return 'derived-type-chip-color';
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

  removeFilterFromTree(filter, index) {
    if (this.filters[0].booleanCriteria) {
      this.flattenedfilters.splice(index, 1);
      if (filter.isAggregationFilter) {
        this.filters = cloneDeep(this.filters.filter(option => {
          return option.uuid !== filter.uuid;
        }));
        console.log(this.filters);
        this.removeFilter.emit({subject: 'filters', data: this.filters});
      } else {
        this.analyzeService.deleteFilterFromTree(this.filters[0], filter.uuid);
        setTimeout(() => {
          this.removeFilter.emit({subject: 'filters', data: this.filters});
        }, 650);
      }


    } else {
      this.flattenedfilters.splice(index, 1);
      this.filters.splice(index, 1);
      this.removeFilter.emit({subject: 'filters', data: this.filters});
    }

  }
}
