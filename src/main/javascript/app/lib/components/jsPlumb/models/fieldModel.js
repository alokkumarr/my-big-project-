import find from 'lodash/find';

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
    this.isHidden = false;
    this.isJoinEligible = false;
    this.isFilterEligible = false;
  }

  getIdentifier() {
    return `${this.table.name}:${this.name}`;
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
    this.table.canvas.component._$eventHandler.emit('changed');
  }

  getIdentifier() {
    return `${this.table.name}:${this.name}`;
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
