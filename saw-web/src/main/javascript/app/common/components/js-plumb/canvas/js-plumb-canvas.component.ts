declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter,
  ElementRef
} from '@angular/core';
import * as jsPlumb from 'jsplumb';
import * as find from 'lodash/find';

import {
  Artifact,
  Join,
  JoinCriterion,
  EndpointPayload,
  JoinChangeEvent,
  ConnectionPayload,
  JsPlumbCanvasChangeEvent
} from '../types';

const template = require('./js-plumb-canvas.component.html');
require('./js-plumb-canvas.component.scss');

@Component({
  selector: 'js-plumb-canvas-u',
  template
})
export class JsPlumbCanvasComponent {
  @Output() change: EventEmitter<JsPlumbCanvasChangeEvent> = new EventEmitter();
  @Input() artifacts: Artifact[];
  @Input() joins: Join[]= [];
  private _jsPlumbInst: any;

  constructor (
    private _elementRef: ElementRef
  ) {
    this.onConnection = this.onConnection.bind(this);
    this.onConnectionDetached = this.onConnectionDetached.bind(this);
    this.onConnectionMoved = this.onConnectionMoved.bind(this);
  }

  ngOnInit() {
    this._jsPlumbInst = jsPlumb.getInstance();
    this._jsPlumbInst.setContainer(this._elementRef.nativeElement);
  }

  ngAfterViewInit() {
    this._jsPlumbInst.bind('connection', this.onConnection);
  }

  onChange(event: JsPlumbCanvasChangeEvent) {
    this.change.emit(event);
  }

  onConnection(info) {
    const sourcePayload= <EndpointPayload>info.sourceEndpoint.getParameter('endpointPayload');
    const targetPayload = <EndpointPayload>info.targetEndpoint.getParameter('endpointPayload');

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
        join = this.addJoin('inner', {
          tableName: sourceArtifactName,
          columnName: sourceColumn.columnName,
          side: sourceSide
        }, {
          tableName: targetArtifactName,
          columnName: targetColumn.columnName,
          side: targetSide
        });
      }
    }
  }

  onConnectionDetached(info) {
    const connectionPayload = <ConnectionPayload>info.connection.getParameter('connectionPayload');
    this.removeJoin(connectionPayload.join);
    // TODO emit event
    // this.onChange({
    //   name: EVENTS.JOIN_CHANGED,
    //   params: {}
    // });
  }

  onConnectionMoved() {

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

  findJoin(sourceTable: string, sourceColumnName: string, targetTable: string, targetColumnName: string) {
    return find(this.joins, (join: Join) => {
      const sourceCriterion = this.findCriterion(join.criteria, sourceTable, sourceColumnName);
      const targetCriterion = this.findCriterion(join.criteria, targetTable, targetColumnName);
      return sourceCriterion && targetCriterion;
    });
  }

  findCriterion(criteria, table, column) {
    return find(criteria, (criterion: JoinCriterion) => (
      criterion.tableName === table &&
      criterion.columnName === column
    ));
  }

  addJoin(type, sourceCriterion: JoinCriterion, targetCriterion: JoinCriterion) {
    if (!type ||
      !sourceCriterion.tableName || !sourceCriterion.columnName ||
      !targetCriterion.tableName || !targetCriterion.columnName)
    {
      return;
    }

    const join: Join = {
      type,
      criteria: [
        sourceCriterion,
        targetCriterion
      ]
    }
    this.joins = [
      ...this.joins,
      join
    ];
    this.onChange({subject: 'joins'});
  }

  changeJoin(index, newJoin) {
    this.joins[index] = newJoin;
    this.onChange({subject: 'joins'});
  }

  removeJoin(join) {
    const index = this.joins.indexOf(join);

    if (index !== -1) {
      this.joins.splice(index, 1);
    }
    this.joins = [...this.joins];
    this.onChange({subject: 'joins'});
  }
}
