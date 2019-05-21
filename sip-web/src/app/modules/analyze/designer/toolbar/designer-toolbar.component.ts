import { Component, Output, EventEmitter } from '@angular/core';

import { DesignerToolbarAciton } from '../types';

@Component({
  selector: 'designer-toolbar',
  templateUrl: './designer-toolbar.component.html',
  styleUrls: ['./designer-toolbar.component.scss']
})
export class DesignerToolbarComponent {
  @Output()
  requestAction: EventEmitter<DesignerToolbarAciton> = new EventEmitter();
}
