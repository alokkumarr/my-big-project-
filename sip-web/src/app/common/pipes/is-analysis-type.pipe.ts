import { Pipe, PipeTransform } from '@angular/core';
import * as startsWith from 'lodash/startsWith';
import { AnalysisTypeFE } from '../types';

/**
 * A pipe to check the type of analysis
 * {{someText | analysisType:analysisValue}}
 *
 * @export
 * @class HighlightPipe
 * @implements {PipeTransform}
 */
@Pipe({
  name: 'isAnalysisType'
})
export class IsAnalysisTypePipe implements PipeTransform {

  transform(type: string, analysisType: AnalysisTypeFE, subType?: string): boolean {

    switch (analysisType) {
      case 'mapChart':
        if (subType) {
          const isMapChart = startsWith(subType, 'chart_');
          return type === 'map' && isMapChart;
        }
        return false;
      case 'map':
        const isNotMapChart = !startsWith(subType, 'chart_');
        return type === 'map' && isNotMapChart;
      default:
        return type === analysisType;
    }
  }
}
