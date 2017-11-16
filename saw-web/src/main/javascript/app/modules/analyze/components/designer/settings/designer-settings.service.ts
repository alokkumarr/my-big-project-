import { Injectable } from '@angular/core';
import { ChartType } from '../../../types';
import {
  IDEsignerSettingGroupAdapter,
  ArtifactColumn
} from './types';
import {
  NUMBER_TYPES
} from '../../../consts';

@Injectable()
export default class DesignerSettingsService {

  public getPivotGroupAdapters(): IDEsignerSettingGroupAdapter[] {
    return [{
      title: 'Data',
      canAcceptArtifactColumn(artifactColumn: ArtifactColumn) {
        return NUMBER_TYPES.includes(artifactColumn.type);
      }
    }, {
      title: 'Row',
      canAcceptArtifactColumn(artifactColumn: ArtifactColumn) {
        return !NUMBER_TYPES.includes(artifactColumn.type);
      }
    }, {
      title: 'Column',
      canAcceptArtifactColumn(artifactColumn: ArtifactColumn) {
        return !NUMBER_TYPES.includes(artifactColumn.type);
      }
    }];
  }

  getChartGroupAdapters(chartType: ChartType) {

  }

  distributePivotArtifactColumnsIntoGroups() {

  }
}
