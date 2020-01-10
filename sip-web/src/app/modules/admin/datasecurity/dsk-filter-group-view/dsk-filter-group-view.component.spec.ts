import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DskFilterGroupViewComponent } from './dsk-filter-group-view.component';

describe('DskFilterGroupViewComponent', () => {
  let component: DskFilterGroupViewComponent;
  let fixture: ComponentFixture<DskFilterGroupViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DskFilterGroupViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DskFilterGroupViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
