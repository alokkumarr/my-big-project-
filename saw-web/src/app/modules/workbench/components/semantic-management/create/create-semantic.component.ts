import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { DxDataGridComponent } from 'devextreme-angular/ui/data-grid';

import { WorkbenchService } from '../../../services/workbench.service';

import * as filter from 'lodash/filter';
import * as cloneDeep from 'lodash/cloneDeep';

@Component({
  selector: 'create-semantic',
  templateUrl: './create-semantic.component.html',
  styleUrls: ['./create-semantic.component.scss']
})
export class CreateSemanticComponent implements OnInit {
  public availableDS: any;
  public joinToggleValue = false;
  public selectionMode = 'single';
  public gridDataAvailableDS: any;
  public isSelected = false;
  public selectedDSData: any = [];
  public contentHeight: number;

  constructor(public router: Router, public workBench: WorkbenchService) {}

  @ViewChild('dsGrid') dataGrid: DxDataGridComponent;

  ngOnInit() {
    this.workBench.getDatasets().subscribe((data: any[]) => {
      this.availableDS = data;
      this.gridDataAvailableDS = cloneDeep(data);
    });

    setTimeout(() => {
      this.contentHeight = window.innerHeight - 175;
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

  onResize(event) {
    this.contentHeight = event.target.innerHeight - 165;
  }
}
