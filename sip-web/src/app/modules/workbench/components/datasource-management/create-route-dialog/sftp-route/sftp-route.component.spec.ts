import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SftpRouteComponent } from './sftp-route.component';
import { FormsModule } from '@angular/forms';
import { MaterialModule } from 'src/app/material.module';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { DatasourceService } from 'src/app/modules/workbench/services/datasource.service';
import { E2eDirective } from 'src/app/common/directives';
import { ROUTE_OPERATION } from 'src/app/modules/workbench/models/workbench.interface';

describe('SftpRouteComponent', () => {
  let component: SftpRouteComponent;
  let fixture: ComponentFixture<SftpRouteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SftpRouteComponent, E2eDirective],
      imports: [FormsModule, MaterialModule, NoopAnimationsModule],
      providers: [{ provide: DatasourceService, useValue: {} }]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SftpRouteComponent);
    component = fixture.componentInstance;
    component.routeData = { routeMetadata: {} };
    component.opType = ROUTE_OPERATION.CREATE;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
