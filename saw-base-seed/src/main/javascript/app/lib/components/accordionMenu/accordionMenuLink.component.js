import template from './accordionMenuLink.component.html';

export const AccordionMenuLink = {
  template,
  bindings: {
    metadata: '<'
  },
  require: {
    parent: '^accordionMenu'
  },
  controller: class AccordionMenuLinkCtrl {
    constructor() {
      this.isOpen = false;
    }

    $postLink() {
      this.isOpen = Boolean(this.metadata.active);
    }

    onClick($event) {
      const action = this.metadata.action;

      if (action instanceof Function) {
        return action($event);
      }

      if (this.hasChildren()) {
        this.isOpen = !this.isOpen;
      }
    }

    hasChildren() {
      const children = this.metadata.children;

      return Boolean((children || []).length);
    }
  }
};
