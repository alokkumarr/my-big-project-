export interface FilterModel {
  lte?:         string;
  gte?:         string;
  modelValues?: string[];
  value?:       number;
  otherValue?:  number;
  operator?:    string;
}
