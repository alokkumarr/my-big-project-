import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { Dashboard } from '../../../observe/models/dashboard.interface';
import { Analysis } from '../../../analyze/models';
import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { map, withLatestFrom } from 'rxjs/operators';
import * as intersectionBy from 'lodash/intersectionBy';

export interface ExportItemChangeOutput {
  checked: boolean;
  item: Analysis | Dashboard;
}

@Component({
  selector: 'admin-export-content',
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.scss']
})
export class AdminExportContentComponent implements OnInit {
  @Input() exportList: Observable<any[]>;
  @Input() analyses: Observable<any[]>;
  // @Input() dashboards: Observable<any[]>;
  @Output() change: EventEmitter<ExportItemChangeOutput> = new EventEmitter();
  @Output() changeAll: EventEmitter<boolean> = new EventEmitter();

  intersection$: Observable<any[]>;
  allSelected$: Observable<boolean>;
  someSelected$: Observable<boolean>;

  config: any;

  constructor(private dxDataGridService: DxDataGridService) {}

  ngOnInit() {
    this.config = this.getConfig();

    // Calculates intersection of export bucket and current selection list.
    // Meant to cacluate which analyses in current selection list are also in export
    this.intersection$ = this.exportList.pipe(
      withLatestFrom(this.analyses),
      map(([exportList, analyses]) => {
        return [
          intersectionBy(exportList, analyses, x =>
            x.entityId ? x.entityId : x.id
          ),
          analyses
        ];
      })
    );

    // Whether all analyses in current selection list have been added to export
    this.allSelected$ = this.intersection$.pipe(
      map(
        ([intersection, analyses]) =>
          intersection.length === analyses.length && analyses.length > 0
      )
    );

    // Whether some analyses in current selection list have been added to export
    this.someSelected$ = this.intersection$.pipe(
      map(
        ([intersection, analyses]) =>
          intersection.length < analyses.length && intersection.length > 0
      )
    );
  }

  /**
   * Handler for checkbox change of each item in list
   *
   * @param {*} { checked }
   * @param {*} item
   * @memberof AdminExportContentComponent
   */
  onItemToggled({ checked }: any, item: any) {
    this.change.emit({ checked, item });
  }

  /**
   * Whether the given item is also present in export bucket
   *
   * @param {*} item
   * @returns {Observable<boolean>}
   * @memberof AdminExportContentComponent
   */
  isSelectedForExport$(item: any): Observable<boolean> {
    return this.exportList.pipe(
      map(list =>
        list.some(a => {
          return item.entityId
            ? a.entityId === item.entityId
            : a.id === item.id;
        })
      )
    );
  }

  /**
   * Handle toggling the 'all' checkbox at the top of list
   *
   * @param {*} { checked }
   * @memberof AdminExportContentComponent
   */
  onToggleAll({ checked }: any) {
    this.changeAll.emit(checked);
  }

  /**
   * Returns data grid config merged with default
   *
   * @returns
   * @memberof AdminExportContentComponent
   */
  getConfig() {
    const columns = [
      {
        caption: '',
        allowSorting: false,
        alignment: 'center',
        headerCellTemplate: 'selectionHeaderCellTemplate',
        cellTemplate: 'selectionCellTemplate',
        width: '10%'
      },
      {
        caption: 'Name',
        dataField: 'name',
        allowSorting: true,
        alignment: 'left',
        width: '40%'
      },
      {
        caption: 'Type',
        dataField: 'type',
        allowSorting: true,
        alignment: 'left',
        width: '25%'
      },
      {
        caption: 'Metric',
        dataField: 'metricName',
        allowSorting: true,
        alignment: 'left',
        width: '25%'
      }
    ];

    return this.dxDataGridService.mergeWithDefaultConfig({
      columns,
      noDataText: 'No Data. Select a category to view analyses.',
      width: '100%',
      height: '100%',
      paging: {
        pageSize: 10
      },
      pager: {
        showPageSizeSelector: true,
        showInfo: true
      }
    });
  }
}
