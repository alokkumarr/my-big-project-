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

  public chosenAnalyses1 = [];
  public chosenAnalyses2 = [];
  public chosenAnalyses3 = [];

  onDrop(data, collection) {
    const movedAnalysis = data.dragData.analysis;
    const present = collection.filter(an => an.id === movedAnalysis.id);
    if (present.length > 0) {
      return;
    }
    this[data.dragData.collection] = this[data.dragData.collection].filter(an => an.id !== data.dragData.analysis.id);
    collection.push(movedAnalysis);
  }
}