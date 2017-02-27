const os = require('os');
const fs = require('fs');
const path = require('path');
const PhantomRender = require('../lib/phantomRender');

module.exports = (context) => {
  const {app} = context;

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

  /*
   /render/dx_grid
   /render/chart
   */
  app.get('/render/:name', (req, res) => {
    fs.readFile(`${__dirname}/../render/${req.params.name}.html`, 'utf8', (err, data) => {
      if (err) {
        return res.status(404).send();
      }

      res.status(200).send(data);
    });
  });

  /*
   /generate/dx_grid
   /generate/chart
   */
  app.get('/generate/:name', (req, res, next) => {
    const phantomRender = new PhantomRender();
    const url = `http://localhost:3000/render/${req.params.name}`;

    phantomRender.open(url, (err, result) => {
      if (err) {
        return next(err);
      }

      const tmpFile = `sync_${Date.now()}.pdf`;
      const tmpPath = path.join(os.tmpdir(), tmpFile);
      const {phantom, page} = result;

      result.setDefaultViewportSize();
      result.setDefaultPaperSize();

      page.evaluate(function () {
        html_render();
      });

      page.render(tmpPath, {
        format: 'pdf',
        quality: '100'
      }).then(() => {
        phantom.exit();

        const stream = fs.createReadStream(tmpPath);

        stream.on('open', () => {
          res.setHeader('Content-disposition', 'inline; filename="' + tmpFile + '"');
          res.setHeader('Content-type', 'application/pdf');

          stream.pipe(res);
        });

        stream.on('error', (err) => {
          next(err);
        });
      });
    });
  });
}
