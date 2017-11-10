import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';

const template = require('./designer-header.component.html');
require('./designer-header.component.scss');

@Component({
  selector: 'designer-header',
  template
})
export default class DesignerHeaderComponent {
  @Output() onBack: EventEmitter<any> = new EventEmitter();
  @Output() onSave: EventEmitter<any> = new EventEmitter();
  // TODO replace any with Analysis model currently analyze model -> rename to analysis
  @Input() analysis: any;
  @Input() isInDraftMode: boolean;
}
