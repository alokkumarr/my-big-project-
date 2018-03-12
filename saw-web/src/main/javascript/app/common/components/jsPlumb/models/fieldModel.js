import * as find from 'lodash/find';

import {EndpointModel} from './endpointModel';

export class FieldModel {
  constructor(table, name) {
    this.table = table;
    this.name = name;
    this.meta = null;
    this.endpoints = [];
    this.displayName = '';
    this.alias = '';
    this.type = 'string';
    this._checked = false;
    this.aggregate = null;
    this.isHidden = false;
    this.isJoinEligible = false;
    this.isFilterEligible = false;
    this.visibleIndex = null;
  }

  setMeta(meta) {
    this.meta = meta;
  }

  getDisplayName() {
    return this.alias || this.displayName || this.name;
  }

  hide() {
    this.isHidden = true;
  }

  show() {
    this.isHidden = false;
  }

  get checked() {
    return this._checked;
  }

  set checked(val) {
    this._checked = val;
    if (this.table.canvas) {
      this.table.canvas.component._$eventEmitter.emit('changed');
    }
  }

  getIdentifier() {
    return `${this.table.name}:${this.getDisplayName()}`;
  }

  getEndpoint(side) {
    return find(this.endpoints, endpoint => {
      return endpoint.side === side;
    });
  }

  addEndpoint(side) {
    let endpoint = this.getEndpoint(side);

    if (!endpoint) {
      endpoint = new EndpointModel(this.table.canvas, this, side);

      this.endpoints.push(endpoint);
    }

    return endpoint;
  }

  removeEndpoint(endpoint) {
    const idx = this.endpoints.indexOf(endpoint);

    if (idx !== -1) {
      this.endpoints.splice(idx, 1);
    }
  }
}
