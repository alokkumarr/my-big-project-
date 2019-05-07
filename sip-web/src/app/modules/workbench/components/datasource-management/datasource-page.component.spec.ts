import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import 'hammerjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from '../../../../material.module';
import { DxTemplateModule } from 'devextreme-angular/core/template';
import { DxDataGridModule } from 'devextreme-angular/ui/data-grid';
import { DatasourceService } from '../../services/datasource.service';
import { DatasourceComponent } from './datasource-page.component';
import { ToastService } from '../../../../common/services/toastMessage.service';
import { Observable } from 'rxjs';

const DatasourceServiceStub = {
  getSourceList: () => {
    return new Observable();
  }
};
const ToastServiceStub: Partial<ToastService> = {};

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
        { provide: DatasourceService, useValue: DatasourceServiceStub },
        { provide: ToastService, useValue: ToastServiceStub }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DatasourceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
