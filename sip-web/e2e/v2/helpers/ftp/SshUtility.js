const SSH2Promise = require('ssh2-promise');

class SshUtility {
  constructor(host, port, userName, password) {
    this._sshConfig = {
      host: host,
      username: userName,
      password: password,
      port: port
    };
    console.log('sshconfig===' + JSON.stringify(this._sshConfig));
  }
  async createDirectoryAndDummyFile(directory, content, filename) {
    let fileInfo = {};
    const ssh = new SSH2Promise(this._sshConfig);
    const pwd = await ssh.exec('pwd');

    fileInfo.pwd = pwd.replace(/(\r\n|\n|\r)/gm, '');
    fileInfo.source = `${fileInfo.pwd}/${directory}`;
    fileInfo.fileName = filename;

    await ssh.exec(
      `mkdir -p ${fileInfo.pwd}/${directory} && echo "${content}" >${
        fileInfo.pwd
      }/${directory}/${filename}`
    );
    ssh.close();
    return fileInfo;
  }
}

module.exports = SshUtility;
