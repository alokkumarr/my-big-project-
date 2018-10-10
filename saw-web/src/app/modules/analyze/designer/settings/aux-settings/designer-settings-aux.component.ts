import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { DesignerChangeEvent } from '../../types';

@Component({
  selector: 'designer-settings-aux',
  templateUrl: 'designer-settings-aux.component.html'
})
export class DesignerSettingsAuxComponent implements OnInit {
  @Input() analysisType: string;
  @Input() analysisSubtype: string;
  @Input() auxSettings: any;
  @Input() chartTitle: string;

  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();

  constructor() {}

  ngOnInit() {}
}
