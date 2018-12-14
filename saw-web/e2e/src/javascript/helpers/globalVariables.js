var fs = require('fs');
const globalVariables = {
  // String which will be appended to all users and roles that we have to make tests independent
  e2eId: getId()
};

function getId() {

  if (!fs.existsSync('target')){
    fs.mkdirSync('target');
  }
  if (!fs.existsSync('target/e2e')){
    fs.mkdirSync('target/e2e');
  }
  if (fs.existsSync('target/e2e/e2eId.json')) {
    let e2eId = JSON.parse(fs.readFileSync('target/e2e/e2eId.json', 'utf8')).uniqueId;
    //console.log('existing e2eid---@' + new Date() + ' is-->' + e2eId);
    return e2eId;
  } else {
    let id = {
      uniqueId:generateId()
    };

    fs.writeFileSync('target/e2e/e2eId.json', JSON.stringify(id), { encoding: 'utf8' });
    //console.log('created e2eid---@' + new Date() + ' is-->' + id.uniqueId);
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
