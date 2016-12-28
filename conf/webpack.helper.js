const path = require('path');

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
  }
};
