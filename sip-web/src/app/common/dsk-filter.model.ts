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
  model: {
    operator: DSKFilterOperator;
    values: Array<String>;
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
