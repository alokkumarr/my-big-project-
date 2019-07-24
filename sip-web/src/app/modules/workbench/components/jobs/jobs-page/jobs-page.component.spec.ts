// import { Component } from '@angular/core';
import { TestBed, ComponentFixture, async } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxsModule } from '@ngxs/store';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Location } from '@angular/common';
import { of } from 'rxjs';
import { WorkbenchState } from '../../../state/workbench.state';
import {
  JwtService,
  MenuService,
  ToastService,
  CommonSemanticService
} from '../../../../../common/services';
import { WorkbenchModule } from '../../../workbench.module';
import { JobsPageComponent } from './jobs-page.component';

const queryParams = { channelTypeId: '' };

class RouterServiceStub {
  navigateByUrl = () => {};
}
class ActivatedRouteServiceStub {
  snapshot = { queryParams };
  queryParams = of(queryParams);
}
class JwtServiceStub {}
class MenuServiceStub {}
class CommonSemanticServiceStub {}
class LocationStub {}
class ToastServiceStub {}

describe('Jobs page', () => {
  let fixture: ComponentFixture<JobsPageComponent>;
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
        { provide: Location, useClass: LocationStub },
        { provide: ToastService, useClass: ToastServiceStub }
      ]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(JobsPageComponent);
        fixture.detectChanges();
      });
  }));

  it('should exist', async(() => {
    expect(fixture.componentInstance).not.toBeNull();
  }));
});
