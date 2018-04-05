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
} from '../types';
import { DesignerStates } from '../container';

const template = require('./designer-report.component.html');
require('./designer-report.component.scss');

@Component({
  selector: 'designer-report',
  template
})
export class DesignerReportComponent {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() artifacts: Artifact[];
  @Input() data;
  @Input() sorts: Sort[];
  @Input() designerState: DesignerStates;
  public DesignerStates = DesignerStates;

  onReportGridChange(event) {
    this.change.emit(event);
  }
}
