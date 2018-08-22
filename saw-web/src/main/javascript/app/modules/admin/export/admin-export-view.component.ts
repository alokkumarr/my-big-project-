import { Component, Input, ViewChild, ElementRef } from '@angular/core';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Observable } from 'rxjs/Observable';
import { FormControl } from '@angular/forms';
import { mergeMap, startWith, map } from 'rxjs/operators';
import * as lowerCase from 'lodash/lowerCase';
import * as includes from 'lodash/includes';
import * as isEmpty from 'lodash/isEmpty';
import * as lodashMap from 'lodash/map';
import * as difference from 'lodash/difference';
import * as fpFilter from 'lodash/fp/filter';
import * as fpPipe from 'lodash/fp/pipe';
import { ExportService } from './export.service';
import { SidenavMenuService } from '../../../common/components/sidenav';
import { AdminMenuData } from '../consts';

const template = require('./admin-export-view.component.html');
require('./admin-export-view.component.scss');

@Component({
  selector: 'admin-export-view',
  template
})
export class AdminExportViewComponent {
  @ViewChild('metricInput')
  metricInput: ElementRef;
  @Input()
  columns: any[];

  data: any[];
  metrics: any[] = [];
  selectedMetrics: any[] = [];
  metricCtrl = new FormControl();
  unSelectedMetrics: Observable<any[]>;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  analyses: any[] = [];
  metrics$: Promise<any>;

  isEmpty = isEmpty;

  constructor(
    private _exportService: ExportService,
    private _sidenav: SidenavMenuService
  ) {
    this.metrics$ = this._exportService.getMetricList();
    this.unSelectedMetrics = this.metricCtrl.valueChanges.pipe(
      startWith(''),
      mergeMap((searchTerm: string) => this._asyncFilter(searchTerm))
    );
  }

  ngOnInit() {
    this._sidenav.updateMenu(AdminMenuData, 'ADMIN');
  }

  loadAnalyses() {
    const metricIds = lodashMap(this.selectedMetrics, ({id}) => id);
    if (isEmpty(this.selectedMetrics)) {
      this.analyses = [];
      return;
    }
    console.log('metricIds', metricIds);
    this._exportService.getAnalysisByMetricIds(metricIds).then(analyses => {
      console.log('analyses', analyses);
      this.analyses = analyses;
    });
  }

  onRemove(index) {
    if (index >= 0) {
      this.selectedMetrics.splice(index, 1);
      this.loadAnalyses();
    }
  }

  onSelect(metric) {
    this.selectedMetrics.push(metric);
    this.metricInput.nativeElement.value = '';
    this.metricCtrl.setValue(null);
    this.loadAnalyses();
  }

  private _asyncFilter(value) {
    const filterValue = lowerCase(value);
    return Observable.fromPromise(this.metrics$).pipe(
      map(metrics => {
        return fpPipe(
          fpFilter(metric => {
            if (!filterValue) {
              return true;
            }
            const metricName = lowerCase(metric.metricName);
            return includes(metricName, filterValue);
          })
        )(difference(metrics, this.selectedMetrics));
      })
    );
  }
}
