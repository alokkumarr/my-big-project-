import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  Analysis,
  AnalyzeViewActionEvent
} from '../types';

const template = require('./analyze-card-view.component.html');
require('./analyze-card-view.component.scss');

@Component({
  selector: 'analyze-card-view',
  template
})

export class AnalyzeCardViewComponent {

  @Output() action: EventEmitter<AnalyzeViewActionEvent> = new EventEmitter();
  @Input() analyses: Analysis[];
  @Input() analysisType: string;
  @Input() highlightTerm: string;
  @Input() cronJobs: any;
  constructor() { }

}
