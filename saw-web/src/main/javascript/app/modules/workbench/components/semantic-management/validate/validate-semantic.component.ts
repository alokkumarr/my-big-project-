import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { UIRouter } from '@uirouter/angular';
import { DxDataGridComponent } from 'devextreme-angular';

import { HeaderProgressService } from '../../../../../common/services/header-progress.service';
import { WorkbenchService } from '../../../services/workbench.service';
import { TYPE_CONVERSION } from '../../../wb-comp-configs';

import * as forIn from 'lodash/forIn';
import * as map from 'lodash/map';
import * as toLower from 'lodash/toLower';

const template = require('./validate-semantic.component.html');
require('./validate-semantic.component.scss');

@Component({
  selector: 'validate-semantic',
  styles: [],
  template: template
})
export class ValidateSemanticComponent implements OnDestroy {
  private selectedDS: any;

  constructor(
    private router: UIRouter,
    private workBench: WorkbenchService,
    private headerProgress: HeaderProgressService
  ) {
    this.selectedDS = this.injectFieldProperties(
      this.workBench.getDataFromLS('selectedDS')
    );
  }

  @ViewChild('dsGrid') dataGrid: DxDataGridComponent;

  ngOnInit() {}

  ngOnDestroy() {
    this.workBench.removeDataFromLS('selectedDS');
  }

  showDSList() {
    this.router.stateService.go('workbench.createSemantic');
  }

  injectFieldProperties(dsData) {
    forIn(dsData, value => {
      value.schema.fields = map(value.schema.fields, value => {
        return {
          aliasName: value.name,
          columnName: value.name,
          displayName: value.name,
          filterEligible: true,
          joinEligible: false,
          kpiEligible: false,
          include: true,
          name: value.name,
          table: value.name,
          type: TYPE_CONVERSION[toLower(value.type)]
        };
      });
    });

    return dsData;
  }
}
