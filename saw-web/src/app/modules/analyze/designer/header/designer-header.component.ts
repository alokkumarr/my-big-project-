import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Analysis, DesignerToolbarAciton } from '../types';
import { DesignerStates } from '../consts';

@Component({
  selector: 'designer-header',
  templateUrl: './designer-header.component.html',
  styleUrls: ['./designer-header.component.scss']
})
export class DesignerHeaderComponent {
  @Output() public onBack: EventEmitter<null> = new EventEmitter();
  @Output()
  requestAction: EventEmitter<DesignerToolbarAciton> = new EventEmitter();
  @Input() public analysis: Analysis;
  @Input() public isInDraftMode: boolean;
  @Input() public isInQueryMode: boolean;
  @Input() public designerState: DesignerStates;
  @Input() public areMinRequirmentsMet: boolean;

  public DesignerStates = DesignerStates;
}
