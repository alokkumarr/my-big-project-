import { ArtifactColumnChart } from '../types';

export class DesignerInitGroupAdapters {
  static readonly type = '[Designer] Init group adapters';
  constructor(
    public artifactColumns: ArtifactColumnChart[],
    public analysisType: string,
    public analysisSubType: string
  ) {}
}

export class DesignerAddColumnToGroupAdapter {
  static readonly type = '[Designer] Add column to group adapter';
  constructor(
    public artifactColumn: ArtifactColumnChart,
    public columnIndex: number,
    public adapterIndex: number
  ) {}
}
export class DesignerRemoveColumnFromGroupAdapter {
  static readonly type = '[Designer] Remove column from group adapter';
  constructor(public columnIndex: number, public adapterIndex: number) {}
}

export class DesignerMoveColumnInGroupAdapter {
  static readonly type = '[Designer] Move column in group adapter';
  constructor(
    public previousColumnIndex: number,
    public currentColumnIndex: number,
    public adapterIndex: number
  ) {}
}
