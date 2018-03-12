declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';

import {
  Artifact
} from '../types';
import { DesignerStates } from '../container';

const template = require('./designer-report.component.html');
require('./designer-report.component.scss');

@Component({
  selector: 'designer-report',
  template
})
export class DesignerReportComponent {
  @Input() artifacts: Artifact[];
  @Input() data;
  @Input() designerState: DesignerStates;
  public DesignerStates = DesignerStates;

}
