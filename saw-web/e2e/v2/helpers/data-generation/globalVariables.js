var fs = require('fs');
const Constants = require('../Constants')
const logger = require('../../conf/logger')(__filename);
const globalVariables = {
  // String which will be appended to all users and roles that we have to make tests independent
  e2eId: getId()
};

function getId() {

  if (!fs.existsSync('target')){
    fs.mkdirSync('target');
  }
  if (!fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR)){
    fs.mkdirSync(Constants.E2E_OUTPUT_BASE_DIR);
  }
  if (fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/e2eId.json')) {
    let e2eId = JSON.parse(fs.readFileSync(Constants.E2E_OUTPUT_BASE_DIR + '/e2eId.json', 'utf8')).uniqueId;
    logger.debug('existing e2eid---@' + new Date() + ' is-->' + e2eId);
    return e2eId;
  } else {
    let id = {
      uniqueId:generateId()
    };

    fs.writeFileSync(Constants.E2E_OUTPUT_BASE_DIR +'/e2eId.json', JSON.stringify(id), { encoding: 'utf8' });
    logger.debug('created e2eid---@' + new Date() + ' is-->' + id.uniqueId);
    return id.uniqueId;
  }
}
function generateId() {
  let text = '';
  let possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'; // Only chars allowed(no numbers)

  for (let i = 0; i < 10; i++)
    text += possible.charAt(Math.floor(Math.random() * possible.length));
  return text;
}

module.exports = globalVariables;
