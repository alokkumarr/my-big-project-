import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'designer-description',
  templateUrl: './designer-description.component.html',
  styleUrls: ['./designer-description.component.scss']
})
export class DesignerDescriptionComponent {
  @Output() public descriptionChange: EventEmitter<string> = new EventEmitter();
  @Input() public description: string;

  onDescriptionChange(description) {
    this.descriptionChange.emit(description);
  }
}
