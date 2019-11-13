import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToolbarActionDialogComponent } from './toolbar-action-dialog.component';
import { MaterialModule } from 'src/app/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatDialog, MatDialogConfig, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { DesignerService } from '../designer.service';
import { HeaderProgressService } from '../../../../common/services';
import { Subject, timer } from 'rxjs';
import { distinctUntilChanged, debounce } from 'rxjs/operators';
import { IToolbarActionData } from '../types';

const mockService = {};

const dataStub: IToolbarActionData = {
  action: 'save',
  analysis: {
    artifacts: [{
      artifactName: 'sample',
      columns: [],
      artifactPosition: [5]
    }],
    categoryId: 5,
    chartTitle: 'sample',
    checked: true,
    createdTimestamp: 1571920398528,
    disabled: false,
    isScheduled: 'true',
    metric: 'MCT TGT Session ES',
    saved: true,
    schedule: null,
    scheduled: null,
    userFullName: 'sawadmin@synchronoss.com',
    metricId: 'MCT TGT Session ES',
    metrics: ['MCT TGT Session ES'],
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
    sqlBuilder: {},
    supports: undefined,
    type: 'pivot',
    userId: 1
  }
};

class HeaderProgressStubService {
  subscribe = (fn) => {
    const _subject$ = new Subject<boolean>();
    return _subject$
      .pipe(
        distinctUntilChanged(),
        debounce(() => timer(100))
      )
      .subscribe(fn);
  }
}

describe('Designer Chart Component', () => {
  let fixture: ComponentFixture<ToolbarActionDialogComponent>;
  let component: ToolbarActionDialogComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: MatDialog, useValue: mockService },
        { provide: MatDialogRef, useValue: mockService },
        { provide: MAT_DIALOG_DATA, useValue: mockService },
        { provide: MatDialogConfig, useValue: mockService },
        { provide: DesignerService, useValue: mockService },
        { provide: HeaderProgressService, useClass: HeaderProgressStubService }],
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

  it('validation for analysis name should fail if name has a * special character', () => {
    const checkAnalysisNameChar = fixture.componentInstance.validateSaving('ABC*');
    // should fail for usage of special characters
    // disabled state is set to true.
    expect(checkAnalysisNameChar).toBeTruthy();
  });

  it('should fail for length validation', () => {
    const checkAnalysisNameChar = fixture.componentInstance.validateSaving(
      'QWERTYUIOPSDFGHJKLZXCVBNM234567890QWERTYUIOPASDFGHJKL'
    );
    // should fail for length validation
    // disabled state is set to true.
    expect(checkAnalysisNameChar).toBeTruthy();
  });

  it('should accept a correct analysis name', () => {
    const checkAnalysisNameChar = fixture.componentInstance.validateSaving(
      'Untitled Analysis'
    );
    // should accept a correct form of analysis name
    // disabled state is set to false.
    expect(checkAnalysisNameChar).toBeFalsy();
  });
});
