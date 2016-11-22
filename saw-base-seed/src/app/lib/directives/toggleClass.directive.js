class ToggleClassDirective {
  constructor() {
    this.restrict = 'A';
  }

  link(scope, element, attrs) {
    element.bind('click', () => {
      element.toggleClass(attrs.toggleClass);
    });
  }
}

export default () => {
  return new ToggleClassDirective();
};
