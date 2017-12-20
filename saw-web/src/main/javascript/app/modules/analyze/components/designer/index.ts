import { DesignerDialogComponent } from './dialog';
import { DesignerContainerComponent } from './container';
import { DesignerHeaderComponent } from './header';
import { DesignerToolbarComponent } from './toolbar';
import {
  DesignerPivotComponent,
  ArtifactColumns2PivotFieldsPipe
} from './pivot';
// import {
//   DesignerSettingsComponent,
//   DesignerSettingsGroupComponent
// } from './settings';
import {
  OldDesignerSettingsComponent,
  PivotAreaFilterPipe
} from './old-settings';
import { DesignerService } from './designer.service';
import { ArtifactColumnFilterPipe } from './artifact-column-filter.pipe';

export {
  DesignerDialogComponent,
  DesignerContainerComponent,
  DesignerHeaderComponent,
  DesignerToolbarComponent,
  DesignerPivotComponent,
  // DesignerSettingsComponent,
  // DesignerSettingsGroupComponent,
  OldDesignerSettingsComponent,
  DesignerService,
  ArtifactColumnFilterPipe,
  ArtifactColumns2PivotFieldsPipe,
  PivotAreaFilterPipe
};
