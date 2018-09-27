import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';

const template = require('./designer-description.component.html');
const style = require('./designer-description.component.scss');

@Component({
  selector: 'designer-description',
  template,
  styles: [style]
})
export class DesignerDescriptionComponent {
  @Output() public descriptionChange: EventEmitter<string> = new EventEmitter();
  @Input() public description: string;

  onDescriptionChange(description) {
    this.descriptionChange.emit(description);
  }
}
