import {
  Component,
  OnInit,
  Input,
  Output
} from '@angular/core';
import { Analysis } from '../../../types';
import { CronJobSchedularComponent } from '../../cron-job-schedular';

const template = require('./analyze-card.component.html');

@Component({
  selector: 'analyze-card-u',
  template
})

export class AnalyzeCardComponent implements OnInit {

  @Input() analysis: Analysis;
  @Input() highlightTerm: string;
  @Input() cronJobs: any;
  constructor() { }

  ngOnInit() { }
}
