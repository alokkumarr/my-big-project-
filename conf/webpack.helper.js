const path = require('path');

module.exports = {
  root: (...args) => {
    return path.join(process.cwd(), ...args);
  }
};
