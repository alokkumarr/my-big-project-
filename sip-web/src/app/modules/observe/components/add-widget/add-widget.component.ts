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

import {
  widgetTypes as wTypes,
  Widget,
  WidgetType,
  WIDGET_ACTIONS
} from './widget.model';

@Component({
  selector: 'add-widget',
  templateUrl: './add-widget.component.html',
  styleUrls: ['./add-widget.component.scss']
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
  selectedStepIndex: number; // Just used as a condition to reset the form in widget-kpi

  @Input() container: MatSidenav;
  @Output() onWidgetAction = new EventEmitter();
  @ViewChild('widgetStepper') widgetStepper: MatHorizontalStepper;

  constructor() {}

  ngOnInit() {}

  onSelectWidgetType(data: WidgetType) {
    this.model.type = data;
    this.widgetStepper.next();
    this.selectedStepIndex = this.widgetStepper.selectedIndex;
  }

  onSelectCategory(data) {
    this.model.subCategory = data;
    delete this.model.metric;
    delete this.model.kpi;
    this.widgetStepper.next();
    this.selectedStepIndex = this.widgetStepper.selectedIndex;
  }

  onSelectMetric({ metric, kpi }) {
    delete this.model.subCategory;
    this.model.metric = clone(metric);
    this.model.kpi = clone(kpi);
    this.widgetStepper.next();
    this.selectedStepIndex = this.widgetStepper.selectedIndex;
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
      widget: this.model.type.id === 3 ? 'BULLET' : 'KPI',
      action: WIDGET_ACTIONS.ADD,
      data: kpi
    });
    setTimeout(() => {
      this.selectedStepIndex = this.widgetStepper.selectedIndex;
    });
  }
}
