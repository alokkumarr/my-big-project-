import { Component, Input, Output, EventEmitter } from '@angular/core';
const template = require('./privilege-editor.component.html');

@Component({
  selector: 'privilege-editor',
  template
})

export class PrivilegeEditorComponent {
  @Output() privilegeChange: EventEmitter<{index: number, privilege: any}> = new EventEmitter();
  @Input() subCategories;
  @Input() activePrivilegeId;
  constructor() { }

  onCategoryChange(index, privilege) {
    this.privilegeChange.emit({index, privilege});
  }
}
