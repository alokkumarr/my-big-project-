import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as filter from 'lodash/filter';
import * as forEach from 'lodash/forEach';

import { DashboardService } from '../../../services/dashboard.service';
import { AnalyzeService } from '../../../../analyze/services/analyze.service';
import { HeaderProgressService } from '../../../../../common/services';
import { ANALYSIS_METHODS } from '../../../../analyze/consts';
import { WIDGET_ACTIONS } from '../widget.model';

const style = require('./widget-analysis.component.scss');

const ALLOWED_ANALYSIS_TYPES = ['chart', 'esReport', 'pivot'];

@Component({
  selector: 'widget-analysis',
  templateUrl: './widget-analysis.component.html',
  styles: [style]
})
export class WidgetAnalysisComponent implements OnInit, OnDestroy {
  @Output() onAnalysisAction = new EventEmitter();
  analyses: Array<any> = [];
  showProgress = false;
  progressSub;
  searchTerm: string;
  widgetLog = {};
  dashboardWidgetSubscription;
  icons = {};

  constructor(
    private analyze: AnalyzeService,
    private dashboard: DashboardService,
    private _headerProgress: HeaderProgressService
  ) {
    this.progressSub = _headerProgress.subscribe(showProgress => {
      this.showProgress = showProgress;
    });
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
    this.progressSub.unsubscribe();
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
    this.analyze.getAnalysesFor(id.toString()).then(
      result => {
        this.analyses = filter(
          result,
          analysis => analysis && ALLOWED_ANALYSIS_TYPES.includes(analysis.type)
        );
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
