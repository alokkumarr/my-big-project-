import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngxs/store';
import CheckBox from 'devextreme/ui/check_box';

import { ToastService } from '../../../../../common/services/toastMessage.service';
import { WorkbenchService } from '../../../services/workbench.service';
import { TYPE_CONVERSION } from '../../../wb-comp-configs';
import { CommonLoadUpdatedMetrics } from '../../../../../common/actions/common.actions';
import { NUMBER_TYPES, DATE_TYPES } from '../../../../../common/consts';

import * as get from 'lodash/get';
import * as cloneDeep from 'lodash/cloneDeep';
import * as forEach from 'lodash/forEach';
import * as map from 'lodash/map';
import * as toLower from 'lodash/toLower';
import * as find from 'lodash/find';
import * as some from 'lodash/some';
import * as omit from 'lodash/omit';
import * as isUndefined from 'lodash/isUndefined';
import * as filter from 'lodash/filter';
import * as set from 'lodash/set';
import * as has from 'lodash/has';

const NUMBER_AND_DATE = [...NUMBER_TYPES, ...DATE_TYPES];
const ELIGIBLE_COLS = ['kpiEligible', 'filterEligible', 'dskEligible'];
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
  public isDateTypeMatched = false;

  constructor(
    public router: Router,
    public workBench: WorkbenchService,
    public notify: ToastService,
    public store: Store
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
      forEach(this.selectedDPData, dp => {
        const parentDSName = dp.artifactName;
        const parentDSData = find(this.availableDS, obj => {
          return obj.system.name === parentDSName;
        });
        if (!isUndefined(parentDSData)) {
          this.isJoinEligible = parentDSData.joinEligible;
          this.injectFieldProperties(parentDSData);
          forEach(parentDSData.schema.fields, obj => {
            if (!some(dp.columns, ['columnName', obj.columnName])) {
              dp.columns.push(obj);
            }
          });
        }
        /**
         * Checking here if dskEligible property for all the columns are available. If not add it and set the default value to false.
         * For existing datapods issue SIP-9483 is reproducible. So adding properties for all the dp if it doesn't have.
         */
        forEach(dp.columns, col => {
          if (!has(col, 'dskEligible')) {
            set(col, 'dskEligible', false);
          }

          if (!has(col, 'kpiEligible')) {
            set(col, 'kpiEligible', false);
          }

          if (!has(col, 'include')) {
            set(col, 'include', false);
          }
        });
      });
    });
  }

  /**
   * Construct semantic layer field object structure.
   * When user does not include a column and update datapod, later point of time, same datapod
   * is updated then filterEligible is selected automatically for not included column.
   * So changing default value of filterEligible to false. Added as part of SIP-9565.
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
        dskEligible: false,
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
    forEach(this.selectedDPData, ds => {
      this.selectedDPDetails.artifacts.push({
        artifactName: ds.artifactName,
        columns: filter(ds.columns, 'include')
      });
    });
    this.workBench
      .updateSemanticDetails(this.selectedDPDetails)
      .subscribe(() => {
        this.notify.info('Datapod Updated successfully', 'Datapod', {
          hideDelay: 9000
        });
        // When any datapod is updated, make it available in Analyze module without page refresh. Added as a part of SIP-9482.
        this.store.dispatch(new CommonLoadUpdatedMetrics());
        this.router.navigate(['workbench', 'dataobjects']);
      });
  }

  /**
   *
   * @param e
   * Disable checkbox of non numeric and date type fields in KPI Eligible column. Added as part of SIP-9373.
   *
   * When Include checkbox is not selected for a column then
   * uncheck and disable filterEligible, kpiEligible and dskEligible for same column.
   * Also when Include column is checked then enable all the checkboxes
   * for all the eligible columns based on requirement of SIP-9373.
   * Added as part of SIP-9566
   */
  cellPrepared(e) {
    if (e.rowType === 'data' && ELIGIBLE_COLS.includes(e.column.dataField)) {
      const check =
        !e.row.cells[0].value ||
        ((['kpiEligible'].includes(e.column.dataField) &&
          !NUMBER_AND_DATE.includes(e.data.type)) ||
          !this.isDateTypeMatched);
      CheckBox.getInstance(e.cellElement.querySelector('.dx-checkbox')).option(
        'disabled',
        check
      );
    }
    return;
  }

  cellClicked(e) {
    if (e.rowType === 'data' && e.column.dataField === 'include') {
      const matchedCol = find(this.selectedDPData[0].columns, col => {
        // For new datapod aliasName is changed to alias hence checking for both conditions.
        return col.aliasName
          ? col.aliasName === e.data.aliasName
          : col.alias === e.data.alias;
      });
      const check = matchedCol && !matchedCol.include;
      set(matchedCol, 'filterEligible', !check);
      if (check) {
        set(matchedCol, 'kpiEligible', !check);
        set(matchedCol, 'dskEligible', !check);
      }
    }
    return;
  }
}
