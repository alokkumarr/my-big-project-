const replace = require('lodash/replace');
const fs = require('fs');

export const getVersion = () => {
  try {
    let data = fs.readFileSync('target/classes/git.properties');
    data = data.toString();
    const version = data.match(/git\.commit\.id\.describe=(.*)/);
    return (version && version.length > 1 ?
      replace(version[1], '-dirty', '') :
      JSON.stringify(version)
    );
  } catch (err) {
    return err.message;
  }
};
