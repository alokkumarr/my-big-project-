export enum DSKFilterOperator {
  ISIN = 'ISIN'
}

export enum DSKFilterBooleanCriteria {
  AND = 'AND',
  OR = 'OR'
}

export interface DSKFilterField {
  columnName: string;
  model: {
    operator: DSKFilterOperator;
    value: Array<String>;
  };
}

export interface DSKFilterGroup {
  booleanCriteria: DSKFilterBooleanCriteria;
  booleanQuery: Array<DSKFilterField | DSKFilterGroup>;
}
