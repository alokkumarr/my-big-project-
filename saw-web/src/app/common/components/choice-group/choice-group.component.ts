import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';

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
export class ChoiceGroupComponent implements OnInit {
  @Output() change: EventEmitter<IChoiceGroupItem> = new EventEmitter();
  @Input() items: IChoiceGroupItem[];
  @Input() value;

  selectedItem;
  selectedSubItem;

  constructor() {}

  ngOnInit() {
    this.selectedItem = this.items[0];
  }

  onItemSelected(value) {
    this.selectedItem = value;
    this.selectedSubItem = null;
    if (value.disabled) {
      return;
    }
    this.change.emit(value);
  }

  onSubItemSelected(value) {
    this.selectedSubItem = value;
    if (value.disabled) {
      return;
    }
    this.change.emit(value);
  }

  trackByIndex(index) {
    return index;
  }
}
