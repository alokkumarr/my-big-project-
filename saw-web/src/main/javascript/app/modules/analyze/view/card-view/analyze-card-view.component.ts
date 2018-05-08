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

@Component({
  selector: 'analyze-card-view-u',
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
