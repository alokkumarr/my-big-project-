import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Store } from '@ngxs/store';
import { TestBed, ComponentFixture, async } from '@angular/core/testing';
import { of } from 'rxjs';

import { DesignerPreviewDialogComponent } from './designer-preview-dialog.component';
import { CheckedArtifactColumnFilterPipe } from 'src/app/common/pipes/filterArtifactColumns.pipe';
import { IsAnalysisTypePipe } from 'src/app/common/pipes/is-analysis-type.pipe';
import { DesignerService } from '../designer.module';

const dialogData = { analysis: {} };
const storeStub = {
  select: () => of({})
};

describe('Designer Preview Component', () => {
  let fixture: ComponentFixture<DesignerPreviewDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [
        DesignerPreviewDialogComponent,
        CheckedArtifactColumnFilterPipe,
        IsAnalysisTypePipe
      ],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: dialogData },
        { provide: DesignerService, useValue: {} },
        { provide: Store, useValue: storeStub }
      ]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(DesignerPreviewDialogComponent);

        fixture.detectChanges();
      });
  }));

  it('should exist', () => {
    expect(fixture.componentInstance).not.toBeNull;
  });
});
