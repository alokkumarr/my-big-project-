declare const require: any;

import { Component, OnInit, ViewChild } from '@angular/core';
import { MatHorizontalStepper } from '@angular/material/stepper';

const template = require('./add-widget.component.html');
require('./add-widget.component.scss');

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
    type?: WidgetType
  } = {};

  @ViewChild('widgetStepper') widgetStepper: MatHorizontalStepper;

  constructor() { }

  ngOnInit() { }

  onSelectWidgetType(data: WidgetType) {
    this.model.type = data;
    this.widgetStepper.next();
  }
}
