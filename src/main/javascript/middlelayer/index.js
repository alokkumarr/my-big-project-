const express = require('express');
const context = require('./context');

context.app = express();

[
  require('./setup/views'),
  require('./setup/routes'),
  require('./setup/server')
].forEach(item => {
  item(context);
});
