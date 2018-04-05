import {
  ArtifactColumnReport,
  Artifact,
  Sort
} from '../../../models';

export type ReportGridChangeEvent = {
  subject: 'format' | 'aliasName' | 'visibility' | 'visibleIndex';
}

export {
  ArtifactColumnReport,
  Artifact,
  Sort
};
