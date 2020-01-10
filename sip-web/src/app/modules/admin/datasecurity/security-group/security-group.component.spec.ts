import { Component } from '@angular/core';
import { async, TestBed, ComponentFixture } from '@angular/core/testing';
import 'hammerjs';
import { MaterialModule } from '../../../../material.module';
import { SecurityGroupComponent } from './security-group.component';
import { DataSecurityService } from '../datasecurity.service';
import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { JwtService } from '../../../../common/services/jwt.service';
import { ErrorDetailDialogService } from '../../../../common/services/error-detail-dialog.service';
import { LocalSearchService } from '../../../../common/services/local-search.service';
import { Router, NavigationEnd } from '@angular/router';
import { ToastService } from '../../../../common/services/toastMessage.service';
import { DxDataGridModule } from 'devextreme-angular/ui/data-grid';
import { DxTemplateModule } from 'devextreme-angular/core/template';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { Observable, of } from 'rxjs';

/* Stubs */
const DataSecurityServiceStub = {
  getSecurityGroups: () => {
    return new Promise(res => res({ data: {} }));
  }
};

const JWTServiceStub = {
  getTokenObj: () => {
    return new Promise(res => res({ data: {} }));
  }
};

const mockService = { open: () => {} };

class MockRouter {
  public navigate = new NavigationEnd(
    0,
    'http://localhost:9876/',
    'http://localhost:9876/'
  );
  public events = new Observable(observer => {
    observer.next(this.navigate);
    observer.complete();
  });
}
const ticketStub = { custID: '', custCode: '', masterLoginId: '' };

@Component({
  selector: 'security-group',
  template: '<h1> security-group </h1>'
})
class SecurityGroupStubComponent {}

describe('security-group component', () => {
  let fixture: ComponentFixture<SecurityGroupComponent>;
  let component: SecurityGroupComponent;
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [MaterialModule, DxDataGridModule, DxTemplateModule],
      declarations: [SecurityGroupComponent, SecurityGroupStubComponent],
      providers: [
        DxDataGridService,
        { provide: Router, useClass: MockRouter },
        { provide: DataSecurityService, useValue: DataSecurityServiceStub },
        { provide: JwtService, useValue: JWTServiceStub },
        { provide: MatDialog, useValue: mockService },
        { provide: MatDialogConfig, useValue: mockService },
        LocalSearchService,
        ToastService,
        ErrorDetailDialogService
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
    })
      .compileComponents()
      .then(() => {
        fixture = TestBed.createComponent(SecurityGroupComponent);
        component = fixture.componentInstance;
        component.ticket = ticketStub;
        fixture.detectChanges();
      });
  }));

  it('should exist', () => {
    expect(fixture.componentInstance).not.toBeNull();
  });

  it('should open dsk dialog', () => {
    const spy = spyOn(TestBed.get(MatDialog), 'open').and.returnValue({
      afterClosed: () => of([])
    });
    component.updateDskFilters();
    expect(spy).toHaveBeenCalled();
  });
});
