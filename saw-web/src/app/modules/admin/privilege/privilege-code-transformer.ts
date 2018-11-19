import * as map from 'lodash/map';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpCompact from 'lodash/fp/compact';
import * as fpJoin from 'lodash/fp/join';

const ALL_PRIVILEGES_DECIMAL = 128;
export const NO_PRIVILEGES_STR = '0000000000000000';
export const ALL_PRIVILEGES_STR = '1111111110000000';
export const PRIVILEGE_NAMES = [
  'Create',
  'Execute',
  'Publish',
  'Fork',
  'Edit',
  'Export',
  'Delete'
];

/**
 * getPrivilegeCodeString
 * Converts decimal to binary and accounts for special cases for privileges
 *
 * @param privilegeCode
 * @returns {string}
 */
export function getPrivilegeCodeString(privilegeCode: number): string {
  switch (privilegeCode) {
    case 0:
      return NO_PRIVILEGES_STR;
    case ALL_PRIVILEGES_DECIMAL:
      return ALL_PRIVILEGES_STR;
    default:
      return privilegeCode.toString(2);
  }
}

/**
 * decimal2BoolArray
 * Converts decimal to boolean array.
 * For example, decimal 5 is 101 binary. So decimal2BoolArray(3) will give [true, false, true]
 *
 * @param {number} privilegeCode
 * @returns {Array<Boolean>}
 */
export function decimal2BoolArray(privilegeCode: number): Array<Boolean> {
  const privilegeCodeString = getPrivilegeCodeString(privilegeCode);
  return binaryString2BoolArray(privilegeCodeString);
}

/**
 * binaryString2BoolArray
 * Converts binary to boolean array. 0 is false. 1 is true.
 * Example: binaryString2BoolArray('101') returns [true, false, true]
 *
 * @param {string} privilegeCodeString
 * @returns {Array<Boolean>}
 */
export function binaryString2BoolArray(
  privilegeCodeString: string
): Array<Boolean> {
  return map(privilegeCodeString, binStr => binStr === '1');
}

/**
 * boolArray2Decimal
 * Translates boolean array to binary and converts it to decimal.
 * Example, [true, false, true] is translate to '101' (binary) and finally
 * returned as 5.
 *
 * @param {Array<Boolean>} boolArray
 * @returns {number}
 */
export function boolArray2Decimal(boolArray: Array<Boolean>): number {
  const privilegeCodeString = map(boolArray, bool => (bool ? '1' : '0')).join(
    ''
  );
  return parseInt(privilegeCodeString, 2);
}

interface PrivilegeCodeResponse {
  privilegeCode: number;
}
/**
 * getPrivilegeFromBoolArray
 * In general, converts boolean array to decimal (similar to boolArray2Decimal).
 * Handles special privilege cases. For example. if 9th bit it true, then returns 128,
 * which stands for all privileges. Doesn't matter what other bits are (un)set.
 *
 * @param {Array<Boolean>} privilegeCodeList
 * @returns {PrivilegeCodeResponse}
 */
export function getPrivilegeFromBoolArray(
  privilegeCodeList: Array<Boolean>
): PrivilegeCodeResponse {
  const hasNoAccessPrivilege = !privilegeCodeList[0];
  if (hasNoAccessPrivilege) {
    return {
      privilegeCode: 0
    };
  }

  const hasAllPrivileges = privilegeCodeList[8];
  if (hasAllPrivileges) {
    return {
      privilegeCode: 128
    };
  }

  return {
    privilegeCode: boolArray2Decimal(privilegeCodeList)
  };
}

export function getPrivilegeDescription(privilegeCode: number): string {
  switch (privilegeCode) {
    case 0:
      return 'No-Access';
    case 128:
      return 'All';
    default:
      /* In backend, first bit stands for 'View', not 'Create'.
       * Following line makes sure the privilege list we use to
       * calculate description matches meaning of corresponding bits.
       */
      const PRIVILEGES = ['View', ...PRIVILEGE_NAMES, 'All'];
      const privilegeCodeList = decimal2BoolArray(privilegeCode);
      return fpPipe(fpCompact, fpJoin(', '))(
        map(
          privilegeCodeList,
          (privilege, index) => (privilege ? PRIVILEGES[index] : null)
        )
      );
  }
}
