const path = require('path');
var fs = require('fs');
var convert = require('xml-js');
const globalVariables = require('./src/javascript/helpers/globalVariables');

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


function generateFailedTests(dir) {

  if (!fs.existsSync('target')){
    fs.mkdirSync('target');
  }
  if (!fs.existsSync('target/testData')){
    fs.mkdirSync('target/testData');
  }

  if (!fs.existsSync('target/testData/processed')){
    fs.mkdirSync('target/testData/processed');
  }
  if (!fs.existsSync('target/testData/failed')){
    fs.mkdirSync('target/testData/failed');
  }

  const dirCont = fs.readdirSync( dir );
  const files = dirCont.filter( ( elm ) => /.*\.(xml)/gi.test(elm) );
  let filesToProcess= [];
  // create processed file json
  if (fs.existsSync('target/testData/processed/processedFiles.json')) {
    // Get all the files matching to json file
    const processedDirCount = fs.readdirSync('target/testData/processed' );
    const oldProcessedFiles = processedDirCount.filter( ( elm ) => /.*\.(json)/gi.test(elm) );
    // Get new files to process
    let oldProcessedXmlFiles = [];
    oldProcessedFiles.forEach(function(oldProcessedFile) {
      let filesInOld = JSON.parse(fs.readFileSync('target/testData/processed/'+oldProcessedFile,'utf8'));
      oldProcessedXmlFiles = [...oldProcessedXmlFiles, ...filesInOld];

    });

    files.forEach(function(file) {
      if(!oldProcessedXmlFiles.includes(file)) {
        //console.log('new file adding into file to be processed...'+file)
        filesToProcess.push(file);
      }
    });

    let oldProcessedFile = 'oldProcessedFiles_'+new Date().getTime()+'.json';
    fs.writeFileSync('target/testData/processed/'+oldProcessedFile, JSON.stringify(oldProcessedXmlFiles), { encoding: 'utf8' });

    // delete old file
    fs.unlinkSync('target/testData/processed/processedFiles.json');
    // Create new file which is actual failed tests
    fs.writeFileSync('target/testData/processed/processedFiles.json', JSON.stringify(filesToProcess), { encoding: 'utf8' });

  } else {
    filesToProcess = files;
    // write processedFiles
    fs.writeFileSync('target/testData/processed/processedFiles.json', JSON.stringify(files), { encoding: 'utf8' });
  }
  let mainTestData = JSON.parse(fs.readFileSync('./e2e/src/testdata/data.json','utf8'));

  filesToProcess.forEach(function(file) {

    let fileDataXml = fs.readFileSync(dir+'/'+file,'utf8');
    let fileDataJson = JSON.parse(convert.xml2json(fileDataXml, {compact: true, spaces: 4}));
    let testCases = fileDataJson['ns2\:test-suite']['test-cases'];

    if(Array.isArray(testCases['test-case'])){
      //more than 1 tests failed
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
    } else {
      // only 1 test failed
      let testCase = testCases['test-case'];
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
    }

  });

  if (fs.existsSync('target/testData/failed/failedTests.json')) {

    // Get all the files matching to json file
    const failedDirCount = fs.readdirSync('target/testData/failed' );
    const oldFailedTests = failedDirCount.filter( ( elm ) => /.*\.(json)/gi.test(elm) );

    let oldFailedJsonData = [];
    oldFailedTests.forEach(function(oldFailedTest) {

      oldFailedJsonData.push(JSON.parse(fs.readFileSync('target/testData/failed/'+oldFailedTest,'utf8')));

    });

    let oldFailedFile = 'oldFailedTests_'+new Date().getTime()+'.json';
    fs.writeFileSync('target/testData/failed/'+oldFailedFile, JSON.stringify(oldFailedJsonData), { encoding: 'utf8' });

    // Create new file which is actual failed tests
    fs.unlinkSync('target/testData/failed/failedTests.json');
    fs.writeFileSync('target/testData/failed/failedTests.json', JSON.stringify(subset), { encoding: 'utf8' });

  } else {
    if(Object.keys(subset).length > 0) {
      fs.writeFileSync('target/testData/failed/failedTests.json', JSON.stringify(subset), { encoding: 'utf8' });
    }
  }
  if(Object.keys(subset).length > 0) {
    //console.log('failed data-->'+subset)
  }
}

module.exports = {
  root: (...args) => {
    return path.join(process.cwd(), ...args);
  },
  generateFailedTests:generateFailedTests,
  sortChunks: (chunks) => {
    return (a, b) => {
      const c = chunks.indexOf(a.names[0]);
      const d = chunks.indexOf(b.names[0]);

      return (c > d) ? 1 : (c < d) ? -1 : 0;
    };
  },
  getSawWebUrl: () => {
    let url;

    if (!fs.existsSync('target')){
      fs.mkdirSync('target');
    }

    if (fs.existsSync('target/url.json')) {
      url = JSON.parse(fs.readFileSync('target/url.json','utf8')).baseUrl;
    } else {
      process.argv.forEach(function (val) {
        if(val.includes('--baseUrl')) {
          url =  val.split('=')[1];url
          let urlObject = {
            baseUrl:url,
            e2eId:globalVariables.generateE2eId
          }
          fs.writeFileSync('target/url.json', JSON.stringify(urlObject), { encoding: 'utf8' });
          return;
        }
      });
    }
    return url;
  },
  getTestData: () => {

    if (fs.existsSync('target/testData/failed/failedTests.json')) {
      //console.log('executing failed--tests');
      let data = JSON.parse(fs.readFileSync('target/testData/failed/failedTests.json','utf8'));
      //console.log('Failed test data---'+JSON.stringify(data));
      return data;
    }else {
       //console.log('executing fresh--tests');
      let data = JSON.parse(fs.readFileSync('../saw-web/e2e/src/testdata/data.json','utf8'));
      //console.log('Fresh data--->'+JSON.stringify(data));
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
