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
        this.$timeout(() => this.updateHeight(element, isCollapsed), 0, false);
      }, false);
    });
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

/** @ngInject */
export default ($mdUtil, $timeout, $parse) => {
  return new AnimatedCollapseDirective($mdUtil, $timeout, $parse);
};
