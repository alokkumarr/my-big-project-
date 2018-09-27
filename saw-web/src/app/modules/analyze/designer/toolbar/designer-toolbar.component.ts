import {
  Component,
  Output,
  EventEmitter
} from '@angular/core';

import { DesignerToolbarAciton } from '../types';

const template = require('./designer-toolbar.component.html');
const style = require('./designer-toolbar.component.scss');

@Component({
  selector: 'designer-toolbar',
  template,
  styles: [style]
})
export class DesignerToolbarComponent {
  @Output() requestAction: EventEmitter<DesignerToolbarAciton> = new EventEmitter();


}
