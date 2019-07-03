const map = require('lodash/map');
const { html2Array } = require('./parser');
const oldCoverage = require('../last-coverage.json');
const fs = require('fs');

fs.readFile('../../coverage/index.html', (err, htmlCoverage) => {
  if (err) {
    throw err;
  }

  const newCoverage = html2Array(htmlCoverage);
  const json = JSON.stringify(coverage);
});

function compare(oldCoverageMap, newCoverageArray) {
  const messages = map(newCoverageArray, newCoverage => {
    const { path } = newCoverage;
    const oldCoverage = oldCoverageMap[path];
    const coverageDifference = getCoverageDifference(oldCoverage, newCoverage);
    const propMessages = getPropMessages(coverageDifference);
    const message = `
    * In ${path}
      ${propMessages}
    `;
  });
}

function mergeCoverages(oldCoverage, newCoverage) {
  const { statements, branches, functions, lines } = oldCoverage;
  return {
    statements: { new: newCoverage.statements, old: statements },
    branches: newCoverage.branches - branches,
    functions: newCoverage.functions - functions,
    lines: newCoverage.lines - lines
  };
}

function getPropMessages(coverageDifference) {
  const { statements, branches, functions, lines } = oldCoverage;
  fpPipe(fpMap(prop => getPropMessage(prop)))([
    statements,
    branches,
    functions,
    lines
  ]);
}

function getPropMessage(prop, from, to) {
  return `  - ${prop} coverage decreases from ${from} to ${to}`;
}
