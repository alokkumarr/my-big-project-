export interface IAdminDataService {
  getList: (customerId: string) => Promise<any[]>;
  save: (obj: Object) => Promise<any[]>;
  update: (obj: Object) => Promise<any[]>;
  remove: (obj: Object) => Promise<any[]>;
}
