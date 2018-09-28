import {
  Component,
  Output,
  EventEmitter
} from '@angular/core';

import { DesignerToolbarAciton } from '../types';

const style = require('./designer-toolbar.component.scss');

@Component({
  selector: 'designer-toolbar',
  templateUrl: './designer-toolbar.component.html',
  styles: [style]
})
export class DesignerToolbarComponent {
  @Output() requestAction: EventEmitter<DesignerToolbarAciton> = new EventEmitter();


}
