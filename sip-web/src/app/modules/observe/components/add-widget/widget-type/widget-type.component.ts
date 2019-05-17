import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';

import { WidgetType } from '../widget.model';

@Component({
  selector: 'widget-type',
  templateUrl: './widget-type.component.html',
  styleUrls: ['./widget-type.component.scss']
})
export class WidgetTypeComponent implements OnInit {
  @Input() widgetTypes: Array<WidgetType>;
  @Output() onSelect = new EventEmitter();
  constructor() {}

  ngOnInit() {}

  onChooseType(data: WidgetType) {
    this.onSelect.emit(data);
  }
}
