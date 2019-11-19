import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { JsPlumbConnectorComponent } from './js-plumb-connector.component';
import { Injector, ComponentFactoryResolver } from '@angular/core';

describe('JS Plumb Connector', () => {
  let fixture: ComponentFixture<JsPlumbConnectorComponent>;
  let component: JsPlumbConnectorComponent;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [],
      declarations: [JsPlumbConnectorComponent],
      providers: [
        {
          provide: Injector,
          useValue: {}
        },
        {
          provide: ComponentFactoryResolver,
          useValue: {}
        }
      ]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JsPlumbConnectorComponent);
    component = fixture.componentInstance;
    component.plumbInstance = {
      getConnections: () => []
    };
    component.join = {
      type: 'inner',
      criteria: [
        { columnName: '1', tableName: 'a', side: 'right' },
        { columnName: '2', tableName: 'b', side: 'left' }
      ]
    };

    fixture.detectChanges();
  });

  it('should initialise', () => {
    expect(component).toBeTruthy();
  });
});
