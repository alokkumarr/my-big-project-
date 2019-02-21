import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import {CdkDragDrop, moveItemInArray, transferArrayItem, CdkDrag } from '@angular/cdk/drag-drop';
import * as isEmpty from 'lodash/isEmpty';
import * as debounce from 'lodash/debounce';
import * as every from 'lodash/every';

import { DesignerService } from '../../designer.service';
import { DndPubsubService, DndEvent } from '../../../../../common/services';
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

  constructor(
    private _designerService: DesignerService,
    private _dndPubsub: DndPubsubService
    ) {
    this._changeSettingsDebounced = debounce(
      this._changeSettingsDebounced,
      SETTINGS_CHANGE_DEBOUNCE_TIME
    );
  }

  ngOnInit() {
    this.setGroupAdapters();
    this._dndSubscription = this._dndPubsub.subscribe(this.onDndEvent.bind(this));
  }

  ngOnDestroy() {
    this._dndSubscription.unsubscribe();
  }

  onDndEvent(event: DndEvent) {
    console.log(event);
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
    console.log('drop', event);
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
      console.log('movedItem: ', movedItem);
    }
    adapter.onReorder(adapter.artifactColumns);
    this.onFieldsChange();
  }

  acceptPredicateFor(adapter: IDEsignerSettingGroupAdapter) {
    return (item: CdkDrag<ArtifactColumn>) => {
      const canAcceptFn = adapter.canAcceptArtifactColumn(adapter, this.groupAdapters);
      const artifactColumn = item.data;
      return canAcceptFn(artifactColumn);
    };
  }
}
