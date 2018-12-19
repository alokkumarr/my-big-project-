# Introduction

This is the Synchronoss Analytics Workbench (SAW) Web component. It
provides a web user interface to SAW services. The project is further
described on its [Confluence page].

[confluence page]: https://confluence.synchronoss.net:8443/display/BDA/Synchronoss+Analytics+Workbench+-+SAW

# Operations

See the [operations guide](doc/operations.md) for details about
operations and monitoring.

# Development

### Requirements: Node 4+ && NPM 3+

## Use NPM scripts

- `npm run start` to launch a webpack-dev-server on your source files
- `npm run build` to build an optimized version of your application in /dist
- `npm run test` to launch your unit tests with Karma
- `npm run test:start` to launch your unit tests with Karma in watch mode
- `npm run lint` to launch tslint checks

#### Note for Windows machines

if you are using a windows machine, you have to set git to to use lf line endings instead of clrf.
because eslint gives errors with crlf line endings, and the eslint rule cannot be overridden
this is what you have to to in order to solve it
[http://stackoverflow.com/a/13154031](http://stackoverflow.com/a/13154031)

## Adding a new theme

- go to src/themes
- make a new file, or just copy one and rename it
- change the \$theme-name variable
- change the colors you want
- go to src/themes.js
- import your scss theme file and put your theme name in the themeName const
- don't forget to comment out the other theme import

## Running production build from

- Run `npm run build`
- This will create a `dist` directory at root of your project.
- Run `mkdir saw/web && mv dist/* saw/web/ && mv saw dist/` from root directory (where package.json is located.)
- Change to `dist` directory and use a file server. If you don't have one, try `python -m SimpleHTTPServer 8000`
- Visit `localhost:8000/saw/web` in your browser.

## The Angular styleguide to follow

[https://angular.io/guide/styleguide](https://angular.io/guide/styleguide)

## Workflow to follow

http://nvie.com/posts/a-successful-git-branching-model/

## Steps to use the Maven integrated seed in Development Environment

Assumption: Maven 3.x & Java 7.x is already installed in laptop

- Either it can be used in WebStorm IDE or in Eclipse or in STS
- In Eclipse you can import it as maven project, once you import it
- To bring up the project in local mode server, under project main directory i.e. sncr_saw/
- execute mvn clean install -P dev, it will in turn up localhost server after installing node, npm & required npm package

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

## Steps for adding Icons

- go to [icomoon](https://icomoon.io/app/#/select)
- if there are other icon sets loaded, just delete them
- click on the Import Icons button
- select the selection.json file in the fonts directory (note: not assets/fonts, use the fonts directory in the root directory)
- click on the menu button of the icon set, on the right, and select "Import to set" to add an svg icon, give the same name and tags to the icon
- when done, generate font and download it
- copy the zipped folder contents to the fonts directory where the current selection.json resides, for future modification
- from there, copy the fonts to the assets/fonts directory, also copy the styles.css and rename it to icomoon.css, edit the icomoon.css file, and delete the "fonts/" part form the urls of the fonts
