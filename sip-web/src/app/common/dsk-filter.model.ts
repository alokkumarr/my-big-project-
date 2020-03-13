export enum DSKFilterOperator {
  ISIN = 'ISIN'
}

export enum DSKFilterBooleanCriteria {
  AND = 'AND',
  OR = 'OR'
}

export interface DSKFilterField {
  attributeId?: string;
  columnName: string;
  type?: string;
  artifactsName?: string;
  isGlobalFilter?: boolean;
  isRuntimeFilter?: boolean;
  isOptional?: boolean;
  uuid?;
  model: {
    operator;
    values;
  };
}

export interface DSKFilterGroup {
  attributeId?: string;
  booleanCriteria: DSKFilterBooleanCriteria;
  booleanQuery: Array<DSKFilterField | DSKFilterGroup>;
}

export interface DSKSecurityGroup {
  securityGroupSysId: number;
  groupName: string;
  groupDescription?: string;
  dskAttributes: DSKFilterGroup;
}
