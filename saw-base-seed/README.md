# Synchronoss Visual Framework

### Requirements: Node 4+ && NPM 3+

## Use NPM scripts

- `npm run build` to build an optimized version of your application in /dist
- `npm run serve` to launch a browser sync server on your source files
- `npm run serve:dist` to launch a server on your optimized application
- `npm run test` to launch your unit tests with Karma
- `npm run test:auto` to launch your unit tests with Karma in watch mode
- `npm run styleguide` to launch the live styleguide that shows you the apps components

#### Note for Windows machines

  if you are using a windows machine, you have to set git to to use lf line endings instead of clrf.
  because eslint gives errors with crlf line endings, and the eslint rule cannot be overridden
  this is what you have to to in order to solve it
  [http://stackoverflow.com/a/13154031](http://stackoverflow.com/a/13154031)
  
## Adding a new theme

  - go to src/themes
  - make a new file, or just copy one and rename it
  - change the $theme-name variable
  - change the colors you want
  - go to src/themes.js
  - import your scss theme file and put your theme name in the themeName const
  - don't forget to comment out the other theme import
  
## The Angular styleguide to follow
[https://github.com/toddmotto/angular-styleguide](https://github.com/toddmotto/angular-styleguide)

1. Inline big nested anonymus functions are not allowed.
2. Private properties should start with '__'. Example: this.__myPrivateProp
3. For Directives use the Classes style: https://github.com/toddmotto/angular-styleguide#constants-or-classes

## Workflow to follow
http://nvie.com/posts/a-successful-git-branching-model/
