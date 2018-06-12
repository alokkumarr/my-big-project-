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
import { DesignerStates } from '../consts';

const template = require('./designer-header.component.html');
require('./designer-header.component.scss');

@Component({
  selector: 'designer-header',
  template
})
export class DesignerHeaderComponent {
  @Output() public onBack: EventEmitter<null> = new EventEmitter();
  @Output() requestAction: EventEmitter<DesignerToolbarAciton> = new EventEmitter();
  @Input() public analysis: Analysis;
  @Input() public isInDraftMode: boolean;
  @Input() public isInQueryMode: boolean;
  @Input() public designerState: DesignerStates;
  @Input() public areMinRequirmentsMet: boolean;

  public DesignerStates = DesignerStates;
}
