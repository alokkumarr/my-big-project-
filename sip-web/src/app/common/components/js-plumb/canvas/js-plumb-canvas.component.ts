import {
  Component,
  Input,
  Output,
  EventEmitter,
  ElementRef,
  OnInit,
  OnDestroy,
  AfterViewInit
} from '@angular/core';
import { Select } from '@ngxs/store';
import * as get from 'lodash/get';
import * as jsPlumb from 'jsplumb';
import * as find from 'lodash/find';
import * as isEqual from 'lodash/isEqual';
import * as isEmpty from 'lodash/isEmpty';
import * as toLower from 'lodash/toLower';
import * as findIndex from 'lodash/findIndex';
import { tap } from 'rxjs/operators';
import { Store } from '@ngxs/store';

import {
  Artifact,
  Join,
  JoinCriterion,
  EndpointPayload,
  JoinChangeEvent,
  ConnectionPayload,
  JoinEventData
} from '../types';
import { Observable, Subscription } from 'rxjs';
import {
  AnalysisDSL,
  ArtifactDSL,
  ArtifactColumnDSL,
  ArtifactColumnReport
} from 'src/app/models';
import { CommonDesignerJoinsArray } from '../../../actions/common.actions';

@Component({
  selector: 'js-plumb-canvas-u',
  templateUrl: './js-plumb-canvas.component.html',
  styles: [
    `
      :host {
        position: relative;
      }
    `
  ]
})
export class JsPlumbCanvasComponent
  implements OnInit, AfterViewInit, OnDestroy {
  @Output() change: EventEmitter<JoinEventData> = new EventEmitter();
  @Input() useAggregate: boolean;
  @Input() artifacts: Artifact[];
  @Input() joins: Join[] = [];
  @Select(state => state.designerState.analysis) dslAnalysis$: Observable<
    AnalysisDSL
  >;
  public _jsPlumbInst: any;
  private listeners: Subscription[] = [];

  /* If we change fields from outside the table component,
     like removing it from the preview grid, we need to
     sync check boxes in table with the latest artifacts
  */
  private syncCheckedField = this.dslAnalysis$.pipe(
    tap(analysis => {
      const artifacts: ArtifactDSL[] =
        get(analysis, 'sipQuery.artifacts') || [];

      /* For each artifact, find the corresponding artifact in sipQuery */
      (this.artifacts || []).forEach(metricArtifact => {
        const analysisArtifact = find(
          artifacts,
          a => toLower(a.artifactsName) === toLower(metricArtifact.artifactName)
        );

        if (!analysisArtifact || !analysisArtifact.fields) {
          // Clear all checkboxes if all fields are unselected
          this.clearAllCheckboxes(metricArtifact);
          return;
        }

        /* For each column in artifact, find corresponding column in sipQuery */
        (<ArtifactColumnReport[]>metricArtifact.columns).forEach(
          metricField => {
            const analysisField = find(
              analysisArtifact.fields,
              (col: ArtifactColumnDSL) =>
                toLower(metricField.columnName) === toLower(col.columnName)
            );

            /* If column not found in sipQuery, it's not selected.
             Mark it unchecked.
          */
            metricField.checked = Boolean(analysisField);
          }
        );
      });
      this.artifacts = [...this.artifacts];
    })
  );

  constructor(public _elementRef: ElementRef, private _store: Store) {
    this.onConnection = this.onConnection.bind(this);
    this.onConnectionDetached = this.onConnectionDetached.bind(this);
  }

  ngOnInit() {
    this._jsPlumbInst = jsPlumb.getInstance();
    this._jsPlumbInst.setContainer(this._elementRef.nativeElement);
    if (!isEmpty(this.joins)) {
      this._store.dispatch(new CommonDesignerJoinsArray(this.joins));
    }
    this.listeners.push(this.syncCheckedField.subscribe());
  }

  ngOnDestroy() {
    this.listeners.forEach(sub => sub.unsubscribe());
  }

  ngAfterViewInit() {
    this._jsPlumbInst.bind('connection', this.onConnection);
  }

  trackByIndex(index) {
    return index;
  }

  onChange(event: JoinEventData) {
    this.change.emit(event);
  }

  onConnection(info) {
    const sourcePayload = <EndpointPayload>(
      info.sourceEndpoint.getParameter('endpointPayload')
    );
    const targetPayload = <EndpointPayload>(
      info.targetEndpoint.getParameter('endpointPayload')
    );

    if (sourcePayload && targetPayload) {
      const {
        artifactName: sourceArtifactName,
        column: sourceColumn,
        side: sourceSide
      } = sourcePayload;
      const {
        artifactName: targetArtifactName,
        column: targetColumn,
        side: targetSide
      } = targetPayload;

      if (sourceArtifactName === targetArtifactName) {
        // if connecting to the same table, detach the conenction because it doesn't make sense
        this._jsPlumbInst.detach(info.connection);
      }

      let join = this.findJoin(
        sourceArtifactName,
        sourceColumn.columnName,
        targetArtifactName,
        targetColumn.columnName
      );

      if (!join) {
        join = this.addJoin(
          'inner',
          {
            tableName: sourceArtifactName,
            columnName: sourceColumn.columnName,
            side: sourceSide
          },
          {
            tableName: targetArtifactName,
            columnName: targetColumn.columnName,
            side: targetSide
          }
        );
      }
    }
  }

  onConnectionDetached(info) {
    const connectionPayload = <ConnectionPayload>(
      info.connection.getParameter('connectionPayload')
    );
    this.removeJoin(connectionPayload.join);
  }

  onJoinChange(event: JoinChangeEvent) {
    if (!event) {
      return;
    }
    const { action, index, join } = event;
    switch (action) {
      case 'save':
        this.changeJoin(index, join);
        break;
      case 'delete':
        this.removeJoin(join);
        break;
    }
  }

  findJoin(
    sourceTable: string,
    sourceColumnName: string,
    targetTable: string,
    targetColumnName: string
  ) {
    return find(this.joins, (join: Join) => {
      const sourceCriterion = this.findCriterion(
        join.criteria,
        sourceTable,
        sourceColumnName
      );
      const targetCriterion = this.findCriterion(
        join.criteria,
        targetTable,
        targetColumnName
      );
      return sourceCriterion && targetCriterion;
    });
  }

  findCriterion(criteria, table, column) {
    return find(
      criteria,
      (criterion: JoinCriterion) =>
        criterion.tableName === table && criterion.columnName === column
    );
  }

  addJoin(
    type,
    sourceCriterion: JoinCriterion,
    targetCriterion: JoinCriterion
  ) {
    if (
      !type ||
      !sourceCriterion.tableName ||
      !sourceCriterion.columnName ||
      !targetCriterion.tableName ||
      !targetCriterion.columnName
    ) {
      return;
    }

    const join: Join = {
      type,
      criteria: [sourceCriterion, targetCriterion]
    };
    this.joins.push(join);
    this.onChange({ subject: 'joins', data: this.joins });
  }

  changeJoin(index, newJoin) {
    this.joins[index] = newJoin;
    this.onChange({ subject: 'joins', data: this.joins });
  }

  removeJoin(join) {
    const index = findIndex(this.joins, j => isEqual(j, join));

    if (index >= 0) {
      this.joins.splice(index, 1);
    }
    this.onChange({ subject: 'joins', data: this.joins });
  }

  clearAllCheckboxes(metric) {
    (<ArtifactColumnReport[]>metric.columns).forEach(
      metricField => {
        const analysisField = find(
          (col: ArtifactColumnDSL) =>
            metricField.checked === true
        );
        metricField.checked = Boolean(analysisField);
      }
    );
  }
}
