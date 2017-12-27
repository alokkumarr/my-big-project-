import * as template from './accordionMenuLink.component.html';
import style from './accordionMenuLink.component.scss';

export const AccordionMenuLink = {
  template,
  styles: [style],
  bindings: {
    metadata: '<'
  },
  require: {
    parent: '^accordionMenu'
  },
  controller: class AccordionMenuLinkCtrl {
    constructor($location) {
      this.isOpen = false;
      this.collapsFlag = 0;
      this._$location = $location;
    }

    $postLink() {
      this.isOpen = Boolean(this.metadata.active);
    }

    /* Check if the current item's url matches the browser url */
    checkActiveMenu(linkUrl) {
      this.url = '#!' + this._$location.path();
      if (this.url === linkUrl) {
        return true;
      }
      return false;
    }

    /* Check if the collapser contains an item that is currently open. */
    checkAndCollapse() {
      this.pathUrl = '#!' + this._$location.path();
      if (this.hasChildren()) {
        for (let i = 0; i < this.metadata.children.length - 1; i++) {
          if (this.pathUrl === this.metadata.children[i].url && this.collapsFlag === 0) {
            this.collapsFlag = 1;
            this.isOpen = true;
          }
        }
      }
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
