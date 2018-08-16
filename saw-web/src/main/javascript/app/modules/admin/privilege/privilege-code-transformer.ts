import * as map from 'lodash/map';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpCompact from 'lodash/fp/compact';
import * as fpJoin from 'lodash/fp/join';

const ALL_PRIVILEGES_DECIMAL = 128;
export const ALL_PRIVILEGES_STR = '1111111110000000';
export const PRIVILEGE_NAMES = ['Create', 'Execute', 'Publish', 'Fork', 'Edit', 'Export', 'Delete'];

export function getPrivilegeCodeString(privilegeCode) {
  switch (privilegeCode) {
  case 0:
    return '0000000000000000';
  case ALL_PRIVILEGES_DECIMAL:
    return ALL_PRIVILEGES_STR;
  default:
    return (privilegeCode).toString(2);
  }
}

export function decimal2BoolArray(privilegeCode) {
  const privilegeCodeString = getPrivilegeCodeString(privilegeCode);
  return binaryString2BoolArray(privilegeCodeString);
}

export function binaryString2BoolArray(privilegeCodeString) {
  return map(privilegeCodeString, binStr => binStr === '1');
}

export function boolArray2Decimal(boolArray) {
  const privilegeCodeString = map(boolArray, bool => bool ? '1' : '0').join('');
  return parseInt(privilegeCodeString, 2);
}

export function getPrivilegeFromBoolArray(privilegeCodeList) {
  const hasAllPrivileges = privilegeCodeList[8];
  if (hasAllPrivileges) {
    return {
      privilegeCode: 128
    };
  }

  const hasNoAccessPrivilege = !privilegeCodeList[0];
  if (hasNoAccessPrivilege) {
    return {
      privilegeCode: 0
    };
  }

  return {
    privilegeCode: boolArray2Decimal(privilegeCodeList)
  };
}

export function getPrivilegeDescription(privilegeCode) {
  switch(privilegeCode) {
  case 0:
    return 'No-Access';
  case 128:
    return 'All';
  default:
    const privilegeCodeList = decimal2BoolArray(privilegeCode);
    return fpPipe(
      fpCompact,
      fpJoin(', ')
    )(map(privilegeCodeList, (privilege, index) => privilege ? PRIVILEGE_NAMES[index] : null));
  }
}
