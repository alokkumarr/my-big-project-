
import { Component } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { HeaderProgressService } from '../../../../common/services/header-progress.service';
import { MenuService } from '../../../../common/services/menu.service';

const template = require('./workbench-page.component.html');
require('./workbench-page.component.scss');

@Component({
  selector: 'workbench-page',
  styles: [],
  template: template
})
export class WorkbenchPageComponent {
  constructor(
    private headerProgress: HeaderProgressService,
    private router: Router,
    private route: ActivatedRoute,
    private menu: MenuService
  ) { }


  ngOnInit() {
    this.headerProgress.show();
    this.menu.getMenu('WORKBENCH')
      .then(data => {
        this.menu.updateMenu(data, 'WORKBENCH');
      });
    /**
    * Temporary fix for routing of SQL component until the issue of editor initialization is figured out.
    * So on refresh re-routin to listing page
    */
    const basePath = this.route.snapshot.url[0].path;
    if (basePath === 'workbench') {
      this.router.navigate(['workbench', 'dataobjects']);
    }
    this.headerProgress.hide();
  }
};
