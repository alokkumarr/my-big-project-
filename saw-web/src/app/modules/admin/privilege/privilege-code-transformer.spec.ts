import {
  getPrivilegeCodeString,
  NO_PRIVILEGES_STR,
  ALL_PRIVILEGES_STR,
  binaryString2BoolArray,
  decimal2BoolArray,
  boolArray2Decimal,
  getPrivilegeFromBoolArray,
  getPrivilegeDescription
} from './privilege-code-transformer';

describe('Privilege Code Transformer', () => {
  describe('getPrivilegeCodeString', () => {
    it('should return all zeros for zero', () => {
      expect(getPrivilegeCodeString(0)).toEqual(NO_PRIVILEGES_STR);
    });

    it('should return all privileges for 128', () => {
      expect(getPrivilegeCodeString(128)).toEqual(ALL_PRIVILEGES_STR);
    });

    it('should return binary of all other numbers', () => {
      expect(getPrivilegeCodeString(1)).toEqual('1');
      expect(getPrivilegeCodeString(45056)).toEqual('1011000000000000');
    });
  });

  describe('binaryString2BoolArray', () => {
    it('should convert binary to bool array', () => {
      expect(binaryString2BoolArray('111')).toEqual([true, true, true]);
      expect(binaryString2BoolArray('0101')).toEqual([
        false,
        true,
        false,
        true
      ]);
    });
  });

  describe('decimal2BoolArray', () => {
    it('should convert decimal to bool array', () => {
      expect(decimal2BoolArray(7)).toEqual([true, true, true]);
      expect(decimal2BoolArray(16)).toEqual([true, false, false, false, false]);
    });
  });

  describe('boolArray2Decimal', () => {
    it('should convert bool array to decimal', () => {
      expect(boolArray2Decimal([false, true, true])).toEqual(3); // binary (011) = decimal (3)
      expect(boolArray2Decimal([true, false, true, false])).toEqual(10); // binary (1010) = decimal (10)
    });
  });

  describe('getPrivilegeFromBoolArray', () => {
    it('should convert bool array to decimal', () => {
      expect(
        getPrivilegeFromBoolArray([true, false, true, false]).privilegeCode
      ).toEqual(10); // binary (1010) = decimal (10)

      expect(
        getPrivilegeFromBoolArray([false, false, true, false]).privilegeCode
      ).toEqual(0); // if first bit is 0 (false), then no privilege

      expect(
        getPrivilegeFromBoolArray([
          true,
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          true
        ]).privilegeCode
      ).toEqual(128); // if ninth bit is 1 (true), then all privileges
    });
  });

  describe('getPrivilegeDescription', () => {
    it('should handle No access case', () => {
      expect(getPrivilegeDescription(0)).toEqual('No-Access');
    });

    it('should handle All Access case', () => {
      expect(getPrivilegeDescription(128)).toEqual('All');
    });

    it('should handle Execute, Publish', () => {
      expect(getPrivilegeDescription(45056)).toEqual('View, Execute, Publish');
    });

    it('should handle View', () => {
      expect(getPrivilegeDescription(32768)).toEqual('View');
    });

    it('should handle Create', () => {
      expect(getPrivilegeDescription(49152)).toEqual('View, Create');
    });

    it('should handle Create, Execute', () => {
      expect(getPrivilegeDescription(57344)).toEqual('View, Create, Execute');
    });

    it('should handle Create, Edit, Delete', () => {
      expect(getPrivilegeDescription(50432)).toEqual(
        'View, Create, Edit, Delete'
      );
    });
  });
});
