/*
 * Git precommit hook.  Ensure that Git author email is a Synchronoss
 * email.
 *
 * Workaround: These commit check scripts should be moved out of the
 * "saw-web" module up to the top-level and managed there.  But leave
 * them in "saw-web" for now as it has husky already set up to install
 * the hooks.
 */

var email = process.env.GIT_AUTHOR_EMAIL || '';
var code = 0;
var suffix = '@synchronoss.com';

if (!email.endsWith(suffix)) {
  console.log('Error: Git author email should end with: ' + suffix);
  console.log('Actual: ' + email);
  code = 1;
}

/* Exit with status that indicates success or failure for Git hook */
process.exit(code);
