# Synchronoss Visual Framework

### Requirements: Node 4+ && NPM 3+

## Use NPM scripts

- `npm run build` to build an optimized version of your application in /dist
- `npm run start` to launch a webpack-dev-server on your source files
- `npm run start:prod` to launch a webpack-dev-server on your optimized application
- `npm run test` to launch your unit tests with Karma
- `npm run test:watch` to launch your unit tests with Karma in watch mode
- `npm run lint` to launch eslint checks
- `npm run lint:fix` to launch eslint checks with automatic code isses fix
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

## Steps to use the Maven integrated seed in Development Environment

   Assumption: Maven 3.x & Java 7.x is already installed in laptop

 - Either it can be used in WebStorm IDE or in Eclipse or in STS
 - In Eclipse you can import it as maven project, once you import it
 - To bring up the project in local mode server, under project main directory i.e. sncr_saw/
 - execute mvn clean install, it will inturn turn up localhost server after installing node, npm & required npm package

 - In WebStorm, you can open a project &
 - execute npm install
 - execute npm run serve
 - rest of the command whatever mentioned above

## How to execute Maven integrated seed along with npm & release build for production

- to release the production build; goto
- project's base folder where pom.xml exists
- execute mvn -Pprod -Doutput=D:\Work\SAW2_0\ clean install
   "-Doutput" : put the path where you want to produce the tar.gz file.
- Distribution will contain the files

## Internationalization

- I18n is done with the use of [angular-translate](https://angular-translate.github.io/docs/#/guide/00_installation)

- Json dictionaries for the supported languages can be found in
assets/i18n
- at the moment there are 4 different modules:
  - common: fro common word in the application for example: Ok, Cancel, Rename, etc...
  - alerts, analyze, observe, for different modules of the application

- Adding a new module:
make an i18n.js angular config file like in the other modules, and include it in the modules index.js
