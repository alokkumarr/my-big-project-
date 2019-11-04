import { TestBed, ComponentFixture, async } from '@angular/core/testing';
import { JobFiltersComponent } from './job-filters.component';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { Router } from '@angular/router';
import { NgxsModule, Store } from '@ngxs/store';
import { WorkbenchState } from '../../../state/workbench.state';
import { DatasourceService } from '../../../services/datasource.service';
import { of } from 'rxjs';

const datasourceServiceStub = {
  getChannelListForJobs: () => of([])
};

describe('JobFiltersComponent', () => {
  let component: JobFiltersComponent;
  let fixture: ComponentFixture<JobFiltersComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: Router, useValue: {} },
        { provide: DatasourceService, useValue: datasourceServiceStub }
      ],
      imports: [
        NgxsModule.forRoot([WorkbenchState], { developmentMode: false })
      ],
      declarations: [JobFiltersComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JobFiltersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeDefined();
  });

  it('should load routes on channel change', () => {
    const store = TestBed.get(Store);
    const spy = spyOn(store, 'dispatch').and.returnValue({});
    component.onChannelSelected(1);
    expect(spy).toHaveBeenCalledTimes(1);
  });
});
