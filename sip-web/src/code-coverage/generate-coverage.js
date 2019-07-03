const fs = require('fs');
const { html2Map } = require('./parser');

fs.readFile('./coverage/index.html', (err, htmlCoverage) => {
  if (err) {
    throw err;
  }

  const coverage = html2Map(htmlCoverage);
  const json = JSON.stringify(coverage);
  fs.writeFile('./last-coverage.json', json, 'utf8');
});
