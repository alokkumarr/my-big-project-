import ArtifactColumn from './artifact-column.model';

export default interface ArtifactColumnPivot extends ArtifactColumn {
  area?:          'row' | 'column' | 'data';
  areaIndex?:     number;
  dateInterval?:  string;
  // dateInterval is used instead of groupInterval
  dateFormat?:    string;
}
