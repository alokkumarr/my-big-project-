import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { Analysis } from '../types';

const template = require('./designer-header.component.html');
require('./designer-header.component.scss');

@Component({
  selector: 'designer-header',
  template
})
export class DesignerHeaderComponent {
  @Output() public onBack: EventEmitter<any> = new EventEmitter();
  @Output() public onSave: EventEmitter<any> = new EventEmitter();
  // TODO replace any with Analysis model currently analyze model -> rename to analysis
  @Input() public analysis: Analysis;
  @Input() public isInDraftMode: boolean;
}
