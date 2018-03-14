declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';

import {
  Artifact,
  FieldChangeEvent,
  Sort
} from '../../types';
import { DesignerStates } from '../../container';

const template = require('./multi-table-designer-layout.component.html');
require('./multi-table-designer-layout.component.scss');

@Component({
  selector: 'multi-table-designer-layout',
  template
})
export class MultiTableDesignerLayout {
  @Output() public settingsChange: EventEmitter<FieldChangeEvent> = new EventEmitter();
  @Input() artifacts: Artifact[];
  @Input() data;
  @Input() sorts: Sort[];
  @Input() designerState: DesignerStates;
}
