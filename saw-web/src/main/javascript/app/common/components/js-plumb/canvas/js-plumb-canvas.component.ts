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
  JoinCriterion
} from '../../../../models';
import { DesignerChangeEvent } from '../../../types';

const template = require('./js-plumb-canvas.component.html');
require('./js-plumb-canvas.component.scss');

type JsPlumbCanvasChangeEvent = DesignerChangeEvent<{
  artifacts?: Artifact[];
  joins?: Join[];
}>;

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

  ngOnDestroy() {

  }

  onChange(event: JsPlumbCanvasChangeEvent) {

  }

  onConnection(info) {
    const sourceEndpointInst = info.sourceEndpoint.getParameter('component');
    const targetEndpointInst = info.targetEndpoint.getParameter('component');

    if (sourceEndpointInst && targetEndpointInst) {
      const sourceField = sourceEndpointInst.model.field;
      const targetField = targetEndpointInst.model.field;

      if (sourceField.table === targetField.table) {
        this._jsPlumbInst.detach(info.connection);
      }

      let join = this.findJoin(sourceField.table.name, sourceField.name, targetField.table.name, targetField.name);

      if (!join) {
        join = this.addJoin('inner', {
          tableName: sourceField.table.name,
          columnName: sourceField.name,
          side: sourceEndpointInst.model.side
        }, {
          tableName: targetField.table.name,
          columnName: targetField.name,
          side: targetEndpointInst.model.side
        });
      }
    }
  }

  onConnectionDetached() {

  }

  onConnectionMoved() {

  }

  findJoin(sourceTable, sourceColumn, targetTable, targetColumn) {
    const findCriterion = (criteria, table, column) => (
      find(criteria, (criterion: JoinCriterion) => (
        criterion.tableName === table &&
        criterion.columnName === column
    )));

    find(this.joins, (join: Join) => {
      const sourceJoin = findCriterion(join.criteria, sourceTable, sourceColumn);
      const targetJoin = findCriterion(join.criteria, targetTable, targetColumn);
    });
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
    this.joins.push(join);
  }

  removeJoin(join) {
    const index = this.joins.indexOf(join);

    if (index !== -1) {
      this.joins.splice(index, 1);
    }
  }
}
