import {PubSubService} from './pubSub.service';

export const ServicesModule = 'ServicesModule';

angular
  .module(ServicesModule, [])
  .factory('PubSubService', PubSubService);
