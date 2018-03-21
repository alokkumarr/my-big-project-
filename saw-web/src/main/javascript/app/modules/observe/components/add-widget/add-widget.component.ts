declare const require: any;

import { Component, OnInit, Input, ViewChild, Output, EventEmitter } from '@angular/core';
import { MatHorizontalStepper } from '@angular/material/stepper';
import { MatSidenav } from '@angular/material/sidenav';

const template = require('./add-widget.component.html');
require('./add-widget.component.scss');
import { WIDGET_ANALYSIS_ACTIONS } from './widget-analysis/widget-analysis.component';

import {
  widgetTypes as wTypes,
  WidgetType
} from './widget.model';

@Component({
  selector: 'add-widget',
  template
})

export class AddWidgetComponent implements OnInit {
  widgetTypes = wTypes;
  model: {
    type?: WidgetType,
    subCategory?: any
  } = {};

  @Input() container: MatSidenav;
  @Output() onWidgetAction = new EventEmitter();
  @ViewChild('widgetStepper') widgetStepper: MatHorizontalStepper;

  constructor() { }

  ngOnInit() { }

  onSelectWidgetType(data: WidgetType) {
    this.model.type = data;
    this.widgetStepper.next();
  }

  onSelectCategory(data) {
    this.model.subCategory = data;
    this.widgetStepper.next();
  }

  onAnalysisAction({action, analysis}) {
    this.onWidgetAction.emit({
      widget: 'ANALYSIS',
      action: WIDGET_ANALYSIS_ACTIONS.ADD_ANALYSIS === action ? 'ADD' : 'REMOVE',
      data: analysis
    });
  }
}
