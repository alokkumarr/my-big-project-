export interface AlertConfig {
  datapodId: string;
  datapodName: string;
  alertName: string;
  alertDescription: any;
  alertSeverity: string;
  monitoringEntity: string;
  aggregation: string;
  operator: string;
  thresholdValue: number;
  activeInd: boolean;
  alertRulesSysId?: string;
  category?: string;
  product?: string;
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

export interface GridPagingOptions {
  take?: number;
  skip?: number;
}

export interface GridData {
  data: any[];
  totalCount: number;
}

export interface AlertIds {
  alertRulesSysId: number;
  alertTriggerSysId: number;
}

export interface AlertDateCount {
  date: string;
  count: string;
}
export interface AlertDateSeverity {
  count: string;
  alertSeverity: string;
}

export interface AlertChartData {
  y: number[];
  x: string[];
}

export interface AlertFilterModel {
  preset: string;
  endTime?: string;
  startTime?: string;
  groupBy: string;
}

export interface AlertsStateModel {
  alertFilter: AlertFilterModel;
  allAlertsCountChartData: AlertChartData;
  allAlertsSeverityChartData: AlertChartData;
  selectedAlertCountChartData: AlertChartData;
  selectedAlertRuleDetails: AlertConfig;
}
