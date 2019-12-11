import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import CheckBox from 'devextreme/ui/check_box';

import { ToastService } from '../../../../../common/services/toastMessage.service';
import { WorkbenchService } from '../../../services/workbench.service';
import { TYPE_CONVERSION } from '../../../wb-comp-configs';
import { NUMBER_TYPES, DATE_TYPES } from '../../../../../../app/common/consts';

import * as get from 'lodash/get';
import * as cloneDeep from 'lodash/cloneDeep';
import * as forIn from 'lodash/forIn';
import * as map from 'lodash/map';
import * as toLower from 'lodash/toLower';
import * as find from 'lodash/find';
import * as findIndex from 'lodash/findIndex';
import * as omit from 'lodash/omit';
import * as isUndefined from 'lodash/isUndefined';
import * as filter from 'lodash/filter';
import * as some from 'lodash/some';
@Component({
  selector: 'update-semantic',
  templateUrl: './update-semantic.component.html',
  styleUrls: ['./update-semantic.component.scss']
})
export class UpdateSemanticComponent implements OnInit, OnDestroy {
  public availableDP: any;
  public gridDataAvailableDP: any;
  public isSelected = false;
  public selectedDPData: any = [];
  public availableDS: any = [];
  public isJoinEligible = false;
  public selectedDPDetails: any = [];
  public dpID = '';
  public isDateTypeMatched = true;

  constructor(
    public router: Router,
    public workBench: WorkbenchService,
    public notify: ToastService
  ) {
    // Below is used when navigating from Datapod view
    this.dpID = this.workBench.getDataFromLS('dpID');
  }

  ngOnInit() {
    this.workBench.getDatasets().subscribe((data: any[]) => {
      this.availableDS = data;
      if (this.dpID !== null) {
        this.onDPSelectionChanged(this.dpID);
      } else {
        this.workBench.getListOfSemantic().subscribe((list: any[]) => {
          this.availableDP = get(list, 'contents[0].ANALYZE');
          this.gridDataAvailableDP = cloneDeep(this.availableDP);
        });
      }
    });
  }

  ngOnDestroy() {
    if (this.dpID !== null) {
      this.workBench.removeDataFromLS('dpID');
    }
  }

  backToDS() {
    this.router.navigate(['workbench', 'dataobjects']);
  }

  /**
   * Gets the detailed description of Datapod and its parent dataset/s.
   * Merges all the fields and shows which are included.
   *
   * @param {*} id
   * @memberof UpdateSemanticComponent
   */
  onDPSelectionChanged(id) {
    this.isSelected = true;
    this.workBench.getSemanticDetails(id).subscribe((data: any) => {
      this.selectedDPDetails = omit(data, 'statusMessage');
      this.selectedDPData = get(data, 'artifacts');
      forIn(this.selectedDPData, dp => {
        const parentDSName = dp.artifactName;
        const parentDSData = find(this.availableDS, obj => {
          return obj.system.name === parentDSName;
        });
        if (!isUndefined(parentDSData)) {
          this.isJoinEligible = parentDSData.joinEligible;
          this.injectFieldProperties(parentDSData);
          forIn(parentDSData.schema.fields, obj => {
            if (findIndex(dp.columns, ['columnName', obj.columnName]) === -1) {
              dp.columns.push(obj);
            }
          });
        }
        this.isDateTypeMatched = some(this.selectedDPData[0].columns, obj => {
          return DATE_TYPES.includes(obj.type);
        });
      });
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
      const colName = value.isKeyword ? `${value.name}.keyword` : value.name;
      return {
        alias: value.name,
        columnName: colName,
        displayName: value.name,
        filterEligible: false,
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

  /**
   * Updates the saemantic definition with user changes.
   *
   * @memberof UpdateSemanticComponent
   */
  updateSemantic() {
    this.selectedDPDetails.artifacts = [];
    forIn(this.selectedDPData, ds => {
      this.selectedDPDetails.artifacts.push({
        artifactName: ds.artifactName,
        columns: filter(ds.columns, 'include')
      });
    });
    this.workBench
      .updateSemanticDetails(this.selectedDPDetails)
      .subscribe((data: any[]) => {
        this.notify.info('Datapod Updated successfully', 'Datapod', {
          hideDelay: 9000
        });
        this.router.navigate(['workbench', 'dataobjects']);
      });
  }

  cellPrepared(e) {
    if (e.rowType === 'data' && e.column.dataField === 'kpiEligible') {
      if (
        (!NUMBER_TYPES.includes(e.data.type) &&
          !DATE_TYPES.includes(e.data.type)) ||
        !this.isDateTypeMatched
      ) {
        CheckBox.getInstance(
          e.cellElement.querySelector('.dx-checkbox')
        ).option('disabled', true);
      }
    }
  }
}
