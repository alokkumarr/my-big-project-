import find from 'lodash/find';

import {EndpointModel} from './endpointModel';

export class FieldModel {
  constructor(table, name, display, alias) {
    this.table = table;
    this.name = name;
    this.display = display;
    this.alias = alias;
    this.type = 'string';
    this.endpoints = [];
    this.selected = false;
  }

  getName() {
    return this.alias || this.display || this.name;
  }

  setType(type) {
    this.type = type;
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
