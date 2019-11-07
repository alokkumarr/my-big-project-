import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateRouteDialogComponent } from './create-route-dialog.component';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';
import { E2eDirective } from 'src/app/common/directives';
import { DatasourceService } from 'src/app/modules/workbench/services/datasource.service';
import { MAT_DIALOG_DATA, MatSnackBar, MatDialogRef } from '@angular/material';
import { SftpRouteComponent } from './sftp-route/sftp-route.component';
import { ApiRouteComponent } from './api-route/api-route.component';
import { HttpMetadataComponent } from '../createSource-dialog/http-metadata/http-metadata.component';
import { ROUTE_OPERATION } from '../../../models/workbench.interface';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('CreateSourceDialogComponent', () => {
  let component: CreateRouteDialogComponent;
  let fixture: ComponentFixture<CreateRouteDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        CreateRouteDialogComponent,
        E2eDirective,
        SftpRouteComponent,
        ApiRouteComponent,
        HttpMetadataComponent
      ],
      imports: [ReactiveFormsModule, MaterialModule, NoopAnimationsModule],
      providers: [
        { provide: DatasourceService, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: {} },
        { provide: MatSnackBar, useValue: {} },
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: { routeMetadata: {} } }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateRouteDialogComponent);
    component = fixture.componentInstance;
    component.opType = ROUTE_OPERATION.CREATE;
    component.routeData = { routeMetadata: {} };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
