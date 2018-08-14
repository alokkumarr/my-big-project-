import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as map from 'lodash/map';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpCompact from 'lodash/fp/compact';
import * as fpJoin from 'lodash/fp/join';
const template = require('./privilege-row.component.html');
/** privilegeCode privilegeDesc
 * 0 => No access
 * 128 => All
 * some number => View, Create, Execute, Publish, Export
 */
type Privilege = {
  privilegeDesc: string,
  privilegeCode: number
}

const ALL_PRIVILEGES_DECIMAL = 128;
const ALL_PRIVILEGES_STR = '0000000010000000';
const PRIVILEGE_NAMES = ['Access', 'Create', 'Execute', 'Publish', 'Fork', 'Edit', 'Export', 'Delete'];
const PRIVILEGE_NAME_LIST = [...PRIVILEGE_NAMES, 'All', '', '', '', '', '', '', ''];
const PRIVILEGE_CODE_LIST = [false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false];

@Component({
  selector: 'tr[privilegeRow]',
  template
})
export class PrivilegeRowComponent {
  @Output() categoryChange: EventEmitter<Privilege> = new EventEmitter();
  @Input() subCategory;

  privilegeCodeList: Boolean[]
  PRIVILEGE_NAMES = PRIVILEGE_NAMES;

  ngOnInit() {
    const { privilegeCode } = this.subCategory;
    const privilegeCodeString = this.getPrivilegeCodeString(privilegeCode);
    this.privilegeCodeList = this.binaryString2BoolArray(privilegeCodeString);
    console.log('privilegeCode', privilegeCodeString);
    console.log('privilegeCodeArray', this.privilegeCodeList);
  }

  getPrivilegeCodeString(privilegeCode) {
    switch (privilegeCode) {
    case 0:
      return '0000000000000000';
    case ALL_PRIVILEGES_DECIMAL:
      return ALL_PRIVILEGES_STR;
    default:
      return (privilegeCode).toString(2);
    }
  }

  binaryString2BoolArray(privilegeCodeString) {
    return map(privilegeCodeString, binStr => binStr === '1');
  }

  boolArray2Decimal(boolArray) {
    const privilegeCodeString = map(boolArray, bool => bool ? '1' : '0').join('');
    return parseInt(privilegeCodeString, 2);
  }

  getPrivilegeFromBoolArray(privilegeCodeList) {
    const hasAllPrivileges = privilegeCodeList[8];
    if (hasAllPrivileges) {
      return {
        privilegeDesc: 'All',
        privilegeCode: 128
      };
    }
    const hasNoAccessPrivilege = !privilegeCodeList[0];
    if (hasNoAccessPrivilege) {
      return {
        privilegeDesc: 'No-Access',
        privilegeCode: 0
      };
    }
    const privilegeDesc = fpPipe(
      fpCompact,
      fpJoin(', ')
    )(map(privilegeCodeList, (privilege, index) => privilege ? PRIVILEGE_NAMES[index] : null));

    return {
      privilegeDesc,
      privilegeCode: this.boolArray2Decimal(privilegeCodeList)
    };
  }

  onPrivilegeClicked(index, value) {
    this.privilegeCodeList[index] = !value;
    const privilege = this.getPrivilegeFromBoolArray(this.privilegeCodeList);
    this.categoryChange.emit(privilege);
  }

  onAllClicked(value) {
    if (!value) {
      this.privilegeCodeList = this.binaryString2BoolArray(ALL_PRIVILEGES_STR);
    } else {
      this.privilegeCodeList[8] = !value;
    }
    const privilege = this.getPrivilegeFromBoolArray(this.privilegeCodeList);
    this.categoryChange.emit(privilege);
  }

  onAccessClicked(value) {
    this.privilegeCodeList[0] = !value;
    const privilege = this.getPrivilegeFromBoolArray(this.privilegeCodeList);
    this.categoryChange.emit(privilege);
  }
}
