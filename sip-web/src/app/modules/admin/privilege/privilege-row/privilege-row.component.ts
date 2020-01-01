import { Component, Input, Output, EventEmitter } from '@angular/core';
import * as toUpper from 'lodash/toUpper';

import {
  PRIVILEGE_NAMES,
  ALL_PRIVILEGES_STR,
  binaryString2BoolArray,
  decimal2BoolArray,
  getPrivilegeFromBoolArray
} from '../privilege-code-transformer';
import { SYSTEM_CATEGORY_OPERATIONS } from './../../../../common/consts';

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
  isDraftsSubCategory = false;
  systemCategory;

  @Input() categoryName: string;

  @Input('subCategory') set _subCategory(subCategory) {
    if (!subCategory) {
      return;
    }
    this.isDraftsSubCategory =
      this.categoryName === 'My Analysis' &&
      toUpper(subCategory.subCategoryName) === 'DRAFTS';
    this.subCategory = subCategory;
    const { privilegeCode } = subCategory;
    this.privilegeCodeList = decimal2BoolArray(privilegeCode);
    // set all privilege based on syster folder category settings.
    this.systemCategory = subCategory.systemCategory;
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

  hasOnlyAccessPrivilege = false;

  @Input('allowedPrivileges') set _allowedPrivileges(privileges: string[]) {
    if (!privileges) {
      return;
    }
    const allowedPrivilegeList: string[] = privileges;
    this.allowedPrivileges = allowedPrivilegeList.reduce(
      (accum, privilegeName: string) => ({
        ...accum,
        [privilegeName.toUpperCase()]: true
      }),
      {}
    );
    this.hasOnlyAccessPrivilege =
      allowedPrivilegeList &&
      allowedPrivilegeList.length === 1 &&
      this.allowedPrivileges['ACCESS'];
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
    const hasAccess = this.privilegeCodeList[0];
    this.privilegeCodeList[0] = !hasAccess;
    if (this.isDraftsSubCategory) {
      if (hasAccess) {
        this.onAllClicked();
      } else {
        this.onAllClicked();
      }
    }
    const privilege = getPrivilegeFromBoolArray(this.privilegeCodeList);
    this.categoryChange.emit(privilege);
  }


  checkPermissions(privilegeName) {
    if (this.systemCategory && SYSTEM_CATEGORY_OPERATIONS.includes(privilegeName)) {
      return true;
    }
    return false;
  }
}
