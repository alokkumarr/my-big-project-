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
const style = require('./analyze-card-view.component.scss');

@Component({
  selector: 'analyze-card-view',
  template,
  styles: [
    `:host {
      display: block;
      height: calc(100vh - 74px - 60px - 64px);
      overflow: auto;
      background-color: whitesmoke;
    }`,
    style
  ]
})

export class AnalyzeCardViewComponent {

  @Output() action: EventEmitter<AnalyzeViewActionEvent> = new EventEmitter();
  @Input() analyses: Analysis[];
  @Input() analysisType: string;
  @Input() highlightTerm: string;
  @Input() cronJobs: any;
  constructor() { }

}
