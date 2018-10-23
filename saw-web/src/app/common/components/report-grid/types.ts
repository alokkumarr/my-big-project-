import {
  ArtifactColumnReport,
  Artifact,
  Sort
} from '../../../models';

export interface ReportGridChangeEvent {
  subject: 'format' | 'aliasName' | 'removeColumn' | 'visibleIndex' | 'aggregate';
  column?: ArtifactColumnReport;
}

export {
  ArtifactColumnReport,
  Artifact,
  Sort
};
