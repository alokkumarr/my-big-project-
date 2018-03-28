declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter,
  ElementRef
} from '@angular/core';
import {MatDialog, MatDialogConfig} from '@angular/material';

import {
  Artifact,
  Join,
  JoinCriterion,
  EndpointPayload,
  JoinChangeEvent
} from '../types';
import { JoinDialogComponent } from '../join-dialog';

const template = require('./js-plumb-join-label.component.html');
require('./js-plumb-join-label.component.scss');

@Component({
  selector: 'js-plumb-join-label-u',
  template
})
export class JsPlumbJoinLabelComponent {
  @Input() join: Join;
  @Output() change: EventEmitter<JoinChangeEvent> = new EventEmitter();
  private _jsPlumbInst: any;

  constructor (
    private _elementRef: ElementRef,
    private _dialog: MatDialog
  ) {
  }

  getIcon() {
    return `icon-${this.join.type}-join`;
  }

  openJoinModal() {
    const data = {join: this.join};
    this._dialog.open(JoinDialogComponent, {
      width: 'auto',
      height: 'auto',
      data
    } as MatDialogConfig)
    .afterClosed().subscribe((result: JoinChangeEvent) => {
      this.change.emit(result);
    });
  }
}
