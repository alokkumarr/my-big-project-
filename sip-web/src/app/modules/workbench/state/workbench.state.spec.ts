import { async, TestBed } from '@angular/core/testing';
import { Store, NgxsModule } from '@ngxs/store';
import { tap } from 'rxjs/operators';
import { CHANNEL_TYPES } from '../wb-comp-configs';

import { WorkbenchState } from './workbench.state';
import { DatasourceService } from '../services/datasource.service';
import {
  SelectChannelType,
  SelectChannel,
  SelectRoute
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
      .selectOnce(state => state.workbench.selectedChannelType)
      .pipe(
        tap(selectedChannelType => {
          expect(selectedChannelType).toEqual(CHANNEL_TYPES[0]);
        })
      )
      .subscribe();
  }));

  it('should set selectedChannelType', async(() => {
    store.dispatch(new SelectChannelType(CHANNEL_TYPES[1]));
    store
      .selectOnce(state => state.workbench.selectedChannelType)
      .pipe(
        tap(selectedChannelType => {
          expect(selectedChannelType).toEqual(CHANNEL_TYPES[1]);
        })
      )
      .subscribe();
  }));

  it('should set selectedChannel', async(() => {
    store.dispatch(new SelectChannel(channelToSelect));
    store
      .selectOnce(state => state.workbench.selectedChannel)
      .pipe(
        tap(selectedChannel => {
          expect(selectedChannel).toEqual(channelToSelect);
        })
      )
      .subscribe();
  }));

  it('should set selectedRoute', async(() => {
    store.dispatch(new SelectRoute(routeToSelect));
    store
      .selectOnce(state => state.workbench.selectedRoute)
      .pipe(
        tap(selectedRoute => {
          expect(selectedRoute).toEqual(routeToSelect);
        })
      )
      .subscribe();
  }));
});
