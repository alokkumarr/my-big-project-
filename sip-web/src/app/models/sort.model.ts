export interface Sort {
  order: string;
  columnName: string;
  type: string;
  aggregate?: string;
  artifactsName?: string; // this represents table name in old analysis structure
  artifacts?: string; // this represents table name in new analysis structure
}
