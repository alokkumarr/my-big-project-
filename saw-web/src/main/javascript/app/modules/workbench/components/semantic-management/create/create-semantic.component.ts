import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { DxDataGridComponent } from 'devextreme-angular';

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
    private router: Router,
    private workBench: WorkbenchService
  ) {}

  @ViewChild('dsGrid') dataGrid: DxDataGridComponent;

  ngOnInit() {
    this.workBench.getDatasets().subscribe((data: any[]) => {
      this.availableDS = data;
      this.gridDataAvailableDS = cloneDeep(data);
    });
  }

  backToDS() {
    this.router.navigate(['workbench', 'dataobjects']);
  }

  /**
   * Only Datalake datasets are join eligible as of now.
   *
   * @memberof CreateSemanticComponent
   */
  joinEligibleToggled() {
    if (this.joinToggleValue) {
      this.selectionMode = 'multiple';
      this.gridDataAvailableDS = filter(this.availableDS, 'joinEligible');
    } else {
      this.selectionMode = 'single';
      this.gridDataAvailableDS = this.availableDS;
    }
    this.dataGrid.instance.clearSelection();
    this.isSelected = false;
    this.selectedDSData = [];
  }

  onDSSelectionChanged(event) {
    if (event.selectedRowsData.length >= 1) {
      this.isSelected = true;
      this.selectedDSData = event.selectedRowsData;
    } else {
      this.isSelected = false;
      this.selectedDSData = [];
    }
  }

  gotoValidate() {
    this.workBench.setDataToLS('selectedDS', this.selectedDSData);
    this.router.navigate(['workbench', 'semantic', 'validate']);
  }
}
