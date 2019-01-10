import { Component, Input, Output, EventEmitter } from '@angular/core';

import {
  PRIVILEGE_NAMES,
  ALL_PRIVILEGES_STR,
  binaryString2BoolArray,
  decimal2BoolArray,
  getPrivilegeFromBoolArray
} from '../privilege-code-transformer';

import * as values from 'lodash/values';

/** privilegeCode privilegeDesc
 * 0 => No access
 * 128 => All
 * some number => View, Create, Execute, Publish, Export
 */
interface Privilege {
  privilegeCode: number;
}

@Component({
  // tslint:disable-next-line:component-selector
  selector: 'tr[privilege-row]',
  templateUrl: './privilege-row.component.html',
  styleUrls: ['./privilege-row.component.scss']
})
export class PrivilegeRowComponent {
  @Output() categoryChange: EventEmitter<Privilege> = new EventEmitter();

  PRIVILEGE_NAMES = PRIVILEGE_NAMES;
  privilegeCodeList: Boolean[];
  subCategory;
  @Input('subCategory') set _subCategory(subCategory) {
    if (!subCategory) {
      return;
    }
    this.subCategory = subCategory;
    const { privilegeCode } = subCategory;
    this.privilegeCodeList = decimal2BoolArray(privilegeCode);
  }

  allowedPrivileges: {
    [privilegeName: string]: boolean;
  } = PRIVILEGE_NAMES.reduce(
    (accum, privilegeName: string) => ({
      ...accum,
      [privilegeName.toUpperCase()]: true
    }),
    {}
  );
  @Input('allowedPrivileges') set _allowedPrivileges(allowedPrivileges) {
    if (!allowedPrivileges) {
      return;
    }
    const allowedPrivilegeList: string[] = values(allowedPrivileges.priviliges);
    this.allowedPrivileges = allowedPrivilegeList.reduce(
      (accum, privilegeName: string) => ({
        ...accum,
        [privilegeName.toUpperCase()]: true
      }),
      {}
    );
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
