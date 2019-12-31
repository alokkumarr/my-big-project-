import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DskFilterGroupComponent } from './dsk-filter-group.component';

describe('DskFilterGroupComponent', () => {
  let component: DskFilterGroupComponent;
  let fixture: ComponentFixture<DskFilterGroupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DskFilterGroupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DskFilterGroupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
