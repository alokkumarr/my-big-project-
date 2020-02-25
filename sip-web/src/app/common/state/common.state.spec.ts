import { TestBed, async } from '@angular/core/testing';
import { NgxsModule, Store } from '@ngxs/store';
import { CommonState } from './common.state';
import { MenuService } from '../../common/services/menu.service';
import { CommonSemanticService } from '../../common/services/semantic.service';

import { CommonResetStateOnLogout } from '../../common/actions/common.actions';

class MenuServiceStub {}

class SemanticServiceStub {}

const initialState = {
  analyzeMenu: null,
  observeMenu: null,
  adminMenu: null,
  metrics: {},
  jobs: null
};

describe('Common actions', () => {
  let store: Store;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [NgxsModule.forRoot([CommonState])],
      providers: [
        { provide: MenuService, useClass: MenuServiceStub },
        { provide: CommonSemanticService, useClass: SemanticServiceStub }
      ]
    }).compileComponents();
    store = TestBed.get(Store);
  }));

  it('should reset the common state. ', () => {
    store.dispatch(new CommonResetStateOnLogout());
    store
      .selectOnce(state => state.common)
      .subscribe(result => {
        expect(result).toEqual(initialState);
      });
  });
});
