const fs = require('fs');
const join = require('lodash/join');
const keys = require('lodash/keys');
const fpJoin = require('lodash/fp/join');
const isEmpty = require('lodash/isEmpty');
const fpPipe = require('lodash/fp/pipe');
const fpMap = require('lodash/fp/map');
const fpReject = require('lodash/fp/reject');
const { html2Array } = require('./parser');
const oldCoverageMap = require('./last-coverage.json');

fs.readFile('./coverage/index.html', (err, htmlCoverage) => {
  if (err) {
    throw err;
  }

  const newCoverageArray = html2Array(htmlCoverage);
  const coverage = coverageDecreasedMessages(oldCoverageMap, newCoverageArray);

  if (!isEmpty(coverage)) {
    console.log(coverage);
    process.exit(1);
  }
});

function coverageDecreasedMessages(oldCoverageMap, newCoverageArray) {
  return fpPipe(
    fpMap(newCoverage => getFolderCoverage(newCoverage, oldCoverageMap)),
    fpReject(isEmpty),
    fpJoin('\n\n')
  )(newCoverageArray);
}

function getFolderCoverage(newCoverage, oldCoverageMap) {
  const { path } = newCoverage;
  const oldCoverage = oldCoverageMap[path];
  const mergedCovereges = mergeCoverages(oldCoverage, newCoverage);
  const propMessages = getPropMessages(mergedCovereges);
  if (isEmpty(propMessages)) {
    return '';
  }

  const messsagesString = join(propMessages, '\n');
  return `* In ${path}\n${messsagesString}`;
}

function mergeCoverages(
  oldCoverage = { statements: 0, branches: 0, functions: 0, lines: 0 },
  newCoverage
) {
  const { statements, branches, functions, lines } = oldCoverage;
  return {
    statements: { new: newCoverage.statements, old: statements },
    branches: { new: newCoverage.branches, old: branches },
    functions: { new: newCoverage.functions, old: functions },
    lines: { new: newCoverage.lines, old: lines }
  };
}

function getPropMessages(mergedCovereges) {
  const props = keys(mergedCovereges);
  return fpPipe(
    fpMap(prop => getPropMessage(prop, mergedCovereges[prop])),
    fpReject(isEmpty)
  )(props);
}

function getPropMessage(prop, mergedCoverege) {
  const { old, new: newCoverage } = mergedCoverege;
  const coverageDecreased = newCoverage - old < 0;
  if (coverageDecreased) {
    return `  - ${prop} coverage decreased from ${old}% to ${newCoverage}%`;
  }
  return '';
}
