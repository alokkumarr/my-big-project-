declare function require(string): string;

import { Inject, OnInit } from '@angular/core';
import { MdIconRegistry } from '@angular/material';
import { UIRouter } from '@uirouter/angular';

import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import * as map from 'lodash/map';

import { ObserveService } from '../../services/observe.service';
import { MenuService } from '../../../../common/services/menu.service';
import { HeaderProgressService } from '../../../../common/services/header-progress.service';
import { AnalyzeService } from '../../../analyze/services/analyze.service';

import { Dashboard } from '../../models/dashboard.interface';


const template = require('./observe-page.component.html');
require('./observe-page.component.scss');
// import * as template from './observe-page.component.html';
// import * as style from './observe-page.component.scss';
// import {OBSERVE_FILTER_SIDENAV_ID} from '../filter-sidenav/filter-sidenav.component';

import { Component } from '@angular/core';

@Component({
  selector: 'observe-page',
  styles: [],
  template: template
})
export class ObservePageComponent implements OnInit {

  constructor(
    private iconRegistry: MdIconRegistry,
    private analyze: AnalyzeService,
    private menu: MenuService,
    private observe: ObserveService,
    private headerProgress: HeaderProgressService,
    private router: UIRouter,
    @Inject('$componentHandler') private $componentHandler
  ) {
    this.iconRegistry.setDefaultFontSetClass('icomoon');
  }


  ngOnInit() {
    this.headerProgress.show();

    /* Needed to get the analyze service working correctly */
    this.menu.getMenu('ANALYZE')
      .then(data => {
        this.analyze.updateMenu(data);
      });

    this.menu.getMenu('OBSERVE')
      .then(data => {

        let count = this.getSubcategoryCount(data);
        forEach(data, category => {
          forEach(category.children || [], subCategory => {

            this.observe.getDashboardsForCategory(subCategory.id).subscribe((dashboards: Array<Dashboard>) => {
              dashboards = dashboards || [];
              subCategory.children = subCategory.children || [];

              subCategory.children = subCategory.children.concat(map(dashboards, dashboard => ({
                id: dashboard.entityId,
                name: dashboard.name,
                url: `#!/observe/${subCategory.id}?dashboard=${dashboard.entityId}`,
                data: dashboard
              })));

              if(--count <= 0) {
                this.updateSidebar(data);
                this.redirectToFirstDash(data);
              }
            }, error => {
              if(--count <= 0) {
                this.updateSidebar(data);
                this.redirectToFirstDash(data);
              }
            });
          });
        });

      });
  }

  updateSidebar(data) {
    // const data = [
    //   {
    //     id: 1,
    //     name: 'My Dashboards',
    //     children: [
    //       { id: 2, name: 'Testing', url: `#!/observe/d8939bf3-d8f4-4ee7-89c4-f2a4fd4abca9::PortalDataSet::1513945502617`}
    //     ]
    //   }
    // ];

    this.menu.updateMenu(data, 'OBSERVE');
    this.headerProgress.hide();
  }

  /* Try to redirect to first dashboard or first empty subcategory */
  redirectToFirstDash(menu) {
    /* Only redirect if on root observe state */
    if (this.router.stateService.current.name !== 'observe') {
      return;
    }

    const categoryWithDashboard = find(menu, cat => {
      const subCategory = find(cat.children, subCat => {
        return subCat.children.length > 0;
      });

      return Boolean(subCategory);
    });

    const categoryWithSubCategory = find(menu, cat => cat.children.length > 0);

    if (categoryWithDashboard) {
      /* If a dashboard has been found in some category/subcategory, redirect to that */
      const subCategory = find(categoryWithDashboard.children, subCat => {
        return subCat.children.length > 0;
      });

      this.router.stateService.go('observe.dashboard', {
        subCategory: subCategory.id,
        dashboard: subCategory.children[0].id
      });

    } else if (categoryWithSubCategory) {
      /* Otherwise, redirect to the first empty subcategory available. */
      this.router.stateService.go('observe.dashboard', {
        subCategory: categoryWithSubCategory.children[0].id
      });
    }
  }

  getSubcategoryCount(data) {
    let count = 0;
    forEach(data, category => {
      count += category.children.length;
    });

    return count;
  }
};
