import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApiRouteComponent } from './api-route.component';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ROUTE_OPERATION } from 'src/app/modules/workbench/models/workbench.interface';
import { DatasourceService } from 'src/app/modules/workbench/services/datasource.service';
import { HttpMetadataComponent } from '../../createSource-dialog/http-metadata/http-metadata.component';
import { E2eDirective } from 'src/app/common/directives';

describe('ApiRouteComponent', () => {
  let component: ApiRouteComponent;
  let fixture: ComponentFixture<ApiRouteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [FormsModule, MaterialModule, NoopAnimationsModule],
      providers: [{ provide: DatasourceService, useValue: {} }],
      declarations: [ApiRouteComponent, HttpMetadataComponent, E2eDirective]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApiRouteComponent);
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
});
