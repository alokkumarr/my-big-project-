import ArtifactColumn from './artifact-column.model';

export default interface ArtifactColumnPivot extends ArtifactColumn {
  checked:        null | boolean;
  area?:          'row' | 'column' | 'data';
  areaIndex?:     number;
  dateInterval?:  string;
  // dateInterval is used instead of groupInterval
  dateFormat?:    string;
}
