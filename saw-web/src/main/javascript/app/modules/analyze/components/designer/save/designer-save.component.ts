import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  Analysis
} from '../types';

const template = require('./designer-save.component.html');

@Component({
  selector: 'designer-save',
  template
})
export class DesignerSaveComponent {
  @Output() public nameChange: EventEmitter<string> = new EventEmitter();
  @Input() public analysis: Analysis;

  onDescriptionChange(description) {
    this.nameChange.emit(description);
  }
}
