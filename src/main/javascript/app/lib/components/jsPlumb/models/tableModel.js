import find from 'lodash/find';

import {FieldModel} from './fieldModel';

export class TableModel {
  constructor(canvas, name) {
    this.canvas = canvas;
    this.name = name;
    this.x = 0;
    this.y = 0;
    this.fields = [];
  }

  setPosition(x, y) {
    this.x = x;
    this.y = y;
  }

  addField(name, display, alias) {
    const field = new FieldModel(this, name, display, alias);

    this.fields.push(field);

    return field;
  }

  findField(fieldName) {
    return find(this.fields, item => {
      return item.name === fieldName;
    });
  }
}
