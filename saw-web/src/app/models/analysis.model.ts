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

export interface Analysis {
  description: string;
  metricId: string;
  checked: null | boolean | string;
  categoryId: number;
  artifacts: Artifact[];
  createdTimestamp: number;
  updatedTimestamp?: number;
  disabled: null | boolean | string;
  id: string;
  isScheduled: string;
  metric: string;
  metrics?: string[];
  metricName: string;
  name: string;
  type: AnalysisType;
  saved: boolean;
  semanticId: string;
  scheduled: null;
  sqlBuilder: SqlBuilder;
  userId: number;
  userFullName: string;
  dataSecurityKey?: string;
  module?: string;
  schedule?: Schedule;
  repository?: Repository;
  esRepository?: EsRepository;
  scheduleHuman?: string;
  customerCode?: string;
  executionType?: string;
  edit?: boolean;
  outputFile?: OutputFile;
  groupByColumns?: any[];
  crondetails?: any;
  chartTitle: string;
  parentAnalysisId?: string;
  parentCategoryId?: string | number;
  parentLastModified?: number;
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
