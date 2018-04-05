declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter,
  ElementRef
} from '@angular/core';
import * as find from 'lodash/find';
import * as unset from 'lodash/unset';
import * as sortBy from 'lodash/sortBy';

import {
  Artifact,
  Join,
  JoinCriterion,
  ArtifactColumnReport,
  JsPlumbCanvasChangeEvent
} from '../types';
import { ArtifactColumn } from '../../../../modules/analyze/types';

const template = require('./js-plumb-table.component.html');
require('./js-plumb-table.component.scss');

@Component({
  selector: 'js-plumb-table-u',
  template
})
export class JsPlumbTableComponent {
  @Output() change: EventEmitter<JsPlumbCanvasChangeEvent> = new EventEmitter();
  @Input() artifact: Artifact;
  @Input() plumbInstance: any;

  public sides = ['left', 'right'];

  constructor (
    private _elementRef: ElementRef
  ) {}

  ngOnInit() {
    this.updatePosition();
  }

  ngAfterViewInit() {
    const elem = this._elementRef.nativeElement;
    const artifactPosition = this.artifact.artifactPosition;
    this.plumbInstance.draggable(elem, {
      allowNegative: false,
      drag: event => {
        artifactPosition[0] = event.pos[0];
        artifactPosition[1] = event.pos[1];
        this.change.emit({subject: 'artifactPosition'});
      }
    });
  }

  getArtifactColumns() {
    return sortBy(this.artifact.columns, ({displayName, aliasName}: ArtifactColumn) => aliasName || displayName);
  }

  updatePosition() {
    const elemStyle = this._elementRef.nativeElement.style;
    const [x, y] = this.artifact.artifactPosition;
    elemStyle.left = x !== 0 ? `${x}px` : 0;
    elemStyle.top = y !== 0 ? `${y}px` : 0;
  }

  getColumnLabel(column: ArtifactColumnReport) {
    return column.aliasName || column.displayName;
  }

  onCheckBoxToggle(column: ArtifactColumnReport, checked) {
    column.checked = checked;
    this.change.emit({
      subject: 'column',
      column
    });
  }

  onAggregateChange(column, aggregate) {
    column.aggregate = aggregate;
    this.change.emit({subject: 'aggregate'});
  }

  clearAggregate(column) {
    unset(column, 'aggregate');
    this.change.emit({subject: 'aggregate'});
  }
}
