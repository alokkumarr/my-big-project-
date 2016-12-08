# Razorsight-UI-Seed ![Razoright](app/assets/img/logo.png)

*@author: Saurav Paul*
***
![dashboard-sample](dashboard.png)
***
#### razorsight-ui-seed follows the
[Best Practice Recommendations for Angular App Structure](https://docs.google.com/document/d/1XXMvReO8-Awi1EZXAXS4PzDzdNvV6pGcuaF4Q9821Es/pub)
***
#### This style guide is a list of dos and don'ts for JavaScript programs
[Google JavaScript Style Guide](http://google-styleguide.googlecode.com/svn/trunk/javascriptguide.xml)

## Features
1. Provides a directory structure geared towards large Angular projects.
    * Each controller, service, filter, and directive are placed in their own file.
    * All files related to a conceptual unit are placed together. For example, the controller, HTML, LESS, and unit test for a partial are placed together in the same directory.
2. Provides a ready-made Grunt build that produces an extremely optimized distribution.
    * Build uses [grunt-ng-annotate](https://github.com/olov/ng-annotate) so you don't have to use the Angular injection syntax for safe minification (i.e. you dont need `$inject` or `(['$scope','$http',....`
    * grunt serve task allows you to run a simple development server with watch/livereload enabled. Additionally, JSHint and the appropriate unit tests are run for the changed files.
3. Integrates Bower for package management
4. Includes Yeoman subgenerators for directives, services, partials, filters, and modules.
5. Integrates LESS and includes Bootstrap via the source LESS files allowing you to reuse Bootstrap vars/mixins/etc.
6. Easily Testable - Each sub-generator creates a skeleton unit test. Unit tests can be run via `grunt test` and they run automatically during the grunt watch that is active during `grunt serve`.

***
## Tools used for this seed

|Tools|Purpose|
|:-----|:-------|
|[Grunt](http://gruntjs.com/)| JavaScript Task Runner|
|[Bower](http://bower.io/)| Package Manager for the web|
|[Yeoman](http://yeoman.io/)|Web's Scaffolding Tool for Web|
|[LESS](http://lesscss.org/)|CSS Preprocessor|
|[NPM](https://www.npmjs.com/)|Package Manager used by `Bower` & `Grunt`|


## Directory Structure
***
All subgenerators prompt the user to specify where to save the new files. Thus you can create any directory structure you desire, including nesting. The generator will create a handful of files in the root of your project including index.html, app.js, and app.less. You determine how the rest of the project will be structured.

There are a set of subgenerators to initialize empty Angular components. Each of these generators will:

* Create one or more skeleton files (javascript, LESS, html, spec etc) for the component type.
* Update index.html and add the necessary script tags.
* Update app.less and add the @import as needed.
* For partials, update the app.js, adding the necessary route call if a route was entered in the generator prompts.

There are generators for `directive`,`partial`,`service`, `filter`, `module`, and `modal`.

Running a generator:

```
yo angular:controller user
yo angular:directive myDirective
yo angular:route myroute
yo angular:route myRoute --uri=my/route
yo angular:directive myDirective
yo angular:view user
yo angular:service myService
yo angular:decorator serviceName

```
The name paramater passed (i.e. 'my-directive') will be used as the file names. The generators will derive appropriate class names from this parameter (ex. 'my-directive' will convert to a class name of 'MyDirective'). Each sub-generator will ask for the folder in which to create the new skeleton files. You may override the default folder for each sub-generator in the .yo-rc.json file.
***
## Submodules

Submodules allow you to more explicitly separate parts of your application. Use the `yo cg-angular:module my-module` command and specify a new subdirectory to place the module into. Once you've created a submodule, running other subgenerators will now prompt you to select the module in which to place the new component.

***
## Preconfigured Libraries

The new app will have a handful of preconfigured libraries included. This includes Angular 1.2, Bootstrap 3, AngularUI Bootstrap, AngularUI Utils, FontAwesome 4, JQuery 2, Underscore 1.5, LESS 1.6, and Moment 2.5 etc. You may of course add to or remove any of these libraries. But the work to integrate them into the app and into the build process has already been done for you. To get the details about the preconfigured libraries please look at `bower.json`

## Getting started with razorsight-ui-seed


The razorsight-ui-seed consists of preconfigured list of components. Th whole UI has been refurbished using razorsight color palatte.

![Razoright color palette](razorsight-color-palette.png)

*Note: Please use the above color palatte to maintain the consistent look & feel*

The project will include a ready-made Grunt build that will:

* Build all the LESS files into one minified CSS file.
* Uses [grunt-angular-templates](https://github.com/ericclemmons/grunt-angular-templates) to turn all your partials into Javascript.
* Uses [grunt-ng-annotate](https://github.com/olov/ng-annotate) to preprocess all Angular injectable methods and make them minification safe. Thus you don't have to use the array syntax.
* Concatenates and minifies all Javascript into one file.
* Replaces all appropriate script references in `index.html` with the minified CSS and JS files.(Optionally)
  Minifies any images in `/img.`
* Minifies the `index.html.`
* Copies any extra files necessary for a distributable build (ex. Font-Awesome font files, etc).The resulting build loads only a few highly compressed files.

The build process uses [grunt-dom-munger](https://github.com/cgross/grunt-dom-munger) to pull script references from the index.html. This means that your `index.html` is the single source of truth about what makes up your app. Adding a new library, new controller, new directive, etc does not require that you update the build file. Also the order of the scripts in your index.html will be maintained when they're concatenated.

Importantly, `grunt-dom-munger`uses CSS attribute selectors to manage the parsing of the script and link tags. Its very easy to exclude certain scripts or stylesheets from the concatenated files. This is often the case if you're using a CDN. This can also be used to prevent certain development scripts from being included in the final build.

To prevent a script or stylesheet from being included in concatenation, put a `data-concat="false"` attribute on the link or script tag. This is currently applied for the `livereload.js` and `less.js` script tags.

To prevent a script or link tag from being removed from the finalized index.html, use a `data-remove="false"` attribute.

List of UI third party components has been integrated with examples which are as follow:

|Components I|Components II|Components III|
|:-----------|:------------|:-------------|
|Responsive grid/ng-grid|Leaflet & Open Street Maps|Breadcrumbs|
|Search box|Vector Maps|Accordions|
|Calendar|Sliders & Ranges|Modal Window|
|Drag & Drop Calendar|Progress Bar|File upload|
|ckEditor|Validation|Drag & Drop File upload|
|Charts (inline, interactive)|Form wizards|Header Bar|
|Toggle Side Bar|Date Paginator|Panels

#### Description of the directories & how to use it

``` 

LESS files description :
/app/assets/less/

variables.less	Bootstrap's variables.less, with additional variables for seed
navbar.less	Bootstrap's navbar.less file, heavily modified
layout.less	Handles the layout & left sidebar of the theme.
topnav.less	The topnav dropdowns
leftbar.less	The left sidebar navigation and search
rightbar.less	The right sidebar, widget area and chatbox
panels.less	Completely redone to support tabs
tiles.less	the Tiles component
apps.less	A few plugins used for the theme has their style modified to use with the theme. Those codes are available here.
razorsight-mixins.less	Mixins used in Razorsight and in some cases modification of bootstrap's mixins.
feeds.less	The CSS for the Feeds Panel and To Do
timeline.less	The CSS for Timeline apge
tabs.less	The CSS for the inline tabs and accordions
```

```
The following files are modification on top of Bootstrap's codes

extend-core.less	scaffolding, type, code, grid, tables, forms, buttons
extend-common.less	dropdown, list-groups, wells, close
extend-nav.less	navs, button-groups, breadcrumbs, paginations, pager
extend-popover.less	modals, tooltips, popovers
extend-misc.less	alerts, thumbnails, labels, badges, progressbars, input groups...
```

```
/img: This directory is used to place your png or jpeg any image file format which will be included into your production build.

```
```
/scripts/shared: Defines all the common directives to be used throughout the webapp
```
```
/views/templates: Cached templates for fast rendering. Cached version of files found in templates/views/*.html files. Note You must run grunt ngtemplates to see the changes made to html files
```
```
/views/templates/overrides.js: Use this file to put all the template customizations.
```
```
/assets/plugins: This folder contains all of the plug-ins which are not available in npm repsoitory.
```

#### Razorsight-UI-Seed is built with the modern build flow that incorporates Bower and Grunt to automate the build process.


*Prerequisites: Node, NPM, Grunt, Yeoman, and Bower. Once Node is installed, do:*

Once you clone this ui-seed & verify grunt is installed, cd to the project folder and run:

```
npm install

```

This will install the necessary dependencies for running Grunt tasks.

Now you should install the Bower packages required by razorsight-ui-seed. To do this:

```
bower install

```

It'll install all the necessary packages for razorsight-ui-seed in the `ui-seed/bower_components folder`.

```
Install livereload plug-in in Chrome

```

Yeoman

```
npm install -g yo
npm install -g grunt-cli yo bower
npm install -g generator-angular

```
Once you execute the above the command on your app root folder then you will be able to use generators for directive,partial,service, filter, module, and modal

```
yo angular:controller user
yo angular:directive myDirective
yo angular:route myroute
yo angular:route myRoute --uri=my/route
yo angular:directive myDirective
yo angular:view user
yo angular:service myService
yo angular:decorator serviceName

```

#### Grunt Task

Now that the project is created, you have 3 simple Grunt commands available:

```
grunt serve   #This will run a development server with watch & livereload enabled & hit `http://localhost:9001` on your browser
grunt test    #Run local unit tests.
grunt build   #Places a fully optimized (minified, concatenated, and more) in /dist
```
When `grunt serve` is running, any changed javascript files will be linted using JSHint as well as have their appropriate unit tests executed. Only the unit tests that correspond to the changed file will be run. This allows for an efficient test driven workflow.


## Using Bower, NPM & GIT behind corporate proxy

#### NPM & Bower

##### There are two options to use Bower & NPM behind proxy

##### OPTION 1:
```
Bower :
Open .bowerrc file include the proxy information
"proxy":"http://<host>:<port>",
"https-proxy":"http://<host>:<port>"

NPM:
npm config set proxy http://proxy.company.com:8080
npm config set https-proxy http://proxy.company.com:8080

If you need to specify credentials, they can be passed in the url using the following syntax
http://user_name:password@proxy.company.com:8080


```

##### OPTION 2:
```
Open your Computer Settings (right click computer) and navigate to the Advanced Settings -> Environment Variables… Add 2 properties HTTP_PROXY and HTTPS_PROXY. (format for the url http(s)://username@password:server:port, only server is required)

```

* (Optional) Configure NPM to store modules and cache on local drive instead of network

```
By default NPM stores modules and cache in the AppData\Roaming directory, if you want to change the default location then you can change the configure npm to store the modules and cache localy we need to execute the following to commands from the command line

npm config set prefix D:\data\nodejs\npm --global
npm config set cache D:\data\nodejs\npm-cache --global

```

#### GIT

```
Yeoman uses Bower as a JavaScript package manager. Bower needs access to git repositories and in general does this by using the git: protocol based URLs. However most corporate proxies don’t allow those to pass. We need to tell git that instead of git: use https:. We can do this by issueing yet another command from the command line:

git config --global url."https://".insteadOf git:// (use this options whenver you opt for OPTION 2)
```
`OR`

```
git config --global http.proxy http://proxyuser:proxypwd@proxy.server.com:8080
git config --global https.proxy https://proxyuser:proxypwd@proxy.server.com:8080
change proxyuser to your proxy user
change proxypwd to your proxy password
change proxy.server.com to the URL of your proxy server
change 8080 to the proxy port configured on your proxy server

If you decide at any time to reset this proxy and work without (no proxy):

git config --global --unset http.proxy
git config --global --unset https.proxy

```

***
## Revision History

|Version|Date|Info|
|:------|:---|:---|
|v1.0.0|01/26/2014|Initial Release of razorsight-ui-seed|
|v1.0.0|01/27/2014|Documentation README.md file|
|v2.0.0|02/11/2015|Directory Structure changed to simplify & fixed production build issue|

