declare const require: any;

import {
  Component,
  OnInit,
  Input,
  ViewChild,
  Output,
  EventEmitter
} from '@angular/core';
import { MatHorizontalStepper } from '@angular/material/stepper';
import { MatSidenav } from '@angular/material/sidenav';
import * as clone from 'lodash/clone';
import { ObserveService } from '../../services/observe.service';

const template = require('./add-widget.component.html');
require('./add-widget.component.scss');

import {
  widgetTypes as wTypes,
  Widget,
  WidgetType,
  WIDGET_ACTIONS
} from './widget.model';

@Component({
  selector: 'add-widget',
  template
})
export class AddWidgetComponent implements OnInit {
  widgetTypes = wTypes;
  model: {
    type?: WidgetType;
    subCategory?: any;
    metric?: any;
    kpi?: any;
  } = {};
  widgets = Widget;

  @Input() container: MatSidenav;
  @Output() onWidgetAction = new EventEmitter();
  @ViewChild('widgetStepper') widgetStepper: MatHorizontalStepper;

  constructor(private observe: ObserveService) {}

  ngOnInit() {}

  onSelectWidgetType(data: WidgetType) {
    this.model.type = data;
    this.widgetStepper.next();
  }

  onSelectCategory(data) {
    this.model.subCategory = data;
    delete this.model.metric;
    delete this.model.kpi;
    this.widgetStepper.next();
  }

  onSelectMetric({ metric, kpi }) {
    delete this.model.subCategory;
    this.model.metric = clone(metric);
    this.model.kpi = clone(kpi);
    this.widgetStepper.next();
  }

  onAnalysisAction({ action, analysis }) {
    this.onWidgetAction.emit({
      widget: 'ANALYSIS',
      action: WIDGET_ACTIONS.ADD === action ? 'ADD' : 'REMOVE',
      data: analysis
    });
  }

  onKPIAction({ kpi }) {
    this.widgetStepper.previous();
    this.onWidgetAction.emit({
      widget: 'KPI',
      action: WIDGET_ACTIONS.ADD,
      data: kpi
    });
  }
}
