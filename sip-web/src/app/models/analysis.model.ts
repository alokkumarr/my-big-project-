import { Artifact } from './artifact.model';
import { SqlBuilder } from './sql-builder.model';
import { Schedule } from './schedule.model';
import { Repository } from './repository.model';
import { EsRepository } from './es-repository.model';
import { OutputFile } from './output-file.model';
import { LabelOptions } from './label-options.model';
import { Legend } from './legend.model';
import { Axis } from './axis.model';
import { AnalysisType } from './analysis-type.model';
import { MapSettings } from './map-settings.model';

// Keep the fields sorted by field name for easy lookup by humans
// Most text editors have a sort functionality
export interface Analysis {
  artifacts: Artifact[];
  categoryId: number;
  chartTitle: string;
  checked: null | boolean | string;
  createdTimestamp: number;
  crondetails?: any;
  customerCode?: string;
  dataSecurityKey?: string;
  description: string;
  disabled: null | boolean | string;
  edit?: boolean;
  esRepository?: EsRepository;
  executionType?: string;
  groupByColumns?: any[];
  id: string;
  isScheduled: string;
  mapSettings?: MapSettings;
  metric: string;
  metricId: string;
  metricName: string;
  metrics?: string[];
  module?: string;
  name: string;
  outputFile?: OutputFile;
  parentAnalysisId?: string;
  parentCategoryId?: string | number;
  parentLastModified?: number;
  repository?: Repository;
  saved: boolean;
  schedule?: Schedule;
  scheduled: null;
  scheduleHuman?: string;
  semanticId: string;
  sqlBuilder: SqlBuilder;
  supports?: string[];
  type: AnalysisType;
  updatedTimestamp?: number;
  updatedUserName?: string;
  userFullName: string;
  createdBy?: string;
  userId: number;
  // groupByColumns should be deprecated
}

export interface AnalysisReport extends Analysis {
  query?: string;
  queryManual?: string;
}

export interface AnalysisChart extends Analysis {
  legend?: Legend;
  isInverted?: boolean;
  chartType?: string;
  labelOptions?: LabelOptions;
  xAxis?: Axis;
  yAxis?: Axis;
}
