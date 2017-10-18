const path = require('path');

const env = process.env.NODE_ENV || 'development';
const root = (...args) => {
  return path.join(process.cwd(), ...args);
};

const context = {
  root,
  rootDir: process.cwd(),
  dist: root('/dist'),
  env,
  startupTime: new Date()
};

module.exports = context;
