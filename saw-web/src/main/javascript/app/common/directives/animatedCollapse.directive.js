class AnimatedCollapseDirective {
  constructor($mdUtil, $timeout, $parse) {
    this.$mdUtil = $mdUtil;
    this.$timeout = $timeout;
    this.$parse = $parse;
    this.restrict = 'A';
  }

  link(scope, element, attrs) {
    element.addClass('collapse-container');

    scope.$watch(this.$parse(attrs.collapse), newValue => {
      const isCollapsed = Boolean(newValue);

      this.$mdUtil.nextTick(() => {
        this.$timeout(() => {
          this.updateHeight(element, isCollapsed);
          this.toggleClass(element, isCollapsed);
        }, 0, false);
      }, false);
    });
  }

  toggleClass(element, isCollapsed) {
    return isCollapsed ?
      element.addClass('is-collapsed') :
      element.removeClass('is-collapsed');
  }

  updateHeight(element, isCollapsed) {
    const targetHeight = isCollapsed ? 0 : this.getTargetHeight(element);

    // Set the height of the container
    element.css({height: `${targetHeight}px`});
  }

  getTargetHeight(target) {
    const height = target.css('height');

    target.addClass('no-transition');

    target.css('height', '');

    const targetHeight = target.prop('clientHeight');

    target.css('height', height);

    target.removeClass('no-transition');

    return targetHeight;
  }
}

export default ($mdUtil, $timeout, $parse) => {
  'ngInject';

  return new AnimatedCollapseDirective($mdUtil, $timeout, $parse);
};
