import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import 'hammerjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from '../../../../material.module';
import { DxTemplateModule } from 'devextreme-angular/core/template';
import { DxDataGridModule } from 'devextreme-angular/ui/data-grid';
import { Router } from '@angular/router';
import { DatasourceService } from '../../services/datasource.service';
import { DatasourceComponent } from './datasource-page.component';
import {
  ToastService,
  HeaderProgressService
} from '../../../../common/services';
import { Observable } from 'rxjs';

const DatasourceServiceStub = {
  getSourceList: () => {
    return new Observable();
  },
  testChannel: () => {}
};
const ToastServiceStub: Partial<ToastService> = {};

class RouterServiceStub {}
class HeaderProgressServiceStub {
  subscribe() {}
}

describe('DatasourcePageComponent', () => {
  let component: DatasourceComponent;
  let fixture: ComponentFixture<DatasourceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NoopAnimationsModule,
        MaterialModule,
        HttpClientTestingModule,
        DxTemplateModule,
        DxDataGridModule
      ],
      declarations: [DatasourceComponent],
      providers: [
        {
          provide: HeaderProgressService,
          useValue: new HeaderProgressServiceStub()
        },
        { provide: DatasourceService, useValue: DatasourceServiceStub },
        { provide: ToastService, useValue: ToastServiceStub },
        { provide: Router, useClass: RouterServiceStub },
        { provide: HeaderProgressService, useClass: HeaderProgressServiceStub }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DatasourceComponent);
    component = fixture.componentInstance;
    component.unFilteredSourceData = [
      { bisChannelSysId: 1, channelName: 'abc' }
    ];
    component.show = true;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('togglePWD', () => {
    it('should toggle password show status', () => {
      component.togglePWD();
      expect(component.show).toBeFalsy();

      component.togglePWD();
      expect(component.show).toBeTruthy();
    });
  });

  describe('select channel', () => {
    it('should select channel based on id', () => {
      component.selectChannel({ bisChannelSysId: 1 });
      expect(component.selectedSourceData.channelName).toEqual('abc');
    });
  });

  describe('test connectivity', () => {
    it('should call service for channel', () => {
      const spy = spyOn(
        TestBed.get(DatasourceService),
        'testChannel'
      ).and.returnValue({ subscribe: () => {} });
      component.testChannel(1);
      expect(spy).toHaveBeenCalled();
    });
  });
});
