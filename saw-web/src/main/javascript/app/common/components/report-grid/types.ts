import {
  ArtifactColumnReport,
  Artifact,
  Sort
} from '../../../models';

export type ReportGridChangeEvent = {
  subject: 'format' | 'aliasName' | 'removeColumn' | 'visibleIndex';
  column?: ArtifactColumnReport;
}

export {
  ArtifactColumnReport,
  Artifact,
  Sort
};
