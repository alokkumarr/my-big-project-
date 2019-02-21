import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import * as map from 'lodash/map';
import * as split from 'lodash/split';
import { ANALYSIS_METHODS } from '../../consts';
@Component({
  selector: 'chart-type-chooser',
  templateUrl: 'chart-type-chooser.component.html',
  styleUrls: ['chart-type-chooser.component.scss']
})

export class ChartTypeChooserComponent implements OnInit {
  @Output() change = new EventEmitter();
  @Input() chartType: string;

  public chartTypes = map(ANALYSIS_METHODS[0].children[0].children, chartType => {
    const [, type] = split(chartType.type, ':');
    const ret = {
      ...chartType,
      type
    };
    return ret;
  });

  ngOnInit() {}
}
