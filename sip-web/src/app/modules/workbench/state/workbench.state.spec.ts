import { async, TestBed } from '@angular/core/testing';
import { Store, NgxsModule } from '@ngxs/store';
import { tap } from 'rxjs/operators';
import { CHANNEL_TYPES } from '../wb-comp-configs';

import { WorkbenchState } from './workbench.state';
import { DatasourceService } from '../services/datasource.service';
import {
  SelectChannelTypeId,
  SelectChannelId,
  SelectRouteId
} from './workbench.actions';

const channelToSelect = { name: 'channel', id: 1 };
const routeToSelect = { name: 'route', id: 2 };

class DatasourceServiceStub {}

describe('Workbench State', () => {
  let store: Store;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        NgxsModule.forRoot([WorkbenchState], { developmentMode: true })
      ],
      providers: [
        { provide: DatasourceService, useClass: DatasourceServiceStub }
      ]
    }).compileComponents();

    store = TestBed.get(Store);
  }));

  it('should have sftp selectedChannelType as default', async(() => {
    store
      .selectOnce(WorkbenchState.selectedChannelTypeId)
      .pipe(
        tap(selectedChannelTypeId => {
          expect(selectedChannelTypeId).toEqual(CHANNEL_TYPES[0].uid);
        })
      )
      .subscribe();
  }));

  it('should set selectedChannelType', async(() => {
    store.dispatch(new SelectChannelTypeId(CHANNEL_TYPES[1].uid));
    store
      .selectOnce(WorkbenchState.selectedChannelTypeId)
      .pipe(
        tap(selectedChannelTypeId => {
          expect(selectedChannelTypeId).toEqual(CHANNEL_TYPES[1].uid);
        })
      )
      .subscribe();
  }));

  it('should set selectedChannel', async(() => {
    store.dispatch(new SelectChannelId(channelToSelect.id));
    store
      .selectOnce(WorkbenchState.selectedChannelId)
      .pipe(
        tap(selectedChannelId => {
          expect(selectedChannelId).toEqual(channelToSelect.id);
        })
      )
      .subscribe();
  }));

  it('should set selectedRoute', async(() => {
    store.dispatch(new SelectRouteId(routeToSelect.id));
    store
      .selectOnce(WorkbenchState.selectedRouteId)
      .pipe(
        tap(selectedRouteId => {
          expect(selectedRouteId).toEqual(routeToSelect.id);
        })
      )
      .subscribe();
  }));
});
