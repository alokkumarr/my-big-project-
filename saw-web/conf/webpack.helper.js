const path = require('path');
var appRoot = require('app-root-path');
var fs = require('fs');

/* Return true if end-to-end tests are run against distribution
 * package built with Maven and deployed to a local Docker container
 * (as happens for example on the Bamboo continuous integration
 * server), as opposed to a local saw-web front-end development
 * server */
function distRun() {
  return process.env.PWD.endsWith('/dist');
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
  getTestData: () => {
    //check if it is retry or first run
    // if (browser.suite) {
    //   console.log('I am coming from suite....')
    // }else {
    //   console.log('I am coming from retry....')
    // }
    if (fs.existsSync(appRoot+'/src/test/e2e-tests/testdata/data_failed.json')) {
      console.log('executing Failed--tests');
      let data = fs.readFileSync(appRoot+'/src/test/e2e-tests/testdata/data.json','utf8');
      console.log(JSON.stringify(JSON.parse(data)))
      return JSON.parse(data);
    }else {
      console.log('executing fresh--tests');
      let data = fs.readFileSync(appRoot+'/src/test/e2e-tests/testdata/data.json','utf8');
      console.log(JSON.stringify(JSON.parse(data)))
      return JSON.parse(data);
    }

  },
  distRun: distRun,
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
