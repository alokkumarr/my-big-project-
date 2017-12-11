export interface IDragPayload {
  data: any;
  allowedZones?: string[];
  sortIndex?: number;
}

export interface IDroppableOptions {
  allowDropFn?: (dragData: any) => boolean;
  zone?: string;
}

export interface ISortableDragEndData {
  isDropSuccessful: boolean;
}

export interface IDraggableDragEndData {
  isDropSuccessful: boolean;
}
