export interface AlertSipQuery {
  artifacts: {
    dataField: string;
    area: string;
    alias: string;
    columnName: string;
    name: string;
    displayName: String;
    type: string;
    aggregate?: string;
  }[];
  filters: {
    type: string;
    artifactsName: string;
    model: {
      operator: string;
      value: number;
    };
  }[];
}
export interface AlertConfig {
  alertRuleName: string;
  alertRuleDescription: any;
  alertSeverity: string;
  alertRulesSysId?: string;
  monitoringEntity: string;
  datapodId: string;
  datapodName: string;
  activeInd: boolean;
  categoryId?: string;
  product?: string;
  createdBy?: any;
  createdTime?: any;
  modifiedTime?: any;
  modifiedBy?: any;
  aggregation: string;
  operator: string;
  thresholdValue: number;
  sipQuery: AlertSipQuery;
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
