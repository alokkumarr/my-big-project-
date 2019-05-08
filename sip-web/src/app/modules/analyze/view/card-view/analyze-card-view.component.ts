import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Analysis, AnalyzeViewActionEvent } from '../types';

@Component({
  selector: 'analyze-card-view',
  templateUrl: './analyze-card-view.component.html',
  styleUrls: ['./analyze-card-view.component.scss']
})
export class AnalyzeCardViewComponent {
  @Output() action: EventEmitter<AnalyzeViewActionEvent> = new EventEmitter();
  @Input() analyses: Analysis[];
  @Input() analysisType: string;
  @Input() highlightTerm: string;
  @Input() cronJobs: any;
  constructor() {}
}
