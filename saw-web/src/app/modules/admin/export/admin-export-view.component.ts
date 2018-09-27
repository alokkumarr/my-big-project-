import { Component, Input, ViewChild, ElementRef, OnInit } from '@angular/core';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { Observable } from 'rxjs/Observable';
import { from } from 'rxjs';
import { FormControl } from '@angular/forms';
import { mergeMap, startWith, map } from 'rxjs/operators';
import { JwtService } from '../../../common/services';
import * as JSZip from 'jszip';
import * as FileSaver from 'file-saver';
import * as moment from 'moment';
import * as lowerCase from 'lodash/lowerCase';
import * as includes from 'lodash/includes';
import * as isEmpty from 'lodash/isEmpty';
import * as lodashMap from 'lodash/map';
import * as forEach from 'lodash/forEach';
import * as reduce from 'lodash/reduce';
import * as get from 'lodash/get';
import * as difference from 'lodash/difference';
import * as isUndefined from 'lodash/isUndefined';
import * as fpFilter from 'lodash/fp/filter';
import * as fpGroupBy from 'lodash/fp/groupBy';
import * as lodashFilter from 'lodash/filter';
import * as debounce from 'lodash/debounce';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';
import { ExportService } from './export.service';
import { CategoryService } from '../category/category.service';
import { SidenavMenuService } from '../../../common/components/sidenav';
import { AdminMenuData } from '../consts';

const template = require('./admin-export-view.component.html');
const style = require('./admin-export-view.component.scss');

@Component({
  selector: 'admin-export-view',
  template,
  styles: [style]
})
export class AdminExportViewComponent implements OnInit {
  @ViewChild('metricInput')
  metricInput: ElementRef;
  @Input() columns: any[];
  data: any[];
  metrics: any[] = [];
  selectedMetrics: any[] = [];
  metricCtrl = new FormControl();
  unSelectedMetrics: Observable<any[]>;
  separatorKeysCodes: number[] = [ENTER, COMMA];
  analyses: any[] = [];
  metrics$: Promise<any>;
  categoriesMap;
  isMetricSelectorFocused = false;
  isAtLeastOneAnalysisSelected = false;
  labelRequested = false;

  isEmpty = isEmpty;

  constructor(
    private _exportService: ExportService,
    private _sidenav: SidenavMenuService,
    private _categoryService: CategoryService,
    private _jwtService: JwtService
  ) {
    this.loadCategories();
    this.metrics$ = this._exportService.getMetricList();
    this.unSelectedMetrics = this.metricCtrl.valueChanges.pipe(
      startWith(''),
      mergeMap((searchTerm: string) => this._asyncFilter(searchTerm))
    );
    this.loadAnalyses = debounce(this.loadAnalyses, 1000);
  }

  ngOnInit() {
    this._sidenav.updateMenu(AdminMenuData, 'ADMIN');
  }

  onFocus() {
    this.isMetricSelectorFocused = true;
  }

  onBlur() {
    this.isMetricSelectorFocused = false;
  }

  loadCategories() {
    this._categoryService.getList().then(categories => {
      this.categoriesMap = reduce(categories, (acc, category) => {
        if (category.moduleName === 'ANALYZE') {
          forEach(category.subCategories, subCategory => {
            acc[subCategory.subCategoryId] = subCategory.subCategoryName;
          });
        }
        return acc;
      }, {});
    });
  }

  loadAnalyses() {
    const metricIds = lodashMap(this.selectedMetrics, ({id}) => id);
    if (isEmpty(this.selectedMetrics)) {
      this.analyses = [];
      return;
    }
    this._exportService.getAnalysisByMetricIds(metricIds).then(analyses => {
      this.analyses = fpPipe(
        fpFilter(({categoryId, name}) => !isUndefined(categoryId) && !isUndefined(name) && name !== ''),
        fpMap(analysis => {
          return {
            analysis,
            selection: false,
            categoryName: this.categoriesMap[analysis.categoryId]
          };
        })
      )(analyses);
    });
  }

  onRemove(index) {
    if (index >= 0) {
      this.selectedMetrics.splice(index, 1);
      this.loadAnalyses();
      this.metricCtrl.setValue(null);
      this.metricInput.nativeElement.value = '';
    }
  }

  onValidityChange(isValid) {
    this.isAtLeastOneAnalysisSelected = isValid;
  }

  onSelect(metric) {
    this.selectedMetrics.push(metric);
    this.metricInput.nativeElement.value = '';
    this.metricCtrl.setValue(null);
    this.loadAnalyses();
  }

  export() {
    const zip = new JSZip();
    const groupedAnalyses = fpPipe(
      fpFilter('selection'),
      fpMap('analysis'),
      fpGroupBy('metricName')
    )(this.analyses);

    forEach(groupedAnalyses, (analyses, metricName) => {
      const fileName = this.getFileName(metricName);
      zip.file(
        `${fileName}.json`,
        new Blob(
          [JSON.stringify(analyses)],
          {type: 'application/json;charset=utf-8'}
        )
      );
    });
    zip.generateAsync({type: 'blob'}).then(content => {
      let zipFileName = this.getFileName('');
      zipFileName = zipFileName.replace('_', '');
      FileSaver.saveAs(content, `${zipFileName}.zip`);
    });
  }

  getFileName(name) {
    const formatedDate = moment().format('YYYYMMDDHHmmss');
    const custCode = get(this._jwtService.getTokenObj(), 'ticket.custCode');
    name = name.replace(' ', '_');
    name = name.replace('\\', '-');
    name = name.replace('/', '-');
    return `${custCode}_${name}_${formatedDate}`;
  }

  splitAnalysisOnMetric(exportAnalysisList, metrics) {
    return lodashMap(metrics, ({metricName}) => {
      return {
        fileName: this.getFileName(metricName),
        analysisList: lodashFilter(exportAnalysisList, analysis => analysis.metricName === metricName)
      };
    });
  }

  private _asyncFilter(value) {
    const filterValue = lowerCase(value);
    return from(this.metrics$).pipe(
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
