import { Observable } from 'rxjs/Observable';

export interface IAdminDataService {
  getList: (customerId :string) => Observable<any[]>;
  save: (obj: Object) => Promise<any[]>;
  update: (obj: Object) => Promise<any[]>;
  remove: (obj: Object) => Promise<any[]>;
}
