import {
  Component,
  Input,
  Output,
  EventEmitter,
  ElementRef,
  OnInit,
  AfterViewInit
} from '@angular/core';
import * as unset from 'lodash/unset';
import * as sortBy from 'lodash/sortBy';

import {
  Artifact,
  ArtifactColumnReport,
  JsPlumbCanvasChangeEvent
} from '../types';

@Component({
  selector: 'js-plumb-table-u',
  templateUrl: './js-plumb-table.component.html',
  styleUrls: ['./js-plumb-table.component.scss']
})
export class JsPlumbTableComponent implements OnInit, AfterViewInit {
  @Output() change: EventEmitter<JsPlumbCanvasChangeEvent> = new EventEmitter();
  @Input() plumbInstance: any;
  @Input() useAggregate: boolean;
  @Input('artifact')
  set setArtifact(artifact: Artifact) {
    this.artifact = artifact;
    this.columns = sortBy(artifact.columns, 'displayName');
  }

  public columns: ArtifactColumnReport[];
  public artifact: Artifact;

  public sides = ['left', 'right'];

  constructor(public _elementRef: ElementRef) {}

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
        this.change.emit({ subject: 'artifactPosition' });
      }
    });
  }

  trackByIndex(index) {
    return index;
  }

  updatePosition() {
    const elemStyle = this._elementRef.nativeElement.style;
    const [x, y] = this.artifact.artifactPosition;
    elemStyle.left = x !== 0 ? `${x}px` : 0;
    elemStyle.top = y !== 0 ? `${y}px` : 0;
  }

  onCheckBoxToggle(column: ArtifactColumnReport, checked) {
    column.checked = checked;
    this.change.emit({
      subject: 'column',
      column
    });
  }

  getIdentifier(column) {
    return `js-plumb-field-${this.artifact.artifactName}:${column.displayName}`;
  }

  onAggregateChange(column, aggregate) {
    column.aggregate = aggregate;
    unset(column, 'format');
    this.change.emit({ subject: 'aggregate', column });
  }

  clearAggregate(column) {
    unset(column, 'aggregate');
    unset(column, 'format');
    this.change.emit({ subject: 'aggregate' });
  }
}
