const fs = require('fs');
const path = require('path');
const express = require('express');
const app = express();
const dist = path.join(process.cwd(), '/dist');

app.set('view engine', 'html');
app.set('views', dist);

app.engine('html', function (path, options, fn) {
  fs.readFile(path, 'utf8', function (err, data) {
    if (err) {
      return fn(err);
    }

    return fn(null, data);
  });
});

app.use(express.static(dist));

app.get('/', (req, res) => {
  res.render('app');
});

app.get('/login', (req, res) => {
  res.render('login');
});

app.get('/changePwd', (req, res) => {
  res.render('login');
});

app.listen(3000);
