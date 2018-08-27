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


function readAllFiles(dir, extension) {
  const dirCont = fs.readdirSync( dir );
  const files = dirCont.filter( ( elm ) => /.*\.(xml)/gi.test(elm) );
  // create processed file json
  
  let mainTestData = JSON.parse(fs.readFileSync(appRoot+'/src/test/e2e-tests/testdata/data.json','utf8'));
  files.forEach(function(file) {

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
  })
  console.log('failed_json----'+JSON.stringify(subset));
  console.log('processed file----'+JSON.stringify(processedFiles));
}

function fromDir(startPath,filter){
  if (!fs.existsSync(startPath)){
    console.log("no dir ",startPath);
    return;
  }

  var files=fs.readdirSync(startPath);
  for(var i=0;i<files.length;i++){
    var filename=path.join(startPath,files[i]);
    var stat = fs.lstatSync(filename);
    if (stat.isDirectory()){
      fromDir(filename,filter); //recurse
    }
    else if (filename.indexOf(filter)>=0) {
      console.log('-- found: ',filename);
      console.error('Now create 2 files ... 1 processedFiles and subset of test data of failed tests');
      let fileDataXlml = fs.readFileSync(filename,'utf8');
      let fileDataJson = JSON.parse(convert.xml2json(fileDataXlml, {compact: true, spaces: 4}));
      let testCases = fileDataJson['ns2\:test-suite']['test-cases'];

      testCases['test-case'].forEach(function(testCase) {

        if(testCase._attributes.status.toLocaleLowerCase() ==='failed') {
          // add them to retry tests
          let testMetaData = JSON.parse(testCase.name._text.split('testDataMetaInfo: ')[1]);
          let mainTestData = JSON.parse(fs.readFileSync(appRoot+'/src/test/e2e-tests/testdata/data.json','utf8'));

          if(! subset[testMetaData['feature']]) {
            subset[testMetaData['feature']] = {}
          }
          if(!subset[testMetaData['feature']][testMetaData['dp']]) {
            subset[testMetaData['feature']][testMetaData['dp']] = {}
          }
          subset[testMetaData['feature']][testMetaData['dp']][testMetaData['test']] = mainTestData[testMetaData['feature']][testMetaData['dp']][testMetaData['test']];

        }
      });

      //let mainTestData = JSON.parse(fs.readFileSync(appRoot+'/src/test/e2e-tests/testdata/data.json','utf8'));

    };
  };
};

module.exports = {
  root: (...args) => {
    return path.join(process.cwd(), ...args);
  },
  readAllFiles:readAllFiles,
  fromDir:fromDir,
  sortChunks: (chunks) => {
    return (a, b) => {
      const c = chunks.indexOf(a.names[0]);
      const d = chunks.indexOf(b.names[0]);

      return (c > d) ? 1 : (c < d) ? -1 : 0;
    };
  },
  getTestData: () => {
    //check if it is retry or first run
    // if (browser.suite) {
    //   console.log('I am coming from suite....')
    // }else {
    //   console.log('I am coming from retry....')
    // }

      if(Object.keys(subset).length > 0) {
        console.log('executing Failed--tests with data:  -->'+JSON.stringify(subset));
        return subset;
    }else {
      console.log('executing fresh--tests');
        console.log('subset data:  -->'+JSON.stringify(subset));
      let data = fs.readFileSync(appRoot+'/src/test/e2e-tests/testdata/data.json','utf8');
      console.log(JSON.stringify(JSON.parse(data)))
      return JSON.parse(data);
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
