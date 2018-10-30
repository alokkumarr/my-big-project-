import { Component, Input, OnInit, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { DOCUMENT } from '@angular/platform-browser';
import * as isUndefined from 'lodash/isUndefined';
import { SidenavComponent } from '../sidenav';
import {
  ConfigService,
  PREFERENCES
} from '../../services/configuration.service';

@Component({
  selector: 'accordion-menu-link',
  templateUrl: './accordionMenuLink.component.html',
  styleUrls: ['./accordionMenuLink.component.scss']
})
export class AccordionMenuLinkComponent implements OnInit {
  location: Location;
  @Input() public metadata: any;

  constructor(
    @Inject(DOCUMENT) private document: any,
    private configService: ConfigService,
    public leftSideNav: SidenavComponent,
    private router: Router
  ) {}

  public url: string;
  public expanded: boolean;
  public active: boolean;

  ngOnInit() {
    this.expanded = false;
    this.expandLoadedPanel();
    this.active = false;
  }

  /**
   * Check whether this link is to a default dashboard
   *
   * @param {id} Id of the dashboard
   * @returns {boolean}
   */
  checkDefault({ id }): boolean {
    const defaultDashboard = this.configService.getPreference(
      PREFERENCES.DEFAULT_DASHBOARD
    );
    return id === defaultDashboard;
  }

  checkActiveMenu(linkUrl, queryParams) {
    this.url = location.hash;
    const urlTree = this.router.createUrlTree(linkUrl, { queryParams });
    return this.url === `#${this.router.serializeUrl(urlTree)}`;
  }

  expandLoadedPanel() {
    const path = location.hash.split('#')[1];

    if (/^\/observe/.test(path) || /^\/workbench/.test(path)) {
      /* If observe module / workbench, open all levels by default */
      this.expanded = true;
      return;
    }

    if (this.checkPanel()) {
      for (let i = 0; i < this.metadata.children.length; i++) {
        const { url, queryParams } = this.metadata.children[i];
        const urlTree = this.router.createUrlTree(url, { queryParams });
        if (path === this.router.serializeUrl(urlTree)) {
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
