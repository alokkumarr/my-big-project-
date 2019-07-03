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

function getPropsFormTr(tr) {
  const path = get(tr, getPropPath(0));
  const statements = get(tr, getPropPath(2));
  const branches = get(tr, getPropPath(4));
  const functions = get(tr, getPropPath(6));
  const lines = get(tr, getPropPath(8));
  return { path, statements, branches, functions, lines };
}

export function html2Map() {
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

export function html2Array() {
  const $ = cheerio.load(htmlCoverage);
  const tableRows = Array.from($('tbody tr'));
  return map(tableRows, tr => getPropsFormTr(tr), {});
}
