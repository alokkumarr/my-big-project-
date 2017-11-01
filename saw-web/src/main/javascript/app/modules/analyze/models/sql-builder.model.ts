import Sort from './sort.model';
import Join from './join.model';
import Filter from './filter.model';
import ArtifactColumn from './artifact-column.model';

export default interface SqlBuilder {
  filters:         Filter[];
  booleanCriteria: string;
  columnFields?:   ArtifactColumn[];
  dataFields?:     ArtifactColumn[];
  nodeFields?:     ArtifactColumn[];
  rowFields?:      ArtifactColumn[];
  joins?:          Join[];
  orderByColumns?: any[];
  sorts?:          Sort[];
}
