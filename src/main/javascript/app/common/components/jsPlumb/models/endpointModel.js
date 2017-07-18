export class EndpointModel {
  constructor(canvas, field, side) {
    this.canvas = canvas;
    this.field = field;
    this.side = side;
  }

  getIdentifier() {
    return `${this.field.getIdentifier()}:${this.side}`;
  }

  getAnchor() {
    switch (this.side) {
      case 'left':
        return 'LeftMiddle';
      case 'right':
        return 'RightMiddle';
      default:
        throw new Error('Can\'t identify anchor!');
    }
  }
}
