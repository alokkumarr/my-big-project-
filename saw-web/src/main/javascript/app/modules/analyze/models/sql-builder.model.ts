import { Sort } from './sort.model';
import { Join } from './join.model';
import Filter from './filter.model';
import { ArtifactColumn } from './artifact-column.model';

interface AbstractSqlBuilder {
  filters:         Filter[];
  sorts?:          Sort[];
  booleanCriteria: string;
  orderByColumns?: any[];
}

export interface SqlBuilderPivot extends AbstractSqlBuilder {
  columnFields:   ArtifactColumn[];
  rowFields:      ArtifactColumn[];
  dataFields:     ArtifactColumn[];
}

export interface SqlBuilderChart extends AbstractSqlBuilder {
  dataFields:     ArtifactColumn[];
  nodeFields:     ArtifactColumn[];
}

export interface SqlBuilderReport extends AbstractSqlBuilder {
  joins:          Join[];
}

export type SqlBuilder = Partial<SqlBuilderPivot> | Partial<SqlBuilderChart> | Partial<SqlBuilderReport>;
