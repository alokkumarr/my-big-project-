import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material';
import * as forIn from 'lodash/forIn';
import * as map from 'lodash/map';
import * as toLower from 'lodash/toLower';
import * as split from 'lodash/split';
import * as filter from 'lodash/filter';
import * as trim from 'lodash/trim';

import { SemanticDetailsDialogComponent } from '../semantic-details-dialog/semantic-details-dialog.component';
import { ToastService } from '../../../../../common/services/toastMessage.service';
import { WorkbenchService } from '../../../services/workbench.service';
import { TYPE_CONVERSION } from '../../../wb-comp-configs';

@Component({
  selector: 'validate-semantic',
  templateUrl: './validate-semantic.component.html',
  styleUrls: ['./validate-semantic.component.scss']
})
export class ValidateSemanticComponent implements OnDestroy {
  public selectedDS: any;
  public isJoinEligible = false;

  constructor(
    public router: Router,
    public workBench: WorkbenchService,
    public dialog: MatDialog,
    public notify: ToastService
  ) {
    this.selectedDS = this.injectFieldProperties(
      this.workBench.getDataFromLS('selectedDS')
    );
  }

  ngOnDestroy() {
    this.workBench.removeDataFromLS('selectedDS');
  }

  showDSList() {
    this.router.navigate(['workbench', 'semantic', 'create']);
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
      value.schema.fields = map(value.schema.fields, val => {
        return {
          aliasName: val.name,
          columnName: val.name,
          displayName: val.name,
          filterEligible: true,
          joinEligible: false,
          kpiEligible: false,
          include: true,
          name: val.name,
          table: artifactName,
          type: TYPE_CONVERSION[toLower(val.type)]
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
      height: 'auto',
      width: '350px'
    });

    dialogRef.afterClosed().subscribe(({name, category}) => {
      if (trim(name).length > 0) {
        const payload = {
          category,
          customerCode: '',
          username: '',
          projectCode: '',
          metricName: '',
          artifacts: [],
          esRepository: { indexName: '', storageType: '', type: '' },
          supports: [
            { category: 'table', children: [], label: 'tables' },
            { category: 'charts', children: [], label: 'charts' }
          ],
          parentDataSetNames: [],
          parentDataSetIds: []
        };
        payload.metricName = name;

        forIn(this.selectedDS, ds => {
          if (ds.storageType === 'ES') {
            payload.esRepository.indexName = ds.system.name;
            payload.esRepository.storageType = 'ES';
            payload.esRepository.type = 'session';
          }
          payload.artifacts.push({
            artifactName: ds.system.name,
            columns: filter(ds.schema.fields, 'include')
          });
          payload.parentDataSetNames.push(ds.system.name);
          payload.parentDataSetIds.push(split(ds._id, '::')[1]);
        });

        this.workBench.createSemantic(payload).subscribe((data: any[]) => {
          this.notify.info('Datapod created successfully', 'Datapod', {
            hideDelay: 9000
          });
          this.router.navigate(['workbench', 'dataobjects']);
        });
      }
    });
  }
}
