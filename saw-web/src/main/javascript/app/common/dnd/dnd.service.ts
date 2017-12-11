import { Injectable } from '@angular/core';
import {
  IDragPayload,
  IDroppableOptions
} from './types';
import {
  dndClasses
} from './consts';
@Injectable()
export class DragnDropService {
  private _payload: IDragPayload = null;
  private _element: HTMLElement = null;

  public startDrag(payload: IDragPayload, element?: HTMLElement) {
    this._payload = payload;
    if (element) {
      this._element = <HTMLElement>element.cloneNode(true);
      this._element.classList.add(dndClasses.draggedSortingNewPlace);
    }
  }

  public getPayload(): IDragPayload {
    return this._payload;
  }

  public getElement(): HTMLElement {
    return this._element;
  }

  public onDragEnd() {
    this._payload = null;
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
    if (allowFn && typeof allowFn === 'function') {
      return allowFn(data);
    }
    return true;
  }
}
