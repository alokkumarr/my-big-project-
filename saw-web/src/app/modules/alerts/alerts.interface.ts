export interface AlertConfig {
  activeInd: string;
  aggregation: string;
  alertSeverity: string;
  categoryId: string;
  datapodId: string;
  datapodName: string;
  monitoringEntity: string;
  operator: string;
  product: string;
  ruleDescriptions: string;
  ruleName: string;
  thresholdValue: number;
}

export interface DatapodMetric {
  aliasName?: string;
  columnName: string;
  displayName: string;
  filterEligible?: boolean;
  joinEligible?: boolean;
  name: string;
  table: string;
  type: string;
  kpiEligible?: boolean;
}
