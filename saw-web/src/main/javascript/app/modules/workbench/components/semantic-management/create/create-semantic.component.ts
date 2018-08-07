import { Component, OnInit, ViewChild } from '@angular/core';
import { UIRouter } from '@uirouter/angular';
import { DxDataGridComponent } from 'devextreme-angular';

import { HeaderProgressService } from '../../../../../common/services/header-progress.service';
import { WorkbenchService } from '../../../services/workbench.service';

import * as filter from 'lodash/filter';
import * as cloneDeep from 'lodash/cloneDeep';

const template = require('./create-semantic.component.html');
require('./create-semantic.component.scss');

@Component({
  selector: 'create-semantic',
  styles: [],
  template: template
})
export class CreateSemanticComponent implements OnInit {
  private availableDS: any;
  private joinToggleValue: boolean = false;
  private selectionMode: string = 'single';
  private gridDataAvailableDS: any;
  private isSelected: boolean = false;
  private selectedDSData: any = [];

  constructor(
    private router: UIRouter,
    private workBench: WorkbenchService,
    private headerProgress: HeaderProgressService
  ) {}

  @ViewChild('dsGrid') dataGrid: DxDataGridComponent;

  ngOnInit() {
    this.headerProgress.show();
    this.workBench.getDatasets().subscribe((data: any[]) => {
      this.availableDS = data;
      this.gridDataAvailableDS = cloneDeep(data);
      this.headerProgress.hide();
    });
  }

  backToDS() {
    this.router.stateService.go('workbench.datasets');
  }

  joinEligibleToggled() {
    if (this.joinToggleValue) {
      this.selectionMode = 'multiple';
      this.gridDataAvailableDS = filter(this.availableDS, [
        'system.format',
        'parquet'
      ]);
    } else {
      this.selectionMode = 'single';
      this.gridDataAvailableDS = this.availableDS;
    }
    this.dataGrid.instance.clearSelection();
    this.isSelected = false;
    this.selectedDSData = [];
  }

  onDSSelectionChanged(event) {
    if (event.currentSelectedRowKeys.length >= 1) {
      this.isSelected = true;
      this.selectedDSData = event.selectedRowsData;
    } else {
      this.isSelected = false;
    }
  }

  gotoValidate() {
    this.workBench.setDataToLS('selectedDS', this.selectedDSData);
    this.router.stateService.go('workbench.validateSemantic');
  }
}
