import { Component, Input, Output, EventEmitter } from '@angular/core';

interface IChoiceGroupItem {
  label: string;
  disabled: boolean;
  icon: { font?: string; svg?: string };
}

@Component({
  selector: 'choice-group-u',
  templateUrl: './choice-group.component.html',
  styleUrls: ['./choice-group.component.scss']
})
export class ChoiceGroupComponent {
  @Output() change: EventEmitter<IChoiceGroupItem> = new EventEmitter();
  @Input() items: IChoiceGroupItem[];
  @Input() value;

  constructor() {}

  onItemSelected(value) {
    if (value.disabled) {
      return;
    }
    this.change.emit(value);
  }

  trackByIndex(index) {
    return index;
  }
}
