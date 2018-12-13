import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'privilege-editor',
  templateUrl: 'privilege-editor.component.html',
  styleUrls: ['privilege-editor.component.scss']
})
export class PrivilegeEditorComponent {
  @Output()
  privilegeChange: EventEmitter<{
    index: number;
    privilege: any;
  }> = new EventEmitter();
  @Input() subCategories;
  @Input() activePrivilegeId;
  constructor() {}

  onCategoryChange(index, privilege) {
    this.privilegeChange.emit({ index, privilege });
  }
}
