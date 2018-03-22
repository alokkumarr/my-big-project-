declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter,
  ElementRef
} from '@angular/core';
import * as find from 'lodash/find';

const template = require('./js-plumb-canvas.component.html');

@Component({
  selector: 'js-plumb-endpoint-u',
  template
})
export class JsPlumbEndpointComponent {


  constructor (
    private _elementRef: ElementRef
  ) {}

  ngOnInit() {
  }

  ngOnDestroy() {
  }
}
