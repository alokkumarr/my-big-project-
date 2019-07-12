// import { Component } from '@angular/core';
import { TestBed, ComponentFixture, async } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxsModule } from '@ngxs/store';
import { Location } from '@angular/common';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { WorkbenchState } from '../../../state/workbench.state';
import {
  JwtService,
  MenuService,
  CommonSemanticService
} from '../../../../../common/services';
import { WorkbenchModule } from '../../../workbench.module';
import { JobLogsPageComponent } from './job-logs-page.component';

const params = { jobId: '' };

class RouterServiceStub {
  navigateByUrl = () => {};
}
class ActivatedRouteServiceStub {
  snapshot = { params };
  params = of(params);
}
class JwtServiceStub {}
class MenuServiceStub {}
class CommonSemanticServiceStub {}
class LocationStub {}

describe('Jobs page', () => {
  let fixture: ComponentFixture<JobLogsPageComponent>;
  beforeEach(async(() => {
    return TestBed.configureTestingModule({
      imports: [
        NoopAnimationsModule,
        NgxsModule.forRoot([WorkbenchState], { developmentMode: true }),
        WorkbenchModule
      ],
      providers: [
        { provide: Router, useClass: RouterServiceStub },
        { provide: ActivatedRoute, useClass: ActivatedRouteServiceStub },
        { provide: JwtService, useClass: JwtServiceStub },
        { provide: MenuService, useClass: MenuServiceStub },
        { provide: CommonSemanticService, useClass: CommonSemanticServiceStub },
        { provide: Location, useClass: LocationStub }
      ]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(JobLogsPageComponent);
        fixture.detectChanges();
      });
  }));

  it('should exist', async(() => {
    expect(fixture.componentInstance).not.toBeNull();
  }));
});
