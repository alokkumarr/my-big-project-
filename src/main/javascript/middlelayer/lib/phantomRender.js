const phantom = require('phantom');

class PhantomRender {
  open(url, callback) {
    const e = {
      phantom: undefined,
      page: undefined,
      setDefaultViewportSize: () => {
        this.setViewportSize(e.page, {
          width: 550,
          height: 842
        });
      },
      setDefaultPaperSize: () => {
        /* eslint-disable prefer-arrow-callback */
        this.setPaperSize(e.page, {
          format: 'A4',
          orientation: 'portrait',
          margin: '1cm',
          header: {
            height: '1cm',
            contents: e.phantom.callback(function (pageNum, numPages) {
              return '<div style="text-align: right; font-size: 12px;">' + pageNum + ' / ' + numPages + '</div>';
            })
          }
        });
        /* eslint-enable prefer-arrow-callback */
      }
    };

    phantom.create()
      .then(instance => {
        e.phantom = instance;
        return instance.createPage();
      })
      .then(page => {
        e.page = page;
        return page.open(url);
      })
      .then(status => {
        if (status !== 'success') {
          return Promise.reject({status});
        }

        callback(null, e);
      })
      .catch(err => {
        if (e.phantom) {
          e.phantom.exit();
        }

        callback(err);
      });
  }

  setViewportSize(page, config) {
    page.property('viewportSize', config);
  }

  setPaperSize(page, config) {
    page.property('paperSize', config);
  }
}

module.exports = PhantomRender;
