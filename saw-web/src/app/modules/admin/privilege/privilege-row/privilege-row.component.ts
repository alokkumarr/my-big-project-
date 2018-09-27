import { Component, Input, Output, EventEmitter, OnChanges } from '@angular/core';

import {
  PRIVILEGE_NAMES,
  ALL_PRIVILEGES_STR,
  binaryString2BoolArray,
  decimal2BoolArray,
  getPrivilegeFromBoolArray
} from '../privilege-code-transformer';

const template = require('./privilege-row.component.html');
const style = require('./privilege-row.component.scss');
/** privilegeCode privilegeDesc
 * 0 => No access
 * 128 => All
 * some number => View, Create, Execute, Publish, Export
 */
interface Privilege {
  privilegeCode: number;
}

@Component({
  selector: 'tr[privilege-row]',
  template,
  styles: [style]
})
export class PrivilegeRowComponent implements OnChanges {
  @Output() categoryChange: EventEmitter<Privilege> = new EventEmitter();
  @Input() subCategory;

  privilegeCodeList: Boolean[];
  PRIVILEGE_NAMES = PRIVILEGE_NAMES;

  ngOnChanges(changes) {
    const subCategory = changes.subCategory.currentValue;
    const { privilegeCode } = subCategory;
    this.privilegeCodeList = decimal2BoolArray(privilegeCode);
  }


  onPrivilegeClicked(index) {
    this.privilegeCodeList[index] = !this.privilegeCodeList[index];
    const privilege = getPrivilegeFromBoolArray(this.privilegeCodeList);
    this.categoryChange.emit(privilege);
  }

  onAllClicked() {
    this.privilegeCodeList[8] = !this.privilegeCodeList[8];
    if (this.privilegeCodeList[8]) {
      this.privilegeCodeList = binaryString2BoolArray(ALL_PRIVILEGES_STR);
    }
    const privilege = getPrivilegeFromBoolArray(this.privilegeCodeList);
    this.categoryChange.emit(privilege);
  }

  onAccessClicked() {
    this.privilegeCodeList[0] = !this.privilegeCodeList[0];
    const privilege = getPrivilegeFromBoolArray(this.privilegeCodeList);
    this.categoryChange.emit(privilege);
  }
}
