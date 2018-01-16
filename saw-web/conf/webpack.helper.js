const path = require('path');

/* Return true if end-to-end tests are run against distribution
 * package built with Maven and deployed to a local Docker container
 * (as happens for example on the Bamboo continuous integration
 * server), as opposed to a local saw-web front-end development
 * server */
function distRun() {
  return process.env.PWD.endsWith('/saw-dist');
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
  sawWebUrl: () => {
    if (distRun()) {
      var port = browser.params.saw.docker.port;
      return 'http://localhost:' + port + '/web/';
    }
    return 'http://localhost:3000/';
  }
};
