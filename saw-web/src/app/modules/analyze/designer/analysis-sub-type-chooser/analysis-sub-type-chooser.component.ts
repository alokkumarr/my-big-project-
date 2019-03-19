import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import * as map from 'lodash/map';
import * as split from 'lodash/split';
import { ANALYSIS_METHODS } from '../../consts';
const methodsMap = {
  chart: ANALYSIS_METHODS[0].children[0].children,
  map: ANALYSIS_METHODS[0].children[3].children
};

@Component({
  selector: 'analysis-sub-type-chooser',
  templateUrl: 'analysis-sub-type-chooser.component.html',
  styleUrls: ['analysis-sub-type-chooser.component.scss']
})
export class AnalysisSubTypeChooserComponent implements OnInit {
  @Output() change = new EventEmitter();
  @Input() category: 'map' | 'chart';
  @Input() subType: string;

  public subTypes;

  ngOnInit() {
    const methods = methodsMap[this.category] || [];
    this.subTypes = map(methods, chartType => {
      const [, type] = split(chartType.type, ':');
      const ret = {
        ...chartType,
        type
      };
      return ret;
    });
  }
}
