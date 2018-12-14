import {
  Component,
  OnInit,
  Input,
  Output,
  EventEmitter,
  ChangeDetectionStrategy
} from '@angular/core';
import { Dashboard } from '../../../observe/models/dashboard.interface';
import { Analysis } from '../../../analyze/models';
import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';
import * as intersectionBy from 'lodash/intersectionBy';

export interface ExportItemChangeOutput {
  checked: boolean;
  item: Analysis | Dashboard;
}

@Component({
  selector: 'admin-export-content',
  templateUrl: './content.component.html',
  styleUrls: ['./content.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminExportContentComponent implements OnInit {
  @Input() exportList: any[];
  @Input() analyses: any[];
  // @Input() dashboards: Observable<any[]>;

  /**
   * Happens whenever an item in the list is toggled
   *
   * @type {EventEmitter<ExportItemChangeOutput>}
   * @memberof AdminExportContentComponent
   */
  @Output() change: EventEmitter<ExportItemChangeOutput> = new EventEmitter();

  /**
   * Happens when user toggles the 'All' checkbox in header
   *
   * @type {EventEmitter<boolean>}
   * @memberof AdminExportContentComponent
   */
  @Output() changeAll: EventEmitter<boolean> = new EventEmitter();

  config: any;

  constructor(private dxDataGridService: DxDataGridService) {}

  ngOnInit() {
    this.config = this.getConfig();
  }

  /**
   * Calculates intersection of export bucket and current selection list.
   * Meant to cacluate which analyses in current selection list are also in export.
   *
   * @returns
   * @memberof AdminExportContentComponent
   */
  intersection(): any[] {
    return [
      intersectionBy(this.exportList, this.analyses, x =>
        x.entityId ? x.entityId : x.id
      ),
      this.analyses
    ];
  }

  /**
   * Whether all analyses in current selection list have been added to export
   *
   * @returns {boolean}
   * @memberof AdminExportContentComponent
   */
  allSelected(): boolean {
    const [intersection, analyses] = this.intersection();
    return intersection.length === analyses.length && analyses.length > 0;
  }

  /**
   * Whether some analyses in current selection list have been added to export
   *
   * @returns {boolean}
   * @memberof AdminExportContentComponent
   */
  someSelected(): boolean {
    const [intersection, analyses] = this.intersection();
    return intersection.length < analyses.length && intersection.length > 0;
  }

  /**
   * Whether the given item is also present in export bucket
   *
   * @param {*} item
   * @returns {Observable<boolean>}
   * @memberof AdminExportContentComponent
   */
  isSelectedForExport(item: any): boolean {
    return this.exportList.some(a => {
      return item.entityId ? a.entityId === item.entityId : a.id === item.id;
    });
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
