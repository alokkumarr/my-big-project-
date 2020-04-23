import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListSubscriberComponent } from './list-subscriber.component';

describe('ListSubscriberComponent', () => {
  let component: ListSubscriberComponent;
  let fixture: ComponentFixture<ListSubscriberComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListSubscriberComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListSubscriberComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
