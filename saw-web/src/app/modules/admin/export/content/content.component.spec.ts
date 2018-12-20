import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { DevExtremeModule } from 'devextreme-angular';

import { AdminExportContentComponent } from './content.component';
import { DxDataGridService } from '../../../../common/services';

describe('ContentComponent', () => {
  let component: AdminExportContentComponent;
  let fixture: ComponentFixture<AdminExportContentComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [DevExtremeModule],
      declarations: [AdminExportContentComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      providers: [DxDataGridService]
    }).compileComponents();
  }));

  beforeEach(() => {
    const gridService = TestBed.get(DxDataGridService);
    spyOn(gridService, 'mergeWithDefaultConfig').and.returnValue({});

    fixture = TestBed.createComponent(AdminExportContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
