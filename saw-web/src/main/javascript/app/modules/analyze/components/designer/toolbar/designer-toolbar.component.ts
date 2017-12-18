import {
  Component,
  Input,
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
  @Input() isDataOutOfSynch: boolean;
  @Output() requestAction: EventEmitter<DesignerToolbarAciton> = new EventEmitter();


}
