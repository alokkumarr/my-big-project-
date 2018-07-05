const globalVariables = {
  // String which will be appended to all users and roles that we have to make tests independent
  e2eId: generateId()
};

function generateId() {
  let text = '';
  let possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'; // Only chars allowed(no numbers)

  for (let i = 0; i < 10; i++)
    text += possible.charAt(Math.floor(Math.random() * possible.length));

  return text;
}

module.exports = globalVariables;
