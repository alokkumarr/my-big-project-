const path = require('path');
var appRoot = require('app-root-path');
var fs = require('fs');
var convert = require('xml-js');

var subset={};
var processedFiles = [];

/* Return true if end-to-end tests are run against distribution
 * package built with Maven and deployed to a local Docker container
 * (as happens for example on the Bamboo continuous integration
 * server), as opposed to a local saw-web front-end development
 * server */
function distRun() {
  return process.env.PWD.endsWith('/dist');
}


function generatefailedTests(dir) {

  if (!fs.existsSync(appRoot+'/target/testData')){
    fs.mkdirSync(appRoot+'/target/testData');
  }

  if (!fs.existsSync(appRoot+'/target/testData/processed')){
    fs.mkdirSync(appRoot+'/target/testData/processed');
  }
  if (!fs.existsSync(appRoot+'/target/testData/failed')){
    fs.mkdirSync(appRoot+'/target/testData/failed');
  }

  const dirCont = fs.readdirSync( dir );
  const files = dirCont.filter( ( elm ) => /.*\.(xml)/gi.test(elm) );
  let filesToProcess= [];
  // create processed file json
  if (fs.existsSync(appRoot+'/target/testData/processed/processedFiles.json')) {
    // Get all the files matching to json file
    const processedDirCount = fs.readdirSync( appRoot+'/target/testData/processed' );
    const oldProcessedFiles = processedDirCount.filter( ( elm ) => /.*\.(json)/gi.test(elm) );
    // Get new files to process
    let oldProcessedXmlFiles = [];
    oldProcessedFiles.forEach(function(oldProcessedFile) {
      oldProcessedXmlFiles.push(JSON.parse(fs.readFileSync(appRoot+'/target/testData/processed/'+oldProcessedFile,'utf8')));

    });

    files.forEach(function(file) {
      if(!oldProcessedXmlFiles.includes(file)) {
        filesToProcess.push(file);
      }
    });

    let oldProcessedFile = 'processedFiles'+new Date().getTime()+'.json';
    fs.writeFileSync(appRoot+'/target/testData/processed/'+oldProcessedFile, JSON.stringify(oldProcessedXmlFiles), { encoding: 'utf8' });

    // Create new file which is actual failed tests
    fs.writeFileSync(appRoot+'/target/testData/processed/processedFiles.json', JSON.stringify(filesToProcess), { encoding: 'utf8' });

  } else {
    filesToProcess = files;
    // write processedFiles
    fs.writeFileSync(appRoot+'/target/testData/processed/processedFiles.json', JSON.stringify(files), { encoding: 'utf8' });
  }

  let mainTestData = JSON.parse(fs.readFileSync(appRoot+'/src/test/e2e-tests/testdata/data.json','utf8'));

  filesToProcess.forEach(function(file) {

    let fileDataXlml = fs.readFileSync(dir+'/'+file,'utf8');
    let fileDataJson = JSON.parse(convert.xml2json(fileDataXlml, {compact: true, spaces: 4}));
    let testCases = fileDataJson['ns2\:test-suite']['test-cases'];

    testCases['test-case'].forEach(function(testCase) {

      if(testCase._attributes.status.toLocaleLowerCase() ==='failed') {
        // add them to retry tests
        let testMetaData = JSON.parse(testCase.name._text.split('testDataMetaInfo: ')[1]);

        if(! subset[testMetaData['feature']]) {
          subset[testMetaData['feature']] = {}
        }
        if(!subset[testMetaData['feature']][testMetaData['dp']]) {
          subset[testMetaData['feature']][testMetaData['dp']] = {}
        }
        subset[testMetaData['feature']][testMetaData['dp']][testMetaData['test']] = mainTestData[testMetaData['feature']][testMetaData['dp']][testMetaData['test']];
      }
    });
  });

  if (fs.existsSync(appRoot+'/target/testData/failed/failedTests.json')) {

    // Get all the files matching to json file
    const failedDirCount = fs.readdirSync( appRoot+'/target/testData/failed' );
    const oldFailedTests = failedDirCount.filter( ( elm ) => /.*\.(json)/gi.test(elm) );

    let oldFailedJsonData = [];
    oldFailedTests.forEach(function(oldFailedTest) {
      oldFailedJsonData.push(JSON.parse(fs.readFileSync(appRoot+'/target/testData/failed/'+oldFailedTest,'utf8')));

    });

    let oldFailedFile = 'failedTests'+new Date().getTime()+'.json';
    fs.writeFileSync(appRoot+'/target/testData/failed/'+oldFailedFile, JSON.stringify(oldFailedJsonData), { encoding: 'utf8' });

    // Create new file which is actual failed tests
    fs.writeFileSync(appRoot+'/target/testData/failed/failedTests.json', JSON.stringify(subset), { encoding: 'utf8' });

  } else {
    fs.writeFileSync(appRoot+'/target/testData/failed/failedTests.json', JSON.stringify(subset), { encoding: 'utf8' });
  }
}

module.exports = {
  root: (...args) => {
    return path.join(process.cwd(), ...args);
  },
  generatefailedTests:generatefailedTests,
  sortChunks: (chunks) => {
    return (a, b) => {
      const c = chunks.indexOf(a.names[0]);
      const d = chunks.indexOf(b.names[0]);

      return (c > d) ? 1 : (c < d) ? -1 : 0;
    };
  },
  getTestData: () => {

    if (fs.existsSync(appRoot+'/target/testData/failed/failedTests.json')) {
      console.log('executing failed--tests');
      let data = JSON.parse(fs.readFileSync(appRoot+'/target/testData/failed/failedTests.json','utf8'));
      console.log('Failed test data---'+JSON.stringify(data));
      return data;
    }else {
       console.log('executing fresh--tests');
      let data = JSON.parse(fs.readFileSync(appRoot+'/src/test/e2e-tests/testdata/data.json','utf8'));
      console.log('Fresh data--->'+JSON.stringify(data));
      return data;
    }

  },
  distRun: distRun,
  sawWebUrl: () => {
    if (distRun()) {
      var host = browser.params.saw.docker.host;
      var port = browser.params.saw.docker.port;
      return 'http://' + host + ':' + port + '/saw/web/';
    }
    //return 'http://localhost:3000/';
    return 'http://localhost/web/';
  }
};
