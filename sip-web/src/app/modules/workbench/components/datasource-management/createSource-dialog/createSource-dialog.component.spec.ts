import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateSourceDialogComponent } from './createSource-dialog.component';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';
import { E2eDirective } from 'src/app/common/directives';
import { DatasourceService } from 'src/app/modules/workbench/services/datasource.service';
import { CHANNEL_OPERATION } from 'src/app/modules/workbench/models/workbench.interface';
import { MAT_DIALOG_DATA, MatSnackBar, MatDialogRef } from '@angular/material';
import { SftpSourceComponent } from './sftp-source/sftp-source.component';
import { ApiSourceComponent } from './api-source/api-source.component';
import { HttpMetadataComponent } from './http-metadata/http-metadata.component';

describe('CreateSourceDialogComponent', () => {
  let component: CreateSourceDialogComponent;
  let fixture: ComponentFixture<CreateSourceDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        CreateSourceDialogComponent,
        E2eDirective,
        SftpSourceComponent,
        ApiSourceComponent,
        HttpMetadataComponent
      ],
      imports: [FormsModule, MaterialModule, NoopAnimationsModule],
      providers: [
        { provide: DatasourceService, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} },
        { provide: MatSnackBar, useValue: {} },
        { provide: MatDialogRef, useValue: {} }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateSourceDialogComponent);
    component = fixture.componentInstance;
    component.channelData = {};
    component.opType = CHANNEL_OPERATION.CREATE;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
