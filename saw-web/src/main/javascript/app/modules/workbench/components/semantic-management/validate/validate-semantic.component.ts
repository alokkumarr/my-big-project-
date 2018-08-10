import { Component, OnDestroy } from '@angular/core';
import { UIRouter } from '@uirouter/angular';
import { MatDialog, MatDialogRef } from '@angular/material';

import { SemanticDetailsDialogComponent } from '../semantic-details-dialog/semantic-details-dialog.component';

import { HeaderProgressService } from '../../../../../common/services/header-progress.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';

import { WorkbenchService } from '../../../services/workbench.service';
import { TYPE_CONVERSION } from '../../../wb-comp-configs';

import * as forIn from 'lodash/forIn';
import * as map from 'lodash/map';
import * as toLower from 'lodash/toLower';
import * as filter from 'lodash/filter';
import * as trim from 'lodash/trim';

const template = require('./validate-semantic.component.html');
require('./validate-semantic.component.scss');

@Component({
  selector: 'validate-semantic',
  styles: [],
  template: template
})
export class ValidateSemanticComponent implements OnDestroy {
  private selectedDS: any;
  private isJoinEligible: boolean = false;

  constructor(
    private router: UIRouter,
    private workBench: WorkbenchService,
    private headerProgress: HeaderProgressService,
    public dialog: MatDialog,
    private notify: ToastService
  ) {
    this.selectedDS = this.injectFieldProperties(
      this.workBench.getDataFromLS('selectedDS')
    );
  }

  ngOnDestroy() {
    this.workBench.removeDataFromLS('selectedDS');
  }

  showDSList() {
    this.router.stateService.go('workbench.createSemantic');
  }

  /**
   * Construct semantic layer field object structure.
   *
   * @param {*} dsData
   * @returns
   * @memberof ValidateSemanticComponent
   */
  injectFieldProperties(dsData) {
    forIn(dsData, value => {
      this.isJoinEligible = value.joinEligible;
      const artifactName = value.system.name;
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
          table: artifactName,
          type: TYPE_CONVERSION[toLower(value.type)]
        };
      });
    });

    return dsData;
  }

  /**
   * Opens dialog for Semantic layer name( metric name) and constructs the Semantic layer structure with only mandatory parameters.
   *
   * @memberof ValidateSemanticComponent
   */
  createDatapod() {
    const dialogRef = this.dialog.open(SemanticDetailsDialogComponent, {
      hasBackdrop: true,
      autoFocus: true,
      closeOnNavigation: true,
      disableClose: true,
      height: '236px',
      width: '350px'
    });

    dialogRef.afterClosed().subscribe(name => {
      if (trim(name).length > 0) {
        const payload = {
          customerCode: '',
          username: '',
          projectCode: '',
          metricName: '',
          artifacts: [],
          supports: [
            {
              category: 'table',
              children: [],
              label: 'tables'
            },
            {
              category: 'charts',
              children: [],
              label: 'charts'
            }
          ]
        };
        payload.metricName = name;

        forIn(this.selectedDS, ds => {
          payload.artifacts.push({
            artifactName: ds.system.name,
            columns: filter(ds.schema.fields, 'include')
          });
        });

        this.headerProgress.show();
        this.workBench.createSemantic(payload).subscribe((data: any[]) => {
          this.headerProgress.hide();
          this.notify.info('Datapod created successfully', 'Datapod', {
            hideDelay: 9000
          });
          this.router.stateService.go('workbench.datasets');
        });
        this.headerProgress.hide();
      }
    });
  }
}
