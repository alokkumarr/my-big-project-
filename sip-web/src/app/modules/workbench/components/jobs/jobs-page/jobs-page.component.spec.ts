import { TestBed, ComponentFixture, async } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { NgxsModule } from '@ngxs/store';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { Location } from '@angular/common';
import { DxDataGridModule } from 'devextreme-angular/ui/data-grid';
import {
  DxTextBoxModule,
  DxButtonModule,
  DxSliderModule,
  DxTooltipModule
} from 'devextreme-angular';
import { DxTemplateModule } from 'devextreme-angular/core/template';
import { RouterModule } from '@angular/router';
import { JobFiltersComponent } from '../filters';
import { MaterialModule } from '../../../../../material.module';
import { of } from 'rxjs';
import { WorkbenchState } from '../../../state/workbench.state';
import { DatasourceService } from '../../../services/datasource.service';
import {
  JwtService,
  MenuService,
  ToastService,
  CommonSemanticService
} from '../../../../../common/services';
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
class DatasourceServiceStub {
  getJobs = () => of({ jobDetails: [], totalRows: 1 }).toPromise();
  getChannelListForJobs = () => of([]);
}

describe('Jobs page', () => {
  let fixture: ComponentFixture<JobsPageComponent>;
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
      declarations: [JobsPageComponent, JobFiltersComponent],
      providers: [
        { provide: Router, useClass: RouterServiceStub },
        { provide: ActivatedRoute, useClass: ActivatedRouteServiceStub },
        { provide: JwtService, useClass: JwtServiceStub },
        { provide: MenuService, useClass: MenuServiceStub },
        { provide: CommonSemanticService, useClass: CommonSemanticServiceStub },
        { provide: Location, useClass: LocationStub },
        { provide: DatasourceService, useClass: DatasourceServiceStub },
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
