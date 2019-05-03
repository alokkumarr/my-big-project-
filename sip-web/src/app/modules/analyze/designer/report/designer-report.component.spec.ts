import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { TestBed, ComponentFixture, async } from '@angular/core/testing';

import { DesignerReportComponent } from './designer-report.component';

import { Pipe, PipeTransform } from '@angular/core';
@Pipe({
  name: 'checkedArtifactColumnFilter'
})
export class ArtifactMockPipe implements PipeTransform {
  transform(artifacts: any): any {
    return artifacts;
  }
}

describe('Designer Report Component', () => {
  let fixture: ComponentFixture<DesignerReportComponent>;

  beforeEach(
    async(() => {
      TestBed.configureTestingModule({
        imports: [],
        schemas: [CUSTOM_ELEMENTS_SCHEMA],
        declarations: [DesignerReportComponent, ArtifactMockPipe],
        providers: []
      })
        .compileComponents()
        .then(() => {
          fixture = TestBed.createComponent(DesignerReportComponent);

          fixture.componentInstance.analysis = {};
          fixture.componentInstance.filters = [];
          fixture.detectChanges();
        });
    })
  );

  it('should exist', () => {
    expect(fixture.componentInstance).not.toBeNull;
  });

  it('should set count as minimum of current and total', () => {
    fixture.componentInstance.dataCount = 5;
    fixture.componentInstance._data = [1, 2, 3, 4, 5, 6];
    fixture.detectChanges();
    expect(fixture.componentInstance.currentDataCount).toBe(5);
  });
});
