export default class User {
 constructor(JWT,AppConstants, $http,$state,$q) {
    'ngInject';
    this._JWT = JWT;
	this._AppConstants = AppConstants;
    this._$http = $http;
     // Object to store our user properties
     this.current = null;
     this._$state = $state;
     this._$q = $q;

  }

// Try to authenticate by registering or logging in
  attemptAuth(type, credentials) {
    let route = (type === 'login') ? '/doAuthenticate' : '';
    let LoginDetails = {
        			masterLoginId: credentials.masterLoginId,
        			password: credentials.password        			
        	}
    return this._$http({
      url: this._AppConstants.api + route,
      method: 'POST',
      data: LoginDetails
      
    }).then(
      // On success...
	
      (res) => {
      	 var base64Url = res.data.token.split('.')[1];
         var base64 = base64Url.replace('-', '+').replace('_', '/');
        let resp = JSON.parse(window.atob(base64));
        // Store the user's info for easy lookup
        this._JWT.save(resp.ticket);
        this.current = resp.ticket;

        return resp;
      }
      
    );
  }

  logout() {
 let route = '/doLogout';
 let ticketID = this.current.ticketId;
    this._$http({
      url: this._AppConstants.api + route,
      method: 'POST',
      data: ticketID
      
    }).then(
      // On success...     
      
       (res) => {
        this.current = null;
       }
       
     
      
    );
    
  }

   verifyAuth() {
    let deferred = this._$q.defer();

    // Check for JWT token first
    if (!this._JWT.get()) {
      deferred.resolve(false);
      return deferred.promise;
    }

    // If there's a JWT & user is already set
    if (this.current) {
      deferred.resolve(true);

    // If current user isn't set, get it from the server.
    // If server doesn't 401, set current user & resolve promise.
    } else {
      this._$http({
        url: this._AppConstants.api + '/user',
        method: 'GET',
        headers: {
          Authorization: 'Token ' + this._JWT.get()
        }
      }).then(
        (res) => {
          this.current = res.data.user;
          deferred.resolve(true);
        },
        // If an error happens, that means the user's token was invalid.
        (err) => {
          this._JWT.destroy();
          deferred.resolve(false);
        }
        // Reject automatically handled by auth interceptor
        // Will boot them to homepage
      );
    }

    return deferred.promise;
  }

}