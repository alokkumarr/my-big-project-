import { Component, OnInit, ViewChild } from '@angular/core';
import { UIRouter } from '@uirouter/angular';
import { DxDataGridComponent } from 'devextreme-angular';

import { HeaderProgressService } from '../../../../../common/services/header-progress.service';
import { WorkbenchService } from '../../../services/workbench.service';

import * as get from 'lodash/get';
import * as cloneDeep from 'lodash/cloneDeep';

const template = require('./update-semantic.component.html');
require('./update-semantic.component.scss');

@Component({
  selector: 'update-semantic',
  styles: [],
  template: template
})
export class UpdateSemanticComponent implements OnInit {
  private availableDP: any;
  private joinToggleValue: boolean = false;
  private selectionMode: string = 'single';
  private gridDataAvailableDP: any;
  private isSelected: boolean = false;
  private selectedDPData: any = [];

  constructor(
    private router: UIRouter,
    private workBench: WorkbenchService,
    private headerProgress: HeaderProgressService
  ) {}

  @ViewChild('dsGrid')
  dataGrid: DxDataGridComponent;

  ngOnInit() {
    this.headerProgress.show();
    this.workBench.getListOfDatapods().subscribe((data: any[]) => {
      this.availableDP = get(data, 'contents[0].ANALYZE');
      this.gridDataAvailableDP = cloneDeep(this.availableDP);
      this.headerProgress.hide();
    });
  }

  backToDS() {
    this.router.stateService.go('workbench.datasets');
  }

  onDPSelectionChanged(event) {
    if (event.selectedRowsData.length >= 1) {
      this.isSelected = true;
      this.selectedDPData = event.selectedRowsData;
    } else {
      this.isSelected = false;
      this.selectedDPData = [];
    }
  }
}
