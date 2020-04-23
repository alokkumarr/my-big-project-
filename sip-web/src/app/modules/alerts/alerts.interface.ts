export interface AlertArtifact {
  dataField: string;
  alias: string;
  columnName: string;
  name?: string;
  displayName: string;
  type: string;
  aggregate?: string;
  table?: string;
  format?: string;
  visibleIndex?: number;
  groupInterval?: string;
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
  metricsColumn: string;
  aggregationType: string;
  operator: string;
  thresholdValue: string;
  otherThresholdValue: string;
  attributeName: string;
  attributeValue: string;
  datapodId: string;
  datapodName: string;
  activeInd: boolean;
  subscribers: Array<string>;
  categoryId?: string;
  product?: string;
  createdBy?: any;
  createdTime?: any;
  triggerOnLookback?: boolean;
  lookbackColumn?: string;
  lookbackPeriod?: string;
  modifiedTime?: any;
  modifiedBy?: any;
  monitoringType?: string;
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
  sort?: {
    selector: string;
    desc: boolean;
  }[];
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
  fieldName: string;
  type: string;
  preset?: string;
  value?: string;
  lte?: number;
  gte?: number;
  operator?: string;
}

export interface AlertFilterEvent {
  isValid: boolean;
  filter: AlertFilterModel;
}

export interface AlertsStateModel {
  alertFilters: AlertFilterModel[];
  editedAlertFilters: AlertFilterModel[];
  editedAlertsValidity: boolean[];
  allAlertsCountChartData: AlertChartData;
  allAlertsSeverityChartData: AlertChartData;
  selectedAlertCountChartData: AlertChartData;
  selectedAlertRuleDetails: AlertConfig;
  allAttributeValues: string[];
}
