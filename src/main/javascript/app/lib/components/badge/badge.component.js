/**
 * BadgeComponent
 *
 * @example usage in an html template: <badge label="{{ctrl.badgeCount}}"></badge>
 * @export
 * @returns badgeDirective
 */

export const BadgeComponent = {
  bindings: {
    label: '@'
  },
  controller: class BadgeController {
    constructor($attrs, $element) {
      'ngInject';

      this.$attrs = $attrs;
      this.$element = $element;
    }

    $postLink() {
      const badgeCountAttr = 'data-badge-count';

      this.$attrs.$observe('label', newVal => {
        if (newVal > 0) {
          this.$element.attr(badgeCountAttr, newVal);
        } else {
          this.$element.removeAttr(badgeCountAttr);
        }
      });
    }
  }
};
