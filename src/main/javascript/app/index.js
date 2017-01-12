import angular from 'angular';
import 'angular-ui-router';

import 'angular-material';
import 'angular-material/angular-material.css';

import 'mottle';

import 'devextreme/ui/data_grid';
import 'devextreme/integration/angular';
import 'devextreme/dist/css/dx.common.css';
import 'devextreme/dist/css/dx.light.css';

import '../../../../fonts/style.css';

import AppConfig from '../../../../appConfig';

import {routesConfig} from './routes';
import {themeConfig} from './theme';
import {runConfig} from './run';

import {LibModule} from './lib';
import {ObserveModule} from './modules/observe';
import {AnalyzeModule} from './modules/analyze';
import {AlertsModule} from './modules/alerts';

import {HeaderComponent, RootComponent, FooterComponent} from './layout';

// import from login module
import {AuthServiceFactory} from '../login/services/auth.service';
import {UserServiceFactory} from '../login/services/user.service';
import {JwtServiceFactory} from '../login/services/jwt.service';

export const AppModule = 'app';

angular
  .module(AppModule, [
    'ui.router',
    'ngMaterial',
    'dx',
    LibModule,
    ObserveModule,
    AnalyzeModule,
    AlertsModule
  ])
  .config(routesConfig)
  .config(themeConfig)
  .run(runConfig)
  .value('AppConfig', AppConfig)
  .factory('AuthService', AuthServiceFactory)
  .factory('UserService', UserServiceFactory)
  .factory('JwtService', JwtServiceFactory)
  .component('root', RootComponent)
  .component('headerComponent', HeaderComponent)
  .component('footerComponent', FooterComponent).run(function($rootScope, $state, JwtService, $location, $window) {
		 var destroy = $rootScope.$on('$locationChangeSuccess', function(event, toState, toParams, fromState, fromParams) {
			 var restrictedPage = ['/','/login','/observe','/analyse','/alerts'];         
			 if((restrictedPage.indexOf($location.path()) !== -1)  && JwtService.get() != null) {
	     		  //todo                 	
	         } else if((restrictedPage.indexOf($location.path()) !== -1) && $location.path() != "/login") {
	        	 event.preventDefault();
	        	 const baseUrl = $window.location.origin;
	             const appUrl = `${baseUrl}/login.html`;
	             $window.location = appUrl; 
	         }
	     });     		
  });