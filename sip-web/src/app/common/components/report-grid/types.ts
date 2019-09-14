import { ArtifactColumnReport, Artifact, Sort } from '../../../models';

export interface ReportGridChangeEvent {
  subject: 'format' | 'alias' | 'removeColumn' | 'aggregate' | 'reorder';
  column?: ArtifactColumnReport;
}

export { ArtifactColumnReport, Artifact, Sort };
