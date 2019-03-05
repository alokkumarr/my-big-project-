import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import {CdkDragDrop, moveItemInArray, transferArrayItem, CdkDrag } from '@angular/cdk/drag-drop';
import { Subject } from 'rxjs';
import { bufferCount, take } from 'rxjs/operators';
import * as isEmpty from 'lodash/isEmpty';
import * as debounce from 'lodash/debounce';
import * as every from 'lodash/every';
import * as map from 'lodash/map';

import { DesignerService } from '../../designer.service';
import { DndPubsubService, DndEvent } from '../../../../../common/services';
import { getArtifactColumnGeneralType } from '../../utils';
import {
  IDEsignerSettingGroupAdapter,
  Artifact,
  ArtifactColumn,
  ArtifactColumns,
  // ArtifactColumnFilter,
  DesignerChangeEvent
} from '../../types';

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
  @Input() analysisType: string;
  @Input() analysisSubtype: string;
  public groupAdapters: IDEsignerSettingGroupAdapter[];
  public artifactColumns: ArtifactColumns;
  private _dndSubscription;
  @Input('artifacts')
  public set setArtifactColumns(artifacts: Artifact[]) {
    if (!isEmpty(artifacts)) {
      this.artifactColumns = artifacts[0].columns;
      if (every(this.artifactColumns, col => !col.checked)) {
        this.setGroupAdapters();
      }
    }
  }
  public isDragInProgress = false;
  // array of booleans to show if the corresponding group addapterg can accept the field that is being dragged
  public openGroupArray: boolean[];
  private _acceptEventStream$: Subject<boolean> = new Subject<boolean>();

  constructor(
    private _designerService: DesignerService,
    private _dndPubsub: DndPubsubService
    ) {
    this._changeSettingsDebounced = debounce(
      this._changeSettingsDebounced,
      SETTINGS_CHANGE_DEBOUNCE_TIME
    );
    this.changeOpenGroupArray = this.changeOpenGroupArray.bind(this);
  }

  ngOnInit() {
    this.setGroupAdapters();
    this._dndSubscription = this._dndPubsub.subscribe(this.onDndEvent.bind(this));
  }

  ngOnDestroy() {
    this._dndSubscription.unsubscribe();
  }

  onDndEvent(event: DndEvent) {
    switch (event) {
    case 'dragStart':
      this.isDragInProgress = true;
      this._acceptEventStream$.pipe(bufferCount(3), take(1)).subscribe(this.changeOpenGroupArray);
      break;
    case 'dragEnd':
      this.isDragInProgress = false;
      break;
    }
  }

  changeOpenGroupArray(array) {
    this.openGroupArray = array;
  }

  setGroupAdapters() {
    switch (this.analysisType) {
    case 'pivot':
      this.groupAdapters = this._designerService.getPivotGroupAdapters(
        this.artifactColumns
      );
      break;
    case 'chart':
      this.groupAdapters = this._designerService.getChartGroupAdapters(
        this.artifactColumns, this.analysisSubtype
      );
    }
    this.openGroupArray = map(this.groupAdapters, () => false);
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

  drop(event: CdkDragDrop<IDEsignerSettingGroupAdapter>) {
    const adapter = event.container.data;
    const previousAdapter = event.previousContainer.data;
    if (event.previousContainer === event.container) {
      moveItemInArray(<any>adapter.artifactColumns, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(<any>previousAdapter.artifactColumns,
                        <any>adapter.artifactColumns,
                        event.previousIndex,
                        event.currentIndex);
      const movedItem = adapter.artifactColumns[event.currentIndex];
      adapter.transform(movedItem);
    }
    adapter.onReorder(adapter.artifactColumns);
    this.onFieldsChange();
  }

  acceptPredicateFor(adapter: IDEsignerSettingGroupAdapter) {
    return (item: CdkDrag<ArtifactColumn>) => {
      const canAcceptFn = adapter.canAcceptArtifactColumn(adapter, this.groupAdapters);
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
        return 'calendar-type-chip-color';
    }
  }
}
