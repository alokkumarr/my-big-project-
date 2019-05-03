import {
  Artifact,
  JoinCriterion,
  ArtifactColumnReport,
  Join
} from '../../../models';
export {
  Artifact,
  JoinCriterion,
  ArtifactColumnReport,
  Join
};
export interface JoinChangeEvent {
  join: Join;
  action: 'save' | 'delete';
  index?: number;
}
export type EndpointSide = 'left' | 'right';
export interface EndpointPayload {
  column: ArtifactColumnReport;
  artifactName: string;
  side: EndpointSide;
}
export interface ConnectionPayload {
  join: Join;
}

export interface JsPlumbCanvasChangeEvent {
  subject: 'joins' | 'artifactPosition' | 'column' | 'aggregate';
  column?: ArtifactColumnReport;
}
