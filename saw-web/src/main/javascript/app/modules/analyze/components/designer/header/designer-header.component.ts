declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  Analysis,
  DesignerToolbarAciton
} from '../types';
import { DesignerStates } from '../container';

const template = require('./designer-header.component.html');
require('./designer-header.component.scss');

@Component({
  selector: 'designer-header',
  template
})
export class DesignerHeaderComponent {
  @Output() public onBack: EventEmitter<any> = new EventEmitter();
  @Output() requestAction: EventEmitter<DesignerToolbarAciton> = new EventEmitter();
  // TODO replace any with Analysis model currently analyze model -> rename to analysis
  @Input() public analysis: Analysis;
  @Input() public isInDraftMode: boolean;
  @Input() public designerState: DesignerStates;

  public DesignerStates = DesignerStates;
}
