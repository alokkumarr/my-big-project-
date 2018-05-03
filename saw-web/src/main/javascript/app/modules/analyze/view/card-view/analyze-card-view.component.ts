import {
  Component,
  Input,
  Output
} from '@angular/core';
import { Analysis } from '../../types';

const template = require('./analyze-card-view.component.html');

@Component({
  selector: 'analyze-card-view-u',
  template
})

export class AnalyzeCardViewComponent {

  @Input() analyses: Analysis[];
  @Input() analysisType: string;
  @Input() searchTerm: string;
  @Input() cronJobs: any;
  constructor() { }

}
