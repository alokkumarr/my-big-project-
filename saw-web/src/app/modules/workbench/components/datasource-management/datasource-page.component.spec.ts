import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DatasourceComponent } from './datasource-page.component';

describe('DatasourcePageComponent', () => {
  let component: DatasourceComponent;
  let fixture: ComponentFixture<DatasourceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [DatasourceComponent]
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
