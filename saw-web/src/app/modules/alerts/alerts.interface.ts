export interface AlertConfig {
  alertRulesSysId: number;
  datapodId: string;
  alertName: string;
  alertDescriptions?: any;
  category: string;
  alertSeverity: string;
  monitoringEntity: string;
  aggregation: string;
  operator: string;
  thresholdValue: number;
  activeInd: boolean;
  createdBy?: any;
  createdTime?: any;
  modifiedTime?: any;
  modifiedBy?: any;
}

export interface AlertDefinition {
  action: string;
  alertConfig?: AlertConfig;
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
