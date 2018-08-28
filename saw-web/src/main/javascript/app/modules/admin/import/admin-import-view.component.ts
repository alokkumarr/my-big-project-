import { Component, Input, ViewChild, ElementRef } from '@angular/core';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
import * as fpMap from 'lodash/fp/map';
import * as map from 'lodash/map';
import * as reduce from 'lodash/reduce';
import * as forEach from 'lodash/forEach';
import * as filter from 'lodash/filter';
import * as assign from 'lodash/assign';
import * as fpFlatMap from 'lodash/fp/flatMap';
import { JwtService } from '../../../../login/services/jwt.service';
import { ImportService } from './import.service';
import { CategoryService } from '../category/category.service';
import { SidenavMenuService } from '../../../common/components/sidenav';
import { AdminMenuData } from '../consts';
import { Analysis } from '../../../models';
import { ExportService } from '../export/export.service';

const template = require('./admin-import-view.component.html');
require('./admin-import-view.component.scss');

type FileInfo = {name: string, count: number};
type FileContent = {name: string, count: number, analyses: Array<Analysis>};
@Component({
  selector: 'admin-import-view',
  template
})
export class AdminImportViewComponent {
  files: Array<FileInfo>;
  fileContents: Array<FileContent>
  selectedCategory;
  categories$;
  metricsMap;
  analyses: Array<Analysis>;
  analysesFromBEMap: Array<Analysis>;

  @Input() columns: any[];

  constructor(
    private _importService: ImportService,
    private _exportService: ExportService,
    private _categoryService: CategoryService,
    private _sidenav: SidenavMenuService,
    private _jwtService: JwtService
  ) {}

  ngOnInit() {
    this._sidenav.updateMenu(AdminMenuData, 'ADMIN');
    this.categories$ = this._categoryService.getList().map(fpFilter(category => category.moduleName === 'ANALYZE'));
    this.getMetrics();
  }

  getMetrics() {
    this._exportService.getMetricList().then(metrics => {
      this.metricsMap = reduce(metrics, (acc, metric) => {
        acc[metric.metricName] = metric;
        return acc;
      }, {});
    })
  }

  onRemoveFile(fileName) {
    this.fileContents = filter(this.fileContents, ({name}) => fileName !== name);
    this.splitFileContents(this.fileContents);
  }

  splitFileContents(contents) {
    this.files = map(contents, ({name, count}) => ({name, count}));
    this.analyses = fpPipe(
      fpFlatMap(({analyses}) => analyses),
      fpMap(analysis => this.getAnalysisObjectForGrid(analysis))
    )(contents);
    console.log('analyses', this.analyses);
    console.log('files', this.files);
  }

  readFiles(event) {
    const files = event.target.files;

    const contentPromises = fpPipe(
      fpFilter(file => file.type === 'application/json'),
      fpMap(file => this.getFileContents(file)
        .then(content => {
          const analyses = JSON.parse(content);
          return {
            name: file.name,
            count: analyses.length,
            analyses
          };
        })
      )
    )(files);

    Promise.all(contentPromises).then(contents => {
      this.fileContents = contents;
      this.splitFileContents(contents);
    });
  }

  getFileContents(file) {
    return new Promise<string>((resolve, reject) => {
      if (typeof FileReader !== 'function') {
        reject(new Error('The file API isn\'t supported on this browser.'));
      }

      const fr = new FileReader();
      fr.onload = e => {
        resolve(e.target ? e.target.result : '');
      }
      fr.readAsText(file);
    });
  }

  onCategoryChange(categoryId) {
    this.selectedCategory = categoryId;
    this._importService.getAnalysesFor(categoryId).then(analyses => {
      this.analysesFromBEMap = reduce(analyses, (acc, analysis) => {
        acc[analysis.id] = analysis;
        return acc;
      }, {});
      this.splitFileContents(this.fileContents);
    });
  }

  checkForDuplicateAnalyses() {
    forEach(this.analyses, analysisGridObj => {
      const analysis = analysisGridObj.analysis;
      const analysisFromBE = this.analysesFromBEMap[analysis.id];
      if (analysisFromBE && !analysisGridObj.noMetricInd) {
        assign(analysisGridObj, {
          duplicateAnalysisInd: true,
          selection: false,
          logColor: 'brown',
          errorMsg: 'Analysis exists. Please Override to delete existing data.',
          errorInd: false,
          log: 'Analysis exists. Please Override to delete existing data.'
        });
        const {
          isScheduled,
          scheduled,
          createdTimestamp,
          esRepository,
          repository
        } = analysis;
        const {
          userFullName,
          userId
        } = this._jwtService.getTokenObj().ticket;

        assign(analysisGridObj.analysis, {
          isScheduled,
          scheduled,
          createdTimestamp,
          userFullName,
          userId,
          esRepository,
          repository
        })
      }
    });
  }

  getAnalysisObjectForGrid(analysis) {
    const metric = this.metricsMap[analysis.metricName];
    if (metric) {
      analysis.semanticId = metric.id;
    }

    return {
      duplicateAnalysisInd: false,
      selection: false,
      errorInd: !metric,
      errorMsg: metric ? '' : `${analysis.metricName} : Metric does not exists.`,
      logColor: metric ? '' : 'red',
      log: metric ? 'Metric doesn\'t exists.' : '',
      noMetricInd: !metric,
      analysis
    };

  }
}
