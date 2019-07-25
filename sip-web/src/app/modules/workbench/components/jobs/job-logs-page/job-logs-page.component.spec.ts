import { TestBed, ComponentFixture, async } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxsModule } from '@ngxs/store';
import { Location } from '@angular/common';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { DxDataGridModule } from 'devextreme-angular/ui/data-grid';
import {
  DxTextBoxModule,
  DxButtonModule,
  DxSliderModule,
  DxTooltipModule
} from 'devextreme-angular';
import { RouterModule } from '@angular/router';
import { DxTemplateModule } from 'devextreme-angular/core/template';
import { MaterialModule } from '../../../../../material.module';
import { DatasourceService } from '../../../services/datasource.service';
import { WorkbenchState } from '../../../state/workbench.state';
import {
  JwtService,
  MenuService,
  CommonSemanticService
} from '../../../../../common/services';
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
class DatasourceServiceStub {
  getJobLogs = () => of({ bisFileLogs: [], totalRows: 1 });
  getJobById = () => of({}).toPromise();
}

describe('Job logs page', () => {
  let fixture: ComponentFixture<JobLogsPageComponent>;
  beforeEach(async(() => {
    return TestBed.configureTestingModule({
      imports: [
        NoopAnimationsModule,
        NgxsModule.forRoot([WorkbenchState], { developmentMode: true }),
        MaterialModule,
        DxDataGridModule,
        DxTextBoxModule,
        DxButtonModule,
        DxSliderModule,
        DxTooltipModule,
        DxTemplateModule,
        RouterModule
      ],
      declarations: [JobLogsPageComponent],
      providers: [
        { provide: Router, useClass: RouterServiceStub },
        { provide: ActivatedRoute, useClass: ActivatedRouteServiceStub },
        { provide: JwtService, useClass: JwtServiceStub },
        { provide: MenuService, useClass: MenuServiceStub },
        { provide: CommonSemanticService, useClass: CommonSemanticServiceStub },
        { provide: DatasourceService, useClass: DatasourceServiceStub },
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
