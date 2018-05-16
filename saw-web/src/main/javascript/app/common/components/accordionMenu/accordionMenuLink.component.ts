import { Component, Input, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/platform-browser';
import * as isUndefined from 'lodash/isUndefined';

const template = require('./accordionMenuLink.component.html');
require('./accordionMenuLink.component.scss');

@Component({
  selector: 'accordion-menu-link',
  template
})

export class AccordionMenuLinkComponent {

  location: Location;
  @Input() public metadata: any;

  constructor(@Inject(DOCUMENT) private document: any) {}
  
  ngOnInit() {
    this.expanded = false;
    this.expandLoadedPanel();
    this.active = false;
  }

  checkActiveMenu(linkUrl) {
    this.url = location.hash;
    if (this.url === linkUrl) {
      return true;
    }
    return false;
  }

  expandLoadedPanel() {
    const url = location.hash.split('#!')[1];

    if (/^\/observe/.test(url)) {
      /* If observe module, open all levels by default */
      this.expanded = true;
      return;
    }

    this.pathUrl = '#!' + url;
    if (this.checkPanel()) {
      for (let i = 0; i < this.metadata.children.length - 1; i++) {
        if (this.pathUrl === this.metadata.children[i].url) {
          this.expanded = true;
        }
      }
    }
  }

  checkPanel() {
    const children = this.metadata.children;
    return Boolean((children || []).length);
  }

  hasChildren() {
    const children = this.metadata.children;
    if (isUndefined(children)) {
      return true;
    } else {
      return false;
    }
  }
}

