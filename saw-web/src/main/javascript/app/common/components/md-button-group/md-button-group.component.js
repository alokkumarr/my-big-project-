export const mdButtonGroupComponent = {
  require: {
    originalCtrl: 'mdButtonGroup'
  },
  controller: class MdButtonGroupController {
    constructor($mdUtil, $mdConstant, $mdTheming, $timeout, $element) {
      'ngInject';
      this.$mdUtil = $mdUtil;
      this.$mdConstant = $mdConstant;
      this.$mdTheming = $mdTheming;
      this.$timeout = $timeout;
      this.$element = $element;

      this.focusedClass = 'md-focused';
      this.mouseActive = false;
      this.activeElement = null;
      this.$mdTheming(this.$element);
      this.$element.addClass('_md');
      // private md component indicator for styling
      this.$element
        .attr({
          role: 'group',
          tabIndex: this.$element.attr('tabindex') || '0'
        });
    }

    $postLink() {
      this.originalCtrl.selectNext();
      this.$element
        .on('keydown', this.onKeydown.bind(this))
        .on('mousedown', this.onMousedown.bind(this))
        .on('focus', this.onFocus.bind(this))
        .on('click', this.onClick.bind(this))
        .on('blur', this.onBlur.bind(this));
    }

    onBlur() {
      this.originalCtrl.$element.removeClass(this.focusedClass);
    }

    onClick(ev) {
      const match = 'button.md-button';
      const target = ev.target;
      let el;

      if (target) {
        if (this.matches(target, match)) {
          el = target;
        } else if (this.matches(target.parentElement, match)) {
          el = target.parentElement;
        }
        this.originalCtrl.select(el);
      }
    }

    matches(target, match) {
      let doesMatch;

      if (angular.isFunction(target.matches)) {
        doesMatch = target.matches(match);
      } else {
        // using angulars $document service does not work.
        doesMatch = this.matchesSelector(document, match, target);
      }
      return doesMatch;
    }

    // IE doesn't support Element.matches
    matchesSelector(doc, selector, element) {
      const all = doc.querySelectorAll(selector);

      for (let i = 0; i < all.length; i++) {
        if (all[i] === element) {
          return true;
        }
      }
      return false;
    }

    onFocus() {
      if (this.mouseActive === false) {
        this.originalCtrl.$element.addClass(this.focusedClass);
      }
    }

    onMousedown() {
      this.mouseActive = true;
      this.$timeout(() => {
        this.mouseActive = false;
      }, 100);
    }

    onKeydown(ev) {
      const mdConst = this.$mdConstant;
      const keyCode = ev.which || ev.keyCode;

      switch (keyCode) {
      case mdConst.KEY_CODE.LEFT_ARROW:
      case mdConst.KEY_CODE.UP_ARROW:
        ev.preventDefault();
        this.originalCtrl.selectPrevious();
        this.setFocus(this.$element);
        break;

      case mdConst.KEY_CODE.RIGHT_ARROW:
      case mdConst.KEY_CODE.DOWN_ARROW:
        ev.preventDefault();
        this.originalCtrl.selectNext();
        this.setFocus(this.$element);
        break;
      default:
        break;
      }
    }

    setFocus(element) {
      if (!element.hasClass(this.focusedClass)) {
        element.addClass(this.focusedClass);
      }
    }

    selectNext() {
      const el = this.changeSelectedButton(this.$element, 1);
      this.select(el);
    }

    selectPrevious() {
      const el = this.changeSelectedButton(this.$element, -1);
      this.select(el);
    }

    /**
     * Change the radio group's selected button by a given increment.
     * If no button is selected, select the first button.
     */
    changeSelectedButton(parent, increment) {
      // Coerce all child radio buttons into an array, then wrap then in an iterator
      const buttons = this.$mdUtil.iterator(parent[0].querySelectorAll('.md-button'), true);
      let sel;

      if (buttons.count()) {
        const validate = button => !angular.element(button).attr('disabled');
        const selected = parent[0].querySelector('.md-button.md-active');
        const target = buttons[increment < 0 ? 'previous' : 'next'](selected, validate) || buttons.first();

        // Activate radioButton's click listener (triggerHandler won't create a real click event)
        sel = angular.element(target);
        this.$timeout(() => sel.triggerHandler('click'));

        return sel;
      }
    }

    select(el) {
      const activeCls = 'md-active';

      if (el) {
        el = angular.element(el);
        el.addClass(activeCls);

        if (this.activeElement && this.activeElement[0] !== el[0]) {
          this.activeElement.removeClass(activeCls);
        }

        this.activeElement = el;
      }
    }
  }
};
