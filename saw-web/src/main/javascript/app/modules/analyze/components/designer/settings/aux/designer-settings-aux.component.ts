import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';

const template = require('./designer-settings-aux.component.html');

@Component({
  selector: 'designer-settings-aux',
  template
})
export class DesignerSettingsAuxComponent implements OnInit {
  @Input() analysisType: string;
  @Input() analysisSubtype: string;
  @Input() auxSettings: any;

  @Output('settingsChange') change = new EventEmitter();

  constructor() {}

  ngOnInit() {}
}
