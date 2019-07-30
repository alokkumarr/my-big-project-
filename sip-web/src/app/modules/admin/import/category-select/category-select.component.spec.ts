import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { AdminImportCategorySelectComponent } from './category-select.component';

describe('CategorySelectComponent', () => {
  let component: AdminImportCategorySelectComponent;
  let fixture: ComponentFixture<AdminImportCategorySelectComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AdminImportCategorySelectComponent],
      schemas: [CUSTOM_ELEMENTS_SCHEMA]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminImportCategorySelectComponent);
    component = fixture.componentInstance;
    component.change = { emit: () => {} } as any;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit change on category change', () => {
    const spy = spyOn(component.change, 'emit');
    component.onCategoryChange('');
    expect(spy).toHaveBeenCalled();
  });
});
