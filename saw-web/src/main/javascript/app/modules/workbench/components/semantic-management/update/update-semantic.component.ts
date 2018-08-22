import { Component, OnInit } from '@angular/core';
import { UIRouter } from '@uirouter/angular';

import { HeaderProgressService } from '../../../../../common/services/header-progress.service';
import { ToastService } from '../../../../../common/services/toastMessage.service';
import { WorkbenchService } from '../../../services/workbench.service';
import { TYPE_CONVERSION } from '../../../wb-comp-configs';

import * as get from 'lodash/get';
import * as cloneDeep from 'lodash/cloneDeep';
import * as forIn from 'lodash/forIn';
import * as map from 'lodash/map';
import * as toLower from 'lodash/toLower';
import * as filter from 'lodash/filter';
import * as find from 'lodash/find';
import * as findIndex from 'lodash/findIndex';
import * as omit from 'lodash/omit';

const template = require('./update-semantic.component.html');
require('./update-semantic.component.scss');

@Component({
  selector: 'update-semantic',
  styles: [],
  template: template
})
export class UpdateSemanticComponent implements OnInit {
  private availableDP: any;
  private gridDataAvailableDP: any;
  private isSelected: boolean = false;
  private selectedDPData: any = [];
  private availableDS: any = [];
  private isJoinEligible: boolean = false;
  private selectedDPDetails: any = [];

  constructor(
    private router: UIRouter,
    private workBench: WorkbenchService,
    private headerProgress: HeaderProgressService,
    private notify: ToastService
  ) {}

  ngOnInit() {
    this.headerProgress.show();
    this.workBench.getListOfSemantic().subscribe((data: any[]) => {
      this.availableDP = get(data, 'contents[0].ANALYZE');
      this.gridDataAvailableDP = cloneDeep(this.availableDP);
      this.headerProgress.hide();
    });
    this.workBench.getDatasets().subscribe((data: any[]) => {
      this.availableDS = data;
    });
  }

  backToDS() {
    this.router.stateService.go('workbench.datasets');
  }

  onDPSelectionChanged(event) {
    this.headerProgress.show();
    this.isSelected = true;
    const datapodID = event.currentSelectedRowKeys[0];
    this.workBench.getSemanticDetails(datapodID).subscribe((data: any) => {
      this.selectedDPDetails = omit(data, 'statusMessage');
      this.selectedDPData = get(data, 'artifacts');
      forIn(this.selectedDPData, dp => {
        const parentDSName = dp.artifactName;
        const parentDSData = find(this.availableDS, obj => {
          return obj.system.name === parentDSName;
        });
        this.isJoinEligible = parentDSData.joinEligible;

        this.injectFieldProperties(parentDSData);

        forIn(parentDSData.schema.fields, obj => {
          if (findIndex(dp.columns, ['columnName', obj.columnName]) === -1) {
            dp.columns.push(obj);
          }
        });
      });
      this.headerProgress.hide();
    });
  }

  /**
   * Construct semantic layer field object structure.
   *
   * @param {*} dsData
   * @returns
   * @memberof ValidateSemanticComponent
   */
  injectFieldProperties(dsData) {
    const artifactName = dsData.system.name;
    dsData.schema.fields = map(dsData.schema.fields, value => {
      return {
        aliasName: value.name,
        columnName: value.name,
        displayName: value.name,
        filterEligible: true,
        joinEligible: false,
        kpiEligible: false,
        include: false,
        name: value.name,
        table: artifactName,
        type: TYPE_CONVERSION[toLower(value.type)]
      };
    });

    return dsData;
  }

  updateSemantic() {
    forIn(this.selectedDPData, ds => {
      this.selectedDPDetails.artifacts = [];
      this.selectedDPDetails.artifacts.push({
        artifactName: ds.artifactName,
        columns: filter(ds.columns, 'include')
      });
    });
    this.headerProgress.show();
    this.workBench
      .updateSemanticDetails(this.selectedDPDetails)
      .subscribe((data: any[]) => {
        this.headerProgress.hide();
        this.notify.info('Datapod Updated successfully', 'Datapod', {
          hideDelay: 9000
        });
        this.router.stateService.go('workbench.datasets');
      });
  }
}
