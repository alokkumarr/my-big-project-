import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
const template = require('./widget-type.component.html');
const style = require('./widget-type.component.scss');

import { WidgetType } from '../widget.model';

@Component({
  selector: 'widget-type',
  template,
  styles: [style]
})

export class WidgetTypeComponent implements OnInit {
  @Input() widgetTypes: Array<WidgetType>;
  @Output() onSelect = new EventEmitter();
  constructor() { }

  ngOnInit() { }

  onChooseType(data: WidgetType) {
    this.onSelect.emit(data);
  }
}
