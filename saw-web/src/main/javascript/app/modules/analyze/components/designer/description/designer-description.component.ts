declare const require: any;

import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';

const template = require('./designer-description.component.html');

@Component({
  selector: 'designer-description',
  template
})
export class DesignerDescriptionComponent {
  @Output() public descriptionChange: EventEmitter<string> = new EventEmitter();
  @Input() public description: string;

  onDescriptionChange(description) {
    this.descriptionChange.emit(description);
  }
}
