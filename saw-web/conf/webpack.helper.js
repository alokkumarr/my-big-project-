const path = require('path');

/* Return true if end-to-end tests are run against distribution
 * package built with Maven and deployed to a local Docker container
 * (as happens for example on the Bamboo continuous integration
 * server), as opposed to a local saw-web front-end development
 * server */
function distRun() {
  return process.env.PWD.endsWith('/dist');
}
/**
 * This function reads the property of the suite and runs those set of tests
 */
function getSuiteName() {

  if (browser.params.saw.e2e.suite !== null && browser.params.saw.e2e.suite.trim().length > 0) {
    //agressive check for unwanted values
    if (browser.params.saw.e2e.suite.trim().toLocaleLowerCase() === 'sanity') {
      return 'sanity';
    } else if (browser.params.saw.e2e.suite.trim().toLocaleLowerCase() === 'regression') {
      return 'regression';
    } else {
      // if some junk values are provided then run smoke suite
      return 'smoke';
    }
  } else {
    /** 
     by default value is set to smoke to eventually above condition will never false.
     Above condition will be false only in case of running e2e tests from saw-web folder
     Hence it means that user is either developing something or want to run all for some local test 
    */
    return 'development';
  }
}

module.exports = {
  root: (...args) => {
    return path.join(process.cwd(), ...args);
  },
  sortChunks: (chunks) => {
    return (a, b) => {
      const c = chunks.indexOf(a.names[0]);
      const d = chunks.indexOf(b.names[0]);

      return (c > d) ? 1 : (c < d) ? -1 : 0;
    };
  },
  distRun: distRun,
  getSuiteName: getSuiteName,
  sawWebUrl: () => {
    if (distRun()) {
      var host = browser.params.saw.docker.host;
      var port = browser.params.saw.docker.port;
      return 'http://' + host + ':' + port + '/saw/web/';
    }
    //return 'http://localhost:3000/';
    return 'http://localhost/web/';
  }
};
