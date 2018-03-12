declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';

import {
  Artifact
} from '../../types';

const template = require('./designer-settings-multi-table.component.html');
// require('./designer-settings-multi-table.component.scss');

@Component({
  selector: 'designer-settings-multi-table',
  template
})
export class DesignerSettingsMultiTableComponent {
  @Input() artifacts: Artifact[];
  @Input() data;

}
