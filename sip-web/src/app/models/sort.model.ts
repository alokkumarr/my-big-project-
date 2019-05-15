export interface Sort {
  order: string;
  columnName: string;
  type: string;
  aggregate?: string;
  tableName?: string; // this represents table name in old analysis structure
  artifacts?: string; // this represents table name in new analysis structure
}
