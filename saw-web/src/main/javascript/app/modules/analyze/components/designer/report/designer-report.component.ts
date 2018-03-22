declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';

import {
  Artifact,
  FieldChangeEvent
} from '../types';
import { DesignerStates } from '../container';

const template = require('./designer-report.component.html');
require('./designer-report.component.scss');

@Component({
  selector: 'designer-report',
  template
})
export class DesignerReportComponent {
  @Output() public settingsChange: EventEmitter<FieldChangeEvent> = new EventEmitter();
  @Input() artifacts: Artifact[];
  @Input() data;
  @Input() designerState: DesignerStates;
  public DesignerStates = DesignerStates;

}
