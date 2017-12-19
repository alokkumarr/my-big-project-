import { Injectable } from '@angular/core';
import * as isFunction from 'lodash/isFunction';
import {
  IDragPayload,
  IDroppableOptions,
  IDndMoveEvent,
  ISortableDragEndEvent,
  ISortableDropEvent
} from './types';
import {
  dndClasses
} from './consts';
@Injectable()
export class DragnDropService {
  private _payload: IDragPayload = null;
  private _element: HTMLElement = null;
  private _moveEventAccumulator: {
    from: IDndMoveEvent<any, any>,
    to: IDndMoveEvent<any, any>
  } = {
    from: null,
    to: null
  };

  public startDrag(element?: HTMLElement) {
    if (element) {
      this._element = <HTMLElement>element.cloneNode(true);
      this._element.classList.add(dndClasses.draggedSortingNewPlace);
    }
  }

  public getPayload(): IDragPayload {
    return this._payload;
  }

  public setPayload(payload: IDragPayload) {
    this._payload = payload;
  }

  public getElement(): HTMLElement {
    return this._element;
  }

  public onDragEnd(event: ISortableDragEndEvent) {
    this._payload = null;
    if (event.isDropSuccessful) {
      this._onMove({
        name: 'from',
        payload: event.payload,
        index: event.index,
        container: event.container,
        moveCallback: event.removeFromCallback
      });
    }
  }

  public onDrop(event: ISortableDropEvent) {
    this._onMove({
      name: 'to',
      payload: event.payload,
      index: event.index,
      container: event.container,
      moveCallback: event.addToCallback
    });
  }

  public shouldAllowDrop(payload: IDragPayload, options: IDroppableOptions): boolean {
    if (options) {
      const {data, allowedZones: zones} = payload;
      const {zone, allowDropFn} = options;
      const isZoneOk = this._isZoneOk(zone, zones);
      const isAllowFnOk = this._isAllowFnOk(allowDropFn, data);

      return isZoneOk && isAllowFnOk;
    }
    return true;
  }

  private _isZoneOk(zone, zones): boolean {
    if (!zone && zones.length === 0) {
      return true;
    }
    if (!zone || zones.length === 0) {
      return false;
    }
    if (zones.includes(zone)) {
      return true;
    }
    return false;
  }

  private _isAllowFnOk(allowFn, data): boolean {
    if (isFunction(allowFn)) {
      return allowFn(data);
    }
    return true;
  }


  private _onMove(event: IDndMoveEvent<any, any>) {
    // because the onDragEnd event fires after the onDrop event
    // the moveFrom coms after the moveTo event
    // however we need the information from the moveFrom event first to take out the element
    // from the old group and then insert it into the new one
    switch (event.name) {
    case 'to':
      this._moveEventAccumulator.to = event;
      break;
    case 'from':
      this._moveEventAccumulator.from = event;
      break;
    }
    if (this._moveEventAccumulator.from && this._moveEventAccumulator.to) {
      const {
        container: fromContainer,
        index: fromIndex,
        payload: fromPayload,
        moveCallback: removeFromCallback
      } = this._moveEventAccumulator.from;
      const {
        container: toContainer,
        index: toIndex,
        payload: toPayload,
        moveCallback: addToCallback
      } = this._moveEventAccumulator.to;
      // remove from old group, if it was dragged from a group
      // do nothing if it was dragged from the unselected fields
      if (fromContainer && isFunction(removeFromCallback)) {
        removeFromCallback(fromPayload, fromIndex, fromContainer);
      }
      // add to new group
      if (toContainer && isFunction(addToCallback)) {
        addToCallback(toPayload, toIndex, toContainer);
      }
      // clear event Acumulator
      this._moveEventAccumulator = {
        to: null,
        from: null
      };
    }
  }
}
