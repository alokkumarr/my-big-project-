import { Component, Input, OnInit } from '@angular/core';
import * as isUndefined from 'lodash/isUndefined';
import { SidenavComponent } from '../sidenav';

@Component({
  selector: 'accordion-menu-link',
  templateUrl: './accordionMenuLink.component.html',
  styleUrls: ['./accordionMenuLink.component.scss']
})
export class AccordionMenuLinkComponent implements OnInit {
  location: Location;
  @Input() public metadata: any;

  constructor(public leftSideNav: SidenavComponent) {}

  public url: string;
  public expanded: boolean;
  public active: boolean;
  public pathUrl: string;

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
    const url = location.hash.split('#')[1];

    if (/^\/observe/.test(url) || /^\/workbench/.test(url)) {
      /* If observe module / workbench, open all levels by default */
      this.expanded = true;
      return;
    }

    this.pathUrl = '#' + url;
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

  closeSideNav() {
    this.leftSideNav.toggleNav();
  }
}
