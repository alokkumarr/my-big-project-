export interface FilterModel {
  lte?:         string;
  gte?:         string;
  preset?:      string;
  modelValues?: string[];
  value?:       number;
  otherValue?:  number;
  operator?:    string;
}
