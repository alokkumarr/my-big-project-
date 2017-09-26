import { Component } from '@angular/core';

const template = require('./create-dashboard.component.html');
require('./create-dashboard.component.scss');

@Component({
  selector: 'create-dashboard',
  template
})
export class CreateDashboardComponent {
  public loadedAnalyses = [{
    id: 1,
    name: 'Sample Chart Analysis'
  }, {
    id: 2,
    name: 'Sample Pivot Analysis'
  }, {
    id: 3,
    name: 'Sample Report Analysis'
  }];

  public chosenAnalyses = [];

  onDrop(data) {
    this.loadedAnalyses = this.loadedAnalyses.filter(analysis => analysis.id !== data.dragData.id);
    this.chosenAnalyses.push(data.dragData);
  }
}