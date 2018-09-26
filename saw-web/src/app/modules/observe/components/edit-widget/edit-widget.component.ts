import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { Observable } from 'rxjs/Observable';
import * as map from 'lodash/map';
import * as clone from 'lodash/clone';
import * as filter from 'lodash/filter';
import * as find from 'lodash/find';
import * as flatMap from 'lodash/flatMap';

import { ObserveService } from '../../services/observe.service';
import { DATE_TYPES } from '../../../../common/consts';

const template = require('./edit-widget.component.html');
require('./edit-widget.component.scss');

@Component({
  selector: 'edit-widget',
  template
})
export class EditWidgetComponent implements OnInit {
  editItem: any;
  _model: any;
  kpiType: string;

  @Input() container: MatSidenav;
  @Output() onWidgetAction = new EventEmitter();

  constructor(private observe: ObserveService) {}

  ngOnInit() {}

  @Input()
  set model(data) {
    if (!data) { return; }
    this._model = data;
    if (data.kpi || data.bullet) {
      this.kpiType = data.kpi ? 'kpi' : 'bullet';
      this.prepareKPI(data);
    }
  }

  onKPIAction({ kpi }) {
    if (this.kpiType === 'bullet') {
      this._model.bullet = kpi;
    } else {
      this._model.kpi = kpi;
    }
    try {
      this.container.close();
    } catch (err) {
      throw new Error(
        'Container is either missing or does not support close method.'
      );
    }
  }

  prepareKPI(model) {
    const semId = model.kpi ? model.kpi.semanticId : model.bullet.semanticId;
    this.observe
      .getArtifacts({ semanticId: semId })
      .map(metric => {
        if (!metric) { return; }
        metric.kpiColumns = flatMap(metric.artifacts, table => {
          return filter(
            table.columns,
            col => col.kpiEligible && !DATE_TYPES.includes(col.type)
          );
        });

        metric.dateColumns = flatMap(metric.artifacts, table => {
          return filter(
            table.columns,
            col => col.kpiEligible && DATE_TYPES.includes(col.type)
          );
        });

        metric.kpiEligible =
          metric.kpiColumns.length > 0 && metric.dateColumns.length > 0;

        return metric;
      })
      .subscribe(metric => {
        if (!metric) { return; }
        if (this.kpiType === 'bullet') {
          this.editItem = {
            bullet: clone(model.bullet),
            metric
          };
        } else {
          this.editItem = {
            kpi: model.kpi ? clone(model.kpi) : clone(model.bullet),
            metric
          };
        }
      });
  }
}
