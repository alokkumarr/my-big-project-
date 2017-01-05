export class JoinModel {
  constructor(canvas, type, leftSide, rightSide) {
    this.canvas = canvas;
    this.type = type;
    this.leftSide = leftSide;
    this.rightSide = rightSide;
  }

  getIdentifier() {
    return `${this.leftSide.field.getIdentifier()}-${this.type}-${this.rightSide.field.getIdentifier()}`;
  }
}
