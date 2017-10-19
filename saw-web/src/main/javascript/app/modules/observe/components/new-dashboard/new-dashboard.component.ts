import { Component } from '@angular/core';
import { MdDialog } from '@angular/material';
import * as find from 'lodash/find';

import { CreateDashboardComponent } from '../create-dashboard/create-dashboard.component';
const template = require('./new-dashboard.component.html');
require('./new-dashboard.component.scss');

interface Layout {
  name: string;
  id: number;
  icon: string;
  tiles: Array<Array<number>>; // tiles are array of [width, height] tuples
}

@Component({
  selector: 'new-dashboard',
  template
})
export class NewDashboardComponent {
  availableLayouts: Array<Layout> = [
    {name: '2x2', id: 1, icon: 'icon-launchpad', tiles: [[50, 50], [50, 50], [50, 50], [50, 50]]},
    {name: '1x1', id: 2, icon: 'icon-uniE910', tiles: [[100, 100]]},
    {name: '1x3', id: 3, icon: 'icon-uniE910', tiles: [[33, 100], [33, 100], [33, 100]]},
    {name: '4x4', id: 4, icon: 'icon-uniE910', tiles: [[25, 25], [25, 25], [25, 25], [25, 25],[25, 25], [25, 25], [25, 25], [25, 25]]}
  ];
  selectedLayout: number;

  constructor(public dialog: MdDialog) {
    window['mydialog'] = dialog;
    window['dashcomponent'] = CreateDashboardComponent;
  }

  createNewDashboard() {
    this.dialog.open(CreateDashboardComponent, {
      panelClass: 'full-screen-dialog',
      data: find(this.availableLayouts, layout => layout.id === this.selectedLayout)
    });
  }
}
