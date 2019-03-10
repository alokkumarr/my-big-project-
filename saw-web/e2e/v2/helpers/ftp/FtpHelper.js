'use-strict';
const client = require('scp2');
const fs = require('fs');

class FtpHelper {
  copyUsingScp(sftpHost, name) {
    return new Promise(function(resolve, reject) {
      client.scp(
        `./${name}`,
        `root:root@${sftpHost}:8022:/home/centos`,
        function(err) {
          if (err) reject(err);
          resolve('done');
        }
      );
    });
  }

  copyFile(sftpHost, name) {
    console.log('sftpHost----' + sftpHost);
    const content = `This is file content of file ${name} ${new Date()}`;
    fs.writeFileSync(`./${name}`, content, { encoding: 'utf8' });
    this.copyUsingScp(sftpHost, name);
  }
}

module.exports = FtpHelper;
