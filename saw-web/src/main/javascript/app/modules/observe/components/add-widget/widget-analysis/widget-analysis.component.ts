import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as filter from 'lodash/filter';
import * as find from 'lodash/find';
import * as forEach from 'lodash/forEach';
import { Subscription } from 'rxjs/subscription';

import { DashboardService } from '../../../services/dashboard.service';

const template = require('./widget-analysis.component.html');
require('./widget-analysis.component.scss');

import { AnalyzeService } from '../../../../analyze/services/analyze.service';
import { ANALYSIS_METHODS } from '../../../../analyze/consts';
import { WIDGET_ACTIONS } from '../widget.model';

const ALLOWED_ANALYSIS_TYPES = ['chart', 'esReport' /*, 'pivot' */];

@Component({
  selector: 'widget-analysis',
  template
})
export class WidgetAnalysisComponent implements OnInit, OnDestroy {
  @Output() onAnalysisAction = new EventEmitter();
  analyses: Array<any> = [];
  showProgress = false;
  searchTerm: string;
  widgetLog = {};
  dashboardWidgetSubscription: Subscription;
  icons = {};

  constructor(
    private analyze: AnalyzeService,
    private dashboard: DashboardService
  ) {
    this.loadIcons();
  }

  ngOnInit() {
    this.dashboardWidgetSubscription = this.dashboard.dashboardWidgets.subscribe(
      data => {
        this.widgetLog = { ...data };
      }
    );
  }

  ngOnDestroy() {
    this.dashboardWidgetSubscription.unsubscribe();
  }

  loadIcons() {
    forEach(ANALYSIS_METHODS, method => {
      forEach(method.children, analysisType => {
        if (analysisType.supportedTypes && analysisType.supportedTypes.length) {
          forEach(analysisType.supportedTypes, supType => {
            this.icons[supType.split(':')[1]] = analysisType.icon.font;
          });
        } else {
          this.icons[analysisType.type.split(':')[1]] = analysisType.icon.font;
        }
      });
    });
  }

  @Input()
  set category(id: number | string) {
    this.searchTerm = '';
    this.showProgress = true;
    this.analyze.getAnalysesFor(id.toString()).then(
      result => {
        this.analyses = filter(
          result,
          analysis => analysis && ALLOWED_ANALYSIS_TYPES.includes(analysis.type)
        );
        this.showProgress = false;
      },
      () => {
        this.showProgress = false;
      }
    );
  }

  sendAnalysisAction(action, analysis) {
    this.onAnalysisAction.emit({
      action,
      analysis
    });
  }

  addAnalysis(analysis) {
    this.sendAnalysisAction(WIDGET_ACTIONS.ADD, analysis);
  }

  removeAnalysis(analysis) {
    this.sendAnalysisAction(WIDGET_ACTIONS.REMOVE, analysis);
  }
}
