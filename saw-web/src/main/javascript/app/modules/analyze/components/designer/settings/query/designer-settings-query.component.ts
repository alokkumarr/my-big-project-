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

const template = require('./designer-settings-query.component.html');
// require('./designer-settings-query.component.scss');

@Component({
  selector: 'designer-settings-query',
  template
})
export class DesignerSettingsQueryComponent {
  @Input() query: string;
  @Input() artifacts: Artifact[];
  @Input() data;

}
