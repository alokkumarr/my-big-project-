declare const require: any;

import {
  Component,
  Output,
  EventEmitter
} from '@angular/core';

import { DesignerToolbarAciton } from '../types';

const template = require('./designer-toolbar.component.html');
require('./designer-toolbar.component.scss');

@Component({
  selector: 'designer-toolbar',
  template
})
export class DesignerToolbarComponent {
  @Output() requestAction: EventEmitter<DesignerToolbarAciton> = new EventEmitter();


}
