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
export type JoinChangeEvent = {
  join: Join;
  action: 'save' | 'delete';
  index?: number;
}
export type EndpointSide = 'left' | 'right';
export type EndpointPayload = {
  column: ArtifactColumnReport,
  artifactName: string,
  side: EndpointSide
}
export type ConnectionPayload = {
  join: Join
}

export type JsPlumbCanvasChangeEvent = {
  subject: 'joins' | 'artifactPosition' | 'column' | 'aggregate';
}
