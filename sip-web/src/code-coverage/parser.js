const memoize = require('lodash/memoize');
const get = require('lodash/get');
const map = require('lodash/map');
const fpPipe = require('lodash/fp/pipe');
const fpMap = require('lodash/fp/map');
const reduce = require('lodash/reduce');
const fpJoin = require('lodash/fp/join');
const fpRange = require('lodash/fp/range');
const cheerio = require('cheerio');

const getPropPath = memoize(index => {
  const nexts = fpPipe(fpRange(0), fpMap(() => 'next.next.'), fpJoin(''))(
    index
  );
  return `children[0].next.${nexts}attribs['data-value']`;
});

function getFloatValue(tr, index) {
  return parseFloat(get(tr, getPropPath(index)));
}

function getPropsFormTr(tr) {
  const path = get(tr, getPropPath(0));
  const statements = getFloatValue(tr, 2);
  const branches = getFloatValue(tr, 4);
  const functions = getFloatValue(tr, 6);
  const lines = getFloatValue(tr, 8);
  return { path, statements, branches, functions, lines };
}

function html2Map(htmlCoverage) {
  const $ = cheerio.load(htmlCoverage);
  const tableRows = Array.from($('tbody tr'));
  return reduce(
    tableRows,
    (rowsMap, tr) => {
      const { path, statements, branches, functions, lines } = getPropsFormTr(
        tr
      );
      rowsMap[path] = { statements, branches, functions, lines };
      return rowsMap;
    },
    {}
  );
}

function html2Array(htmlCoverage) {
  const $ = cheerio.load(htmlCoverage);
  const tableRows = Array.from($('tbody tr'));
  return map(tableRows, tr => getPropsFormTr(tr), {});
}

module.exports = {
  html2Map,
  html2Array
};
