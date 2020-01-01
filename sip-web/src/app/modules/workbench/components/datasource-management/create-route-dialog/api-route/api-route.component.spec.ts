import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApiRouteComponent } from './api-route.component';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ROUTE_OPERATION } from 'src/app/modules/workbench/models/workbench.interface';
import { DatasourceService } from 'src/app/modules/workbench/services/datasource.service';
import { HttpMetadataComponent } from '../../createSource-dialog/http-metadata/http-metadata.component';
import { E2eDirective } from 'src/app/common/directives';

describe('ApiRouteComponent', () => {
  let component: ApiRouteComponent;
  let fixture: ComponentFixture<ApiRouteComponent>;
  let formBuilder: FormBuilder;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, MaterialModule, NoopAnimationsModule],
      providers: [{ provide: DatasourceService, useValue: {} }],
      declarations: [ApiRouteComponent, HttpMetadataComponent, E2eDirective]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApiRouteComponent);
    formBuilder = TestBed.get(FormBuilder);
    component = fixture.componentInstance;
    component.routeData = {
      routeMetadata: {
        headerParameters: [],
        queryParameters: []
      }
    };
    component.opType = ROUTE_OPERATION.CREATE;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return provisional headers inside normal headers', () => {
    const array = formBuilder.array([
      formBuilder.group({
        key: ['123'],
        value: ['abc']
      })
    ]);
    component.detailsFormGroup.setControl('provisionalHeaders', array);

    const value = component.value;
    expect(value.headerParameters.length).toEqual(1);
    expect(value['provisionalHeaders']).toBeUndefined();
  });
});
