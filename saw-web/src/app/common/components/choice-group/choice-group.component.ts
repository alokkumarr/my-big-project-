import { Component, Input, Output, EventEmitter } from '@angular/core';

const template = require('./choice-group.component.html');
const style = require('./choice-group.component.scss');

interface IChoiceGroupItem {
  label: string;
  disabled: boolean;
  icon: {font?: string, svg?: string};
}

@Component({
  selector: 'choice-group-u',
  template,
  styles: [style]
})
export class ChoiceGroupComponent {

  @Output() change: EventEmitter<IChoiceGroupItem> = new EventEmitter();
  @Input() items: IChoiceGroupItem[];
  @Input() value;

  constructor() { }

  onItemSelected(value) {
    if (value.disabled) {
      return;
    }
    this.change.emit(value);
  }
}
