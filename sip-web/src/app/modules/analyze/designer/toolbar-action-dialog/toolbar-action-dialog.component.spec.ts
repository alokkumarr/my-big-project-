import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToolbarActionDialogComponent } from './toolbar-action-dialog.component';
import { MaterialModule } from 'src/app/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {
  MatDialog,
  MatDialogConfig,
  MatDialogRef,
  MAT_DIALOG_DATA
} from '@angular/material';
import { DesignerService } from '../designer.service';
import { HeaderProgressService } from '../../../../common/services';
import { Subject, timer } from 'rxjs';
import { distinctUntilChanged, debounce } from 'rxjs/operators';
import { IToolbarActionData } from '../types';

const mockService = {};

const dataStub: IToolbarActionData = {
  action: 'save',
  analysis: {
    artifacts: [
      {
        artifactName: 'sample',
        columns: [],
        artifactPosition: [5]
      }
    ],
    category: 5,
    saved: true,
    schedule: null,
    scheduled: null,
    createdBy: 'sawadmin@synchronoss.com',
    customerCode: 'SYNCHRONOSS',
    description: '',
    id: '',
    metricName: 'MCT TGT Session ES',
    modifiedTime: '1571920398528',
    module: 'ANALYZE',
    name: 'Pivot Vrify Dispatch',
    parentAnalysisId: '57b39194-0c8c-4384-9e66-7cb1c9c8ae01',
    parentCategoryId: '4',
    semanticId: 'tf-es-201901160115',
    type: 'pivot',
    userId: 1
  } as any
};

class HeaderProgressStubService {
  subscribe = fn => {
    const _subject$ = new Subject<boolean>();
    return _subject$
      .pipe(
        distinctUntilChanged(),
        debounce(() => timer(100))
      )
      .subscribe(fn);
  };
}

describe('Designer Chart Component', () => {
  let fixture: ComponentFixture<ToolbarActionDialogComponent>;
  let component: ToolbarActionDialogComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: MatDialog, useValue: mockService },
        { provide: MatDialogRef, useValue: mockService },
        { provide: MAT_DIALOG_DATA, useValue: mockService },
        { provide: MatDialogConfig, useValue: mockService },
        { provide: DesignerService, useValue: mockService },
        { provide: HeaderProgressService, useClass: HeaderProgressStubService }
      ],
      imports: [MaterialModule, FormsModule, BrowserAnimationsModule],
      declarations: [ToolbarActionDialogComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToolbarActionDialogComponent);
    component = fixture.componentInstance;
    component.data = dataStub;
    fixture.detectChanges();
  });

  it('should exist', () => {
    expect(fixture.componentInstance).not.toBeNull;
  });
});
