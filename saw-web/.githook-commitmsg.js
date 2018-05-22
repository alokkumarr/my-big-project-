/*
 * Git commitmsg hook.  Ensure that Git commit message follows project
 * conventions.
 */

var code = 0;

var fs = require('fs');
process.chdir('..')

var params = process.env.GIT_PARAMS || '';
var message = fs.readFileSync(params);
var lines = message.toString().split(/\n/);
var subject = lines[0];

if (!subject.match(/^(SIP-\d+|WIP): .*/)) {
  console.log('Error: Git commit message does not start with ticket ID');
  console.log('Expected: "SIP-<nnnn>: <subject>"');
  console.log('Actual: "' + subject + '"');
  console.log();
  console.log('Please read: https://chris.beams.io/posts/git-commit/');
  console.log();
  console.log('Full commit message:');
  console.log();
  for (var i = 0; i < lines.length; i++) {
    if (!lines[i].match(/^\w*#/)) {
      console.log('> ' + lines[i]);
    }
  }
  console.log();
  code = 1;
}

/* Exit with status that indicates success or failure for Git hook */
process.exit(code);
