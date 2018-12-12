import { Component, OnInit, OnDestroy } from '@angular/core';
import { Select, Store } from '@ngxs/store';
import { JwtService } from '../../../common/services';
import {
  ResetExportPageState,
  ExportSelectTreeItem,
  AddAnalysisToExport,
  RemoveAnalysisFromExport
} from './actions/export-page.actions';
import { Menu } from '../../../common/state/common.state.model';
import { AdminExportLoadMenu } from '../../../common/actions/menu.actions';
import { ExportPageState } from './state/export-page.state';
import { ExportService } from './export.service';
import { SidenavMenuService } from '../../../common/components/sidenav';
import { AdminMenuData } from '../consts';
import { Observable } from 'rxjs/Observable';
import { map } from 'rxjs/operators';
import * as JSZip from 'jszip';
import * as FileSaver from 'file-saver';
import * as moment from 'moment';
import * as get from 'lodash/get';

@Component({
  selector: 'admin-export-view',
  templateUrl: './admin-export-view.component.html',
  styleUrls: ['./admin-export-view.component.scss']
})
export class AdminExportViewComponent implements OnInit, OnDestroy {
  @Select(state => state.common.analyzeMenu) analyzeMenu$: Observable<Menu>;

  @Select(ExportPageState.exportList) exportList$: Observable<any[]>;

  @Select(state => state.admin.exportPage.categoryAnalyses)
  exportAnalyses$: Observable<any[]>;

  isExportListEmpty$ = this.exportList$.pipe(map(list => list.length <= 0));

  constructor(
    public _exportService: ExportService,
    public _sidenav: SidenavMenuService,
    public _jwtService: JwtService,
    private store: Store
  ) {
    this.store.dispatch([
      new AdminExportLoadMenu('ANALYZE'),
      new AdminExportLoadMenu('OBSERVE')
    ]);
  }

  ngOnInit() {
    this._sidenav.updateMenu(AdminMenuData, 'ADMIN');
  }

  ngOnDestroy() {
    this.store.dispatch(new ResetExportPageState());
  }

  /**
   * Handler for changes in left pane - Selection of category/sub-category
   *
   * @param {*} { moduleName, menuItem }
   * @memberof AdminExportViewComponent
   */
  onSelectMenuItem({ moduleName, menuItem }) {
    this.store.dispatch(new ExportSelectTreeItem(moduleName, menuItem));
  }

  /**
   * When the item in middle pane is toggled, update store with it.
   *
   * @param {*} { checked, item }
   * @memberof AdminExportViewComponent
   */
  onChangeItemSelection({ checked, item }) {
    if (item.entityId) {
      // TODO: Handle dashboard
    } else {
      // Item is analysis
      this.store.dispatch(
        checked
          ? new AddAnalysisToExport(item)
          : new RemoveAnalysisFromExport(item)
      );
    }
  }

  export() {
    const zip = new JSZip();
    const { analyses } = this.store.selectSnapshot(
      state => state.admin.exportPage.exportData
    );

    const fileName = this.getFileName('ANALYZE');
    zip.file(
      `${fileName}.json`,
      new Blob([JSON.stringify(analyses)], {
        type: 'application/json;charset=utf-8'
      })
    );

    zip.generateAsync({ type: 'blob' }).then(content => {
      let zipFileName = this.getFileName('');
      zipFileName = zipFileName.replace('_', '');
      FileSaver.saveAs(content, `${zipFileName}.zip`);
    });
  }

  getFileName(name) {
    const formatedDate = moment().format('YYYYMMDDHHmmss');
    const custCode = get(this._jwtService.getTokenObj(), 'ticket.custCode');
    name = name.replace(' ', '_');
    name = name.replace('\\', '-');
    name = name.replace('/', '-');
    return `${custCode}_${name}_${formatedDate}`;
  }
}
