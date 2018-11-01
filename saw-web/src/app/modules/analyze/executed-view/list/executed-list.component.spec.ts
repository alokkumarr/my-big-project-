import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { async, TestBed, ComponentFixture } from '@angular/core/testing';
import { ExecutedListComponent } from './executed-list.component';
import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';

@Component({
  selector: 'dx-data-grid',
  template: '<h1>DataGrid</h1>'
})
class DxDataGridStubComponent {
  @Input() customizeColumns;
  @Input() columnAutoWidth;
  @Input() columnMinWidth;
  @Input() columnResizingMode;
  @Input() allowColumnReordering;
  @Input() allowColumnResizing;
  @Input() showColumnHeaders;
  @Input() showColumnLines;
  @Input() showRowLines;
  @Input() showBorders;
  @Input() rowAlternationEnabled;
  @Input() hoverStateEnabled;
  @Input() wordWrapEnabled;
  @Input() scrolling;
  @Input() sorting;
  @Input() dataSource;
  @Input() columns;
  @Input() pager;
  @Input() paging;
  @Output() onRowClick = new EventEmitter();
}

class RouterStub {}

describe('Executed list component', () => {
  let fixture: ComponentFixture<ExecutedListComponent>;
  beforeEach(
    async(() => {
      TestBed.configureTestingModule({
        providers: [
          DxDataGridService,
          { provide: Router, useValue: RouterStub }
        ],
        declarations: [ExecutedListComponent, DxDataGridStubComponent]
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(ExecutedListComponent);
    fixture.componentInstance.config = {};
    fixture.detectChanges();
  });

  it('should exist', () => {
    expect(fixture.componentInstance).not.toBeNull();
  });

  it('should get grid config from data service', () => {
    const service = fixture.componentInstance;
    const spy = spyOn(service, 'getGridConfig').and.returnValue({});
    fixture.componentInstance.setAnalyses = [];
    fixture.detectChanges();
    expect(spy).toHaveBeenCalled();
  });
});
