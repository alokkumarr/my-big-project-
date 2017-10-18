const express = require('express');
const fs = require('fs');

module.exports = context => {
  const {app, dist} = context;

  app.set('view engine', 'html');
  app.set('views', dist);

  app.engine('html', (path, options, fn) => {
    fs.readFile(path, 'utf8', (err, data) => {
      if (err) {
        return fn(err);
      }

      return fn(null, data);
    });
  });

  app.use(express.static(dist));
};
