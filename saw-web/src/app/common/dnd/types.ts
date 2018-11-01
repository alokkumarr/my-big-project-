export interface IDragPayload {
  data: any;
  allowedZones?: string[];
  sortIndex?: number;
}

export interface IDroppableOptions {
  allowDropFn?: (dragData: any) => boolean;
  zone?: string;
}

export type SortableCallback = (payload: any, index: number, container: any) => boolean;

export interface ISortableDragEndEvent {
  isDropSuccessful: boolean;
  payload: any;
  container: any;
  index: number;
  removeFromCallback: SortableCallback;
}

export interface ISortableDropEvent {
  payload: any;
  container: any;
  index: number;
  addToCallback: SortableCallback;
}

export interface IDraggableDragEndData {
  isDropSuccessful: boolean;
}


export interface IDndMoveEvent<PayloadType, ContainerType> {
  name: 'to' | 'from';
  payload: PayloadType;
  container: ContainerType;
  index: number;
  moveCallback: SortableCallback;
}
