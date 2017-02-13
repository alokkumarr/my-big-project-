const fs = require('fs');
const path = require('path');
const express = require('express');
const app = express();
const dist = path.join(process.cwd(), '/dist');

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

let logged = false;

app.get('/', (req, res) => {
  if (!logged) {
    res.redirect('/login');
  } else {
    res.render('index');
  }
});

app.get('/login', (req, res) => {
  if (logged) {
    res.redirect('/');
  } else {
    res.render('login');
  }
});

app.get('/doLogin', (req, res) => {
  logged = true;
  res.json({logged});
});

app.get('/doLogout', (req, res) => {
  logged = false;
  res.json({logged});
});

app.get('/changePwd', (req, res) => {
  res.render('login');
});

app.get('/preResetPwd', (req, res) => {
  res.render('login');
});

app.get('/resetPassword', (req, res) => {
  res.render('login');
});

app.use(express.static(dist));

app.listen(3000);
