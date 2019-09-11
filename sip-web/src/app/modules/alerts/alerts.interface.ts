export interface AlertArtifact {
  dataField: string;
  alias: string;
  columnName: string;
  name?: string;
  displayName: String;
  type: string;
  aggregate?: string;
}
export interface AlertSipQuery {
  artifacts: {
    artifactName: string;
    fields: AlertArtifact[];
  }[];
  filters: {
    type: string;
    artifactsName: string;
    model: {
      operator?: string;
      value?: number;
      presetCal?: string;
    };
  }[];
}
export interface AlertConfig {
  alertRuleName: string;
  alertRuleDescription: any;
  alertSeverity: string;
  alertRulesSysId?: string;
  datapodId: string;
  datapodName: string;
  activeInd: boolean;
  notification: string[];
  categoryId?: string;
  product?: string;
  createdBy?: any;
  createdTime?: any;
  modifiedTime?: any;
  modifiedBy?: any;
  sipQuery?: AlertSipQuery;
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
