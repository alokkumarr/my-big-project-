declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';

import {
  Artifact,
  DesignerChangeEvent,
  Sort
} from '../../types';
import { DesignerStates } from '../../container';

const template = require('./single-table-designer-layout.component.html');
require('./single-table-designer-layout.component.scss');

@Component({
  selector: 'single-table-designer-layout',
  template
})
export class SingleTableDesignerLayout {
  @Output() public settingsChange: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() artifacts: Artifact[];
  @Input() data;
  @Input() sorts: Sort[];
  @Input() designerState: DesignerStates;
}
