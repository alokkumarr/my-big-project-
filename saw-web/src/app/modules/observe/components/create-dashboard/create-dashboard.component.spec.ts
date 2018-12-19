import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { Location } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { CreateDashboardComponent } from './create-dashboard.component';
import { MatDialog } from '@angular/material';
import { MenuService } from '../../../../common/services';
import { GlobalFilterService } from '../../services/global-filter.service';
import { DashboardService } from '../../services/dashboard.service';
import { ObserveService } from '../../services/observe.service';

class MatDialogStub {}
class RouterStub {}
class LocationStub {}
class ActivatedRouteStub {}
class MenuServiceStub {}
class DashboardServiceStub {}
class ObserveServiceStub {}
class GlobalFilterServiceStub {}

describe('Create dashboard component', () => {
  let fixture: ComponentFixture<CreateDashboardComponent>;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CreateDashboardComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      providers: [
        { provide: MatDialog, useValue: new MatDialogStub() },
        { provide: Router, useValue: new RouterStub() },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub() },
        { provide: Location, useValue: new LocationStub() },
        { provide: MenuService, useValue: new MenuServiceStub() },
        { provide: DashboardService, useValue: new DashboardServiceStub() },
        { provide: ObserveService, useValue: new ObserveServiceStub() },
        {
          provide: GlobalFilterService,
          useValue: new GlobalFilterServiceStub()
        }
      ]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(CreateDashboardComponent);
      });
  }));

  it('should initialise properly', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });
});
