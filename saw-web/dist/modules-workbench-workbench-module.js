(window["webpackJsonp"] = window["webpackJsonp"] || []).push([["modules-workbench-workbench-module"],{

/***/ "./node_modules/lodash/_baseFindKey.js":
/*!*********************************************!*\
  !*** ./node_modules/lodash/_baseFindKey.js ***!
  \*********************************************/
/*! no static exports found */
/***/ (function(module, exports) {

/**
 * The base implementation of methods like `_.findKey` and `_.findLastKey`,
 * without support for iteratee shorthands, which iterates over `collection`
 * using `eachFunc`.
 *
 * @private
 * @param {Array|Object} collection The collection to inspect.
 * @param {Function} predicate The function invoked per iteration.
 * @param {Function} eachFunc The function to iterate over `collection`.
 * @returns {*} Returns the found element or its key, else `undefined`.
 */
function baseFindKey(collection, predicate, eachFunc) {
  var result;
  eachFunc(collection, function(value, key, collection) {
    if (predicate(value, key, collection)) {
      result = key;
      return false;
    }
  });
  return result;
}

module.exports = baseFindKey;


/***/ }),

/***/ "./node_modules/lodash/countBy.js":
/*!****************************************!*\
  !*** ./node_modules/lodash/countBy.js ***!
  \****************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var baseAssignValue = __webpack_require__(/*! ./_baseAssignValue */ "./node_modules/lodash/_baseAssignValue.js"),
    createAggregator = __webpack_require__(/*! ./_createAggregator */ "./node_modules/lodash/_createAggregator.js");

/** Used for built-in method references. */
var objectProto = Object.prototype;

/** Used to check objects for own properties. */
var hasOwnProperty = objectProto.hasOwnProperty;

/**
 * Creates an object composed of keys generated from the results of running
 * each element of `collection` thru `iteratee`. The corresponding value of
 * each key is the number of times the key was returned by `iteratee`. The
 * iteratee is invoked with one argument: (value).
 *
 * @static
 * @memberOf _
 * @since 0.5.0
 * @category Collection
 * @param {Array|Object} collection The collection to iterate over.
 * @param {Function} [iteratee=_.identity] The iteratee to transform keys.
 * @returns {Object} Returns the composed aggregate object.
 * @example
 *
 * _.countBy([6.1, 4.2, 6.3], Math.floor);
 * // => { '4': 1, '6': 2 }
 *
 * // The `_.property` iteratee shorthand.
 * _.countBy(['one', 'two', 'three'], 'length');
 * // => { '3': 2, '5': 1 }
 */
var countBy = createAggregator(function(result, value, key) {
  if (hasOwnProperty.call(result, key)) {
    ++result[key];
  } else {
    baseAssignValue(result, key, 1);
  }
});

module.exports = countBy;


/***/ }),

/***/ "./node_modules/lodash/endsWith.js":
/*!*****************************************!*\
  !*** ./node_modules/lodash/endsWith.js ***!
  \*****************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var baseClamp = __webpack_require__(/*! ./_baseClamp */ "./node_modules/lodash/_baseClamp.js"),
    baseToString = __webpack_require__(/*! ./_baseToString */ "./node_modules/lodash/_baseToString.js"),
    toInteger = __webpack_require__(/*! ./toInteger */ "./node_modules/lodash/toInteger.js"),
    toString = __webpack_require__(/*! ./toString */ "./node_modules/lodash/toString.js");

/**
 * Checks if `string` ends with the given target string.
 *
 * @static
 * @memberOf _
 * @since 3.0.0
 * @category String
 * @param {string} [string=''] The string to inspect.
 * @param {string} [target] The string to search for.
 * @param {number} [position=string.length] The position to search up to.
 * @returns {boolean} Returns `true` if `string` ends with `target`,
 *  else `false`.
 * @example
 *
 * _.endsWith('abc', 'c');
 * // => true
 *
 * _.endsWith('abc', 'b');
 * // => false
 *
 * _.endsWith('abc', 'b', 2);
 * // => true
 */
function endsWith(string, target, position) {
  string = toString(string);
  target = baseToString(target);

  var length = string.length;
  position = position === undefined
    ? length
    : baseClamp(toInteger(position), 0, length);

  var end = position;
  position -= target.length;
  return position >= 0 && string.slice(position, end) == target;
}

module.exports = endsWith;


/***/ }),

/***/ "./node_modules/lodash/findKey.js":
/*!****************************************!*\
  !*** ./node_modules/lodash/findKey.js ***!
  \****************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var baseFindKey = __webpack_require__(/*! ./_baseFindKey */ "./node_modules/lodash/_baseFindKey.js"),
    baseForOwn = __webpack_require__(/*! ./_baseForOwn */ "./node_modules/lodash/_baseForOwn.js"),
    baseIteratee = __webpack_require__(/*! ./_baseIteratee */ "./node_modules/lodash/_baseIteratee.js");

/**
 * This method is like `_.find` except that it returns the key of the first
 * element `predicate` returns truthy for instead of the element itself.
 *
 * @static
 * @memberOf _
 * @since 1.1.0
 * @category Object
 * @param {Object} object The object to inspect.
 * @param {Function} [predicate=_.identity] The function invoked per iteration.
 * @returns {string|undefined} Returns the key of the matched element,
 *  else `undefined`.
 * @example
 *
 * var users = {
 *   'barney':  { 'age': 36, 'active': true },
 *   'fred':    { 'age': 40, 'active': false },
 *   'pebbles': { 'age': 1,  'active': true }
 * };
 *
 * _.findKey(users, function(o) { return o.age < 40; });
 * // => 'barney' (iteration order is not guaranteed)
 *
 * // The `_.matches` iteratee shorthand.
 * _.findKey(users, { 'age': 1, 'active': true });
 * // => 'pebbles'
 *
 * // The `_.matchesProperty` iteratee shorthand.
 * _.findKey(users, ['active', false]);
 * // => 'fred'
 *
 * // The `_.property` iteratee shorthand.
 * _.findKey(users, 'active');
 * // => 'barney'
 */
function findKey(object, predicate) {
  return baseFindKey(object, baseIteratee(predicate, 3), baseForOwn);
}

module.exports = findKey;


/***/ }),

/***/ "./node_modules/lodash/forIn.js":
/*!**************************************!*\
  !*** ./node_modules/lodash/forIn.js ***!
  \**************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var baseFor = __webpack_require__(/*! ./_baseFor */ "./node_modules/lodash/_baseFor.js"),
    castFunction = __webpack_require__(/*! ./_castFunction */ "./node_modules/lodash/_castFunction.js"),
    keysIn = __webpack_require__(/*! ./keysIn */ "./node_modules/lodash/keysIn.js");

/**
 * Iterates over own and inherited enumerable string keyed properties of an
 * object and invokes `iteratee` for each property. The iteratee is invoked
 * with three arguments: (value, key, object). Iteratee functions may exit
 * iteration early by explicitly returning `false`.
 *
 * @static
 * @memberOf _
 * @since 0.3.0
 * @category Object
 * @param {Object} object The object to iterate over.
 * @param {Function} [iteratee=_.identity] The function invoked per iteration.
 * @returns {Object} Returns `object`.
 * @see _.forInRight
 * @example
 *
 * function Foo() {
 *   this.a = 1;
 *   this.b = 2;
 * }
 *
 * Foo.prototype.c = 3;
 *
 * _.forIn(new Foo, function(value, key) {
 *   console.log(key);
 * });
 * // => Logs 'a', 'b', then 'c' (iteration order is not guaranteed).
 */
function forIn(object, iteratee) {
  return object == null
    ? object
    : baseFor(object, castFunction(iteratee), keysIn);
}

module.exports = forIn;


/***/ }),

/***/ "./node_modules/lodash/toLower.js":
/*!****************************************!*\
  !*** ./node_modules/lodash/toLower.js ***!
  \****************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var toString = __webpack_require__(/*! ./toString */ "./node_modules/lodash/toString.js");

/**
 * Converts `string`, as a whole, to lower case just like
 * [String#toLowerCase](https://mdn.io/toLowerCase).
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category String
 * @param {string} [string=''] The string to convert.
 * @returns {string} Returns the lower cased string.
 * @example
 *
 * _.toLower('--Foo-Bar--');
 * // => '--foo-bar--'
 *
 * _.toLower('fooBar');
 * // => 'foobar'
 *
 * _.toLower('__FOO_BAR__');
 * // => '__foo_bar__'
 */
function toLower(value) {
  return toString(value).toLowerCase();
}

module.exports = toLower;


/***/ }),

/***/ "./src/app/common/validators/index.ts":
/*!********************************************!*\
  !*** ./src/app/common/validators/index.ts ***!
  \********************************************/
/*! exports provided: isUnique */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _is_unique_validator__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./is-unique.validator */ "./src/app/common/validators/is-unique.validator.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "isUnique", function() { return _is_unique_validator__WEBPACK_IMPORTED_MODULE_0__["isUnique"]; });




/***/ }),

/***/ "./src/app/common/validators/is-unique.validator.ts":
/*!**********************************************************!*\
  !*** ./src/app/common/validators/is-unique.validator.ts ***!
  \**********************************************************/
/*! exports provided: isUnique */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "isUnique", function() { return isUnique; });
/* harmony import */ var lodash_trim__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! lodash/trim */ "./node_modules/lodash/trim.js");
/* harmony import */ var lodash_trim__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(lodash_trim__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");



var DEBOUNCE_TIME = 500;
/**
 * isUnique valiator checks if the value in the input is unique
 * @param isDuplicateFn function to check if the input value already exists or not
 * @param value2Params function to get the parameters needed for the isDuplicateFn
 *                     if it needs other parameters besides the input value
 * @param oldValue the old value of the input, for example when editing an object, the value that is already
 *                 there should not be checked if it already exists
 */
var isUnique = function (isDupicateFn, value2Params, oldValue) {
    if (value2Params === void 0) { value2Params = function (val) { return val; }; }
    return function (thisControl) {
        return Object(rxjs__WEBPACK_IMPORTED_MODULE_1__["timer"])(DEBOUNCE_TIME).pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_2__["switchMap"])(function () {
            var value = lodash_trim__WEBPACK_IMPORTED_MODULE_0__(thisControl.value);
            if (!value || (oldValue && oldValue === value)) {
                return Object(rxjs__WEBPACK_IMPORTED_MODULE_1__["of"])(null);
            }
            var errorObject = { isUnique: true };
            var params = value2Params(value);
            return isDupicateFn(params).pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_2__["map"])(function (isDuplicate) { return isDuplicate ? errorObject : null; }));
        }));
    };
};


/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/create-datasets.component.html":
/*!*********************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/create-datasets.component.html ***!
  \*********************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<mat-card fxFlex fxFill fxLayout=\"row\">\n  <mat-horizontal-stepper linear=\"true\" (selectionChange)=\"stepChanged($event)\">\n    <ng-template matStepperIcon=\"edit\">\n      <mat-icon fontIcon=\"icon-checkmark\"></mat-icon>\n    </ng-template>\n    <ng-template matStepperIcon=\"done\">\n      <mat-icon fontIcon=\"icon-checkmark\"></mat-icon>\n    </ng-template>\n    <mat-step e2e=\"create-dataset-first-window\" [completed]=\"selectFullfilled\" label=\"SELECT\">\n      <select-rawdata (onSelectFullfilled)=\"markSelectDone($event)\"></select-rawdata>\n      <div fxLayout=\"row\" fxLayoutGap=\"20px\">\n        <button e2e=\"first-window-stepperCancel\" mat-stroked-button (click)=\"backtoLists()\" color=\"warn\" class=\"stepperBtn\">\n          <span i18n>Cancel</span>\n        </button>\n        <div fxFlex></div>\n        <button mat-raised-button e2e=\"first-window-stepperFwd\" color=\"primary\" matStepperNext class=\"stepperBtn\">\n          <span i18n>Next</span>\n          <mat-icon fontIcon=\"icon-right-arrow\"></mat-icon>\n        </button>\n      </div>\n    </mat-step>\n    <mat-step e2e=\"create-dataset-second-window\" [completed]=\"detailsFilled\" label=\"DETAILS\">\n      <dataset-details #detailsComponent [selFiles]=\"selectedFiles\" [previewConfig]=\"csvConfig\" (onDetailsFilled)=\"markDetailsDone($event)\"></dataset-details>\n      <div fxLayout=\"row\" fxLayoutGap=\"20px\">\n        <button e2e=\"second-window-stepperCancel\" mat-stroked-button (click)=\"backtoLists()\" color=\"warn\" class=\"stepperBtn\">\n          <span i18n>Cancel</span>\n        </button>\n        <div fxFlex></div>\n        <button mat-stroked-button e2e=\"second-window-stepperPrev\" color=\"primary\" matStepperPrevious class=\"stepperBtn previous \">\n          <mat-icon fontIcon=\"icon-back\"></mat-icon>\n          <span i18n>Previous</span>\n        </button>\n        <button mat-raised-button e2e=\"second-window-stepperFwd\" color=\"primary\" matStepperNext class=\"stepperBtn\">\n          <span i18n> Next</span>\n          <mat-icon fontIcon=\"icon-right-arrow\"></mat-icon>\n        </button>\n      </div>\n    </mat-step>\n    <mat-step e2e=\"create-dataset-third-window\" [completed]=\"previewDone\" label=\"PREVIEW\">\n      <parser-preview *ngIf=\"selectedIndex === 2 || selectedIndex === 3\" [previewObj]=\"parsedPreview\" #previewComponent\n        (parserConfig)=\"getParserConfig($event)\"></parser-preview>\n      <div fxLayout=\"row\" fxLayoutGap=\"20px\">\n        <button e2e=\"third-window-stepperCancel\" mat-stroked-button (click)=\"backtoLists()\" color=\"warn\" class=\"stepperBtn\">\n          <span i18n>Cancel</span>\n        </button>\n        <div fxFlex></div>\n        <button e2e=\"third-window-stepperPrev\" mat-stroked-button matStepperPrevious color=\"primary\" class=\"stepperBtn previous\">\n          <mat-icon fontIcon=\"icon-back\"></mat-icon>\n          <span i18n>Previous</span>\n        </button>\n        <button e2e=\"third-window-stepperFwd\" mat-raised-button color=\"primary\" matStepperNext class=\"stepperBtn\">\n          <span i18n>Next</span>\n          <mat-icon fontIcon=\"icon-right-arrow\"></mat-icon>\n        </button>\n      </div>\n    </mat-step>\n    <mat-step e2e=\"create-dataset-fourth-window\" label=\"ADD_DATASET\" [stepControl]=\"nameFormGroup\">\n      <div fxLayout.gt-sm=\"row\" fxLayoutGap=\"20px\" class=\"data-details\">\n        <div fxFlex=\"35\" fxShow fxHide.xs=\"true\" class=\"list-margin1\">\n          <mat-list>\n            <h3 class=\"list-header\" mat-subheader i18n>Selected Files</h3>\n            <mat-list-item class=\"list-items\" *ngFor=\"let file of selectedFiles; index as i\" (click)=\"previewDialog(file)\">\n              <p mat-line>{{i + 1}}. &ensp; {{ file.name }}</p>\n              <div class=\"icon-div\">\n                <mat-icon mat-list-icon class=\"preview-icon\" fontIcon=\"icon-show\"></mat-icon>\n              </div>\n              <mat-divider></mat-divider>\n            </mat-list-item>\n          </mat-list>\n        </div>\n        <mat-card fxFlex class=\"top-margin\">\n          <mat-card-header class=\"headerGradient\">\n            <div class=\"mat-body-1\" i18n>Describe Your Dataset</div>\n          </mat-card-header>\n          <mat-card-content>\n            <div class=\"form-div\">\n              <form [formGroup]=\"nameFormGroup\">\n                <section class=\"input-section\">\n                  <mat-form-field class=\"margin20\">\n                    <input matInput placeholder=\"Dataset Name\" e2e=\"dataset-name\" name=\"datasetName\" formControlName=\"nameControl\"\n                      maxlength=\"18\" required />\n                    <mat-error *ngIf=\"nameFormGroup.controls.nameControl.hasError('required')\" i18n>\n                      Dataset Name is\n                      <strong>required</strong>\n                    </mat-error>\n                    <mat-error *ngIf=\"nameFormGroup.controls.nameControl.hasError('pattern')\" i18n>\n                      <strong> Only alphabets, numbers are allowed\n                      </strong>\n                    </mat-error>\n                    <mat-hint> Only alphabets, numbers are allowed </mat-hint>\n                  </mat-form-field>\n                  <!-- <div class=\"margin20\">\n                    <mat-radio-group labelPosition=\"before\" [(ngModel)]=\"parserConf.outputs[0].mode\" [ngModelOptions]=\"{standalone: true}\">\n                      <mat-radio-button value=\"replace\">\n                        Replace\n                      </mat-radio-button>\n                      <mat-radio-button value=\"append\">\n                        Append\n                      </mat-radio-button>\n                    </mat-radio-group>\n                  </div> -->\n                </section>\n                <section class=\"input-section\">\n                  <mat-form-field class=\"margin20\">\n                    <input matInput placeholder=\"Description\" name=\"description\" e2e=\"dataset-desc\" formControlName=\"descControl\"\n                      maxlength=\"50\" required />\n                    <mat-error *ngIf=\"nameFormGroup.controls.descControl.hasError('required')\" i18n>\n                      Dataset description is\n                      <strong>required</strong>\n                    </mat-error>\n                  </mat-form-field>\n                </section>\n                <!-- <mat-divider></mat-divider>\n                <section class=\"input-section\">\n                  <mat-form-field class=\"margin20\">\n                    <mat-select placeholder=\"SPARK_MASTER\" [(value)]=\"parserConf.parameters[0].value\">\n                      <mat-option value=\"yarn\">Yarn</mat-option>\n                      <mat-option value=\"standard\" disabled>Standard</mat-option>\n                    </mat-select>\n                  </mat-form-field>\n                  <div class=\"margin20\">\n                    <span fxFlex=\"20\">Spark Instances</span>\n                    <mat-slider matInput fxFlex name=\"sparkInstances\" class=\"margin20\" max=\"10\" min=\"2\" step=\"1\" thumb-label=\"true\" tick-interval=\"1\"\n                      [(ngModel)]=\"parserConf.parameters[1].value\" [ngModelOptions]=\"{standalone: true}\">\n                    </mat-slider>\n                    <span>{{parserConf.parameters[1].value}}</span>\n                  </div>\n                </section> -->\n              </form>\n            </div>\n          </mat-card-content>\n        </mat-card>\n      </div>\n      <div fxLayout=\"row\" fxLayoutGap=\"20px\">\n        <button e2e=\"fourth-window-stepperCancel\" mat-stroked-button (click)=\"backtoLists()\" class=\"stepperBtn Outline\">\n          <span i18n>Cancel</span>\n        </button>\n        <div fxFlex></div>\n        <button e2e=\"fourth-window-stepperPrev\" mat-stroked-button color=\"primary\" matStepperPrevious class=\"stepperBtn previous\">\n          <mat-icon fontIcon=\"icon-back\"></mat-icon>\n          <span i18n>Previous</span>\n        </button>\n        <button e2e=\"fourth-window-stepperAdd\" mat-raised-button color=\"primary\" matStepperNext [disabled]=\"!nameFormGroup.valid\"\n          (click)=\"triggerParser()\" class=\"stepperBtn\">\n          <span i18n>ADD</span>\n          <mat-icon fontIcon=\"icon-right-arrow\"></mat-icon>\n        </button>\n      </div>\n    </mat-step>\n  </mat-horizontal-stepper>\n</mat-card>"

/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/create-datasets.component.scss":
/*!*********************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/create-datasets.component.scss ***!
  \*********************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%;\n  height: 100%;\n  display: flex;\n  flex-direction: column; }\n\nmat-icon {\n  vertical-align: text-bottom !important; }\n\nmat-dialog-content {\n  height: 100%;\n  background-color: #f5f9fc;\n  padding: 3px !important;\n  margin: 0 !important;\n  max-height: 100% !important; }\n\n::ng-deep .mat-horizontal-content-container {\n  padding: 0 !important;\n  height: calc(100% - 56px); }\n\n::ng-deep .mat-horizontal-stepper-header-container {\n  background-color: #f5f9fc;\n  height: 55px; }\n\n::ng-deep .mat-horizontal-stepper-content {\n  height: 100%;\n  padding: 1px; }\n\n::ng-deep .mat-stepper-horizontal {\n  height: 100%;\n  width: 100% !important;\n  overflow: hidden; }\n\n.stepperBtn {\n  margin-top: 3px;\n  margin-right: 5px;\n  font-size: 20px;\n  width: 136px; }\n\n.stepperBtn .mat-icon {\n    height: auto; }\n\n.Outline {\n  color: #ab0e27; }\n\n.xBtn {\n  color: #ab0e27; }\n\n.whiteFrame {\n  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.2), 0 1px 1px 0 rgba(0, 0, 0, 0.14), 0 2px 1px -1px rgba(0, 0, 0, 0.12);\n  background-color: white; }\n\n.file-icon {\n  color: #73b421; }\n\n.preview-icon {\n  font-size: 18px;\n  color: #0077be; }\n\n.mat-card {\n  padding: 0 !important; }\n\n.margin20 {\n  margin: 3px;\n  width: 100%; }\n\n.input-section {\n  display: flex;\n  align-content: center;\n  align-items: center;\n  height: 9vh; }\n\n.headerGradient {\n  padding: 8px 15px;\n  background-color: white;\n  border-top-left-radius: 3px;\n  border-top-right-radius: 3px; }\n\n.headerText {\n  margin: 9px !important;\n  font-size: 16px !important; }\n\n.form-div {\n  padding: 1% 9% 0; }\n\n.list-header {\n  background-color: #0077be;\n  font-size: 16px !important;\n  border-radius: 12px 12px 0 0;\n  color: white !important; }\n\n.list-items {\n  background-color: white;\n  border-radius: 0 3px 3px;\n  border: #0077be 0.5px dotted; }\n\n.list-items:hover {\n    box-shadow: 0 11px 15px -7px rgba(0, 0, 0, 0.2), 0 24px 38px 3px rgba(0, 0, 0, 0.14), 0 9px 46px 8px rgba(0, 0, 0, 0.12) !important;\n    cursor: pointer; }\n\n.data-details {\n  height: calc(100% - 50px); }\n\n.top-margin {\n  margin-top: 3px; }\n\n.mat-horizontal-stepper-header {\n  height: 55px; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2NyZWF0ZS1kYXRhc2V0cy9jcmVhdGUtZGF0YXNldHMuY29tcG9uZW50LnNjc3MiLCIvVXNlcnMvYmFybmFtdW10eWFuL1Byb2plY3RzL21vZHVzL3NpcC9zYXctd2ViL3NyYy90aGVtZXMvYmFzZS9fY29sb3JzLnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSxXQUFXO0VBQ1gsWUFBWTtFQUNaLGFBQWE7RUFDYixzQkFBc0IsRUFBQTs7QUFHeEI7RUFDRSxzQ0FBc0MsRUFBQTs7QUFHeEM7RUFDRSxZQUFZO0VBQ1oseUJDZ0N3QjtFRC9CeEIsdUJBQXVCO0VBQ3ZCLG9CQUFvQjtFQUNwQiwyQkFBMkIsRUFBQTs7QUFHN0I7RUFDRSxxQkFBcUI7RUFDckIseUJBQXlCLEVBQUE7O0FBRzNCO0VBQ0UseUJDb0J3QjtFRG5CeEIsWUFBWSxFQUFBOztBQUdkO0VBQ0UsWUFBWTtFQUNaLFlBQVksRUFBQTs7QUFHZDtFQUNFLFlBQVk7RUFDWixzQkFBc0I7RUFDdEIsZ0JBQWdCLEVBQUE7O0FBR2xCO0VBQ0UsZUFBZTtFQUNmLGlCQUFpQjtFQUNqQixlQUFlO0VBQ2YsWUFBWSxFQUFBOztBQUpkO0lBT0ksWUFBWSxFQUFBOztBQUloQjtFQUNFLGNDOUJtQixFQUFBOztBRGlDckI7RUFDRSxjQ2xDbUIsRUFBQTs7QURxQ3JCO0VBQ0UsK0dBQStHO0VBQy9HLHVCQUF1QixFQUFBOztBQUd6QjtFQUNFLGNDMUNxQixFQUFBOztBRDZDdkI7RUFDRSxlQUFlO0VBQ2YsY0N0RXVCLEVBQUE7O0FEeUV6QjtFQUNFLHFCQUFxQixFQUFBOztBQUd2QjtFQUNFLFdBQVc7RUFDWCxXQUFXLEVBQUE7O0FBR2I7RUFDRSxhQUFhO0VBQ2IscUJBQXFCO0VBQ3JCLG1CQUFtQjtFQUNuQixXQUFXLEVBQUE7O0FBR2I7RUFDRSxpQkFBaUI7RUFDakIsdUJBQXVCO0VBQ3ZCLDJCQUEyQjtFQUMzQiw0QkFBNEIsRUFBQTs7QUFHOUI7RUFDRSxzQkFBc0I7RUFDdEIsMEJBQTBCLEVBQUE7O0FBRzVCO0VBQ0UsZ0JBQWdCLEVBQUE7O0FBR2xCO0VBQ0UseUJDMUd1QjtFRDJHdkIsMEJBQTBCO0VBQzFCLDRCQUE0QjtFQUM1Qix1QkFBdUIsRUFBQTs7QUFHekI7RUFDRSx1QkFBdUI7RUFDdkIsd0JBQXdCO0VBQ3hCLDRCQUFxQyxFQUFBOztBQUh2QztJQU1JLG1JQUFtSTtJQUNuSSxlQUFlLEVBQUE7O0FBSW5CO0VBQ0UseUJBQXlCLEVBQUE7O0FBRzNCO0VBQ0UsZUFBZSxFQUFBOztBQUdqQjtFQUNFLFlBQVksRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvd29ya2JlbmNoL2NvbXBvbmVudHMvY3JlYXRlLWRhdGFzZXRzL2NyZWF0ZS1kYXRhc2V0cy5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgXCJzcmMvdGhlbWVzL2Jhc2UvY29sb3JzXCI7XG5cbjpob3N0IHtcbiAgd2lkdGg6IDEwMCU7XG4gIGhlaWdodDogMTAwJTtcbiAgZGlzcGxheTogZmxleDtcbiAgZmxleC1kaXJlY3Rpb246IGNvbHVtbjtcbn1cblxubWF0LWljb24ge1xuICB2ZXJ0aWNhbC1hbGlnbjogdGV4dC1ib3R0b20gIWltcG9ydGFudDtcbn1cblxubWF0LWRpYWxvZy1jb250ZW50IHtcbiAgaGVpZ2h0OiAxMDAlO1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAkYmFja2dyb3VuZC1jb2xvcjtcbiAgcGFkZGluZzogM3B4ICFpbXBvcnRhbnQ7XG4gIG1hcmdpbjogMCAhaW1wb3J0YW50O1xuICBtYXgtaGVpZ2h0OiAxMDAlICFpbXBvcnRhbnQ7XG59XG5cbjo6bmctZGVlcCAubWF0LWhvcml6b250YWwtY29udGVudC1jb250YWluZXIge1xuICBwYWRkaW5nOiAwICFpbXBvcnRhbnQ7XG4gIGhlaWdodDogY2FsYygxMDAlIC0gNTZweCk7XG59XG5cbjo6bmctZGVlcCAubWF0LWhvcml6b250YWwtc3RlcHBlci1oZWFkZXItY29udGFpbmVyIHtcbiAgYmFja2dyb3VuZC1jb2xvcjogJGJhY2tncm91bmQtY29sb3I7XG4gIGhlaWdodDogNTVweDtcbn1cblxuOjpuZy1kZWVwIC5tYXQtaG9yaXpvbnRhbC1zdGVwcGVyLWNvbnRlbnQge1xuICBoZWlnaHQ6IDEwMCU7XG4gIHBhZGRpbmc6IDFweDtcbn1cblxuOjpuZy1kZWVwIC5tYXQtc3RlcHBlci1ob3Jpem9udGFsIHtcbiAgaGVpZ2h0OiAxMDAlO1xuICB3aWR0aDogMTAwJSAhaW1wb3J0YW50O1xuICBvdmVyZmxvdzogaGlkZGVuO1xufVxuXG4uc3RlcHBlckJ0biB7XG4gIG1hcmdpbi10b3A6IDNweDtcbiAgbWFyZ2luLXJpZ2h0OiA1cHg7XG4gIGZvbnQtc2l6ZTogMjBweDtcbiAgd2lkdGg6IDEzNnB4O1xuXG4gIC5tYXQtaWNvbiB7XG4gICAgaGVpZ2h0OiBhdXRvO1xuICB9XG59XG5cbi5PdXRsaW5lIHtcbiAgY29sb3I6ICRwcmltYXJ5LXJlZDtcbn1cblxuLnhCdG4ge1xuICBjb2xvcjogJHByaW1hcnktcmVkO1xufVxuXG4ud2hpdGVGcmFtZSB7XG4gIGJveC1zaGFkb3c6IDAgMXB4IDNweCAwIHJnYmEoMCwgMCwgMCwgMC4yKSwgMCAxcHggMXB4IDAgcmdiYSgwLCAwLCAwLCAwLjE0KSwgMCAycHggMXB4IC0xcHggcmdiYSgwLCAwLCAwLCAwLjEyKTtcbiAgYmFja2dyb3VuZC1jb2xvcjogd2hpdGU7XG59XG5cbi5maWxlLWljb24ge1xuICBjb2xvcjogJHByaW1hcnktZ3JlZW47XG59XG5cbi5wcmV2aWV3LWljb24ge1xuICBmb250LXNpemU6IDE4cHg7XG4gIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIyO1xufVxuXG4ubWF0LWNhcmQge1xuICBwYWRkaW5nOiAwICFpbXBvcnRhbnQ7XG59XG5cbi5tYXJnaW4yMCB7XG4gIG1hcmdpbjogM3B4O1xuICB3aWR0aDogMTAwJTtcbn1cblxuLmlucHV0LXNlY3Rpb24ge1xuICBkaXNwbGF5OiBmbGV4O1xuICBhbGlnbi1jb250ZW50OiBjZW50ZXI7XG4gIGFsaWduLWl0ZW1zOiBjZW50ZXI7XG4gIGhlaWdodDogOXZoO1xufVxuXG4uaGVhZGVyR3JhZGllbnQge1xuICBwYWRkaW5nOiA4cHggMTVweDtcbiAgYmFja2dyb3VuZC1jb2xvcjogd2hpdGU7XG4gIGJvcmRlci10b3AtbGVmdC1yYWRpdXM6IDNweDtcbiAgYm9yZGVyLXRvcC1yaWdodC1yYWRpdXM6IDNweDtcbn1cblxuLmhlYWRlclRleHQge1xuICBtYXJnaW46IDlweCAhaW1wb3J0YW50O1xuICBmb250LXNpemU6IDE2cHggIWltcG9ydGFudDtcbn1cblxuLmZvcm0tZGl2IHtcbiAgcGFkZGluZzogMSUgOSUgMDtcbn1cblxuLmxpc3QtaGVhZGVyIHtcbiAgYmFja2dyb3VuZC1jb2xvcjogJHByaW1hcnktYmx1ZS1iMjtcbiAgZm9udC1zaXplOiAxNnB4ICFpbXBvcnRhbnQ7XG4gIGJvcmRlci1yYWRpdXM6IDEycHggMTJweCAwIDA7XG4gIGNvbG9yOiB3aGl0ZSAhaW1wb3J0YW50O1xufVxuXG4ubGlzdC1pdGVtcyB7XG4gIGJhY2tncm91bmQtY29sb3I6IHdoaXRlO1xuICBib3JkZXItcmFkaXVzOiAwIDNweCAzcHg7XG4gIGJvcmRlcjogJHByaW1hcnktYmx1ZS1iMiAwLjVweCBkb3R0ZWQ7XG5cbiAgJjpob3ZlciB7XG4gICAgYm94LXNoYWRvdzogMCAxMXB4IDE1cHggLTdweCByZ2JhKDAsIDAsIDAsIDAuMiksIDAgMjRweCAzOHB4IDNweCByZ2JhKDAsIDAsIDAsIDAuMTQpLCAwIDlweCA0NnB4IDhweCByZ2JhKDAsIDAsIDAsIDAuMTIpICFpbXBvcnRhbnQ7XG4gICAgY3Vyc29yOiBwb2ludGVyO1xuICB9XG59XG5cbi5kYXRhLWRldGFpbHMge1xuICBoZWlnaHQ6IGNhbGMoMTAwJSAtIDUwcHgpO1xufVxuXG4udG9wLW1hcmdpbiB7XG4gIG1hcmdpbi10b3A6IDNweDtcbn1cblxuLm1hdC1ob3Jpem9udGFsLXN0ZXBwZXItaGVhZGVyIHtcbiAgaGVpZ2h0OiA1NXB4O1xufVxuIiwiLy8gQnJhbmRpbmcgY29sb3JzXG4kcHJpbWFyeS1ibHVlLWIxOiAjMWE4OWQ0O1xuJHByaW1hcnktYmx1ZS1iMjogIzAwNzdiZTtcbiRwcmltYXJ5LWJsdWUtYjM6ICMyMDZiY2U7XG4kcHJpbWFyeS1ibHVlLWI0OiAjMWQzYWIyO1xuXG4kcHJpbWFyeS1ob3Zlci1ibHVlOiAjMWQ2MWIxO1xuJGdyaWQtaG92ZXItY29sb3I6ICNmNWY5ZmM7XG4kZ3JpZC1oZWFkZXItYmctY29sb3I6ICNkN2VhZmE7XG4kZ3JpZC1oZWFkZXItY29sb3I6ICMwYjRkOTk7XG4kZ3JpZC10ZXh0LWNvbG9yOiAjNDY0NjQ2O1xuJGdyZXktdGV4dC1jb2xvcjogIzYzNjM2MztcblxuJHNlbGVjdGlvbi1oaWdobGlnaHQtY29sOiByZ2JhKDAsIDE0MCwgMjYwLCAwLjIpO1xuJHByaW1hcnktZ3JleS1nMTogI2QxZDNkMztcbiRwcmltYXJ5LWdyZXktZzI6ICM5OTk7XG4kcHJpbWFyeS1ncmV5LWczOiAjNzM3MzczO1xuJHByaW1hcnktZ3JleS1nNDogIzVjNjY3MDtcbiRwcmltYXJ5LWdyZXktZzU6ICMzMTMxMzE7XG4kcHJpbWFyeS1ncmV5LWc2OiAjZjVmNWY1O1xuJHByaW1hcnktZ3JleS1nNzogIzNkM2QzZDtcblxuJHByaW1hcnktd2hpdGU6ICNmZmY7XG4kcHJpbWFyeS1ibGFjazogIzAwMDtcbiRwcmltYXJ5LXJlZDogI2FiMGUyNztcbiRwcmltYXJ5LWdyZWVuOiAjNzNiNDIxO1xuJHByaW1hcnktb3JhbmdlOiAjZjA3NjAxO1xuXG4kc2Vjb25kYXJ5LWdyZWVuOiAjNmZiMzIwO1xuJHNlY29uZGFyeS15ZWxsb3c6ICNmZmJlMDA7XG4kc2Vjb25kYXJ5LW9yYW5nZTogI2ZmOTAwMDtcbiRzZWNvbmRhcnktcmVkOiAjZDkzZTAwO1xuJHNlY29uZGFyeS1iZXJyeTogI2FjMTQ1YTtcbiRzZWNvbmRhcnktcHVycGxlOiAjOTE0MTkxO1xuXG4kc3RyaW5nLXR5cGUtY29sb3I6ICM0OTk1YjI7XG4kbnVtYmVyLXR5cGUtY29sb3I6ICMwMGIxODA7XG4kZ2VvLXR5cGUtY29sb3I6ICM4NDVlYzI7XG4kZGF0ZS10eXBlLWNvbG9yOiAjZDE5NjIxO1xuXG4kdHlwZS1jaGlwLW9wYWNpdHk6IDE7XG4kc3RyaW5nLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkc3RyaW5nLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kbnVtYmVyLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkbnVtYmVyLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZ2VvLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZ2VvLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZGF0ZS10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGRhdGUtdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcblxuJHJlcG9ydC1kZXNpZ25lci1zZXR0aW5ncy1iZy1jb2xvcjogI2Y1ZjlmYztcbiRiYWNrZ3JvdW5kLWNvbG9yOiAjZjVmOWZjO1xuIl19 */"

/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/create-datasets.component.ts":
/*!*******************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/create-datasets.component.ts ***!
  \*******************************************************************************************/
/*! exports provided: CreateDatasetsComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CreateDatasetsComponent", function() { return CreateDatasetsComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/cloneDeep */ "./node_modules/lodash/cloneDeep.js");
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! lodash/isUndefined */ "./node_modules/lodash/isUndefined.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(lodash_isUndefined__WEBPACK_IMPORTED_MODULE_7__);
/* harmony import */ var _wb_comp_configs__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../../wb-comp-configs */ "./src/app/modules/workbench/wb-comp-configs.ts");
/* harmony import */ var _parser_preview_parser_preview_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./parser-preview/parser-preview.component */ "./src/app/modules/workbench/components/create-datasets/parser-preview/parser-preview.component.ts");
/* harmony import */ var _dataset_details_dataset_details_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./dataset-details/dataset-details.component */ "./src/app/modules/workbench/components/create-datasets/dataset-details/dataset-details.component.ts");
/* harmony import */ var _rawpreview_dialog_rawpreview_dialog_component__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ./rawpreview-dialog/rawpreview-dialog.component */ "./src/app/modules/workbench/components/create-datasets/rawpreview-dialog/rawpreview-dialog.component.ts");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");
/* harmony import */ var _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ../../../../common/services/toastMessage.service */ "./src/app/common/services/toastMessage.service.ts");














var CreateDatasetsComponent = /** @class */ (function () {
    function CreateDatasetsComponent(router, dialog, workBench, notify) {
        this.router = router;
        this.dialog = dialog;
        this.workBench = workBench;
        this.notify = notify;
        this.selectFullfilled = false;
        this.detailsFilled = false;
        this.previewDone = false;
        this.details = [];
        this.parsedPreview = new rxjs__WEBPACK_IMPORTED_MODULE_6__["BehaviorSubject"]([]);
        this.selectedIndex = 0;
        this.folNamePattern = '[A-Za-z0-9]+';
    }
    CreateDatasetsComponent.prototype.ngOnInit = function () {
        this.csvConfig = lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_3__(_wb_comp_configs__WEBPACK_IMPORTED_MODULE_8__["CSV_CONFIG"]);
        this.parserConf = lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_3__(_wb_comp_configs__WEBPACK_IMPORTED_MODULE_8__["PARSER_CONFIG"]);
        this.nameFormGroup = new _angular_forms__WEBPACK_IMPORTED_MODULE_2__["FormGroup"]({
            nameControl: new _angular_forms__WEBPACK_IMPORTED_MODULE_2__["FormControl"]('', [
                _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].required,
                _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].pattern(this.folNamePattern),
                _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].minLength(3),
                _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].maxLength(25)
            ]),
            descControl: new _angular_forms__WEBPACK_IMPORTED_MODULE_2__["FormControl"]('', [
                _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].required,
                _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].minLength(5),
                _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].maxLength(99)
            ])
        });
    };
    CreateDatasetsComponent.prototype.stepChanged = function (event) {
        this.selectedIndex = event.selectedIndex;
        if (event.selectedIndex === 2 && event.previouslySelectedIndex !== 3) {
            this.detailsComponent.toPreview();
            this.previewDone = false;
            this.parsedPreview.next([]);
            this.getParsedPreview();
        }
        else if (event.selectedIndex === 3) {
            this.previewComponent.toAdd();
        }
        else if (event.selectedIndex === 2 &&
            event.previouslySelectedIndex === 3) {
            this.previewDone = true;
        }
    };
    CreateDatasetsComponent.prototype.markSelectDone = function (data) {
        this.selectFullfilled = data.selectFullfilled;
        this.selectedFiles = data.selectedFiles;
        this.csvConfig.file = data.filePath;
    };
    CreateDatasetsComponent.prototype.markDetailsDone = function (data) {
        this.detailsFilled = data.detailsFilled;
        this.details = data.details;
    };
    CreateDatasetsComponent.prototype.getParsedPreview = function () {
        var _this = this;
        if (this.selectedIndex === 2) {
            this.workBench.getParsedPreviewData(this.details).subscribe(function (data) {
                _this.previewData = data;
                if (!lodash_isUndefined__WEBPACK_IMPORTED_MODULE_7__(data.samplesParsed)) {
                    _this.previewDone = true;
                }
                else {
                    _this.previewDone = false;
                }
                _this.parsedPreview.next([_this.previewData, _this.details.file]);
            });
        }
    };
    CreateDatasetsComponent.prototype.getParserConfig = function (data) {
        this.fieldsConf = data;
    };
    CreateDatasetsComponent.prototype.previewDialog = function (fileDetails) {
        var _this = this;
        var path = fileDetails.path + "/" + fileDetails.name;
        this.workBench.getRawPreviewData(path).subscribe(function (data) {
            _this.dialog.open(_rawpreview_dialog_rawpreview_dialog_component__WEBPACK_IMPORTED_MODULE_11__["RawpreviewDialogComponent"], {
                minHeight: 500,
                minWidth: 600,
                data: {
                    title: fileDetails.name,
                    rawData: data.data
                }
            });
        });
    };
    CreateDatasetsComponent.prototype.triggerParser = function () {
        var _this = this;
        var payload = {
            name: this.nameFormGroup.value.nameControl,
            description: this.nameFormGroup.value.descControl,
            component: 'parser',
            configuration: {
                fields: this.fieldsConf.fields,
                file: this.fieldsConf.info.file,
                lineSeparator: this.fieldsConf.lineSeparator,
                delimiter: this.fieldsConf.delimiter,
                quoteChar: this.fieldsConf.quoteChar,
                quoteEscape: this.fieldsConf.quoteEscapeChar,
                headerSize: this.fieldsConf.headerSize
            }
        };
        this.parserConf.outputs[0].description = this.nameFormGroup.value.descControl;
        this.workBench.triggerParser(payload).subscribe(function (data) {
            _this.notify.info('Parser_triggered_successfully', 'Parsing', {
                hideDelay: 9000
            });
        });
        this.router.navigate(['workbench', 'dataobjects']);
    };
    CreateDatasetsComponent.prototype.backtoLists = function () {
        this.router.navigate(['workbench', 'dataobjects']);
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_4__["ViewChild"])('previewComponent'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _parser_preview_parser_preview_component__WEBPACK_IMPORTED_MODULE_9__["ParserPreviewComponent"])
    ], CreateDatasetsComponent.prototype, "previewComponent", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_4__["ViewChild"])('detailsComponent'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _dataset_details_dataset_details_component__WEBPACK_IMPORTED_MODULE_10__["DatasetDetailsComponent"])
    ], CreateDatasetsComponent.prototype, "detailsComponent", void 0);
    CreateDatasetsComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_4__["Component"])({
            selector: 'create-datasets',
            template: __webpack_require__(/*! ./create-datasets.component.html */ "./src/app/modules/workbench/components/create-datasets/create-datasets.component.html"),
            styles: [__webpack_require__(/*! ./create-datasets.component.scss */ "./src/app/modules/workbench/components/create-datasets/create-datasets.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_router__WEBPACK_IMPORTED_MODULE_5__["Router"],
            _angular_material__WEBPACK_IMPORTED_MODULE_1__["MatDialog"],
            _services_workbench_service__WEBPACK_IMPORTED_MODULE_12__["WorkbenchService"],
            _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_13__["ToastService"]])
    ], CreateDatasetsComponent);
    return CreateDatasetsComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/dataset-details/dataset-details.component.html":
/*!*************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/dataset-details/dataset-details.component.html ***!
  \*************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div fxLayout.gt-sm=\"row\" fxLayoutGap=\"20px\" class=\"data-details\">\n  <div fxFlex=\"35\" fxShow fxHide.xs=\"true\" class=\"list-margin1\">\n    <mat-list>\n      <h3 class=\"list-header\" mat-subheader>Selected Files</h3>\n      <mat-list-item class=\"list-items\" *ngFor=\"let file of selFiles; index as i\" (click)=\"previewDialog(file)\">\n        <p mat-line>{{i + 1}}. &ensp; {{ file.name }}</p>\n        <div class=\"icon-div\">\n          <mat-icon fontIcon=\"icon-show\" mat-list-icon class=\"preview-icon\"></mat-icon>\n        </div>\n        <mat-divider></mat-divider>\n      </mat-list-item>\n    </mat-list>\n  </div>\n  <mat-card fxFlex>\n    <mat-card-header class=\"headerGradient\">\n      <mat-card-title class=\"headerText\">Details</mat-card-title>\n    </mat-card-header>\n    <mat-card-content class=\"form-div\">\n      <form #datasetDetails novalidate [formGroup]=\"detailsFormGroup\" autocomplete=\"off\">\n        <section class=\"input-section\">\n          <mat-form-field class=\"margin20 select-form-field\" appearance=\"outline\">\n            <mat-label>Data format type?</mat-label>\n            <mat-select [(value)]=\"previewConfig.delimiterType\">\n              <mat-option value=\"delimited\">Delimited</mat-option>\n              <mat-option value=\"parquet\" disabled>Parquet</mat-option>\n            </mat-select>\n          </mat-form-field>\n          <mat-form-field class=\"margin20 select-form-field\" appearance=\"outline\">\n            <mat-label>Header row present?</mat-label>\n            <mat-select [(value)]=\"previewConfig.header\">\n              <mat-option value=\"yes\">Yes</mat-option>\n              <mat-option value=\"no\">No</mat-option>\n            </mat-select>\n          </mat-form-field>\n        </section>\n        <section class=\"input-section\">\n          <mat-form-field class=\"margin20\">\n            <input matInput placeholder=\"Field Seperator\" name=\"fieldSeperator\" formControlName=\"fieldSeperatorControl\" required />\n            <mat-error *ngIf=\"detailsFormGroup.controls.fieldSeperatorControl.hasError('required')\">\n              Field Seperator is\n              <strong>required</strong>\n            </mat-error>\n          </mat-form-field>\n          <mat-form-field class=\"margin20\">\n            <input matInput type=\"number\" placeholder=\"Header Size\" name=\"hederSize\" formControlName=\"hederSizeControl\" required />\n            <mat-error *ngIf=\"detailsFormGroup.controls.hederSizeControl.hasError('required')\">\n              Header Size is\n              <strong>required</strong>\n            </mat-error>\n          </mat-form-field>\n        </section>\n        <section class=\"input-section\">\n          <mat-form-field class=\"margin20\">\n            <input matInput placeholder=\"Field names Line\" name=\"fieldNamesLine\" formControlName=\"fieldNamesLineControl\" />\n            <mat-error *ngIf=\"detailsFormGroup.controls.fieldNamesLineControl.hasError('required')\">\n              Field names Line is\n              <strong>required</strong>\n            </mat-error>\n          </mat-form-field>\n          <mat-form-field class=\"margin20 select-form-field\" appearance=\"outline\">\n            <mat-label>Line Seperator</mat-label>\n            <mat-select [(value)]=\"lineSeperator\">\n              <mat-option value=\"lineFeed\">Line Feed (\\n)</mat-option>\n              <mat-option value=\"carriageReturn\">Carriage Return (\\r)</mat-option>\n              <mat-option value=\"eol\">End of Line (\\r\\n)</mat-option>\n            </mat-select>\n          </mat-form-field>\n        </section>\n        <section class=\"input-section\">\n          <mat-form-field class=\"margin20\">\n            <input matInput placeholder=\"Quote Character\" name=\"quoteChar\" formControlName=\"quoteCharControl\" />\n            <mat-error *ngIf=\"detailsFormGroup.controls.quoteCharControl.hasError('required')\">\n              Quote Character is\n              <strong>required</strong>\n            </mat-error>\n          </mat-form-field>\n          <mat-form-field class=\"margin20\">\n            <input matInput placeholder=\"Escape Character\" name=\"escapeChar\" formControlName=\"escapeCharControl\" />\n            <mat-error *ngIf=\"detailsFormGroup.controls.escapeCharControl.hasError('required')\">\n              Quote Character is\n              <strong>required</strong>\n            </mat-error>\n          </mat-form-field>\n        </section>\n        <section class=\"input-section\">\n          <mat-form-field class=\"margin20\">\n            <mat-chip-list #chipList>\n              <mat-chip *ngFor=\"let format of previewConfig.dateFormats\" [selectable]=\"selectable\" [removable]=\"true\" (removed)=\"removeFormat(format)\">\n                {{format}}\n                <mat-icon fontIcon=\"icon-remove\" matChipRemove></mat-icon>\n              </mat-chip>\n              <input placeholder=\"Date Formats\" [matChipInputFor]=\"chipList\" [matChipInputSeparatorKeyCodes]=\"separatorKeysCodes\" matChipInputAddOnBlur=\"true\"\n                (matChipInputTokenEnd)=\"addFormat($event)\" />\n            </mat-chip-list>\n          </mat-form-field>\n        </section>\n        <mat-divider></mat-divider>\n        <section class=\"input-section\">\n          <span fxFlex=\"20\">Sample Size</span>\n          <mat-slider fxFlex placeholder=\"Rows To Inspect\" name=\"rowsToInspect\" class=\"margin20\" max=\"1000\" min=\"50\" step=\"50\" thumb-label=\"true\"\n            tick-interval=\"500\" [(ngModel)]=\"previewConfig.rowsToInspect\" [ngModelOptions]=\"{standalone: true}\">\n          </mat-slider>\n          <span>{{previewConfig.rowsToInspect}}</span>\n        </section>\n        <!-- <section class=\"input-section\">\n            <span fxFlex=\"20\">Sample Size</span>\n            <mat-slider fxFlex placeholder=\"Sample Size\" name=\"sampleSize\" class=\"margin20\" max=\"200\" min=\"10\" step=\"10\" thumb-label=\"true\"\n              tick-interval=\"auto\" [(ngModel)]=\"previewConfig.sampleSize\" [ngModelOptions]=\"{standalone: true}\">\n            </mat-slider>\n            <span>{{previewConfig.sampleSize}}</span>\n          </section> -->\n      </form>\n    </mat-card-content>\n  </mat-card>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/dataset-details/dataset-details.component.scss":
/*!*************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/dataset-details/dataset-details.component.scss ***!
  \*************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%; }\n\n.data-details {\n  padding: 3px;\n  height: calc(100% - 47px);\n  background-color: #f5f9fc; }\n\n.toolbar-spacer {\n  flex: 1 1 auto; }\n\n.margin-9 {\n  margin: 3px 3px 0 0;\n  padding-bottom: 0; }\n\n.borderPrimary {\n  border-top-color: #0077be;\n  border-top-width: 1.6px; }\n\n.whiteFrame {\n  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.2), 0 1px 1px 0 rgba(0, 0, 0, 0.14), 0 2px 1px -1px rgba(0, 0, 0, 0.12);\n  background-color: white; }\n\n.file-icon {\n  color: #73b421; }\n\n.mat-card {\n  padding: 0 !important; }\n\n.margin20 {\n  margin: 3px;\n  width: 100%; }\n\n.input-section {\n  display: flex;\n  align-content: center;\n  align-items: center;\n  height: 9vh; }\n\n.headerGradient {\n  padding: 8px 15px;\n  background-color: white;\n  border-top-left-radius: 3px;\n  border-top-right-radius: 3px; }\n\n.headerText {\n  margin: 9px !important;\n  font-size: 16px !important; }\n\n.form-div {\n  padding: 3px 9% 0;\n  height: calc(100% - 59px);\n  overflow: auto; }\n\n.list-margin1 {\n  height: 100%;\n  overflow: auto; }\n\n.list-header {\n  background-color: #0077be;\n  font-size: 16px !important;\n  border-radius: 12px 12px 0 0;\n  color: white !important; }\n\n.list-items {\n  background-color: white;\n  border-radius: 0 3px 3px;\n  border: #0077be 0.5px dotted; }\n\n.list-items:hover {\n    box-shadow: 0 11px 15px -7px rgba(0, 0, 0, 0.2), 0 24px 38px 3px rgba(0, 0, 0, 0.14), 0 9px 46px 8px rgba(0, 0, 0, 0.12) !important;\n    cursor: pointer; }\n\n.preview-icon {\n  font-size: 18px;\n  color: #0077be; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2NyZWF0ZS1kYXRhc2V0cy9kYXRhc2V0LWRldGFpbHMvZGF0YXNldC1kZXRhaWxzLmNvbXBvbmVudC5zY3NzIiwiL1VzZXJzL2Jhcm5hbXVtdHlhbi9Qcm9qZWN0cy9tb2R1cy9zaXAvc2F3LXdlYi9zcmMvdGhlbWVzL2Jhc2UvX2NvbG9ycy5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUVBO0VBQ0UsV0FBVyxFQUFBOztBQUdiO0VBQ0UsWUFBWTtFQUNaLHlCQUF5QjtFQUN6Qix5QkNzQ3dCLEVBQUE7O0FEbkMxQjtFQUNFLGNBQWMsRUFBQTs7QUFHaEI7RUFDRSxtQkFBbUI7RUFDbkIsaUJBQWlCLEVBQUE7O0FBR25CO0VBQ0UseUJDcEJ1QjtFRHFCdkIsdUJBQXVCLEVBQUE7O0FBR3pCO0VBQ0UsK0dBQStHO0VBQy9HLHVCQUF1QixFQUFBOztBQUd6QjtFQUNFLGNDUHFCLEVBQUE7O0FEVXZCO0VBQ0UscUJBQXFCLEVBQUE7O0FBR3ZCO0VBQ0UsV0FBVztFQUNYLFdBQVcsRUFBQTs7QUFHYjtFQUNFLGFBQWE7RUFDYixxQkFBcUI7RUFDckIsbUJBQW1CO0VBQ25CLFdBQVcsRUFBQTs7QUFHYjtFQUNFLGlCQUFpQjtFQUNqQix1QkFBdUI7RUFDdkIsMkJBQTJCO0VBQzNCLDRCQUE0QixFQUFBOztBQUc5QjtFQUNFLHNCQUFzQjtFQUN0QiwwQkFBMEIsRUFBQTs7QUFHNUI7RUFDRSxpQkFBaUI7RUFDakIseUJBQXlCO0VBQ3pCLGNBQWMsRUFBQTs7QUFHaEI7RUFDRSxZQUFZO0VBQ1osY0FBYyxFQUFBOztBQUdoQjtFQUNFLHlCQ3pFdUI7RUQwRXZCLDBCQUEwQjtFQUMxQiw0QkFBNEI7RUFDNUIsdUJBQXVCLEVBQUE7O0FBR3pCO0VBQ0UsdUJBQXVCO0VBQ3ZCLHdCQUF3QjtFQUN4Qiw0QkFBcUMsRUFBQTs7QUFIdkM7SUFNSSxtSUFBbUk7SUFDbkksZUFBZSxFQUFBOztBQUluQjtFQUNFLGVBQWU7RUFDZixjQzVGdUIsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvd29ya2JlbmNoL2NvbXBvbmVudHMvY3JlYXRlLWRhdGFzZXRzL2RhdGFzZXQtZGV0YWlscy9kYXRhc2V0LWRldGFpbHMuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0IFwic3JjL3RoZW1lcy9iYXNlL2NvbG9yc1wiO1xuXG46aG9zdCB7XG4gIHdpZHRoOiAxMDAlO1xufVxuXG4uZGF0YS1kZXRhaWxzIHtcbiAgcGFkZGluZzogM3B4O1xuICBoZWlnaHQ6IGNhbGMoMTAwJSAtIDQ3cHgpO1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAkYmFja2dyb3VuZC1jb2xvcjtcbn1cblxuLnRvb2xiYXItc3BhY2VyIHtcbiAgZmxleDogMSAxIGF1dG87XG59XG5cbi5tYXJnaW4tOSB7XG4gIG1hcmdpbjogM3B4IDNweCAwIDA7XG4gIHBhZGRpbmctYm90dG9tOiAwO1xufVxuXG4uYm9yZGVyUHJpbWFyeSB7XG4gIGJvcmRlci10b3AtY29sb3I6ICRwcmltYXJ5LWJsdWUtYjI7XG4gIGJvcmRlci10b3Atd2lkdGg6IDEuNnB4O1xufVxuXG4ud2hpdGVGcmFtZSB7XG4gIGJveC1zaGFkb3c6IDAgMXB4IDNweCAwIHJnYmEoMCwgMCwgMCwgMC4yKSwgMCAxcHggMXB4IDAgcmdiYSgwLCAwLCAwLCAwLjE0KSwgMCAycHggMXB4IC0xcHggcmdiYSgwLCAwLCAwLCAwLjEyKTtcbiAgYmFja2dyb3VuZC1jb2xvcjogd2hpdGU7XG59XG5cbi5maWxlLWljb24ge1xuICBjb2xvcjogJHByaW1hcnktZ3JlZW47XG59XG5cbi5tYXQtY2FyZCB7XG4gIHBhZGRpbmc6IDAgIWltcG9ydGFudDtcbn1cblxuLm1hcmdpbjIwIHtcbiAgbWFyZ2luOiAzcHg7XG4gIHdpZHRoOiAxMDAlO1xufVxuXG4uaW5wdXQtc2VjdGlvbiB7XG4gIGRpc3BsYXk6IGZsZXg7XG4gIGFsaWduLWNvbnRlbnQ6IGNlbnRlcjtcbiAgYWxpZ24taXRlbXM6IGNlbnRlcjtcbiAgaGVpZ2h0OiA5dmg7XG59XG5cbi5oZWFkZXJHcmFkaWVudCB7XG4gIHBhZGRpbmc6IDhweCAxNXB4O1xuICBiYWNrZ3JvdW5kLWNvbG9yOiB3aGl0ZTtcbiAgYm9yZGVyLXRvcC1sZWZ0LXJhZGl1czogM3B4O1xuICBib3JkZXItdG9wLXJpZ2h0LXJhZGl1czogM3B4O1xufVxuXG4uaGVhZGVyVGV4dCB7XG4gIG1hcmdpbjogOXB4ICFpbXBvcnRhbnQ7XG4gIGZvbnQtc2l6ZTogMTZweCAhaW1wb3J0YW50O1xufVxuXG4uZm9ybS1kaXYge1xuICBwYWRkaW5nOiAzcHggOSUgMDtcbiAgaGVpZ2h0OiBjYWxjKDEwMCUgLSA1OXB4KTtcbiAgb3ZlcmZsb3c6IGF1dG87XG59XG5cbi5saXN0LW1hcmdpbjEge1xuICBoZWlnaHQ6IDEwMCU7XG4gIG92ZXJmbG93OiBhdXRvO1xufVxuXG4ubGlzdC1oZWFkZXIge1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAkcHJpbWFyeS1ibHVlLWIyO1xuICBmb250LXNpemU6IDE2cHggIWltcG9ydGFudDtcbiAgYm9yZGVyLXJhZGl1czogMTJweCAxMnB4IDAgMDtcbiAgY29sb3I6IHdoaXRlICFpbXBvcnRhbnQ7XG59XG5cbi5saXN0LWl0ZW1zIHtcbiAgYmFja2dyb3VuZC1jb2xvcjogd2hpdGU7XG4gIGJvcmRlci1yYWRpdXM6IDAgM3B4IDNweDtcbiAgYm9yZGVyOiAkcHJpbWFyeS1ibHVlLWIyIDAuNXB4IGRvdHRlZDtcblxuICAmOmhvdmVyIHtcbiAgICBib3gtc2hhZG93OiAwIDExcHggMTVweCAtN3B4IHJnYmEoMCwgMCwgMCwgMC4yKSwgMCAyNHB4IDM4cHggM3B4IHJnYmEoMCwgMCwgMCwgMC4xNCksIDAgOXB4IDQ2cHggOHB4IHJnYmEoMCwgMCwgMCwgMC4xMikgIWltcG9ydGFudDtcbiAgICBjdXJzb3I6IHBvaW50ZXI7XG4gIH1cbn1cblxuLnByZXZpZXctaWNvbiB7XG4gIGZvbnQtc2l6ZTogMThweDtcbiAgY29sb3I6ICRwcmltYXJ5LWJsdWUtYjI7XG59XG4iLCIvLyBCcmFuZGluZyBjb2xvcnNcbiRwcmltYXJ5LWJsdWUtYjE6ICMxYTg5ZDQ7XG4kcHJpbWFyeS1ibHVlLWIyOiAjMDA3N2JlO1xuJHByaW1hcnktYmx1ZS1iMzogIzIwNmJjZTtcbiRwcmltYXJ5LWJsdWUtYjQ6ICMxZDNhYjI7XG5cbiRwcmltYXJ5LWhvdmVyLWJsdWU6ICMxZDYxYjE7XG4kZ3JpZC1ob3Zlci1jb2xvcjogI2Y1ZjlmYztcbiRncmlkLWhlYWRlci1iZy1jb2xvcjogI2Q3ZWFmYTtcbiRncmlkLWhlYWRlci1jb2xvcjogIzBiNGQ5OTtcbiRncmlkLXRleHQtY29sb3I6ICM0NjQ2NDY7XG4kZ3JleS10ZXh0LWNvbG9yOiAjNjM2MzYzO1xuXG4kc2VsZWN0aW9uLWhpZ2hsaWdodC1jb2w6IHJnYmEoMCwgMTQwLCAyNjAsIDAuMik7XG4kcHJpbWFyeS1ncmV5LWcxOiAjZDFkM2QzO1xuJHByaW1hcnktZ3JleS1nMjogIzk5OTtcbiRwcmltYXJ5LWdyZXktZzM6ICM3MzczNzM7XG4kcHJpbWFyeS1ncmV5LWc0OiAjNWM2NjcwO1xuJHByaW1hcnktZ3JleS1nNTogIzMxMzEzMTtcbiRwcmltYXJ5LWdyZXktZzY6ICNmNWY1ZjU7XG4kcHJpbWFyeS1ncmV5LWc3OiAjM2QzZDNkO1xuXG4kcHJpbWFyeS13aGl0ZTogI2ZmZjtcbiRwcmltYXJ5LWJsYWNrOiAjMDAwO1xuJHByaW1hcnktcmVkOiAjYWIwZTI3O1xuJHByaW1hcnktZ3JlZW46ICM3M2I0MjE7XG4kcHJpbWFyeS1vcmFuZ2U6ICNmMDc2MDE7XG5cbiRzZWNvbmRhcnktZ3JlZW46ICM2ZmIzMjA7XG4kc2Vjb25kYXJ5LXllbGxvdzogI2ZmYmUwMDtcbiRzZWNvbmRhcnktb3JhbmdlOiAjZmY5MDAwO1xuJHNlY29uZGFyeS1yZWQ6ICNkOTNlMDA7XG4kc2Vjb25kYXJ5LWJlcnJ5OiAjYWMxNDVhO1xuJHNlY29uZGFyeS1wdXJwbGU6ICM5MTQxOTE7XG5cbiRzdHJpbmctdHlwZS1jb2xvcjogIzQ5OTViMjtcbiRudW1iZXItdHlwZS1jb2xvcjogIzAwYjE4MDtcbiRnZW8tdHlwZS1jb2xvcjogIzg0NWVjMjtcbiRkYXRlLXR5cGUtY29sb3I6ICNkMTk2MjE7XG5cbiR0eXBlLWNoaXAtb3BhY2l0eTogMTtcbiRzdHJpbmctdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRzdHJpbmctdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRudW1iZXItdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRudW1iZXItdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRnZW8tdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRnZW8tdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRkYXRlLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZGF0ZS10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuXG4kcmVwb3J0LWRlc2lnbmVyLXNldHRpbmdzLWJnLWNvbG9yOiAjZjVmOWZjO1xuJGJhY2tncm91bmQtY29sb3I6ICNmNWY5ZmM7XG4iXX0= */"

/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/dataset-details/dataset-details.component.ts":
/*!***********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/dataset-details/dataset-details.component.ts ***!
  \***********************************************************************************************************/
/*! exports provided: DatasetDetailsComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DatasetDetailsComponent", function() { return DatasetDetailsComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var _angular_cdk_keycodes__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/cdk/keycodes */ "./node_modules/@angular/cdk/esm5/keycodes.es5.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/isUndefined */ "./node_modules/lodash/isUndefined.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_isUndefined__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var _rawpreview_dialog_rawpreview_dialog_component__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../rawpreview-dialog/rawpreview-dialog.component */ "./src/app/modules/workbench/components/create-datasets/rawpreview-dialog/rawpreview-dialog.component.ts");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");








var DatasetDetailsComponent = /** @class */ (function () {
    function DatasetDetailsComponent(dialog, workBench) {
        this.dialog = dialog;
        this.workBench = workBench;
        this.separatorKeysCodes = [_angular_cdk_keycodes__WEBPACK_IMPORTED_MODULE_4__["ENTER"], _angular_cdk_keycodes__WEBPACK_IMPORTED_MODULE_4__["COMMA"]]; // tslint:disable-line
        this.lineSeperator = 'lineFeed';
        this.onDetailsFilled = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
    }
    DatasetDetailsComponent.prototype.ngOnInit = function () {
        this.detailsFormGroup = new _angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormGroup"]({
            fieldSeperatorControl: new _angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormControl"]('', _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required),
            hederSizeControl: new _angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormControl"]('1', _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required),
            fieldNamesLineControl: new _angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormControl"]('1'),
            quoteCharControl: new _angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormControl"](''),
            escapeCharControl: new _angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormControl"]('')
        });
        this.subcribeToFormChanges();
    };
    DatasetDetailsComponent.prototype.subcribeToFormChanges = function () {
        var _this = this;
        var detailsFormStatusChange = this.detailsFormGroup.statusChanges;
        detailsFormStatusChange.subscribe(function (status) {
            if (status === 'VALID') {
                _this.onFormValid(_this.detailsFormGroup.value);
            }
            else {
                _this.onDetailsFilled.emit({
                    detailsFilled: false,
                    details: _this.previewConfig
                });
            }
        });
    };
    DatasetDetailsComponent.prototype.addFormat = function (event) {
        var input = event.input;
        var value = event.value;
        // Add Format
        if ((value || '').trim()) {
            this.previewConfig.dateFormats.push(value.trim());
        }
        // Reset the input value
        if (input) {
            input.value = '';
        }
    };
    DatasetDetailsComponent.prototype.removeFormat = function (format) {
        var index = this.previewConfig.dateFormats.indexOf(format);
        if (index >= 0) {
            this.previewConfig.dateFormats.splice(index, 1);
        }
    };
    DatasetDetailsComponent.prototype.previewDialog = function (fileDetails) {
        var _this = this;
        var path = fileDetails.path + "/" + fileDetails.name;
        this.workBench.getRawPreviewData(path).subscribe(function (data) {
            _this.dialog.open(_rawpreview_dialog_rawpreview_dialog_component__WEBPACK_IMPORTED_MODULE_6__["RawpreviewDialogComponent"], {
                minHeight: 500,
                minWidth: 600,
                data: {
                    title: fileDetails.name,
                    rawData: data.data
                }
            });
        });
    };
    DatasetDetailsComponent.prototype.onFormValid = function (data) {
        if (!lodash_isUndefined__WEBPACK_IMPORTED_MODULE_5__(this.selFiles)) {
            this.onDetailsFilled.emit({
                detailsFilled: true,
                details: this.previewConfig
            });
        }
    };
    DatasetDetailsComponent.prototype.toPreview = function () {
        if (this.lineSeperator === 'lineFeed') {
            this.previewConfig.lineSeparator = '\n';
        }
        else if (this.lineSeperator === 'carriageReturn') {
            this.previewConfig.lineSeparator = '\r';
        }
        else {
            this.previewConfig.lineSeparator = '\r\n';
        }
        this.previewConfig.delimiter = this.detailsFormGroup.value.fieldSeperatorControl;
        this.previewConfig.fieldNamesLine = this.detailsFormGroup.value.fieldNamesLineControl;
        this.previewConfig.headerSize = this.detailsFormGroup.value.hederSizeControl;
        this.previewConfig.quoteEscapeChar = this.detailsFormGroup.value.escapeCharControl;
        this.previewConfig.quoteChar = this.detailsFormGroup.value.quoteCharControl;
        this.onDetailsFilled.emit({
            detailsFilled: true,
            details: this.previewConfig
        });
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], DatasetDetailsComponent.prototype, "selFiles", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], DatasetDetailsComponent.prototype, "previewConfig", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], DatasetDetailsComponent.prototype, "onDetailsFilled", void 0);
    DatasetDetailsComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'dataset-details',
            template: __webpack_require__(/*! ./dataset-details.component.html */ "./src/app/modules/workbench/components/create-datasets/dataset-details/dataset-details.component.html"),
            styles: [__webpack_require__(/*! ./dataset-details.component.scss */ "./src/app/modules/workbench/components/create-datasets/dataset-details/dataset-details.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialog"], _services_workbench_service__WEBPACK_IMPORTED_MODULE_7__["WorkbenchService"]])
    ], DatasetDetailsComponent);
    return DatasetDetailsComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/dateformat-dialog/dateformat-dialog.component.html":
/*!*****************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/dateformat-dialog/dateformat-dialog.component.html ***!
  \*****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<form [formGroup]=\"form\" (ngSubmit)=\"submit(form)\">\n  <mat-dialog-content>\n    <mat-list class=\"formatList\" *ngIf=\"formatArr.length > 0\">\n        <h3 mat-subheader>Identified formats</h3>\n      <mat-list-item class=\"list-items\" *ngFor=\"let format of formatArr; index as i\">\n        <p mat-line>{{i + 1}}. &ensp; {{ format }}</p>\n        <mat-divider></mat-divider>\n      </mat-list-item>\n    </mat-list>\n    <mat-form-field class=\"inpElement\">\n      <input matInput formControlName=\"dateformat\" placeholder=\"{{placeholder}}\" />\n    </mat-form-field>\n  </mat-dialog-content>\n  <mat-dialog-actions>\n    <button mat-button type=\"submit\">OK</button>\n    <button mat-button type=\"button\" style=\"color: #E5524C;\" mat-dialog-close>Cancel</button>\n  </mat-dialog-actions>\n</form>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/dateformat-dialog/dateformat-dialog.component.scss":
/*!*****************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/dateformat-dialog/dateformat-dialog.component.scss ***!
  \*****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".inpElement {\n  width: 100%; }\n\n.formatList {\n  border: 1px #ab0e27 solid;\n  margin-bottom: 9px; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2NyZWF0ZS1kYXRhc2V0cy9kYXRlZm9ybWF0LWRpYWxvZy9kYXRlZm9ybWF0LWRpYWxvZy5jb21wb25lbnQuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUNFLFdBQVcsRUFBQTs7QUFHYjtFQUNFLHlCQUE4QjtFQUM5QixrQkFBa0IsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvd29ya2JlbmNoL2NvbXBvbmVudHMvY3JlYXRlLWRhdGFzZXRzL2RhdGVmb3JtYXQtZGlhbG9nL2RhdGVmb3JtYXQtZGlhbG9nLmNvbXBvbmVudC5zY3NzIiwic291cmNlc0NvbnRlbnQiOlsiQGltcG9ydCBcInNyYy90aGVtZXMvYmFzZS9jb2xvcnNcIjtcblxuLmlucEVsZW1lbnQge1xuICB3aWR0aDogMTAwJTtcbn1cblxuLmZvcm1hdExpc3Qge1xuICBib3JkZXI6IDFweCAkcHJpbWFyeS1yZWQgc29saWQ7XG4gIG1hcmdpbi1ib3R0b206IDlweDtcbn1cbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/dateformat-dialog/dateformat-dialog.component.ts":
/*!***************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/dateformat-dialog/dateformat-dialog.component.ts ***!
  \***************************************************************************************************************/
/*! exports provided: DateformatDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DateformatDialogComponent", function() { return DateformatDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_4__);





var DateformatDialogComponent = /** @class */ (function () {
    function DateformatDialogComponent(formBuilder, dialogRef, data) {
        this.formBuilder = formBuilder;
        this.dialogRef = dialogRef;
        this.data = data;
        this.placeholder = ''; // tslint:disable-line
        this.formatArr = ''; // tslint:disable-line
        if (lodash_get__WEBPACK_IMPORTED_MODULE_4__(data, 'placeholder')) {
            this.placeholder = data.placeholder;
        }
    }
    DateformatDialogComponent.prototype.ngOnInit = function () {
        if (lodash_get__WEBPACK_IMPORTED_MODULE_4__(this.data, 'formatArr')) {
            this.formatArr = this.data.formatArr;
        }
        this.form = this.formBuilder.group({
            dateformat: this.data ? this.data.format : ''
        });
    };
    DateformatDialogComponent.prototype.submit = function (form) {
        this.dialogRef.close("" + form.value.dateformat);
    };
    DateformatDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'dateformat-dialog',
            template: __webpack_require__(/*! ./dateformat-dialog.component.html */ "./src/app/modules/workbench/components/create-datasets/dateformat-dialog/dateformat-dialog.component.html"),
            styles: [__webpack_require__(/*! ./dateformat-dialog.component.scss */ "./src/app/modules/workbench/components/create-datasets/dateformat-dialog/dateformat-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](2, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormBuilder"],
            _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"], Object])
    ], DateformatDialogComponent);
    return DateformatDialogComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/parser-preview/parser-preview.component.html":
/*!***********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/parser-preview/parser-preview.component.html ***!
  \***********************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<mat-card class=\"preview-container\">\n  <mat-card-content fxFill class=\"content-hgt\">\n    <mat-tab-group fxFill dynamicHeight=\"false\" style=\"background-color: white;\">\n      <mat-tab e2e=\"parsed-data-preview\" fxFill label=\"Parsed View\">\n        <dx-data-grid *ngIf=\"!inspectError\" id=\"gridContainer\">\n          <div *dxTemplate=\"let data of 'headerTemplate'\">\n            <div fxLayout=\"column\" class=\"fNameInp\">\n              <mat-form-field>\n                <input matInput [(ngModel)]=\"fieldInfo[data.columnIndex].name\" class=\"fieldName\" type=\"text\" required />\n              </mat-form-field>\n              <div fxLayout=\"row\" class=\"fTypeGroup\">\n                <div fxFlex>\n                  <select (change)=\"checkDate($event)\" id=\"{{ data.columnIndex }}\" class=\"fieldType\" [(ngModel)]=\"fieldInfo[data.columnIndex].type\">\n                    <option value=\"string\">STRING</option>\n                    <option value=\"long\">LONG</option>\n                    <option value=\"date\">DATE</option>\n                    <option value=\"boolean\">BOOLEAN</option>\n                    <option value=\"double\">DOUBLE</option>\n                  </select>\n                  <mat-icon fontIcon=\"icon-edit-solid\" class=\"formatEditIcon\" id=\"edit_{{ data.columnIndex }}\" (click)=\"openDateFormatDialog($event)\"\n                    [style.visibility]=\"fieldInfo[data.columnIndex].type === 'date' ? 'visible' : 'hidden'\"></mat-icon>\n                </div>\n                <mat-icon fontIcon=\"icon-info-solid\" class=\"errorInfoIcon\" *ngIf=\"fieldInfo[data.columnIndex].format?.length > 1\"></mat-icon>\n              </div>\n            </div>\n          </div>\n        </dx-data-grid>\n        <div *ngIf=\"inspectError\" fxLayout=\"row\" fxLayoutAlign=\"space-around center\">\n          <div fxFlex=\"75\" class=\"alert is-important\">\n            <h4>Please validate entered details!</h4>\n            <p>{{errMsg}}</p>\n          </div>\n        </div>\n      </mat-tab>\n      <mat-tab e2e=\"raw-data-preview\" fxFill label=\"Raw Preview\">\n        <div class=\"raw-content\">\n          <div *ngFor=\"let line of rawFile\">\n            <span [textContent]=\"line\"></span>\n          </div>\n        </div>\n      </mat-tab>\n    </mat-tab-group>\n  </mat-card-content>\n</mat-card>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/parser-preview/parser-preview.component.scss":
/*!***********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/parser-preview/parser-preview.component.scss ***!
  \***********************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%; }\n\nmat-tab-group {\n  max-width: 100%; }\n\n.fieldType {\n  border-color: #C4C4C4;\n  border-radius: 3px;\n  font-size: 11px;\n  color: rgba(0, 0, 0, 0.69); }\n\n::ng-deep .mat-tab-label-active {\n  color: #0077be; }\n\n.formatEditIcon {\n  font-size: 18px;\n  padding-top: 7px; }\n\n.fTypeGroup {\n  margin-top: -17px;\n  padding-bottom: 0.3px;\n  z-index: 99; }\n\n.mat-form-field-infix {\n  padding: 0 !important; }\n\n.errorInfoIcon {\n  font-size: 22px;\n  padding-top: 2px;\n  color: #ab0e27; }\n\n.preview-container {\n  height: calc(100% - 47px);\n  padding: 0 3px; }\n\n.raw-content {\n  padding: 20px;\n  font-size: 14px; }\n\n.content-hgt {\n  height: 99% !important; }\n\n.alert {\n  padding: 16px;\n  margin: 24px 0;\n  font-size: 14px;\n  color: #333;\n  width: 100%;\n  box-sizing: border-box; }\n\n.is-important {\n  border-left: 10px solid #ab0e27;\n  background-color: #E5524C08; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2NyZWF0ZS1kYXRhc2V0cy9wYXJzZXItcHJldmlldy9wYXJzZXItcHJldmlldy5jb21wb25lbnQuc2NzcyIsIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL3RoZW1lcy9iYXNlL19jb2xvcnMuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUNFLFdBQVcsRUFBQTs7QUFHYjtFQUNFLGVBQWUsRUFBQTs7QUFHakI7RUFDRSxxQkFBcUI7RUFDckIsa0JBQWtCO0VBQ2xCLGVBQWU7RUFDZiwwQkFBMEIsRUFBQTs7QUFHNUI7RUFDRSxjQ2hCdUIsRUFBQTs7QURtQnpCO0VBQ0UsZUFBZTtFQUNmLGdCQUFnQixFQUFBOztBQUdsQjtFQUNFLGlCQUFpQjtFQUNqQixxQkFBcUI7RUFDckIsV0FBVyxFQUFBOztBQUdiO0VBQ0UscUJBQXFCLEVBQUE7O0FBR3ZCO0VBQ0UsZUFBZTtFQUNmLGdCQUFnQjtFQUNoQixjQ2ZtQixFQUFBOztBRGtCckI7RUFDRSx5QkFBeUI7RUFDekIsY0FBYyxFQUFBOztBQUdoQjtFQUNFLGFBQWE7RUFDYixlQUFlLEVBQUE7O0FBR2pCO0VBQ0Usc0JBQXNCLEVBQUE7O0FBR3hCO0VBQ0UsYUFBYTtFQUNiLGNBQWM7RUFDZCxlQUFlO0VBQ2YsV0FBVztFQUNYLFdBQVc7RUFDWCxzQkFBc0IsRUFBQTs7QUFHeEI7RUFDRSwrQkMxQ21CO0VEMkNuQiwyQkFBMkIsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvd29ya2JlbmNoL2NvbXBvbmVudHMvY3JlYXRlLWRhdGFzZXRzL3BhcnNlci1wcmV2aWV3L3BhcnNlci1wcmV2aWV3LmNvbXBvbmVudC5zY3NzIiwic291cmNlc0NvbnRlbnQiOlsiQGltcG9ydCBcInNyYy90aGVtZXMvYmFzZS9jb2xvcnNcIjtcblxuOmhvc3Qge1xuICB3aWR0aDogMTAwJTtcbn1cblxubWF0LXRhYi1ncm91cCB7XG4gIG1heC13aWR0aDogMTAwJTtcbn1cblxuLmZpZWxkVHlwZSB7XG4gIGJvcmRlci1jb2xvcjogI0M0QzRDNDtcbiAgYm9yZGVyLXJhZGl1czogM3B4O1xuICBmb250LXNpemU6IDExcHg7XG4gIGNvbG9yOiByZ2JhKDAsIDAsIDAsIDAuNjkpO1xufVxuXG46Om5nLWRlZXAgLm1hdC10YWItbGFiZWwtYWN0aXZlIHtcbiAgY29sb3I6ICRwcmltYXJ5LWJsdWUtYjI7XG59XG5cbi5mb3JtYXRFZGl0SWNvbiB7XG4gIGZvbnQtc2l6ZTogMThweDtcbiAgcGFkZGluZy10b3A6IDdweDtcbn1cblxuLmZUeXBlR3JvdXAge1xuICBtYXJnaW4tdG9wOiAtMTdweDtcbiAgcGFkZGluZy1ib3R0b206IDAuM3B4O1xuICB6LWluZGV4OiA5OTtcbn1cblxuLm1hdC1mb3JtLWZpZWxkLWluZml4IHtcbiAgcGFkZGluZzogMCAhaW1wb3J0YW50O1xufVxuXG4uZXJyb3JJbmZvSWNvbiB7XG4gIGZvbnQtc2l6ZTogMjJweDtcbiAgcGFkZGluZy10b3A6IDJweDtcbiAgY29sb3I6ICRwcmltYXJ5LXJlZDtcbn1cblxuLnByZXZpZXctY29udGFpbmVyIHtcbiAgaGVpZ2h0OiBjYWxjKDEwMCUgLSA0N3B4KTtcbiAgcGFkZGluZzogMCAzcHg7XG59XG5cbi5yYXctY29udGVudCB7XG4gIHBhZGRpbmc6IDIwcHg7XG4gIGZvbnQtc2l6ZTogMTRweDtcbn1cblxuLmNvbnRlbnQtaGd0IHtcbiAgaGVpZ2h0OiA5OSUgIWltcG9ydGFudDtcbn1cblxuLmFsZXJ0IHtcbiAgcGFkZGluZzogMTZweDtcbiAgbWFyZ2luOiAyNHB4IDA7XG4gIGZvbnQtc2l6ZTogMTRweDtcbiAgY29sb3I6ICMzMzM7XG4gIHdpZHRoOiAxMDAlO1xuICBib3gtc2l6aW5nOiBib3JkZXItYm94O1xufVxuXG4uaXMtaW1wb3J0YW50IHtcbiAgYm9yZGVyLWxlZnQ6IDEwcHggc29saWQgJHByaW1hcnktcmVkO1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAjRTU1MjRDMDg7XG59XG4iLCIvLyBCcmFuZGluZyBjb2xvcnNcbiRwcmltYXJ5LWJsdWUtYjE6ICMxYTg5ZDQ7XG4kcHJpbWFyeS1ibHVlLWIyOiAjMDA3N2JlO1xuJHByaW1hcnktYmx1ZS1iMzogIzIwNmJjZTtcbiRwcmltYXJ5LWJsdWUtYjQ6ICMxZDNhYjI7XG5cbiRwcmltYXJ5LWhvdmVyLWJsdWU6ICMxZDYxYjE7XG4kZ3JpZC1ob3Zlci1jb2xvcjogI2Y1ZjlmYztcbiRncmlkLWhlYWRlci1iZy1jb2xvcjogI2Q3ZWFmYTtcbiRncmlkLWhlYWRlci1jb2xvcjogIzBiNGQ5OTtcbiRncmlkLXRleHQtY29sb3I6ICM0NjQ2NDY7XG4kZ3JleS10ZXh0LWNvbG9yOiAjNjM2MzYzO1xuXG4kc2VsZWN0aW9uLWhpZ2hsaWdodC1jb2w6IHJnYmEoMCwgMTQwLCAyNjAsIDAuMik7XG4kcHJpbWFyeS1ncmV5LWcxOiAjZDFkM2QzO1xuJHByaW1hcnktZ3JleS1nMjogIzk5OTtcbiRwcmltYXJ5LWdyZXktZzM6ICM3MzczNzM7XG4kcHJpbWFyeS1ncmV5LWc0OiAjNWM2NjcwO1xuJHByaW1hcnktZ3JleS1nNTogIzMxMzEzMTtcbiRwcmltYXJ5LWdyZXktZzY6ICNmNWY1ZjU7XG4kcHJpbWFyeS1ncmV5LWc3OiAjM2QzZDNkO1xuXG4kcHJpbWFyeS13aGl0ZTogI2ZmZjtcbiRwcmltYXJ5LWJsYWNrOiAjMDAwO1xuJHByaW1hcnktcmVkOiAjYWIwZTI3O1xuJHByaW1hcnktZ3JlZW46ICM3M2I0MjE7XG4kcHJpbWFyeS1vcmFuZ2U6ICNmMDc2MDE7XG5cbiRzZWNvbmRhcnktZ3JlZW46ICM2ZmIzMjA7XG4kc2Vjb25kYXJ5LXllbGxvdzogI2ZmYmUwMDtcbiRzZWNvbmRhcnktb3JhbmdlOiAjZmY5MDAwO1xuJHNlY29uZGFyeS1yZWQ6ICNkOTNlMDA7XG4kc2Vjb25kYXJ5LWJlcnJ5OiAjYWMxNDVhO1xuJHNlY29uZGFyeS1wdXJwbGU6ICM5MTQxOTE7XG5cbiRzdHJpbmctdHlwZS1jb2xvcjogIzQ5OTViMjtcbiRudW1iZXItdHlwZS1jb2xvcjogIzAwYjE4MDtcbiRnZW8tdHlwZS1jb2xvcjogIzg0NWVjMjtcbiRkYXRlLXR5cGUtY29sb3I6ICNkMTk2MjE7XG5cbiR0eXBlLWNoaXAtb3BhY2l0eTogMTtcbiRzdHJpbmctdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRzdHJpbmctdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRudW1iZXItdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRudW1iZXItdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRnZW8tdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRnZW8tdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRkYXRlLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZGF0ZS10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuXG4kcmVwb3J0LWRlc2lnbmVyLXNldHRpbmdzLWJnLWNvbG9yOiAjZjVmOWZjO1xuJGJhY2tncm91bmQtY29sb3I6ICNmNWY5ZmM7XG4iXX0= */"

/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/parser-preview/parser-preview.component.ts":
/*!*********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/parser-preview/parser-preview.component.ts ***!
  \*********************************************************************************************************/
/*! exports provided: ParserPreviewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ParserPreviewComponent", function() { return ParserPreviewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var lodash_set__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/set */ "./node_modules/lodash/set.js");
/* harmony import */ var lodash_set__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_set__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/map */ "./node_modules/lodash/map.js");
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_map__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var lodash_replace__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/replace */ "./node_modules/lodash/replace.js");
/* harmony import */ var lodash_replace__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_replace__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! lodash/cloneDeep */ "./node_modules/lodash/cloneDeep.js");
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_7__);
/* harmony import */ var lodash_assign__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! lodash/assign */ "./node_modules/lodash/assign.js");
/* harmony import */ var lodash_assign__WEBPACK_IMPORTED_MODULE_8___default = /*#__PURE__*/__webpack_require__.n(lodash_assign__WEBPACK_IMPORTED_MODULE_8__);
/* harmony import */ var lodash_has__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! lodash/has */ "./node_modules/lodash/has.js");
/* harmony import */ var lodash_has__WEBPACK_IMPORTED_MODULE_9___default = /*#__PURE__*/__webpack_require__.n(lodash_has__WEBPACK_IMPORTED_MODULE_9__);
/* harmony import */ var lodash_take__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! lodash/take */ "./node_modules/lodash/take.js");
/* harmony import */ var lodash_take__WEBPACK_IMPORTED_MODULE_10___default = /*#__PURE__*/__webpack_require__.n(lodash_take__WEBPACK_IMPORTED_MODULE_10__);
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! lodash/isUndefined */ "./node_modules/lodash/isUndefined.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_11___default = /*#__PURE__*/__webpack_require__.n(lodash_isUndefined__WEBPACK_IMPORTED_MODULE_11__);
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! devextreme-angular/ui/data-grid */ "./node_modules/devextreme-angular/ui/data-grid.js");
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_13___default = /*#__PURE__*/__webpack_require__.n(devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_13__);
/* harmony import */ var _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ../../../../../common/services/dxDataGrid.service */ "./src/app/common/services/dxDataGrid.service.ts");
/* harmony import */ var _dateformat_dialog_dateformat_dialog_component__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ../dateformat-dialog/dateformat-dialog.component */ "./src/app/modules/workbench/components/create-datasets/dateformat-dialog/dateformat-dialog.component.ts");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ../../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");

















var ParserPreviewComponent = /** @class */ (function () {
    function ParserPreviewComponent(dxDataGrid, dialog, workBench) {
        this.dxDataGrid = dxDataGrid;
        this.dialog = dialog;
        this.workBench = workBench;
        this.fieldInfo = [];
        this.inspectError = false;
        this.errMsg = '';
        this.parserConfig = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
    }
    ParserPreviewComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.previewgridConfig = this.getPreviewGridConfig();
        setTimeout(function () {
            _this.updaterSubscribtion = _this.previewObj.subscribe(function (data) {
                _this.onUpdate(data);
            });
        }, 100);
    };
    ParserPreviewComponent.prototype.ngOnDestroy = function () {
        this.updaterSubscribtion.unsubscribe();
    };
    ParserPreviewComponent.prototype.onUpdate = function (data) {
        var _this = this;
        if (data.length === 2 && !lodash_isUndefined__WEBPACK_IMPORTED_MODULE_11__(data[0].samplesParsed)) {
            this.parserData = lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_7__(data[0]);
            var parsedData_1 = data[0].samplesParsed;
            this.fieldInfo = lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_7__(data[0].fields);
            setTimeout(function () {
                _this.dataGrid.instance.beginCustomLoading('Loading...');
                _this.reloadDataGrid(parsedData_1);
            });
        }
        else if (data.length !== 0 && !lodash_isUndefined__WEBPACK_IMPORTED_MODULE_11__(data[0].error)) {
            this.inspectError = true;
            this.errMsg = data[0].error.message;
        }
        else if (data.length === 0) {
            setTimeout(function () {
                _this.dataGrid.instance.beginCustomLoading('Loading...');
                _this.reloadDataGrid([]);
            });
        }
        if (data.length === 2 && !lodash_isUndefined__WEBPACK_IMPORTED_MODULE_11__(data[1])) {
            this.rawPreview(data[1]);
        }
    };
    ParserPreviewComponent.prototype.ngAfterViewInit = function () {
        this.dataGrid.instance.option(this.previewgridConfig);
    };
    ParserPreviewComponent.prototype.getPreviewGridConfig = function () {
        var dataSource = [];
        return this.dxDataGrid.mergeWithDefaultConfig({
            dataSource: dataSource,
            columnAutoWidth: false,
            wordWrapEnabled: false,
            searchPanel: {
                visible: false,
                width: 240,
                placeholder: 'Search...'
            },
            height: '100%',
            width: '100%',
            filterRow: {
                visible: true,
                applyFilter: 'auto'
            },
            headerFilter: {
                visible: false
            },
            sorting: {
                mode: 'none'
            },
            export: {
                fileName: 'Parsed_Sample'
            },
            scrolling: {
                showScrollbar: 'always',
                mode: 'virtual',
                useNative: false
            },
            showRowLines: false,
            showBorders: false,
            rowAlternationEnabled: true,
            showColumnLines: true,
            selection: {
                mode: 'none'
            },
            customizeColumns: function (columns) {
                for (var _i = 0, columns_1 = columns; _i < columns_1.length; _i++) {
                    var column = columns_1[_i];
                    column.headerCellTemplate = 'headerTemplate';
                }
            }
        });
    };
    ParserPreviewComponent.prototype.reloadDataGrid = function (data) {
        this.dataGrid.instance.option('dataSource', data);
        this.dataGrid.instance.refresh();
        this.dataGrid.instance.endCustomLoading();
    };
    ParserPreviewComponent.prototype.checkDate = function (event) {
        var ftype = event.srcElement.value;
        if (ftype === 'date') {
            this.openDateFormatDialog(event);
        }
        else {
            var formatEdit = event.currentTarget.nextElementSibling;
            formatEdit.style.visibility = 'hidden';
            var index = event.srcElement.id;
            if (lodash_has__WEBPACK_IMPORTED_MODULE_9__(this.parserData.fields[index], 'format')) {
                lodash_set__WEBPACK_IMPORTED_MODULE_3__(this.fieldInfo[index], 'format', lodash_get__WEBPACK_IMPORTED_MODULE_4__(this.parserData.fields[index], 'format'));
            }
        }
    };
    /*set the user provided date format for the Field. If the format is empty revert back the field type to original. */
    ParserPreviewComponent.prototype.openDateFormatDialog = function (event) {
        var _this = this;
        var index;
        var dateformat = '';
        var formatArr = [];
        if (event.type === 'click') {
            index = lodash_replace__WEBPACK_IMPORTED_MODULE_6__(event.target.id, 'edit_', '');
        }
        else {
            index = event.srcElement.id;
        }
        if (lodash_has__WEBPACK_IMPORTED_MODULE_9__(this.fieldInfo[index], 'format')) {
            if (this.fieldInfo[index].format.length > 1) {
                formatArr = this.fieldInfo[index].format;
            }
            else {
                dateformat = lodash_get__WEBPACK_IMPORTED_MODULE_4__(this.fieldInfo[index], 'format[0]');
            }
        }
        var dateDialogRef = this.dialog.open(_dateformat_dialog_dateformat_dialog_component__WEBPACK_IMPORTED_MODULE_15__["DateformatDialogComponent"], {
            hasBackdrop: false,
            data: {
                placeholder: 'Enter date format',
                format: dateformat,
                formatArr: formatArr
            }
        });
        dateDialogRef.afterClosed().subscribe(function (format) {
            var id = -1;
            if (event.type === 'click') {
                id = lodash_replace__WEBPACK_IMPORTED_MODULE_6__(event.target.id, 'edit_', '');
            }
            else {
                id = event.srcElement.id;
            }
            if (format === '' && lodash_has__WEBPACK_IMPORTED_MODULE_9__(_this.parserData.fields[id], 'format')) {
                if (_this.parserData.fields[id].format.length === 0 ||
                    _this.parserData.fields[id].format.length > 1) {
                    event.srcElement.value = lodash_get__WEBPACK_IMPORTED_MODULE_4__(_this.parserData.fields[id], 'type');
                }
                else if (_this.parserData.fields[id].format.length === 1) {
                    lodash_set__WEBPACK_IMPORTED_MODULE_3__(_this.fieldInfo[id], 'format[0]', lodash_get__WEBPACK_IMPORTED_MODULE_4__(_this.parserData.fields[id], 'format[0]'));
                }
            }
            else if (format === '' && !lodash_has__WEBPACK_IMPORTED_MODULE_9__(_this.parserData.fields[id], 'format')) {
                event.srcElement.value = lodash_get__WEBPACK_IMPORTED_MODULE_4__(_this.parserData.fields[id], 'type');
            }
            else if (format !== '') {
                lodash_set__WEBPACK_IMPORTED_MODULE_3__(_this.fieldInfo[id], 'format', [format]);
            }
        });
    };
    ParserPreviewComponent.prototype.toAdd = function () {
        var fieldsObj = lodash_map__WEBPACK_IMPORTED_MODULE_5__(this.fieldInfo, function (obj) {
            if (obj.format) {
                obj.format = obj.format[0]; // Parser component expects format to be a string.
            }
            return obj;
        });
        var config = lodash_assign__WEBPACK_IMPORTED_MODULE_8__(this.parserData, { fields: fieldsObj });
        this.parserConfig.emit(config);
    };
    ParserPreviewComponent.prototype.errorInfoVisibility = function (index) {
        if (lodash_get__WEBPACK_IMPORTED_MODULE_4__(this.fieldInfo[index], 'format').length > 1) {
            return 'visible';
        }
        return 'hidden';
    };
    ParserPreviewComponent.prototype.rawPreview = function (filePath) {
        var _this = this;
        this.workBench.getRawPreviewData(filePath).subscribe(function (data) {
            _this.rawFile = lodash_take__WEBPACK_IMPORTED_MODULE_10__(data.data, 50);
        });
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_2__["BehaviorSubject"])
    ], ParserPreviewComponent.prototype, "previewObj", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], ParserPreviewComponent.prototype, "parserConfig", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ViewChild"])(devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_13__["DxDataGridComponent"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_13__["DxDataGridComponent"])
    ], ParserPreviewComponent.prototype, "dataGrid", void 0);
    ParserPreviewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'parser-preview',
            template: __webpack_require__(/*! ./parser-preview.component.html */ "./src/app/modules/workbench/components/create-datasets/parser-preview/parser-preview.component.html"),
            styles: [__webpack_require__(/*! ./parser-preview.component.scss */ "./src/app/modules/workbench/components/create-datasets/parser-preview/parser-preview.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_14__["DxDataGridService"],
            _angular_material__WEBPACK_IMPORTED_MODULE_12__["MatDialog"],
            _services_workbench_service__WEBPACK_IMPORTED_MODULE_16__["WorkbenchService"]])
    ], ParserPreviewComponent);
    return ParserPreviewComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/rawpreview-dialog/rawpreview-dialog.component.html":
/*!*****************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/rawpreview-dialog/rawpreview-dialog.component.html ***!
  \*****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div mat-dialog-title>\n  <span [textContent]=\"title\"></span>\n  <span fxFlex></span>\n  <button e2e=\"close-raw-preview-dialog-icon\" mat-icon-button mat-dialog-close class=\"xBtn\">\n    <mat-icon style=\"color: #E5524C;\" fontIcon=\"icon-close\"></mat-icon>\n  </button>\n</div>\n<div mat-dialog-content style=\"background-color: #F5F9FC; min-height:450px;\">\n  <!-- <span [textContent]=\"message\"></span> -->\n  <div *ngFor=\"let line of message\">\n    <span [textContent]=\"line\"></span>\n  </div>\n</div>\n<div mat-dialog-actions style=\"flex-direction: row-reverse\">\n  <button e2e=\"close-raw-preview-dialog-button\" style=\"color: #E5524C;\" mat-button mat-raised-button (click)=\"closeDashboard()\">\n    Close\n  </button>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/rawpreview-dialog/rawpreview-dialog.component.ts":
/*!***************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/rawpreview-dialog/rawpreview-dialog.component.ts ***!
  \***************************************************************************************************************/
/*! exports provided: RawpreviewDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RawpreviewDialogComponent", function() { return RawpreviewDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var lodash_take__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/take */ "./node_modules/lodash/take.js");
/* harmony import */ var lodash_take__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_take__WEBPACK_IMPORTED_MODULE_4__);





var RawpreviewDialogComponent = /** @class */ (function () {
    function RawpreviewDialogComponent(dialogRef, params // tslint:disable-line
    ) {
        this.dialogRef = dialogRef;
        this.params = params;
        this.title = ''; // tslint:disable-line
        this.message = 'No Data'; // tslint:disable-line
        if (lodash_get__WEBPACK_IMPORTED_MODULE_3__(params, 'rawData')) {
            this.message = lodash_take__WEBPACK_IMPORTED_MODULE_4__(params.rawData, 50);
        }
        if (lodash_get__WEBPACK_IMPORTED_MODULE_3__(params, 'title')) {
            this.title = params.title;
        }
    }
    RawpreviewDialogComponent.prototype.ngOnInit = function () { };
    RawpreviewDialogComponent.prototype.closeDashboard = function (confirm) {
        if (confirm === void 0) { confirm = false; }
        this.dialogRef.close(confirm);
    };
    RawpreviewDialogComponent.prototype.confirm = function () {
        this.closeDashboard(true);
    };
    RawpreviewDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'rawpreview-dialog',
            template: __webpack_require__(/*! ./rawpreview-dialog.component.html */ "./src/app/modules/workbench/components/create-datasets/rawpreview-dialog/rawpreview-dialog.component.html")
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](1, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"], Object])
    ], RawpreviewDialogComponent);
    return RawpreviewDialogComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/select-rawdata/select-rawdata.component.html":
/*!***********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/select-rawdata/select-rawdata.component.html ***!
  \***********************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"select-view\" fxLayout=\"row\" fxLayoutGap=\"9px\">\n  <mat-card class=\"margin-9\" style=\"padding:0px;\" fxFlex=\"75\" fxFlex.gt-md=\"75\" fxFlex.md=\"75\">\n    <mat-card-header class=\"headerGradient\">\n      <div class=\"mat-body-1\" i18n>Staging Area</div>\n\n      <span class=\"toolbar-spacer\"></span>\n      <button for=\"fileUpload\" i18n-matTooltip matTooltip=\"Upload File\" e2e=\"upload-selected-file\" mat-icon-button color=\"primary\" (click)=\"fileUpload.click()\">\n        <mat-icon fontIcon=\"icon-upload\"></mat-icon>\n      </button>\n      <input fxHide e2e=\"uploaded-file\" type=\"file\" #fileUpload (change)=\"fileInput($event)\" multiple accept=\"text/csv, 'text/plain'\" />\n    </mat-card-header>\n    <mat-divider></mat-divider>\n    <mat-card-content class=\"staging-content\">\n      <div fxLayout=\"row\" [style.height.%]=\"100\">\n        <div class=\"tree\" fxFlex=\"30\">\n          <remote-folder-selector (selectionChange)=\"onFolderSelected($event)\"\n                                  [fileSystemAPI]=\"fileSystemAPI\"\n                                  [enableFolderCreation]=\"true\"\n                                  [rootNode]=\"treeNodes\"\n          >\n          </remote-folder-selector>\n        </div>\n        <div fxFlex=\"70\" fxFill fxLayout=\"column\">\n          <mat-form-field id=\"infoForMask\">\n            <input matInput autocomplete=\"off\" i18n placeholder=\"Enter file mask\" [formControl]=\"fileMaskControl\" required />\n            <mat-icon matSuffix fontIcon=\"icon-info\" matTooltip=\"File mask to select Files (e.g: *.csv, user*.csv...)\"></mat-icon>\n          </mat-form-field>\n          <div class=\"gridContainer\" e2e=\"grid-container\">\n            <dx-data-grid id=\"gridContainer\">\n              <div *dxTemplate=\"let data of 'dobjTemplate'\">\n                <div fxLayout=\"row\">\n                  <mat-icon class=\"file-icon\" fontIcon=\"icon-file-solid\"></mat-icon>\n                  <div>{{data.value}}</div>\n                </div>\n              </div>\n              <div *dxTemplate=\"let data of 'sizeTemplate'\">\n                <span [innerHTML]=\"data.value\"></span>\n              </div>\n              <div *dxTemplate=\"let data of 'dateTemplate'\">\n                <span [innerHTML]=\"data.value | date : 'short'\"></span>\n              </div>\n              <div *dxTemplate=\"let data of 'actionsTemplate'\">\n                <div fxLayout=\"row\" fxLayoutAlign=\"center center\">\n                  <span e2e=\"preview-file\" class=\"preview-icon\" matTooltip=\"Preview File\" (click)=\"previewDialog(data.value)\">\n                    <mat-icon fontIcon=\"icon-show\"></mat-icon>\n                  </span>\n                </div>\n              </div>\n            </dx-data-grid>\n          </div>\n        </div>\n      </div>\n    </mat-card-content>\n  </mat-card>\n\n  <mat-card class=\"margin-9 selectCard\" fxFlex=\"25\" fxFlex.gt-md=\"25\" fxFlex.md=\"25\">\n    <mat-card-header class=\"headerGradient\">\n      <div class=\"mat-body-1\" i18n>Selected Files</div>\n      <span class=\"toolbar-spacer\"></span>\n      <button mat-icon-button matTooltip=\"Clear All Selected\" color=\"warn\" (click)=\"clearSelected()\">\n        <mat-icon fontIcon=\"icon-clear-all\"></mat-icon>\n      </button>\n    </mat-card-header>\n    <mat-divider></mat-divider>\n    <mat-card-content class=\"selected-files\">\n      <mat-list role=\"list\" class=\"selected-files\">\n        <mat-list-item class=\"whiteFrame\" role=\"listitem\" *ngFor=\"let file of selFiles; index as i\">\n          <div class=\"mat-body-2\">{{i + 1}}. &ensp; {{ file.name }}</div>\n        </mat-list-item>\n      </mat-list>\n      <div class=\"results\" fxLayout=\"row\" fxLayoutAlign=\"center center\" *ngIf=\"!fileMask && !selFiles.length\">\n        <span>Use Mask to select files</span>\n      </div>\n      <div class=\"results\" fxLayout=\"row\" fxLayoutAlign=\"center center\" *ngIf=\"fileMask && !selFiles.length\">\n        <span>NO MATCHING RESULTS</span>\n      </div>\n    </mat-card-content>\n  </mat-card>\n\n</div>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/select-rawdata/select-rawdata.component.scss":
/*!***********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/select-rawdata/select-rawdata.component.scss ***!
  \***********************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%; }\n\n.select-view {\n  height: calc(100% - 43px); }\n\n.select-view mat-card {\n    height: 99%; }\n\n.select-view mat-card ::ng-deep .mat-card-header-text {\n      display: none; }\n\n.tree-icon {\n  color: #eecb00;\n  font-size: 20px; }\n\n.toolbar-spacer {\n  flex: 1 1 auto; }\n\n.margin-9 {\n  margin: 3px 3px 0 0;\n  padding-bottom: 0; }\n\n#infoForMask {\n  padding-left: 9px;\n  padding-right: 9px; }\n\n#infoForMask mat-icon {\n    color: #0077be;\n    font-size: 19px; }\n\n#infoForMask ::ng-deep .mat-form-field-wrapper {\n    padding-bottom: 0; }\n\n#infoForMask ::ng-deep .mat-form-field-underline {\n    bottom: 0.25em; }\n\n.selectCard {\n  padding: 0;\n  background-color: #f5f9fc; }\n\n.whiteFrame {\n  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.2), 0 1px 1px 0 rgba(0, 0, 0, 0.14), 0 2px 1px -1px rgba(0, 0, 0, 0.12);\n  background-color: white; }\n\n.headerGradient {\n  padding: 8px 15px;\n  background-color: white;\n  border-top-left-radius: 3px;\n  border-top-right-radius: 3px; }\n\n.results {\n  width: 100%;\n  height: 150px;\n  color: #bdbdbd;\n  text-align: center; }\n\n.results span {\n    font-size: 28px;\n    word-break: break-word; }\n\n.file-icon {\n  color: #73b421;\n  font-size: 18px; }\n\n.tree {\n  padding: 25px 0 0 9px;\n  border-right-style: inset; }\n\n.preview-icon {\n  font-size: 18px;\n  cursor: pointer;\n  color: #0077be; }\n\n.staging-content {\n  height: calc(100% - 55px); }\n\n.mat-icon-button {\n  height: 36px !important; }\n\n.gridContainer {\n  height: calc(100% - 47px) !important; }\n\n.selected-files {\n  padding-top: 0;\n  max-height: calc(100% - 55px) !important;\n  overflow: auto; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2NyZWF0ZS1kYXRhc2V0cy9zZWxlY3QtcmF3ZGF0YS9zZWxlY3QtcmF3ZGF0YS5jb21wb25lbnQuc2NzcyIsIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL3RoZW1lcy9iYXNlL19jb2xvcnMuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUNFLFdBQVcsRUFBQTs7QUFHYjtFQUNFLHlCQUF5QixFQUFBOztBQUQzQjtJQUlJLFdBQVcsRUFBQTs7QUFKZjtNQU9NLGFBQWEsRUFBQTs7QUFLbkI7RUFDRSxjQUF1QjtFQUN2QixlQUFlLEVBQUE7O0FBR2pCO0VBQ0UsY0FBYyxFQUFBOztBQUdoQjtFQUNFLG1CQUFtQjtFQUNuQixpQkFBaUIsRUFBQTs7QUFHbkI7RUFDRSxpQkFBaUI7RUFDakIsa0JBQWtCLEVBQUE7O0FBRnBCO0lBS0ksY0NuQ3FCO0lEb0NyQixlQUFlLEVBQUE7O0FBTm5CO0lBVUksaUJBQWlCLEVBQUE7O0FBVnJCO0lBY0ksY0FBYyxFQUFBOztBQUlsQjtFQUNFLFVBQVU7RUFDVix5QkNMd0IsRUFBQTs7QURRMUI7RUFDRSwrR0FDb0M7RUFDcEMsdUJBQXVCLEVBQUE7O0FBR3pCO0VBQ0UsaUJBQWlCO0VBQ2pCLHVCQUF1QjtFQUN2QiwyQkFBMkI7RUFDM0IsNEJBQTRCLEVBQUE7O0FBRzlCO0VBQ0UsV0FBVztFQUNYLGFBQWE7RUFDYixjQUF5QjtFQUN6QixrQkFBa0IsRUFBQTs7QUFKcEI7SUFPSSxlQUFlO0lBQ2Ysc0JBQXNCLEVBQUE7O0FBSTFCO0VBQ0UsY0N4RHFCO0VEeURyQixlQUFlLEVBQUE7O0FBR2pCO0VBQ0UscUJBQXFCO0VBQ3JCLHlCQUF5QixFQUFBOztBQUczQjtFQUNFLGVBQWU7RUFDZixlQUFlO0VBQ2YsY0MzRnVCLEVBQUE7O0FEOEZ6QjtFQUNFLHlCQUF5QixFQUFBOztBQUczQjtFQUNFLHVCQUF1QixFQUFBOztBQUd6QjtFQUNFLG9DQUFvQyxFQUFBOztBQUd0QztFQUNFLGNBQWM7RUFDZCx3Q0FBd0M7RUFDeEMsY0FBYyxFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy93b3JrYmVuY2gvY29tcG9uZW50cy9jcmVhdGUtZGF0YXNldHMvc2VsZWN0LXJhd2RhdGEvc2VsZWN0LXJhd2RhdGEuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0ICdzcmMvdGhlbWVzL2Jhc2UvY29sb3JzJztcblxuOmhvc3Qge1xuICB3aWR0aDogMTAwJTtcbn1cblxuLnNlbGVjdC12aWV3IHtcbiAgaGVpZ2h0OiBjYWxjKDEwMCUgLSA0M3B4KTtcblxuICBtYXQtY2FyZCB7XG4gICAgaGVpZ2h0OiA5OSU7XG5cbiAgICA6Om5nLWRlZXAgLm1hdC1jYXJkLWhlYWRlci10ZXh0IHtcbiAgICAgIGRpc3BsYXk6IG5vbmU7XG4gICAgfVxuICB9XG59XG5cbi50cmVlLWljb24ge1xuICBjb2xvcjogcmdiKDIzOCwgMjAzLCAwKTtcbiAgZm9udC1zaXplOiAyMHB4O1xufVxuXG4udG9vbGJhci1zcGFjZXIge1xuICBmbGV4OiAxIDEgYXV0bztcbn1cblxuLm1hcmdpbi05IHtcbiAgbWFyZ2luOiAzcHggM3B4IDAgMDtcbiAgcGFkZGluZy1ib3R0b206IDA7XG59XG5cbiNpbmZvRm9yTWFzayB7XG4gIHBhZGRpbmctbGVmdDogOXB4O1xuICBwYWRkaW5nLXJpZ2h0OiA5cHg7XG5cbiAgbWF0LWljb24ge1xuICAgIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIyO1xuICAgIGZvbnQtc2l6ZTogMTlweDtcbiAgfVxuXG4gIDo6bmctZGVlcCAubWF0LWZvcm0tZmllbGQtd3JhcHBlciB7XG4gICAgcGFkZGluZy1ib3R0b206IDA7XG4gIH1cblxuICA6Om5nLWRlZXAgLm1hdC1mb3JtLWZpZWxkLXVuZGVybGluZSB7XG4gICAgYm90dG9tOiAwLjI1ZW07XG4gIH1cbn1cblxuLnNlbGVjdENhcmQge1xuICBwYWRkaW5nOiAwO1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAkYmFja2dyb3VuZC1jb2xvcjtcbn1cblxuLndoaXRlRnJhbWUge1xuICBib3gtc2hhZG93OiAwIDFweCAzcHggMCByZ2JhKDAsIDAsIDAsIDAuMiksIDAgMXB4IDFweCAwIHJnYmEoMCwgMCwgMCwgMC4xNCksXG4gICAgMCAycHggMXB4IC0xcHggcmdiYSgwLCAwLCAwLCAwLjEyKTtcbiAgYmFja2dyb3VuZC1jb2xvcjogd2hpdGU7XG59XG5cbi5oZWFkZXJHcmFkaWVudCB7XG4gIHBhZGRpbmc6IDhweCAxNXB4O1xuICBiYWNrZ3JvdW5kLWNvbG9yOiB3aGl0ZTtcbiAgYm9yZGVyLXRvcC1sZWZ0LXJhZGl1czogM3B4O1xuICBib3JkZXItdG9wLXJpZ2h0LXJhZGl1czogM3B4O1xufVxuXG4ucmVzdWx0cyB7XG4gIHdpZHRoOiAxMDAlO1xuICBoZWlnaHQ6IDE1MHB4O1xuICBjb2xvcjogcmdiKDE4OSwgMTg5LCAxODkpO1xuICB0ZXh0LWFsaWduOiBjZW50ZXI7XG5cbiAgc3BhbiB7XG4gICAgZm9udC1zaXplOiAyOHB4O1xuICAgIHdvcmQtYnJlYWs6IGJyZWFrLXdvcmQ7XG4gIH1cbn1cblxuLmZpbGUtaWNvbiB7XG4gIGNvbG9yOiAkcHJpbWFyeS1ncmVlbjtcbiAgZm9udC1zaXplOiAxOHB4O1xufVxuXG4udHJlZSB7XG4gIHBhZGRpbmc6IDI1cHggMCAwIDlweDtcbiAgYm9yZGVyLXJpZ2h0LXN0eWxlOiBpbnNldDtcbn1cblxuLnByZXZpZXctaWNvbiB7XG4gIGZvbnQtc2l6ZTogMThweDtcbiAgY3Vyc29yOiBwb2ludGVyO1xuICBjb2xvcjogJHByaW1hcnktYmx1ZS1iMjtcbn1cblxuLnN0YWdpbmctY29udGVudCB7XG4gIGhlaWdodDogY2FsYygxMDAlIC0gNTVweCk7XG59XG5cbi5tYXQtaWNvbi1idXR0b24ge1xuICBoZWlnaHQ6IDM2cHggIWltcG9ydGFudDtcbn1cblxuLmdyaWRDb250YWluZXIge1xuICBoZWlnaHQ6IGNhbGMoMTAwJSAtIDQ3cHgpICFpbXBvcnRhbnQ7XG59XG5cbi5zZWxlY3RlZC1maWxlcyB7XG4gIHBhZGRpbmctdG9wOiAwO1xuICBtYXgtaGVpZ2h0OiBjYWxjKDEwMCUgLSA1NXB4KSAhaW1wb3J0YW50O1xuICBvdmVyZmxvdzogYXV0bztcbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/workbench/components/create-datasets/select-rawdata/select-rawdata.component.ts":
/*!*********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/create-datasets/select-rawdata/select-rawdata.component.ts ***!
  \*********************************************************************************************************/
/*! exports provided: SelectRawdataComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SelectRawdataComponent", function() { return SelectRawdataComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/filter */ "./node_modules/lodash/filter.js");
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_filter__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/cloneDeep */ "./node_modules/lodash/cloneDeep.js");
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! devextreme-angular/ui/data-grid */ "./node_modules/devextreme-angular/ui/data-grid.js");
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../../../common/services/dxDataGrid.service */ "./src/app/common/services/dxDataGrid.service.ts");
/* harmony import */ var _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../../../../../common/services/toastMessage.service */ "./src/app/common/services/toastMessage.service.ts");
/* harmony import */ var _rawpreview_dialog_rawpreview_dialog_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../rawpreview-dialog/rawpreview-dialog.component */ "./src/app/modules/workbench/components/create-datasets/rawpreview-dialog/rawpreview-dialog.component.ts");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");
/* harmony import */ var _wb_comp_configs__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../../../wb-comp-configs */ "./src/app/modules/workbench/wb-comp-configs.ts");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");













var SelectRawdataComponent = /** @class */ (function () {
    function SelectRawdataComponent(dialog, dxDataGrid, workBench, notify) {
        this.dialog = dialog;
        this.dxDataGrid = dxDataGrid;
        this.workBench = workBench;
        this.notify = notify;
        this.selFiles = [];
        this.fileMask = '';
        this.fileMaskControl = new _angular_forms__WEBPACK_IMPORTED_MODULE_2__["FormControl"]('', _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].required);
        this.currentPath = '';
        this.nodeID = '';
        this.onSelectFullfilled = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.fileSystemAPI = {
            getDir: this.workBench.getStagingData,
            createDir: this.workBench.createFolder
        };
    }
    SelectRawdataComponent.prototype.ngOnInit = function () {
        this.treeNodes = lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_5__(_wb_comp_configs__WEBPACK_IMPORTED_MODULE_11__["STAGING_TREE"]);
        this.gridConfig = this.getGridConfig();
    };
    SelectRawdataComponent.prototype.ngAfterViewInit = function () {
        var _this = this;
        this.getPageData();
        this.dataGrid.instance.option(this.gridConfig);
        this.fileMaskControl.valueChanges
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_12__["debounceTime"])(1000))
            .subscribe(function (mask) { return _this.maskSearch(mask); });
    };
    SelectRawdataComponent.prototype.ngOnDestroy = function () {
        this.treeNodes = [];
    };
    SelectRawdataComponent.prototype.getPageData = function () {
        var _this = this;
        this.workBench.getStagingData('/').subscribe(function (data) {
            var filteredDataFiles = lodash_filter__WEBPACK_IMPORTED_MODULE_4__(data.data, ['isDirectory', false]);
            _this.reloadDataGrid(filteredDataFiles);
        });
    };
    SelectRawdataComponent.prototype.onFolderSelected = function (_a) {
        var files = _a.files;
        this.reloadDataGrid(files);
        this.clearSelected();
    };
    SelectRawdataComponent.prototype.onUpdate = function (data) {
        var _this = this;
        setTimeout(function () {
            _this.reloadDataGrid(data);
        });
    };
    SelectRawdataComponent.prototype.getGridConfig = function () {
        var _this = this;
        var dataSource = [];
        var columns = [
            {
                caption: 'File',
                dataField: 'name',
                dataType: 'string',
                cellTemplate: 'dobjTemplate',
                width: '66%',
                allowSorting: true,
                sortOrder: 'asc'
            },
            {
                dataField: 'size',
                caption: 'Size',
                width: '15%',
                dataType: 'number',
                cellTemplate: 'sizeTemplate',
                allowSorting: true
            },
            {
                dataField: 'name',
                caption: 'Preview',
                alignment: 'right',
                width: '14%',
                allowFiltering: false,
                cellTemplate: 'actionsTemplate'
            }
        ];
        return this.dxDataGrid.mergeWithDefaultConfig({
            columns: columns,
            dataSource: dataSource,
            searchPanel: {
                visible: false,
                width: 240,
                placeholder: 'Search...'
            },
            height: '100%',
            scrolling: {
                showScrollbar: 'always',
                mode: 'virtual',
                useNative: false
            },
            sorting: {
                mode: 'multiple'
            },
            filterRow: {
                visible: true,
                applyFilter: 'auto'
            },
            headerFilter: {
                visible: false
            },
            showRowLines: false,
            showBorders: false,
            rowAlternationEnabled: true,
            showColumnLines: false,
            selection: {
                mode: 'single'
            },
            onSelectionChanged: function (selectedItems) {
                var currFile = selectedItems.selectedRowsData[0];
                if (currFile) {
                    _this.filePath = currFile.path + "/" + currFile.name;
                    _this.fileMask = currFile.name;
                    _this.fileMaskControl.setValue(_this.fileMask);
                }
                _this.selFiles = [];
                _this.selFiles = selectedItems.selectedRowsData;
            }
        });
    };
    SelectRawdataComponent.prototype.reloadDataGrid = function (data) {
        this.dataGrid.instance.option('dataSource', data);
        this.dataGrid.instance.refresh();
    };
    SelectRawdataComponent.prototype.maskSearch = function (mask) {
        this.fileMask = this.fileMaskControl.value;
        var tempFiles = this.dataGrid.instance.option('dataSource');
        this.selFiles = this.workBench.filterFiles(mask, tempFiles);
        if (this.selFiles.length > 0) {
            this.filePath = this.selFiles[0].path + "/" + this.fileMask;
            this.onSelectFullfilled.emit({
                selectFullfilled: true,
                selectedFiles: this.selFiles,
                filePath: this.filePath
            });
        }
        else {
            this.onSelectFullfilled.emit({
                selectFullfilled: false,
                selectedFiles: this.selFiles,
                filePath: this.filePath
            });
        }
    };
    SelectRawdataComponent.prototype.clearSelected = function () {
        this.selFiles = [];
        this.fileMask = '';
    };
    SelectRawdataComponent.prototype.previewDialog = function (title) {
        var _this = this;
        var path = this.currentPath + "/" + title;
        this.workBench.getRawPreviewData(path).subscribe(function (data) {
            _this.dialog.open(_rawpreview_dialog_rawpreview_dialog_component__WEBPACK_IMPORTED_MODULE_9__["RawpreviewDialogComponent"], {
                minHeight: 500,
                minWidth: 600,
                data: {
                    title: title,
                    rawData: data.data
                }
            });
        });
    };
    /**
     * File upload function.
     * Validates size and type(Allows only txt/csv)
     * If valid then only sends the formdata to upload
     * @param {any} event
     * @memberof SelectRawdataComponent
     */
    SelectRawdataComponent.prototype.fileInput = function (event) {
        var _this = this;
        var filesToUpload = event.srcElement.files;
        var validSize = this.workBench.validateMaxSize(filesToUpload);
        var validType = this.workBench.validateFileTypes(filesToUpload);
        if (validSize && validType) {
            var path = this.currentPath;
            this.workBench.uploadFile(filesToUpload, path).subscribe(function (data) {
                var filteredDataFiles = lodash_filter__WEBPACK_IMPORTED_MODULE_4__(data.data, ['isDirectory', false]);
                _this.reloadDataGrid(filteredDataFiles);
                _this.clearSelected();
            });
        }
        else {
            this.notify.warn('Only ".csv" or ".txt" extension files are supported', 'Unsupported file type');
        }
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ViewChild"])(devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_6__["DxDataGridComponent"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_6__["DxDataGridComponent"])
    ], SelectRawdataComponent.prototype, "dataGrid", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], SelectRawdataComponent.prototype, "onSelectFullfilled", void 0);
    SelectRawdataComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'select-rawdata',
            template: __webpack_require__(/*! ./select-rawdata.component.html */ "./src/app/modules/workbench/components/create-datasets/select-rawdata/select-rawdata.component.html"),
            styles: [__webpack_require__(/*! ./select-rawdata.component.scss */ "./src/app/modules/workbench/components/create-datasets/select-rawdata/select-rawdata.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_material__WEBPACK_IMPORTED_MODULE_3__["MatDialog"],
            _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_7__["DxDataGridService"],
            _services_workbench_service__WEBPACK_IMPORTED_MODULE_10__["WorkbenchService"],
            _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_8__["ToastService"]])
    ], SelectRawdataComponent);
    return SelectRawdataComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/data-objects-page.component.html":
/*!*************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/data-objects-page.component.html ***!
  \*************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"dataSets-view\" fxLayout=\"column\" (window:resize)=\"onResize($event)\">\n  <mat-toolbar class=\"toolbar-white\">\n    <mat-toolbar-row>\n      <div fxFlex=\"47\" fxLayout=\"row\" fxLayoutAlign=\"start center\" fxLayoutGap=\"5px\" e2e=\"data-mode-group\" class=\"data-mode\">\n        <mat-button-toggle-group [(ngModel)]=\"dataView\" [value]=\"dataView\" (change)=\"onDataObjectViewChange()\"\n          #viewType=\"matButtonToggleGroup\">\n          <mat-button-toggle e2e=\"data-sets-view\" value=\"sets\">\n            <mat-icon fontIcon=\"icon-exchange\"></mat-icon>\n            <span i18n>Data Sets</span>\n          </mat-button-toggle>\n          <mat-button-toggle e2e=\"data-pods-view\" value=\"pods\">\n            <mat-icon fontIcon=\"icon-portal-libraries\"></mat-icon>\n            <span i18n>Data Pods</span>\n          </mat-button-toggle>\n        </mat-button-toggle-group>\n        <search-box placeholder=\"Search\" [value]=\"states.searchTerm\" (searchTermChange)=\"applySearchFilter($event)\"\n          [delay]=\"1000\"></search-box>\n      </div>\n      <div fxFlex=\"20\" fxLayout=\"row\" e2e=\"view-mode-group\" class=\"view-mode\">\n        <mat-button-toggle-group [(ngModel)]=\"viewState\" [value]=\"viewState\" (change)=\"onViewChange()\" #viewMode=\"matButtonToggleGroup\">\n          <mat-button-toggle e2e=\"list-view\" matTooltip=\"List view\" value=\"list\">\n            <mat-icon fontIcon=\"icon-list-view\" style=\"font-size: 24px;\"></mat-icon>\n          </mat-button-toggle>\n          <mat-button-toggle e2e=\"card-view\" matTooltip=\"Card view\" value=\"card\">\n            <mat-icon fontIcon=\"icon-tile-view-solid\" style=\"font-size: 21px;\"></mat-icon>\n          </mat-button-toggle>\n        </mat-button-toggle-group>\n      </div>\n      <span fxFlex></span>\n      <div class=\"action-buttons\" fxLayout=\"row\" fxLayoutGap=\"10px\">\n        <mat-slide-toggle *ngIf=\"dataView == 'sets'\" checked=\"false\" (change)=\"togglePoll()\" i18n>Auto Refresh</mat-slide-toggle>\n\n        <button mat-raised-button e2e=\"add-new-data-sets\" color=\"primary\" (click)=\"addDataSet()\">\n          <span i18n>+ Dataset</span>\n        </button>\n\n      </div>\n    </mat-toolbar-row>\n  </mat-toolbar>\n  <div *ngIf=\"dataView == 'sets'\">\n    <div class=\"grid-view\" [style.max-height.px]=\"contentHeight\" *ngIf=\"viewState == 'list'\">\n      <div [style.height.px]=\"contentHeight - 6\">\n        <datasets-grid-page [updater]=\"updater\" [searchTerm]=\"states.searchTermValue\"></datasets-grid-page>\n      </div>\n    </div>\n\n    <div class=\"card-view\" [style.height.px]=\"contentHeight\" [style.max-height.px]=\"contentHeight\" *ngIf=\"viewState == 'card'\">\n      <datasets-card-page [updater]=\"updater\" [searchTerm]=\"states.searchTermValue\"></datasets-card-page>\n    </div>\n  </div>\n\n  <div *ngIf=\"dataView == 'pods'\">\n    <div class=\"grid-view\" [style.max-height.px]=\"contentHeight\" *ngIf=\"viewState == 'list'\">\n      <div [style.height.px]=\"contentHeight - 6\">\n        <datapods-grid-page [updater]=\"dpUpdater\" [searchTerm]=\"states.searchTermValue\">\n        </datapods-grid-page>\n      </div>\n    </div>\n\n    <div class=\"card-view\" [style.height.px]=\"contentHeight\" [style.max-height.px]=\"contentHeight\" *ngIf=\"viewState == 'card'\">\n      <datapods-card-page [updater]=\"dpUpdater\" [searchTerm]=\"states.searchTermValue\">\n      </datapods-card-page>\n    </div>\n  </div>\n\n</div>"

/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/data-objects-page.component.scss":
/*!*************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/data-objects-page.component.scss ***!
  \*************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%; }\n\n.action-buttons .mat-icon-button {\n  font-size: 22px;\n  line-height: 32px !important;\n  height: 34px !important; }\n\n.action-buttons .mat-raised-button {\n  line-height: 32px !important;\n  vertical-align: middle; }\n\n.mat-slide-toggle {\n  padding-top: 5px; }\n\n.icon-search {\n  font-size: 26px !important; }\n\n.toolbar-white {\n  background-color: #fff;\n  height: 45px !important;\n  min-height: 45px !important; }\n\n.md-subhead {\n  color: #0077be;\n  font-weight: 600;\n  text-transform: uppercase; }\n\n.mat-button-toggle-checked {\n  background-color: #0077be;\n  color: white; }\n\n.mat-button-toggle {\n  font-size: 16px;\n  height: 34px; }\n\n::ng-deep .mat-button-toggle-label-content {\n  padding: 0 7px;\n  line-height: 32px !important; }\n\n::ng-deep .mat-button-toggle-label-content .mat-icon {\n    width: auto; }\n\n::ng-deep .mat-button-toggle-label-content > * {\n  vertical-align: bottom !important; }\n\n.view-mode .mat-button-toggle-group {\n  box-shadow: none; }\n\n.view-mode ::ng-deep .mat-button-toggle-checked {\n  background-color: white;\n  color: #0077be; }\n\n.grid-view {\n  padding: 5px;\n  background-color: #f5f9fc; }\n\n.card-view {\n  overflow: auto;\n  background-color: #f5f9fc; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGEtb2JqZWN0cy12aWV3L2RhdGEtb2JqZWN0cy1wYWdlLmNvbXBvbmVudC5zY3NzIiwiL1VzZXJzL2Jhcm5hbXVtdHlhbi9Qcm9qZWN0cy9tb2R1cy9zaXAvc2F3LXdlYi9zcmMvdGhlbWVzL2Jhc2UvX2NvbG9ycy5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUVBO0VBQ0UsV0FBVyxFQUFBOztBQUdiO0VBRUksZUFBZTtFQUNmLDRCQUE0QjtFQUM1Qix1QkFBdUIsRUFBQTs7QUFKM0I7RUFRSSw0QkFBNEI7RUFDNUIsc0JBQXNCLEVBQUE7O0FBSTFCO0VBQ0UsZ0JBQWdCLEVBQUE7O0FBR2xCO0VBQ0UsMEJBQTBCLEVBQUE7O0FBRzVCO0VBQ0Usc0JDTmtCO0VET2xCLHVCQUF1QjtFQUN2QiwyQkFBMkIsRUFBQTs7QUFHN0I7RUFDRSxjQ2hDdUI7RURpQ3ZCLGdCQUFnQjtFQUNoQix5QkFBeUIsRUFBQTs7QUFHM0I7RUFDRSx5QkN0Q3VCO0VEdUN2QixZQUFZLEVBQUE7O0FBR2Q7RUFDRSxlQUFlO0VBQ2YsWUFBWSxFQUFBOztBQUdkO0VBQ0UsY0FBYztFQUNkLDRCQUE0QixFQUFBOztBQUY5QjtJQUtJLFdBQVcsRUFBQTs7QUFJZjtFQUNFLGlDQUFpQyxFQUFBOztBQUduQztFQUVJLGdCQUFnQixFQUFBOztBQUZwQjtFQU1JLHVCQUF1QjtFQUN2QixjQ25FcUIsRUFBQTs7QUR1RXpCO0VBQ0UsWUFBWTtFQUNaLHlCQzVCd0IsRUFBQTs7QUQrQjFCO0VBQ0UsY0FBYztFQUNkLHlCQ2pDd0IsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvd29ya2JlbmNoL2NvbXBvbmVudHMvZGF0YS1vYmplY3RzLXZpZXcvZGF0YS1vYmplY3RzLXBhZ2UuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0ICdzcmMvdGhlbWVzL2Jhc2UvY29sb3JzJztcblxuOmhvc3Qge1xuICB3aWR0aDogMTAwJTtcbn1cblxuLmFjdGlvbi1idXR0b25zIHtcbiAgLm1hdC1pY29uLWJ1dHRvbiB7XG4gICAgZm9udC1zaXplOiAyMnB4O1xuICAgIGxpbmUtaGVpZ2h0OiAzMnB4ICFpbXBvcnRhbnQ7XG4gICAgaGVpZ2h0OiAzNHB4ICFpbXBvcnRhbnQ7XG4gIH1cblxuICAubWF0LXJhaXNlZC1idXR0b24ge1xuICAgIGxpbmUtaGVpZ2h0OiAzMnB4ICFpbXBvcnRhbnQ7XG4gICAgdmVydGljYWwtYWxpZ246IG1pZGRsZTtcbiAgfVxufVxuXG4ubWF0LXNsaWRlLXRvZ2dsZSB7XG4gIHBhZGRpbmctdG9wOiA1cHg7XG59XG5cbi5pY29uLXNlYXJjaCB7XG4gIGZvbnQtc2l6ZTogMjZweCAhaW1wb3J0YW50O1xufVxuXG4udG9vbGJhci13aGl0ZSB7XG4gIGJhY2tncm91bmQtY29sb3I6ICRwcmltYXJ5LXdoaXRlO1xuICBoZWlnaHQ6IDQ1cHggIWltcG9ydGFudDtcbiAgbWluLWhlaWdodDogNDVweCAhaW1wb3J0YW50O1xufVxuXG4ubWQtc3ViaGVhZCB7XG4gIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIyO1xuICBmb250LXdlaWdodDogNjAwO1xuICB0ZXh0LXRyYW5zZm9ybTogdXBwZXJjYXNlO1xufVxuXG4ubWF0LWJ1dHRvbi10b2dnbGUtY2hlY2tlZCB7XG4gIGJhY2tncm91bmQtY29sb3I6ICRwcmltYXJ5LWJsdWUtYjI7XG4gIGNvbG9yOiB3aGl0ZTtcbn1cblxuLm1hdC1idXR0b24tdG9nZ2xlIHtcbiAgZm9udC1zaXplOiAxNnB4O1xuICBoZWlnaHQ6IDM0cHg7XG59XG5cbjo6bmctZGVlcCAubWF0LWJ1dHRvbi10b2dnbGUtbGFiZWwtY29udGVudCB7XG4gIHBhZGRpbmc6IDAgN3B4O1xuICBsaW5lLWhlaWdodDogMzJweCAhaW1wb3J0YW50O1xuXG4gIC5tYXQtaWNvbiB7XG4gICAgd2lkdGg6IGF1dG87XG4gIH1cbn1cblxuOjpuZy1kZWVwIC5tYXQtYnV0dG9uLXRvZ2dsZS1sYWJlbC1jb250ZW50ID4gKiB7XG4gIHZlcnRpY2FsLWFsaWduOiBib3R0b20gIWltcG9ydGFudDtcbn1cblxuLnZpZXctbW9kZSB7XG4gIC5tYXQtYnV0dG9uLXRvZ2dsZS1ncm91cCB7XG4gICAgYm94LXNoYWRvdzogbm9uZTtcbiAgfVxuXG4gIDo6bmctZGVlcCAubWF0LWJ1dHRvbi10b2dnbGUtY2hlY2tlZCB7XG4gICAgYmFja2dyb3VuZC1jb2xvcjogd2hpdGU7XG4gICAgY29sb3I6ICRwcmltYXJ5LWJsdWUtYjI7XG4gIH1cbn1cblxuLmdyaWQtdmlldyB7XG4gIHBhZGRpbmc6IDVweDtcbiAgYmFja2dyb3VuZC1jb2xvcjogJGJhY2tncm91bmQtY29sb3I7XG59XG5cbi5jYXJkLXZpZXcge1xuICBvdmVyZmxvdzogYXV0bztcbiAgYmFja2dyb3VuZC1jb2xvcjogJGJhY2tncm91bmQtY29sb3I7XG59XG4iLCIvLyBCcmFuZGluZyBjb2xvcnNcbiRwcmltYXJ5LWJsdWUtYjE6ICMxYTg5ZDQ7XG4kcHJpbWFyeS1ibHVlLWIyOiAjMDA3N2JlO1xuJHByaW1hcnktYmx1ZS1iMzogIzIwNmJjZTtcbiRwcmltYXJ5LWJsdWUtYjQ6ICMxZDNhYjI7XG5cbiRwcmltYXJ5LWhvdmVyLWJsdWU6ICMxZDYxYjE7XG4kZ3JpZC1ob3Zlci1jb2xvcjogI2Y1ZjlmYztcbiRncmlkLWhlYWRlci1iZy1jb2xvcjogI2Q3ZWFmYTtcbiRncmlkLWhlYWRlci1jb2xvcjogIzBiNGQ5OTtcbiRncmlkLXRleHQtY29sb3I6ICM0NjQ2NDY7XG4kZ3JleS10ZXh0LWNvbG9yOiAjNjM2MzYzO1xuXG4kc2VsZWN0aW9uLWhpZ2hsaWdodC1jb2w6IHJnYmEoMCwgMTQwLCAyNjAsIDAuMik7XG4kcHJpbWFyeS1ncmV5LWcxOiAjZDFkM2QzO1xuJHByaW1hcnktZ3JleS1nMjogIzk5OTtcbiRwcmltYXJ5LWdyZXktZzM6ICM3MzczNzM7XG4kcHJpbWFyeS1ncmV5LWc0OiAjNWM2NjcwO1xuJHByaW1hcnktZ3JleS1nNTogIzMxMzEzMTtcbiRwcmltYXJ5LWdyZXktZzY6ICNmNWY1ZjU7XG4kcHJpbWFyeS1ncmV5LWc3OiAjM2QzZDNkO1xuXG4kcHJpbWFyeS13aGl0ZTogI2ZmZjtcbiRwcmltYXJ5LWJsYWNrOiAjMDAwO1xuJHByaW1hcnktcmVkOiAjYWIwZTI3O1xuJHByaW1hcnktZ3JlZW46ICM3M2I0MjE7XG4kcHJpbWFyeS1vcmFuZ2U6ICNmMDc2MDE7XG5cbiRzZWNvbmRhcnktZ3JlZW46ICM2ZmIzMjA7XG4kc2Vjb25kYXJ5LXllbGxvdzogI2ZmYmUwMDtcbiRzZWNvbmRhcnktb3JhbmdlOiAjZmY5MDAwO1xuJHNlY29uZGFyeS1yZWQ6ICNkOTNlMDA7XG4kc2Vjb25kYXJ5LWJlcnJ5OiAjYWMxNDVhO1xuJHNlY29uZGFyeS1wdXJwbGU6ICM5MTQxOTE7XG5cbiRzdHJpbmctdHlwZS1jb2xvcjogIzQ5OTViMjtcbiRudW1iZXItdHlwZS1jb2xvcjogIzAwYjE4MDtcbiRnZW8tdHlwZS1jb2xvcjogIzg0NWVjMjtcbiRkYXRlLXR5cGUtY29sb3I6ICNkMTk2MjE7XG5cbiR0eXBlLWNoaXAtb3BhY2l0eTogMTtcbiRzdHJpbmctdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRzdHJpbmctdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRudW1iZXItdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRudW1iZXItdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRnZW8tdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRnZW8tdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRkYXRlLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZGF0ZS10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuXG4kcmVwb3J0LWRlc2lnbmVyLXNldHRpbmdzLWJnLWNvbG9yOiAjZjVmOWZjO1xuJGJhY2tncm91bmQtY29sb3I6ICNmNWY5ZmM7XG4iXX0= */"

/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/data-objects-page.component.ts":
/*!***********************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/data-objects-page.component.ts ***!
  \***********************************************************************************************/
/*! exports provided: DataobjectsComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DataobjectsComponent", function() { return DataobjectsComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/common */ "./node_modules/@angular/common/fesm5/common.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var lodash_orderBy__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! lodash/orderBy */ "./node_modules/lodash/orderBy.js");
/* harmony import */ var lodash_orderBy__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(lodash_orderBy__WEBPACK_IMPORTED_MODULE_7__);
/* harmony import */ var _common_services_local_search_service__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../../../../common/services/local-search.service */ "./src/app/common/services/local-search.service.ts");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");
/* harmony import */ var _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../../../../common/services/toastMessage.service */ "./src/app/common/services/toastMessage.service.ts");
/* harmony import */ var _common_components_search_box__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../../../../common/components/search-box */ "./src/app/common/components/search-box/index.ts");












var DataobjectsComponent = /** @class */ (function () {
    function DataobjectsComponent(router, dialog, LocalSearch, workBench, datePipe, _toastMessage) {
        this.router = router;
        this.dialog = dialog;
        this.LocalSearch = LocalSearch;
        this.workBench = workBench;
        this.datePipe = datePipe;
        this._toastMessage = _toastMessage;
        this.availableSets = [];
        this.availableDP = [];
        this.viewState = 'card';
        this.states = {
            searchTerm: '',
            searchTermValue: ''
        };
        this.updater = new rxjs__WEBPACK_IMPORTED_MODULE_5__["BehaviorSubject"]([]);
        this.dpUpdater = new rxjs__WEBPACK_IMPORTED_MODULE_5__["BehaviorSubject"]([]);
        this.dataView = 'sets';
        this.poll = false;
        this.interval = 20000;
    }
    DataobjectsComponent.prototype.ngOnInit = function () {
        this.getDatasets();
    };
    DataobjectsComponent.prototype.ngOnDestroy = function () {
        if (this.poll) {
            this.stopPolling();
        }
    };
    DataobjectsComponent.prototype.startPolling = function () {
        var _this = this;
        /**
         * Calls list datasets/datapods api every 10 seconds or whatever set interval
         *
         * @memberof DatasetsComponent
         */
        this.timer = Object(rxjs__WEBPACK_IMPORTED_MODULE_5__["timer"])(0, this.interval);
        this.timerSubscription = this.timer.subscribe(function () {
            _this.getDatasets();
        });
        this.poll = true;
    };
    DataobjectsComponent.prototype.stopPolling = function () {
        this.timerSubscription && this.timerSubscription.unsubscribe();
        this.poll = false;
    };
    DataobjectsComponent.prototype.getDatasets = function () {
        var _this = this;
        this.workBench.getDatasets().subscribe(function (data) {
            _this.availableSets = lodash_orderBy__WEBPACK_IMPORTED_MODULE_7__(data, 'system.modifiedTime', 'desc');
            _this.updateData(_this.availableSets);
        });
    };
    DataobjectsComponent.prototype.getDatapods = function () {
        var _this = this;
        this.workBench.getListOfSemantic().subscribe(function (data) {
            _this.availableDP = lodash_get__WEBPACK_IMPORTED_MODULE_6__(data, 'contents[0].ANALYZE');
            _this.updateData(_this.availableDP);
        });
    };
    DataobjectsComponent.prototype.updateData = function (data) {
        var _this = this;
        setTimeout(function () {
            _this.dataView === 'sets'
                ? _this.updater.next(data)
                : _this.dpUpdater.next(data);
        });
        setTimeout(function () {
            _this.contentHeight = window.innerHeight - 170;
        });
    };
    /**
     * Toggling from Card and list views
     *
     * @memberof DataobjectsComponent
     */
    DataobjectsComponent.prototype.onViewChange = function () {
        if (this.states.searchTerm !== '') {
            this.applySearchFilter(this.states.searchTerm);
        }
        else {
            this.dataView === 'sets'
                ? this.updateData(this.availableSets)
                : this.updateData(this.availableDP);
        }
    };
    DataobjectsComponent.prototype.applySearchFilter = function (value) {
        var _this = this;
        this.states.searchTerm = value;
        var DS_SEARCH_CONFIG = [
            {
                keyword: 'Data Set Name',
                fieldName: 'system',
                accessor: function (system) { return system.name; }
            },
            {
                keyword: 'Added By',
                fieldName: 'system',
                accessor: function (system) { return system.createdBy; }
            },
            {
                keyword: 'Last Updated',
                fieldName: 'system',
                accessor: function (system) {
                    return _this.datePipe.transform(system.modifiedTime * 1000, 'short');
                }
            },
            {
                keyword: 'Description',
                fieldName: 'system',
                accessor: function (system) { return system.description; }
            }
        ];
        var DP_SEARCH_CONFIG = [
            {
                keyword: 'Datapod Name',
                fieldName: 'metricName'
            },
            {
                keyword: 'Created by',
                fieldName: 'createdBy'
            },
            {
                keyword: 'Last Updated',
                fieldName: 'createdAt',
                accessor: function (createdAt) {
                    return _this.datePipe.transform(createdAt * 1000, 'short');
                }
            },
            {
                keyword: 'Description',
                fieldName: 'description'
            }
        ];
        var SEARCH_CONFIG = [];
        var DATA = [];
        if (this.dataView === 'sets') {
            SEARCH_CONFIG = DS_SEARCH_CONFIG;
            DATA = this.availableSets;
        }
        else {
            SEARCH_CONFIG = DP_SEARCH_CONFIG;
            DATA = this.availableDP;
        }
        var searchCriteria = this.LocalSearch.parseSearchTerm(this.states.searchTerm);
        this.states.searchTermValue = searchCriteria.trimmedTerm;
        this.LocalSearch.doSearch(searchCriteria, DATA, SEARCH_CONFIG).then(function (data) {
            _this.updateData(data);
        }, function (err) {
            _this._toastMessage.error(err.message);
        });
    };
    DataobjectsComponent.prototype.addDataSet = function () {
        this.router.navigate(['workbench', 'dataset', 'add']);
    };
    /**
     * Toggling view from Datasets and Datapods
     *
     * @memberof DataobjectsComponent
     */
    DataobjectsComponent.prototype.onDataObjectViewChange = function () {
        this.stopPolling();
        this.states.searchTerm === '';
        // Have to directly interact with search component to clear and close it while switching views
        this.searchBox.onClose();
        this.dataView === 'pods' ? this.getDatapods() : this.getDatasets();
    };
    DataobjectsComponent.prototype.onResize = function (event) {
        this.contentHeight = event.target.innerHeight - 165;
    };
    DataobjectsComponent.prototype.togglePoll = function () {
        this.poll === true ? this.stopPolling() : this.startPolling();
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ViewChild"])(_common_components_search_box__WEBPACK_IMPORTED_MODULE_11__["SearchBoxComponent"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _common_components_search_box__WEBPACK_IMPORTED_MODULE_11__["SearchBoxComponent"])
    ], DataobjectsComponent.prototype, "searchBox", void 0);
    DataobjectsComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'data-objects-page',
            template: __webpack_require__(/*! ./data-objects-page.component.html */ "./src/app/modules/workbench/components/data-objects-view/data-objects-page.component.html"),
            providers: [_angular_common__WEBPACK_IMPORTED_MODULE_3__["DatePipe"]],
            styles: [__webpack_require__(/*! ./data-objects-page.component.scss */ "./src/app/modules/workbench/components/data-objects-view/data-objects-page.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"],
            _angular_material__WEBPACK_IMPORTED_MODULE_4__["MatDialog"],
            _common_services_local_search_service__WEBPACK_IMPORTED_MODULE_8__["LocalSearchService"],
            _services_workbench_service__WEBPACK_IMPORTED_MODULE_9__["WorkbenchService"],
            _angular_common__WEBPACK_IMPORTED_MODULE_3__["DatePipe"],
            _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_10__["ToastService"]])
    ], DataobjectsComponent);
    return DataobjectsComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datapod-actions/datapod-actions.component.html":
/*!***************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datapod-actions/datapod-actions.component.html ***!
  \***************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<button mat-icon-button class=\"triggerbtn\" [matMenuTriggerFor]=\"datapodmenu\">\n  <mat-icon fontIcon=\"icon-action-solid\"></mat-icon>\n</button>\n<mat-menu #datapodmenu=\"matMenu\">\n  <button mat-menu-item disabled (click)=\"openSQLEditor()\">\n    <mat-icon style=\"margin-right: 0px !important; vertical-align: baseline;\" fontIcon=\"icon-combo-chart\"></mat-icon>\n    <span>Visualize</span>\n  </button>\n  <button mat-menu-item disabled (click)=\"openSQLEditor()\">\n    <mat-icon style=\"margin-right: 0px !important; vertical-align: baseline;\" fontIcon=\"icon-utilities-solid\"></mat-icon>\n    <span>Inspect</span>\n  </button>\n  <button mat-menu-item (click)=\"gotoEdit()\">\n    <mat-icon style=\"margin-right: 0px !important; vertical-align: baseline;\" fontIcon=\"icon-edit\"></mat-icon>\n    <span>Edit</span>\n  </button>\n  <button mat-menu-item disabled>\n    <mat-icon style=\"margin-right: 0px; vertical-align: baseline;\" fontIcon=\"icon-delete\"></mat-icon>\n    <span>delete</span>\n  </button>\n</mat-menu>"

/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datapod-actions/datapod-actions.component.scss":
/*!***************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datapod-actions/datapod-actions.component.scss ***!
  \***************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".triggerbtn {\n  height: 20px;\n  line-height: 20px;\n  font-size: 20px; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGEtb2JqZWN0cy12aWV3L2RhdGFwb2QtYWN0aW9ucy9kYXRhcG9kLWFjdGlvbnMuY29tcG9uZW50LnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7RUFDRSxZQUFZO0VBQ1osaUJBQWlCO0VBQ2pCLGVBQWUsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvd29ya2JlbmNoL2NvbXBvbmVudHMvZGF0YS1vYmplY3RzLXZpZXcvZGF0YXBvZC1hY3Rpb25zL2RhdGFwb2QtYWN0aW9ucy5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIi50cmlnZ2VyYnRuIHtcbiAgaGVpZ2h0OiAyMHB4O1xuICBsaW5lLWhlaWdodDogMjBweDtcbiAgZm9udC1zaXplOiAyMHB4O1xufVxuIl19 */"

/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datapod-actions/datapod-actions.component.ts":
/*!*************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datapod-actions/datapod-actions.component.ts ***!
  \*************************************************************************************************************/
/*! exports provided: DatapodActionsComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DatapodActionsComponent", function() { return DatapodActionsComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");




var DatapodActionsComponent = /** @class */ (function () {
    function DatapodActionsComponent(router, workBench) {
        this.router = router;
        this.workBench = workBench;
    }
    DatapodActionsComponent.prototype.ngOnInit = function () { };
    DatapodActionsComponent.prototype.gotoEdit = function () {
        this.workBench.setDataToLS('dpID', this.dpMetadata.id);
        this.router.navigate(['workbench', 'semantic', 'update']);
    };
    DatapodActionsComponent.prototype.openSQLEditor = function () {
        if (this.dpMetadata.asOfNow.status === 'SUCCESS') {
            this.workBench.setDataToLS('dpMetadata', this.dpMetadata);
            this.router.navigate(['workbench', 'create', 'sql']);
        }
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], DatapodActionsComponent.prototype, "dpMetadata", void 0);
    DatapodActionsComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'datapod-actions',
            template: __webpack_require__(/*! ./datapod-actions.component.html */ "./src/app/modules/workbench/components/data-objects-view/datapod-actions/datapod-actions.component.html"),
            styles: [__webpack_require__(/*! ./datapod-actions.component.scss */ "./src/app/modules/workbench/components/data-objects-view/datapod-actions/datapod-actions.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"], _services_workbench_service__WEBPACK_IMPORTED_MODULE_3__["WorkbenchService"]])
    ], DatapodActionsComponent);
    return DatapodActionsComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datapods-card/datapods-card-page.component.html":
/*!****************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datapods-card/datapods-card-page.component.html ***!
  \****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"dataPods-cardView\" fxLayout=\"row wrap\">\n  <div class=\"dp-card\" fxFlex.gt-md=\"25\" fxFlex.md=\"33\" fxFlex.lt-md=\"50\" fxFlex.lt-sm=\"100\" *ngFor=\"let pod of dataPods\">\n    <mat-card class=\"dataPods-card\">\n      <mat-card-header class=\"SUCCESS\">\n\n        <!-- There is no state for Datapods as of now since they are still logical entities. \n          Once they are physical the below switch case will reflect real value -->\n\n        <div class=\"status-icon\" matTooltip=\"{{pod?.status}}\" ngSwitch=\"SUCCESS\">\n          <mat-icon *ngSwitchCase=\"'FAILED'\" style=\"color: #AB0E27;\" fontIcon=\"icon-warning\"></mat-icon>\n          <mat-icon *ngSwitchCase=\"'SUCCESS'\" fontIcon=\"icon-portal-libraries\"></mat-icon>\n          <mat-icon *ngSwitchCase=\"'INIT'\" fontIcon=\"icon-wip\"></mat-icon>\n        </div>\n        <a class=\"datapod-name\" (click)=\"viewDetails(pod)\" fxFlex [innerHTML]=\"pod.metricName | highlight: searchTerm\"></a>\n        <datapod-actions [dpMetadata]=\"pod\"></datapod-actions>\n      </mat-card-header>\n      <mat-divider></mat-divider>\n      <mat-card-content class=\"mat-body-1\">\n        <div class=\"margin-btm-9\" fxLayout=\"row wrap\">\n          <div fxFlex=\"70\" class=\"mat-body-1\">Data Pods:\n            <span [innerHTML]=\"pod?.dataPods?.numberOfPods\"></span>\n          </div>\n          <div class=\"mat-caption\" fxLayoutAlign=\"end end\" fxFlex=\"30\">Size: </div>\n        </div>\n        <div class=\"descr margin-btm-9\">\n          Description:\n          <span [innerHTML]=\"pod?.description | highlight: searchTerm\"></span>\n        </div>\n        <div class=\"margin-btm-9\" fxLayout=\"row\">\n          <div fxFlex=\"35\" fxLayout=\"column\" fxLayoutAlign=\"start start\">Created by:\n            <span [innerHTML]=\"pod?.createdBy | highlight: searchTerm\"></span>\n          </div>\n          <div fxFlex=\"65\n                    \" fxLayout=\"column\" fxLayoutAlign=\"center end\">Last updated:\n            <div fxLayout=\"row\" style=\"align-items: baseline;\">\n              <span [innerHTML]=\"pod?.modifiedTime * 1000 | date: 'short' | highlight: searchTerm\"> </span>\n            </div>\n          </div>\n        </div>\n      </mat-card-content>\n    </mat-card>\n  </div>\n  <div class=\"dataPods-view_no-results\" *ngIf=\"searchTerm && dataPods.length== 0\">\n    <span i18n>NO MATCHING RESULTS</span>\n  </div>\n</div>"

/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datapods-card/datapods-card-page.component.scss":
/*!****************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datapods-card/datapods-card-page.component.scss ***!
  \****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%;\n  height: 100%;\n  max-height: 100%; }\n\n.md-subhead {\n  color: #1a89d4;\n  font-weight: 600;\n  text-transform: uppercase; }\n\n.btn-card2 {\n  background-color: #636363 !important;\n  color: #fff !important;\n  padding: 2px 15px; }\n\n.branded-column-name {\n  color: #0077be;\n  font-weight: 600; }\n\n.dp-card {\n  min-height: 220px;\n  max-height: 220px;\n  height: 220px !important; }\n\n.dataPods-card {\n  transition: box-shadow 0.5s;\n  border-radius: 3px !important;\n  background-color: #fff;\n  border: solid 1px #d7eafa;\n  padding: 3px !important;\n  margin: 7px;\n  height: 200px !important; }\n\n.dataPods-card:hover {\n    /* prettier-ignore */\n    box-shadow: 0 11px 15px -7px rgba(0, 0, 0, 0.2), 0 24px 38px 3px rgba(0, 0, 0, 0.14), 0 9px 46px 8px rgba(0, 0, 0, 0.12) !important; }\n\n.dataPods-card .mat-title {\n    margin: 0 !important; }\n\n.dataPods-card mat-card-header {\n    padding: 6px; }\n\n.dataPods-card mat-card-header .datapod-name {\n      color: #0077be;\n      font-weight: 300;\n      font-size: 18px;\n      -webkit-line-clamp: 1;\n      overflow: hidden;\n      text-overflow: ellipsis; }\n\n.dataPods-card .status-icon {\n    width: 30px; }\n\n.dataPods-card .status-icon .mat-icon {\n      font-size: 21px; }\n\n.dataPods-card .mat-card-header-text {\n    margin: 0 3px; }\n\n.dataPods-card mat-card-content {\n    padding: 16px;\n    font-size: 14px; }\n\n.dataPods-card span {\n    word-wrap: break-word; }\n\n.dataPods-card .descr {\n    font-size: 13px;\n    overflow: hidden;\n    text-overflow: ellipsis;\n    display: -webkit-box;\n    line-height: 16px;\n    max-height: 32px;\n    min-height: 32px;\n    -webkit-line-clamp: 2;\n    /* number of lines to show */ }\n\n.dataPods-card .descr > span {\n      font-size: 12px; }\n\n.FAILED {\n  border-left: 3px solid #ab0e27; }\n\n.SUCCESS {\n  border-left: 3px solid #73b421; }\n\n.INIT {\n  border-left: 3px solid #f07601; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGEtb2JqZWN0cy12aWV3L2RhdGFwb2RzLWNhcmQvZGF0YXBvZHMtY2FyZC1wYWdlLmNvbXBvbmVudC5zY3NzIiwiL1VzZXJzL2Jhcm5hbXVtdHlhbi9Qcm9qZWN0cy9tb2R1cy9zaXAvc2F3LXdlYi9zcmMvdGhlbWVzL2Jhc2UvX2NvbG9ycy5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUVBO0VBQ0UsV0FBVztFQUNYLFlBQVk7RUFDWixnQkFBZ0IsRUFBQTs7QUFHbEI7RUFDRSxjQ1J1QjtFRFN2QixnQkFBZ0I7RUFDaEIseUJBQXlCLEVBQUE7O0FBRzNCO0VBQ0Usb0NBQTZDO0VBQzdDLHNCQUFnQztFQUNoQyxpQkFBaUIsRUFBQTs7QUFHbkI7RUFDRSxjQ25CdUI7RURvQnZCLGdCQUFnQixFQUFBOztBQUdsQjtFQUNFLGlCQUFpQjtFQUNqQixpQkFBaUI7RUFDakIsd0JBQXdCLEVBQUE7O0FBRzFCO0VBQ0UsMkJBQTJCO0VBQzNCLDZCQUE2QjtFQUM3QixzQkNaa0I7RURhbEIseUJDM0I0QjtFRDRCNUIsdUJBQXVCO0VBQ3ZCLFdBQVc7RUFDWCx3QkFBd0IsRUFBQTs7QUFQMUI7SUFVSSxvQkFBQTtJQUNBLG1JQUcrQyxFQUFBOztBQWRuRDtJQWtCSSxvQkFBb0IsRUFBQTs7QUFsQnhCO0lBc0JJLFlBQVksRUFBQTs7QUF0QmhCO01BeUJNLGNDdERtQjtNRHVEbkIsZ0JBQWdCO01BQ2hCLGVBQWU7TUFDZixxQkFBcUI7TUFDckIsZ0JBQWdCO01BQ2hCLHVCQUF1QixFQUFBOztBQTlCN0I7SUFtQ0ksV0FBVyxFQUFBOztBQW5DZjtNQXNDTSxlQUFlLEVBQUE7O0FBdENyQjtJQTJDSSxhQUFhLEVBQUE7O0FBM0NqQjtJQStDSSxhQUFhO0lBQ2IsZUFBZSxFQUFBOztBQWhEbkI7SUFvREkscUJBQXFCLEVBQUE7O0FBcER6QjtJQXdESSxlQUFlO0lBQ2YsZ0JBQWdCO0lBQ2hCLHVCQUF1QjtJQUN2QixvQkFBb0I7SUFDcEIsaUJBQWlCO0lBQ2pCLGdCQUFnQjtJQUNoQixnQkFBZ0I7SUFDaEIscUJBQXFCO0lBQUUsNEJBQUEsRUFDSzs7QUFoRWhDO01BbUVNLGVBQWUsRUFBQTs7QUFLckI7RUFDRSw4QkNoRm1CLEVBQUE7O0FEbUZyQjtFQUNFLDhCQ25GcUIsRUFBQTs7QURzRnZCO0VBQ0UsOEJDdEZzQixFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy93b3JrYmVuY2gvY29tcG9uZW50cy9kYXRhLW9iamVjdHMtdmlldy9kYXRhcG9kcy1jYXJkL2RhdGFwb2RzLWNhcmQtcGFnZS5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgJ3NyYy90aGVtZXMvYmFzZS9jb2xvcnMnO1xuXG46aG9zdCB7XG4gIHdpZHRoOiAxMDAlO1xuICBoZWlnaHQ6IDEwMCU7XG4gIG1heC1oZWlnaHQ6IDEwMCU7XG59XG5cbi5tZC1zdWJoZWFkIHtcbiAgY29sb3I6ICRwcmltYXJ5LWJsdWUtYjE7XG4gIGZvbnQtd2VpZ2h0OiA2MDA7XG4gIHRleHQtdHJhbnNmb3JtOiB1cHBlcmNhc2U7XG59XG5cbi5idG4tY2FyZDIge1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAkZ3JleS10ZXh0LWNvbG9yICFpbXBvcnRhbnQ7XG4gIGNvbG9yOiAkcHJpbWFyeS13aGl0ZSAhaW1wb3J0YW50O1xuICBwYWRkaW5nOiAycHggMTVweDtcbn1cblxuLmJyYW5kZWQtY29sdW1uLW5hbWUge1xuICBjb2xvcjogJHByaW1hcnktYmx1ZS1iMjtcbiAgZm9udC13ZWlnaHQ6IDYwMDtcbn1cblxuLmRwLWNhcmQge1xuICBtaW4taGVpZ2h0OiAyMjBweDtcbiAgbWF4LWhlaWdodDogMjIwcHg7XG4gIGhlaWdodDogMjIwcHggIWltcG9ydGFudDtcbn1cblxuLmRhdGFQb2RzLWNhcmQge1xuICB0cmFuc2l0aW9uOiBib3gtc2hhZG93IDAuNXM7XG4gIGJvcmRlci1yYWRpdXM6IDNweCAhaW1wb3J0YW50O1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAkcHJpbWFyeS13aGl0ZTtcbiAgYm9yZGVyOiBzb2xpZCAxcHggJGdyaWQtaGVhZGVyLWJnLWNvbG9yO1xuICBwYWRkaW5nOiAzcHggIWltcG9ydGFudDtcbiAgbWFyZ2luOiA3cHg7XG4gIGhlaWdodDogMjAwcHggIWltcG9ydGFudDtcblxuICAmOmhvdmVyIHtcbiAgICAvKiBwcmV0dGllci1pZ25vcmUgKi9cbiAgICBib3gtc2hhZG93OlxuICAgICAgMCAxMXB4IDE1cHggLTdweCByZ2JhKDAsIDAsIDAsIDAuMiksXG4gICAgICAwIDI0cHggMzhweCAzcHggcmdiYSgwLCAwLCAwLCAwLjE0KSxcbiAgICAgIDAgOXB4IDQ2cHggOHB4IHJnYmEoMCwgMCwgMCwgMC4xMikgIWltcG9ydGFudDtcbiAgfVxuXG4gIC5tYXQtdGl0bGUge1xuICAgIG1hcmdpbjogMCAhaW1wb3J0YW50O1xuICB9XG5cbiAgbWF0LWNhcmQtaGVhZGVyIHtcbiAgICBwYWRkaW5nOiA2cHg7XG5cbiAgICAuZGF0YXBvZC1uYW1lIHtcbiAgICAgIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIyO1xuICAgICAgZm9udC13ZWlnaHQ6IDMwMDtcbiAgICAgIGZvbnQtc2l6ZTogMThweDtcbiAgICAgIC13ZWJraXQtbGluZS1jbGFtcDogMTtcbiAgICAgIG92ZXJmbG93OiBoaWRkZW47XG4gICAgICB0ZXh0LW92ZXJmbG93OiBlbGxpcHNpcztcbiAgICB9XG4gIH1cblxuICAuc3RhdHVzLWljb24ge1xuICAgIHdpZHRoOiAzMHB4O1xuXG4gICAgLm1hdC1pY29uIHtcbiAgICAgIGZvbnQtc2l6ZTogMjFweDtcbiAgICB9XG4gIH1cblxuICAubWF0LWNhcmQtaGVhZGVyLXRleHQge1xuICAgIG1hcmdpbjogMCAzcHg7XG4gIH1cblxuICBtYXQtY2FyZC1jb250ZW50IHtcbiAgICBwYWRkaW5nOiAxNnB4O1xuICAgIGZvbnQtc2l6ZTogMTRweDtcbiAgfVxuXG4gIHNwYW4ge1xuICAgIHdvcmQtd3JhcDogYnJlYWstd29yZDtcbiAgfVxuXG4gIC5kZXNjciB7XG4gICAgZm9udC1zaXplOiAxM3B4O1xuICAgIG92ZXJmbG93OiBoaWRkZW47XG4gICAgdGV4dC1vdmVyZmxvdzogZWxsaXBzaXM7XG4gICAgZGlzcGxheTogLXdlYmtpdC1ib3g7XG4gICAgbGluZS1oZWlnaHQ6IDE2cHg7XG4gICAgbWF4LWhlaWdodDogMzJweDtcbiAgICBtaW4taGVpZ2h0OiAzMnB4O1xuICAgIC13ZWJraXQtbGluZS1jbGFtcDogMjsgLyogbnVtYmVyIG9mIGxpbmVzIHRvIHNob3cgKi9cbiAgICAtd2Via2l0LWJveC1vcmllbnQ6IHZlcnRpY2FsO1xuXG4gICAgJiA+IHNwYW4ge1xuICAgICAgZm9udC1zaXplOiAxMnB4O1xuICAgIH1cbiAgfVxufVxuXG4uRkFJTEVEIHtcbiAgYm9yZGVyLWxlZnQ6IDNweCBzb2xpZCAkcHJpbWFyeS1yZWQ7XG59XG5cbi5TVUNDRVNTIHtcbiAgYm9yZGVyLWxlZnQ6IDNweCBzb2xpZCAkcHJpbWFyeS1ncmVlbjtcbn1cblxuLklOSVQge1xuICBib3JkZXItbGVmdDogM3B4IHNvbGlkICRwcmltYXJ5LW9yYW5nZTtcbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datapods-card/datapods-card-page.component.ts":
/*!**************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datapods-card/datapods-card-page.component.ts ***!
  \**************************************************************************************************************/
/*! exports provided: DatapodsCardPageComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DatapodsCardPageComponent", function() { return DatapodsCardPageComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");





var DatapodsCardPageComponent = /** @class */ (function () {
    function DatapodsCardPageComponent(dialog, workbench) {
        this.dialog = dialog;
        this.workbench = workbench;
        this.dataPods = [];
    }
    DatapodsCardPageComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.updaterSubscribtion = this.updater.subscribe(function (data) {
            _this.onUpdate(data);
        });
    };
    DatapodsCardPageComponent.prototype.ngOnDestroy = function () {
        this.updaterSubscribtion.unsubscribe();
    };
    DatapodsCardPageComponent.prototype.onUpdate = function (data) {
        this.dataPods = data;
        setTimeout(function () { }, 1000);
    };
    DatapodsCardPageComponent.prototype.viewDetails = function (metadata) {
        // this.workbench.navigateToDetails(metadata);
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", String)
    ], DatapodsCardPageComponent.prototype, "searchTerm", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_3__["BehaviorSubject"])
    ], DatapodsCardPageComponent.prototype, "updater", void 0);
    DatapodsCardPageComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'datapods-card-page',
            template: __webpack_require__(/*! ./datapods-card-page.component.html */ "./src/app/modules/workbench/components/data-objects-view/datapods-card/datapods-card-page.component.html"),
            styles: [__webpack_require__(/*! ./datapods-card-page.component.scss */ "./src/app/modules/workbench/components/data-objects-view/datapods-card/datapods-card-page.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialog"], _services_workbench_service__WEBPACK_IMPORTED_MODULE_4__["WorkbenchService"]])
    ], DatapodsCardPageComponent);
    return DatapodsCardPageComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datapods-grid/datapods-grid-page.component.html":
/*!****************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datapods-grid/datapods-grid-page.component.html ***!
  \****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"grid-container\" fxFill>\n  <dx-data-grid #dsGrid fxFlex [dataSource]=\"gridData\" [rowAlternationEnabled]=\"true\" [height]=\"'100%'\" [width]=\"'100%'\"\n    keyExpr=\"id\" [showBorders]=\"false\" [showColumnLines]=\"false\" [hoverStateEnabled]=\"true\">\n    <dxo-selection mode=\"single\"></dxo-selection>\n    <dxi-column caption=\"Datapod Name\" dataField=\"metricName\" cellTemplate=\"nameTemplate\"></dxi-column>\n    <dxi-column caption=\"Created By\" dataField=\"createdBy\" cellTemplate=\"cellTemplate\"></dxi-column>\n    <dxi-column caption=\"Updated Time\" dataField=\"modifiedTime\" dataType=\"date\" cellTemplate=\"dateTemplate\"></dxi-column>\n    <dxi-column caption=\"Actions\" dataField=\"id\" cellTemplate=\"actionsTemplate\" [allowFiltering]=\"false\" [allowSorting]=\"false\"></dxi-column>\n    <div *dxTemplate=\"let data of 'nameTemplate'\">\n      <a (click)=\"viewDetails(data.data)\" [innerHTML]=\"data.value | highlight: searchTerm\"></a>\n    </div>\n    <div *dxTemplate=\"let data of 'cellTemplate'\">\n      <span [innerHTML]=\"data.value | highlight: searchTerm\"></span>\n    </div>\n    <div *dxTemplate=\"let data of 'dateTemplate'\">\n      <span [innerHTML]=\"data.value * 1000 | date: 'short' | highlight: searchTerm\"></span>\n    </div>\n    <div *dxTemplate=\"let data of 'actionsTemplate'\">\n      <datapod-actions [dpMetadata]=\"pod\"></datapod-actions>\n    </div>\n    <dxo-scrolling mode=\"virtual\" showScrollbar=\"always\" [useNative]=\"false\"></dxo-scrolling>\n  </dx-data-grid>\n</div>"

/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datapods-grid/datapods-grid-page.component.scss":
/*!****************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datapods-grid/datapods-grid-page.component.scss ***!
  \****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%;\n  height: 100%;\n  overflow: hidden; }\n\n.branded-column-name {\n  color: #0077be;\n  font-weight: 600; }\n\n.grid-container {\n  overflow: hidden;\n  display: block; }\n\n.FAILED {\n  border-left: 3px solid #ab0e27; }\n\n.SUCCESS {\n  border-left: 3px solid #73b421; }\n\n.INIT {\n  border-left: 3px solid #f07601; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGEtb2JqZWN0cy12aWV3L2RhdGFwb2RzLWdyaWQvZGF0YXBvZHMtZ3JpZC1wYWdlLmNvbXBvbmVudC5zY3NzIiwiL1VzZXJzL2Jhcm5hbXVtdHlhbi9Qcm9qZWN0cy9tb2R1cy9zaXAvc2F3LXdlYi9zcmMvdGhlbWVzL2Jhc2UvX2NvbG9ycy5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUVBO0VBQ0UsV0FBVztFQUNYLFlBQVk7RUFDWixnQkFBZ0IsRUFBQTs7QUFHbEI7RUFDRSxjQ1B1QjtFRFF2QixnQkFBZ0IsRUFBQTs7QUFHbEI7RUFDRSxnQkFBZ0I7RUFDaEIsY0FBYyxFQUFBOztBQUdoQjtFQUNFLDhCQ0ttQixFQUFBOztBREZyQjtFQUNFLDhCQ0VxQixFQUFBOztBREN2QjtFQUNFLDhCQ0RzQixFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy93b3JrYmVuY2gvY29tcG9uZW50cy9kYXRhLW9iamVjdHMtdmlldy9kYXRhcG9kcy1ncmlkL2RhdGFwb2RzLWdyaWQtcGFnZS5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgJ3NyYy90aGVtZXMvYmFzZS9jb2xvcnMnO1xuXG46aG9zdCB7XG4gIHdpZHRoOiAxMDAlO1xuICBoZWlnaHQ6IDEwMCU7XG4gIG92ZXJmbG93OiBoaWRkZW47XG59XG5cbi5icmFuZGVkLWNvbHVtbi1uYW1lIHtcbiAgY29sb3I6ICRwcmltYXJ5LWJsdWUtYjI7XG4gIGZvbnQtd2VpZ2h0OiA2MDA7XG59XG5cbi5ncmlkLWNvbnRhaW5lciB7XG4gIG92ZXJmbG93OiBoaWRkZW47XG4gIGRpc3BsYXk6IGJsb2NrO1xufVxuXG4uRkFJTEVEIHtcbiAgYm9yZGVyLWxlZnQ6IDNweCBzb2xpZCAkcHJpbWFyeS1yZWQ7XG59XG5cbi5TVUNDRVNTIHtcbiAgYm9yZGVyLWxlZnQ6IDNweCBzb2xpZCAkcHJpbWFyeS1ncmVlbjtcbn1cblxuLklOSVQge1xuICBib3JkZXItbGVmdDogM3B4IHNvbGlkICRwcmltYXJ5LW9yYW5nZTtcbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datapods-grid/datapods-grid-page.component.ts":
/*!**************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datapods-grid/datapods-grid-page.component.ts ***!
  \**************************************************************************************************************/
/*! exports provided: DatapodsGridPageComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DatapodsGridPageComponent", function() { return DatapodsGridPageComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");



var DatapodsGridPageComponent = /** @class */ (function () {
    function DatapodsGridPageComponent() {
    }
    DatapodsGridPageComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.updaterSubscribtion = this.updater.subscribe(function (data) {
            _this.onUpdate(data);
        });
    };
    DatapodsGridPageComponent.prototype.ngOnDestroy = function () {
        this.updaterSubscribtion.unsubscribe();
    };
    DatapodsGridPageComponent.prototype.onUpdate = function (data) {
        var _this = this;
        if (data.length !== 0) {
            setTimeout(function () {
                _this.reloadDataGrid(data);
            });
        }
    };
    DatapodsGridPageComponent.prototype.reloadDataGrid = function (data) {
        this.gridData = data;
    };
    DatapodsGridPageComponent.prototype.viewDetails = function (metadata) {
        // this.workbench.navigateToDetails(metadata);
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", String)
    ], DatapodsGridPageComponent.prototype, "searchTerm", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_2__["BehaviorSubject"])
    ], DatapodsGridPageComponent.prototype, "updater", void 0);
    DatapodsGridPageComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'datapods-grid-page',
            template: __webpack_require__(/*! ./datapods-grid-page.component.html */ "./src/app/modules/workbench/components/data-objects-view/datapods-grid/datapods-grid-page.component.html"),
            styles: [__webpack_require__(/*! ./datapods-grid-page.component.scss */ "./src/app/modules/workbench/components/data-objects-view/datapods-grid/datapods-grid-page.component.scss")]
        })
    ], DatapodsGridPageComponent);
    return DatapodsGridPageComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/dataset-actions/dataset-actions.component.html":
/*!***************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/dataset-actions/dataset-actions.component.html ***!
  \***************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<button\n  mat-icon-button\n  e2e=\"open-sql-editor-btn\"\n  class=\"triggerbtn\"\n  [matMenuTriggerFor]=\"datasetmenu\"\n>\n  <mat-icon fontIcon=\"icon-action-solid\"></mat-icon>\n</button>\n<mat-menu #datasetmenu=\"matMenu\">\n  <button\n    e2e=\"execute-sql-btn\"\n    mat-menu-item\n    [disabled]=\"dsMetadata?.asOfNow?.status !== 'SUCCESS'\"\n    (click)=\"openSQLEditor()\"\n  >\n    <mat-icon\n      style=\"margin-right: 0px !important; vertical-align: baseline;\"\n      fontIcon=\"icon-query-mode\"\n    ></mat-icon>\n    <span>Execute SQL</span>\n  </button>\n  <button mat-menu-item disabled>\n    <mat-icon\n      style=\"margin-right: 0px; vertical-align: baseline;\"\n      fontIcon=\"icon-delete\"\n    ></mat-icon>\n    <span>delete</span>\n  </button>\n</mat-menu>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/dataset-actions/dataset-actions.component.scss":
/*!***************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/dataset-actions/dataset-actions.component.scss ***!
  \***************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".triggerbtn {\n  height: 20px;\n  line-height: 20px;\n  font-size: 20px; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGEtb2JqZWN0cy12aWV3L2RhdGFzZXQtYWN0aW9ucy9kYXRhc2V0LWFjdGlvbnMuY29tcG9uZW50LnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7RUFDRSxZQUFZO0VBQ1osaUJBQWlCO0VBQ2pCLGVBQWUsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvd29ya2JlbmNoL2NvbXBvbmVudHMvZGF0YS1vYmplY3RzLXZpZXcvZGF0YXNldC1hY3Rpb25zL2RhdGFzZXQtYWN0aW9ucy5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIi50cmlnZ2VyYnRuIHtcbiAgaGVpZ2h0OiAyMHB4O1xuICBsaW5lLWhlaWdodDogMjBweDtcbiAgZm9udC1zaXplOiAyMHB4O1xufVxuIl19 */"

/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/dataset-actions/dataset-actions.component.ts":
/*!*************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/dataset-actions/dataset-actions.component.ts ***!
  \*************************************************************************************************************/
/*! exports provided: DatasetActionsComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DatasetActionsComponent", function() { return DatasetActionsComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");




var DatasetActionsComponent = /** @class */ (function () {
    function DatasetActionsComponent(router, workBench) {
        this.router = router;
        this.workBench = workBench;
    }
    DatasetActionsComponent.prototype.ngOnInit = function () { };
    DatasetActionsComponent.prototype.openSQLEditor = function () {
        if (this.dsMetadata.asOfNow.status === 'SUCCESS') {
            this.workBench.setDataToLS('dsMetadata', this.dsMetadata);
            this.router.navigate(['workbench', 'create', 'sql']);
        }
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], DatasetActionsComponent.prototype, "dsMetadata", void 0);
    DatasetActionsComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'dataset-actions',
            template: __webpack_require__(/*! ./dataset-actions.component.html */ "./src/app/modules/workbench/components/data-objects-view/dataset-actions/dataset-actions.component.html"),
            styles: [__webpack_require__(/*! ./dataset-actions.component.scss */ "./src/app/modules/workbench/components/data-objects-view/dataset-actions/dataset-actions.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"], _services_workbench_service__WEBPACK_IMPORTED_MODULE_3__["WorkbenchService"]])
    ], DatasetActionsComponent);
    return DatasetActionsComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datasets-card/datasets-card-page.component.html":
/*!****************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datasets-card/datasets-card-page.component.html ***!
  \****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"dataSets-cardView\" fxLayout=\"row wrap\">\n  <div\n    class=\"ds-card\"\n    fxFlex.gt-md=\"25\"\n    fxFlex.md=\"33\"\n    fxFlex.lt-md=\"50\"\n    fxFlex.lt-sm=\"100\"\n    *ngFor=\"let sets of dataSets\"\n  >\n    <mat-card class=\"datasets-card\">\n      <mat-card-header class=\"{{ sets?.asOfNow?.status }}\">\n        <div\n          class=\"status-icon\"\n          matTooltip=\"{{ sets?.asOfNow?.status }}\"\n          [ngSwitch]=\"sets?.asOfNow?.status\"\n        >\n          <mat-icon\n            *ngSwitchCase=\"'FAILED'\"\n            style=\"color: #AB0E27;\"\n            fontIcon=\"icon-warning\"\n          ></mat-icon>\n          <mat-icon\n            *ngSwitchCase=\"'SUCCESS'\"\n            fontIcon=\"icon-exchange\"\n          ></mat-icon>\n          <mat-icon *ngSwitchCase=\"'INIT'\" fontIcon=\"icon-wip\"></mat-icon>\n        </div>\n        <a\n          class=\"dataset-name\"\n          e2e=\"data-set-card\"\n          (click)=\"viewDetails(sets)\"\n          fxFlex\n          [innerHTML]=\"sets.system.name | highlight: searchTerm\"\n        ></a>\n        <dataset-actions [dsMetadata]=\"sets\"></dataset-actions>\n      </mat-card-header>\n      <mat-divider></mat-divider>\n      <mat-card-content class=\"mat-body-1\">\n        <div class=\"margin-btm-9\" fxLayout=\"row wrap\">\n          <div fxFlex=\"70\" class=\"mat-body-1\">\n            Data Pods: <span [innerHTML]=\"sets?.dataPods?.numberOfPods\"></span>\n          </div>\n          <div class=\"mat-caption\" fxLayoutAlign=\"end end\" fxFlex=\"30\">\n            Size: {{ sets.size }}\n          </div>\n        </div>\n        <div class=\"descr margin-btm-9\">\n          Description:\n          <span\n            [innerHTML]=\"sets?.system?.description | highlight: searchTerm\"\n          ></span>\n        </div>\n        <div class=\"margin-btm-9\" fxLayout=\"row\">\n          <div fxFlex=\"35\" fxLayout=\"column\" fxLayoutAlign=\"start start\">\n            Added by:\n            <span\n              [innerHTML]=\"sets.system.createdBy | highlight: searchTerm\"\n            ></span>\n          </div>\n          <div\n            fxFlex=\"65\n                    \"\n            fxLayout=\"column\"\n            fxLayoutAlign=\"center end\"\n          >\n            Last updated:\n            <div fxLayout=\"row\" style=\"align-items: baseline;\">\n              <span\n                [innerHTML]=\"\n                  sets.system.modifiedTime * 1000\n                    | date: 'short'\n                    | highlight: searchTerm\n                \"\n              >\n              </span>\n            </div>\n          </div>\n        </div>\n      </mat-card-content>\n    </mat-card>\n  </div>\n  <div\n    class=\"dataSets-view_no-results\"\n    *ngIf=\"searchTerm && dataSets.length == 0\"\n  >\n    <span i18n>NO MATCHING RESULTS</span>\n  </div>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datasets-card/datasets-card-page.component.scss":
/*!****************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datasets-card/datasets-card-page.component.scss ***!
  \****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%;\n  height: 100%;\n  max-height: 100%; }\n\n.md-subhead {\n  color: #1a89d4;\n  font-weight: 600;\n  text-transform: uppercase; }\n\n.btn-card2 {\n  background-color: #636363 !important;\n  color: #fff !important;\n  padding: 2px 15px; }\n\n.branded-column-name {\n  color: #0077be;\n  font-weight: 600; }\n\n.ds-card {\n  min-height: 220px;\n  max-height: 220px;\n  height: 220px !important; }\n\n.datasets-card {\n  transition: box-shadow 0.5s;\n  border-radius: 3px !important;\n  background-color: #fff;\n  border: solid 1px #d7eafa;\n  padding: 3px !important;\n  margin: 7px;\n  height: 200px !important; }\n\n.datasets-card:hover {\n    /* prettier-ignore */\n    box-shadow: 0 11px 15px -7px rgba(0, 0, 0, 0.2), 0 24px 38px 3px rgba(0, 0, 0, 0.14), 0 9px 46px 8px rgba(0, 0, 0, 0.12) !important; }\n\n.datasets-card .mat-title {\n    margin: 0 !important; }\n\n.datasets-card mat-card-header {\n    padding: 6px; }\n\n.datasets-card mat-card-header .dataset-name {\n      color: #0077be;\n      font-weight: 300;\n      font-size: 18px;\n      -webkit-line-clamp: 1;\n      overflow: hidden;\n      text-overflow: ellipsis; }\n\n.datasets-card .status-icon {\n    width: 30px; }\n\n.datasets-card .status-icon .mat-icon {\n      font-size: 21px; }\n\n.datasets-card .mat-card-header-text {\n    margin: 0 3px; }\n\n.datasets-card mat-card-content {\n    padding: 16px;\n    font-size: 14px; }\n\n.datasets-card span {\n    word-wrap: break-word; }\n\n.datasets-card .descr {\n    font-size: 13px;\n    overflow: hidden;\n    text-overflow: ellipsis;\n    display: -webkit-box;\n    line-height: 16px;\n    max-height: 32px;\n    min-height: 32px;\n    -webkit-line-clamp: 2;\n    /* number of lines to show */ }\n\n.datasets-card .descr > span {\n      font-size: 12px; }\n\n.FAILED {\n  border-left: 3px solid #ab0e27; }\n\n.SUCCESS {\n  border-left: 3px solid #73b421; }\n\n.INIT {\n  border-left: 3px solid #f07601; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGEtb2JqZWN0cy12aWV3L2RhdGFzZXRzLWNhcmQvZGF0YXNldHMtY2FyZC1wYWdlLmNvbXBvbmVudC5zY3NzIiwiL1VzZXJzL2Jhcm5hbXVtdHlhbi9Qcm9qZWN0cy9tb2R1cy9zaXAvc2F3LXdlYi9zcmMvdGhlbWVzL2Jhc2UvX2NvbG9ycy5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUVBO0VBQ0UsV0FBVztFQUNYLFlBQVk7RUFDWixnQkFBZ0IsRUFBQTs7QUFHbEI7RUFDRSxjQ1J1QjtFRFN2QixnQkFBZ0I7RUFDaEIseUJBQXlCLEVBQUE7O0FBRzNCO0VBQ0Usb0NBQTZDO0VBQzdDLHNCQUFnQztFQUNoQyxpQkFBaUIsRUFBQTs7QUFHbkI7RUFDRSxjQ25CdUI7RURvQnZCLGdCQUFnQixFQUFBOztBQUdsQjtFQUNFLGlCQUFpQjtFQUNqQixpQkFBaUI7RUFDakIsd0JBQXdCLEVBQUE7O0FBRzFCO0VBQ0UsMkJBQTJCO0VBQzNCLDZCQUE2QjtFQUM3QixzQkNaa0I7RURhbEIseUJDM0I0QjtFRDRCNUIsdUJBQXVCO0VBQ3ZCLFdBQVc7RUFDWCx3QkFBd0IsRUFBQTs7QUFQMUI7SUFVSSxvQkFBQTtJQUNBLG1JQUcrQyxFQUFBOztBQWRuRDtJQWtCSSxvQkFBb0IsRUFBQTs7QUFsQnhCO0lBc0JJLFlBQVksRUFBQTs7QUF0QmhCO01BeUJNLGNDdERtQjtNRHVEbkIsZ0JBQWdCO01BQ2hCLGVBQWU7TUFDZixxQkFBcUI7TUFDckIsZ0JBQWdCO01BQ2hCLHVCQUF1QixFQUFBOztBQTlCN0I7SUFtQ0ksV0FBVyxFQUFBOztBQW5DZjtNQXNDTSxlQUFlLEVBQUE7O0FBdENyQjtJQTJDSSxhQUFhLEVBQUE7O0FBM0NqQjtJQStDSSxhQUFhO0lBQ2IsZUFBZSxFQUFBOztBQWhEbkI7SUFvREkscUJBQXFCLEVBQUE7O0FBcER6QjtJQXdESSxlQUFlO0lBQ2YsZ0JBQWdCO0lBQ2hCLHVCQUF1QjtJQUN2QixvQkFBb0I7SUFDcEIsaUJBQWlCO0lBQ2pCLGdCQUFnQjtJQUNoQixnQkFBZ0I7SUFDaEIscUJBQXFCO0lBQUUsNEJBQUEsRUFDSzs7QUFoRWhDO01BbUVNLGVBQWUsRUFBQTs7QUFLckI7RUFDRSw4QkNoRm1CLEVBQUE7O0FEbUZyQjtFQUNFLDhCQ25GcUIsRUFBQTs7QURzRnZCO0VBQ0UsOEJDdEZzQixFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy93b3JrYmVuY2gvY29tcG9uZW50cy9kYXRhLW9iamVjdHMtdmlldy9kYXRhc2V0cy1jYXJkL2RhdGFzZXRzLWNhcmQtcGFnZS5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgJ3NyYy90aGVtZXMvYmFzZS9jb2xvcnMnO1xuXG46aG9zdCB7XG4gIHdpZHRoOiAxMDAlO1xuICBoZWlnaHQ6IDEwMCU7XG4gIG1heC1oZWlnaHQ6IDEwMCU7XG59XG5cbi5tZC1zdWJoZWFkIHtcbiAgY29sb3I6ICRwcmltYXJ5LWJsdWUtYjE7XG4gIGZvbnQtd2VpZ2h0OiA2MDA7XG4gIHRleHQtdHJhbnNmb3JtOiB1cHBlcmNhc2U7XG59XG5cbi5idG4tY2FyZDIge1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAkZ3JleS10ZXh0LWNvbG9yICFpbXBvcnRhbnQ7XG4gIGNvbG9yOiAkcHJpbWFyeS13aGl0ZSAhaW1wb3J0YW50O1xuICBwYWRkaW5nOiAycHggMTVweDtcbn1cblxuLmJyYW5kZWQtY29sdW1uLW5hbWUge1xuICBjb2xvcjogJHByaW1hcnktYmx1ZS1iMjtcbiAgZm9udC13ZWlnaHQ6IDYwMDtcbn1cblxuLmRzLWNhcmQge1xuICBtaW4taGVpZ2h0OiAyMjBweDtcbiAgbWF4LWhlaWdodDogMjIwcHg7XG4gIGhlaWdodDogMjIwcHggIWltcG9ydGFudDtcbn1cblxuLmRhdGFzZXRzLWNhcmQge1xuICB0cmFuc2l0aW9uOiBib3gtc2hhZG93IDAuNXM7XG4gIGJvcmRlci1yYWRpdXM6IDNweCAhaW1wb3J0YW50O1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAkcHJpbWFyeS13aGl0ZTtcbiAgYm9yZGVyOiBzb2xpZCAxcHggJGdyaWQtaGVhZGVyLWJnLWNvbG9yO1xuICBwYWRkaW5nOiAzcHggIWltcG9ydGFudDtcbiAgbWFyZ2luOiA3cHg7XG4gIGhlaWdodDogMjAwcHggIWltcG9ydGFudDtcblxuICAmOmhvdmVyIHtcbiAgICAvKiBwcmV0dGllci1pZ25vcmUgKi9cbiAgICBib3gtc2hhZG93OlxuICAgICAgMCAxMXB4IDE1cHggLTdweCByZ2JhKDAsIDAsIDAsIDAuMiksXG4gICAgICAwIDI0cHggMzhweCAzcHggcmdiYSgwLCAwLCAwLCAwLjE0KSxcbiAgICAgIDAgOXB4IDQ2cHggOHB4IHJnYmEoMCwgMCwgMCwgMC4xMikgIWltcG9ydGFudDtcbiAgfVxuXG4gIC5tYXQtdGl0bGUge1xuICAgIG1hcmdpbjogMCAhaW1wb3J0YW50O1xuICB9XG5cbiAgbWF0LWNhcmQtaGVhZGVyIHtcbiAgICBwYWRkaW5nOiA2cHg7XG5cbiAgICAuZGF0YXNldC1uYW1lIHtcbiAgICAgIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIyO1xuICAgICAgZm9udC13ZWlnaHQ6IDMwMDtcbiAgICAgIGZvbnQtc2l6ZTogMThweDtcbiAgICAgIC13ZWJraXQtbGluZS1jbGFtcDogMTtcbiAgICAgIG92ZXJmbG93OiBoaWRkZW47XG4gICAgICB0ZXh0LW92ZXJmbG93OiBlbGxpcHNpcztcbiAgICB9XG4gIH1cblxuICAuc3RhdHVzLWljb24ge1xuICAgIHdpZHRoOiAzMHB4O1xuXG4gICAgLm1hdC1pY29uIHtcbiAgICAgIGZvbnQtc2l6ZTogMjFweDtcbiAgICB9XG4gIH1cblxuICAubWF0LWNhcmQtaGVhZGVyLXRleHQge1xuICAgIG1hcmdpbjogMCAzcHg7XG4gIH1cblxuICBtYXQtY2FyZC1jb250ZW50IHtcbiAgICBwYWRkaW5nOiAxNnB4O1xuICAgIGZvbnQtc2l6ZTogMTRweDtcbiAgfVxuXG4gIHNwYW4ge1xuICAgIHdvcmQtd3JhcDogYnJlYWstd29yZDtcbiAgfVxuXG4gIC5kZXNjciB7XG4gICAgZm9udC1zaXplOiAxM3B4O1xuICAgIG92ZXJmbG93OiBoaWRkZW47XG4gICAgdGV4dC1vdmVyZmxvdzogZWxsaXBzaXM7XG4gICAgZGlzcGxheTogLXdlYmtpdC1ib3g7XG4gICAgbGluZS1oZWlnaHQ6IDE2cHg7XG4gICAgbWF4LWhlaWdodDogMzJweDtcbiAgICBtaW4taGVpZ2h0OiAzMnB4O1xuICAgIC13ZWJraXQtbGluZS1jbGFtcDogMjsgLyogbnVtYmVyIG9mIGxpbmVzIHRvIHNob3cgKi9cbiAgICAtd2Via2l0LWJveC1vcmllbnQ6IHZlcnRpY2FsO1xuXG4gICAgJiA+IHNwYW4ge1xuICAgICAgZm9udC1zaXplOiAxMnB4O1xuICAgIH1cbiAgfVxufVxuXG4uRkFJTEVEIHtcbiAgYm9yZGVyLWxlZnQ6IDNweCBzb2xpZCAkcHJpbWFyeS1yZWQ7XG59XG5cbi5TVUNDRVNTIHtcbiAgYm9yZGVyLWxlZnQ6IDNweCBzb2xpZCAkcHJpbWFyeS1ncmVlbjtcbn1cblxuLklOSVQge1xuICBib3JkZXItbGVmdDogM3B4IHNvbGlkICRwcmltYXJ5LW9yYW5nZTtcbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datasets-card/datasets-card-page.component.ts":
/*!**************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datasets-card/datasets-card-page.component.ts ***!
  \**************************************************************************************************************/
/*! exports provided: DatasetsCardPageComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DatasetsCardPageComponent", function() { return DatasetsCardPageComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");





var DatasetsCardPageComponent = /** @class */ (function () {
    function DatasetsCardPageComponent(dialog, workbench) {
        this.dialog = dialog;
        this.workbench = workbench;
        this.dataSets = [];
    }
    DatasetsCardPageComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.updaterSubscribtion = this.updater.subscribe(function (data) {
            _this.onUpdate(data);
        });
    };
    DatasetsCardPageComponent.prototype.ngOnDestroy = function () {
        this.updaterSubscribtion.unsubscribe();
    };
    DatasetsCardPageComponent.prototype.onUpdate = function (data) {
        this.dataSets = data;
    };
    DatasetsCardPageComponent.prototype.viewDetails = function (metadata) {
        this.workbench.navigateToDetails(metadata);
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", String)
    ], DatasetsCardPageComponent.prototype, "searchTerm", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_3__["BehaviorSubject"])
    ], DatasetsCardPageComponent.prototype, "updater", void 0);
    DatasetsCardPageComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'datasets-card-page',
            template: __webpack_require__(/*! ./datasets-card-page.component.html */ "./src/app/modules/workbench/components/data-objects-view/datasets-card/datasets-card-page.component.html"),
            styles: [__webpack_require__(/*! ./datasets-card-page.component.scss */ "./src/app/modules/workbench/components/data-objects-view/datasets-card/datasets-card-page.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialog"], _services_workbench_service__WEBPACK_IMPORTED_MODULE_4__["WorkbenchService"]])
    ], DatasetsCardPageComponent);
    return DatasetsCardPageComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datasets-grid/datasets-grid-page.component.html":
/*!****************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datasets-grid/datasets-grid-page.component.html ***!
  \****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"grid-container\" fxFill>\n  <dx-data-grid id=\"gridContainer\">\n    <div *dxTemplate=\"let data of 'nameCellTemplate'\">\n      <a (click)=\"viewDetails(data.data)\" [innerHTML]=\"data.value | highlight: searchTerm\" e2e=\"data-set-grid-view-mode\"></a>\n    </div>\n    <div *dxTemplate=\"let data of 'creatorCellTemplate'\">\n      <span class=\"clickable\" [innerHTML]=\"data.value | highlight: searchTerm\"></span>\n    </div>\n    <div *dxTemplate=\"let data of 'timecreatedCellTemplate'\">\n      <span class=\"clickable\" [innerHTML]=\"data.value * 1000 | date: 'short' | highlight: searchTerm\"></span>\n    </div>\n    <div *dxTemplate=\"let data of 'actionsCellTemplate'\">\n      <dataset-actions [dsMetadata]=\"data.data\"></dataset-actions>\n    </div>\n  </dx-data-grid>\n</div>"

/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datasets-grid/datasets-grid-page.component.scss":
/*!****************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datasets-grid/datasets-grid-page.component.scss ***!
  \****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%;\n  height: 100%;\n  overflow: hidden; }\n\n.src-img {\n  height: 20px;\n  width: 46px;\n  margin-right: 5%;\n  background-size: contain;\n  background-repeat: no-repeat; }\n\n.btn-card2 {\n  background-color: #636363 !important;\n  color: #FFF !important;\n  padding: 2px 15px; }\n\n.branded-column-name {\n  color: #0077be;\n  font-weight: 600; }\n\n.parquet {\n  background-image: url(\"/assets/img/maprE.png\");\n  background-size: contain;\n  background-repeat: no-repeat; }\n\n.es {\n  background-image: url(\"/assets/svg/elastic.svg\"); }\n\n.grid-container {\n  overflow: hidden;\n  display: block; }\n\n.FAILED {\n  border-left: 3px solid #ab0e27; }\n\n.SUCCESS {\n  border-left: 3px solid #73b421; }\n\n.INIT {\n  border-left: 3px solid #f07601; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGEtb2JqZWN0cy12aWV3L2RhdGFzZXRzLWdyaWQvZGF0YXNldHMtZ3JpZC1wYWdlLmNvbXBvbmVudC5zY3NzIiwiL1VzZXJzL2Jhcm5hbXVtdHlhbi9Qcm9qZWN0cy9tb2R1cy9zaXAvc2F3LXdlYi9zcmMvdGhlbWVzL2Jhc2UvX2NvbG9ycy5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUVBO0VBQ0UsV0FBVztFQUNYLFlBQVk7RUFDWixnQkFBZ0IsRUFBQTs7QUFHbEI7RUFDRSxZQUFZO0VBQ1osV0FBVztFQUNYLGdCQUFnQjtFQUNoQix3QkFBd0I7RUFDeEIsNEJBQTRCLEVBQUE7O0FBRzlCO0VBQ0Usb0NBQW9DO0VBQ3BDLHNCQUFzQjtFQUN0QixpQkFBaUIsRUFBQTs7QUFHbkI7RUFDRSxjQ3JCdUI7RURzQnZCLGdCQUFnQixFQUFBOztBQUdsQjtFQUNFLDhDQUE4QztFQUM5Qyx3QkFBd0I7RUFDeEIsNEJBQTRCLEVBQUE7O0FBRzlCO0VBQ0UsZ0RBQWdELEVBQUE7O0FBR2xEO0VBQ0UsZ0JBQWdCO0VBQ2hCLGNBQWMsRUFBQTs7QUFHaEI7RUFDRSw4QkNuQm1CLEVBQUE7O0FEc0JyQjtFQUNFLDhCQ3RCcUIsRUFBQTs7QUR5QnZCO0VBQ0UsOEJDekJzQixFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy93b3JrYmVuY2gvY29tcG9uZW50cy9kYXRhLW9iamVjdHMtdmlldy9kYXRhc2V0cy1ncmlkL2RhdGFzZXRzLWdyaWQtcGFnZS5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgXCJzcmMvdGhlbWVzL2Jhc2UvY29sb3JzXCI7XG5cbjpob3N0IHtcbiAgd2lkdGg6IDEwMCU7XG4gIGhlaWdodDogMTAwJTtcbiAgb3ZlcmZsb3c6IGhpZGRlbjtcbn1cblxuLnNyYy1pbWcge1xuICBoZWlnaHQ6IDIwcHg7XG4gIHdpZHRoOiA0NnB4O1xuICBtYXJnaW4tcmlnaHQ6IDUlO1xuICBiYWNrZ3JvdW5kLXNpemU6IGNvbnRhaW47XG4gIGJhY2tncm91bmQtcmVwZWF0OiBuby1yZXBlYXQ7XG59XG5cbi5idG4tY2FyZDIge1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAjNjM2MzYzICFpbXBvcnRhbnQ7XG4gIGNvbG9yOiAjRkZGICFpbXBvcnRhbnQ7XG4gIHBhZGRpbmc6IDJweCAxNXB4O1xufVxuXG4uYnJhbmRlZC1jb2x1bW4tbmFtZSB7XG4gIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIyO1xuICBmb250LXdlaWdodDogNjAwO1xufVxuXG4ucGFycXVldCB7XG4gIGJhY2tncm91bmQtaW1hZ2U6IHVybCgnL2Fzc2V0cy9pbWcvbWFwckUucG5nJyk7XG4gIGJhY2tncm91bmQtc2l6ZTogY29udGFpbjtcbiAgYmFja2dyb3VuZC1yZXBlYXQ6IG5vLXJlcGVhdDtcbn1cblxuLmVzIHtcbiAgYmFja2dyb3VuZC1pbWFnZTogdXJsKCcvYXNzZXRzL3N2Zy9lbGFzdGljLnN2ZycpO1xufVxuXG4uZ3JpZC1jb250YWluZXIge1xuICBvdmVyZmxvdzogaGlkZGVuO1xuICBkaXNwbGF5OiBibG9jaztcbn1cblxuLkZBSUxFRCB7XG4gIGJvcmRlci1sZWZ0OiAzcHggc29saWQgJHByaW1hcnktcmVkO1xufVxuXG4uU1VDQ0VTUyB7XG4gIGJvcmRlci1sZWZ0OiAzcHggc29saWQgJHByaW1hcnktZ3JlZW47XG59XG5cbi5JTklUIHtcbiAgYm9yZGVyLWxlZnQ6IDNweCBzb2xpZCAkcHJpbWFyeS1vcmFuZ2U7XG59XG4iLCIvLyBCcmFuZGluZyBjb2xvcnNcbiRwcmltYXJ5LWJsdWUtYjE6ICMxYTg5ZDQ7XG4kcHJpbWFyeS1ibHVlLWIyOiAjMDA3N2JlO1xuJHByaW1hcnktYmx1ZS1iMzogIzIwNmJjZTtcbiRwcmltYXJ5LWJsdWUtYjQ6ICMxZDNhYjI7XG5cbiRwcmltYXJ5LWhvdmVyLWJsdWU6ICMxZDYxYjE7XG4kZ3JpZC1ob3Zlci1jb2xvcjogI2Y1ZjlmYztcbiRncmlkLWhlYWRlci1iZy1jb2xvcjogI2Q3ZWFmYTtcbiRncmlkLWhlYWRlci1jb2xvcjogIzBiNGQ5OTtcbiRncmlkLXRleHQtY29sb3I6ICM0NjQ2NDY7XG4kZ3JleS10ZXh0LWNvbG9yOiAjNjM2MzYzO1xuXG4kc2VsZWN0aW9uLWhpZ2hsaWdodC1jb2w6IHJnYmEoMCwgMTQwLCAyNjAsIDAuMik7XG4kcHJpbWFyeS1ncmV5LWcxOiAjZDFkM2QzO1xuJHByaW1hcnktZ3JleS1nMjogIzk5OTtcbiRwcmltYXJ5LWdyZXktZzM6ICM3MzczNzM7XG4kcHJpbWFyeS1ncmV5LWc0OiAjNWM2NjcwO1xuJHByaW1hcnktZ3JleS1nNTogIzMxMzEzMTtcbiRwcmltYXJ5LWdyZXktZzY6ICNmNWY1ZjU7XG4kcHJpbWFyeS1ncmV5LWc3OiAjM2QzZDNkO1xuXG4kcHJpbWFyeS13aGl0ZTogI2ZmZjtcbiRwcmltYXJ5LWJsYWNrOiAjMDAwO1xuJHByaW1hcnktcmVkOiAjYWIwZTI3O1xuJHByaW1hcnktZ3JlZW46ICM3M2I0MjE7XG4kcHJpbWFyeS1vcmFuZ2U6ICNmMDc2MDE7XG5cbiRzZWNvbmRhcnktZ3JlZW46ICM2ZmIzMjA7XG4kc2Vjb25kYXJ5LXllbGxvdzogI2ZmYmUwMDtcbiRzZWNvbmRhcnktb3JhbmdlOiAjZmY5MDAwO1xuJHNlY29uZGFyeS1yZWQ6ICNkOTNlMDA7XG4kc2Vjb25kYXJ5LWJlcnJ5OiAjYWMxNDVhO1xuJHNlY29uZGFyeS1wdXJwbGU6ICM5MTQxOTE7XG5cbiRzdHJpbmctdHlwZS1jb2xvcjogIzQ5OTViMjtcbiRudW1iZXItdHlwZS1jb2xvcjogIzAwYjE4MDtcbiRnZW8tdHlwZS1jb2xvcjogIzg0NWVjMjtcbiRkYXRlLXR5cGUtY29sb3I6ICNkMTk2MjE7XG5cbiR0eXBlLWNoaXAtb3BhY2l0eTogMTtcbiRzdHJpbmctdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRzdHJpbmctdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRudW1iZXItdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRudW1iZXItdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRnZW8tdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRnZW8tdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRkYXRlLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZGF0ZS10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuXG4kcmVwb3J0LWRlc2lnbmVyLXNldHRpbmdzLWJnLWNvbG9yOiAjZjVmOWZjO1xuJGJhY2tncm91bmQtY29sb3I6ICNmNWY5ZmM7XG4iXX0= */"

/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/datasets-grid/datasets-grid-page.component.ts":
/*!**************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/datasets-grid/datasets-grid-page.component.ts ***!
  \**************************************************************************************************************/
/*! exports provided: DatasetsGridPageComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DatasetsGridPageComponent", function() { return DatasetsGridPageComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! devextreme-angular/ui/data-grid */ "./node_modules/devextreme-angular/ui/data-grid.js");
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../../../common/services/dxDataGrid.service */ "./src/app/common/services/dxDataGrid.service.ts");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");






var DatasetsGridPageComponent = /** @class */ (function () {
    function DatasetsGridPageComponent(dxDataGrid, workbench) {
        this.dxDataGrid = dxDataGrid;
        this.workbench = workbench;
    }
    DatasetsGridPageComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.gridConfig = this.getGridConfig();
        this.updaterSubscribtion = this.updater.subscribe(function (data) {
            _this.onUpdate(data);
        });
    };
    DatasetsGridPageComponent.prototype.ngAfterViewInit = function () {
        this.dataGrid.instance.option(this.gridConfig);
    };
    DatasetsGridPageComponent.prototype.ngOnDestroy = function () {
        this.updaterSubscribtion.unsubscribe();
    };
    DatasetsGridPageComponent.prototype.onUpdate = function (data) {
        var _this = this;
        setTimeout(function () {
            _this.reloadDataGrid(data);
        });
    };
    DatasetsGridPageComponent.prototype.getGridConfig = function () {
        var dataSource = [];
        var columns = [
            {
                caption: 'Data Set Name',
                dataField: 'system.name',
                alignment: 'left',
                width: '20%',
                cellTemplate: 'nameCellTemplate',
                cssClass: 'branded-column-name'
            },
            {
                dataField: 'system.description',
                caption: 'Description',
                width: '25%',
                dataType: 'String',
                cellTemplate: 'creatorCellTemplate'
            },
            {
                caption: 'Size',
                dataField: 'system.numberOfFiles',
                dataType: 'number',
                width: '10%'
            },
            {
                dataField: 'system.createdBy',
                caption: 'Added By',
                width: '13%',
                dataType: 'string',
                cellTemplate: 'creatorCellTemplate'
            },
            {
                dataField: 'dataPods.numberOfPods',
                caption: 'Data Pods',
                width: '8%',
                dataType: 'number'
            },
            {
                dataField: 'system.modifiedTime',
                caption: 'Last Updated',
                cellTemplate: 'timecreatedCellTemplate',
                width: '12%',
                dataType: 'date',
                alignment: 'right'
            },
            {
                dataField: 'asOfNow.status',
                caption: 'Status',
                width: '7%',
                alignment: 'center'
            },
            {
                dataField: '_id',
                caption: 'Actions',
                cellTemplate: 'actionsCellTemplate',
                width: '5%'
            }
        ];
        return this.dxDataGrid.mergeWithDefaultConfig({
            columns: columns,
            dataSource: dataSource,
            height: '100%',
            width: '100%',
            headerFilter: {
                visible: false
            },
            sorting: {
                mode: 'multiple'
            },
            scrolling: {
                showScrollbar: 'always',
                mode: 'virtual',
                useNative: false
            },
            filterRow: {
                visible: false,
                applyFilter: 'auto'
            },
            showRowLines: false,
            showBorders: false,
            rowAlternationEnabled: true,
            showColumnLines: false,
            hoverStateEnabled: true
        });
    };
    DatasetsGridPageComponent.prototype.reloadDataGrid = function (data) {
        this.dataGrid.instance.option('dataSource', data);
        this.dataGrid.instance.refresh();
    };
    DatasetsGridPageComponent.prototype.viewDetails = function (metadata) {
        this.workbench.navigateToDetails(metadata);
    };
    DatasetsGridPageComponent.prototype.sizeCalculator = function (size) {
        var a = size;
        return a + " B";
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", String)
    ], DatasetsGridPageComponent.prototype, "searchTerm", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_2__["BehaviorSubject"])
    ], DatasetsGridPageComponent.prototype, "updater", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ViewChild"])(devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3__["DxDataGridComponent"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3__["DxDataGridComponent"])
    ], DatasetsGridPageComponent.prototype, "dataGrid", void 0);
    DatasetsGridPageComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'datasets-grid-page',
            template: __webpack_require__(/*! ./datasets-grid-page.component.html */ "./src/app/modules/workbench/components/data-objects-view/datasets-grid/datasets-grid-page.component.html"),
            styles: [__webpack_require__(/*! ./datasets-grid-page.component.scss */ "./src/app/modules/workbench/components/data-objects-view/datasets-grid/datasets-grid-page.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_4__["DxDataGridService"],
            _services_workbench_service__WEBPACK_IMPORTED_MODULE_5__["WorkbenchService"]])
    ], DatasetsGridPageComponent);
    return DatasetsGridPageComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/data-objects-view/index.ts":
/*!*************************************************************************!*\
  !*** ./src/app/modules/workbench/components/data-objects-view/index.ts ***!
  \*************************************************************************/
/*! exports provided: DataobjectsComponent, DatapodsCardPageComponent, DatapodsGridPageComponent, DatasetsCardPageComponent, DatasetsGridPageComponent, DatasetActionsComponent, DatapodActionsComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _data_objects_page_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./data-objects-page.component */ "./src/app/modules/workbench/components/data-objects-view/data-objects-page.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DataobjectsComponent", function() { return _data_objects_page_component__WEBPACK_IMPORTED_MODULE_0__["DataobjectsComponent"]; });

/* harmony import */ var _datapods_card_datapods_card_page_component__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./datapods-card/datapods-card-page.component */ "./src/app/modules/workbench/components/data-objects-view/datapods-card/datapods-card-page.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DatapodsCardPageComponent", function() { return _datapods_card_datapods_card_page_component__WEBPACK_IMPORTED_MODULE_1__["DatapodsCardPageComponent"]; });

/* harmony import */ var _datapods_grid_datapods_grid_page_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./datapods-grid/datapods-grid-page.component */ "./src/app/modules/workbench/components/data-objects-view/datapods-grid/datapods-grid-page.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DatapodsGridPageComponent", function() { return _datapods_grid_datapods_grid_page_component__WEBPACK_IMPORTED_MODULE_2__["DatapodsGridPageComponent"]; });

/* harmony import */ var _datasets_card_datasets_card_page_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./datasets-card/datasets-card-page.component */ "./src/app/modules/workbench/components/data-objects-view/datasets-card/datasets-card-page.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DatasetsCardPageComponent", function() { return _datasets_card_datasets_card_page_component__WEBPACK_IMPORTED_MODULE_3__["DatasetsCardPageComponent"]; });

/* harmony import */ var _datasets_grid_datasets_grid_page_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./datasets-grid/datasets-grid-page.component */ "./src/app/modules/workbench/components/data-objects-view/datasets-grid/datasets-grid-page.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DatasetsGridPageComponent", function() { return _datasets_grid_datasets_grid_page_component__WEBPACK_IMPORTED_MODULE_4__["DatasetsGridPageComponent"]; });

/* harmony import */ var _dataset_actions_dataset_actions_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./dataset-actions/dataset-actions.component */ "./src/app/modules/workbench/components/data-objects-view/dataset-actions/dataset-actions.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DatasetActionsComponent", function() { return _dataset_actions_dataset_actions_component__WEBPACK_IMPORTED_MODULE_5__["DatasetActionsComponent"]; });

/* harmony import */ var _datapod_actions_datapod_actions_component__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./datapod-actions/datapod-actions.component */ "./src/app/modules/workbench/components/data-objects-view/datapod-actions/datapod-actions.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DatapodActionsComponent", function() { return _datapod_actions_datapod_actions_component__WEBPACK_IMPORTED_MODULE_6__["DatapodActionsComponent"]; });










/***/ }),

/***/ "./src/app/modules/workbench/components/dataset-detailedView/dataset-detail-view.component.html":
/*!******************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/dataset-detailedView/dataset-detail-view.component.html ***!
  \******************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"details-container\">\n  <div class=\"details-header\" fxLayout=\"row\" fxLayoutAlign=\"center center\">\n    <button mat-button class=\"bck-btn\">\n      <mat-icon fontIcon=\"icon-arrow-left\" (click)=\"backToDS()\"></mat-icon>\n    </button>\n    <div class=\"ds-name\" fxFlex fxLayout=\"row\" fxLayoutAlign=\"start center\" fxLayoutGap=\"10px\">\n      <span>Selected Dataset: </span>\n      <mat-chip-list>\n        <mat-chip>{{dsMetadata.system.name}}</mat-chip>\n      </mat-chip-list>\n      <div class=\"status-icon\" matTooltip=\"{{dsMetadata.asOfNow.status}}\" [ngSwitch]=\"dsMetadata.asOfNow.status\">\n        <mat-icon *ngSwitchCase=\"'FAILED'\" style=\"color: #AB0E27;\" fontIcon=\"icon-warning\"></mat-icon>\n        <mat-icon *ngSwitchCase=\"'SUCCESS'\" style=\"color: #73B421;\" fontIcon=\"icon-exchange\"></mat-icon>\n        <!-- <img *ngSwitchCase=\"'SUCCESS'\" src=\"../../../../../../../../../assets/svg/happy.svg\" height=\"24\" width=\"24\" /> -->\n        <mat-icon *ngSwitchCase=\"'INIT'\" fontIcon=\"icon-wip\"></mat-icon>\n      </div>\n    </div>\n  </div>\n  <div class=\"details-body\">\n    <mat-tab-group dynamicHeight=\"false\" (selectedTabChange)=\"tabChanged($event)\">\n      <mat-tab label=\"Summary\">\n        <div class=\"data-summary-view\" e2e=\"data-set-summary-preview\" fxLayout=\"row\" fxFill fxFlex fxLayoutGap=\"10px\">\n          <mat-card class=\"details-card\" fxLayout=\"column\" fxFlex=\"60\">\n            <div class=\"ds-details\">\n              <span class=\"details-title mat-subheading-2\">Dataset Details</span>\n              <mat-divider></mat-divider>\n              <div class=\"details-content\" fxLayout=\"row\">\n                <span class=\"prop-name\">Dataset Name :</span>\n                <span class=\"prop-value\">{{dsMetadata.system.name}}</span>\n              </div>\n              <div class=\"details-content\" fxLayout=\"row\">\n                <span class=\"prop-name\">Dataset Description :</span>\n                <span class=\"prop-value\">{{dsMetadata.system.description}}</span>\n              </div>\n              <div class=\"details-content\" fxLayout=\"row\">\n                <span class=\"prop-name\">Execution Status :</span>\n                <span class=\"prop-value status_{{dsMetadata.asOfNow.status}}\">{{dsMetadata.asOfNow.status}}</span>\n              </div>\n              <div class=\"details-content\" fxLayout=\"row\">\n                <span class=\"prop-name\">Format :</span>\n                <span class=\"prop-value\">{{dsMetadata.system.format}}</span>\n              </div>\n              <div class=\"details-content\" fxLayout=\"row\">\n                <span class=\"prop-name\">Number of Files :</span>\n                <span class=\"prop-value\">{{dsMetadata.system.numberOfFiles}}</span>\n              </div>\n            </div>\n            <h2></h2>\n            <div class=\"ds-details\">\n              <span class=\"details-title mat-subheading-2\">Audit Log</span>\n              <mat-divider></mat-divider>\n              <div class=\"details-content\" fxLayout=\"row\">\n                <span class=\"prop-name\">Created By :</span>\n                <span class=\"prop-value\">{{dsMetadata.system.user}}</span>\n              </div>\n              <div class=\"details-content\" fxLayout=\"row\">\n                <span class=\"prop-name\">Started :</span>\n                <span class=\"prop-value\">{{dsMetadata.asOfNow.started}}</span>\n              </div>\n              <div class=\"details-content\" fxLayout=\"row\">\n                <span class=\"prop-name\">Finished :</span>\n                <span class=\"prop-value\">{{dsMetadata.asOfNow.finished}}</span>\n              </div>\n            </div>\n          </mat-card>\n          <mat-card class=\"details-card details-grid\" fxFlex=\"40\">\n            <div fxFill>\n              <dx-data-grid fxFlex [dataSource]=\"dsMetadata?.schema?.fields\" [rowAlternationEnabled]=\"true\" [height]=\"'98%'\" [width]=\"'100%'\" style=\"position:absolute;top:0;bottom:0;left:0;bottom:0;\"\n                [showBorders]=\"false\">\n                <dxi-column caption=\"Field Name\" dataField=\"name\"></dxi-column>\n                <dxi-column caption=\"Data Type\" dataField=\"type\"></dxi-column>\n                <dxo-scrolling mode=\"virtual\" showScrollbar=\"always\" [useNative]=\"false\"></dxo-scrolling>\n                <dxo-filter-row [visible]=\"true\" applyFilter=\"auto\"></dxo-filter-row>\n                <dxo-header-filter [visible]=\"true\"></dxo-header-filter>\n              </dx-data-grid>\n            </div>\n          </mat-card>\n        </div>\n      </mat-tab>\n      <mat-tab  label=\"Data Preview\" [disabled]=\"previewStatus === 'failed'\">\n        <div e2e=\"data-set-detial-preview\" fxLayout=\"row\" class=\"data-cont\" fxFlex fxLayoutAlign=\"center center\">\n          <mat-card class=\"data-grid\" fxFlex=\"100\">\n            <dx-data-grid #dpGrid id=\"dpGrid\" *ngIf=\"previewStatus === 'success'\" fxFlex [dataSource]=\"previewData\" [height]=\"'99.5%'\" [width]=\"'100%'\" [allowColumnResizing]=\"true\" style=\"position:absolute;top:0;bottom:0;left:0;bottom:0;\"\n              [columnAutoWidth]=\"true\" [rowAlternationEnabled]=\"true\" [showBorders]=\"false\">\n              <dxo-scrolling mode=\"virtual\" showScrollbar=\"always\" [useNative]=\"false\"></dxo-scrolling>\n              <dxo-filter-row [visible]=\"true\" applyFilter=\"auto\"></dxo-filter-row>\n              <dxo-header-filter [visible]=\"false\"></dxo-header-filter>\n              <dxo-load-panel [enabled]=\"false\">\n              </dxo-load-panel>\n            </dx-data-grid>\n            <div class=\"loading\" fxLayout=\"column\" fxLayoutAlign=\"center center\" *ngIf=\"previewStatus === 'queued'\">\n              <span class=\"load-gif\">Generating Preview... </span>\n            </div>\n          </mat-card>\n        </div>\n      </mat-tab>\n    </mat-tab-group>\n  </div>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/dataset-detailedView/dataset-detail-view.component.scss":
/*!******************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/dataset-detailedView/dataset-detail-view.component.scss ***!
  \******************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%;\n  height: calc(100% - 3px);\n  background-color: #f5f9fc;\n  overflow: auto; }\n\n.details-container {\n  height: calc(100% - 4px);\n  padding: 0 4px 4px;\n  background-color: #f5f9fc; }\n\n.details-container mat-tab-group {\n    max-width: 100%;\n    height: 100%; }\n\n.details-container .details-header {\n    color: rgba(0, 0, 0, 0.54);\n    background: rgba(0, 0, 0, 0.03);\n    padding: 8px 0;\n    height: 50px;\n    font-size: 18px;\n    font-weight: 500;\n    border: 2px solid white; }\n\n.details-container .details-header .mat-icon {\n      font-size: 26px; }\n\n.details-container .details-header .mat-chip {\n      color: rgba(0, 0, 0, 0.54);\n      font-size: 16px; }\n\n.details-container .details-header .status-icon {\n      width: 36px; }\n\n.details-container .details-header .status-icon .mat-icon {\n        font-size: 24px;\n        vertical-align: middle; }\n\n.details-container .details-body {\n    background-color: #fff;\n    height: calc(100% - 50px); }\n\n.details-container ::ng-deep .mat-tab-body {\n    background-color: #f5f9fc; }\n\n.details-container ::ng-deep .mat-tab-label-active {\n    color: #0077be; }\n\n.details-container ::ng-deep .mat-tab-body-wrapper {\n    height: 100%; }\n\n.details-container ::ng-deep .mat-tab-body-wrapper .mat-tab-body {\n      overflow: hidden !important; }\n\n.details-container ::ng-deep .mat-tab-body-wrapper .mat-tab-body .mat-tab-body-content {\n        overflow: hidden !important; }\n\n.details-container ::ng-deep .mat-tab-label,\n  .details-container .mat-tab-link {\n    font-size: 17px;\n    font-weight: 600; }\n\n.details-container .details-card {\n    margin: 3px; }\n\n.details-container .details-card .details-title {\n      font-size: 16px;\n      font-weight: 600;\n      text-align: left;\n      color: #555; }\n\n.details-container .details-card .details-content {\n      font-size: 14px;\n      font-weight: normal;\n      line-height: 1.43; }\n\n.details-container .details-card .mat-divider {\n      position: relative;\n      padding-bottom: 5px; }\n\n.details-container .details-card .prop-name {\n      color: #727272;\n      width: 136px;\n      line-height: 1.43; }\n\n.details-container .details-card .prop-value {\n      color: #555; }\n\n.details-container .details-card .status_INIT {\n      color: #f07601; }\n\n.details-container .details-card .status_SUCCESS {\n      color: #73b421; }\n\n.details-container .details-card .status_FAILED {\n      color: #ab0e27; }\n\n.details-container .details-grid {\n    padding: 3px; }\n\n.details-container .data-cont {\n    height: 100%; }\n\n.details-container .data-cont .data-grid {\n      padding: 3px;\n      height: 100%;\n      width: 100%;\n      overflow: auto;\n      max-height: 100%; }\n\n.details-container .loading {\n    width: 100%;\n    height: 100%;\n    color: #bdbdbd;\n    text-align: center;\n    background-color: #fff; }\n\n.details-container .loading span {\n      font-size: 28px;\n      word-break: break-word; }\n\n.details-container .loading .load-gif {\n      height: 70%;\n      width: 70%;\n      background-image: url(\"/assets/svg/loading-ripple.svg\");\n      background-size: contain;\n      background-repeat: no-repeat;\n      background-position: center; }\n\n.details-container ::ng-deep .dx-freespace-row {\n    height: 0 !important; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGFzZXQtZGV0YWlsZWRWaWV3L2RhdGFzZXQtZGV0YWlsLXZpZXcuY29tcG9uZW50LnNjc3MiLCIvVXNlcnMvYmFybmFtdW10eWFuL1Byb2plY3RzL21vZHVzL3NpcC9zYXctd2ViL3NyYy90aGVtZXMvYmFzZS9fY29sb3JzLnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSxXQUFXO0VBQ1gsd0JBQXdCO0VBQ3hCLHlCQzBDd0I7RUR6Q3hCLGNBQWMsRUFBQTs7QUFHaEI7RUFDRSx3QkFBd0I7RUFDeEIsa0JBQWtCO0VBQ2xCLHlCQ21Dd0IsRUFBQTs7QUR0QzFCO0lBTUksZUFBZTtJQUNmLFlBQVksRUFBQTs7QUFQaEI7SUFXSSwwQkFBMEI7SUFDMUIsK0JBQStCO0lBQy9CLGNBQWM7SUFDZCxZQUFZO0lBQ1osZUFBZTtJQUNmLGdCQUFnQjtJQUNoQix1QkFBdUIsRUFBQTs7QUFqQjNCO01Bb0JNLGVBQWUsRUFBQTs7QUFwQnJCO01Bd0JNLDBCQUEwQjtNQUMxQixlQUFlLEVBQUE7O0FBekJyQjtNQTZCTSxXQUFXLEVBQUE7O0FBN0JqQjtRQWdDUSxlQUFlO1FBQ2Ysc0JBQXNCLEVBQUE7O0FBakM5QjtJQXVDSSxzQkFBc0I7SUFDdEIseUJBQXlCLEVBQUE7O0FBeEM3QjtJQTRDSSx5QkNOc0IsRUFBQTs7QUR0QzFCO0lBZ0RJLGNDdkRxQixFQUFBOztBRE96QjtJQW9ESSxZQUFZLEVBQUE7O0FBcERoQjtNQXVETSwyQkFBMkIsRUFBQTs7QUF2RGpDO1FBMERRLDJCQUEyQixFQUFBOztBQTFEbkM7O0lBaUVJLGVBQWU7SUFDZixnQkFBZ0IsRUFBQTs7QUFsRXBCO0lBc0VJLFdBQVcsRUFBQTs7QUF0RWY7TUF5RU0sZUFBZTtNQUNmLGdCQUFnQjtNQUNoQixnQkFBZ0I7TUFDaEIsV0FBVyxFQUFBOztBQTVFakI7TUFnRk0sZUFBZTtNQUNmLG1CQUFtQjtNQUNuQixpQkFBaUIsRUFBQTs7QUFsRnZCO01Bc0ZNLGtCQUFrQjtNQUNsQixtQkFBbUIsRUFBQTs7QUF2RnpCO01BMkZNLGNBQWM7TUFDZCxZQUFZO01BQ1osaUJBQWlCLEVBQUE7O0FBN0Z2QjtNQWlHTSxXQUFXLEVBQUE7O0FBakdqQjtNQXFHTSxjQ3BGa0IsRUFBQTs7QURqQnhCO01BeUdNLGNDekZpQixFQUFBOztBRGhCdkI7TUE2R00sY0M5RmUsRUFBQTs7QURmckI7SUFrSEksWUFBWSxFQUFBOztBQWxIaEI7SUFzSEksWUFBWSxFQUFBOztBQXRIaEI7TUF5SE0sWUFBWTtNQUNaLFlBQVk7TUFDWixXQUFXO01BQ1gsY0FBYztNQUNkLGdCQUFnQixFQUFBOztBQTdIdEI7SUFrSUksV0FBVztJQUNYLFlBQVk7SUFDWixjQUF5QjtJQUN6QixrQkFBa0I7SUFDbEIsc0JBQXNCLEVBQUE7O0FBdEkxQjtNQXlJTSxlQUFlO01BQ2Ysc0JBQXNCLEVBQUE7O0FBMUk1QjtNQThJTSxXQUFXO01BQ1gsVUFBVTtNQUNWLHVEQUF1RDtNQUN2RCx3QkFBd0I7TUFDeEIsNEJBQTRCO01BQzVCLDJCQUEyQixFQUFBOztBQW5KakM7SUF3Skksb0JBQW9CLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGFzZXQtZGV0YWlsZWRWaWV3L2RhdGFzZXQtZGV0YWlsLXZpZXcuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0ICdzcmMvdGhlbWVzL2Jhc2UvY29sb3JzJztcblxuOmhvc3Qge1xuICB3aWR0aDogMTAwJTtcbiAgaGVpZ2h0OiBjYWxjKDEwMCUgLSAzcHgpO1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAkYmFja2dyb3VuZC1jb2xvcjtcbiAgb3ZlcmZsb3c6IGF1dG87XG59XG5cbi5kZXRhaWxzLWNvbnRhaW5lciB7XG4gIGhlaWdodDogY2FsYygxMDAlIC0gNHB4KTtcbiAgcGFkZGluZzogMCA0cHggNHB4O1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAkYmFja2dyb3VuZC1jb2xvcjtcblxuICBtYXQtdGFiLWdyb3VwIHtcbiAgICBtYXgtd2lkdGg6IDEwMCU7XG4gICAgaGVpZ2h0OiAxMDAlO1xuICB9XG5cbiAgLmRldGFpbHMtaGVhZGVyIHtcbiAgICBjb2xvcjogcmdiYSgwLCAwLCAwLCAwLjU0KTtcbiAgICBiYWNrZ3JvdW5kOiByZ2JhKDAsIDAsIDAsIDAuMDMpO1xuICAgIHBhZGRpbmc6IDhweCAwO1xuICAgIGhlaWdodDogNTBweDtcbiAgICBmb250LXNpemU6IDE4cHg7XG4gICAgZm9udC13ZWlnaHQ6IDUwMDtcbiAgICBib3JkZXI6IDJweCBzb2xpZCB3aGl0ZTtcblxuICAgIC5tYXQtaWNvbiB7XG4gICAgICBmb250LXNpemU6IDI2cHg7XG4gICAgfVxuXG4gICAgLm1hdC1jaGlwIHtcbiAgICAgIGNvbG9yOiByZ2JhKDAsIDAsIDAsIDAuNTQpO1xuICAgICAgZm9udC1zaXplOiAxNnB4O1xuICAgIH1cblxuICAgIC5zdGF0dXMtaWNvbiB7XG4gICAgICB3aWR0aDogMzZweDtcblxuICAgICAgLm1hdC1pY29uIHtcbiAgICAgICAgZm9udC1zaXplOiAyNHB4O1xuICAgICAgICB2ZXJ0aWNhbC1hbGlnbjogbWlkZGxlO1xuICAgICAgfVxuICAgIH1cbiAgfVxuXG4gIC5kZXRhaWxzLWJvZHkge1xuICAgIGJhY2tncm91bmQtY29sb3I6ICNmZmY7XG4gICAgaGVpZ2h0OiBjYWxjKDEwMCUgLSA1MHB4KTtcbiAgfVxuXG4gIDo6bmctZGVlcCAubWF0LXRhYi1ib2R5IHtcbiAgICBiYWNrZ3JvdW5kLWNvbG9yOiAkYmFja2dyb3VuZC1jb2xvcjtcbiAgfVxuXG4gIDo6bmctZGVlcCAubWF0LXRhYi1sYWJlbC1hY3RpdmUge1xuICAgIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIyO1xuICB9XG5cbiAgOjpuZy1kZWVwIC5tYXQtdGFiLWJvZHktd3JhcHBlciB7XG4gICAgaGVpZ2h0OiAxMDAlO1xuXG4gICAgLm1hdC10YWItYm9keSB7XG4gICAgICBvdmVyZmxvdzogaGlkZGVuICFpbXBvcnRhbnQ7XG5cbiAgICAgIC5tYXQtdGFiLWJvZHktY29udGVudCB7XG4gICAgICAgIG92ZXJmbG93OiBoaWRkZW4gIWltcG9ydGFudDtcbiAgICAgIH1cbiAgICB9XG4gIH1cblxuICA6Om5nLWRlZXAgLm1hdC10YWItbGFiZWwsXG4gIC5tYXQtdGFiLWxpbmsge1xuICAgIGZvbnQtc2l6ZTogMTdweDtcbiAgICBmb250LXdlaWdodDogNjAwO1xuICB9XG5cbiAgLmRldGFpbHMtY2FyZCB7XG4gICAgbWFyZ2luOiAzcHg7XG5cbiAgICAuZGV0YWlscy10aXRsZSB7XG4gICAgICBmb250LXNpemU6IDE2cHg7XG4gICAgICBmb250LXdlaWdodDogNjAwO1xuICAgICAgdGV4dC1hbGlnbjogbGVmdDtcbiAgICAgIGNvbG9yOiAjNTU1O1xuICAgIH1cblxuICAgIC5kZXRhaWxzLWNvbnRlbnQge1xuICAgICAgZm9udC1zaXplOiAxNHB4O1xuICAgICAgZm9udC13ZWlnaHQ6IG5vcm1hbDtcbiAgICAgIGxpbmUtaGVpZ2h0OiAxLjQzO1xuICAgIH1cblxuICAgIC5tYXQtZGl2aWRlciB7XG4gICAgICBwb3NpdGlvbjogcmVsYXRpdmU7XG4gICAgICBwYWRkaW5nLWJvdHRvbTogNXB4O1xuICAgIH1cblxuICAgIC5wcm9wLW5hbWUge1xuICAgICAgY29sb3I6ICM3MjcyNzI7XG4gICAgICB3aWR0aDogMTM2cHg7XG4gICAgICBsaW5lLWhlaWdodDogMS40MztcbiAgICB9XG5cbiAgICAucHJvcC12YWx1ZSB7XG4gICAgICBjb2xvcjogIzU1NTtcbiAgICB9XG5cbiAgICAuc3RhdHVzX0lOSVQge1xuICAgICAgY29sb3I6ICRwcmltYXJ5LW9yYW5nZTtcbiAgICB9XG5cbiAgICAuc3RhdHVzX1NVQ0NFU1Mge1xuICAgICAgY29sb3I6ICRwcmltYXJ5LWdyZWVuO1xuICAgIH1cblxuICAgIC5zdGF0dXNfRkFJTEVEIHtcbiAgICAgIGNvbG9yOiAkcHJpbWFyeS1yZWQ7XG4gICAgfVxuICB9XG5cbiAgLmRldGFpbHMtZ3JpZCB7XG4gICAgcGFkZGluZzogM3B4O1xuICB9XG5cbiAgLmRhdGEtY29udCB7XG4gICAgaGVpZ2h0OiAxMDAlO1xuXG4gICAgLmRhdGEtZ3JpZCB7XG4gICAgICBwYWRkaW5nOiAzcHg7XG4gICAgICBoZWlnaHQ6IDEwMCU7XG4gICAgICB3aWR0aDogMTAwJTtcbiAgICAgIG92ZXJmbG93OiBhdXRvO1xuICAgICAgbWF4LWhlaWdodDogMTAwJTtcbiAgICB9XG4gIH1cblxuICAubG9hZGluZyB7XG4gICAgd2lkdGg6IDEwMCU7XG4gICAgaGVpZ2h0OiAxMDAlO1xuICAgIGNvbG9yOiByZ2IoMTg5LCAxODksIDE4OSk7XG4gICAgdGV4dC1hbGlnbjogY2VudGVyO1xuICAgIGJhY2tncm91bmQtY29sb3I6ICNmZmY7XG5cbiAgICBzcGFuIHtcbiAgICAgIGZvbnQtc2l6ZTogMjhweDtcbiAgICAgIHdvcmQtYnJlYWs6IGJyZWFrLXdvcmQ7XG4gICAgfVxuXG4gICAgLmxvYWQtZ2lmIHtcbiAgICAgIGhlaWdodDogNzAlO1xuICAgICAgd2lkdGg6IDcwJTtcbiAgICAgIGJhY2tncm91bmQtaW1hZ2U6IHVybCgnL2Fzc2V0cy9zdmcvbG9hZGluZy1yaXBwbGUuc3ZnJyk7XG4gICAgICBiYWNrZ3JvdW5kLXNpemU6IGNvbnRhaW47XG4gICAgICBiYWNrZ3JvdW5kLXJlcGVhdDogbm8tcmVwZWF0O1xuICAgICAgYmFja2dyb3VuZC1wb3NpdGlvbjogY2VudGVyO1xuICAgIH1cbiAgfVxuXG4gIDo6bmctZGVlcCAuZHgtZnJlZXNwYWNlLXJvdyB7XG4gICAgaGVpZ2h0OiAwICFpbXBvcnRhbnQ7XG4gIH1cbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/workbench/components/dataset-detailedView/dataset-detail-view.component.ts":
/*!****************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/dataset-detailedView/dataset-detail-view.component.ts ***!
  \****************************************************************************************************/
/*! exports provided: DatasetDetailViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DatasetDetailViewComponent", function() { return DatasetDetailViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../../common/services/dxDataGrid.service */ "./src/app/common/services/dxDataGrid.service.ts");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! devextreme-angular/ui/data-grid */ "./node_modules/devextreme-angular/ui/data-grid.js");
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! lodash/isUndefined */ "./node_modules/lodash/isUndefined.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(lodash_isUndefined__WEBPACK_IMPORTED_MODULE_7__);








var DatasetDetailViewComponent = /** @class */ (function () {
    function DatasetDetailViewComponent(router, dxDataGrid, workBench) {
        this.router = router;
        this.dxDataGrid = dxDataGrid;
        this.workBench = workBench;
        this.interval = 5000;
        this.previewData = [];
        this.dsMetadata = this.workBench.getDataFromLS('dsMetadata');
    }
    DatasetDetailViewComponent.prototype.ngOnInit = function () {
        if (this.dsMetadata.asOfNow.status === 'SUCCESS') {
            this.previewStatus = 'queued';
            this.triggerPreview();
        }
        else {
            this.previewStatus = 'failed';
        }
    };
    DatasetDetailViewComponent.prototype.ngOnDestroy = function () {
        if (!lodash_isUndefined__WEBPACK_IMPORTED_MODULE_7__(this.timerSubscription) &&
            !this.timerSubscription.isStopped) {
            this.stopPolling();
        }
        this.workBench.removeDataFromLS('dsMetadata');
    };
    DatasetDetailViewComponent.prototype.backToDS = function () {
        this.router.navigate(['workbench', 'dataobjects']);
    };
    DatasetDetailViewComponent.prototype.triggerPreview = function () {
        var _this = this;
        this.workBench
            .triggerDatasetPreview(this.dsMetadata.system.name)
            .subscribe(function (data) {
            _this.previewStatus = 'queued';
            if (!lodash_isUndefined__WEBPACK_IMPORTED_MODULE_7__(data.id)) {
                _this.startPolling(data.id);
            }
        });
    };
    DatasetDetailViewComponent.prototype.getPreview = function (id) {
        var _this = this;
        this.workBench.getDatasetPreviewData(id).subscribe(function (data) {
            _this.previewStatus = data.status;
            if (_this.previewStatus === 'success') {
                _this.previewData = data.rows;
                setTimeout(function () {
                    _this.dataGrid.instance.refresh();
                });
                _this.stopPolling();
            }
            else if (_this.previewStatus === 'failed') {
                _this.stopPolling();
            }
        });
    };
    /**
     * Calls list datasets api onInit and every 10 seconds or whatever set interval
     *
     * @memberof DatasetsComponent
     */
    DatasetDetailViewComponent.prototype.startPolling = function (id) {
        var _this = this;
        this.timer = Object(rxjs__WEBPACK_IMPORTED_MODULE_2__["timer"])(0, this.interval);
        this.timerSubscription = this.timer.subscribe(function () {
            _this.getPreview(id);
        });
        this.poll = true;
    };
    DatasetDetailViewComponent.prototype.stopPolling = function () {
        this.timerSubscription.unsubscribe();
        this.poll = false;
    };
    DatasetDetailViewComponent.prototype.tabChanged = function (event) {
        if (event.index === 1 && this.previewStatus === 'success') {
            this.dataGrid.instance.refresh();
        }
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ViewChild"])('dpGrid'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_6__["DxDataGridComponent"])
    ], DatasetDetailViewComponent.prototype, "dataGrid", void 0);
    DatasetDetailViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'dataset-detail-view',
            template: __webpack_require__(/*! ./dataset-detail-view.component.html */ "./src/app/modules/workbench/components/dataset-detailedView/dataset-detail-view.component.html"),
            styles: [__webpack_require__(/*! ./dataset-detail-view.component.scss */ "./src/app/modules/workbench/components/dataset-detailedView/dataset-detail-view.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_router__WEBPACK_IMPORTED_MODULE_3__["Router"],
            _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_4__["DxDataGridService"],
            _services_workbench_service__WEBPACK_IMPORTED_MODULE_5__["WorkbenchService"]])
    ], DatasetDetailViewComponent);
    return DatasetDetailViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/confirm-action-dialog/confirm-action-dialog.component.html":
/*!*******************************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/confirm-action-dialog/confirm-action-dialog.component.html ***!
  \*******************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<h1 mat-dialog-title>Are you sure to delete ?</h1>\n<mat-divider></mat-divider>\n<div mat-dialog-content>\n  <div fxLayout=\"column\">\n    <div class=\"details-content\" fxFlex=\"50\" fxLayout=\"row\">\n      <span class=\"prop-name\" e2e=\"delete-title\" i18n>{{ data?.typeTitle }} : </span>\n      <span>{{ data?.typeName }}</span>\n    </div>\n    <p *ngIf=\"!data.routesNr\" class=\"mat-caption\" e2e=\"delete-route-warn-msg\" i18n>\n      This action deletes the above permanently and cannot be un-done.\n    </p>\n    <p *ngIf=\"data.routesNr > 0\" class=\"mat-caption\" e2e=\"delete-channel-warn-msg\" i18n>\n      This channel has {{data.routesNr}} routes and cannot be deleted. Delete the routes first!\n    </p>\n  </div>\n</div>\n<div mat-dialog-actions>\n  <button mat-raised-button color=\"primary\" e2e=\"delete-no\" (click)=\"onNoClick()\">No</button>\n  <div fxFlex></div>\n  <button e2e=\"delete-yes\" [disabled]=\"data.routesNr > 0\" mat-raised-button color=\"warn\" (click)=\"onYesClick()\" cdkFocusInitial>\n    Yes\n  </button>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/confirm-action-dialog/confirm-action-dialog.component.scss":
/*!*******************************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/confirm-action-dialog/confirm-action-dialog.component.scss ***!
  \*******************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".details-content {\n  font-size: 16px;\n  font-weight: 600;\n  line-height: 3;\n  padding: 0 9px;\n  color: #1a89d4; }\n\n.prop-name {\n  color: #727272;\n  width: 136px; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGFzb3VyY2UtbWFuYWdlbWVudC9jb25maXJtLWFjdGlvbi1kaWFsb2cvY29uZmlybS1hY3Rpb24tZGlhbG9nLmNvbXBvbmVudC5zY3NzIiwiL1VzZXJzL2Jhcm5hbXVtdHlhbi9Qcm9qZWN0cy9tb2R1cy9zaXAvc2F3LXdlYi9zcmMvdGhlbWVzL2Jhc2UvX2NvbG9ycy5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUVBO0VBQ0ksZUFBZTtFQUNmLGdCQUFnQjtFQUNoQixjQUFjO0VBQ2QsY0FBYztFQUNkLGNDTnFCLEVBQUE7O0FEU3ZCO0VBQ0UsY0FBYztFQUNkLFlBQVksRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvd29ya2JlbmNoL2NvbXBvbmVudHMvZGF0YXNvdXJjZS1tYW5hZ2VtZW50L2NvbmZpcm0tYWN0aW9uLWRpYWxvZy9jb25maXJtLWFjdGlvbi1kaWFsb2cuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0ICdzcmMvdGhlbWVzL2Jhc2UvY29sb3JzJztcblxuLmRldGFpbHMtY29udGVudCB7XG4gICAgZm9udC1zaXplOiAxNnB4O1xuICAgIGZvbnQtd2VpZ2h0OiA2MDA7XG4gICAgbGluZS1oZWlnaHQ6IDM7XG4gICAgcGFkZGluZzogMCA5cHg7XG4gICAgY29sb3I6ICRwcmltYXJ5LWJsdWUtYjE7XG4gIH1cblxuICAucHJvcC1uYW1lIHtcbiAgICBjb2xvcjogIzcyNzI3MjtcbiAgICB3aWR0aDogMTM2cHg7XG4gIH0iLCIvLyBCcmFuZGluZyBjb2xvcnNcbiRwcmltYXJ5LWJsdWUtYjE6ICMxYTg5ZDQ7XG4kcHJpbWFyeS1ibHVlLWIyOiAjMDA3N2JlO1xuJHByaW1hcnktYmx1ZS1iMzogIzIwNmJjZTtcbiRwcmltYXJ5LWJsdWUtYjQ6ICMxZDNhYjI7XG5cbiRwcmltYXJ5LWhvdmVyLWJsdWU6ICMxZDYxYjE7XG4kZ3JpZC1ob3Zlci1jb2xvcjogI2Y1ZjlmYztcbiRncmlkLWhlYWRlci1iZy1jb2xvcjogI2Q3ZWFmYTtcbiRncmlkLWhlYWRlci1jb2xvcjogIzBiNGQ5OTtcbiRncmlkLXRleHQtY29sb3I6ICM0NjQ2NDY7XG4kZ3JleS10ZXh0LWNvbG9yOiAjNjM2MzYzO1xuXG4kc2VsZWN0aW9uLWhpZ2hsaWdodC1jb2w6IHJnYmEoMCwgMTQwLCAyNjAsIDAuMik7XG4kcHJpbWFyeS1ncmV5LWcxOiAjZDFkM2QzO1xuJHByaW1hcnktZ3JleS1nMjogIzk5OTtcbiRwcmltYXJ5LWdyZXktZzM6ICM3MzczNzM7XG4kcHJpbWFyeS1ncmV5LWc0OiAjNWM2NjcwO1xuJHByaW1hcnktZ3JleS1nNTogIzMxMzEzMTtcbiRwcmltYXJ5LWdyZXktZzY6ICNmNWY1ZjU7XG4kcHJpbWFyeS1ncmV5LWc3OiAjM2QzZDNkO1xuXG4kcHJpbWFyeS13aGl0ZTogI2ZmZjtcbiRwcmltYXJ5LWJsYWNrOiAjMDAwO1xuJHByaW1hcnktcmVkOiAjYWIwZTI3O1xuJHByaW1hcnktZ3JlZW46ICM3M2I0MjE7XG4kcHJpbWFyeS1vcmFuZ2U6ICNmMDc2MDE7XG5cbiRzZWNvbmRhcnktZ3JlZW46ICM2ZmIzMjA7XG4kc2Vjb25kYXJ5LXllbGxvdzogI2ZmYmUwMDtcbiRzZWNvbmRhcnktb3JhbmdlOiAjZmY5MDAwO1xuJHNlY29uZGFyeS1yZWQ6ICNkOTNlMDA7XG4kc2Vjb25kYXJ5LWJlcnJ5OiAjYWMxNDVhO1xuJHNlY29uZGFyeS1wdXJwbGU6ICM5MTQxOTE7XG5cbiRzdHJpbmctdHlwZS1jb2xvcjogIzQ5OTViMjtcbiRudW1iZXItdHlwZS1jb2xvcjogIzAwYjE4MDtcbiRnZW8tdHlwZS1jb2xvcjogIzg0NWVjMjtcbiRkYXRlLXR5cGUtY29sb3I6ICNkMTk2MjE7XG5cbiR0eXBlLWNoaXAtb3BhY2l0eTogMTtcbiRzdHJpbmctdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRzdHJpbmctdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRudW1iZXItdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRudW1iZXItdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRnZW8tdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRnZW8tdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRkYXRlLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZGF0ZS10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuXG4kcmVwb3J0LWRlc2lnbmVyLXNldHRpbmdzLWJnLWNvbG9yOiAjZjVmOWZjO1xuJGJhY2tncm91bmQtY29sb3I6ICNmNWY5ZmM7XG4iXX0= */"

/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/confirm-action-dialog/confirm-action-dialog.component.ts":
/*!*****************************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/confirm-action-dialog/confirm-action-dialog.component.ts ***!
  \*****************************************************************************************************************************/
/*! exports provided: ConfirmActionDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ConfirmActionDialogComponent", function() { return ConfirmActionDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");



var ConfirmActionDialogComponent = /** @class */ (function () {
    function ConfirmActionDialogComponent(dialogRef, data) {
        this.dialogRef = dialogRef;
        this.data = data;
    }
    ConfirmActionDialogComponent.prototype.ngOnInit = function () { };
    ConfirmActionDialogComponent.prototype.onNoClick = function () {
        this.dialogRef.close(false);
    };
    ConfirmActionDialogComponent.prototype.onYesClick = function () {
        this.dialogRef.close(true);
    };
    ConfirmActionDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'confirm-action-dialog',
            template: __webpack_require__(/*! ./confirm-action-dialog.component.html */ "./src/app/modules/workbench/components/datasource-management/confirm-action-dialog/confirm-action-dialog.component.html"),
            styles: [__webpack_require__(/*! ./confirm-action-dialog.component.scss */ "./src/app/modules/workbench/components/datasource-management/confirm-action-dialog/confirm-action-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](1, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"], Object])
    ], ConfirmActionDialogComponent);
    return ConfirmActionDialogComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/create-route-dialog/create-route-dialog.component.html":
/*!***************************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/create-route-dialog/create-route-dialog.component.html ***!
  \***************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<mat-card class=\"createroute-dialog\">\n  <mat-card-header>\n    <mat-card-title class=\"details-title mat-subheading-2\">\n      <span *ngIf=\"opType === 'create'\" e2e=\"create-route-label\">\n        Create route for\n      </span>\n      <span *ngIf=\"opType === 'update'\" e2e=\"update-route-label\">\n        Editing route for\n      </span>\n      <strong>{{ channelName }}</strong></mat-card-title\n    >\n    <div fxFlex></div>\n    <button mat-icon-button color=\"warn\" (click)=\"onCancelClick()\">\n      <mat-icon fontIcon=\"icon-close\"></mat-icon>\n    </button>\n  </mat-card-header>\n  <mat-divider></mat-divider>\n  <mat-card-content>\n    <mat-horizontal-stepper linear>\n      <ng-template matStepperIcon=\"edit\">\n        <mat-icon fontIcon=\"icon-checkmark\"></mat-icon>\n      </ng-template>\n      <ng-template matStepperIcon=\"done\">\n        <mat-icon fontIcon=\"icon-checkmark\"></mat-icon>\n      </ng-template>\n      <mat-step label=\"Route Details\" [stepControl]=\"detailsFormGroup\">\n        <form\n          fxFlex\n          fxLayout=\"row wrap\"\n          class=\"details-container\"\n          #sourceDetails\n          novalidate\n          [formGroup]=\"detailsFormGroup\"\n          autocomplete=\"false\"\n        >\n          <section\n            fxFlex=\"49\"\n            fxLayout=\"column\"\n            fxLayoutAlign=\"space-around center\"\n          >\n            <mat-form-field appearance=\"outline\">\n              <mat-label i18n>Route Name</mat-label>\n              <input\n                matInput\n                name=\"routeName\"\n                formControlName=\"routeName\"\n                e2e=\"route-name-input\"\n                required\n              />\n              <mat-error\n                e2e=\"route-name-error\"\n                *ngIf=\"detailsFormGroup.controls.routeName.hasError('required')\"\n                i18n\n              >\n                Route Name is <strong>required</strong>\n              </mat-error>\n              <mat-error\n                e2e=\"route-name-exist\"\n                *ngIf=\"detailsFormGroup.controls.routeName.hasError('isUnique')\"\n                i18n\n              >\n                Route Name already <strong>exists</strong>\n              </mat-error>\n            </mat-form-field>\n            <mat-form-field appearance=\"outline\">\n              <mat-label i18n>File Pattern</mat-label>\n              <input\n                matInput\n                name=\"filePattern\"\n                formControlName=\"filePattern\"\n                e2e=\"route-file-pattern-input\"\n                required\n              />\n              <mat-error\n                e2e=\"route-file-pattern-error\"\n                *ngIf=\"\n                  detailsFormGroup.controls.filePattern.hasError('required')\n                \"\n                i18n\n              >\n                File Pattern is <strong>required</strong>\n              </mat-error>\n              <mat-error\n                e2e=\"route-file-pattern-invalid\"\n                *ngIf=\"\n                  detailsFormGroup.controls.filePattern.hasError(\n                    'inValidPattern'\n                  )\n                \"\n                i18n\n              >\n                File Pattern is invalid.\n                <strong> Cannot have more than one pattern</strong>\n              </mat-error>\n            </mat-form-field>\n\n            <mat-form-field appearance=\"outline\">\n              <mat-label i18n>Exclude Extension</mat-label>\n              <input\n                matInput\n                type=\"text\"\n                name=\"fileExclusions\"\n                e2e=\"route-file-exclusions-input\"\n                formControlName=\"fileExclusions\"\n              />\n              <mat-error\n                e2e=\"route-file-exclusions-error\"\n                *ngIf=\"\n                  detailsFormGroup.controls.fileExclusions.hasError(\n                    'inValidPattern'\n                  )\n                \"\n                i18n\n              >\n                <strong>\n                  Cannot have more than one extension or a ' . ' in extension\n                  value</strong\n                >\n              </mat-error>\n            </mat-form-field>\n            <mat-checkbox id = \"disableDuplicate\"\n              e2e=\"route-disable-duplicate-ch\"\n              formControlName=\"disableDuplicate\"\n              i18n>\n              Disable duplicate check\n            </mat-checkbox>\n            <mat-checkbox id = \"disableConcurrency\"\n              e2e=\"route-disable-duplicate-ch\"\n              formControlName=\"disableConcurrency\"\n              i18n\n            >\n              Disable Concurrency      \n            </mat-checkbox>\n          </section>\n          <mat-divider fxFlex=\"2\" [vertical]=\"true\"></mat-divider>\n          <section\n            fxFlex=\"49\"\n            fxLayout=\"column\"\n            fxLayoutAlign=\"space-around center\"\n          >\n            <mat-form-field appearance=\"outline\">\n              <mat-label i18n>Source Location</mat-label>\n              <input\n                matInput\n                name=\"sourceLocation\"\n                e2e=\"route-source-location-input\"\n                formControlName=\"sourceLocation\"\n                required\n              />\n              <mat-error\n                e2e=\"route-source-error\"\n                *ngIf=\"\n                  detailsFormGroup.controls.sourceLocation.hasError('required')\n                \"\n                i18n\n              >\n                Source Location is <strong>required</strong>\n              </mat-error>\n            </mat-form-field>\n            <mat-form-field appearance=\"outline\">\n              <mat-label i18n>Destination Location</mat-label>\n              <input\n                matInput\n                name=\"destinationLocation\"\n                e2e=\"route-dest-location-input\"\n                formControlName=\"destinationLocation\"\n                required\n              />\n              <button\n                mat-button\n                matSuffix\n                mat-icon-button\n                (click)=\"openSelectSourceFolderDialog()\"\n              >\n                <mat-icon\n                  matTooltip=\"Browse\"\n                  fontIcon=\"icon-folder-solid\"\n                ></mat-icon>\n              </button>\n              <mat-error\n                e2e=\"route-dest-location-error\"\n                *ngIf=\"\n                  detailsFormGroup.controls.destinationLocation.hasError(\n                    'required'\n                  )\n                \"\n                i18n\n              >\n                Destination Location is <strong>required</strong>\n              </mat-error>\n            </mat-form-field>\n            <mat-form-field appearance=\"outline\">\n              <mat-label i18n>Batch size</mat-label>\n              <input\n                matInput\n                type=\"number\"\n                e2e=\"route-batch-size-input\"\n                name=\"batchSize\"\n                formControlName=\"batchSize\"\n                required\n              />\n              <mat-error\n                e2e=\"route-batch-size-error\"\n                *ngIf=\"detailsFormGroup.controls.batchSize.hasError('required')\"\n                i18n\n              >\n                Batch size is <strong>required</strong>\n              </mat-error>\n            </mat-form-field>\n            <mat-form-field appearance=\"outline\">\n              <mat-label i18n>Look back interval (Hrs)</mat-label>\n              <input\n                matInput\n                type=\"text\"\n                [value]=\"detailsFormGroup.controls.lastModifiedLimitHours.value \"\n                name=\"lastModifiedLimitHours\"\n                e2e=\"last-modified-limit-input\"\n                formControlName=\"lastModifiedLimitHours\"\n              />\n              <span *ngIf=\"detailsFormGroup.controls.errors\" class=\"errorTextMsg\" i18n>Only numbers allowed</span>\n             </mat-form-field>\n            <mat-form-field appearance=\"outline\">\n              <mat-label i18n>Description about the route</mat-label>\n              <textarea\n                matInput\n                formControlName=\"description\"\n                e2e=\"route-description-input\"\n              ></textarea>\n            </mat-form-field>\n          </section>\n        </form>\n        <mat-divider></mat-divider>\n        <div\n          fxFlex\n          fxLayout=\"row\"\n          fxLayoutAlign=\"space-between center\"\n          style=\"height: 20%;\"\n        >\n          <button\n            mat-stroked-button\n            color=\"primary\"\n            color=\"warn\"\n            (click)=\"onCancelClick()\"\n            e2e=\"route-cancel-btn\"\n            i18n\n          >\n            Cancel\n          </button>\n          <button\n            mat-stroked-button\n            color=\"primary\"\n            (click)=\"testRoute(detailsFormGroup.value)\"\n            [disabled]=\"!detailsFormGroup.valid\"\n            e2e=\"route-test-connectivity-btn\"\n            i18n\n          >\n            <mat-icon\n              fontIcon=\"icon-query-mode\"\n              style=\"vertical-align: bottom;\"\n            ></mat-icon>\n            Test Connectivity\n          </button>\n          <button\n            mat-raised-button\n            color=\"primary\"\n            e2e=\"route-next-btn\"\n            matStepperNext\n            [disabled]=\"!detailsFormGroup.valid\"\n            i18n\n          >\n            Next\n          </button>\n        </div>\n      </mat-step>\n      <mat-step label=\"Schedule Route\">\n        <div style=\"height: 80%;\">\n          <cron-job-schedular\n            [crondetails]=\"crondetails\"\n            (onCronChanged)=\"onCronChanged($event)\"\n          ></cron-job-schedular>\n        </div>\n        <span *ngIf=\"!startDateCorrectFlag\" class=\"errorTextMsg\" i18n\n          >Start date cannot be in past. Please select a Date that is current or\n          in future.</span\n        >\n        <mat-divider></mat-divider>\n        <div\n          fxFlex\n          fxLayout=\"row\"\n          fxLayoutAlign=\"space-between center\"\n          style=\"height: 20%;\"\n        >\n          <button\n            mat-raised-button\n            color=\"primary\"\n            e2e=\"route-schd-previous-btn\"\n            matStepperPrevious\n            i18n\n          >\n            Previous\n          </button>\n          <button\n            mat-stroked-button\n            color=\"primary\"\n            (click)=\"testRoute(detailsFormGroup.value)\"\n            [disabled]=\"!detailsFormGroup.valid\"\n            e2e=\"route-schd-test-conn-btn\"\n            i18n\n          >\n            <mat-icon\n              fontIcon=\"icon-query-mode\"\n              style=\"vertical-align: bottom;\"\n            ></mat-icon>\n            Test Connectivity\n          </button>\n          <button\n            mat-raised-button\n            color=\"primary\"\n            *ngIf=\"opType === 'create'\"\n            [disabled]=\"!detailsFormGroup.valid || !isCronExpressionValid\"\n            (click)=\"createRoute(detailsFormGroup.value)\"\n            e2e=\"route-schd-create-btn\"\n            i18n\n          >\n            Create\n          </button>\n          <button\n            mat-raised-button\n            color=\"primary\"\n            e2e=\"route-schd-update-btn\"\n            *ngIf=\"opType === 'update'\"\n            [disabled]=\"!detailsFormGroup.valid\"\n            (click)=\"createRoute(detailsFormGroup.value)\"\n            i18n\n          >\n            Update\n          </button>\n        </div>\n      </mat-step>\n    </mat-horizontal-stepper>\n  </mat-card-content>\n</mat-card>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/create-route-dialog/create-route-dialog.component.scss":
/*!***************************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/create-route-dialog/create-route-dialog.component.scss ***!
  \***************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "::ng-deep .createroute-dialog {\n  height: calc(100% - 48px); }\n  ::ng-deep .createroute-dialog .mat-card-content {\n    height: calc(100% - 36px); }\n  ::ng-deep .createroute-dialog .mat-stepper-horizontal {\n    height: 100%; }\n  ::ng-deep .createroute-dialog .mat-horizontal-content-container {\n    height: calc(100% - 55px);\n    padding: 0; }\n  ::ng-deep .createroute-dialog .mat-horizontal-content-container .mat-horizontal-stepper-content {\n      height: 98%; }\n  ::ng-deep .createroute-dialog .mat-horizontal-content-container .mat-horizontal-stepper-content span.errorTextMsg {\n        color: #d93e00;\n        margin-left: 3.5%; }\n  ::ng-deep .createroute-dialog .mat-horizontal-stepper-content[aria-expanded='false'] {\n    height: 0 !important; }\n  ::ng-deep .createroute-dialog .details-title {\n    text-align: left;\n    padding-left: 10px;\n    margin: 0;\n    font-weight: 600 !important; }\n  ::ng-deep .createroute-dialog .details-title > strong {\n      color: #1a89d4; }\n  ::ng-deep .createroute-dialog .details-container {\n    height: 85%;\n    overflow: auto; }\n  ::ng-deep .createroute-dialog .details-container .mat-form-field {\n      width: 80%; }\n  ::ng-deep .createroute-dialog .source-list > mat-card {\n    margin: 9px 0;\n    border-radius: 4px;\n    padding: 9px 24px; }\n  ::ng-deep .createroute-dialog mat-card > img {\n    height: 60px;\n    max-width: 70px; }\n  ::ng-deep .createroute-dialog .selected {\n    border: 3px #1a89d4 solid; }\n  ::ng-deep .createroute-dialog #disableDuplicate {\n    margin: 2px 0 63px 17px; }\n  ::ng-deep .createroute-dialog #disableConcurrency {\n    margin: 2px 0 63px 0; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGFzb3VyY2UtbWFuYWdlbWVudC9jcmVhdGUtcm91dGUtZGlhbG9nL2NyZWF0ZS1yb3V0ZS1kaWFsb2cuY29tcG9uZW50LnNjc3MiLCIvVXNlcnMvYmFybmFtdW10eWFuL1Byb2plY3RzL21vZHVzL3NpcC9zYXctd2ViL3NyYy90aGVtZXMvYmFzZS9fY29sb3JzLnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSx5QkFBeUIsRUFBQTtFQUQzQjtJQUlJLHlCQUF5QixFQUFBO0VBSjdCO0lBUUksWUFBWSxFQUFBO0VBUmhCO0lBWUkseUJBQXlCO0lBQ3pCLFVBQVUsRUFBQTtFQWJkO01BZ0JNLFdBQVcsRUFBQTtFQWhCakI7UUFtQlEsY0NVZTtRRFRmLGlCQUFpQixFQUFBO0VBcEJ6QjtJQTBCSSxvQkFBb0IsRUFBQTtFQTFCeEI7SUE4QkksZ0JBQWdCO0lBQ2hCLGtCQUFrQjtJQUNsQixTQUFTO0lBQ1QsMkJBQTJCLEVBQUE7RUFqQy9CO01Bb0NNLGNDckNtQixFQUFBO0VEQ3pCO0lBeUNJLFdBQVc7SUFDWCxjQUFjLEVBQUE7RUExQ2xCO01BNkNNLFVBQVUsRUFBQTtFQTdDaEI7SUFtRE0sYUFBYTtJQUNiLGtCQUFrQjtJQUNsQixpQkFBaUIsRUFBQTtFQXJEdkI7SUEyRE0sWUFBWTtJQUNaLGVBQWUsRUFBQTtFQTVEckI7SUFpRUkseUJBQWtDLEVBQUE7RUFqRXRDO0lBcUVJLHVCQUF1QixFQUFBO0VBckUzQjtJQXlFSSxvQkFBb0IsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvd29ya2JlbmNoL2NvbXBvbmVudHMvZGF0YXNvdXJjZS1tYW5hZ2VtZW50L2NyZWF0ZS1yb3V0ZS1kaWFsb2cvY3JlYXRlLXJvdXRlLWRpYWxvZy5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgJ3NyYy90aGVtZXMvYmFzZS9jb2xvcnMnO1xuXG46Om5nLWRlZXAgLmNyZWF0ZXJvdXRlLWRpYWxvZyB7XG4gIGhlaWdodDogY2FsYygxMDAlIC0gNDhweCk7XG5cbiAgLm1hdC1jYXJkLWNvbnRlbnQge1xuICAgIGhlaWdodDogY2FsYygxMDAlIC0gMzZweCk7XG4gIH1cblxuICAubWF0LXN0ZXBwZXItaG9yaXpvbnRhbCB7XG4gICAgaGVpZ2h0OiAxMDAlO1xuICB9XG5cbiAgLm1hdC1ob3Jpem9udGFsLWNvbnRlbnQtY29udGFpbmVyIHtcbiAgICBoZWlnaHQ6IGNhbGMoMTAwJSAtIDU1cHgpO1xuICAgIHBhZGRpbmc6IDA7XG5cbiAgICAubWF0LWhvcml6b250YWwtc3RlcHBlci1jb250ZW50IHtcbiAgICAgIGhlaWdodDogOTglO1xuXG4gICAgICBzcGFuLmVycm9yVGV4dE1zZyB7XG4gICAgICAgIGNvbG9yOiAkc2Vjb25kYXJ5LXJlZDtcbiAgICAgICAgbWFyZ2luLWxlZnQ6IDMuNSU7XG4gICAgICB9XG4gICAgfVxuICB9XG5cbiAgLm1hdC1ob3Jpem9udGFsLXN0ZXBwZXItY29udGVudFthcmlhLWV4cGFuZGVkPSdmYWxzZSddIHtcbiAgICBoZWlnaHQ6IDAgIWltcG9ydGFudDtcbiAgfVxuXG4gIC5kZXRhaWxzLXRpdGxlIHtcbiAgICB0ZXh0LWFsaWduOiBsZWZ0O1xuICAgIHBhZGRpbmctbGVmdDogMTBweDtcbiAgICBtYXJnaW46IDA7XG4gICAgZm9udC13ZWlnaHQ6IDYwMCAhaW1wb3J0YW50O1xuXG4gICAgPiBzdHJvbmcge1xuICAgICAgY29sb3I6ICRwcmltYXJ5LWJsdWUtYjE7XG4gICAgfVxuICB9XG5cbiAgLmRldGFpbHMtY29udGFpbmVyIHtcbiAgICBoZWlnaHQ6IDg1JTtcbiAgICBvdmVyZmxvdzogYXV0bztcblxuICAgIC5tYXQtZm9ybS1maWVsZCB7XG4gICAgICB3aWR0aDogODAlO1xuICAgIH1cbiAgfVxuXG4gIC5zb3VyY2UtbGlzdCB7XG4gICAgJiA+IG1hdC1jYXJkIHtcbiAgICAgIG1hcmdpbjogOXB4IDA7XG4gICAgICBib3JkZXItcmFkaXVzOiA0cHg7XG4gICAgICBwYWRkaW5nOiA5cHggMjRweDtcbiAgICB9XG4gIH1cblxuICBtYXQtY2FyZCB7XG4gICAgJiA+IGltZyB7XG4gICAgICBoZWlnaHQ6IDYwcHg7XG4gICAgICBtYXgtd2lkdGg6IDcwcHg7XG4gICAgfVxuICB9XG5cbiAgLnNlbGVjdGVkIHtcbiAgICBib3JkZXI6IDNweCAkcHJpbWFyeS1ibHVlLWIxIHNvbGlkO1xuICB9XG4gIFxuICAjZGlzYWJsZUR1cGxpY2F0ZXtcbiAgXHRcdG1hcmdpbjogMnB4IDAgNjNweCAxN3B4O1xuICB9XG4gIFxuICAjZGlzYWJsZUNvbmN1cnJlbmN5e1xuICBcdFx0bWFyZ2luOiAycHggMCA2M3B4IDA7XG4gIH1cbn1cblxuIiwiLy8gQnJhbmRpbmcgY29sb3JzXG4kcHJpbWFyeS1ibHVlLWIxOiAjMWE4OWQ0O1xuJHByaW1hcnktYmx1ZS1iMjogIzAwNzdiZTtcbiRwcmltYXJ5LWJsdWUtYjM6ICMyMDZiY2U7XG4kcHJpbWFyeS1ibHVlLWI0OiAjMWQzYWIyO1xuXG4kcHJpbWFyeS1ob3Zlci1ibHVlOiAjMWQ2MWIxO1xuJGdyaWQtaG92ZXItY29sb3I6ICNmNWY5ZmM7XG4kZ3JpZC1oZWFkZXItYmctY29sb3I6ICNkN2VhZmE7XG4kZ3JpZC1oZWFkZXItY29sb3I6ICMwYjRkOTk7XG4kZ3JpZC10ZXh0LWNvbG9yOiAjNDY0NjQ2O1xuJGdyZXktdGV4dC1jb2xvcjogIzYzNjM2MztcblxuJHNlbGVjdGlvbi1oaWdobGlnaHQtY29sOiByZ2JhKDAsIDE0MCwgMjYwLCAwLjIpO1xuJHByaW1hcnktZ3JleS1nMTogI2QxZDNkMztcbiRwcmltYXJ5LWdyZXktZzI6ICM5OTk7XG4kcHJpbWFyeS1ncmV5LWczOiAjNzM3MzczO1xuJHByaW1hcnktZ3JleS1nNDogIzVjNjY3MDtcbiRwcmltYXJ5LWdyZXktZzU6ICMzMTMxMzE7XG4kcHJpbWFyeS1ncmV5LWc2OiAjZjVmNWY1O1xuJHByaW1hcnktZ3JleS1nNzogIzNkM2QzZDtcblxuJHByaW1hcnktd2hpdGU6ICNmZmY7XG4kcHJpbWFyeS1ibGFjazogIzAwMDtcbiRwcmltYXJ5LXJlZDogI2FiMGUyNztcbiRwcmltYXJ5LWdyZWVuOiAjNzNiNDIxO1xuJHByaW1hcnktb3JhbmdlOiAjZjA3NjAxO1xuXG4kc2Vjb25kYXJ5LWdyZWVuOiAjNmZiMzIwO1xuJHNlY29uZGFyeS15ZWxsb3c6ICNmZmJlMDA7XG4kc2Vjb25kYXJ5LW9yYW5nZTogI2ZmOTAwMDtcbiRzZWNvbmRhcnktcmVkOiAjZDkzZTAwO1xuJHNlY29uZGFyeS1iZXJyeTogI2FjMTQ1YTtcbiRzZWNvbmRhcnktcHVycGxlOiAjOTE0MTkxO1xuXG4kc3RyaW5nLXR5cGUtY29sb3I6ICM0OTk1YjI7XG4kbnVtYmVyLXR5cGUtY29sb3I6ICMwMGIxODA7XG4kZ2VvLXR5cGUtY29sb3I6ICM4NDVlYzI7XG4kZGF0ZS10eXBlLWNvbG9yOiAjZDE5NjIxO1xuXG4kdHlwZS1jaGlwLW9wYWNpdHk6IDE7XG4kc3RyaW5nLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkc3RyaW5nLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kbnVtYmVyLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkbnVtYmVyLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZ2VvLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZ2VvLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZGF0ZS10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGRhdGUtdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcblxuJHJlcG9ydC1kZXNpZ25lci1zZXR0aW5ncy1iZy1jb2xvcjogI2Y1ZjlmYztcbiRiYWNrZ3JvdW5kLWNvbG9yOiAjZjVmOWZjO1xuIl19 */"

/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/create-route-dialog/create-route-dialog.component.ts":
/*!*************************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/create-route-dialog/create-route-dialog.component.ts ***!
  \*************************************************************************************************************************/
/*! exports provided: CreateRouteDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CreateRouteDialogComponent", function() { return CreateRouteDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/isUndefined */ "./node_modules/lodash/isUndefined.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_isUndefined__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_includes__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/includes */ "./node_modules/lodash/includes.js");
/* harmony import */ var lodash_includes__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_includes__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/isEmpty */ "./node_modules/lodash/isEmpty.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_isEmpty__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var _services_datasource_service__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../services/datasource.service */ "./src/app/modules/workbench/services/datasource.service.ts");
/* harmony import */ var _common_validators__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../../../../../common/validators */ "./src/app/common/validators/index.ts");
/* harmony import */ var _select_folder_dialog__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../select-folder-dialog */ "./src/app/modules/workbench/components/datasource-management/select-folder-dialog/index.ts");
/* harmony import */ var _test_connectivity_test_connectivity_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../test-connectivity/test-connectivity.component */ "./src/app/modules/workbench/components/datasource-management/test-connectivity/test-connectivity.component.ts");
/* harmony import */ var moment__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! moment */ "./node_modules/moment/moment.js");
/* harmony import */ var moment__WEBPACK_IMPORTED_MODULE_11___default = /*#__PURE__*/__webpack_require__.n(moment__WEBPACK_IMPORTED_MODULE_11__);













var CreateRouteDialogComponent = /** @class */ (function () {
    function CreateRouteDialogComponent(_formBuilder, dialogRef, snackBar, datasourceService, _dialog, routeData) {
        this._formBuilder = _formBuilder;
        this.dialogRef = dialogRef;
        this.snackBar = snackBar;
        this.datasourceService = datasourceService;
        this._dialog = _dialog;
        this.routeData = routeData;
        this.crondetails = {};
        this.opType = 'create';
        this.channelName = '';
        this.isCronExpressionValid = false;
        this.startDateCorrectFlag = true;
        this.channelName = this.routeData.channelName;
        if (lodash_isUndefined__WEBPACK_IMPORTED_MODULE_4__(this.routeData.routeMetadata.length)) {
            this.opType = 'update';
        }
        this.createForm();
        if (lodash_isUndefined__WEBPACK_IMPORTED_MODULE_4__(this.routeData.routeMetadata.length)) {
            this.detailsFormGroup.patchValue(this.routeData.routeMetadata);
            this.crondetails = this.routeData.routeMetadata.schedulerExpression;
        }
    }
    CreateRouteDialogComponent.prototype.createForm = function () {
        var channelId = this.routeData.channelID;
        var tranformerFn = function (value) { return ({ channelId: channelId, routeName: value }); };
        var oldRouteName = this.opType === 'update' ? this.routeData.routeMetadata.routeName : '';
        this.detailsFormGroup = this._formBuilder.group({
            routeName: [
                '',
                _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].required,
                Object(_common_validators__WEBPACK_IMPORTED_MODULE_8__["isUnique"])(this.datasourceService.isDuplicateRoute, tranformerFn, oldRouteName)
            ],
            sourceLocation: ['', _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].required],
            destinationLocation: ['', _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].required],
            filePattern: ['', [_angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].required, this.validateFilePattern]],
            description: [''],
            disableDuplicate: [false],
            disableConcurrency: [false],
            batchSize: ['', [_angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].required]],
            fileExclusions: ['', this.validatefileExclusion],
            lastModifiedLimitHours: ['', _angular_forms__WEBPACK_IMPORTED_MODULE_2__["Validators"].pattern(/^\d*[1-9]\d*$/)]
        });
    };
    CreateRouteDialogComponent.prototype.onCancelClick = function () {
        this.dialogRef.close();
    };
    CreateRouteDialogComponent.prototype.validateFilePattern = function (control) {
        if (lodash_includes__WEBPACK_IMPORTED_MODULE_5__(control.value, ',')) {
            return { inValidPattern: true };
        }
        return null;
    };
    CreateRouteDialogComponent.prototype.validatefileExclusion = function (control) {
        if (lodash_includes__WEBPACK_IMPORTED_MODULE_5__(control.value, ',') || lodash_includes__WEBPACK_IMPORTED_MODULE_5__(control.value, '.')) {
            return { inValidPattern: true };
        }
        return null;
    };
    CreateRouteDialogComponent.prototype.testRoute = function (formData) {
        var _this = this;
        var routeInfo = {
            channelType: 'sftp',
            channelId: this.routeData.channelID,
            sourceLocation: formData.sourceLocation,
            destinationLocation: formData.destinationLocation
        };
        this.datasourceService.testRouteWithBody(routeInfo).subscribe(function (data) {
            _this.showConnectivityLog(data);
        });
    };
    CreateRouteDialogComponent.prototype.showConnectivityLog = function (logData) {
        var _this = this;
        this.dialogRef.updatePosition({ top: '30px' });
        var snackBarRef = this.snackBar.openFromComponent(_test_connectivity_test_connectivity_component__WEBPACK_IMPORTED_MODULE_10__["TestConnectivityComponent"], {
            data: logData,
            horizontalPosition: 'center',
            panelClass: ['mat-elevation-z9', 'testConnectivityClass']
        });
        snackBarRef.afterDismissed().subscribe(function () {
            _this.dialogRef.updatePosition({ top: '' });
        });
    };
    CreateRouteDialogComponent.prototype.onCronChanged = function (cronexpression) {
        this.crondetails = cronexpression;
        this.isCronExpressionValid = !(lodash_isEmpty__WEBPACK_IMPORTED_MODULE_6__(cronexpression.cronexp) &&
            cronexpression.activeTab !== 'immediate');
    };
    CreateRouteDialogComponent.prototype.createRoute = function (data) {
        this.startDateCorrectFlag = this.crondetails.activeTab === 'immediate' || moment__WEBPACK_IMPORTED_MODULE_11__(this.crondetails.startDate) > moment__WEBPACK_IMPORTED_MODULE_11__();
        if (!this.startDateCorrectFlag) {
            return false;
        }
        var routeDetails = this.mapData(data);
        this.dialogRef.close({ routeDetails: routeDetails, opType: this.opType });
    };
    CreateRouteDialogComponent.prototype.mapData = function (data) {
        var routeDetails = {
            routeName: data.routeName,
            sourceLocation: data.sourceLocation,
            destinationLocation: data.destinationLocation,
            filePattern: data.filePattern,
            schedulerExpression: this.crondetails,
            description: data.description,
            disableDuplicate: data.disableDuplicate,
            disableConcurrency: data.disableConcurrency,
            batchSize: data.batchSize,
            fileExclusions: data.fileExclusions,
            lastModifiedLimitHours: data.lastModifiedLimitHours
        };
        return routeDetails;
    };
    CreateRouteDialogComponent.prototype.openSelectSourceFolderDialog = function () {
        var _this = this;
        var dateDialogRef = this._dialog.open(_select_folder_dialog__WEBPACK_IMPORTED_MODULE_9__["SourceFolderDialogComponent"], {
            hasBackdrop: true,
            autoFocus: false,
            closeOnNavigation: true,
            height: '400px',
            width: '300px'
        });
        dateDialogRef.afterClosed().subscribe(function (sourcePath) {
            _this.detailsFormGroup.controls.destinationLocation.setValue(sourcePath);
        });
    };
    CreateRouteDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'create-route-dialog',
            template: __webpack_require__(/*! ./create-route-dialog.component.html */ "./src/app/modules/workbench/components/datasource-management/create-route-dialog/create-route-dialog.component.html"),
            styles: [__webpack_require__(/*! ./create-route-dialog.component.scss */ "./src/app/modules/workbench/components/datasource-management/create-route-dialog/create-route-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](5, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_3__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_forms__WEBPACK_IMPORTED_MODULE_2__["FormBuilder"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatDialogRef"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatSnackBar"],
            _services_datasource_service__WEBPACK_IMPORTED_MODULE_7__["DatasourceService"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatDialog"], Object])
    ], CreateRouteDialogComponent);
    return CreateRouteDialogComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/createSource-dialog/createSource-dialog.component.html":
/*!***************************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/createSource-dialog/createSource-dialog.component.html ***!
  \***************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<mat-card class=\"createsource-dialog\">\n  <mat-card-header>\n    <mat-card-title class=\"details-title mat-subheading-2\">{{\n      dialogTitle\n    }}</mat-card-title>\n    <div fxFlex></div>\n    <button mat-icon-button color=\"warn\" (click)=\"onCancelClick()\">\n      <mat-icon fontIcon=\"icon-close\"></mat-icon>\n    </button>\n  </mat-card-header>\n  <mat-divider></mat-divider>\n  <mat-card-content e2e=\"data-source-panel\">\n    <mat-horizontal-stepper linear [selectedIndex]=\"selectedStepIndex\">\n      <ng-template matStepperIcon=\"edit\">\n        <mat-icon fontIcon=\"icon-checkmark\"></mat-icon>\n      </ng-template>\n      <ng-template matStepperIcon=\"done\">\n        <mat-icon fontIcon=\"icon-checkmark\"></mat-icon>\n      </ng-template>\n      <mat-step\n        label=\"Select Channel Type\"\n        [stepControl]=\"firstStep\"\n        [editable]=\"isTypeEditable\"\n      >\n        <div\n          fxFlex\n          fxLayout=\"row\"\n          fxLayoutAlign=\"center center\"\n          class=\"source-container\"\n        >\n          <div\n            fxFlex\n            fxLayout=\"row wrap\"\n            fxLayoutAlign=\"space-around space-around\"\n            class=\"source-list\"\n          >\n            <mat-card\n              class=\"mat-elevation-z3\"\n              [class.selected]=\"selectedSource === source.uid\"\n              *ngFor=\"let source of sources\"\n              fxFlex=\"31\"\n              fxLayout=\"row\"\n              [attr.e2e]=\"source.name\"\n              fxLayoutAlign=\"start center\"\n              (click)=\"sourceSelected(source)\"\n              [class.no-click]=\"source.supported === false\"\n            >\n              <div>{{ source.name }}</div>\n              <div fxFlex></div>\n              <img [src]=\"source.imgsrc\" [attr.alt]=\"source.name\" />\n            </mat-card>\n          </div>\n        </div>\n        <mat-divider></mat-divider>\n        <div\n          fxFlex\n          fxLayout=\"row\"\n          fxLayoutAlign=\"end center\"\n          style=\"height: 15%;\"\n        >\n          <button\n            mat-raised-button\n            color=\"primary\"\n            e2e=\"create-channel-next-button\"\n            matStepperNext\n            [disabled]=\"selectedSource === ''\"\n            i18n\n          >\n            Next\n          </button>\n        </div>\n      </mat-step>\n      <mat-step label=\"Channel Details\">\n        <form\n          fxFlex\n          fxLayout=\"row wrap\"\n          class=\"source-container\"\n          #sourceDetails\n          novalidate\n          [formGroup]=\"detailsFormGroup\"\n          autocomplete=\"false\"\n        >\n          <section\n            fxFlex=\"49\"\n            fxLayout=\"column\"\n            fxLayoutAlign=\"space-around center\"\n          >\n            <mat-form-field appearance=\"outline\">\n              <mat-label i18n>Name Your Channel</mat-label>\n              <input\n                matInput\n                name=\"name-of-channel\"\n                e2e=\"name-of-channel\"\n                formControlName=\"channelName\"\n                maxlength=\"18\"\n                required\n                autocomplete=\"off\"\n              />\n              <mat-error\n                *ngIf=\"\n                  detailsFormGroup.controls.channelName.hasError('required')\n                \"\n                i18n\n              >\n                Channel Name is\n                <strong>required</strong>\n              </mat-error>\n              <mat-error\n                e2e=\"channel-exist-error-msg\"\n                *ngIf=\"\n                  detailsFormGroup.controls.channelName.hasError('isUnique')\n                \"\n                i18n\n              >\n                Channel Name already\n                <strong>exists</strong>\n              </mat-error>\n            </mat-form-field>\n            <mat-form-field appearance=\"outline\">\n              <mat-label i18n>Hostname</mat-label>\n              <input\n                matInput\n                placeholder=\"Host Name\"\n                name=\"hostName\"\n                e2e=\"host-name-input\"\n                formControlName=\"hostName\"\n                required\n                name=\"name-of-host\"\n                autocomplete=\"new-password\"\n              />\n              <mat-error\n                *ngIf=\"detailsFormGroup.controls.hostName.hasError('required')\"\n                i18n\n              >\n                Host Name is\n                <strong>required</strong>\n              </mat-error>\n            </mat-form-field>\n            <mat-form-field appearance=\"outline\">\n              <mat-label i18n>Port Number</mat-label>\n              <input\n                matInput\n                placeholder=\"Port Number\"\n                name=\"portNo\"\n                e2e=\"port-no-input\"\n                formControlName=\"portNo\"\n                required\n              />\n              <mat-error\n                *ngIf=\"detailsFormGroup.controls.portNo.hasError('required')\"\n                i18n\n              >\n                Port Number is\n                <strong>required</strong>\n              </mat-error>\n              <mat-error\n                e2e=\"port-number-error-msg\"\n                *ngIf=\"detailsFormGroup.controls.portNo.hasError('pattern')\"\n                i18n\n              >\n                Port Number can consist of only\n                <strong>Numbers</strong>\n              </mat-error>\n            </mat-form-field>\n          </section>\n          <mat-divider fxFlex=\"2\" [vertical]=\"true\"></mat-divider>\n          <section\n            fxFlex=\"49\"\n            fxLayout=\"column\"\n            fxLayoutAlign=\"space-around center\"\n          >\n            <mat-form-field appearance=\"outline\">\n              <mat-label i18n>Access Type</mat-label>\n              <mat-select formControlName=\"accessType\" e2e=\"access-type-select\">\n                <mat-option value=\"R\" e2e=\"access-r\" i18n>Read</mat-option>\n                <mat-option value=\"RW\" e2e=\"access-rw\" i18n\n                  >Read & Write</mat-option\n                >\n              </mat-select>\n            </mat-form-field>\n            <mat-form-field appearance=\"outline\">\n              <mat-label i18n>Username</mat-label>\n              <input\n                type=\"text\"\n                matInput\n                placeholder=\"Username\"\n                name=\"name-of-user\"\n                e2e=\"name-of-user-input\"\n                autocomplete=\"new-password\"\n                formControlName=\"userName\"\n                required\n              />\n              <button mat-button matSuffix mat-icon-button>\n                <mat-icon\n                  matTooltip=\"Username\"\n                  fontIcon=\"icon-chat-bot\"\n                ></mat-icon>\n              </button>\n              <mat-error\n                e2e=\"user-error-msg\"\n                *ngIf=\"detailsFormGroup.controls.userName.hasError('required')\"\n                i18n\n              >\n                Username is\n                <strong>required</strong>\n              </mat-error>\n            </mat-form-field>\n            <mat-form-field appearance=\"outline\">\n              <mat-label i18n>Password</mat-label>\n              <input\n                [type]=\"show ? 'text' : 'password'\"\n                matInput\n                placeholder=\"Password\"\n                name=\"user-password\"\n                e2e=\"user-password\"\n                formControlName=\"password\"\n                required\n                autocomplete=\"new-password\"\n              />\n              <button\n                matTooltip=\"Show Password\"\n                e2e=\"show-password-sc\"\n                *ngIf=\"!show\"\n                mat-button\n                matSuffix\n                mat-icon-button\n                aria-label=\"show\"\n                (click)=\"togglePWD()\"\n              >\n                <mat-icon fontIcon=\"icon-show\"></mat-icon>\n              </button>\n              <button\n                matTooltip=\"Hide Password\"\n                e2e=\"hide-password-cs\"\n                *ngIf=\"show\"\n                mat-button\n                matSuffix\n                mat-icon-button\n                aria-label=\"show\"\n                (click)=\"togglePWD()\"\n              >\n                <mat-icon fontIcon=\"icon-hide\"></mat-icon>\n              </button>\n              <mat-error\n                e2e=\"pwd-validation-msg\"\n                *ngIf=\"detailsFormGroup.controls.password.hasError('required')\"\n                i18n\n              >\n                Password is\n                <strong>required</strong>\n              </mat-error>\n            </mat-form-field>\n          </section>\n          <section fxFlex fxLayout=\"column\" fxLayoutAlign=\"center center\">\n            <mat-form-field style=\"width: 90%;\" appearance=\"outline\">\n              <mat-label i18n>Description about the source</mat-label>\n              <textarea\n                matInput\n                formControlName=\"description\"\n                e2e=\"channel-description-input\"\n              ></textarea>\n            </mat-form-field>\n          </section>\n        </form>\n        <mat-divider></mat-divider>\n        <div\n          fxFlex\n          fxLayout=\"row\"\n          fxLayoutAlign=\"space-between center\"\n          style=\"height: 20%;\"\n        >\n          <button\n            mat-raised-button\n            color=\"primary\"\n            *ngIf=\"isTypeEditable\"\n            matStepperPrevious\n            e2e=\"channel-prev-btn\"\n          >\n            Previous\n          </button>\n          <button\n            mat-stroked-button\n            color=\"primary\"\n            *ngIf=\"!isTypeEditable\"\n            (click)=\"onCancelClick()\"\n            color=\"warn\"\n            e2e=\"channel-cancel-btn\"\n            matStepperPrevious\n          >\n            Cancel\n          </button>\n          <button\n            e2e=\"channel-test-connect-btn-model\"\n            mat-stroked-button\n            color=\"primary\"\n            (click)=\"testChannel(detailsFormGroup.value)\"\n            i18n\n            [disabled]=\"!detailsFormGroup.valid\"\n          >\n            <mat-icon\n              fontIcon=\"icon-query-mode\"\n              style=\"vertical-align: bottom;\"\n            ></mat-icon>\n            Test Connectivity\n          </button>\n          <button\n            e2e=\"channel-create-btn\"\n            mat-raised-button\n            *ngIf=\"opType === 'create'\"\n            color=\"primary\"\n            type=\"submit\"\n            [disabled]=\"!detailsFormGroup.valid\"\n            (click)=\"createSource(detailsFormGroup.value)\"\n            i18n\n          >\n            Create\n          </button>\n          <button\n            e2e=\"channel-update-btn\"\n            mat-raised-button\n            *ngIf=\"opType === 'update'\"\n            color=\"primary\"\n            type=\"submit\"\n            [disabled]=\"!detailsFormGroup.valid\"\n            (click)=\"createSource(detailsFormGroup.value)\"\n            i18n\n          >\n            Update\n          </button>\n        </div>\n      </mat-step>\n    </mat-horizontal-stepper>\n  </mat-card-content>\n</mat-card>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/createSource-dialog/createSource-dialog.component.scss":
/*!***************************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/createSource-dialog/createSource-dialog.component.scss ***!
  \***************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "::ng-deep .createsource-dialog {\n  height: calc(100% - 48px); }\n  ::ng-deep .createsource-dialog .mat-card-content {\n    height: calc(100% - 36px); }\n  ::ng-deep .createsource-dialog .mat-stepper-horizontal {\n    height: 100%; }\n  ::ng-deep .createsource-dialog .mat-horizontal-content-container {\n    height: calc(100% - 55px);\n    padding: 0; }\n  ::ng-deep .createsource-dialog .mat-horizontal-content-container .mat-horizontal-stepper-content {\n      height: 98%; }\n  ::ng-deep .createsource-dialog .mat-horizontal-stepper-content[aria-expanded='false'] {\n    height: 0 !important; }\n  ::ng-deep .createsource-dialog .mat-form-field {\n    min-width: 75% !important; }\n  ::ng-deep .createsource-dialog .source-container {\n    height: 85%;\n    overflow: auto; }\n  ::ng-deep .createsource-dialog .no-click {\n    cursor: not-allowed !important; }\n  ::ng-deep .createsource-dialog .details-title {\n    text-align: left;\n    color: #1a89d4;\n    padding-left: 10px;\n    margin: 0; }\n  ::ng-deep .createsource-dialog .source-list > mat-card {\n    margin: 9px 0;\n    border-radius: 4px;\n    padding: 9px 24px;\n    cursor: pointer; }\n  ::ng-deep .createsource-dialog mat-card > img {\n    height: 60px;\n    max-width: 70px; }\n  ::ng-deep .createsource-dialog .selected {\n    border: 3px #1a89d4 solid; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGFzb3VyY2UtbWFuYWdlbWVudC9jcmVhdGVTb3VyY2UtZGlhbG9nL2NyZWF0ZVNvdXJjZS1kaWFsb2cuY29tcG9uZW50LnNjc3MiLCIvVXNlcnMvYmFybmFtdW10eWFuL1Byb2plY3RzL21vZHVzL3NpcC9zYXctd2ViL3NyYy90aGVtZXMvYmFzZS9fY29sb3JzLnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSx5QkFBeUIsRUFBQTtFQUQzQjtJQUlJLHlCQUF5QixFQUFBO0VBSjdCO0lBUUksWUFBWSxFQUFBO0VBUmhCO0lBWUkseUJBQXlCO0lBQ3pCLFVBQVUsRUFBQTtFQWJkO01BZ0JNLFdBQVcsRUFBQTtFQWhCakI7SUFxQkksb0JBQW9CLEVBQUE7RUFyQnhCO0lBeUJJLHlCQUF5QixFQUFBO0VBekI3QjtJQTZCSSxXQUFXO0lBQ1gsY0FBYyxFQUFBO0VBOUJsQjtJQWtDSSw4QkFBOEIsRUFBQTtFQWxDbEM7SUFzQ0ksZ0JBQWdCO0lBQ2hCLGNDeENxQjtJRHlDckIsa0JBQWtCO0lBQ2xCLFNBQVMsRUFBQTtFQXpDYjtJQThDTSxhQUFhO0lBQ2Isa0JBQWtCO0lBQ2xCLGlCQUFpQjtJQUNqQixlQUFlLEVBQUE7RUFqRHJCO0lBdURNLFlBQVk7SUFDWixlQUFlLEVBQUE7RUF4RHJCO0lBNkRJLHlCQUFrQyxFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy93b3JrYmVuY2gvY29tcG9uZW50cy9kYXRhc291cmNlLW1hbmFnZW1lbnQvY3JlYXRlU291cmNlLWRpYWxvZy9jcmVhdGVTb3VyY2UtZGlhbG9nLmNvbXBvbmVudC5zY3NzIiwic291cmNlc0NvbnRlbnQiOlsiQGltcG9ydCAnc3JjL3RoZW1lcy9iYXNlL2NvbG9ycyc7XG5cbjo6bmctZGVlcCAuY3JlYXRlc291cmNlLWRpYWxvZyB7XG4gIGhlaWdodDogY2FsYygxMDAlIC0gNDhweCk7XG5cbiAgLm1hdC1jYXJkLWNvbnRlbnQge1xuICAgIGhlaWdodDogY2FsYygxMDAlIC0gMzZweCk7XG4gIH1cblxuICAubWF0LXN0ZXBwZXItaG9yaXpvbnRhbCB7XG4gICAgaGVpZ2h0OiAxMDAlO1xuICB9XG5cbiAgLm1hdC1ob3Jpem9udGFsLWNvbnRlbnQtY29udGFpbmVyIHtcbiAgICBoZWlnaHQ6IGNhbGMoMTAwJSAtIDU1cHgpO1xuICAgIHBhZGRpbmc6IDA7XG5cbiAgICAubWF0LWhvcml6b250YWwtc3RlcHBlci1jb250ZW50IHtcbiAgICAgIGhlaWdodDogOTglO1xuICAgIH1cbiAgfVxuXG4gIC5tYXQtaG9yaXpvbnRhbC1zdGVwcGVyLWNvbnRlbnRbYXJpYS1leHBhbmRlZD0nZmFsc2UnXSB7XG4gICAgaGVpZ2h0OiAwICFpbXBvcnRhbnQ7XG4gIH1cblxuICAubWF0LWZvcm0tZmllbGQge1xuICAgIG1pbi13aWR0aDogNzUlICFpbXBvcnRhbnQ7XG4gIH1cblxuICAuc291cmNlLWNvbnRhaW5lciB7XG4gICAgaGVpZ2h0OiA4NSU7XG4gICAgb3ZlcmZsb3c6IGF1dG87XG4gIH1cblxuICAubm8tY2xpY2sge1xuICAgIGN1cnNvcjogbm90LWFsbG93ZWQgIWltcG9ydGFudDtcbiAgfVxuXG4gIC5kZXRhaWxzLXRpdGxlIHtcbiAgICB0ZXh0LWFsaWduOiBsZWZ0O1xuICAgIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIxO1xuICAgIHBhZGRpbmctbGVmdDogMTBweDtcbiAgICBtYXJnaW46IDA7XG4gIH1cblxuICAuc291cmNlLWxpc3Qge1xuICAgICYgPiBtYXQtY2FyZCB7XG4gICAgICBtYXJnaW46IDlweCAwO1xuICAgICAgYm9yZGVyLXJhZGl1czogNHB4O1xuICAgICAgcGFkZGluZzogOXB4IDI0cHg7XG4gICAgICBjdXJzb3I6IHBvaW50ZXI7XG4gICAgfVxuICB9XG5cbiAgbWF0LWNhcmQge1xuICAgICYgPiBpbWcge1xuICAgICAgaGVpZ2h0OiA2MHB4O1xuICAgICAgbWF4LXdpZHRoOiA3MHB4O1xuICAgIH1cbiAgfVxuXG4gIC5zZWxlY3RlZCB7XG4gICAgYm9yZGVyOiAzcHggJHByaW1hcnktYmx1ZS1iMSBzb2xpZDtcbiAgfVxufVxuIiwiLy8gQnJhbmRpbmcgY29sb3JzXG4kcHJpbWFyeS1ibHVlLWIxOiAjMWE4OWQ0O1xuJHByaW1hcnktYmx1ZS1iMjogIzAwNzdiZTtcbiRwcmltYXJ5LWJsdWUtYjM6ICMyMDZiY2U7XG4kcHJpbWFyeS1ibHVlLWI0OiAjMWQzYWIyO1xuXG4kcHJpbWFyeS1ob3Zlci1ibHVlOiAjMWQ2MWIxO1xuJGdyaWQtaG92ZXItY29sb3I6ICNmNWY5ZmM7XG4kZ3JpZC1oZWFkZXItYmctY29sb3I6ICNkN2VhZmE7XG4kZ3JpZC1oZWFkZXItY29sb3I6ICMwYjRkOTk7XG4kZ3JpZC10ZXh0LWNvbG9yOiAjNDY0NjQ2O1xuJGdyZXktdGV4dC1jb2xvcjogIzYzNjM2MztcblxuJHNlbGVjdGlvbi1oaWdobGlnaHQtY29sOiByZ2JhKDAsIDE0MCwgMjYwLCAwLjIpO1xuJHByaW1hcnktZ3JleS1nMTogI2QxZDNkMztcbiRwcmltYXJ5LWdyZXktZzI6ICM5OTk7XG4kcHJpbWFyeS1ncmV5LWczOiAjNzM3MzczO1xuJHByaW1hcnktZ3JleS1nNDogIzVjNjY3MDtcbiRwcmltYXJ5LWdyZXktZzU6ICMzMTMxMzE7XG4kcHJpbWFyeS1ncmV5LWc2OiAjZjVmNWY1O1xuJHByaW1hcnktZ3JleS1nNzogIzNkM2QzZDtcblxuJHByaW1hcnktd2hpdGU6ICNmZmY7XG4kcHJpbWFyeS1ibGFjazogIzAwMDtcbiRwcmltYXJ5LXJlZDogI2FiMGUyNztcbiRwcmltYXJ5LWdyZWVuOiAjNzNiNDIxO1xuJHByaW1hcnktb3JhbmdlOiAjZjA3NjAxO1xuXG4kc2Vjb25kYXJ5LWdyZWVuOiAjNmZiMzIwO1xuJHNlY29uZGFyeS15ZWxsb3c6ICNmZmJlMDA7XG4kc2Vjb25kYXJ5LW9yYW5nZTogI2ZmOTAwMDtcbiRzZWNvbmRhcnktcmVkOiAjZDkzZTAwO1xuJHNlY29uZGFyeS1iZXJyeTogI2FjMTQ1YTtcbiRzZWNvbmRhcnktcHVycGxlOiAjOTE0MTkxO1xuXG4kc3RyaW5nLXR5cGUtY29sb3I6ICM0OTk1YjI7XG4kbnVtYmVyLXR5cGUtY29sb3I6ICMwMGIxODA7XG4kZ2VvLXR5cGUtY29sb3I6ICM4NDVlYzI7XG4kZGF0ZS10eXBlLWNvbG9yOiAjZDE5NjIxO1xuXG4kdHlwZS1jaGlwLW9wYWNpdHk6IDE7XG4kc3RyaW5nLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkc3RyaW5nLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kbnVtYmVyLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkbnVtYmVyLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZ2VvLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZ2VvLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZGF0ZS10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGRhdGUtdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcblxuJHJlcG9ydC1kZXNpZ25lci1zZXR0aW5ncy1iZy1jb2xvcjogI2Y1ZjlmYztcbiRiYWNrZ3JvdW5kLWNvbG9yOiAjZjVmOWZjO1xuIl19 */"

/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/createSource-dialog/createSource-dialog.component.ts":
/*!*************************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/createSource-dialog/createSource-dialog.component.ts ***!
  \*************************************************************************************************************************/
/*! exports provided: CreateSourceDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CreateSourceDialogComponent", function() { return CreateSourceDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/isUndefined */ "./node_modules/lodash/isUndefined.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_isUndefined__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var _services_datasource_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../services/datasource.service */ "./src/app/modules/workbench/services/datasource.service.ts");
/* harmony import */ var _common_validators__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../../../../common/validators */ "./src/app/common/validators/index.ts");
/* harmony import */ var _wb_comp_configs__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../wb-comp-configs */ "./src/app/modules/workbench/wb-comp-configs.ts");
/* harmony import */ var _test_connectivity_test_connectivity_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../test-connectivity/test-connectivity.component */ "./src/app/modules/workbench/components/datasource-management/test-connectivity/test-connectivity.component.ts");










var CreateSourceDialogComponent = /** @class */ (function () {
    function CreateSourceDialogComponent(_formBuilder, dialogRef, snackBar, datasourceService, channelData) {
        this._formBuilder = _formBuilder;
        this.dialogRef = dialogRef;
        this.snackBar = snackBar;
        this.datasourceService = datasourceService;
        this.channelData = channelData;
        this.selectedSource = '';
        this.sources = _wb_comp_configs__WEBPACK_IMPORTED_MODULE_7__["CHANNEL_TYPES"];
        this.opType = 'create';
        this.show = false;
        this.dialogTitle = 'Create Data Channel';
        this.selectedStepIndex = 0;
        this.isTypeEditable = true;
        if (lodash_isUndefined__WEBPACK_IMPORTED_MODULE_4__(this.channelData.length)) {
            this.opType = 'update';
            this.isTypeEditable = false;
            this.dialogTitle = 'Edit Channel';
            this.selectedStepIndex = 1;
            this.selectedSource = this.channelData.channelType;
        }
        this.createForm();
        if (lodash_isUndefined__WEBPACK_IMPORTED_MODULE_4__(this.channelData.length)) {
            this.firstStep.patchValue(this.channelData);
            this.detailsFormGroup.patchValue(this.channelData);
        }
    }
    CreateSourceDialogComponent.prototype.createForm = function () {
        this.firstStep = this._formBuilder.group({
            channelType: ['', _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required]
        });
        var oldChannelName = this.opType === 'update' ? this.channelData.channelName : '';
        this.detailsFormGroup = this._formBuilder.group({
            channelName: ['', _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required, Object(_common_validators__WEBPACK_IMPORTED_MODULE_6__["isUnique"])(this.datasourceService.isDuplicateChannel, function (v) { return v; }, oldChannelName)],
            hostName: ['', _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required],
            portNo: [
                '',
                _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].compose([
                    _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required,
                    _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].pattern('^[0-9]*$')
                ])
            ],
            userName: ['', _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required],
            password: ['', _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required],
            description: [''],
            accessType: ['R', _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required]
        });
    };
    CreateSourceDialogComponent.prototype.sourceSelected = function (source) {
        if (source.supported) {
            this.selectedSource = source.uid;
            this.firstStep.controls.channelType.reset(source.uid);
        }
    };
    CreateSourceDialogComponent.prototype.createSource = function (formData) {
        var sourceDetails = this.mapData(formData);
        this.dialogRef.close({ sourceDetails: sourceDetails, opType: this.opType });
    };
    CreateSourceDialogComponent.prototype.mapData = function (data) {
        var sourceDetails = {
            channelName: data.channelName,
            channelType: this.selectedSource,
            hostName: data.hostName,
            portNo: data.portNo,
            accessType: data.accessType,
            userName: data.userName,
            password: data.password,
            description: data.description
        };
        return sourceDetails;
    };
    CreateSourceDialogComponent.prototype.onCancelClick = function () {
        this.dialogRef.close();
    };
    CreateSourceDialogComponent.prototype.togglePWD = function () {
        this.show = !this.show;
    };
    CreateSourceDialogComponent.prototype.testChannel = function (formData) {
        var _this = this;
        var channelData = {
            channelType: this.selectedSource,
            hostName: formData.hostName,
            password: formData.password,
            portNo: formData.portNo,
            userName: formData.userName
        };
        this.datasourceService.testChannelWithBody(channelData).subscribe(function (data) {
            _this.showConnectivityLog(data);
        });
    };
    CreateSourceDialogComponent.prototype.showConnectivityLog = function (logData) {
        var _this = this;
        this.dialogRef.updatePosition({ top: '30px' });
        var snackBarRef = this.snackBar.openFromComponent(_test_connectivity_test_connectivity_component__WEBPACK_IMPORTED_MODULE_8__["TestConnectivityComponent"], {
            data: logData,
            horizontalPosition: 'center',
            panelClass: ['mat-elevation-z9', 'testConnectivityClass']
        });
        snackBarRef.afterDismissed().subscribe(function () {
            _this.dialogRef.updatePosition({ top: '' });
        });
    };
    CreateSourceDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'createsource-dialog',
            template: __webpack_require__(/*! ./createSource-dialog.component.html */ "./src/app/modules/workbench/components/datasource-management/createSource-dialog/createSource-dialog.component.html"),
            styles: [__webpack_require__(/*! ./createSource-dialog.component.scss */ "./src/app/modules/workbench/components/datasource-management/createSource-dialog/createSource-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](4, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormBuilder"],
            _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"],
            _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatSnackBar"],
            _services_datasource_service__WEBPACK_IMPORTED_MODULE_5__["DatasourceService"], Object])
    ], CreateSourceDialogComponent);
    return CreateSourceDialogComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/datasource-page.component.html":
/*!***************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/datasource-page.component.html ***!
  \***************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"datasource-view\" fxLayout=\"column\" fxFlex fxFill>\n  <mat-toolbar class=\"toolbar-white\">\n    <mat-toolbar-row>\n      <div\n        fxFlex=\"47\"\n        fxLayout=\"row\"\n        fxLayoutAlign=\"start center\"\n        fxLayoutGap=\"5px\"\n      >\n        <span i18n>Data Sources</span>\n      </div>\n      <span fxFlex></span>\n      <div class=\"action-buttons\" fxLayout=\"row\" fxLayoutGap=\"10px\">\n        <button\n          mat-raised-button\n          color=\"primary\"\n          e2e=\"add-new-channel-btn\"\n          (click)=\"createSource(undefined)\"\n        >\n          <span i18n>+ Channel</span>\n        </button>\n      </div>\n    </mat-toolbar-row>\n  </mat-toolbar>\n  <div class=\"body-container\" fxLayout=\"column\">\n    <div fxFlex=\"1 1 100px\" fxLayout=\"row\" fxLayoutAlign=\"space-around start\">\n      <div\n        type=\"button\"\n        [attr.aria-label]=\"source.name\"\n        class=\"mat-elevation-z3 source-row\"\n        fxLayoutAlign=\"center center\"\n        [class.selected]=\"selectedSourceType === source.uid\"\n        *ngFor=\"let source of sourceTypes\"\n        (click)=\"sourceSelectedType(source.uid, source.count)\"\n        [matBadge]=\"source.count\"\n        matBadgePosition=\"after\"\n        [matBadgeColor]=\"source.color\"\n        [matTooltip]=\"source.name\"\n        [class.no-click]=\"source.count == 0\"\n      >\n        <img [src]=\"source.imgsrc\" [attr.alt]=\"source.name\" />\n      </div>\n    </div>\n\n    <div fxFlex fxLayout=\"row\" fxLayoutGap=\"5px\">\n      <dx-data-grid\n        #channelsGrid\n        fxFlex=\"25\"\n        e2e=\"channels-container\"\n        class=\"mat-elevation-z3 sources-grid\"\n        [dataSource]=\"sourceData\"\n        [showBorders]=\"false\"\n        [columnAutoWidth]=\"false\"\n        [showColumnLines]=\"false\"\n        [showRowLines]=\"true\"\n        (onToolbarPreparing)=\"onToolbarPreparing($event)\"\n        keyExpr=\"bisChannelSysId\"\n        [rowAlternationEnabled]=\"false\"\n        (onSelectionChanged)=\"onSourceSelectionChanged($event)\"\n      >\n        <dxo-selection mode=\"single\"></dxo-selection>\n        <dxo-filter-row [visible]=\"false\" applyFilter=\"auto\"></dxo-filter-row>\n        <dxo-search-panel\n          [visible]=\"true\"\n          [width]=\"220\"\n          placeholder=\"Search...\"\n        ></dxo-search-panel>\n        <dxo-scrolling mode=\"virtual\" [useNative]=\"false\"></dxo-scrolling>\n        <dxi-column\n          caption=\"Channel Name\"\n          dataField=\"channelName\"\n          alignment=\"left\"\n          cellTemplate=\"nameTemplate\"\n        ></dxi-column>\n        <dxi-column\n          caption=\"Host Name\"\n          dataField=\"hostName\"\n          alignment=\"left\"\n        ></dxi-column>\n        <dxi-column\n          caption=\"Actions\"\n          dataField=\"bisRouteSysId\"\n          alignment=\"center\"\n          cellTemplate=\"actionsTemplate\"\n          [allowFiltering]=\"false\"\n          [allowSorting]=\"false\"\n        >\n        </dxi-column>\n        <div\n          *dxTemplate=\"let data of 'actionsTemplate'\"\n          fxLayout=\"row\"\n          fxLayoutAlign=\"center center\"\n        >\n          <button\n            mat-icon-button\n            i18n-matTooltip\n            [attr.e2e]=\"data.data.status\"\n            class=\"grid-icon-btn\"\n            [color]=\"data.data.status ? 'error' : 'primary'\"\n            [matTooltip]=\"data.data.status ? 'Deactivate' : 'Activate'\"\n            (click)=\"toggleChannelActivation(data.data)\"\n          >\n            <mat-icon fontIcon=\"icon-logout\"></mat-icon>\n          </button>\n        </div>\n        <div *dxTemplate=\"let data of 'sourceTypeTemplate'\">\n          <div\n            fxHide.lt-lg=\"true\"\n            class=\"master-detail-caption\"\n            e2e=\"current-source-name\"\n          >\n            {{ selectedSourceType | uppercase }}\n          </div>\n        </div>\n        <div *dxTemplate=\"let data of 'nameTemplate'\">\n          <div class=\"s-name\" [attr.e2e]=\"data.value\" [matTooltip]=\"data.value\">\n            {{ data.value }}\n          </div>\n        </div>\n      </dx-data-grid>\n\n      <mat-card fxFlex class=\"routes-card\" fxLayout=\"column\">\n        <div\n          fxFlex=\"1 1 40px\"\n          fxLayout=\"row\"\n          fxLayoutGap=\"9px\"\n          fxLayoutAlign=\"space-around center\"\n        >\n          <div class=\"mat-body-1\">\n            <span\n              class=\"details-title mat-subheading-2\"\n              e2e=\"channel-name-detail-panel\"\n              >{{ selectedSourceData?.channelName | uppercase }}</span\n            >\n          </div>\n          <span fxFlex></span>\n          <button\n            mat-mini-fab\n            e2e=\"channel-test-connect-btn-detail\"\n            class=\"mat-elevation-z2\"\n            color=\"primary\"\n            [disabled]=\"!channelEditable\"\n            (click)=\"testChannel(selectedSourceData?.bisChannelSysId)\"\n            i18n-matTooltip\n            matTooltip=\"Test Channel Connectivity\"\n          >\n            <mat-icon fontIcon=\"icon-query-mode\"></mat-icon>\n          </button>\n          <button\n            mat-mini-fab\n            e2e=\"edit-channel\"\n            class=\"mat-elevation-z2\"\n            color=\"primary\"\n            [disabled]=\"!channelEditable\"\n            (click)=\"createSource(selectedSourceData)\"\n            matTooltip=\"Edit Channel\"\n            i18n-matTooltip\n          >\n            <mat-icon fontIcon=\"icon-edit-solid\"></mat-icon>\n          </button>\n          <button\n            mat-mini-fab\n            e2e=\"delete-channel\"\n            class=\"mat-elevation-z3\"\n            color=\"warn\"\n            [disabled]=\"!channelEditable\"\n            (click)=\"deleteChannel(selectedSourceData?.bisChannelSysId)\"\n            matTooltip=\"Delete Channel\"\n            i18n-matTooltip\n          >\n            <mat-icon fontIcon=\"icon-delete-solid\"></mat-icon>\n          </button>\n          <button\n            mat-raised-button\n            e2e=\"add-route\"\n            color=\"primary\"\n            [disabled]=\"!channelEditable\"\n            (click)=\"createRoute(selectedSourceData?.bisChannelSysId)\"\n          >\n            <span i18n>+ Route</span>\n          </button>\n        </div>\n        <div style=\"padding-top: 5px;\">\n          <mat-divider></mat-divider>\n        </div>\n        <div class=\"routes-card-body\">\n          <div\n            fxLayout=\"row\"\n            fxLayoutalign=\"start center\"\n            fxLayout.lt-md=\"column\"\n          >\n            <div class=\"details-content\" fxFlex=\"50\" fxLayout=\"row\">\n              <span class=\"prop-name\" e2e=\"host-name\" i18n>Host Name : </span>\n              <span>{{ selectedSourceData?.hostName }}</span>\n            </div>\n            <mat-divider [vertical]=\"true\"></mat-divider>\n            <div class=\"details-content\" fxFlex=\"50\" fxLayout=\"row\">\n              <span class=\"prop-name\" e2e=\"port-number\" i18n>Port No : </span>\n              <span>{{ selectedSourceData?.portNo }}</span>\n            </div>\n          </div>\n          <mat-divider></mat-divider>\n          <div\n            fxLayout=\"row\"\n            fxLayoutalign=\"start center\"\n            fxLayout.lt-md=\"column\"\n          >\n            <div class=\"details-content\" fxFlex=\"50\" fxLayout=\"row\">\n              <span class=\"prop-name\" e2e=\"access-type\" i18n\n                >Access Type :\n              </span>\n              <span>{{ selectedSourceData?.accessType }}</span>\n            </div>\n            <mat-divider [vertical]=\"true\"></mat-divider>\n            <div class=\"details-content\" fxFlex=\"50\" fxLayout=\"row\">\n              <span class=\"prop-name\" e2e=\"description\" i18n\n                >Description :\n              </span>\n              <span>{{ selectedSourceData?.description }}</span>\n            </div>\n          </div>\n          <mat-divider></mat-divider>\n          <div\n            fxLayout=\"row\"\n            fxLayoutalign=\"start center\"\n            fxLayout.lt-md=\"column\"\n          >\n            <div class=\"details-content\" fxFlex=\"50\" fxLayout=\"row\">\n              <span class=\"prop-name\" e2e=\"created-by\" i18n>Created By : </span>\n              <span>{{\n                selectedSourceData?.modifiedBy || selectedSourceData?.createdBy\n              }}</span>\n            </div>\n            <mat-divider [vertical]=\"true\"></mat-divider>\n            <div class=\"details-content\" fxFlex=\"50\" fxLayout=\"row\">\n              <span class=\"prop-name\" e2e=\"last-modified-at\" i18n\n                >Last Modified:\n              </span>\n              <span *ngIf=\"selectedSourceData\">\n                <span>{{\n                  selectedSourceData?.modifiedDate ||\n                    selectedSourceData?.createdDate | date: 'short'\n                }}</span>\n              </span>\n            </div>\n          </div>\n          <mat-divider></mat-divider>\n          <div\n            fxLayout=\"row\"\n            fxLayoutalign=\"start center\"\n            fxLayout.lt-md=\"column\"\n          >\n            <div class=\"details-content\" fxFlex=\"50\" fxLayout=\"row\">\n              <span class=\"prop-name\" e2e=\"user-name\" i18n>User Name : </span>\n              <span>{{ selectedSourceData?.userName }}</span>\n            </div>\n            <mat-divider [vertical]=\"true\"></mat-divider>\n            <div class=\"details-content\" fxFlex=\"50\" fxLayout=\"row\">\n              <span class=\"prop-name\" e2e=\"password-name\" i18n\n                >Password :\n              </span>\n              <div *ngIf=\"selectedSourceData\">\n                <span *ngIf=\"show; else hidePwd\">{{\n                  selectedSourceData?.password\n                }}</span>\n                <ng-template #hidePwd\n                  >&bull; &bull; &bull; &bull; &bull; &bull; &bull; &bull;\n                  &bull;</ng-template\n                >\n                <button\n                  *ngIf=\"!show\"\n                  e2e=\"show-password-ds\"\n                  matTooltip=\"Show Password\"\n                  color=\"primary\"\n                  mat-button\n                  matSuffix\n                  mat-icon-button\n                  aria-label=\"show\"\n                  (click)=\"togglePWD()\"\n                >\n                  <mat-icon fontIcon=\"icon-show\"></mat-icon>\n                </button>\n                <button\n                  *ngIf=\"show\"\n                  e2e=\"hide-password-ds\"\n                  matTooltip=\"Hide Password\"\n                  color=\"primary\"\n                  mat-button\n                  matSuffix\n                  mat-icon-button\n                  aria-label=\"show\"\n                  (click)=\"togglePWD()\"\n                >\n                  <mat-icon fontIcon=\"icon-hide\"></mat-icon>\n                </button>\n              </div>\n            </div>\n          </div>\n          <mat-divider></mat-divider>\n          <dx-data-grid\n            class=\"routes-grid\"\n            [dataSource]=\"routesData\"\n            [showBorders]=\"false\"\n            [columnAutoWidth]=\"false\"\n            [showColumnLines]=\"false\"\n            (onToolbarPreparing)=\"onRoutesToolbarPreparing($event)\"\n            [rowAlternationEnabled]=\"true\"\n            keyExpr=\"bisRouteSysId\"\n            [width]=\"'100%'\"\n            [allowColumnResizing]=\"true\"\n            e2e=\"routes-container\"\n          >\n            <dxo-filter-row\n              [visible]=\"true\"\n              applyFilter=\"auto\"\n            ></dxo-filter-row>\n            <dxo-search-panel\n              [visible]=\"true\"\n              [width]=\"240\"\n              placeholder=\"Search...\"\n            ></dxo-search-panel>\n            <dxo-scrolling\n              mode=\"virtual\"\n              showScrollbar=\"always\"\n              [useNative]=\"false\"\n            ></dxo-scrolling>\n            <dxi-column\n              caption=\"Route Name\"\n              dataField=\"routeName\"\n              alignment=\"left\"\n            ></dxi-column>\n            <dxi-column\n              caption=\"Created By\"\n              dataField=\"createdBy\"\n              alignment=\"left\"\n            ></dxi-column>\n            <dxi-column\n              caption=\"Last Modified\"\n              dataField=\"createdDate\"\n              alignment=\"left\"\n              cellTemplate=\"dateTemplate\"\n            ></dxi-column>\n            <dxi-column\n              caption=\"File Pattern\"\n              dataField=\"filePattern\"\n              alignment=\"left\"\n            ></dxi-column>\n            <dxi-column\n              caption=\"Schedule\"\n              dataField=\"schedulerExpression\"\n              alignment=\"left\"\n              [calculateCellValue]=\"calculateScheduleCellValue\"\n            ></dxi-column>\n            <dxi-column\n              caption=\"Source Location\"\n              dataField=\"sourceLocation\"\n              alignment=\"left\"\n            ></dxi-column>\n            <dxi-column\n              caption=\"Destination Location\"\n              dataField=\"destinationLocation\"\n              alignment=\"left\"\n            ></dxi-column>\n            <dxi-column\n              caption=\"Description\"\n              dataField=\"description\"\n              alignment=\"left\"\n            ></dxi-column>\n            <dxi-column\n              caption=\"Actions\"\n              dataField=\"bisRouteSysId\"\n              alignment=\"center\"\n              cellTemplate=\"actionsTemplate\"\n              [allowFiltering]=\"false\"\n              [allowSorting]=\"false\"\n            ></dxi-column>\n\n            <div *dxTemplate=\"let data of 'nameTemplate'\">\n              <div\n                class=\"details-title mat-subheading-2\"\n                e2e=\"available-routes\"\n                i18n\n              >\n                Available Routes\n              </div>\n            </div>\n\n            <div *dxTemplate=\"let data of 'dateTemplate'\">\n              <span *ngIf=\"data.data.modifiedDate; else defDate\">{{\n                data.data.modifiedDate | date: 'short'\n              }}</span>\n              <ng-template #defDate>{{\n                data.data.createdDate | date: 'short'\n              }}</ng-template>\n            </div>\n\n            <div *dxTemplate=\"let data of 'actionsTemplate'\">\n              <button\n                mat-icon-button\n                class=\"triggerbtn\"\n                [attr.e2e]=\"data.data.routeName\"\n                [matMenuTriggerFor]=\"datapodmenu\"\n              >\n                <mat-icon fontIcon=\"icon-action-solid\"></mat-icon>\n              </button>\n              <mat-menu #datapodmenu=\"matMenu\">\n                <button\n                  mat-menu-item\n                  e2e=\"route-active-inactive-btn\"\n                  [disabled]=\"!selectedSourceData?.status\"\n                  [attr.color]=\"data.data.status ? 'warn' : 'primary'\"\n                  (click)=\"toggleRouteActivation(data.data)\"\n                >\n                  <mat-icon\n                    class=\"menu-btn-icon\"\n                    [color]=\"data.data.status ? 'warn' : 'primary'\"\n                    [attr.color]=\"data.data.status ? 'warn' : 'primary'\"\n                    fontIcon=\"icon-logout\"\n                  >\n                  </mat-icon>\n                  <span>{{\n                    data.data.status ? 'Deactivate' : 'Activate'\n                  }}</span>\n                </button>\n                <button\n                  mat-menu-item\n                  (click)=\"openLogsDialog(data.data)\"\n                  e2e=\"view-route-logs-btn\"\n                >\n                  <mat-icon\n                    class=\"menu-btn-icon\"\n                    fontIcon=\"icon-clipboard\"\n                  ></mat-icon>\n                  <span i18n>View Route Logs</span>\n                </button>\n                <button\n                  mat-menu-item\n                  (click)=\"testRoute(data.data)\"\n                  e2e=\"test-connectivity-btn\"\n                >\n                  <mat-icon\n                    class=\"menu-btn-icon\"\n                    fontIcon=\"icon-query-mode\"\n                  ></mat-icon>\n                  <span i18n>Test Route Connectivity</span>\n                </button>\n                <button\n                  mat-menu-item\n                  (click)=\"createRoute(data.data)\"\n                  e2e=\"edit-route-btn\"\n                >\n                  <mat-icon\n                    class=\"menu-btn-icon\"\n                    fontIcon=\"icon-edit\"\n                  ></mat-icon>\n                  <span i18n>Edit Route</span>\n                </button>\n                <button\n                  color=\"warn\"\n                  mat-menu-item\n                  (click)=\"deleteRoute(data.data)\"\n                  e2e=\"delete-route-btn\"\n                >\n                  <mat-icon\n                    color=\"warn\"\n                    class=\"menu-btn-icon\"\n                    fontIcon=\"icon-delete\"\n                  ></mat-icon>\n                  <span i18n>Delete Route</span>\n                </button>\n              </mat-menu>\n            </div>\n          </dx-data-grid>\n        </div>\n      </mat-card>\n    </div>\n  </div>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/datasource-page.component.scss":
/*!***************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/datasource-page.component.scss ***!
  \***************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%;\n  height: 100%; }\n\n.datasource-view {\n  width: 100%;\n  background-color: #f5f9fc;\n  height: 100%; }\n\n.datasource-view .action-buttons .mat-raised-button {\n    line-height: 32px !important;\n    vertical-align: middle; }\n\n.datasource-view .grid-icon-btn {\n    line-height: normal !important;\n    height: 25px !important; }\n\n.datasource-view .icon-search {\n    font-size: 26px !important; }\n\n.datasource-view .source-row {\n    border-radius: 50%;\n    background-color: white;\n    width: 70px;\n    height: 70px;\n    margin: 20px 10px;\n    cursor: pointer; }\n\n.datasource-view .source-row > img {\n      height: 45px;\n      width: 45px;\n      border-radius: 20%; }\n\n.datasource-view .selected {\n    border: 2px #1a89d4 solid; }\n\n.datasource-view .no-click {\n    cursor: not-allowed; }\n\n.datasource-view .toolbar-white {\n    background-color: #fff;\n    height: 45px !important;\n    min-height: 45px !important; }\n\n.datasource-view .body-container {\n    height: calc(100% - 52px);\n    margin: 3px;\n    padding: 5px 3px 0; }\n\n.datasource-view .master-detail-caption {\n    padding: 12px;\n    font-size: 14px;\n    font-weight: 600;\n    color: #1a89d4; }\n\n.datasource-view .sources-grid {\n    padding: 3px 3px 3px 0;\n    border-radius: 5px; }\n\n.datasource-view .sources-grid .s-name {\n      font-size: 14px;\n      font-weight: 600;\n      color: #1a89d4; }\n\n.datasource-view .routes-card {\n    padding: 3px;\n    border-radius: 5px; }\n\n.datasource-view .routes-card .routes-card-body {\n      overflow: auto;\n      height: calc(100% - 43px); }\n\n.datasource-view .routes-card .routes-card-body .mat-divider {\n        border-color: rgba(0, 0, 0, 0.05) !important; }\n\n.datasource-view .routes-card .routes-card-body .route-grid--action-buttons button {\n        width: 24px; }\n\n.datasource-view .routes-card .mat-mini-fab {\n      height: 36px !important;\n      width: 36px !important; }\n\n.datasource-view .routes-card .details-title {\n      text-align: left;\n      color: #1a89d4;\n      padding-left: 10px;\n      margin: 0;\n      font-weight: 600 !important; }\n\n.datasource-view .routes-card .details-content {\n      font-size: 14px;\n      font-weight: normal;\n      line-height: 3;\n      padding: 0 9px; }\n\n.datasource-view .routes-card .prop-name {\n      color: #727272;\n      width: 136px; }\n\n.datasource-view .routes-card .routes-grid {\n      padding-top: 3px; }\n\n.datasource-view .routes-card .menu-btn-icon {\n      margin-right: 0px;\n      vertical-align: baseline; }\n\n.datasource-view .routes-card .menu-btn-icon.mat-error {\n        color: red; }\n\n.datasource-view .routes-card .menu-btn-icon.mat-primary {\n        color: #1a89d4; }\n\n.datasource-view .dx-virtual-row {\n    height: 0 !important; }\n\n::ng-deep .sourceDialogClass {\n  padding: 0; }\n\n::ng-deep .sourceDialogClass ::ng-deep .mat-dialog-container {\n    padding: 0 !important; }\n\n::ng-deep .testConnectivityClass {\n  height: 222px !important;\n  width: 768px !important;\n  min-width: 768px !important;\n  padding: 0 !important;\n  background-color: black;\n  border-radius: 7px 7px 0 0 !important; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGFzb3VyY2UtbWFuYWdlbWVudC9kYXRhc291cmNlLXBhZ2UuY29tcG9uZW50LnNjc3MiLCIvVXNlcnMvYmFybmFtdW10eWFuL1Byb2plY3RzL21vZHVzL3NpcC9zYXctd2ViL3NyYy90aGVtZXMvYmFzZS9fY29sb3JzLnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSxXQUFXO0VBQ1gsWUFBWSxFQUFBOztBQUdkO0VBQ0UsV0FBVztFQUNYLHlCQ3NDd0I7RURyQ3hCLFlBQVksRUFBQTs7QUFIZDtJQU9NLDRCQUE0QjtJQUM1QixzQkFBc0IsRUFBQTs7QUFSNUI7SUFhSSw4QkFBOEI7SUFDOUIsdUJBQXVCLEVBQUE7O0FBZDNCO0lBa0JJLDBCQUEwQixFQUFBOztBQWxCOUI7SUFzQkksa0JBQWtCO0lBQ2xCLHVCQUF1QjtJQUN2QixXQUFXO0lBQ1gsWUFBWTtJQUNaLGlCQUFpQjtJQUNqQixlQUFlLEVBQUE7O0FBM0JuQjtNQThCTSxZQUFZO01BQ1osV0FBVztNQUNYLGtCQUFrQixFQUFBOztBQWhDeEI7SUFxQ0kseUJBQWtDLEVBQUE7O0FBckN0QztJQXlDSSxtQkFBbUIsRUFBQTs7QUF6Q3ZCO0lBNkNJLHNCQzlCZ0I7SUQrQmhCLHVCQUF1QjtJQUN2QiwyQkFBMkIsRUFBQTs7QUEvQy9CO0lBbURJLHlCQUF5QjtJQUN6QixXQUFXO0lBQ1gsa0JBQWtCLEVBQUE7O0FBckR0QjtJQXlESSxhQUFhO0lBQ2IsZUFBZTtJQUNmLGdCQUFnQjtJQUNoQixjQ2xFcUIsRUFBQTs7QURNekI7SUFnRUksc0JBQXNCO0lBQ3RCLGtCQUFrQixFQUFBOztBQWpFdEI7TUFvRU0sZUFBZTtNQUNmLGdCQUFnQjtNQUNoQixjQzVFbUIsRUFBQTs7QURNekI7SUEyRUksWUFBWTtJQUNaLGtCQUFrQixFQUFBOztBQTVFdEI7TUErRU0sY0FBYztNQUNkLHlCQUF5QixFQUFBOztBQWhGL0I7UUFtRlEsNENBQTRDLEVBQUE7O0FBbkZwRDtRQXdGVSxXQUFXLEVBQUE7O0FBeEZyQjtNQThGTSx1QkFBdUI7TUFDdkIsc0JBQXNCLEVBQUE7O0FBL0Y1QjtNQW1HTSxnQkFBZ0I7TUFDaEIsY0MxR21CO01EMkduQixrQkFBa0I7TUFDbEIsU0FBUztNQUNULDJCQUEyQixFQUFBOztBQXZHakM7TUEyR00sZUFBZTtNQUNmLG1CQUFtQjtNQUNuQixjQUFjO01BQ2QsY0FBYyxFQUFBOztBQTlHcEI7TUFrSE0sY0FBYztNQUNkLFlBQVksRUFBQTs7QUFuSGxCO01BdUhNLGdCQUFnQixFQUFBOztBQXZIdEI7TUEySE0saUJBQWlCO01BQ2pCLHdCQUF3QixFQUFBOztBQTVIOUI7UUErSFEsVUFBVSxFQUFBOztBQS9IbEI7UUFtSVEsY0N6SWlCLEVBQUE7O0FETXpCO0lBeUlJLG9CQUFvQixFQUFBOztBQUl4QjtFQUNFLFVBQVUsRUFBQTs7QUFEWjtJQUlJLHFCQUFxQixFQUFBOztBQUl6QjtFQUNFLHdCQUF3QjtFQUN4Qix1QkFBdUI7RUFDdkIsMkJBQTJCO0VBQzNCLHFCQUFxQjtFQUNyQix1QkFBdUI7RUFDdkIscUNBQXFDLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGFzb3VyY2UtbWFuYWdlbWVudC9kYXRhc291cmNlLXBhZ2UuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0ICdzcmMvdGhlbWVzL2Jhc2UvY29sb3JzJztcblxuOmhvc3Qge1xuICB3aWR0aDogMTAwJTtcbiAgaGVpZ2h0OiAxMDAlO1xufVxuXG4uZGF0YXNvdXJjZS12aWV3IHtcbiAgd2lkdGg6IDEwMCU7XG4gIGJhY2tncm91bmQtY29sb3I6ICRiYWNrZ3JvdW5kLWNvbG9yO1xuICBoZWlnaHQ6IDEwMCU7XG5cbiAgLmFjdGlvbi1idXR0b25zIHtcbiAgICAubWF0LXJhaXNlZC1idXR0b24ge1xuICAgICAgbGluZS1oZWlnaHQ6IDMycHggIWltcG9ydGFudDtcbiAgICAgIHZlcnRpY2FsLWFsaWduOiBtaWRkbGU7XG4gICAgfVxuICB9XG5cbiAgLmdyaWQtaWNvbi1idG4ge1xuICAgIGxpbmUtaGVpZ2h0OiBub3JtYWwgIWltcG9ydGFudDtcbiAgICBoZWlnaHQ6IDI1cHggIWltcG9ydGFudDtcbiAgfVxuXG4gIC5pY29uLXNlYXJjaCB7XG4gICAgZm9udC1zaXplOiAyNnB4ICFpbXBvcnRhbnQ7XG4gIH1cblxuICAuc291cmNlLXJvdyB7XG4gICAgYm9yZGVyLXJhZGl1czogNTAlO1xuICAgIGJhY2tncm91bmQtY29sb3I6IHdoaXRlO1xuICAgIHdpZHRoOiA3MHB4O1xuICAgIGhlaWdodDogNzBweDtcbiAgICBtYXJnaW46IDIwcHggMTBweDtcbiAgICBjdXJzb3I6IHBvaW50ZXI7XG5cbiAgICAmID4gaW1nIHtcbiAgICAgIGhlaWdodDogNDVweDtcbiAgICAgIHdpZHRoOiA0NXB4O1xuICAgICAgYm9yZGVyLXJhZGl1czogMjAlO1xuICAgIH1cbiAgfVxuXG4gIC5zZWxlY3RlZCB7XG4gICAgYm9yZGVyOiAycHggJHByaW1hcnktYmx1ZS1iMSBzb2xpZDtcbiAgfVxuXG4gIC5uby1jbGljayB7XG4gICAgY3Vyc29yOiBub3QtYWxsb3dlZDtcbiAgfVxuXG4gIC50b29sYmFyLXdoaXRlIHtcbiAgICBiYWNrZ3JvdW5kLWNvbG9yOiAkcHJpbWFyeS13aGl0ZTtcbiAgICBoZWlnaHQ6IDQ1cHggIWltcG9ydGFudDtcbiAgICBtaW4taGVpZ2h0OiA0NXB4ICFpbXBvcnRhbnQ7XG4gIH1cblxuICAuYm9keS1jb250YWluZXIge1xuICAgIGhlaWdodDogY2FsYygxMDAlIC0gNTJweCk7XG4gICAgbWFyZ2luOiAzcHg7XG4gICAgcGFkZGluZzogNXB4IDNweCAwO1xuICB9XG5cbiAgLm1hc3Rlci1kZXRhaWwtY2FwdGlvbiB7XG4gICAgcGFkZGluZzogMTJweDtcbiAgICBmb250LXNpemU6IDE0cHg7XG4gICAgZm9udC13ZWlnaHQ6IDYwMDtcbiAgICBjb2xvcjogJHByaW1hcnktYmx1ZS1iMTtcbiAgfVxuXG4gIC5zb3VyY2VzLWdyaWQge1xuICAgIHBhZGRpbmc6IDNweCAzcHggM3B4IDA7XG4gICAgYm9yZGVyLXJhZGl1czogNXB4O1xuXG4gICAgLnMtbmFtZSB7XG4gICAgICBmb250LXNpemU6IDE0cHg7XG4gICAgICBmb250LXdlaWdodDogNjAwO1xuICAgICAgY29sb3I6ICRwcmltYXJ5LWJsdWUtYjE7XG4gICAgfVxuICB9XG5cbiAgLnJvdXRlcy1jYXJkIHtcbiAgICBwYWRkaW5nOiAzcHg7XG4gICAgYm9yZGVyLXJhZGl1czogNXB4O1xuXG4gICAgLnJvdXRlcy1jYXJkLWJvZHkge1xuICAgICAgb3ZlcmZsb3c6IGF1dG87XG4gICAgICBoZWlnaHQ6IGNhbGMoMTAwJSAtIDQzcHgpO1xuXG4gICAgICAubWF0LWRpdmlkZXIge1xuICAgICAgICBib3JkZXItY29sb3I6IHJnYmEoMCwgMCwgMCwgMC4wNSkgIWltcG9ydGFudDtcbiAgICAgIH1cblxuICAgICAgLnJvdXRlLWdyaWQtLWFjdGlvbi1idXR0b25zIHtcbiAgICAgICAgYnV0dG9uIHtcbiAgICAgICAgICB3aWR0aDogMjRweDtcbiAgICAgICAgfVxuICAgICAgfVxuICAgIH1cblxuICAgIC5tYXQtbWluaS1mYWIge1xuICAgICAgaGVpZ2h0OiAzNnB4ICFpbXBvcnRhbnQ7XG4gICAgICB3aWR0aDogMzZweCAhaW1wb3J0YW50O1xuICAgIH1cblxuICAgIC5kZXRhaWxzLXRpdGxlIHtcbiAgICAgIHRleHQtYWxpZ246IGxlZnQ7XG4gICAgICBjb2xvcjogJHByaW1hcnktYmx1ZS1iMTtcbiAgICAgIHBhZGRpbmctbGVmdDogMTBweDtcbiAgICAgIG1hcmdpbjogMDtcbiAgICAgIGZvbnQtd2VpZ2h0OiA2MDAgIWltcG9ydGFudDtcbiAgICB9XG5cbiAgICAuZGV0YWlscy1jb250ZW50IHtcbiAgICAgIGZvbnQtc2l6ZTogMTRweDtcbiAgICAgIGZvbnQtd2VpZ2h0OiBub3JtYWw7XG4gICAgICBsaW5lLWhlaWdodDogMztcbiAgICAgIHBhZGRpbmc6IDAgOXB4O1xuICAgIH1cblxuICAgIC5wcm9wLW5hbWUge1xuICAgICAgY29sb3I6ICM3MjcyNzI7XG4gICAgICB3aWR0aDogMTM2cHg7XG4gICAgfVxuXG4gICAgLnJvdXRlcy1ncmlkIHtcbiAgICAgIHBhZGRpbmctdG9wOiAzcHg7XG4gICAgfVxuXG4gICAgLm1lbnUtYnRuLWljb24ge1xuICAgICAgbWFyZ2luLXJpZ2h0OiAwcHg7XG4gICAgICB2ZXJ0aWNhbC1hbGlnbjogYmFzZWxpbmU7XG5cbiAgICAgICYubWF0LWVycm9yIHtcbiAgICAgICAgY29sb3I6IHJlZDtcbiAgICAgIH1cblxuICAgICAgJi5tYXQtcHJpbWFyeSB7XG4gICAgICAgIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIxO1xuICAgICAgfVxuICAgIH1cbiAgfVxuXG4gIC5keC12aXJ0dWFsLXJvdyB7XG4gICAgaGVpZ2h0OiAwICFpbXBvcnRhbnQ7XG4gIH1cbn1cblxuOjpuZy1kZWVwIC5zb3VyY2VEaWFsb2dDbGFzcyB7XG4gIHBhZGRpbmc6IDA7XG5cbiAgOjpuZy1kZWVwIC5tYXQtZGlhbG9nLWNvbnRhaW5lciB7XG4gICAgcGFkZGluZzogMCAhaW1wb3J0YW50O1xuICB9XG59XG5cbjo6bmctZGVlcCAudGVzdENvbm5lY3Rpdml0eUNsYXNzIHtcbiAgaGVpZ2h0OiAyMjJweCAhaW1wb3J0YW50O1xuICB3aWR0aDogNzY4cHggIWltcG9ydGFudDtcbiAgbWluLXdpZHRoOiA3NjhweCAhaW1wb3J0YW50O1xuICBwYWRkaW5nOiAwICFpbXBvcnRhbnQ7XG4gIGJhY2tncm91bmQtY29sb3I6IGJsYWNrO1xuICBib3JkZXItcmFkaXVzOiA3cHggN3B4IDAgMCAhaW1wb3J0YW50O1xufVxuIiwiLy8gQnJhbmRpbmcgY29sb3JzXG4kcHJpbWFyeS1ibHVlLWIxOiAjMWE4OWQ0O1xuJHByaW1hcnktYmx1ZS1iMjogIzAwNzdiZTtcbiRwcmltYXJ5LWJsdWUtYjM6ICMyMDZiY2U7XG4kcHJpbWFyeS1ibHVlLWI0OiAjMWQzYWIyO1xuXG4kcHJpbWFyeS1ob3Zlci1ibHVlOiAjMWQ2MWIxO1xuJGdyaWQtaG92ZXItY29sb3I6ICNmNWY5ZmM7XG4kZ3JpZC1oZWFkZXItYmctY29sb3I6ICNkN2VhZmE7XG4kZ3JpZC1oZWFkZXItY29sb3I6ICMwYjRkOTk7XG4kZ3JpZC10ZXh0LWNvbG9yOiAjNDY0NjQ2O1xuJGdyZXktdGV4dC1jb2xvcjogIzYzNjM2MztcblxuJHNlbGVjdGlvbi1oaWdobGlnaHQtY29sOiByZ2JhKDAsIDE0MCwgMjYwLCAwLjIpO1xuJHByaW1hcnktZ3JleS1nMTogI2QxZDNkMztcbiRwcmltYXJ5LWdyZXktZzI6ICM5OTk7XG4kcHJpbWFyeS1ncmV5LWczOiAjNzM3MzczO1xuJHByaW1hcnktZ3JleS1nNDogIzVjNjY3MDtcbiRwcmltYXJ5LWdyZXktZzU6ICMzMTMxMzE7XG4kcHJpbWFyeS1ncmV5LWc2OiAjZjVmNWY1O1xuJHByaW1hcnktZ3JleS1nNzogIzNkM2QzZDtcblxuJHByaW1hcnktd2hpdGU6ICNmZmY7XG4kcHJpbWFyeS1ibGFjazogIzAwMDtcbiRwcmltYXJ5LXJlZDogI2FiMGUyNztcbiRwcmltYXJ5LWdyZWVuOiAjNzNiNDIxO1xuJHByaW1hcnktb3JhbmdlOiAjZjA3NjAxO1xuXG4kc2Vjb25kYXJ5LWdyZWVuOiAjNmZiMzIwO1xuJHNlY29uZGFyeS15ZWxsb3c6ICNmZmJlMDA7XG4kc2Vjb25kYXJ5LW9yYW5nZTogI2ZmOTAwMDtcbiRzZWNvbmRhcnktcmVkOiAjZDkzZTAwO1xuJHNlY29uZGFyeS1iZXJyeTogI2FjMTQ1YTtcbiRzZWNvbmRhcnktcHVycGxlOiAjOTE0MTkxO1xuXG4kc3RyaW5nLXR5cGUtY29sb3I6ICM0OTk1YjI7XG4kbnVtYmVyLXR5cGUtY29sb3I6ICMwMGIxODA7XG4kZ2VvLXR5cGUtY29sb3I6ICM4NDVlYzI7XG4kZGF0ZS10eXBlLWNvbG9yOiAjZDE5NjIxO1xuXG4kdHlwZS1jaGlwLW9wYWNpdHk6IDE7XG4kc3RyaW5nLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkc3RyaW5nLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kbnVtYmVyLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkbnVtYmVyLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZ2VvLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZ2VvLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZGF0ZS10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGRhdGUtdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcblxuJHJlcG9ydC1kZXNpZ25lci1zZXR0aW5ncy1iZy1jb2xvcjogI2Y1ZjlmYztcbiRiYWNrZ3JvdW5kLWNvbG9yOiAjZjVmOWZjO1xuIl19 */"

/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/datasource-page.component.ts":
/*!*************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/datasource-page.component.ts ***!
  \*************************************************************************************************/
/*! exports provided: DatasourceComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DatasourceComponent", function() { return DatasourceComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! devextreme-angular/ui/data-grid */ "./node_modules/devextreme-angular/ui/data-grid.js");
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");
/* harmony import */ var _wb_comp_configs__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../wb-comp-configs */ "./src/app/modules/workbench/wb-comp-configs.ts");
/* harmony import */ var _services_datasource_service__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../services/datasource.service */ "./src/app/modules/workbench/services/datasource.service.ts");
/* harmony import */ var _common_utils_cron2Readable__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../../common/utils/cron2Readable */ "./src/app/common/utils/cron2Readable.ts");
/* harmony import */ var _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../../../../common/services/toastMessage.service */ "./src/app/common/services/toastMessage.service.ts");
/* harmony import */ var _createSource_dialog_createSource_dialog_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./createSource-dialog/createSource-dialog.component */ "./src/app/modules/workbench/components/datasource-management/createSource-dialog/createSource-dialog.component.ts");
/* harmony import */ var _create_route_dialog_create_route_dialog_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./create-route-dialog/create-route-dialog.component */ "./src/app/modules/workbench/components/datasource-management/create-route-dialog/create-route-dialog.component.ts");
/* harmony import */ var _test_connectivity_test_connectivity_component__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ./test-connectivity/test-connectivity.component */ "./src/app/modules/workbench/components/datasource-management/test-connectivity/test-connectivity.component.ts");
/* harmony import */ var _confirm_action_dialog_confirm_action_dialog_component__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ./confirm-action-dialog/confirm-action-dialog.component */ "./src/app/modules/workbench/components/datasource-management/confirm-action-dialog/confirm-action-dialog.component.ts");
/* harmony import */ var _logs_dialog__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ./logs-dialog */ "./src/app/modules/workbench/components/datasource-management/logs-dialog/index.ts");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! lodash/isUndefined */ "./node_modules/lodash/isUndefined.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_14___default = /*#__PURE__*/__webpack_require__.n(lodash_isUndefined__WEBPACK_IMPORTED_MODULE_14__);
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! lodash/forEach */ "./node_modules/lodash/forEach.js");
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_15___default = /*#__PURE__*/__webpack_require__.n(lodash_forEach__WEBPACK_IMPORTED_MODULE_15__);
/* harmony import */ var lodash_countBy__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! lodash/countBy */ "./node_modules/lodash/countBy.js");
/* harmony import */ var lodash_countBy__WEBPACK_IMPORTED_MODULE_16___default = /*#__PURE__*/__webpack_require__.n(lodash_countBy__WEBPACK_IMPORTED_MODULE_16__);
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_17___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_17__);
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_18__ = __webpack_require__(/*! lodash/map */ "./node_modules/lodash/map.js");
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_18___default = /*#__PURE__*/__webpack_require__.n(lodash_map__WEBPACK_IMPORTED_MODULE_18__);
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_19__ = __webpack_require__(/*! lodash/find */ "./node_modules/lodash/find.js");
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_19___default = /*#__PURE__*/__webpack_require__.n(lodash_find__WEBPACK_IMPORTED_MODULE_19__);
/* harmony import */ var lodash_findKey__WEBPACK_IMPORTED_MODULE_20__ = __webpack_require__(/*! lodash/findKey */ "./node_modules/lodash/findKey.js");
/* harmony import */ var lodash_findKey__WEBPACK_IMPORTED_MODULE_20___default = /*#__PURE__*/__webpack_require__.n(lodash_findKey__WEBPACK_IMPORTED_MODULE_20__);
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_21__ = __webpack_require__(/*! lodash/filter */ "./node_modules/lodash/filter.js");
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_21___default = /*#__PURE__*/__webpack_require__.n(lodash_filter__WEBPACK_IMPORTED_MODULE_21__);























/* NOTE: In the below channel and source are synonyms and refer to a single connection to a host. */
var DatasourceComponent = /** @class */ (function () {
    function DatasourceComponent(dialog, datasourceService, notify, snackBar) {
        this.dialog = dialog;
        this.datasourceService = datasourceService;
        this.notify = notify;
        this.snackBar = snackBar;
        this.unFilteredSourceData = [];
        this.routesData = [];
        this.sourceData = [];
        this.sourceTypes = _wb_comp_configs__WEBPACK_IMPORTED_MODULE_5__["CHANNEL_TYPES"];
        // channel activation/deactivation request is pending
        this.channelToggleRequestPending = false;
        this.show = false;
        this.channelEditable = false;
    }
    DatasourceComponent.prototype.ngOnInit = function () {
        this.getSources();
    };
    DatasourceComponent.prototype.ngOnDestroy = function () { };
    DatasourceComponent.prototype.getSources = function () {
        var _this = this;
        this.datasourceService
            .getSourceList()
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["map"])(function (channels) {
            return lodash_map__WEBPACK_IMPORTED_MODULE_18__(channels, function (channel) { return (tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, channel, JSON.parse(channel.channelMetadata))); });
        }))
            .subscribe(function (channels) {
            _this.unFilteredSourceData = channels;
            _this.countSourceByType(_this.unFilteredSourceData);
        });
    };
    DatasourceComponent.prototype.countSourceByType = function (sources) {
        var sCountObj = lodash_countBy__WEBPACK_IMPORTED_MODULE_16__(sources, 'channelType');
        this.selectedSourceType = lodash_findKey__WEBPACK_IMPORTED_MODULE_20__(sCountObj);
        this.filterSourcesByType(sources, this.selectedSourceType);
        lodash_forEach__WEBPACK_IMPORTED_MODULE_15__(this.sourceTypes, function (value) {
            var count = lodash_get__WEBPACK_IMPORTED_MODULE_17__(sCountObj, value.uid);
            value.count = lodash_isUndefined__WEBPACK_IMPORTED_MODULE_14__(count) ? 0 : count;
            value.color = value.count > 0 ? 'primary' : 'warn';
        });
    };
    DatasourceComponent.prototype.filterSourcesByType = function (channelData, cType) {
        this.sourceData = lodash_filter__WEBPACK_IMPORTED_MODULE_21__(channelData, ['channelType', cType]);
        if (this.sourceData.length > 0) {
            var firstChannelId = this.sourceData[0].bisChannelSysId;
            if (this.selectedSourceData) {
                var selectedId_1 = this.selectedSourceData.bisChannelSysId;
                var alreadySelected = lodash_find__WEBPACK_IMPORTED_MODULE_19__(this.sourceData, function (_a) {
                    var bisChannelSysId = _a.bisChannelSysId;
                    return bisChannelSysId === selectedId_1;
                });
                if (alreadySelected) {
                    this.selectSingleChannel(selectedId_1);
                }
                else {
                    this.selectSingleChannel(firstChannelId);
                }
            }
            else {
                this.selectSingleChannel(firstChannelId);
            }
        }
    };
    DatasourceComponent.prototype.selectSingleChannel = function (channelID) {
        var _this = this;
        setTimeout(function () {
            _this.channelsGrid.instance.deselectAll();
            _this.channelsGrid.instance.selectRows([channelID], false);
            _this.getRoutesForChannel(channelID);
        });
    };
    DatasourceComponent.prototype.onSourceSelectionChanged = function (event) {
        if (!lodash_isUndefined__WEBPACK_IMPORTED_MODULE_14__(event.currentDeselectedRowKeys[0]) &&
            event.selectedRowKeys.length > 0) {
            this.channelEditable = true;
            this.selectChannel(event.selectedRowsData[0]);
            if (!this.channelToggleRequestPending) {
                this.getRoutesForChannel(event.selectedRowKeys[0]);
            }
        }
        else if (event.selectedRowKeys.length > 0) {
            this.channelEditable = true;
            this.selectChannel(event.selectedRowsData[0]);
        }
        else {
            this.channelEditable = false;
            this.selectChannel(null);
            this.routesData = [];
        }
    };
    DatasourceComponent.prototype.selectChannel = function (channel) {
        this.selectedSourceData = channel;
    };
    DatasourceComponent.prototype.sourceSelectedType = function (sourceType, channelCount) {
        if (channelCount > 0) {
            this.filterSourcesByType(this.unFilteredSourceData, sourceType);
            this.selectedSourceType = sourceType;
        }
    };
    DatasourceComponent.prototype.createSource = function (channelData) {
        var _this = this;
        var channelMetadata = lodash_isUndefined__WEBPACK_IMPORTED_MODULE_14__(channelData)
            ? []
            : JSON.parse(channelData.channelMetadata);
        var dateDialogRef = this.dialog.open(_createSource_dialog_createSource_dialog_component__WEBPACK_IMPORTED_MODULE_9__["CreateSourceDialogComponent"], {
            hasBackdrop: true,
            autoFocus: false,
            closeOnNavigation: true,
            disableClose: true,
            height: '60%',
            width: '70%',
            minWidth: '750px',
            minHeight: '600px',
            maxWidth: '900px',
            panelClass: 'sourceDialogClass',
            data: channelMetadata
        });
        dateDialogRef.afterClosed().subscribe(function (data) {
            if (!lodash_isUndefined__WEBPACK_IMPORTED_MODULE_14__(data)) {
                var payload = {
                    createdBy: '',
                    productCode: '',
                    projectCode: '',
                    customerCode: '',
                    channelType: data.sourceDetails.channelType,
                    // Channel metadata JSON object have to be stringified  to store in MariaDB due to BE limitation.
                    channelMetadata: JSON.stringify(data.sourceDetails)
                };
                if (data.opType === 'create') {
                    _this.datasourceService.createSource(payload).subscribe(function () {
                        _this.getSources();
                    });
                }
                else {
                    payload.createdBy = channelData.createdBy;
                    payload.productCode = channelData.productCode;
                    payload.projectCode = channelData.projectCode;
                    payload.customerCode = channelData.customerCode;
                    payload.status = !channelData.status ? 0 : 1;
                    _this.datasourceService
                        .updateSource(channelData.bisChannelSysId, payload)
                        .subscribe(function () {
                        _this.getSources();
                    });
                }
            }
        });
    };
    DatasourceComponent.prototype.deleteChannel = function (channelID) {
        var _this = this;
        var dialogRef = this.dialog.open(_confirm_action_dialog_confirm_action_dialog_component__WEBPACK_IMPORTED_MODULE_12__["ConfirmActionDialogComponent"], {
            width: '350px',
            data: {
                typeTitle: 'Channel Name',
                typeName: this.selectedSourceData.channelName,
                routesNr: this.routesData.length
            }
        });
        dialogRef.afterClosed().subscribe(function (confirmed) {
            if (confirmed) {
                _this.datasourceService.deleteChannel(channelID).subscribe(function () {
                    _this.notify.success('Channel deleted successfully');
                    _this.getSources();
                });
            }
        });
    };
    DatasourceComponent.prototype.testChannel = function (channelID) {
        var _this = this;
        this.datasourceService.testChannel(channelID).subscribe(function (data) {
            _this.showConnectivityLog(data);
        });
    };
    DatasourceComponent.prototype.testRoute = function (routeData) {
        var _this = this;
        var routeID = routeData.bisRouteSysId;
        this.datasourceService.testRoute(routeID).subscribe(function (data) {
            _this.showConnectivityLog(data);
        });
    };
    DatasourceComponent.prototype.showConnectivityLog = function (logData) {
        this.snackBar.openFromComponent(_test_connectivity_test_connectivity_component__WEBPACK_IMPORTED_MODULE_11__["TestConnectivityComponent"], {
            data: logData,
            horizontalPosition: 'center',
            panelClass: ['mat-elevation-z9', 'testConnectivityClass']
        });
    };
    DatasourceComponent.prototype.onToolbarPreparing = function (e) {
        e.toolbarOptions.items.unshift({
            location: 'before',
            template: 'sourceTypeTemplate'
        });
    };
    DatasourceComponent.prototype.onRoutesToolbarPreparing = function (e) {
        e.toolbarOptions.items.unshift({
            location: 'before',
            template: 'nameTemplate'
        });
    };
    DatasourceComponent.prototype.getRoutesForChannel = function (channelID) {
        var _this = this;
        this.datasourceService
            .getRoutesList(channelID)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["map"])(function (routes) {
            return lodash_map__WEBPACK_IMPORTED_MODULE_18__(routes, function (route) { return (tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, route, JSON.parse(route.routeMetadata))); });
        }))
            .subscribe(function (routes) {
            _this.routesData = routes;
        });
    };
    DatasourceComponent.prototype.createRoute = function (routeData) {
        var _this = this;
        var routeMetadata = lodash_isUndefined__WEBPACK_IMPORTED_MODULE_14__(routeData.routeMetadata)
            ? []
            : JSON.parse(routeData.routeMetadata);
        var dateDialogRef = this.dialog.open(_create_route_dialog_create_route_dialog_component__WEBPACK_IMPORTED_MODULE_10__["CreateRouteDialogComponent"], {
            hasBackdrop: true,
            autoFocus: false,
            closeOnNavigation: true,
            disableClose: true,
            height: '60%',
            width: '70%',
            minWidth: '750px',
            minHeight: '600px',
            maxWidth: '900px',
            panelClass: 'sourceDialogClass',
            data: {
                routeMetadata: routeMetadata,
                channelID: this.selectedSourceData.bisChannelSysId,
                channelName: this.selectedSourceData.channelName
            }
        });
        dateDialogRef.afterClosed().subscribe(function (data) {
            if (!lodash_isUndefined__WEBPACK_IMPORTED_MODULE_14__(data)) {
                var payload = {
                    createdBy: '',
                    // Route metadata JSON object have to be stringified  to store in MariaDB due to BE limitation.
                    routeMetadata: JSON.stringify(data.routeDetails)
                };
                if (data.opType === 'create') {
                    _this.datasourceService
                        .createRoute(routeData, payload)
                        .subscribe(function (createdRoute) {
                        var promise = _this.afterRouteAddedChanged(createdRoute);
                        if (promise) {
                            promise.then(function () {
                                _this.getRoutesForChannel(routeData);
                            });
                        }
                        else {
                            _this.getRoutesForChannel(routeData);
                        }
                    });
                }
                else {
                    payload.createdBy = routeData.createdBy;
                    payload.status = !routeData.status ? 0 : 1;
                    _this.datasourceService
                        .updateRoute(routeData.bisChannelSysId, routeData.bisRouteSysId, payload)
                        .subscribe(function () {
                        _this.getRoutesForChannel(routeData.bisChannelSysId);
                    });
                }
            }
        });
    };
    DatasourceComponent.prototype.afterRouteAddedChanged = function (createdRoute) {
        var channelId = createdRoute.bisChannelSysId;
        var routeId = createdRoute.bisRouteSysId;
        var selectedChannelId = this.selectedSourceData.bisChannelSysId;
        var isChannelNotActive = this.selectedSourceData.status === 0;
        if (isChannelNotActive && channelId === selectedChannelId) {
            return this.datasourceService
                .toggleRoute(channelId, routeId, false)
                .toPromise();
        }
    };
    DatasourceComponent.prototype.deleteRoute = function (routeData) {
        var _this = this;
        var dialogRef = this.dialog.open(_confirm_action_dialog_confirm_action_dialog_component__WEBPACK_IMPORTED_MODULE_12__["ConfirmActionDialogComponent"], {
            width: '350px',
            data: {
                typeTitle: 'Route Name',
                typeName: routeData.routeName
            }
        });
        dialogRef.afterClosed().subscribe(function (confirmed) {
            if (confirmed) {
                var channelID_1 = routeData.bisChannelSysId;
                var routeID = routeData.bisRouteSysId;
                _this.datasourceService.deleteRoute(channelID_1, routeID).subscribe(function () {
                    _this.getRoutesForChannel(channelID_1);
                });
            }
        });
    };
    DatasourceComponent.prototype.calculateScheduleCellValue = function (rowData) {
        var _a = rowData.schedulerExpression, cronexp = _a.cronexp, activeTab = _a.activeTab, timezone = _a.timezone;
        return Object(_common_utils_cron2Readable__WEBPACK_IMPORTED_MODULE_7__["generateSchedule"])(cronexp, activeTab, timezone);
    };
    DatasourceComponent.prototype.togglePWD = function () {
        this.show = !this.show;
    };
    DatasourceComponent.prototype.openLogsDialog = function (routeData) {
        this.dialog.open(_logs_dialog__WEBPACK_IMPORTED_MODULE_13__["LogsDialogComponent"], {
            hasBackdrop: true,
            autoFocus: false,
            closeOnNavigation: true,
            disableClose: true,
            height: '80vh',
            width: '80vw',
            panelClass: 'sourceDialogClass',
            data: tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, routeData, { channelName: this.selectedSourceData.channelName })
        });
    };
    DatasourceComponent.prototype.toggleRouteActivation = function (route) {
        var _this = this;
        var bisChannelSysId = route.bisChannelSysId, bisRouteSysId = route.bisRouteSysId, status = route.status;
        this.datasourceService
            .toggleRoute(bisChannelSysId, bisRouteSysId, !status)
            .subscribe(function () {
            route.status = _this.reverseStatus(status);
        });
    };
    DatasourceComponent.prototype.toggleChannelActivation = function (channel) {
        var _this = this;
        var status = channel.status;
        this.channelToggleRequestPending = true;
        this.datasourceService
            .toggleChannel(channel.bisChannelSysId, !status)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_4__["finalize"])(function () {
            _this.channelToggleRequestPending = false;
        }))
            .subscribe(function () {
            _this.channelToggleRequestPending = false;
            channel.status = _this.reverseStatus(status);
            _this.getRoutesForChannel(channel.bisChannelSysId);
        });
    };
    DatasourceComponent.prototype.toggleAllRoutesOnFrontEnd = function (status) {
        lodash_forEach__WEBPACK_IMPORTED_MODULE_15__(this.routesData, function (route) {
            route.status = status;
        });
    };
    DatasourceComponent.prototype.reverseStatus = function (status) {
        return status === 1 ? 0 : 1;
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ViewChild"])('channelsGrid'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3__["DxDataGridComponent"])
    ], DatasourceComponent.prototype, "channelsGrid", void 0);
    DatasourceComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'datasource-page',
            template: __webpack_require__(/*! ./datasource-page.component.html */ "./src/app/modules/workbench/components/datasource-management/datasource-page.component.html"),
            styles: [__webpack_require__(/*! ./datasource-page.component.scss */ "./src/app/modules/workbench/components/datasource-management/datasource-page.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialog"],
            _services_datasource_service__WEBPACK_IMPORTED_MODULE_6__["DatasourceService"],
            _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_8__["ToastService"],
            _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatSnackBar"]])
    ], DatasourceComponent);
    return DatasourceComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/index.ts":
/*!*****************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/index.ts ***!
  \*****************************************************************************/
/*! exports provided: CreateSourceDialogComponent, CreateRouteDialogComponent, DatasourceComponent, TestConnectivityComponent, ConfirmActionDialogComponent, LogsDialogComponent, SourceFolderDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _createSource_dialog_createSource_dialog_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./createSource-dialog/createSource-dialog.component */ "./src/app/modules/workbench/components/datasource-management/createSource-dialog/createSource-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CreateSourceDialogComponent", function() { return _createSource_dialog_createSource_dialog_component__WEBPACK_IMPORTED_MODULE_0__["CreateSourceDialogComponent"]; });

/* harmony import */ var _create_route_dialog_create_route_dialog_component__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./create-route-dialog/create-route-dialog.component */ "./src/app/modules/workbench/components/datasource-management/create-route-dialog/create-route-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CreateRouteDialogComponent", function() { return _create_route_dialog_create_route_dialog_component__WEBPACK_IMPORTED_MODULE_1__["CreateRouteDialogComponent"]; });

/* harmony import */ var _datasource_page_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./datasource-page.component */ "./src/app/modules/workbench/components/datasource-management/datasource-page.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DatasourceComponent", function() { return _datasource_page_component__WEBPACK_IMPORTED_MODULE_2__["DatasourceComponent"]; });

/* harmony import */ var _test_connectivity_test_connectivity_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./test-connectivity/test-connectivity.component */ "./src/app/modules/workbench/components/datasource-management/test-connectivity/test-connectivity.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "TestConnectivityComponent", function() { return _test_connectivity_test_connectivity_component__WEBPACK_IMPORTED_MODULE_3__["TestConnectivityComponent"]; });

/* harmony import */ var _confirm_action_dialog_confirm_action_dialog_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./confirm-action-dialog/confirm-action-dialog.component */ "./src/app/modules/workbench/components/datasource-management/confirm-action-dialog/confirm-action-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ConfirmActionDialogComponent", function() { return _confirm_action_dialog_confirm_action_dialog_component__WEBPACK_IMPORTED_MODULE_4__["ConfirmActionDialogComponent"]; });

/* harmony import */ var _logs_dialog__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./logs-dialog */ "./src/app/modules/workbench/components/datasource-management/logs-dialog/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "LogsDialogComponent", function() { return _logs_dialog__WEBPACK_IMPORTED_MODULE_5__["LogsDialogComponent"]; });

/* harmony import */ var _select_folder_dialog__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./select-folder-dialog */ "./src/app/modules/workbench/components/datasource-management/select-folder-dialog/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SourceFolderDialogComponent", function() { return _select_folder_dialog__WEBPACK_IMPORTED_MODULE_6__["SourceFolderDialogComponent"]; });










/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/logs-dialog/index.ts":
/*!*****************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/logs-dialog/index.ts ***!
  \*****************************************************************************************/
/*! exports provided: LogsDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _logs_dialog_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./logs-dialog.component */ "./src/app/modules/workbench/components/datasource-management/logs-dialog/logs-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "LogsDialogComponent", function() { return _logs_dialog_component__WEBPACK_IMPORTED_MODULE_0__["LogsDialogComponent"]; });




/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/logs-dialog/logs-dialog.component.html":
/*!***********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/logs-dialog/logs-dialog.component.html ***!
  \***********************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div mat-dialog-title fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n  <h3>{{ routeData.routeName }}</h3>\n  <button mat-icon-button color=\"warn\" class=\"close-button\" (click)=\"close()\">\n    <mat-icon fontIcon=\"icon-close\"></mat-icon>\n  </button>\n</div>\n<div mat-dialog-content id=\"route-log__dialog-content-container\">\n  <div class=\"logs__route-details\">\n    <div fxLayout=\"row\" fxLayoutalign=\"start center\" fxLayout.lt-md=\"column\">\n      <div class=\"logs__route-details__cell\" fxFlex=\"50\" fxLayout=\"row\">\n        <span class=\"logs__route-details__cell__label\" i18n\n          >Channel Name :\n        </span>\n        <span e2e=\"channel-name-route-logs\">{{ routeData?.channelName }}</span>\n      </div>\n      <div class=\"logs__route-details__cell\" fxFlex=\"50\" fxLayout=\"row\">\n        <span class=\"logs__route-details__cell__label\" i18n\n          >File pattern :\n        </span>\n        <span e2e=\"file-pattern-route-logs\">{{ routeData?.filePattern }}</span>\n      </div>\n    </div>\n    <mat-divider></mat-divider>\n    <div fxLayout=\"row\" fxLayoutalign=\"start center\" fxLayout.lt-md=\"column\">\n      <div class=\"logs__route-details__cell\" fxFlex=\"50\" fxLayout=\"row\">\n        <span class=\"logs__route-details__cell__label\" i18n\n          >Modified by :\n        </span>\n        <span e2e=\"modifiedby-route-logs\">{{ routeData?.modifiedBy || routeData?.mcreatedBy }}</span>\n      </div>\n      <mat-divider [vertical]=\"true\"></mat-divider>\n      <div class=\"logs__route-details__cell\" fxFlex=\"50\" fxLayout=\"row\">\n        <span class=\"logs__route-details__cell__label\" i18n\n          >Modified at :\n        </span>\n        <span e2e=\"modified-at-route-logs\">{{\n          routeData?.modifiedDate || routeData?.createdDate | date: 'short'\n        }}</span>\n      </div>\n    </div>\n    <mat-divider></mat-divider>\n    <div fxLayout=\"row\" fxLayoutalign=\"start center\" fxLayout.lt-md=\"column\">\n      <div class=\"logs__route-details__cell\" fxFlex=\"50\" fxLayout=\"row\">\n        <span class=\"logs__route-details__cell__label\" i18n\n          >Destination :\n        </span>\n        <span e2e=\"destination-route-logs\">{{ routeData?.destinationLocation }}</span>\n      </div>\n      <mat-divider [vertical]=\"true\"></mat-divider>\n      <div class=\"logs__route-details__cell\" fxFlex=\"50\" fxLayout=\"row\">\n        <span class=\"logs__route-details__cell__label\" i18n>Source : </span>\n        <span e2e=\"source-loc-route-logs\">{{ routeData?.sourceLocation }}</span>\n      </div>\n    </div>\n    <mat-divider></mat-divider>\n    <div fxLayout=\"row\" fxLayoutalign=\"start center\" fxLayout.lt-md=\"column\">\n      <div class=\"logs__route-details__cell\" fxFlex=\"50\" fxLayout=\"row\">\n        <span class=\"logs__route-details__cell__label\" i18n\n          >Last Fire Time :\n        </span>\n        <span e2e=\"last-fire-route-logs\">{{\n          lastFireTime > 0 ? (lastFireTime | date: 'short') : 'NA'\n        }}</span>\n      </div>\n      <mat-divider [vertical]=\"true\"></mat-divider>\n      <div class=\"logs__route-details__cell\" fxFlex=\"50\" fxLayout=\"row\">\n        <span class=\"logs__route-details__cell__label\" i18n\n          >Next Fire Time :\n        </span>\n        <span e2e=\"next-fire-route-logs\">{{\n          nextFireTime > 0 ? (nextFireTime | date: 'short') : 'NA'\n        }}</span>\n      </div>\n    </div>\n  </div>\n\n  <dx-data-grid\n    id=\"routeLogsGrid\"\n    class=\"mat-elevation-z3 route-log__data-grid\"\n    [height]=\"customGridHeight\"\n    width=\"100%\"\n    e2e=\"route-logs-container\"\n    [allowColumnResizing]=\"true\"\n    [columnMinWidth]=\"50\"\n    [dataSource]=\"logs\"\n    [showBorders]=\"false\"\n    [columnAutoWidth]=\"true\"\n    [showRowLines]=\"false\"\n    [showColumnLines]=\"false\"\n    [rowAlternationEnabled]=\"true\"\n    rowTemplate=\"logTableRowTemplate\"\n    e2e=\"route-logs-container\"\n  >\n    <dxo-scrolling mode=\"virtual\" [useNative]=\"false\"></dxo-scrolling>\n    <dxi-column\n      caption=\"File Pattern\"\n      dataField=\"filePattern\"\n      alignment=\"left\"\n    ></dxi-column>\n    <dxi-column\n      caption=\"File Name\"\n      dataField=\"fileName\"\n      alignment=\"left\"\n    ></dxi-column>\n    <dxi-column\n      caption=\"Actual File Rec Date\"\n      dataField=\"actualFileRecDate\"\n      alignment=\"left\"\n    ></dxi-column>\n    <dxi-column\n      caption=\"Rec File Name\"\n      dataField=\"recdFileName\"\n      alignment=\"left\"\n    ></dxi-column>\n    <dxi-column\n      caption=\"Rec File Size\"\n      dataField=\"recdFileSize\"\n      alignment=\"left\"\n    ></dxi-column>\n    <dxi-column\n      caption=\"File Status\"\n      dataField=\"mflFileStatus\"\n      alignment=\"left\"\n    ></dxi-column>\n    <dxi-column\n      caption=\"Process State\"\n      dataField=\"bisProcessState\"\n      alignment=\"left\"\n    ></dxi-column>\n    <dxi-column\n      caption=\"Transfer Start\"\n      dataField=\"transferStartTime\"\n      alignment=\"left\"\n    ></dxi-column>\n    <dxi-column\n      caption=\"Transfer End\"\n      dataField=\"transferEndTime\"\n      alignment=\"left\"\n    ></dxi-column>\n    <dxi-column\n      caption=\"Transfer Duration (ms)\"\n      dataField=\"transferDuration\"\n      alignment=\"left\"\n    ></dxi-column>\n    <dxi-column\n      caption=\"Modified at\"\n      dataField=\"modifiedDate\"\n      alignment=\"left\"\n    ></dxi-column>\n    <dxi-column\n      caption=\"Created at\"\n      dataField=\"createdDate\"\n      alignment=\"left\"\n    ></dxi-column>\n\n    <tbody\n      class=\"logRow dx-row\"\n      e2e=\"route-log-row\"\n      *dxTemplate=\"let logRow of 'logTableRowTemplate'\"\n    >\n      <tr class=\"tr-row\" e2e=\"route-log-row-tr\">\n        <td e2e=\"route-log-filePattern\">{{ logRow.data.filePattern }}</td>\n        <td e2e=\"route-log-fileName\" [matTooltip]=\"logRow.data.fileName\">\n          {{ logRow.data.fileName }}\n        </td>\n        <td e2e=\"route-log-actualFileRecDate\">\n          {{ logRow.data.actualFileRecDate | date: 'short' }}\n        </td>\n        <td\n          e2e=\"route-log-recdFileName\"\n          [matTooltip]=\"logRow.data.recdFileName\"\n        >\n          {{ logRow.data.recdFileName }}\n        </td>\n        <td e2e=\"route-log-recdFileSize\">{{ logRow.data.recdFileSize }}</td>\n        <td e2e=\"route-log-mflFileStatus\">{{ logRow.data.mflFileStatus }}</td>\n        <td e2e=\"route-log-bisProcessState\">\n          {{ logRow.data.bisProcessState }}\n        </td>\n        <td e2e=\"route-log-transferStartTime\">\n          {{ logRow.data.transferStartTime | date: 'MM/dd/yyyy, HH:mm:ss.SSS' }}\n        </td>\n        <td e2e=\"route-log-transferEndTime\">\n          {{ logRow.data.transferEndTime | date: 'MM/dd/yyyy, HH:mm:ss.SSS' }}\n        </td>\n\n        <td e2e=\"route-log-transferDuration\">\n          {{ logRow.data.transferDuration }}\n        </td>\n        <td e2e=\"route-log-modifiedDate\">\n          {{ logRow.data.modifiedDate | date: 'short' }}\n        </td>\n        <td e2e=\"route-log-createdDate\">\n          {{ logRow.data.createdDate | date: 'short' }}\n        </td>\n      </tr>\n    </tbody>\n  </dx-data-grid>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/logs-dialog/logs-dialog.component.scss":
/*!***********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/logs-dialog/logs-dialog.component.scss ***!
  \***********************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  display: flex;\n  padding: 10px;\n  flex-direction: column;\n  height: 95%; }\n\n[mat-dialog-title] {\n  padding: 0 20px 0 30px;\n  border-bottom: 1px solid #d1d3d3; }\n\n[mat-dialog-title] h3 {\n    color: #0077be; }\n\n.mat-card {\n  margin: 0;\n  padding: 20px; }\n\n.mat-dialog-content {\n  margin: 0;\n  flex-grow: 1;\n  display: flex;\n  flex-direction: column; }\n\n.title {\n  text-align: left;\n  color: #1a89d4;\n  padding-left: 10px;\n  margin: 0;\n  font-weight: 600 !important; }\n\n.logs__route-details .mat-divider.mat-divider-horizontal {\n  width: calc(100% - 40px);\n  margin-left: 20px; }\n\n.logs__route-details__cell {\n  font-size: 14px;\n  font-weight: normal;\n  line-height: 3;\n  padding: 0 9px; }\n\n.logs__route-details__cell__label {\n    color: #727272;\n    width: 136px; }\n\n::ng-deep #routeLogsGrid .dx-row > tr > td {\n  padding: 3px 7px; }\n\n::ng-deep #routeLogsGrid .dx-freespace-row {\n  visibility: hidden; }\n\n::ng-deep #routeLogsGrid tbody:nth-child(even) {\n  background-color: #f5f9fc; }\n\n.close-button {\n  -webkit-transform: translateY(-6px);\n          transform: translateY(-6px); }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGFzb3VyY2UtbWFuYWdlbWVudC9sb2dzLWRpYWxvZy9sb2dzLWRpYWxvZy5jb21wb25lbnQuc2NzcyIsInNyYy9hcHAvbW9kdWxlcy93b3JrYmVuY2gvY29tcG9uZW50cy9kYXRhc291cmNlLW1hbmFnZW1lbnQvbG9ncy1kaWFsb2cvbG9ncy1kaWFsb2cuY29tcG9uZW50LnNjc3MiLCIvVXNlcnMvYmFybmFtdW10eWFuL1Byb2plY3RzL21vZHVzL3NpcC9zYXctd2ViL3NyYy90aGVtZXMvYmFzZS9fY29sb3JzLnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSxhQUFhO0VBQ2IsYUFBYTtFQUNiLHNCQUFzQjtFQUN0QixXQUFXLEVBQUE7O0FDQWI7RURJRSxzQkFBc0I7RUFDdEIsZ0NFR3VCLEVBQUE7O0FETHZCO0lES0UsY0VacUIsRUFBQTs7QUZnQnpCO0VBQ0UsU0FBUztFQUNULGFBQWEsRUFBQTs7QUFHZjtFQUNFLFNBQVM7RUFDVCxZQUFZO0VBQ1osYUFBYTtFQUNiLHNCQUFzQixFQUFBOztBQUd4QjtFQUNFLGdCQUFnQjtFQUNoQixjRS9CdUI7RUZnQ3ZCLGtCQUFrQjtFQUNsQixTQUFTO0VBQ1QsMkJBQTJCLEVBQUE7O0FBRzdCO0VBRUksd0JBQXdCO0VBQ3hCLGlCQUFpQixFQUFBOztBQUduQjtFQUNFLGVBQWU7RUFDZixtQkFBbUI7RUFDbkIsY0FBYztFQUNkLGNBQWMsRUFBQTs7QUFFZDtJQUNFLGNBQWM7SUFDZCxZQUFZLEVBQUE7O0FBS2xCO0VBQ0UsZ0JBQWdCLEVBQUE7O0FBR2xCO0VBQ0Usa0JBQWtCLEVBQUE7O0FBR3BCO0VBQ0UseUJFbkJ3QixFQUFBOztBRnNCMUI7RUFDRSxtQ0FBMkI7VUFBM0IsMkJBQTJCLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGFzb3VyY2UtbWFuYWdlbWVudC9sb2dzLWRpYWxvZy9sb2dzLWRpYWxvZy5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgJ3NyYy90aGVtZXMvYmFzZS9jb2xvcnMnO1xuXG46aG9zdCB7XG4gIGRpc3BsYXk6IGZsZXg7XG4gIHBhZGRpbmc6IDEwcHg7XG4gIGZsZXgtZGlyZWN0aW9uOiBjb2x1bW47XG4gIGhlaWdodDogOTUlO1xufVxuXG5bbWF0LWRpYWxvZy10aXRsZV0ge1xuICBwYWRkaW5nOiAwIDIwcHggMCAzMHB4O1xuICBib3JkZXItYm90dG9tOiAxcHggc29saWQgJHByaW1hcnktZ3JleS1nMTtcblxuICBoMyB7XG4gICAgY29sb3I6ICRwcmltYXJ5LWJsdWUtYjI7XG4gIH1cbn1cblxuLm1hdC1jYXJkIHtcbiAgbWFyZ2luOiAwO1xuICBwYWRkaW5nOiAyMHB4O1xufVxuXG4ubWF0LWRpYWxvZy1jb250ZW50IHtcbiAgbWFyZ2luOiAwO1xuICBmbGV4LWdyb3c6IDE7XG4gIGRpc3BsYXk6IGZsZXg7XG4gIGZsZXgtZGlyZWN0aW9uOiBjb2x1bW47XG59XG5cbi50aXRsZSB7XG4gIHRleHQtYWxpZ246IGxlZnQ7XG4gIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIxO1xuICBwYWRkaW5nLWxlZnQ6IDEwcHg7XG4gIG1hcmdpbjogMDtcbiAgZm9udC13ZWlnaHQ6IDYwMCAhaW1wb3J0YW50O1xufVxuXG4ubG9nc19fcm91dGUtZGV0YWlscyB7XG4gIC5tYXQtZGl2aWRlci5tYXQtZGl2aWRlci1ob3Jpem9udGFsIHtcbiAgICB3aWR0aDogY2FsYygxMDAlIC0gNDBweCk7XG4gICAgbWFyZ2luLWxlZnQ6IDIwcHg7XG4gIH1cblxuICAmX19jZWxsIHtcbiAgICBmb250LXNpemU6IDE0cHg7XG4gICAgZm9udC13ZWlnaHQ6IG5vcm1hbDtcbiAgICBsaW5lLWhlaWdodDogMztcbiAgICBwYWRkaW5nOiAwIDlweDtcblxuICAgICZfX2xhYmVsIHtcbiAgICAgIGNvbG9yOiAjNzI3MjcyO1xuICAgICAgd2lkdGg6IDEzNnB4O1xuICAgIH1cbiAgfVxufVxuXG46Om5nLWRlZXAgI3JvdXRlTG9nc0dyaWQgLmR4LXJvdyA+IHRyID4gdGQge1xuICBwYWRkaW5nOiAzcHggN3B4O1xufVxuXG46Om5nLWRlZXAgI3JvdXRlTG9nc0dyaWQgLmR4LWZyZWVzcGFjZS1yb3cge1xuICB2aXNpYmlsaXR5OiBoaWRkZW47XG59XG5cbjo6bmctZGVlcCAjcm91dGVMb2dzR3JpZCB0Ym9keTpudGgtY2hpbGQoZXZlbikge1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAkYmFja2dyb3VuZC1jb2xvcjtcbn1cblxuLmNsb3NlLWJ1dHRvbiB7XG4gIHRyYW5zZm9ybTogdHJhbnNsYXRlWSgtNnB4KTtcbn1cbiIsIjpob3N0IHtcbiAgZGlzcGxheTogZmxleDtcbiAgcGFkZGluZzogMTBweDtcbiAgZmxleC1kaXJlY3Rpb246IGNvbHVtbjtcbiAgaGVpZ2h0OiA5NSU7IH1cblxuW21hdC1kaWFsb2ctdGl0bGVdIHtcbiAgcGFkZGluZzogMCAyMHB4IDAgMzBweDtcbiAgYm9yZGVyLWJvdHRvbTogMXB4IHNvbGlkICNkMWQzZDM7IH1cbiAgW21hdC1kaWFsb2ctdGl0bGVdIGgzIHtcbiAgICBjb2xvcjogIzAwNzdiZTsgfVxuXG4ubWF0LWNhcmQge1xuICBtYXJnaW46IDA7XG4gIHBhZGRpbmc6IDIwcHg7IH1cblxuLm1hdC1kaWFsb2ctY29udGVudCB7XG4gIG1hcmdpbjogMDtcbiAgZmxleC1ncm93OiAxO1xuICBkaXNwbGF5OiBmbGV4O1xuICBmbGV4LWRpcmVjdGlvbjogY29sdW1uOyB9XG5cbi50aXRsZSB7XG4gIHRleHQtYWxpZ246IGxlZnQ7XG4gIGNvbG9yOiAjMWE4OWQ0O1xuICBwYWRkaW5nLWxlZnQ6IDEwcHg7XG4gIG1hcmdpbjogMDtcbiAgZm9udC13ZWlnaHQ6IDYwMCAhaW1wb3J0YW50OyB9XG5cbi5sb2dzX19yb3V0ZS1kZXRhaWxzIC5tYXQtZGl2aWRlci5tYXQtZGl2aWRlci1ob3Jpem9udGFsIHtcbiAgd2lkdGg6IGNhbGMoMTAwJSAtIDQwcHgpO1xuICBtYXJnaW4tbGVmdDogMjBweDsgfVxuXG4ubG9nc19fcm91dGUtZGV0YWlsc19fY2VsbCB7XG4gIGZvbnQtc2l6ZTogMTRweDtcbiAgZm9udC13ZWlnaHQ6IG5vcm1hbDtcbiAgbGluZS1oZWlnaHQ6IDM7XG4gIHBhZGRpbmc6IDAgOXB4OyB9XG4gIC5sb2dzX19yb3V0ZS1kZXRhaWxzX19jZWxsX19sYWJlbCB7XG4gICAgY29sb3I6ICM3MjcyNzI7XG4gICAgd2lkdGg6IDEzNnB4OyB9XG5cbjo6bmctZGVlcCAjcm91dGVMb2dzR3JpZCAuZHgtcm93ID4gdHIgPiB0ZCB7XG4gIHBhZGRpbmc6IDNweCA3cHg7IH1cblxuOjpuZy1kZWVwICNyb3V0ZUxvZ3NHcmlkIC5keC1mcmVlc3BhY2Utcm93IHtcbiAgdmlzaWJpbGl0eTogaGlkZGVuOyB9XG5cbjo6bmctZGVlcCAjcm91dGVMb2dzR3JpZCB0Ym9keTpudGgtY2hpbGQoZXZlbikge1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAjZjVmOWZjOyB9XG5cbi5jbG9zZS1idXR0b24ge1xuICB0cmFuc2Zvcm06IHRyYW5zbGF0ZVkoLTZweCk7IH1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/logs-dialog/logs-dialog.component.ts":
/*!*********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/logs-dialog/logs-dialog.component.ts ***!
  \*********************************************************************************************************/
/*! exports provided: LogsDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LogsDialogComponent", function() { return LogsDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _services_datasource_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../services/datasource.service */ "./src/app/modules/workbench/services/datasource.service.ts");
/* harmony import */ var _common_utils_cron2Readable__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../../../common/utils/cron2Readable */ "./src/app/common/utils/cron2Readable.ts");





var LogsDialogComponent = /** @class */ (function () {
    function LogsDialogComponent(_dialogRef, _datasourceService, routeData) {
        this._dialogRef = _dialogRef;
        this._datasourceService = _datasourceService;
        this.routeData = routeData;
    }
    LogsDialogComponent.prototype.ngOnInit = function () {
        var _this = this;
        var _a = this.routeData, bisChannelSysId = _a.bisChannelSysId, bisRouteSysId = _a.bisRouteSysId;
        this._datasourceService
            .getRoutesLogs(bisChannelSysId, bisRouteSysId)
            .subscribe(function (resp) {
            _this.logs = resp.logs;
            _this.lastFireTime = resp.lastFireTime;
            _this.nextFireTime = resp.nextFireTime;
        });
    };
    LogsDialogComponent.prototype.close = function () {
        this._dialogRef.close();
    };
    LogsDialogComponent.prototype.getSchedule = function (schedulerExpression) {
        var cronexp = schedulerExpression.cronexp, activeTab = schedulerExpression.activeTab, timezone = schedulerExpression.timezone;
        return Object(_common_utils_cron2Readable__WEBPACK_IMPORTED_MODULE_4__["generateSchedule"])(cronexp, activeTab, timezone);
    };
    /**
     * Returns height of logs grid calculated from container.
     * This is necessary because in safari, the implementation of CSS's calc
     * is buggy.
     *
     * @returns {number}
     * @memberof LogsDialogComponent
     */
    LogsDialogComponent.prototype.customGridHeight = function () {
        var container = document.getElementById('route-log__dialog-content-container');
        return container.scrollHeight - container.children[0].scrollHeight;
    };
    LogsDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'logs-dialog',
            template: __webpack_require__(/*! ./logs-dialog.component.html */ "./src/app/modules/workbench/components/datasource-management/logs-dialog/logs-dialog.component.html"),
            styles: [__webpack_require__(/*! ./logs-dialog.component.scss */ "./src/app/modules/workbench/components/datasource-management/logs-dialog/logs-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](2, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"],
            _services_datasource_service__WEBPACK_IMPORTED_MODULE_3__["DatasourceService"], Object])
    ], LogsDialogComponent);
    return LogsDialogComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/select-folder-dialog/index.ts":
/*!**************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/select-folder-dialog/index.ts ***!
  \**************************************************************************************************/
/*! exports provided: SourceFolderDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _select_folder_dialog_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./select-folder-dialog.component */ "./src/app/modules/workbench/components/datasource-management/select-folder-dialog/select-folder-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SourceFolderDialogComponent", function() { return _select_folder_dialog_component__WEBPACK_IMPORTED_MODULE_0__["SourceFolderDialogComponent"]; });




/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/select-folder-dialog/select-folder-dialog.component.html":
/*!*****************************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/select-folder-dialog/select-folder-dialog.component.html ***!
  \*****************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<h1 mat-dialog-title i18n>Select fodler</h1>\n<mat-divider></mat-divider>\n<div mat-dialog-content style=\"height: 250px;\">\n  <remote-folder-selector (selectionChange)=\"onFolderSelected($event)\"\n                          [fileSystemAPI]=\"fileSystemAPI\"\n                          [rootNode]=\"rootNode\"\n  >\n  </remote-folder-selector>\n</div>\n<div mat-dialog-actions>\n  <button mat-raised-button i18n\n          color=\"primary\"\n          (click)=\"onNoClick()\"\n  >\n    Cancel\n  </button>\n  <div fxFlex></div>\n  <button mat-raised-button cdkFocusInitial i18n\n          color=\"warn\"\n          (click)=\"onYesClick()\"\n          [disabled]=\"!selectedPath\"\n  >\n    Select\n  </button>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/select-folder-dialog/select-folder-dialog.component.ts":
/*!***************************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/select-folder-dialog/select-folder-dialog.component.ts ***!
  \***************************************************************************************************************************/
/*! exports provided: SourceFolderDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SourceFolderDialogComponent", function() { return SourceFolderDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var lodash_startsWith__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/startsWith */ "./node_modules/lodash/startsWith.js");
/* harmony import */ var lodash_startsWith__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_startsWith__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var lodash_replace__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/replace */ "./node_modules/lodash/replace.js");
/* harmony import */ var lodash_replace__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_replace__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var _wb_comp_configs__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../wb-comp-configs */ "./src/app/modules/workbench/wb-comp-configs.ts");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");







var SourceFolderDialogComponent = /** @class */ (function () {
    function SourceFolderDialogComponent(dialogRef, workBench) {
        this.dialogRef = dialogRef;
        this.workBench = workBench;
        this.selectedPath = '';
        this.rootNode = _wb_comp_configs__WEBPACK_IMPORTED_MODULE_5__["STAGING_TREE"];
        this.fileSystemAPI = {
            getDir: this.workBench.getStagingData,
            createDir: this.workBench.createFolder
        };
    }
    SourceFolderDialogComponent.prototype.onFolderSelected = function (_a) {
        var folder = _a.folder;
        switch (folder.path) {
            case 'root':
                this.selectedPath = "/";
                break;
            case '/':
                this.selectedPath = "/" + folder.name;
                break;
            default:
                this.selectedPath = folder.path + "/" + folder.name;
                break;
        }
    };
    SourceFolderDialogComponent.prototype.onNoClick = function () {
        this.dialogRef.close();
    };
    SourceFolderDialogComponent.prototype.onYesClick = function () {
        var result = lodash_startsWith__WEBPACK_IMPORTED_MODULE_3__(this.selectedPath, '//') ?
            lodash_replace__WEBPACK_IMPORTED_MODULE_4__(this.selectedPath, '/', '') : this.selectedPath;
        this.dialogRef.close(result);
    };
    SourceFolderDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'select-folder-dialog',
            template: __webpack_require__(/*! ./select-folder-dialog.component.html */ "./src/app/modules/workbench/components/datasource-management/select-folder-dialog/select-folder-dialog.component.html")
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"],
            _services_workbench_service__WEBPACK_IMPORTED_MODULE_6__["WorkbenchService"]])
    ], SourceFolderDialogComponent);
    return SourceFolderDialogComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/test-connectivity/test-connectivity.component.html":
/*!***********************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/test-connectivity/test-connectivity.component.html ***!
  \***********************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div fxFlex fxLayout=\"column\" class=\"container test-connectivity\">\n    <div class=\"console-header\" fxFlex fxLayout=\"row\" fxLayoutAlign=\"center center\">\n        <div e2e=\"test-connectivity\">Connection Logs</div>\n        <span fxFlex></span>\n        <mat-icon e2e=\"close-test-connectivity\" fontIcon=\"icon-remove\" color=\"warn\" style=\"height: 15px;\" (click)=\"close()\"></mat-icon>\n    </div>\n    <div class=\"console-body\">\n        <div id=\"screen\" #screen><b id=\"prompt\" class=\"idle\"> &#9595; </b></div>\n    </div>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/test-connectivity/test-connectivity.component.scss":
/*!***********************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/test-connectivity/test-connectivity.component.scss ***!
  \***********************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".test-connectivity {\n  display: block !important;\n  color: greenyellow; }\n  .test-connectivity .console-header {\n    height: 30px;\n    background-color: #3b3b3b;\n    padding: 0 3px 0 12px;\n    color: #d7d7d7;\n    border-radius: 7px 7px 0 0; }\n  .test-connectivity .console-body {\n    padding: 0 9px; }\n  .test-connectivity #prompt {\n    font-style: unset;\n    font-size: 1em; }\n  .test-connectivity #prompt.idle {\n    animation: blink 1100ms linear infinite;\n    -webkit-animation: blink 1100ms linear infinite; }\n  @keyframes blink {\n  49% {\n    opacity: 1; }\n  50% {\n    opacity: 0; }\n  89% {\n    opacity: 0; }\n  90% {\n    opacity: 1; } }\n  @-webkit-keyframes blink {\n  49% {\n    opacity: 1; }\n  50% {\n    opacity: 0; }\n  89% {\n    opacity: 0; }\n  90% {\n    opacity: 1; } }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL2RhdGFzb3VyY2UtbWFuYWdlbWVudC90ZXN0LWNvbm5lY3Rpdml0eS90ZXN0LWNvbm5lY3Rpdml0eS5jb21wb25lbnQuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUNFLHlCQUF5QjtFQUN6QixrQkFBa0IsRUFBQTtFQUZwQjtJQUtJLFlBQVk7SUFDWix5QkFBeUI7SUFDekIscUJBQXFCO0lBQ3JCLGNBQWM7SUFDZCwwQkFBMEIsRUFBQTtFQVQ5QjtJQWFJLGNBQWMsRUFBQTtFQWJsQjtJQWlCSSxpQkFBaUI7SUFDakIsY0FBYyxFQUFBO0VBbEJsQjtJQXNCSSx1Q0FBdUM7SUFDdkMsK0NBQStDLEVBQUE7RUFHakQ7RUFDRTtJQUNFLFVBQVUsRUFBQTtFQUdaO0lBQ0UsVUFBVSxFQUFBO0VBR1o7SUFDRSxVQUFVLEVBQUE7RUFHWjtJQUNFLFVBQVUsRUFBQSxFQUFBO0VBSWQ7RUFDRTtJQUNFLFVBQVUsRUFBQTtFQUdaO0lBQ0UsVUFBVSxFQUFBO0VBR1o7SUFDRSxVQUFVLEVBQUE7RUFHWjtJQUNFLFVBQVUsRUFBQSxFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy93b3JrYmVuY2gvY29tcG9uZW50cy9kYXRhc291cmNlLW1hbmFnZW1lbnQvdGVzdC1jb25uZWN0aXZpdHkvdGVzdC1jb25uZWN0aXZpdHkuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0ICdzcmMvdGhlbWVzL2Jhc2UvY29sb3JzJztcblxuLnRlc3QtY29ubmVjdGl2aXR5IHtcbiAgZGlzcGxheTogYmxvY2sgIWltcG9ydGFudDtcbiAgY29sb3I6IGdyZWVueWVsbG93O1xuXG4gIC5jb25zb2xlLWhlYWRlciB7XG4gICAgaGVpZ2h0OiAzMHB4O1xuICAgIGJhY2tncm91bmQtY29sb3I6ICMzYjNiM2I7XG4gICAgcGFkZGluZzogMCAzcHggMCAxMnB4O1xuICAgIGNvbG9yOiAjZDdkN2Q3O1xuICAgIGJvcmRlci1yYWRpdXM6IDdweCA3cHggMCAwO1xuICB9XG5cbiAgLmNvbnNvbGUtYm9keSB7XG4gICAgcGFkZGluZzogMCA5cHg7XG4gIH1cblxuICAjcHJvbXB0IHtcbiAgICBmb250LXN0eWxlOiB1bnNldDtcbiAgICBmb250LXNpemU6IDFlbTtcbiAgfVxuXG4gICNwcm9tcHQuaWRsZSB7XG4gICAgYW5pbWF0aW9uOiBibGluayAxMTAwbXMgbGluZWFyIGluZmluaXRlO1xuICAgIC13ZWJraXQtYW5pbWF0aW9uOiBibGluayAxMTAwbXMgbGluZWFyIGluZmluaXRlO1xuICB9XG5cbiAgQGtleWZyYW1lcyBibGluayB7XG4gICAgNDklIHtcbiAgICAgIG9wYWNpdHk6IDE7XG4gICAgfVxuXG4gICAgNTAlIHtcbiAgICAgIG9wYWNpdHk6IDA7XG4gICAgfVxuXG4gICAgODklIHtcbiAgICAgIG9wYWNpdHk6IDA7XG4gICAgfVxuXG4gICAgOTAlIHtcbiAgICAgIG9wYWNpdHk6IDE7XG4gICAgfVxuICB9XG5cbiAgQC13ZWJraXQta2V5ZnJhbWVzIGJsaW5rIHtcbiAgICA0OSUge1xuICAgICAgb3BhY2l0eTogMTtcbiAgICB9XG5cbiAgICA1MCUge1xuICAgICAgb3BhY2l0eTogMDtcbiAgICB9XG5cbiAgICA4OSUge1xuICAgICAgb3BhY2l0eTogMDtcbiAgICB9XG5cbiAgICA5MCUge1xuICAgICAgb3BhY2l0eTogMTtcbiAgICB9XG4gIH1cbn1cbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/workbench/components/datasource-management/test-connectivity/test-connectivity.component.ts":
/*!*********************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/datasource-management/test-connectivity/test-connectivity.component.ts ***!
  \*********************************************************************************************************************/
/*! exports provided: TestConnectivityComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "TestConnectivityComponent", function() { return TestConnectivityComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");



var TestConnectivityComponent = /** @class */ (function () {
    // private text = [
    //   'Last login: Fri Sep 28 17:29:06 2018 from 192.168.1.74',
    //   '** Authorized Use Only **',
    //   'Chef-Client: saw01.ana.demo.vaste.sncrcorp.net',
    //   'Node platform: centos 7.4.1708 (rhel)',
    //   'Node arch: x86_64',
    //   'Hostnawme: saw01 (saw01.ana.demo.vaste.sncrcorp.net)',
    //   'Chef Server: https://chef02-itopscorpvaste.sncrcorp.net:443/organizations/sncr-sip',
    //   'Environment: sip-demo',
    //   'Last Run: 2018-10-02 17:03:26 +0000'
    // ];
    function TestConnectivityComponent(snackBarRef, logData) {
        this.snackBarRef = snackBarRef;
        this.logData = logData;
    }
    TestConnectivityComponent.prototype.ngOnInit = function () {
        this.typeMessage(this.logData, this.screen.nativeElement);
    };
    TestConnectivityComponent.prototype.close = function () {
        this.snackBarRef.dismiss();
    };
    /**
     * Credits: https://codereview.stackexchange.com/a/97114
     *
     * @param {*} text
     * @param {*} screen
     * @returns
     * @memberof TestConnectivityComponent
     */
    TestConnectivityComponent.prototype.typeMessage = function (text, screen) {
        // You have to check for lines and if the screen is an element
        if (!text || !text.length || !(screen instanceof Element)) {
            return;
        }
        // if it is not a string, you will want to make it into one
        if ('string' !== typeof text) {
            text = text.join('\n');
        }
        // normalize newlines, and split it to have a nice array
        text = text.replace(/\r\n?/g, '\n').split('');
        // the prompt is always the last child
        var prompt = screen.lastChild;
        prompt.className = 'typing';
        var typer = function () {
            var character = text.shift();
            screen.insertBefore(
            // newlines must be written as a `<br>`
            character === '\n'
                ? document.createElement('br')
                : document.createTextNode(character), prompt);
            // only run this again if there are letters
            if (text.length) {
                setTimeout(typer, 10);
            }
            else {
                prompt.className = 'idle';
            }
        };
        setTimeout(typer, 10);
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ViewChild"])('screen'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], TestConnectivityComponent.prototype, "screen", void 0);
    TestConnectivityComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'test-connectivity',
            template: __webpack_require__(/*! ./test-connectivity.component.html */ "./src/app/modules/workbench/components/datasource-management/test-connectivity/test-connectivity.component.html"),
            styles: [__webpack_require__(/*! ./test-connectivity.component.scss */ "./src/app/modules/workbench/components/datasource-management/test-connectivity/test-connectivity.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](1, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_SNACK_BAR_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_material__WEBPACK_IMPORTED_MODULE_2__["MatSnackBarRef"], Object])
    ], TestConnectivityComponent);
    return TestConnectivityComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/semantic-management/create/create-semantic.component.html":
/*!********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/semantic-management/create/create-semantic.component.html ***!
  \********************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"container\" (window:resize)=\"onResize($event)\">\n  <div\n    class=\"header\"\n    fxLayout=\"row\"\n    fxLayoutAlign=\"center center\"\n    fxLayoutGap=\"10px\"\n  >\n    <button mat-button class=\"bck-btn\">\n      <mat-icon fontIcon=\"icon-arrow-left\" (click)=\"backToDS()\"></mat-icon>\n    </button>\n    <div fxFlex>Create Datapod</div>\n    <span fxFlex>Select Dataset/s to Create</span>\n    <!-- Commenting out creation of multiple Artifacts as there is issue with report. -->\n\n    <!-- <mat-slide-toggle\n      (change)=\"joinEligibleToggled()\"\n      [checked]=\"joinToggleValue\"\n      [(ngModel)]=\"joinToggleValue\"\n      labelPosition=\"before\"\n    >\n      Show Join Eligible\n    </mat-slide-toggle> -->\n    <button\n      mat-raised-button\n      color=\"primary\"\n      [disabled]=\"!isSelected\"\n      (click)=\"gotoValidate()\"\n    >\n      <span i18n>VALIDATE</span>\n    </button>\n  </div>\n  <div\n    class=\"body\"\n    fxLayout=\"row\"\n    fxLayoutAlign=\"center stretch\"\n    fxLayoutGap=\"10px\"\n  >\n    <mat-card fxFlex=\"25\">\n      <mat-card-content style=\"overflow:hidden;\">\n        <dx-data-grid\n          #dsGrid\n          fxFlex\n          [dataSource]=\"gridDataAvailableDS\"\n          [rowAlternationEnabled]=\"true\"\n          [height]=\"contentHeight\"\n          [width]=\"'100%'\"\n          [showBorders]=\"false\"\n          [showColumnLines]=\"false\"\n          [hoverStateEnabled]=\"true\"\n          (onSelectionChanged)=\"onDSSelectionChanged($event)\"\n        >\n          <dxo-selection\n            selectAllMode=\"allMode\"\n            showCheckBoxesMode=\"always\"\n            [mode]=\"selectionMode\"\n          ></dxo-selection>\n          <dxi-column\n            caption=\"Dataset Name\"\n            dataField=\"system.name\"\n          ></dxi-column>\n          <dxi-column\n            caption=\"Created By\"\n            dataField=\"system.createdBy\"\n          ></dxi-column>\n          <dxi-column\n            caption=\"Updated Time\"\n            dataField=\"system.modifiedTime\"\n            dataType=\"date\"\n            cellTemplate=\"timeTemplate\"\n          ></dxi-column>\n          <dxo-scrolling\n            mode=\"virtual\"\n            showScrollbar=\"always\"\n            [useNative]=\"false\"\n          ></dxo-scrolling>\n          <dxo-filter-row [visible]=\"true\" applyFilter=\"auto\"></dxo-filter-row>\n          <div *dxTemplate=\"let data of 'timeTemplate'\">\n            <span> {{ data.value * 1000 | date: 'short' }}</span>\n          </div>\n        </dx-data-grid>\n      </mat-card-content>\n    </mat-card>\n    <mat-card fxFlex=\"75\">\n      <mat-card-content fxLayout=\"row\" fxLayoutAlign=\"center stretch\">\n        <div\n          class=\"results\"\n          fxLayout=\"row\"\n          fxLayoutAlign=\"center center\"\n          *ngIf=\"!selectedDSData?.length\"\n        >\n          <span>Select Dataset/s from left pane to view schema</span>\n        </div>\n        <mat-tab-group\n          fxFlex\n          *ngIf=\"selectedDSData.length\"\n          dynamicHeight=\"false\"\n          [selectedIndex]=\"selectedDSData?.length\"\n        >\n          <mat-tab\n            *ngFor=\"let ds of selectedDSData; let index = index\"\n            [label]=\"ds?.system?.name\"\n          >\n            <dx-data-grid\n              [dataSource]=\"ds.schema?.fields\"\n              [showBorders]=\"true\"\n              [height]=\"'100%'\"\n              [width]=\"'100%'\"\n              [rowAlternationEnabled]=\"true\"\n              [showColumnLines]=\"false\"\n              style=\"position:absolute;top:0;bottom:0;left:0;bottom:0;\"\n            >\n              <dxi-column\n                caption=\"No\"\n                [width]=\"50\"\n                cellTemplate=\"NoTemplate\"\n              ></dxi-column>\n              <dxi-column caption=\"Field Name\" dataField=\"name\"></dxi-column>\n              <dxi-column caption=\"Data Type\" dataField=\"type\"></dxi-column>\n              <dxo-scrolling\n                mode=\"virtual\"\n                showScrollbar=\"always\"\n                [useNative]=\"false\"\n              ></dxo-scrolling>\n              <dxo-filter-row\n                [visible]=\"true\"\n                applyFilter=\"auto\"\n              ></dxo-filter-row>\n              <dxo-header-filter [visible]=\"false\"></dxo-header-filter>\n              <div *dxTemplate=\"let data of 'NoTemplate'\">\n                <span> {{ data.rowIndex + 1 }}</span>\n              </div>\n            </dx-data-grid>\n          </mat-tab>\n        </mat-tab-group>\n      </mat-card-content>\n    </mat-card>\n  </div>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/semantic-management/create/create-semantic.component.scss":
/*!********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/semantic-management/create/create-semantic.component.scss ***!
  \********************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%; }\n\n.container {\n  width: 100%;\n  height: 100%;\n  background-color: #f5f9fc;\n  overflow: auto; }\n\n.container .header {\n    color: rgba(0, 0, 0, 0.54);\n    background: rgba(0, 0, 0, 0.03);\n    padding: 8px 0;\n    height: 50px;\n    font-size: 18px;\n    font-weight: 700;\n    border: 2px solid white; }\n\n.container .header .mat-icon {\n      font-size: 26px; }\n\n.container .header > span {\n      font-size: 14px;\n      font-weight: normal; }\n\n.container .body {\n    background-color: white;\n    height: calc(100% - 54px);\n    max-height: calc(100% - 54px) !important; }\n\n.container .body mat-card {\n      padding: 0;\n      height: 100%; }\n\n.container .body mat-card .mat-card-content {\n        height: 100%;\n        max-height: 100%;\n        overflow: auto; }\n\n.container .body mat-card .mat-card-content .mat-tab-group {\n          height: 100% !important;\n          max-height: 100% !important;\n          overflow: auto; }\n\n.container .body mat-card .mat-card-content ::ng-deep .mat-tab-label-active {\n          color: #0077be; }\n\n.container .body mat-card .mat-card-content ::ng-deep .mat-tab-body-wrapper {\n          height: 100%; }\n\n.container .body mat-card .mat-card-content ::ng-deep .mat-tab-body-wrapper .mat-tab-body {\n            overflow: hidden !important; }\n\n.container .body mat-card .mat-card-content ::ng-deep .mat-tab-body-wrapper .mat-tab-body .mat-tab-body-content {\n              overflow: hidden !important; }\n\n.container .body mat-card .mat-card-content .results {\n          width: 100%;\n          height: 100%;\n          color: #bdbdbd;\n          text-align: center; }\n\n.container .body mat-card .mat-card-content .results span {\n            font-size: 28px;\n            word-break: break-word; }\n\n.container .body mat-card .mat-card-content .dx-datagrid-content .dx-datagrid-table .dx-row .dx-command-select {\n          padding: 0;\n          width: 40px;\n          min-width: 40px; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL3NlbWFudGljLW1hbmFnZW1lbnQvY3JlYXRlL2NyZWF0ZS1zZW1hbnRpYy5jb21wb25lbnQuc2NzcyIsIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL3RoZW1lcy9iYXNlL19jb2xvcnMuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUNFLFdBQVcsRUFBQTs7QUFHYjtFQUNFLFdBQVc7RUFDWCxZQUFZO0VBQ1oseUJDc0N3QjtFRHJDeEIsY0FBYyxFQUFBOztBQUpoQjtJQU9JLDBCQUEwQjtJQUMxQiwrQkFBK0I7SUFDL0IsY0FBYztJQUNkLFlBQVk7SUFDWixlQUFlO0lBQ2YsZ0JBQWdCO0lBQ2hCLHVCQUF1QixFQUFBOztBQWIzQjtNQWdCTSxlQUFlLEVBQUE7O0FBaEJyQjtNQW9CTSxlQUFlO01BQ2YsbUJBQW1CLEVBQUE7O0FBckJ6QjtJQTBCSSx1QkFBdUI7SUFDdkIseUJBQXlCO0lBQ3pCLHdDQUF3QyxFQUFBOztBQTVCNUM7TUErQk0sVUFBVTtNQUNWLFlBQVksRUFBQTs7QUFoQ2xCO1FBbUNRLFlBQVk7UUFDWixnQkFBZ0I7UUFDaEIsY0FBYyxFQUFBOztBQXJDdEI7VUF3Q1UsdUJBQXVCO1VBQ3ZCLDJCQUEyQjtVQUMzQixjQUFjLEVBQUE7O0FBMUN4QjtVQThDVSxjQ2xEZSxFQUFBOztBREl6QjtVQWtEVSxZQUFZLEVBQUE7O0FBbER0QjtZQXFEWSwyQkFBMkIsRUFBQTs7QUFyRHZDO2NBd0RjLDJCQUEyQixFQUFBOztBQXhEekM7VUE4RFUsV0FBVztVQUNYLFlBQVk7VUFDWixjQUF5QjtVQUN6QixrQkFBa0IsRUFBQTs7QUFqRTVCO1lBb0VZLGVBQWU7WUFDZixzQkFBc0IsRUFBQTs7QUFyRWxDO1VBMEVVLFVBQVU7VUFDVixXQUFXO1VBQ1gsZUFBZSxFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy93b3JrYmVuY2gvY29tcG9uZW50cy9zZW1hbnRpYy1tYW5hZ2VtZW50L2NyZWF0ZS9jcmVhdGUtc2VtYW50aWMuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0ICdzcmMvdGhlbWVzL2Jhc2UvY29sb3JzJztcblxuOmhvc3Qge1xuICB3aWR0aDogMTAwJTtcbn1cblxuLmNvbnRhaW5lciB7XG4gIHdpZHRoOiAxMDAlO1xuICBoZWlnaHQ6IDEwMCU7XG4gIGJhY2tncm91bmQtY29sb3I6ICRiYWNrZ3JvdW5kLWNvbG9yO1xuICBvdmVyZmxvdzogYXV0bztcblxuICAuaGVhZGVyIHtcbiAgICBjb2xvcjogcmdiYSgwLCAwLCAwLCAwLjU0KTtcbiAgICBiYWNrZ3JvdW5kOiByZ2JhKDAsIDAsIDAsIDAuMDMpO1xuICAgIHBhZGRpbmc6IDhweCAwO1xuICAgIGhlaWdodDogNTBweDtcbiAgICBmb250LXNpemU6IDE4cHg7XG4gICAgZm9udC13ZWlnaHQ6IDcwMDtcbiAgICBib3JkZXI6IDJweCBzb2xpZCB3aGl0ZTtcblxuICAgIC5tYXQtaWNvbiB7XG4gICAgICBmb250LXNpemU6IDI2cHg7XG4gICAgfVxuXG4gICAgJiA+IHNwYW4ge1xuICAgICAgZm9udC1zaXplOiAxNHB4O1xuICAgICAgZm9udC13ZWlnaHQ6IG5vcm1hbDtcbiAgICB9XG4gIH1cblxuICAuYm9keSB7XG4gICAgYmFja2dyb3VuZC1jb2xvcjogd2hpdGU7XG4gICAgaGVpZ2h0OiBjYWxjKDEwMCUgLSA1NHB4KTtcbiAgICBtYXgtaGVpZ2h0OiBjYWxjKDEwMCUgLSA1NHB4KSAhaW1wb3J0YW50O1xuXG4gICAgbWF0LWNhcmQge1xuICAgICAgcGFkZGluZzogMDtcbiAgICAgIGhlaWdodDogMTAwJTtcblxuICAgICAgLm1hdC1jYXJkLWNvbnRlbnQge1xuICAgICAgICBoZWlnaHQ6IDEwMCU7XG4gICAgICAgIG1heC1oZWlnaHQ6IDEwMCU7XG4gICAgICAgIG92ZXJmbG93OiBhdXRvO1xuXG4gICAgICAgIC5tYXQtdGFiLWdyb3VwIHtcbiAgICAgICAgICBoZWlnaHQ6IDEwMCUgIWltcG9ydGFudDtcbiAgICAgICAgICBtYXgtaGVpZ2h0OiAxMDAlICFpbXBvcnRhbnQ7XG4gICAgICAgICAgb3ZlcmZsb3c6IGF1dG87XG4gICAgICAgIH1cblxuICAgICAgICA6Om5nLWRlZXAgLm1hdC10YWItbGFiZWwtYWN0aXZlIHtcbiAgICAgICAgICBjb2xvcjogJHByaW1hcnktYmx1ZS1iMjtcbiAgICAgICAgfVxuXG4gICAgICAgIDo6bmctZGVlcCAubWF0LXRhYi1ib2R5LXdyYXBwZXIge1xuICAgICAgICAgIGhlaWdodDogMTAwJTtcblxuICAgICAgICAgIC5tYXQtdGFiLWJvZHkge1xuICAgICAgICAgICAgb3ZlcmZsb3c6IGhpZGRlbiAhaW1wb3J0YW50O1xuXG4gICAgICAgICAgICAubWF0LXRhYi1ib2R5LWNvbnRlbnQge1xuICAgICAgICAgICAgICBvdmVyZmxvdzogaGlkZGVuICFpbXBvcnRhbnQ7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgfVxuICAgICAgICB9XG5cbiAgICAgICAgLnJlc3VsdHMge1xuICAgICAgICAgIHdpZHRoOiAxMDAlO1xuICAgICAgICAgIGhlaWdodDogMTAwJTtcbiAgICAgICAgICBjb2xvcjogcmdiKDE4OSwgMTg5LCAxODkpO1xuICAgICAgICAgIHRleHQtYWxpZ246IGNlbnRlcjtcblxuICAgICAgICAgIHNwYW4ge1xuICAgICAgICAgICAgZm9udC1zaXplOiAyOHB4O1xuICAgICAgICAgICAgd29yZC1icmVhazogYnJlYWstd29yZDtcbiAgICAgICAgICB9XG4gICAgICAgIH1cblxuICAgICAgICAuZHgtZGF0YWdyaWQtY29udGVudCAuZHgtZGF0YWdyaWQtdGFibGUgLmR4LXJvdyAuZHgtY29tbWFuZC1zZWxlY3Qge1xuICAgICAgICAgIHBhZGRpbmc6IDA7XG4gICAgICAgICAgd2lkdGg6IDQwcHg7XG4gICAgICAgICAgbWluLXdpZHRoOiA0MHB4O1xuICAgICAgICB9XG4gICAgICB9XG4gICAgfVxuICB9XG59XG4iLCIvLyBCcmFuZGluZyBjb2xvcnNcbiRwcmltYXJ5LWJsdWUtYjE6ICMxYTg5ZDQ7XG4kcHJpbWFyeS1ibHVlLWIyOiAjMDA3N2JlO1xuJHByaW1hcnktYmx1ZS1iMzogIzIwNmJjZTtcbiRwcmltYXJ5LWJsdWUtYjQ6ICMxZDNhYjI7XG5cbiRwcmltYXJ5LWhvdmVyLWJsdWU6ICMxZDYxYjE7XG4kZ3JpZC1ob3Zlci1jb2xvcjogI2Y1ZjlmYztcbiRncmlkLWhlYWRlci1iZy1jb2xvcjogI2Q3ZWFmYTtcbiRncmlkLWhlYWRlci1jb2xvcjogIzBiNGQ5OTtcbiRncmlkLXRleHQtY29sb3I6ICM0NjQ2NDY7XG4kZ3JleS10ZXh0LWNvbG9yOiAjNjM2MzYzO1xuXG4kc2VsZWN0aW9uLWhpZ2hsaWdodC1jb2w6IHJnYmEoMCwgMTQwLCAyNjAsIDAuMik7XG4kcHJpbWFyeS1ncmV5LWcxOiAjZDFkM2QzO1xuJHByaW1hcnktZ3JleS1nMjogIzk5OTtcbiRwcmltYXJ5LWdyZXktZzM6ICM3MzczNzM7XG4kcHJpbWFyeS1ncmV5LWc0OiAjNWM2NjcwO1xuJHByaW1hcnktZ3JleS1nNTogIzMxMzEzMTtcbiRwcmltYXJ5LWdyZXktZzY6ICNmNWY1ZjU7XG4kcHJpbWFyeS1ncmV5LWc3OiAjM2QzZDNkO1xuXG4kcHJpbWFyeS13aGl0ZTogI2ZmZjtcbiRwcmltYXJ5LWJsYWNrOiAjMDAwO1xuJHByaW1hcnktcmVkOiAjYWIwZTI3O1xuJHByaW1hcnktZ3JlZW46ICM3M2I0MjE7XG4kcHJpbWFyeS1vcmFuZ2U6ICNmMDc2MDE7XG5cbiRzZWNvbmRhcnktZ3JlZW46ICM2ZmIzMjA7XG4kc2Vjb25kYXJ5LXllbGxvdzogI2ZmYmUwMDtcbiRzZWNvbmRhcnktb3JhbmdlOiAjZmY5MDAwO1xuJHNlY29uZGFyeS1yZWQ6ICNkOTNlMDA7XG4kc2Vjb25kYXJ5LWJlcnJ5OiAjYWMxNDVhO1xuJHNlY29uZGFyeS1wdXJwbGU6ICM5MTQxOTE7XG5cbiRzdHJpbmctdHlwZS1jb2xvcjogIzQ5OTViMjtcbiRudW1iZXItdHlwZS1jb2xvcjogIzAwYjE4MDtcbiRnZW8tdHlwZS1jb2xvcjogIzg0NWVjMjtcbiRkYXRlLXR5cGUtY29sb3I6ICNkMTk2MjE7XG5cbiR0eXBlLWNoaXAtb3BhY2l0eTogMTtcbiRzdHJpbmctdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRzdHJpbmctdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRudW1iZXItdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRudW1iZXItdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRnZW8tdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRnZW8tdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRkYXRlLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZGF0ZS10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuXG4kcmVwb3J0LWRlc2lnbmVyLXNldHRpbmdzLWJnLWNvbG9yOiAjZjVmOWZjO1xuJGJhY2tncm91bmQtY29sb3I6ICNmNWY5ZmM7XG4iXX0= */"

/***/ }),

/***/ "./src/app/modules/workbench/components/semantic-management/create/create-semantic.component.ts":
/*!******************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/semantic-management/create/create-semantic.component.ts ***!
  \******************************************************************************************************/
/*! exports provided: CreateSemanticComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CreateSemanticComponent", function() { return CreateSemanticComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! devextreme-angular/ui/data-grid */ "./node_modules/devextreme-angular/ui/data-grid.js");
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/filter */ "./node_modules/lodash/filter.js");
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_filter__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/cloneDeep */ "./node_modules/lodash/cloneDeep.js");
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_6__);







var CreateSemanticComponent = /** @class */ (function () {
    function CreateSemanticComponent(router, workBench) {
        this.router = router;
        this.workBench = workBench;
        this.joinToggleValue = false;
        this.selectionMode = 'single';
        this.isSelected = false;
        this.selectedDSData = [];
    }
    CreateSemanticComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.workBench.getDatasets().subscribe(function (data) {
            _this.availableDS = data;
            _this.gridDataAvailableDS = lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_6__(data);
        });
        setTimeout(function () {
            _this.contentHeight = window.innerHeight - 175;
        });
    };
    CreateSemanticComponent.prototype.backToDS = function () {
        this.router.navigate(['workbench', 'dataobjects']);
    };
    /**
     * Only Datalake datasets are join eligible as of now.
     *
     * @memberof CreateSemanticComponent
     */
    CreateSemanticComponent.prototype.joinEligibleToggled = function () {
        if (this.joinToggleValue) {
            this.selectionMode = 'multiple';
            this.gridDataAvailableDS = lodash_filter__WEBPACK_IMPORTED_MODULE_5__(this.availableDS, 'joinEligible');
        }
        else {
            this.selectionMode = 'single';
            this.gridDataAvailableDS = this.availableDS;
        }
        this.dataGrid.instance.clearSelection();
        this.isSelected = false;
        this.selectedDSData = [];
    };
    CreateSemanticComponent.prototype.onDSSelectionChanged = function (event) {
        if (event.selectedRowsData.length >= 1) {
            this.isSelected = true;
            this.selectedDSData = event.selectedRowsData;
        }
        else {
            this.isSelected = false;
            this.selectedDSData = [];
        }
    };
    CreateSemanticComponent.prototype.gotoValidate = function () {
        this.workBench.setDataToLS('selectedDS', this.selectedDSData);
        this.router.navigate(['workbench', 'semantic', 'validate']);
    };
    CreateSemanticComponent.prototype.onResize = function (event) {
        this.contentHeight = event.target.innerHeight - 165;
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ViewChild"])('dsGrid'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3__["DxDataGridComponent"])
    ], CreateSemanticComponent.prototype, "dataGrid", void 0);
    CreateSemanticComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'create-semantic',
            template: __webpack_require__(/*! ./create-semantic.component.html */ "./src/app/modules/workbench/components/semantic-management/create/create-semantic.component.html"),
            styles: [__webpack_require__(/*! ./create-semantic.component.scss */ "./src/app/modules/workbench/components/semantic-management/create/create-semantic.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"], _services_workbench_service__WEBPACK_IMPORTED_MODULE_4__["WorkbenchService"]])
    ], CreateSemanticComponent);
    return CreateSemanticComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/semantic-management/index.ts":
/*!***************************************************************************!*\
  !*** ./src/app/modules/workbench/components/semantic-management/index.ts ***!
  \***************************************************************************/
/*! exports provided: CreateSemanticComponent, SemanticDetailsDialogComponent, UpdateSemanticComponent, ValidateSemanticComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _create_create_semantic_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./create/create-semantic.component */ "./src/app/modules/workbench/components/semantic-management/create/create-semantic.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "CreateSemanticComponent", function() { return _create_create_semantic_component__WEBPACK_IMPORTED_MODULE_0__["CreateSemanticComponent"]; });

/* harmony import */ var _semantic_details_dialog_semantic_details_dialog_component__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./semantic-details-dialog/semantic-details-dialog.component */ "./src/app/modules/workbench/components/semantic-management/semantic-details-dialog/semantic-details-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "SemanticDetailsDialogComponent", function() { return _semantic_details_dialog_semantic_details_dialog_component__WEBPACK_IMPORTED_MODULE_1__["SemanticDetailsDialogComponent"]; });

/* harmony import */ var _update_update_semantic_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./update/update-semantic.component */ "./src/app/modules/workbench/components/semantic-management/update/update-semantic.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "UpdateSemanticComponent", function() { return _update_update_semantic_component__WEBPACK_IMPORTED_MODULE_2__["UpdateSemanticComponent"]; });

/* harmony import */ var _validate_validate_semantic_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./validate/validate-semantic.component */ "./src/app/modules/workbench/components/semantic-management/validate/validate-semantic.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ValidateSemanticComponent", function() { return _validate_validate_semantic_component__WEBPACK_IMPORTED_MODULE_3__["ValidateSemanticComponent"]; });







/***/ }),

/***/ "./src/app/modules/workbench/components/semantic-management/semantic-details-dialog/semantic-details-dialog.component.html":
/*!*********************************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/semantic-management/semantic-details-dialog/semantic-details-dialog.component.html ***!
  \*********************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<mat-dialog-content>\n  <form [formGroup]=\"form\" (ngSubmit)=\"submit()\">\n    <div mat-dialog-title i18n>Enter Datapod Details</div>\n\n    <mat-form-field appearance=\"outline\" class=\"form-select\">\n      <mat-label>Datapod Category</mat-label>\n      <mat-select formControlName=\"category\">\n        <mat-option *ngFor=\"let category of dataPodCategories\" [value]=\"category.name\">\n          <div fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n            {{category.name | changeCase:'title'}}\n            <mat-icon [svgIcon]=\"category.icon\"></mat-icon>\n          </div>\n        </mat-option>\n      </mat-select>\n    </mat-form-field>\n\n    <mat-form-field class=\"inpElement\" style=\"margin-top: 5px;\">\n        <input matInput formControlName=\"name\" autocomplete=\"off\" placeholder=\"Enter Datapod Name\" required maxlength=\"25\"/>\n        <mat-error *ngIf=\"form.controls.name.hasError('required')\" i18n>\n            Datapod Name is\n            <strong>required</strong>\n        </mat-error>\n    </mat-form-field>\n\n  </form>\n</mat-dialog-content>\n<mat-dialog-actions>\n  <button mat-button type=\"button\" style=\"color: #E5524C;\" mat-dialog-close i18n>Cancel</button>\n  <span fxFlex></span>\n  <button mat-raised-button color=\"primary\" type=\"submit\" [disabled]=\"!form.valid\" (click)=\"submit()\" i18n>OK</button>\n</mat-dialog-actions>\n\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/semantic-management/semantic-details-dialog/semantic-details-dialog.component.scss":
/*!*********************************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/semantic-management/semantic-details-dialog/semantic-details-dialog.component.scss ***!
  \*********************************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".inpElement {\n  width: 100%; }\n\n.formatList {\n  border: 1px #ab0e27 solid;\n  margin-bottom: 9px; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL3NlbWFudGljLW1hbmFnZW1lbnQvc2VtYW50aWMtZGV0YWlscy1kaWFsb2cvc2VtYW50aWMtZGV0YWlscy1kaWFsb2cuY29tcG9uZW50LnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSxXQUFXLEVBQUE7O0FBR2I7RUFDRSx5QkFBOEI7RUFDOUIsa0JBQWtCLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL3NlbWFudGljLW1hbmFnZW1lbnQvc2VtYW50aWMtZGV0YWlscy1kaWFsb2cvc2VtYW50aWMtZGV0YWlscy1kaWFsb2cuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0ICdzcmMvdGhlbWVzL2Jhc2UvY29sb3JzJztcblxuLmlucEVsZW1lbnQge1xuICB3aWR0aDogMTAwJTtcbn1cblxuLmZvcm1hdExpc3Qge1xuICBib3JkZXI6IDFweCAkcHJpbWFyeS1yZWQgc29saWQ7XG4gIG1hcmdpbi1ib3R0b206IDlweDtcbn1cbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/workbench/components/semantic-management/semantic-details-dialog/semantic-details-dialog.component.ts":
/*!*******************************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/semantic-management/semantic-details-dialog/semantic-details-dialog.component.ts ***!
  \*******************************************************************************************************************************/
/*! exports provided: SemanticDetailsDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SemanticDetailsDialogComponent", function() { return SemanticDetailsDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var _consts__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../consts */ "./src/app/modules/workbench/consts.ts");





var SemanticDetailsDialogComponent = /** @class */ (function () {
    function SemanticDetailsDialogComponent(formBuilder, dialogRef, data) {
        this.formBuilder = formBuilder;
        this.dialogRef = dialogRef;
        this.data = data;
        this.dataPodCategories = _consts__WEBPACK_IMPORTED_MODULE_4__["DATAPOD_CATEGORIES"];
    }
    SemanticDetailsDialogComponent.prototype.ngOnInit = function () {
        var defaultCategory = _consts__WEBPACK_IMPORTED_MODULE_4__["DATAPOD_CATEGORIES"][0].name;
        this.form = new _angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormGroup"]({
            name: new _angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormControl"]('', [
                _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required,
                _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].maxLength(25)
            ]),
            category: new _angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormControl"](defaultCategory)
        });
    };
    SemanticDetailsDialogComponent.prototype.submit = function () {
        var _a = this.form.value, name = _a.name, category = _a.category;
        this.dialogRef.close({ name: name, category: category });
    };
    SemanticDetailsDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'semantic-details-dialog',
            template: __webpack_require__(/*! ./semantic-details-dialog.component.html */ "./src/app/modules/workbench/components/semantic-management/semantic-details-dialog/semantic-details-dialog.component.html"),
            styles: [__webpack_require__(/*! ./semantic-details-dialog.component.scss */ "./src/app/modules/workbench/components/semantic-management/semantic-details-dialog/semantic-details-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](2, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormBuilder"],
            _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"], Object])
    ], SemanticDetailsDialogComponent);
    return SemanticDetailsDialogComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/semantic-management/update/update-semantic.component.html":
/*!********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/semantic-management/update/update-semantic.component.html ***!
  \********************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"container\">\n    <div class=\"header\" fxLayout=\"row\" fxLayoutAlign=\"center center\" fxLayoutGap=\"10px\">\n        <button mat-button class=\"bck-btn\">\n            <mat-icon fontIcon=\"icon-arrow-left\" (click)=\"backToDS()\"></mat-icon>\n        </button>\n        <div fxFlex>Update Datapod </div>\n        <button mat-raised-button color=\"primary\" [disabled]=\"!isSelected\" (click)=\"updateSemantic()\">\n            <span i18n>Update</span>\n        </button>\n    </div>\n    <div class=\"body\" fxLayout=\"row\" fxLayoutAlign=\"center stretch\" fxLayoutGap=\"10px\">\n        <mat-card fxFlex=\"25\" *ngIf=\"!dpID\">\n            <mat-card-content>\n                <dx-data-grid #dsGrid fxFlex [dataSource]=\"gridDataAvailableDP\" [rowAlternationEnabled]=\"true\" [height]=\"'100%'\" [width]=\"'100%'\"\n                    keyExpr=\"id\" [showBorders]=\"false\" [showColumnLines]=\"false\" [hoverStateEnabled]=\"true\" (onSelectionChanged)=\"onDPSelectionChanged($event.currentSelectedRowKeys[0])\">\n                    <dxo-selection mode=\"single\"></dxo-selection>\n                    <dxi-column caption=\"Datapod Name\" dataField=\"metricName\"></dxi-column>\n                    <dxi-column caption=\"Created By\" dataField=\"createdBy\"></dxi-column>\n                    <dxi-column caption=\"Updated Time\" dataField=\"createdAt\" dataType=\"date\"></dxi-column>\n                    <dxo-scrolling mode=\"virtual\" showScrollbar=\"always\" [useNative]=\"false\"></dxo-scrolling>\n                    <dxo-filter-row [visible]=\"true\" [applyFilter]=\"auto\"></dxo-filter-row>\n                </dx-data-grid>\n            </mat-card-content>\n        </mat-card>\n        <mat-card fxFlex>\n            <mat-card-content fxLayout=\"row\" fxLayoutAlign=\"center stretch\">\n                <div class=\"results\" fxLayout=\"row\" fxLayoutAlign=\"center center\" *ngIf=\"!selectedDPData?.length\">\n                    <span>Select Datapod from left pane to update</span>\n                </div>\n                <mat-tab-group *ngIf=\"selectedDPData?.length\" fxFlex>\n                    <mat-tab *ngFor=\"let dp of selectedDPData; let index = index\" [label]=\"dp?.artifactName\">\n                        <dx-data-grid #dsGrid [dataSource]=\"dp?.columns\" [showBorders]=\"true\" [height]=\"'100%'\" [width]=\"'100%'\" [rowAlternationEnabled]=\"true\"\n                            [showColumnLines]=\"false\" style=\"position:absolute;top:0;bottom:0;left:0;bottom:0;\">\n                            <dxo-editing mode=\"cell\" [allowUpdating]=\"true\">\n                            </dxo-editing>\n                            <dxi-column [width]=\"80\" caption=\"Include\" dataField=\"include\" dataType=\"boolean\"></dxi-column>\n                            <dxi-column caption=\"Display Name\" dataField=\"displayName\">\n                                <dxi-validation-rule type=\"required\"></dxi-validation-rule>\n                                <dxi-validation-rule type=\"stringLength\" [max]=\"30\">\n                                </dxi-validation-rule>\n                            </dxi-column>\n                            <dxi-column caption=\"Column Name\" [allowEditing]=\"false\" dataField=\"columnName\"></dxi-column>\n                            <dxi-column [width]=\"150\" caption=\"Data Type\" [allowEditing]=\"false\" dataField=\"type\"></dxi-column>\n                            <dxi-column [width]=\"100\" caption=\"Filter Eligible\" dataField=\"filterEligible\" dataType=\"boolean\"></dxi-column>\n                            <dxi-column [width]=\"80\" caption=\"KPI Eligible\" dataField=\"kpiEligible\" dataType=\"boolean\"></dxi-column>\n                            <dxi-column [width]=\"80\" *ngIf=\"isJoinEligible\" caption=\"Join Eligible\" dataField=\"joinEligible\" dataType=\"boolean\"></dxi-column>\n                            <dxo-scrolling mode=\"virtual\" showScrollbar=\"always\" [useNative]=\"false\"></dxo-scrolling>\n                            <dxo-filter-row [visible]=\"true\" [applyFilter]=\"auto\"></dxo-filter-row>\n                            <dxo-header-filter [visible]=\"false\"></dxo-header-filter>\n                        </dx-data-grid>\n                    </mat-tab>\n                </mat-tab-group>\n            </mat-card-content>\n        </mat-card>\n    </div>\n</div>"

/***/ }),

/***/ "./src/app/modules/workbench/components/semantic-management/update/update-semantic.component.scss":
/*!********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/semantic-management/update/update-semantic.component.scss ***!
  \********************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%; }\n\n.container {\n  width: 100%;\n  height: 100%;\n  background-color: #f5f9fc;\n  overflow: auto; }\n\n.container .header {\n    color: rgba(0, 0, 0, 0.54);\n    background: rgba(0, 0, 0, 0.03);\n    padding: 8px 0;\n    height: 50px;\n    font-size: 18px;\n    font-weight: 700;\n    border: 2px solid white; }\n\n.container .header .mat-icon {\n      font-size: 26px; }\n\n.container .header > span {\n      font-size: 14px;\n      font-weight: normal; }\n\n.container .body {\n    background-color: #fff;\n    height: calc(100% - 50px);\n    max-height: calc(100% - 50px) !important; }\n\n.container .body mat-card {\n      padding: 0;\n      height: 100%; }\n\n.container .body mat-card .mat-card-content {\n        height: 100%;\n        max-height: 100%;\n        overflow: auto; }\n\n.container .body mat-card .mat-card-content .mat-tab-group {\n          height: 100% !important;\n          max-height: 100% !important;\n          overflow: auto; }\n\n.container .body mat-card .mat-card-content ::ng-deep .mat-tab-label-active {\n          color: #0077be; }\n\n.container .body mat-card .mat-card-content ::ng-deep .mat-tab-body-wrapper {\n          height: 100%; }\n\n.container .body mat-card .mat-card-content ::ng-deep .mat-tab-body-wrapper .mat-tab-body {\n            overflow: hidden !important; }\n\n.container .body mat-card .mat-card-content ::ng-deep .mat-tab-body-wrapper .mat-tab-body .mat-tab-body-content {\n              overflow: hidden !important; }\n\n.container .body mat-card .mat-card-content .results {\n          width: 100%;\n          height: 100%;\n          color: #bdbdbd;\n          text-align: center; }\n\n.container .body mat-card .mat-card-content .results span {\n            font-size: 28px;\n            word-break: break-word; }\n\n.container .body mat-card .mat-card-content .dx-datagrid-content .dx-datagrid-table .dx-row .dx-command-select {\n          padding: 0;\n          width: 40px;\n          min-width: 40px; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL3NlbWFudGljLW1hbmFnZW1lbnQvdXBkYXRlL3VwZGF0ZS1zZW1hbnRpYy5jb21wb25lbnQuc2NzcyIsIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL3RoZW1lcy9iYXNlL19jb2xvcnMuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUNFLFdBQVcsRUFBQTs7QUFHYjtFQUNFLFdBQVc7RUFDWCxZQUFZO0VBQ1oseUJDc0N3QjtFRHJDeEIsY0FBYyxFQUFBOztBQUpoQjtJQU9JLDBCQUEwQjtJQUMxQiwrQkFBK0I7SUFDL0IsY0FBYztJQUNkLFlBQVk7SUFDWixlQUFlO0lBQ2YsZ0JBQWdCO0lBQ2hCLHVCQUF1QixFQUFBOztBQWIzQjtNQWdCTSxlQUFlLEVBQUE7O0FBaEJyQjtNQW9CTSxlQUFlO01BQ2YsbUJBQW1CLEVBQUE7O0FBckJ6QjtJQTBCSSxzQkFBc0I7SUFDdEIseUJBQXlCO0lBQ3pCLHdDQUF3QyxFQUFBOztBQTVCNUM7TUErQk0sVUFBVTtNQUNWLFlBQVksRUFBQTs7QUFoQ2xCO1FBbUNRLFlBQVk7UUFDWixnQkFBZ0I7UUFDaEIsY0FBYyxFQUFBOztBQXJDdEI7VUF3Q1UsdUJBQXVCO1VBQ3ZCLDJCQUEyQjtVQUMzQixjQUFjLEVBQUE7O0FBMUN4QjtVQThDVSxjQ2xEZSxFQUFBOztBREl6QjtVQWtEVSxZQUFZLEVBQUE7O0FBbER0QjtZQXFEWSwyQkFBMkIsRUFBQTs7QUFyRHZDO2NBd0RjLDJCQUEyQixFQUFBOztBQXhEekM7VUE4RFUsV0FBVztVQUNYLFlBQVk7VUFDWixjQUF5QjtVQUN6QixrQkFBa0IsRUFBQTs7QUFqRTVCO1lBb0VZLGVBQWU7WUFDZixzQkFBc0IsRUFBQTs7QUFyRWxDO1VBMEVVLFVBQVU7VUFDVixXQUFXO1VBQ1gsZUFBZSxFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy93b3JrYmVuY2gvY29tcG9uZW50cy9zZW1hbnRpYy1tYW5hZ2VtZW50L3VwZGF0ZS91cGRhdGUtc2VtYW50aWMuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0ICdzcmMvdGhlbWVzL2Jhc2UvY29sb3JzJztcblxuOmhvc3Qge1xuICB3aWR0aDogMTAwJTtcbn1cblxuLmNvbnRhaW5lciB7XG4gIHdpZHRoOiAxMDAlO1xuICBoZWlnaHQ6IDEwMCU7XG4gIGJhY2tncm91bmQtY29sb3I6ICRiYWNrZ3JvdW5kLWNvbG9yO1xuICBvdmVyZmxvdzogYXV0bztcblxuICAuaGVhZGVyIHtcbiAgICBjb2xvcjogcmdiYSgwLCAwLCAwLCAwLjU0KTtcbiAgICBiYWNrZ3JvdW5kOiByZ2JhKDAsIDAsIDAsIDAuMDMpO1xuICAgIHBhZGRpbmc6IDhweCAwO1xuICAgIGhlaWdodDogNTBweDtcbiAgICBmb250LXNpemU6IDE4cHg7XG4gICAgZm9udC13ZWlnaHQ6IDcwMDtcbiAgICBib3JkZXI6IDJweCBzb2xpZCB3aGl0ZTtcblxuICAgIC5tYXQtaWNvbiB7XG4gICAgICBmb250LXNpemU6IDI2cHg7XG4gICAgfVxuXG4gICAgJiA+IHNwYW4ge1xuICAgICAgZm9udC1zaXplOiAxNHB4O1xuICAgICAgZm9udC13ZWlnaHQ6IG5vcm1hbDtcbiAgICB9XG4gIH1cblxuICAuYm9keSB7XG4gICAgYmFja2dyb3VuZC1jb2xvcjogI2ZmZjtcbiAgICBoZWlnaHQ6IGNhbGMoMTAwJSAtIDUwcHgpO1xuICAgIG1heC1oZWlnaHQ6IGNhbGMoMTAwJSAtIDUwcHgpICFpbXBvcnRhbnQ7XG5cbiAgICBtYXQtY2FyZCB7XG4gICAgICBwYWRkaW5nOiAwO1xuICAgICAgaGVpZ2h0OiAxMDAlO1xuXG4gICAgICAubWF0LWNhcmQtY29udGVudCB7XG4gICAgICAgIGhlaWdodDogMTAwJTtcbiAgICAgICAgbWF4LWhlaWdodDogMTAwJTtcbiAgICAgICAgb3ZlcmZsb3c6IGF1dG87XG5cbiAgICAgICAgLm1hdC10YWItZ3JvdXAge1xuICAgICAgICAgIGhlaWdodDogMTAwJSAhaW1wb3J0YW50O1xuICAgICAgICAgIG1heC1oZWlnaHQ6IDEwMCUgIWltcG9ydGFudDtcbiAgICAgICAgICBvdmVyZmxvdzogYXV0bztcbiAgICAgICAgfVxuXG4gICAgICAgIDo6bmctZGVlcCAubWF0LXRhYi1sYWJlbC1hY3RpdmUge1xuICAgICAgICAgIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIyO1xuICAgICAgICB9XG5cbiAgICAgICAgOjpuZy1kZWVwIC5tYXQtdGFiLWJvZHktd3JhcHBlciB7XG4gICAgICAgICAgaGVpZ2h0OiAxMDAlO1xuXG4gICAgICAgICAgLm1hdC10YWItYm9keSB7XG4gICAgICAgICAgICBvdmVyZmxvdzogaGlkZGVuICFpbXBvcnRhbnQ7XG5cbiAgICAgICAgICAgIC5tYXQtdGFiLWJvZHktY29udGVudCB7XG4gICAgICAgICAgICAgIG92ZXJmbG93OiBoaWRkZW4gIWltcG9ydGFudDtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICB9XG4gICAgICAgIH1cblxuICAgICAgICAucmVzdWx0cyB7XG4gICAgICAgICAgd2lkdGg6IDEwMCU7XG4gICAgICAgICAgaGVpZ2h0OiAxMDAlO1xuICAgICAgICAgIGNvbG9yOiByZ2IoMTg5LCAxODksIDE4OSk7XG4gICAgICAgICAgdGV4dC1hbGlnbjogY2VudGVyO1xuXG4gICAgICAgICAgc3BhbiB7XG4gICAgICAgICAgICBmb250LXNpemU6IDI4cHg7XG4gICAgICAgICAgICB3b3JkLWJyZWFrOiBicmVhay13b3JkO1xuICAgICAgICAgIH1cbiAgICAgICAgfVxuXG4gICAgICAgIC5keC1kYXRhZ3JpZC1jb250ZW50IC5keC1kYXRhZ3JpZC10YWJsZSAuZHgtcm93IC5keC1jb21tYW5kLXNlbGVjdCB7XG4gICAgICAgICAgcGFkZGluZzogMDtcbiAgICAgICAgICB3aWR0aDogNDBweDtcbiAgICAgICAgICBtaW4td2lkdGg6IDQwcHg7XG4gICAgICAgIH1cbiAgICAgIH1cbiAgICB9XG4gIH1cbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/workbench/components/semantic-management/update/update-semantic.component.ts":
/*!******************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/semantic-management/update/update-semantic.component.ts ***!
  \******************************************************************************************************/
/*! exports provided: UpdateSemanticComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "UpdateSemanticComponent", function() { return UpdateSemanticComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../../../common/services/toastMessage.service */ "./src/app/common/services/toastMessage.service.ts");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");
/* harmony import */ var _wb_comp_configs__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../wb-comp-configs */ "./src/app/modules/workbench/wb-comp-configs.ts");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! lodash/cloneDeep */ "./node_modules/lodash/cloneDeep.js");
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_7__);
/* harmony import */ var lodash_forIn__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! lodash/forIn */ "./node_modules/lodash/forIn.js");
/* harmony import */ var lodash_forIn__WEBPACK_IMPORTED_MODULE_8___default = /*#__PURE__*/__webpack_require__.n(lodash_forIn__WEBPACK_IMPORTED_MODULE_8__);
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! lodash/map */ "./node_modules/lodash/map.js");
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_9___default = /*#__PURE__*/__webpack_require__.n(lodash_map__WEBPACK_IMPORTED_MODULE_9__);
/* harmony import */ var lodash_toLower__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! lodash/toLower */ "./node_modules/lodash/toLower.js");
/* harmony import */ var lodash_toLower__WEBPACK_IMPORTED_MODULE_10___default = /*#__PURE__*/__webpack_require__.n(lodash_toLower__WEBPACK_IMPORTED_MODULE_10__);
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! lodash/filter */ "./node_modules/lodash/filter.js");
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_11___default = /*#__PURE__*/__webpack_require__.n(lodash_filter__WEBPACK_IMPORTED_MODULE_11__);
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! lodash/find */ "./node_modules/lodash/find.js");
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_12___default = /*#__PURE__*/__webpack_require__.n(lodash_find__WEBPACK_IMPORTED_MODULE_12__);
/* harmony import */ var lodash_findIndex__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! lodash/findIndex */ "./node_modules/lodash/findIndex.js");
/* harmony import */ var lodash_findIndex__WEBPACK_IMPORTED_MODULE_13___default = /*#__PURE__*/__webpack_require__.n(lodash_findIndex__WEBPACK_IMPORTED_MODULE_13__);
/* harmony import */ var lodash_omit__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! lodash/omit */ "./node_modules/lodash/omit.js");
/* harmony import */ var lodash_omit__WEBPACK_IMPORTED_MODULE_14___default = /*#__PURE__*/__webpack_require__.n(lodash_omit__WEBPACK_IMPORTED_MODULE_14__);















var UpdateSemanticComponent = /** @class */ (function () {
    function UpdateSemanticComponent(router, workBench, notify) {
        this.router = router;
        this.workBench = workBench;
        this.notify = notify;
        this.isSelected = false;
        this.selectedDPData = [];
        this.availableDS = [];
        this.isJoinEligible = false;
        this.selectedDPDetails = [];
        this.dpID = '';
        // Below is used when navigating from Datapod view
        this.dpID = this.workBench.getDataFromLS('dpID');
    }
    UpdateSemanticComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.workBench.getDatasets().subscribe(function (data) {
            _this.availableDS = data;
            if (_this.dpID !== null) {
                _this.onDPSelectionChanged(_this.dpID);
            }
            else {
                _this.workBench.getListOfSemantic().subscribe(function (list) {
                    _this.availableDP = lodash_get__WEBPACK_IMPORTED_MODULE_6__(list, 'contents[0].ANALYZE');
                    _this.gridDataAvailableDP = lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_7__(_this.availableDP);
                });
            }
        });
    };
    UpdateSemanticComponent.prototype.ngOnDestroy = function () {
        if (this.dpID !== null) {
            this.workBench.removeDataFromLS('dpID');
        }
    };
    UpdateSemanticComponent.prototype.backToDS = function () {
        this.router.navigate(['workbench', 'dataobjects']);
    };
    /**
     * Gets the detailed description of Datapod and its parent dataset/s.
     * Merges all the fields and shows which are included.
     *
     * @param {*} id
     * @memberof UpdateSemanticComponent
     */
    UpdateSemanticComponent.prototype.onDPSelectionChanged = function (id) {
        var _this = this;
        this.isSelected = true;
        this.workBench.getSemanticDetails(id).subscribe(function (data) {
            _this.selectedDPDetails = lodash_omit__WEBPACK_IMPORTED_MODULE_14__(data, 'statusMessage');
            _this.selectedDPData = lodash_get__WEBPACK_IMPORTED_MODULE_6__(data, 'artifacts');
            lodash_forIn__WEBPACK_IMPORTED_MODULE_8__(_this.selectedDPData, function (dp) {
                var parentDSName = dp.artifactName;
                var parentDSData = lodash_find__WEBPACK_IMPORTED_MODULE_12__(_this.availableDS, function (obj) {
                    return obj.system.name === parentDSName;
                });
                _this.isJoinEligible = parentDSData.joinEligible;
                _this.injectFieldProperties(parentDSData);
                lodash_forIn__WEBPACK_IMPORTED_MODULE_8__(parentDSData.schema.fields, function (obj) {
                    if (lodash_findIndex__WEBPACK_IMPORTED_MODULE_13__(dp.columns, ['columnName', obj.columnName]) === -1) {
                        dp.columns.push(obj);
                    }
                });
            });
        });
    };
    /**
     * Construct semantic layer field object structure.
     *
     * @param {*} dsData
     * @returns
     * @memberof ValidateSemanticComponent
     */
    UpdateSemanticComponent.prototype.injectFieldProperties = function (dsData) {
        var artifactName = dsData.system.name;
        dsData.schema.fields = lodash_map__WEBPACK_IMPORTED_MODULE_9__(dsData.schema.fields, function (value) {
            var colName = value.isKeyword ? value.name + ".keyword" : value.name;
            return {
                alias: value.name,
                columnName: colName,
                displayName: value.name,
                filterEligible: true,
                joinEligible: false,
                kpiEligible: false,
                include: false,
                name: value.name,
                table: artifactName,
                type: _wb_comp_configs__WEBPACK_IMPORTED_MODULE_5__["TYPE_CONVERSION"][lodash_toLower__WEBPACK_IMPORTED_MODULE_10__(value.type)]
            };
        });
        return dsData;
    };
    /**
     * Updates the saemantic definition with user changes.
     *
     * @memberof UpdateSemanticComponent
     */
    UpdateSemanticComponent.prototype.updateSemantic = function () {
        var _this = this;
        this.selectedDPDetails.artifacts = [];
        lodash_forIn__WEBPACK_IMPORTED_MODULE_8__(this.selectedDPData, function (ds) {
            _this.selectedDPDetails.artifacts.push({
                artifactName: ds.artifactName,
                columns: lodash_filter__WEBPACK_IMPORTED_MODULE_11__(ds.columns, 'include')
            });
        });
        this.workBench
            .updateSemanticDetails(this.selectedDPDetails)
            .subscribe(function (data) {
            _this.notify.info('Datapod Updated successfully', 'Datapod', {
                hideDelay: 9000
            });
            _this.router.navigate(['workbench', 'dataobjects']);
        });
    };
    UpdateSemanticComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'update-semantic',
            template: __webpack_require__(/*! ./update-semantic.component.html */ "./src/app/modules/workbench/components/semantic-management/update/update-semantic.component.html"),
            styles: [__webpack_require__(/*! ./update-semantic.component.scss */ "./src/app/modules/workbench/components/semantic-management/update/update-semantic.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"],
            _services_workbench_service__WEBPACK_IMPORTED_MODULE_4__["WorkbenchService"],
            _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_3__["ToastService"]])
    ], UpdateSemanticComponent);
    return UpdateSemanticComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/semantic-management/validate/validate-semantic.component.html":
/*!************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/semantic-management/validate/validate-semantic.component.html ***!
  \************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"container\">\n  <div\n    class=\"header\"\n    fxLayout=\"row\"\n    fxLayoutAlign=\"center center\"\n    fxLayoutGap=\"10px\"\n  >\n    <button mat-button class=\"bck-btn\">\n      <mat-icon fontIcon=\"icon-arrow-left\" (click)=\"showDSList()\"></mat-icon>\n    </button>\n    <div fxFlex>Create Datapod</div>\n    <span fxFlex>Selected Dataset/s</span>\n    <button mat-raised-button color=\"primary\" (click)=\"createDatapod()\">\n      <span i18n>Create</span>\n    </button>\n  </div>\n  <div\n    class=\"body\"\n    fxLayout=\"row\"\n    fxLayoutAlign=\"center stretch\"\n    fxLayoutGap=\"10px\"\n  >\n    <mat-card fxFlex>\n      <mat-card-content>\n        <mat-tab-group fxFlex>\n          <mat-tab\n            *ngFor=\"let ds of selectedDS; let index = index\"\n            [label]=\"ds?.system?.name\"\n          >\n            <dx-data-grid\n              #dsGrid\n              [dataSource]=\"ds.schema?.fields\"\n              [showBorders]=\"true\"\n              [height]=\"'100%'\"\n              [width]=\"'100%'\"\n              [rowAlternationEnabled]=\"true\"\n              [showColumnLines]=\"false\"\n              style=\"position:absolute;top:0;bottom:0;left:0;bottom:0;\"\n            >\n              <dxo-editing mode=\"cell\" [allowUpdating]=\"true\"> </dxo-editing>\n              <dxi-column\n                [width]=\"80\"\n                caption=\"Include\"\n                dataField=\"include\"\n                dataType=\"boolean\"\n              ></dxi-column>\n              <dxi-column caption=\"Display Name\" dataField=\"displayName\">\n                <dxi-validation-rule type=\"required\"></dxi-validation-rule>\n                <dxi-validation-rule type=\"stringLength\" [max]=\"30\">\n                </dxi-validation-rule>\n              </dxi-column>\n              <dxi-column\n                caption=\"Column Name\"\n                [allowEditing]=\"false\"\n                dataField=\"columnName\"\n              ></dxi-column>\n              <dxi-column\n                [width]=\"150\"\n                caption=\"Data Type\"\n                [allowEditing]=\"false\"\n                dataField=\"type\"\n              ></dxi-column>\n              <dxi-column\n                [width]=\"100\"\n                caption=\"Filter Eligible\"\n                dataField=\"filterEligible\"\n                dataType=\"boolean\"\n              ></dxi-column>\n              <dxi-column\n                *ngIf=\"!isJoinEligible\"\n                [width]=\"80\"\n                caption=\"KPI Eligible\"\n                dataField=\"kpiEligible\"\n                dataType=\"boolean\"\n              ></dxi-column>\n              <!-- Commenting out creation of multiple Artifacts as there is issue with report. -->\n\n              <!-- <dxi-column\n                [width]=\"80\"\n                *ngIf=\"isJoinEligible\"\n                caption=\"Join Eligible\"\n                dataField=\"joinEligible\"\n                dataType=\"boolean\"\n              ></dxi-column> -->\n              <dxo-scrolling\n                mode=\"virtual\"\n                showScrollbar=\"always\"\n                [useNative]=\"false\"\n              ></dxo-scrolling>\n              <dxo-filter-row\n                [visible]=\"true\"\n                [applyFilter]=\"auto\"\n              ></dxo-filter-row>\n              <dxo-header-filter [visible]=\"false\"></dxo-header-filter>\n            </dx-data-grid>\n          </mat-tab>\n        </mat-tab-group>\n      </mat-card-content>\n    </mat-card>\n  </div>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/semantic-management/validate/validate-semantic.component.scss":
/*!************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/semantic-management/validate/validate-semantic.component.scss ***!
  \************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%; }\n\n.container {\n  height: calc(100% - 3px);\n  background-color: #f5f9fc;\n  padding: 0 3px 3px;\n  overflow: auto; }\n\n.container .header {\n    color: rgba(0, 0, 0, 0.54);\n    background: rgba(0, 0, 0, 0.03);\n    padding: 8px 0;\n    height: 50px;\n    font-size: 18px;\n    font-weight: 700;\n    border: 2px solid white; }\n\n.container .header .mat-icon {\n      font-size: 26px; }\n\n.container .header > span {\n      font-size: 14px;\n      font-weight: normal; }\n\n.container .body {\n    background-color: #fff;\n    height: calc(100% - 50px);\n    max-height: calc(100% - 50px) !important; }\n\n.container .body mat-card {\n      padding: 0;\n      height: 100%; }\n\n.container .body mat-card .mat-card-content {\n        height: 100%;\n        max-height: 100%;\n        overflow: auto; }\n\n.container .body mat-card .mat-card-content .mat-tab-group {\n          height: 100%; }\n\n.container .body mat-card .mat-card-content ::ng-deep .mat-tab-label-active {\n          color: #0077be; }\n\n.container .body mat-card .mat-card-content ::ng-deep .mat-tab-body-wrapper {\n          height: 100%; }\n\n.container .body mat-card .mat-card-content ::ng-deep .mat-tab-body-wrapper .mat-tab-body {\n            overflow: hidden !important; }\n\n.container .body mat-card .mat-card-content ::ng-deep .mat-tab-body-wrapper .mat-tab-body .mat-tab-body-content {\n              overflow: hidden !important; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL3NlbWFudGljLW1hbmFnZW1lbnQvdmFsaWRhdGUvdmFsaWRhdGUtc2VtYW50aWMuY29tcG9uZW50LnNjc3MiLCIvVXNlcnMvYmFybmFtdW10eWFuL1Byb2plY3RzL21vZHVzL3NpcC9zYXctd2ViL3NyYy90aGVtZXMvYmFzZS9fY29sb3JzLnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSxXQUFXLEVBQUE7O0FBR2I7RUFDRSx3QkFBd0I7RUFDeEIseUJDdUN3QjtFRHRDeEIsa0JBQWtCO0VBQ2xCLGNBQWMsRUFBQTs7QUFKaEI7SUFPSSwwQkFBMEI7SUFDMUIsK0JBQStCO0lBQy9CLGNBQWM7SUFDZCxZQUFZO0lBQ1osZUFBZTtJQUNmLGdCQUFnQjtJQUNoQix1QkFBdUIsRUFBQTs7QUFiM0I7TUFnQk0sZUFBZSxFQUFBOztBQWhCckI7TUFvQk0sZUFBZTtNQUNmLG1CQUFtQixFQUFBOztBQXJCekI7SUEwQkksc0JBQXNCO0lBQ3RCLHlCQUF5QjtJQUN6Qix3Q0FBd0MsRUFBQTs7QUE1QjVDO01BK0JNLFVBQVU7TUFDVixZQUFZLEVBQUE7O0FBaENsQjtRQW1DUSxZQUFZO1FBQ1osZ0JBQWdCO1FBQ2hCLGNBQWMsRUFBQTs7QUFyQ3RCO1VBd0NVLFlBQVksRUFBQTs7QUF4Q3RCO1VBNENVLGNDaERlLEVBQUE7O0FESXpCO1VBZ0RVLFlBQVksRUFBQTs7QUFoRHRCO1lBbURZLDJCQUEyQixFQUFBOztBQW5EdkM7Y0FzRGMsMkJBQTJCLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL3NlbWFudGljLW1hbmFnZW1lbnQvdmFsaWRhdGUvdmFsaWRhdGUtc2VtYW50aWMuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0ICdzcmMvdGhlbWVzL2Jhc2UvY29sb3JzJztcblxuOmhvc3Qge1xuICB3aWR0aDogMTAwJTtcbn1cblxuLmNvbnRhaW5lciB7XG4gIGhlaWdodDogY2FsYygxMDAlIC0gM3B4KTtcbiAgYmFja2dyb3VuZC1jb2xvcjogJGJhY2tncm91bmQtY29sb3I7XG4gIHBhZGRpbmc6IDAgM3B4IDNweDtcbiAgb3ZlcmZsb3c6IGF1dG87XG5cbiAgLmhlYWRlciB7XG4gICAgY29sb3I6IHJnYmEoMCwgMCwgMCwgMC41NCk7XG4gICAgYmFja2dyb3VuZDogcmdiYSgwLCAwLCAwLCAwLjAzKTtcbiAgICBwYWRkaW5nOiA4cHggMDtcbiAgICBoZWlnaHQ6IDUwcHg7XG4gICAgZm9udC1zaXplOiAxOHB4O1xuICAgIGZvbnQtd2VpZ2h0OiA3MDA7XG4gICAgYm9yZGVyOiAycHggc29saWQgd2hpdGU7XG5cbiAgICAubWF0LWljb24ge1xuICAgICAgZm9udC1zaXplOiAyNnB4O1xuICAgIH1cblxuICAgICYgPiBzcGFuIHtcbiAgICAgIGZvbnQtc2l6ZTogMTRweDtcbiAgICAgIGZvbnQtd2VpZ2h0OiBub3JtYWw7XG4gICAgfVxuICB9XG5cbiAgLmJvZHkge1xuICAgIGJhY2tncm91bmQtY29sb3I6ICNmZmY7XG4gICAgaGVpZ2h0OiBjYWxjKDEwMCUgLSA1MHB4KTtcbiAgICBtYXgtaGVpZ2h0OiBjYWxjKDEwMCUgLSA1MHB4KSAhaW1wb3J0YW50O1xuXG4gICAgbWF0LWNhcmQge1xuICAgICAgcGFkZGluZzogMDtcbiAgICAgIGhlaWdodDogMTAwJTtcblxuICAgICAgLm1hdC1jYXJkLWNvbnRlbnQge1xuICAgICAgICBoZWlnaHQ6IDEwMCU7XG4gICAgICAgIG1heC1oZWlnaHQ6IDEwMCU7XG4gICAgICAgIG92ZXJmbG93OiBhdXRvO1xuXG4gICAgICAgIC5tYXQtdGFiLWdyb3VwIHtcbiAgICAgICAgICBoZWlnaHQ6IDEwMCU7XG4gICAgICAgIH1cblxuICAgICAgICA6Om5nLWRlZXAgLm1hdC10YWItbGFiZWwtYWN0aXZlIHtcbiAgICAgICAgICBjb2xvcjogJHByaW1hcnktYmx1ZS1iMjtcbiAgICAgICAgfVxuXG4gICAgICAgIDo6bmctZGVlcCAubWF0LXRhYi1ib2R5LXdyYXBwZXIge1xuICAgICAgICAgIGhlaWdodDogMTAwJTtcblxuICAgICAgICAgIC5tYXQtdGFiLWJvZHkge1xuICAgICAgICAgICAgb3ZlcmZsb3c6IGhpZGRlbiAhaW1wb3J0YW50O1xuXG4gICAgICAgICAgICAubWF0LXRhYi1ib2R5LWNvbnRlbnQge1xuICAgICAgICAgICAgICBvdmVyZmxvdzogaGlkZGVuICFpbXBvcnRhbnQ7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICB9XG4gICAgfVxuICB9XG59XG4iLCIvLyBCcmFuZGluZyBjb2xvcnNcbiRwcmltYXJ5LWJsdWUtYjE6ICMxYTg5ZDQ7XG4kcHJpbWFyeS1ibHVlLWIyOiAjMDA3N2JlO1xuJHByaW1hcnktYmx1ZS1iMzogIzIwNmJjZTtcbiRwcmltYXJ5LWJsdWUtYjQ6ICMxZDNhYjI7XG5cbiRwcmltYXJ5LWhvdmVyLWJsdWU6ICMxZDYxYjE7XG4kZ3JpZC1ob3Zlci1jb2xvcjogI2Y1ZjlmYztcbiRncmlkLWhlYWRlci1iZy1jb2xvcjogI2Q3ZWFmYTtcbiRncmlkLWhlYWRlci1jb2xvcjogIzBiNGQ5OTtcbiRncmlkLXRleHQtY29sb3I6ICM0NjQ2NDY7XG4kZ3JleS10ZXh0LWNvbG9yOiAjNjM2MzYzO1xuXG4kc2VsZWN0aW9uLWhpZ2hsaWdodC1jb2w6IHJnYmEoMCwgMTQwLCAyNjAsIDAuMik7XG4kcHJpbWFyeS1ncmV5LWcxOiAjZDFkM2QzO1xuJHByaW1hcnktZ3JleS1nMjogIzk5OTtcbiRwcmltYXJ5LWdyZXktZzM6ICM3MzczNzM7XG4kcHJpbWFyeS1ncmV5LWc0OiAjNWM2NjcwO1xuJHByaW1hcnktZ3JleS1nNTogIzMxMzEzMTtcbiRwcmltYXJ5LWdyZXktZzY6ICNmNWY1ZjU7XG4kcHJpbWFyeS1ncmV5LWc3OiAjM2QzZDNkO1xuXG4kcHJpbWFyeS13aGl0ZTogI2ZmZjtcbiRwcmltYXJ5LWJsYWNrOiAjMDAwO1xuJHByaW1hcnktcmVkOiAjYWIwZTI3O1xuJHByaW1hcnktZ3JlZW46ICM3M2I0MjE7XG4kcHJpbWFyeS1vcmFuZ2U6ICNmMDc2MDE7XG5cbiRzZWNvbmRhcnktZ3JlZW46ICM2ZmIzMjA7XG4kc2Vjb25kYXJ5LXllbGxvdzogI2ZmYmUwMDtcbiRzZWNvbmRhcnktb3JhbmdlOiAjZmY5MDAwO1xuJHNlY29uZGFyeS1yZWQ6ICNkOTNlMDA7XG4kc2Vjb25kYXJ5LWJlcnJ5OiAjYWMxNDVhO1xuJHNlY29uZGFyeS1wdXJwbGU6ICM5MTQxOTE7XG5cbiRzdHJpbmctdHlwZS1jb2xvcjogIzQ5OTViMjtcbiRudW1iZXItdHlwZS1jb2xvcjogIzAwYjE4MDtcbiRnZW8tdHlwZS1jb2xvcjogIzg0NWVjMjtcbiRkYXRlLXR5cGUtY29sb3I6ICNkMTk2MjE7XG5cbiR0eXBlLWNoaXAtb3BhY2l0eTogMTtcbiRzdHJpbmctdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRzdHJpbmctdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRudW1iZXItdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRudW1iZXItdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRnZW8tdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRnZW8tdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRkYXRlLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZGF0ZS10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuXG4kcmVwb3J0LWRlc2lnbmVyLXNldHRpbmdzLWJnLWNvbG9yOiAjZjVmOWZjO1xuJGJhY2tncm91bmQtY29sb3I6ICNmNWY5ZmM7XG4iXX0= */"

/***/ }),

/***/ "./src/app/modules/workbench/components/semantic-management/validate/validate-semantic.component.ts":
/*!**********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/semantic-management/validate/validate-semantic.component.ts ***!
  \**********************************************************************************************************/
/*! exports provided: ValidateSemanticComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ValidateSemanticComponent", function() { return ValidateSemanticComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var lodash_forIn__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/forIn */ "./node_modules/lodash/forIn.js");
/* harmony import */ var lodash_forIn__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_forIn__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/map */ "./node_modules/lodash/map.js");
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_map__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var lodash_toLower__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/toLower */ "./node_modules/lodash/toLower.js");
/* harmony import */ var lodash_toLower__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_toLower__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var lodash_split__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! lodash/split */ "./node_modules/lodash/split.js");
/* harmony import */ var lodash_split__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(lodash_split__WEBPACK_IMPORTED_MODULE_7__);
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! lodash/filter */ "./node_modules/lodash/filter.js");
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_8___default = /*#__PURE__*/__webpack_require__.n(lodash_filter__WEBPACK_IMPORTED_MODULE_8__);
/* harmony import */ var lodash_trim__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! lodash/trim */ "./node_modules/lodash/trim.js");
/* harmony import */ var lodash_trim__WEBPACK_IMPORTED_MODULE_9___default = /*#__PURE__*/__webpack_require__.n(lodash_trim__WEBPACK_IMPORTED_MODULE_9__);
/* harmony import */ var _semantic_details_dialog_semantic_details_dialog_component__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../semantic-details-dialog/semantic-details-dialog.component */ "./src/app/modules/workbench/components/semantic-management/semantic-details-dialog/semantic-details-dialog.component.ts");
/* harmony import */ var _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../../../../../common/services/toastMessage.service */ "./src/app/common/services/toastMessage.service.ts");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ../../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");
/* harmony import */ var _wb_comp_configs__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ../../../wb-comp-configs */ "./src/app/modules/workbench/wb-comp-configs.ts");














var ValidateSemanticComponent = /** @class */ (function () {
    function ValidateSemanticComponent(router, workBench, dialog, notify) {
        this.router = router;
        this.workBench = workBench;
        this.dialog = dialog;
        this.notify = notify;
        this.isJoinEligible = false;
        this.selectedDS = this.injectFieldProperties(this.workBench.getDataFromLS('selectedDS'));
    }
    ValidateSemanticComponent.prototype.ngOnDestroy = function () {
        this.workBench.removeDataFromLS('selectedDS');
    };
    ValidateSemanticComponent.prototype.showDSList = function () {
        this.router.navigate(['workbench', 'semantic', 'create']);
    };
    /**
     * Construct semantic layer field object structure.
     *
     * @param {*} dsData
     * @returns
     * @memberof ValidateSemanticComponent
     */
    ValidateSemanticComponent.prototype.injectFieldProperties = function (dsData) {
        var _this = this;
        lodash_forIn__WEBPACK_IMPORTED_MODULE_4__(dsData, function (value) {
            _this.isJoinEligible = value.storageType === 'ES' ? false : true;
            var artifactName = value.system.name;
            value.schema.fields = lodash_map__WEBPACK_IMPORTED_MODULE_5__(value.schema.fields, function (val) {
                var colName = val.isKeyword ? val.name + ".keyword" : val.name;
                return {
                    alias: val.name,
                    columnName: colName,
                    displayName: val.name,
                    filterEligible: true,
                    joinEligible: false,
                    kpiEligible: false,
                    include: true,
                    name: val.name,
                    table: artifactName,
                    type: _wb_comp_configs__WEBPACK_IMPORTED_MODULE_13__["TYPE_CONVERSION"][lodash_toLower__WEBPACK_IMPORTED_MODULE_6__(val.type)]
                };
            });
        });
        return dsData;
    };
    /**
     * Opens dialog for Semantic layer name( metric name) and constructs the Semantic layer structure with only mandatory parameters.
     *
     * @memberof ValidateSemanticComponent
     */
    ValidateSemanticComponent.prototype.createDatapod = function () {
        var _this = this;
        var dialogRef = this.dialog.open(_semantic_details_dialog_semantic_details_dialog_component__WEBPACK_IMPORTED_MODULE_10__["SemanticDetailsDialogComponent"], {
            hasBackdrop: true,
            autoFocus: true,
            closeOnNavigation: true,
            disableClose: true,
            height: 'auto',
            width: '350px'
        });
        dialogRef.afterClosed().subscribe(function (_a) {
            var name = _a.name, category = _a.category;
            if (lodash_trim__WEBPACK_IMPORTED_MODULE_9__(name).length > 0) {
                var payload_1 = {
                    category: category,
                    customerCode: '',
                    username: '',
                    projectCode: '',
                    metricName: '',
                    artifacts: [],
                    esRepository: { indexName: '', storageType: '', type: '' },
                    supports: [
                        { category: 'table', children: [], label: 'tables' },
                        { category: 'charts', children: [], label: 'charts' }
                    ],
                    parentDataSetNames: [],
                    parentDataSetIds: []
                };
                payload_1.metricName = name;
                lodash_forIn__WEBPACK_IMPORTED_MODULE_4__(_this.selectedDS, function (ds) {
                    if (ds.storageType === 'ES') {
                        payload_1.esRepository.indexName = ds.system.name;
                        payload_1.esRepository.storageType = 'ES';
                        payload_1.esRepository.type = ds.system.esIndexType;
                    }
                    payload_1.artifacts.push({
                        artifactName: ds.system.name,
                        columns: lodash_filter__WEBPACK_IMPORTED_MODULE_8__(ds.schema.fields, 'include')
                    });
                    payload_1.parentDataSetNames.push(ds.system.name);
                    payload_1.parentDataSetIds.push(lodash_split__WEBPACK_IMPORTED_MODULE_7__(ds._id, '::')[1]);
                });
                _this.workBench.createSemantic(payload_1).subscribe(function (data) {
                    _this.notify.info('Datapod created successfully', 'Datapod', {
                        hideDelay: 9000
                    });
                    _this.router.navigate(['workbench', 'dataobjects']);
                });
            }
        });
    };
    ValidateSemanticComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'validate-semantic',
            template: __webpack_require__(/*! ./validate-semantic.component.html */ "./src/app/modules/workbench/components/semantic-management/validate/validate-semantic.component.html"),
            styles: [__webpack_require__(/*! ./validate-semantic.component.scss */ "./src/app/modules/workbench/components/semantic-management/validate/validate-semantic.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"],
            _services_workbench_service__WEBPACK_IMPORTED_MODULE_12__["WorkbenchService"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatDialog"],
            _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_11__["ToastService"]])
    ], ValidateSemanticComponent);
    return ValidateSemanticComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/sql-executor/dataset-details-dialog/details-dialog.component.html":
/*!****************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/sql-executor/dataset-details-dialog/details-dialog.component.html ***!
  \****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<form [formGroup]=\"form\" (ngSubmit)=\"submit(form)\">\n  <mat-dialog-content class=\"content\">\n    <section class=\"input-section\">\n      <mat-form-field class=\"margin20\">\n        <input matInput placeholder=\"Dataset Name\" name=\"datasetName\" formControlName=\"nameControl\" maxlength=\"18\"\n          required />\n        <mat-error *ngIf=\"form.controls.nameControl.hasError('required')\">\n          Dataset Name is\n          <strong>required</strong>\n        </mat-error>\n        <mat-error *ngIf=\"form.controls.nameControl.hasError('pattern')\" i18n>\n          <strong> Only alphabets, numbers are allowed\n          </strong>\n        </mat-error>\n        <mat-hint> Only alphabets, numbers are allowed </mat-hint>\n      </mat-form-field>\n    </section>\n    <section class=\"input-section\">\n      <mat-form-field class=\"margin20\">\n        <input matInput placeholder=\"Description\" name=\"description\" formControlName=\"descControl\" maxlength=\"100\"\n          required />\n        <mat-error *ngIf=\"form.controls.descControl.hasError('required')\">\n          Dataset description is\n          <strong>required</strong>\n        </mat-error>\n      </mat-form-field>\n    </section>\n  </mat-dialog-content>\n  <mat-dialog-actions>\n    <button mat-stroked-button type=\"button\" color=\"warn\" (click)=\"onClose()\">Cancel</button>\n    <span fxFlex></span>\n    <button mat-raised-button type=\"submit\" color=\"primary\" [disabled]=\"!form.valid\">OK</button>\n  </mat-dialog-actions>\n</form>"

/***/ }),

/***/ "./src/app/modules/workbench/components/sql-executor/dataset-details-dialog/details-dialog.component.scss":
/*!****************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/sql-executor/dataset-details-dialog/details-dialog.component.scss ***!
  \****************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".margin20 {\n  margin: 3px;\n  width: 100%; }\n\n.input-section {\n  display: flex;\n  align-content: center;\n  align-items: center; }\n\n.content {\n  height: 180px; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL3NxbC1leGVjdXRvci9kYXRhc2V0LWRldGFpbHMtZGlhbG9nL2RldGFpbHMtZGlhbG9nLmNvbXBvbmVudC5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUVBO0VBQ0UsV0FBVztFQUNYLFdBQVcsRUFBQTs7QUFHYjtFQUNFLGFBQWE7RUFDYixxQkFBcUI7RUFDckIsbUJBQW1CLEVBQUE7O0FBR3JCO0VBQ0UsYUFBYSxFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy93b3JrYmVuY2gvY29tcG9uZW50cy9zcWwtZXhlY3V0b3IvZGF0YXNldC1kZXRhaWxzLWRpYWxvZy9kZXRhaWxzLWRpYWxvZy5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgXCJzcmMvdGhlbWVzL2Jhc2UvY29sb3JzXCI7XG5cbi5tYXJnaW4yMCB7XG4gIG1hcmdpbjogM3B4O1xuICB3aWR0aDogMTAwJTtcbn1cblxuLmlucHV0LXNlY3Rpb24ge1xuICBkaXNwbGF5OiBmbGV4O1xuICBhbGlnbi1jb250ZW50OiBjZW50ZXI7XG4gIGFsaWduLWl0ZW1zOiBjZW50ZXI7XG59XG5cbi5jb250ZW50IHtcbiAgaGVpZ2h0OiAxODBweDtcbn1cbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/workbench/components/sql-executor/dataset-details-dialog/details-dialog.component.ts":
/*!**************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/sql-executor/dataset-details-dialog/details-dialog.component.ts ***!
  \**************************************************************************************************************/
/*! exports provided: DetailsDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DetailsDialogComponent", function() { return DetailsDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");




var DetailsDialogComponent = /** @class */ (function () {
    function DetailsDialogComponent(formBuilder, dialogRef) {
        this.formBuilder = formBuilder;
        this.dialogRef = dialogRef;
        this.folNamePattern = '[A-Za-z0-9]+';
    }
    DetailsDialogComponent.prototype.ngOnInit = function () {
        this.form = this.formBuilder.group({
            nameControl: [
                '',
                [
                    _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required,
                    _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].pattern(this.folNamePattern),
                    _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].minLength(3),
                    _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].maxLength(18)
                ]
            ],
            descControl: [
                '',
                [_angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].required, _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].minLength(5), _angular_forms__WEBPACK_IMPORTED_MODULE_3__["Validators"].maxLength(50)]
            ]
        });
    };
    DetailsDialogComponent.prototype.submit = function (form) {
        var details = {
            name: form.value.nameControl,
            desc: form.value.descControl
        };
        this.dialogRef.close(details);
    };
    DetailsDialogComponent.prototype.onClose = function () {
        this.dialogRef.close(false);
    };
    DetailsDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'details-dialog',
            template: __webpack_require__(/*! ./details-dialog.component.html */ "./src/app/modules/workbench/components/sql-executor/dataset-details-dialog/details-dialog.component.html"),
            styles: [__webpack_require__(/*! ./details-dialog.component.scss */ "./src/app/modules/workbench/components/sql-executor/dataset-details-dialog/details-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_forms__WEBPACK_IMPORTED_MODULE_3__["FormBuilder"],
            _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"]])
    ], DetailsDialogComponent);
    return DetailsDialogComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/sql-executor/preview-grid/sqlpreview-grid-page.component.html":
/*!************************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/sql-executor/preview-grid/sqlpreview-grid-page.component.html ***!
  \************************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<dx-data-grid id=\"gridContainer\">\n  <div *dxTemplate=\"let data of 'toggleViewTemplate'\">\n    <div *ngIf=\"!fullScreen\">\n      <button mat-icon-button matTooltip=\"Full screen mode\" (click)=\"togglePreview(true)\">\n        <mat-icon fontIcon=\"icon-full-screen\" class=\"screen-icon\"></mat-icon>\n      </button>\n    </div>\n    <div *ngIf=\"fullScreen\">\n      <button mat-icon-button matTooltip=\"Exit full screen mode\" (click)=\"togglePreview(false)\">\n        <mat-icon fontIcon=\"icon-fullscreen-exit\" class=\"screen-icon\" style=\"font-size: 28px;\"></mat-icon>\n      </button>\n    </div>\n  </div>\n</dx-data-grid>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/sql-executor/preview-grid/sqlpreview-grid-page.component.ts":
/*!**********************************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/sql-executor/preview-grid/sqlpreview-grid-page.component.ts ***!
  \**********************************************************************************************************/
/*! exports provided: SqlpreviewGridPageComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlpreviewGridPageComponent", function() { return SqlpreviewGridPageComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! devextreme-angular/ui/data-grid */ "./node_modules/devextreme-angular/ui/data-grid.js");
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../../../common/services/dxDataGrid.service */ "./src/app/common/services/dxDataGrid.service.ts");





var SqlpreviewGridPageComponent = /** @class */ (function () {
    function SqlpreviewGridPageComponent(dxDataGrid) {
        this.dxDataGrid = dxDataGrid;
        this.fullScreen = false; // tslint:disable-line
        this.onToggleScreenMode = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
    }
    SqlpreviewGridPageComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.gridConfig = this.getGridConfig();
        this.updaterSubscribtion = this.updater.subscribe(function (data) {
            _this.onUpdate(data);
        });
    };
    SqlpreviewGridPageComponent.prototype.ngAfterViewInit = function () {
        this.dataGrid.instance.option(this.gridConfig);
    };
    SqlpreviewGridPageComponent.prototype.ngOnDestroy = function () {
        this.updaterSubscribtion.unsubscribe();
    };
    SqlpreviewGridPageComponent.prototype.onUpdate = function (data) {
        var _this = this;
        setTimeout(function () {
            _this.reloadDataGrid(data);
        });
    };
    SqlpreviewGridPageComponent.prototype.getGridConfig = function () {
        var dataSource = [];
        return this.dxDataGrid.mergeWithDefaultConfig({
            dataSource: dataSource,
            columnAutoWidth: false,
            wordWrapEnabled: false,
            searchPanel: {
                visible: true,
                width: 240,
                placeholder: 'Search...'
            },
            height: '100%',
            width: '100%',
            filterRow: {
                visible: true,
                applyFilter: 'auto'
            },
            headerFilter: {
                visible: true
            },
            sorting: {
                mode: 'none'
            },
            export: {
                fileName: 'Preview_Sample',
                enabled: false
            },
            scrolling: {
                showScrollbar: 'always',
                mode: 'virtual',
                useNative: false
            },
            showRowLines: false,
            showBorders: false,
            rowAlternationEnabled: false,
            showColumnLines: true,
            selection: {
                mode: 'none'
            },
            onToolbarPreparing: function (e) {
                e.toolbarOptions.items.unshift({
                    location: 'before',
                    template: 'toggleViewTemplate'
                });
            }
        });
    };
    SqlpreviewGridPageComponent.prototype.reloadDataGrid = function (data) {
        this.dataGrid.instance.option('dataSource', data);
        this.dataGrid.instance.refresh();
        this.dataGrid.instance.endCustomLoading();
    };
    SqlpreviewGridPageComponent.prototype.togglePreview = function (fullScrMode) {
        var _this = this;
        this.fullScreen = fullScrMode;
        this.onToggleScreenMode.emit(fullScrMode);
        setTimeout(function () {
            _this.dataGrid.instance.refresh();
        }, 100);
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_2__["BehaviorSubject"])
    ], SqlpreviewGridPageComponent.prototype, "updater", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ViewChild"])(devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3__["DxDataGridComponent"]),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_3__["DxDataGridComponent"])
    ], SqlpreviewGridPageComponent.prototype, "dataGrid", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], SqlpreviewGridPageComponent.prototype, "onToggleScreenMode", void 0);
    SqlpreviewGridPageComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'sqlpreview-grid-page',
            template: __webpack_require__(/*! ./sqlpreview-grid-page.component.html */ "./src/app/modules/workbench/components/sql-executor/preview-grid/sqlpreview-grid-page.component.html"),
            styles: [":host {\n    width: 100%;\n    height: 100%;\n  }"]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_4__["DxDataGridService"]])
    ], SqlpreviewGridPageComponent);
    return SqlpreviewGridPageComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/sql-executor/query/sql-script.component.html":
/*!*******************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/sql-executor/query/sql-script.component.html ***!
  \*******************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<ace-editor #editor\n  [(text)]=\"query\"\n  (textChanged)=\"queryUpdated($event)\"\n  [options]=\"editorOptions\"\n  [readOnly]=\"readOnlyMode\"\n  theme=\"sqlserver\"\n  mode=\"sql\">\n</ace-editor>"

/***/ }),

/***/ "./src/app/modules/workbench/components/sql-executor/query/sql-script.component.scss":
/*!*******************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/sql-executor/query/sql-script.component.scss ***!
  \*******************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  height: 100%;\n  width: 100%; }\n\ntextarea {\n  margin-bottom: 10px; }\n\n.ace_editor {\n  width: 100%;\n  height: 100% !important; }\n\n.query-actions {\n  display: flex;\n  flex-direction: row-reverse; }\n\n.ace-sqlserver {\n  background-color: #f5f9fc; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL3NxbC1leGVjdXRvci9xdWVyeS9zcWwtc2NyaXB0LmNvbXBvbmVudC5zY3NzIiwiL1VzZXJzL2Jhcm5hbXVtdHlhbi9Qcm9qZWN0cy9tb2R1cy9zaXAvc2F3LXdlYi9zcmMvdGhlbWVzL2Jhc2UvX2NvbG9ycy5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUVBO0VBQ0UsWUFBWTtFQUNaLFdBQVcsRUFBQTs7QUFHYjtFQUNFLG1CQUFtQixFQUFBOztBQUdyQjtFQUNFLFdBQVc7RUFDWCx1QkFBdUIsRUFBQTs7QUFHekI7RUFDRSxhQUFhO0VBQ2IsMkJBQTJCLEVBQUE7O0FBRzdCO0VBQ0UseUJDeUJ3QixFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy93b3JrYmVuY2gvY29tcG9uZW50cy9zcWwtZXhlY3V0b3IvcXVlcnkvc3FsLXNjcmlwdC5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgXCJzcmMvdGhlbWVzL2Jhc2UvY29sb3JzXCI7XG5cbjpob3N0IHtcbiAgaGVpZ2h0OiAxMDAlO1xuICB3aWR0aDogMTAwJTtcbn1cblxudGV4dGFyZWEge1xuICBtYXJnaW4tYm90dG9tOiAxMHB4O1xufVxuXG4uYWNlX2VkaXRvciB7XG4gIHdpZHRoOiAxMDAlO1xuICBoZWlnaHQ6IDEwMCUgIWltcG9ydGFudDtcbn1cblxuLnF1ZXJ5LWFjdGlvbnMge1xuICBkaXNwbGF5OiBmbGV4O1xuICBmbGV4LWRpcmVjdGlvbjogcm93LXJldmVyc2U7XG59XG5cbi5hY2Utc3Fsc2VydmVyIHtcbiAgYmFja2dyb3VuZC1jb2xvcjogJGJhY2tncm91bmQtY29sb3I7XG59XG4iLCIvLyBCcmFuZGluZyBjb2xvcnNcbiRwcmltYXJ5LWJsdWUtYjE6ICMxYTg5ZDQ7XG4kcHJpbWFyeS1ibHVlLWIyOiAjMDA3N2JlO1xuJHByaW1hcnktYmx1ZS1iMzogIzIwNmJjZTtcbiRwcmltYXJ5LWJsdWUtYjQ6ICMxZDNhYjI7XG5cbiRwcmltYXJ5LWhvdmVyLWJsdWU6ICMxZDYxYjE7XG4kZ3JpZC1ob3Zlci1jb2xvcjogI2Y1ZjlmYztcbiRncmlkLWhlYWRlci1iZy1jb2xvcjogI2Q3ZWFmYTtcbiRncmlkLWhlYWRlci1jb2xvcjogIzBiNGQ5OTtcbiRncmlkLXRleHQtY29sb3I6ICM0NjQ2NDY7XG4kZ3JleS10ZXh0LWNvbG9yOiAjNjM2MzYzO1xuXG4kc2VsZWN0aW9uLWhpZ2hsaWdodC1jb2w6IHJnYmEoMCwgMTQwLCAyNjAsIDAuMik7XG4kcHJpbWFyeS1ncmV5LWcxOiAjZDFkM2QzO1xuJHByaW1hcnktZ3JleS1nMjogIzk5OTtcbiRwcmltYXJ5LWdyZXktZzM6ICM3MzczNzM7XG4kcHJpbWFyeS1ncmV5LWc0OiAjNWM2NjcwO1xuJHByaW1hcnktZ3JleS1nNTogIzMxMzEzMTtcbiRwcmltYXJ5LWdyZXktZzY6ICNmNWY1ZjU7XG4kcHJpbWFyeS1ncmV5LWc3OiAjM2QzZDNkO1xuXG4kcHJpbWFyeS13aGl0ZTogI2ZmZjtcbiRwcmltYXJ5LWJsYWNrOiAjMDAwO1xuJHByaW1hcnktcmVkOiAjYWIwZTI3O1xuJHByaW1hcnktZ3JlZW46ICM3M2I0MjE7XG4kcHJpbWFyeS1vcmFuZ2U6ICNmMDc2MDE7XG5cbiRzZWNvbmRhcnktZ3JlZW46ICM2ZmIzMjA7XG4kc2Vjb25kYXJ5LXllbGxvdzogI2ZmYmUwMDtcbiRzZWNvbmRhcnktb3JhbmdlOiAjZmY5MDAwO1xuJHNlY29uZGFyeS1yZWQ6ICNkOTNlMDA7XG4kc2Vjb25kYXJ5LWJlcnJ5OiAjYWMxNDVhO1xuJHNlY29uZGFyeS1wdXJwbGU6ICM5MTQxOTE7XG5cbiRzdHJpbmctdHlwZS1jb2xvcjogIzQ5OTViMjtcbiRudW1iZXItdHlwZS1jb2xvcjogIzAwYjE4MDtcbiRnZW8tdHlwZS1jb2xvcjogIzg0NWVjMjtcbiRkYXRlLXR5cGUtY29sb3I6ICNkMTk2MjE7XG5cbiR0eXBlLWNoaXAtb3BhY2l0eTogMTtcbiRzdHJpbmctdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRzdHJpbmctdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRudW1iZXItdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRudW1iZXItdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRnZW8tdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRnZW8tdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRkYXRlLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZGF0ZS10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuXG4kcmVwb3J0LWRlc2lnbmVyLXNldHRpbmdzLWJnLWNvbG9yOiAjZjVmOWZjO1xuJGJhY2tncm91bmQtY29sb3I6ICNmNWY5ZmM7XG4iXX0= */"

/***/ }),

/***/ "./src/app/modules/workbench/components/sql-executor/query/sql-script.component.ts":
/*!*****************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/sql-executor/query/sql-script.component.ts ***!
  \*****************************************************************************************/
/*! exports provided: SqlScriptComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlScriptComponent", function() { return SqlScriptComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! lodash/isEmpty */ "./node_modules/lodash/isEmpty.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(lodash_isEmpty__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/cloneDeep */ "./node_modules/lodash/cloneDeep.js");
/* harmony import */ var lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/map */ "./node_modules/lodash/map.js");
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_map__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var ng2_ace_editor__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ng2-ace-editor */ "./node_modules/ng2-ace-editor/index.js");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");







var SqlScriptComponent = /** @class */ (function () {
    function SqlScriptComponent(workBench) {
        this.workBench = workBench;
        this.onExecute = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.onCreate = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.readOnlyMode = false; // tslint:disable-line
        this.editorOptions = {
            // tslint:disable-line
            displayIndentGuides: true,
            enableBasicAutocompletion: true,
            enableLiveAutocompletion: true,
            showPrintMargin: false,
            maxLines: Infinity,
            fontSize: '100%',
            wrap: 'free',
            wrapBehavioursEnabled: true,
            cursorStyle: 'ace'
        };
        this.langTools = ace.require('ace/ext/language_tools');
        this.completions = [];
    }
    SqlScriptComponent.prototype.ngAfterViewInit = function () {
        var _this = this;
        setTimeout(function () {
            _this.editor.getEditor().focus();
            _this.editor.getEditor().resize();
        }, 100);
    };
    /* Before exiting, reset ace completers to default.
       This removes any custom completers added to ace. */
    SqlScriptComponent.prototype.ngOnDestroy = function () {
        this.langTools.setCompleters([
            this.langTools.snippetCompleter,
            this.langTools.textCompleter,
            this.langTools.keyWordCompleter
        ]);
    };
    Object.defineProperty(SqlScriptComponent.prototype, "artifacts", {
        set: function (tables) {
            if (!lodash_isEmpty__WEBPACK_IMPORTED_MODULE_2__(tables)) {
                this._artifacts = tables;
                this.generateCompletions();
                this.addCompletionsToEditor();
            }
        },
        enumerable: true,
        configurable: true
    });
    SqlScriptComponent.prototype.generateCompletions = function () {
        var _this = this;
        this._artifacts.forEach(function (table) {
            _this.completions.push({
                name: table.artifactName,
                value: table.artifactName,
                meta: 'table',
                score: 1001
            });
            table.columns.forEach(function (column) {
                var caption = column.name;
                _this.completions.push({
                    name: caption,
                    value: caption,
                    caption: caption,
                    meta: 'column',
                    score: 1000,
                    /* Custom attribute stores column name.
                    This is used to insert this string when matched instead
                    of 'value' attribute of this completion. */
                    insertValue: caption
                });
            });
        });
    };
    SqlScriptComponent.prototype.addCompletionsToEditor = function () {
        var self = this;
        var artifactsCompleter = {
            getCompletions: function (editor, session, pos, prefix, callback) {
                /* Add reference to this completer in each match. Ace editor
                uses this reference to call the custom 'insertMatch' method of
                this completer. */
                var withCompleter = lodash_map__WEBPACK_IMPORTED_MODULE_4__(self.completions, function (completion) {
                    completion.completer = artifactsCompleter;
                    return completion;
                });
                if (prefix.length === 0) {
                    return callback(null, lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_3__(withCompleter));
                }
                var matchingCompletions = withCompleter.filter(function (match) {
                    return (match.caption || match.name)
                        .toLowerCase()
                        .indexOf(prefix.toLowerCase()) >= 0;
                });
                return callback(null, lodash_cloneDeep__WEBPACK_IMPORTED_MODULE_3__(matchingCompletions));
            },
            insertMatch: function (editor, data) {
                editor.completer.insertMatch({
                    value: data.insertValue || data.value || data
                });
            }
        };
        this.langTools.addCompleter(artifactsCompleter);
    };
    SqlScriptComponent.prototype.queryUpdated = function (query) { };
    SqlScriptComponent.prototype.onCreateEmitter = function () {
        this.onCreate.emit(this.query);
    };
    /**
     * Executes the queryy against the datset and emits the result to preview component.
     *
     * @memberof SqlScriptComponent
     */
    SqlScriptComponent.prototype.executeQuery = function () {
        var _this = this;
        this.workBench.executeSqlQuery(this.query).subscribe(function (data) {
            _this.onExecute.emit(data);
        });
    };
    /**
     * Shows the preview of an action from list in editor.
     * shifts the editor in readonly mode.
     *
     * @param {string} action
     * @memberof SqlScriptComponent
     */
    SqlScriptComponent.prototype.viewAction = function (action) {
        this.readOnlyMode = true;
        this.query = action;
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], SqlScriptComponent.prototype, "onExecute", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], SqlScriptComponent.prototype, "onCreate", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ViewChild"])('editor'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", ng2_ace_editor__WEBPACK_IMPORTED_MODULE_5__["AceEditorComponent"])
    ], SqlScriptComponent.prototype, "editor", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object])
    ], SqlScriptComponent.prototype, "artifacts", null);
    SqlScriptComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'sql-script',
            template: __webpack_require__(/*! ./sql-script.component.html */ "./src/app/modules/workbench/components/sql-executor/query/sql-script.component.html"),
            styles: [__webpack_require__(/*! ./sql-script.component.scss */ "./src/app/modules/workbench/components/sql-executor/query/sql-script.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_services_workbench_service__WEBPACK_IMPORTED_MODULE_6__["WorkbenchService"]])
    ], SqlScriptComponent);
    return SqlScriptComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/sql-executor/sql-executor.component.html":
/*!***************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/sql-executor/sql-executor.component.html ***!
  \***************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"executor-container\">\n  <div class=\"title-elem\" fxLayout=\"row\" fxLayoutAlign=\"center center\">\n    <button mat-button class=\"bck-btn\" e2e=\"back-to-data-sets-page\">\n      <mat-icon fontIcon=\"icon-arrow-left\" (click)=\"backToDS()\"></mat-icon>\n    </button>\n    <div class=\"action-title\">\n      <mat-icon fontIcon=\"icon-query-mode\"></mat-icon>\n      <span>SQL EXECUTOR </span>\n    </div>\n    <div class=\"ds-name\" fxFlex fxLayout=\"row\" fxLayoutAlign=\"start center\" fxLayoutGap=\"10px\">\n      <span>Selected Dataset: </span>\n      <mat-chip-list>\n        <mat-chip>{{dsMetadata.system.name}}</mat-chip>\n      </mat-chip-list>\n    </div>\n    <div fxLayout=\"row\" fxLayoutGap=\"20px\" fxFlexAlign=\"end center\">\n      <!-- <div class=\"btn-icon\">\n        <button mat-button matTooltip=\"Execute Query\" (click)=\"runScript()\">\n          <mat-icon fontIcon=\"icon-play-circle\"></mat-icon>\n          <span>Run</span>\n        </button>\n      </div>\n      <div class=\"btn-icon\">\n        <button mat-button matTooltip=\"Add to list\" (click)=\"addScript()\">\n          <mat-icon fontIcon=\"icon-clipboard\"></mat-icon>\n          <span>Add</span>\n        </button>\n      </div>\n      <div class=\"btn-icon\">\n        <button mat-button matTooltip=\"New Query\" (click)=\"newScript()\">\n          <mat-icon fontIcon=\"icon-soup-solid\"></mat-icon>\n          <span>New</span>\n        </button>\n      </div> -->\n      <div class=\"create-btn\">\n        <button mat-raised-button color=\"primary\" (click)=\"openSaveDialog()\">\n          <!-- <mat-icon fontIcon=\"icon-save\"></mat-icon> -->\n          <span i18n>Create</span>\n        </button>\n      </div>\n    </div>\n  </div>\n  <div class=\"exec-body\">\n    <as-split direction=\"vertical\">\n      <as-split-area class=\"script-pane\" [size]=\"scriptHeight\">\n        <as-split direction=\"horizontal\">\n          <as-split-area size=\"75\">\n            <sql-script #sqlscript [artifacts]=\"artifacts\" (onExecute)=\"sendDataToPreview($event)\" (onCreate)=\"getQuery($event)\">\n            </sql-script>\n          </as-split-area>\n          <as-split-area size=\"25\" class=\"fields-pane\">\n            <mat-card class=\"details-grid\" fxFlex>\n              <div fxFill>\n                <dx-data-grid fxFlex [dataSource]=\"dsMetadata?.schema?.fields\" [rowAlternationEnabled]=\"true\" [height]=\"'99%'\"\n                  [width]=\"'100%'\" [showBorders]=\"false\" style=\"position:absolute;top:0;bottom:0;left:0;bottom:0;\">\n                  <dxi-column caption=\"Field Name\" dataField=\"name\"></dxi-column>\n                  <dxi-column caption=\"Data Type\" dataField=\"type\"></dxi-column>\n                  <dxo-scrolling mode=\"infinite\"></dxo-scrolling>\n                  <dxo-filter-row [visible]=\"true\" applyFilter=\"auto\"></dxo-filter-row>\n                  <dxo-header-filter [visible]=\"true\"></dxo-header-filter>\n                </dx-data-grid>\n              </div>\n            </mat-card>\n            <!-- <mat-list>\n              <div fxLayout=\"row\" fxLayoutAlign=\"start start\">\n                <h3 mat-subheader fxFlex fxLayout=\"row\" fxLayoutAlign=\"start start\">\n                  <mat-icon fontIcon=\"icon-clipboard\"></mat-icon>\n                  <span>ACTIONS_LIST</span>\n                </h3>\n              </div>\n              <mat-list-item class=\"list-items\" *ngFor=\"let action of appliedActions; index as i; last as isLast\" (click)=\"previewAction(action.statement)\">\n                <p mat-line>{{i + 1}}. &ensp; {{ action.statement }}</p>\n                <div class=\"icon-div\" *ngIf=\"isLast\">\n                  <mat-icon mat-list-icon style=\"color: #E5524C;\" fontIcon=\"icon-delete\"></mat-icon>\n                </div>\n                <mat-divider></mat-divider>\n              </mat-list-item>\n            </mat-list> -->\n          </as-split-area>\n        </as-split>\n      </as-split-area>\n      <as-split-area class=\"preview-pane\" [size]=\"previewHeight\">\n        <!-- <sqlpreview-grid-page [updater]=\"gridData\" (onToggleScreenMode)=\"toggleViewMode($event)\"></sqlpreview-grid-page> -->\n      </as-split-area>\n    </as-split>\n  </div>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/sql-executor/sql-executor.component.scss":
/*!***************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/sql-executor/sql-executor.component.scss ***!
  \***************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%; }\n\n.executor-container {\n  width: 100%;\n  height: calc(100% - 10px);\n  padding: 3px;\n  background-color: #f5f9fc; }\n\n.executor-container .ds-name {\n    font-size: 16px;\n    padding-left: 5%; }\n\n.executor-container .bck-btn {\n    min-width: 50px;\n    height: 32px; }\n\n.executor-container .details-grid {\n    padding: 3px; }\n\n.executor-container .action-title {\n    color: #0077be;\n    font-size: 18px; }\n\n.executor-container .action-title mat-icon {\n      vertical-align: middle; }\n\n.executor-container .gridContainer {\n    border-right: 3px #f5f9fc solid;\n    padding-top: 3px; }\n\n.executor-container .exec-body {\n    height: calc(100% - 50px);\n    max-height: calc(100% - 50px); }\n\n.executor-container .script-pane {\n    overflow: auto; }\n\n.executor-container .fields-pane {\n    background-color: #FFF; }\n\n.executor-container .preview-pane {\n    overflow: auto; }\n\n.executor-container .exec-btn-icon {\n    font-size: 24px; }\n\n.executor-container .list-items {\n    background-color: white;\n    border-radius: 0 3px 3px;\n    border: #0077be 0.5px dotted; }\n\n.executor-container .list-items:hover {\n      box-shadow: 0 11px 15px -7px rgba(0, 0, 0, 0.2), 0 24px 38px 3px rgba(0, 0, 0, 0.14), 0 9px 46px 8px rgba(0, 0, 0, 0.12) !important;\n      cursor: pointer; }\n\n.executor-container .btn-icon {\n    color: #0077be; }\n\n.executor-container .btn-icon .mat-button {\n      vertical-align: middle;\n      line-height: 32px !important;\n      height: 32px !important;\n      border-radius: 2px;\n      background-color: white;\n      border: solid 1px #0077be; }\n\n.executor-container .btn-icon .mat-button .mat-button-wrapper > * {\n      vertical-align: initial; }\n\n.executor-container .create-btn > .mat-raised-button {\n    line-height: 32px !important; }\n\n.executor-container .title-elem {\n    color: rgba(0, 0, 0, 0.54);\n    background: rgba(0, 0, 0, 0.03);\n    padding: 8px 0;\n    height: 50px;\n    font-size: 18px;\n    font-weight: 500;\n    border: 2px solid white; }\n\n.executor-container .title-elem .mat-icon {\n      font-size: 20px; }\n\n.executor-container .title-elem .mat-chip {\n      color: rgba(0, 0, 0, 0.54);\n      font-size: 16px; }\n\n.executor-container .title-elem .status-icon {\n      width: 36px; }\n\n.executor-container .title-elem .status-icon .mat-icon {\n        font-size: 24px;\n        vertical-align: middle; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL3NxbC1leGVjdXRvci9zcWwtZXhlY3V0b3IuY29tcG9uZW50LnNjc3MiLCIvVXNlcnMvYmFybmFtdW10eWFuL1Byb2plY3RzL21vZHVzL3NpcC9zYXctd2ViL3NyYy90aGVtZXMvYmFzZS9fY29sb3JzLnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSxXQUFXLEVBQUE7O0FBR2I7RUFDRSxXQUFXO0VBQ1gseUJBQXlCO0VBQ3pCLFlBQVk7RUFDWix5QkNxQ3dCLEVBQUE7O0FEekMxQjtJQU9JLGVBQWU7SUFDZixnQkFBZ0IsRUFBQTs7QUFScEI7SUFZSSxlQUFlO0lBQ2YsWUFBWSxFQUFBOztBQWJoQjtJQWlCSSxZQUFZLEVBQUE7O0FBakJoQjtJQXFCSSxjQ3pCcUI7SUQwQnJCLGVBQWUsRUFBQTs7QUF0Qm5CO01BeUJNLHNCQUFzQixFQUFBOztBQXpCNUI7SUE4QkksK0JBQXlDO0lBQ3pDLGdCQUFnQixFQUFBOztBQS9CcEI7SUFtQ0kseUJBQXlCO0lBQ3pCLDZCQUE2QixFQUFBOztBQXBDakM7SUF3Q0ksY0FBYyxFQUFBOztBQXhDbEI7SUE0Q0ksc0JBQXNCLEVBQUE7O0FBNUMxQjtJQWdESSxjQUFjLEVBQUE7O0FBaERsQjtJQW9ESSxlQUFlLEVBQUE7O0FBcERuQjtJQXdESSx1QkFBdUI7SUFDdkIsd0JBQXdCO0lBQ3hCLDRCQUFxQyxFQUFBOztBQTFEekM7TUE2RE0sbUlBQW1JO01BQ25JLGVBQWUsRUFBQTs7QUE5RHJCO0lBbUVJLGNDdkVxQixFQUFBOztBREl6QjtNQXNFTSxzQkFBc0I7TUFDdEIsNEJBQTRCO01BQzVCLHVCQUF1QjtNQUN2QixrQkFBa0I7TUFDbEIsdUJBQXVCO01BQ3ZCLHlCQy9FbUIsRUFBQTs7QURJekI7TUErRU0sdUJBQXVCLEVBQUE7O0FBL0U3QjtJQW9GSSw0QkFBNEIsRUFBQTs7QUFwRmhDO0lBd0ZJLDBCQUEwQjtJQUMxQiwrQkFBK0I7SUFDL0IsY0FBYztJQUNkLFlBQVk7SUFDWixlQUFlO0lBQ2YsZ0JBQWdCO0lBQ2hCLHVCQUF1QixFQUFBOztBQTlGM0I7TUFpR00sZUFBZSxFQUFBOztBQWpHckI7TUFxR00sMEJBQTBCO01BQzFCLGVBQWUsRUFBQTs7QUF0R3JCO01BMEdNLFdBQVcsRUFBQTs7QUExR2pCO1FBNkdRLGVBQWU7UUFDZixzQkFBc0IsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvd29ya2JlbmNoL2NvbXBvbmVudHMvc3FsLWV4ZWN1dG9yL3NxbC1leGVjdXRvci5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgXCJzcmMvdGhlbWVzL2Jhc2UvY29sb3JzXCI7XG5cbjpob3N0IHtcbiAgd2lkdGg6IDEwMCU7XG59XG5cbi5leGVjdXRvci1jb250YWluZXIge1xuICB3aWR0aDogMTAwJTtcbiAgaGVpZ2h0OiBjYWxjKDEwMCUgLSAxMHB4KTtcbiAgcGFkZGluZzogM3B4O1xuICBiYWNrZ3JvdW5kLWNvbG9yOiAkYmFja2dyb3VuZC1jb2xvcjtcblxuICAuZHMtbmFtZSB7XG4gICAgZm9udC1zaXplOiAxNnB4O1xuICAgIHBhZGRpbmctbGVmdDogNSU7XG4gIH1cblxuICAuYmNrLWJ0biB7XG4gICAgbWluLXdpZHRoOiA1MHB4O1xuICAgIGhlaWdodDogMzJweDtcbiAgfVxuXG4gIC5kZXRhaWxzLWdyaWQge1xuICAgIHBhZGRpbmc6IDNweDtcbiAgfVxuXG4gIC5hY3Rpb24tdGl0bGUge1xuICAgIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIyO1xuICAgIGZvbnQtc2l6ZTogMThweDtcblxuICAgIG1hdC1pY29uIHtcbiAgICAgIHZlcnRpY2FsLWFsaWduOiBtaWRkbGU7XG4gICAgfVxuICB9XG5cbiAgLmdyaWRDb250YWluZXIge1xuICAgIGJvcmRlci1yaWdodDogM3B4ICRiYWNrZ3JvdW5kLWNvbG9yIHNvbGlkO1xuICAgIHBhZGRpbmctdG9wOiAzcHg7XG4gIH1cblxuICAuZXhlYy1ib2R5IHtcbiAgICBoZWlnaHQ6IGNhbGMoMTAwJSAtIDUwcHgpO1xuICAgIG1heC1oZWlnaHQ6IGNhbGMoMTAwJSAtIDUwcHgpO1xuICB9XG5cbiAgLnNjcmlwdC1wYW5lIHtcbiAgICBvdmVyZmxvdzogYXV0bztcbiAgfVxuXG4gIC5maWVsZHMtcGFuZSB7XG4gICAgYmFja2dyb3VuZC1jb2xvcjogI0ZGRjtcbiAgfVxuXG4gIC5wcmV2aWV3LXBhbmUge1xuICAgIG92ZXJmbG93OiBhdXRvO1xuICB9XG5cbiAgLmV4ZWMtYnRuLWljb24ge1xuICAgIGZvbnQtc2l6ZTogMjRweDtcbiAgfVxuXG4gIC5saXN0LWl0ZW1zIHtcbiAgICBiYWNrZ3JvdW5kLWNvbG9yOiB3aGl0ZTtcbiAgICBib3JkZXItcmFkaXVzOiAwIDNweCAzcHg7XG4gICAgYm9yZGVyOiAkcHJpbWFyeS1ibHVlLWIyIDAuNXB4IGRvdHRlZDtcblxuICAgICY6aG92ZXIge1xuICAgICAgYm94LXNoYWRvdzogMCAxMXB4IDE1cHggLTdweCByZ2JhKDAsIDAsIDAsIDAuMiksIDAgMjRweCAzOHB4IDNweCByZ2JhKDAsIDAsIDAsIDAuMTQpLCAwIDlweCA0NnB4IDhweCByZ2JhKDAsIDAsIDAsIDAuMTIpICFpbXBvcnRhbnQ7XG4gICAgICBjdXJzb3I6IHBvaW50ZXI7XG4gICAgfVxuICB9XG5cbiAgLmJ0bi1pY29uIHtcbiAgICBjb2xvcjogJHByaW1hcnktYmx1ZS1iMjtcblxuICAgIC5tYXQtYnV0dG9uIHtcbiAgICAgIHZlcnRpY2FsLWFsaWduOiBtaWRkbGU7XG4gICAgICBsaW5lLWhlaWdodDogMzJweCAhaW1wb3J0YW50O1xuICAgICAgaGVpZ2h0OiAzMnB4ICFpbXBvcnRhbnQ7XG4gICAgICBib3JkZXItcmFkaXVzOiAycHg7XG4gICAgICBiYWNrZ3JvdW5kLWNvbG9yOiB3aGl0ZTtcbiAgICAgIGJvcmRlcjogc29saWQgMXB4ICRwcmltYXJ5LWJsdWUtYjI7XG4gICAgfVxuXG4gICAgLm1hdC1idXR0b24gLm1hdC1idXR0b24td3JhcHBlciA+ICoge1xuICAgICAgdmVydGljYWwtYWxpZ246IGluaXRpYWw7XG4gICAgfVxuICB9XG5cbiAgLmNyZWF0ZS1idG4gPiAubWF0LXJhaXNlZC1idXR0b24ge1xuICAgIGxpbmUtaGVpZ2h0OiAzMnB4ICFpbXBvcnRhbnQ7XG4gIH1cblxuICAudGl0bGUtZWxlbSB7XG4gICAgY29sb3I6IHJnYmEoMCwgMCwgMCwgMC41NCk7XG4gICAgYmFja2dyb3VuZDogcmdiYSgwLCAwLCAwLCAwLjAzKTtcbiAgICBwYWRkaW5nOiA4cHggMDtcbiAgICBoZWlnaHQ6IDUwcHg7XG4gICAgZm9udC1zaXplOiAxOHB4O1xuICAgIGZvbnQtd2VpZ2h0OiA1MDA7XG4gICAgYm9yZGVyOiAycHggc29saWQgd2hpdGU7XG5cbiAgICAubWF0LWljb24ge1xuICAgICAgZm9udC1zaXplOiAyMHB4O1xuICAgIH1cblxuICAgIC5tYXQtY2hpcCB7XG4gICAgICBjb2xvcjogcmdiYSgwLCAwLCAwLCAwLjU0KTtcbiAgICAgIGZvbnQtc2l6ZTogMTZweDtcbiAgICB9XG5cbiAgICAuc3RhdHVzLWljb24ge1xuICAgICAgd2lkdGg6IDM2cHg7XG5cbiAgICAgIC5tYXQtaWNvbiB7XG4gICAgICAgIGZvbnQtc2l6ZTogMjRweDtcbiAgICAgICAgdmVydGljYWwtYWxpZ246IG1pZGRsZTtcbiAgICAgIH1cbiAgICB9XG4gIH1cbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/workbench/components/sql-executor/sql-executor.component.ts":
/*!*************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/sql-executor/sql-executor.component.ts ***!
  \*************************************************************************************/
/*! exports provided: SqlExecutorComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SqlExecutorComponent", function() { return SqlExecutorComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../../common/services/toastMessage.service */ "./src/app/common/services/toastMessage.service.ts");
/* harmony import */ var lodash_endsWith__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/endsWith */ "./node_modules/lodash/endsWith.js");
/* harmony import */ var lodash_endsWith__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_endsWith__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var _sample_data__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../sample-data */ "./src/app/modules/workbench/sample-data.ts");
/* harmony import */ var _query_sql_script_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./query/sql-script.component */ "./src/app/modules/workbench/components/sql-executor/query/sql-script.component.ts");
/* harmony import */ var _dataset_details_dialog_details_dialog_component__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./dataset-details-dialog/details-dialog.component */ "./src/app/modules/workbench/components/sql-executor/dataset-details-dialog/details-dialog.component.ts");
/* harmony import */ var _services_workbench_service__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../../services/workbench.service */ "./src/app/modules/workbench/services/workbench.service.ts");











var SqlExecutorComponent = /** @class */ (function () {
    function SqlExecutorComponent(router, dialog, workBench, notify) {
        this.router = router;
        this.dialog = dialog;
        this.workBench = workBench;
        this.notify = notify;
        this.artifacts = [];
        this.gridData = new rxjs__WEBPACK_IMPORTED_MODULE_4__["BehaviorSubject"]([]);
        this.appliedActions = _sample_data__WEBPACK_IMPORTED_MODULE_7__["SQL_AQCTIONS"];
        this.scriptHeight = 100;
        this.previewHeight = 0;
        this.query = '';
    }
    SqlExecutorComponent.prototype.ngOnInit = function () {
        this.getPageData();
    };
    SqlExecutorComponent.prototype.ngOnDestroy = function () {
        this.workBench.removeDataFromLS('dsMetadata');
    };
    SqlExecutorComponent.prototype.getPageData = function () {
        this.dsMetadata = this.workBench.getDataFromLS('dsMetadata');
        this.constructArtifactForEditor();
        // this.workBench.getDatasetDetails('this.datasetID').subscribe(data => {
        //   this.artifacts = data.artifacts;
        // });
    };
    SqlExecutorComponent.prototype.constructArtifactForEditor = function () {
        var table = {
            artifactName: this.dsMetadata.system.name,
            columns: this.dsMetadata.schema.fields
        };
        this.artifacts.push(table);
    };
    SqlExecutorComponent.prototype.runScript = function () {
        this.scriptComponent.executeQuery();
    };
    SqlExecutorComponent.prototype.sendDataToPreview = function (data) {
        this.gridData.next(data);
    };
    SqlExecutorComponent.prototype.getQuery = function (data) {
        this.query = data;
    };
    SqlExecutorComponent.prototype.openSaveDialog = function () {
        var _this = this;
        var detailsDialogRef = this.dialog.open(_dataset_details_dialog_details_dialog_component__WEBPACK_IMPORTED_MODULE_9__["DetailsDialogComponent"], {
            hasBackdrop: false,
            width: '400px',
            height: '300px'
        });
        detailsDialogRef.afterClosed().subscribe(function (data) {
            if (data !== false) {
                _this.datasetDetails = data;
                _this.scriptComponent.onCreateEmitter();
                _this.triggerSQL(data);
            }
        });
    };
    SqlExecutorComponent.prototype.toggleViewMode = function (fullScreenPreview) {
        this.previewHeight = fullScreenPreview ? 100 : 60;
        this.scriptHeight = fullScreenPreview ? 0 : 40;
    };
    /**
     * Constructs the payload for SQL executor component.
     *
     * @param {any} data
     * @memberof SqlExecutorComponent
     */
    SqlExecutorComponent.prototype.triggerSQL = function (data) {
        var _this = this;
        /**
         * Temporary workaround to construct user friendly SQL script.
         * Will be handled in BE in upcoming release
         */
        var appendedScript = "CREATE TABLE " + data.name + " AS " + this.query;
        var script = lodash_endsWith__WEBPACK_IMPORTED_MODULE_6__(appendedScript, ';') === true
            ? "" + appendedScript
            : appendedScript + ";";
        var payload = {
            name: data.name,
            input: this.dsMetadata.system.name,
            component: 'sql',
            configuration: {
                script: script
            }
        };
        this.workBench.triggerParser(payload).subscribe(function (_) {
            _this.notify.info('SQL_Executor_triggered_successfully', 'Creating Dataset', { hideDelay: 9000 });
        });
        this.router.navigate(['workbench', 'dataobjects']);
    };
    SqlExecutorComponent.prototype.previewAction = function (action) {
        this.scriptComponent.viewAction(action);
    };
    SqlExecutorComponent.prototype.backToDS = function () {
        this.router.navigate(['workbench', 'dataobjects']);
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ViewChild"])('sqlscript'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _query_sql_script_component__WEBPACK_IMPORTED_MODULE_8__["SqlScriptComponent"])
    ], SqlExecutorComponent.prototype, "scriptComponent", void 0);
    SqlExecutorComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'sql-executor',
            template: __webpack_require__(/*! ./sql-executor.component.html */ "./src/app/modules/workbench/components/sql-executor/sql-executor.component.html"),
            styles: [__webpack_require__(/*! ./sql-executor.component.scss */ "./src/app/modules/workbench/components/sql-executor/sql-executor.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_router__WEBPACK_IMPORTED_MODULE_3__["Router"],
            _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialog"],
            _services_workbench_service__WEBPACK_IMPORTED_MODULE_10__["WorkbenchService"],
            _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_5__["ToastService"]])
    ], SqlExecutorComponent);
    return SqlExecutorComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/components/workbench-page/workbench-page.component.html":
/*!*******************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/workbench-page/workbench-page.component.html ***!
  \*******************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<router-outlet></router-outlet>\n"

/***/ }),

/***/ "./src/app/modules/workbench/components/workbench-page/workbench-page.component.scss":
/*!*******************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/workbench-page/workbench-page.component.scss ***!
  \*******************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%;\n  max-width: 100% !important;\n  height: 100%;\n  max-height: 100%;\n  overflow: auto;\n  background-color: #f5f9fc; }\n\n.margin-btm-9 {\n  margin-bottom: 9px !important; }\n\n.dx-freespace-row {\n  height: 0 !important; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL3dvcmtiZW5jaC9jb21wb25lbnRzL3dvcmtiZW5jaC1wYWdlL3dvcmtiZW5jaC1wYWdlLmNvbXBvbmVudC5zY3NzIiwiL1VzZXJzL2Jhcm5hbXVtdHlhbi9Qcm9qZWN0cy9tb2R1cy9zaXAvc2F3LXdlYi9zcmMvdGhlbWVzL2Jhc2UvX2NvbG9ycy5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUVBO0VBQ0UsV0FBVztFQUNYLDBCQUEwQjtFQUMxQixZQUFZO0VBQ1osZ0JBQWdCO0VBQ2hCLGNBQWM7RUFDZCx5QkN1Q3dCLEVBQUE7O0FEcEMxQjtFQUNFLDZCQUE2QixFQUFBOztBQUcvQjtFQUNFLG9CQUFvQixFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy93b3JrYmVuY2gvY29tcG9uZW50cy93b3JrYmVuY2gtcGFnZS93b3JrYmVuY2gtcGFnZS5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIkBpbXBvcnQgJ3NyYy90aGVtZXMvYmFzZS9jb2xvcnMnO1xuXG46aG9zdCB7XG4gIHdpZHRoOiAxMDAlO1xuICBtYXgtd2lkdGg6IDEwMCUgIWltcG9ydGFudDtcbiAgaGVpZ2h0OiAxMDAlO1xuICBtYXgtaGVpZ2h0OiAxMDAlO1xuICBvdmVyZmxvdzogYXV0bztcbiAgYmFja2dyb3VuZC1jb2xvcjogJGJhY2tncm91bmQtY29sb3I7XG59XG5cbi5tYXJnaW4tYnRtLTkge1xuICBtYXJnaW4tYm90dG9tOiA5cHggIWltcG9ydGFudDtcbn1cblxuLmR4LWZyZWVzcGFjZS1yb3cge1xuICBoZWlnaHQ6IDAgIWltcG9ydGFudDtcbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/workbench/components/workbench-page/workbench-page.component.ts":
/*!*****************************************************************************************!*\
  !*** ./src/app/modules/workbench/components/workbench-page/workbench-page.component.ts ***!
  \*****************************************************************************************/
/*! exports provided: WorkbenchPageComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WorkbenchPageComponent", function() { return WorkbenchPageComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");


var WorkbenchPageComponent = /** @class */ (function () {
    function WorkbenchPageComponent() {
    }
    WorkbenchPageComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'workbench-page',
            template: __webpack_require__(/*! ./workbench-page.component.html */ "./src/app/modules/workbench/components/workbench-page/workbench-page.component.html"),
            styles: [__webpack_require__(/*! ./workbench-page.component.scss */ "./src/app/modules/workbench/components/workbench-page/workbench-page.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [])
    ], WorkbenchPageComponent);
    return WorkbenchPageComponent;
}());



/***/ }),

/***/ "./src/app/modules/workbench/consts.ts":
/*!*********************************************!*\
  !*** ./src/app/modules/workbench/consts.ts ***!
  \*********************************************/
/*! exports provided: DATAPOD_CATEGORIES */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _common_consts__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ../../common/consts */ "./src/app/common/consts.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DATAPOD_CATEGORIES", function() { return _common_consts__WEBPACK_IMPORTED_MODULE_0__["DATAPOD_CATEGORIES"]; });




/***/ }),

/***/ "./src/app/modules/workbench/guards/default-workbench-page.guard.ts":
/*!**************************************************************************!*\
  !*** ./src/app/modules/workbench/guards/default-workbench-page.guard.ts ***!
  \**************************************************************************/
/*! exports provided: DefaultWorkbenchPageGuard */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DefaultWorkbenchPageGuard", function() { return DefaultWorkbenchPageGuard; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");




var DefaultWorkbenchPageGuard = /** @class */ (function () {
    function DefaultWorkbenchPageGuard(_router, _jwt) {
        this._router = _router;
        this._jwt = _jwt;
    }
    DefaultWorkbenchPageGuard.prototype.canActivate = function () {
        var _this = this;
        var redirectRoute = this._jwt.isAdmin() ? 'datasource/create' : 'dataobjects';
        setTimeout(function () { return _this._router.navigate(['workbench', redirectRoute]); });
        return true;
    };
    DefaultWorkbenchPageGuard = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"],
            _common_services__WEBPACK_IMPORTED_MODULE_3__["JwtService"]])
    ], DefaultWorkbenchPageGuard);
    return DefaultWorkbenchPageGuard;
}());



/***/ }),

/***/ "./src/app/modules/workbench/guards/index.ts":
/*!***************************************************!*\
  !*** ./src/app/modules/workbench/guards/index.ts ***!
  \***************************************************/
/*! exports provided: DefaultWorkbenchPageGuard */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _default_workbench_page_guard__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./default-workbench-page.guard */ "./src/app/modules/workbench/guards/default-workbench-page.guard.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DefaultWorkbenchPageGuard", function() { return _default_workbench_page_guard__WEBPACK_IMPORTED_MODULE_0__["DefaultWorkbenchPageGuard"]; });




/***/ }),

/***/ "./src/app/modules/workbench/routes.ts":
/*!*********************************************!*\
  !*** ./src/app/modules/workbench/routes.ts ***!
  \*********************************************/
/*! exports provided: routes */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "routes", function() { return routes; });
/* harmony import */ var _components_workbench_page_workbench_page_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./components/workbench-page/workbench-page.component */ "./src/app/modules/workbench/components/workbench-page/workbench-page.component.ts");
/* harmony import */ var _components_data_objects_view_data_objects_page_component__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./components/data-objects-view/data-objects-page.component */ "./src/app/modules/workbench/components/data-objects-view/data-objects-page.component.ts");
/* harmony import */ var _components_create_datasets_create_datasets_component__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./components/create-datasets/create-datasets.component */ "./src/app/modules/workbench/components/create-datasets/create-datasets.component.ts");
/* harmony import */ var _components_sql_executor_sql_executor_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./components/sql-executor/sql-executor.component */ "./src/app/modules/workbench/components/sql-executor/sql-executor.component.ts");
/* harmony import */ var _components_dataset_detailedView_dataset_detail_view_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./components/dataset-detailedView/dataset-detail-view.component */ "./src/app/modules/workbench/components/dataset-detailedView/dataset-detail-view.component.ts");
/* harmony import */ var _components_semantic_management_create_create_semantic_component__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./components/semantic-management/create/create-semantic.component */ "./src/app/modules/workbench/components/semantic-management/create/create-semantic.component.ts");
/* harmony import */ var _components_semantic_management_validate_validate_semantic_component__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./components/semantic-management/validate/validate-semantic.component */ "./src/app/modules/workbench/components/semantic-management/validate/validate-semantic.component.ts");
/* harmony import */ var _components_semantic_management_update_update_semantic_component__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./components/semantic-management/update/update-semantic.component */ "./src/app/modules/workbench/components/semantic-management/update/update-semantic.component.ts");
/* harmony import */ var _components_datasource_management_datasource_page_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./components/datasource-management/datasource-page.component */ "./src/app/modules/workbench/components/datasource-management/datasource-page.component.ts");
/* harmony import */ var _common_guards__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../../common/guards */ "./src/app/common/guards/index.ts");
/* harmony import */ var _admin_guards__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../admin/guards */ "./src/app/modules/admin/guards/index.ts");











var routes = [
    {
        path: '',
        canActivate: [_common_guards__WEBPACK_IMPORTED_MODULE_9__["IsUserLoggedInGuard"]],
        canActivateChild: [_common_guards__WEBPACK_IMPORTED_MODULE_9__["IsUserLoggedInGuard"]],
        component: _components_workbench_page_workbench_page_component__WEBPACK_IMPORTED_MODULE_0__["WorkbenchPageComponent"],
        runGuardsAndResolvers: 'paramsOrQueryParamsChange',
        children: [
            {
                path: 'dataobjects',
                component: _components_data_objects_view_data_objects_page_component__WEBPACK_IMPORTED_MODULE_1__["DataobjectsComponent"]
            },
            {
                path: 'dataset/add',
                component: _components_create_datasets_create_datasets_component__WEBPACK_IMPORTED_MODULE_2__["CreateDatasetsComponent"]
            },
            {
                path: 'create/sql',
                component: _components_sql_executor_sql_executor_component__WEBPACK_IMPORTED_MODULE_3__["SqlExecutorComponent"]
            },
            {
                path: 'dataset/details',
                component: _components_dataset_detailedView_dataset_detail_view_component__WEBPACK_IMPORTED_MODULE_4__["DatasetDetailViewComponent"]
            },
            {
                path: 'semantic/create',
                component: _components_semantic_management_create_create_semantic_component__WEBPACK_IMPORTED_MODULE_5__["CreateSemanticComponent"]
            },
            {
                path: 'semantic/validate',
                component: _components_semantic_management_validate_validate_semantic_component__WEBPACK_IMPORTED_MODULE_6__["ValidateSemanticComponent"]
            },
            {
                path: 'semantic/update',
                component: _components_semantic_management_update_update_semantic_component__WEBPACK_IMPORTED_MODULE_7__["UpdateSemanticComponent"]
            },
            {
                path: 'datasource/create',
                component: _components_datasource_management_datasource_page_component__WEBPACK_IMPORTED_MODULE_8__["DatasourceComponent"],
                canActivate: [_admin_guards__WEBPACK_IMPORTED_MODULE_10__["IsAdminGuard"]]
            },
            {
                path: '',
                pathMatch: 'full',
                redirectTo: 'datasource/create'
            }
        ]
    }
];


/***/ }),

/***/ "./src/app/modules/workbench/sample-data.ts":
/*!**************************************************!*\
  !*** ./src/app/modules/workbench/sample-data.ts ***!
  \**************************************************/
/*! exports provided: TREE_VIEW_Data, RAW_SAMPLE, parser_preview, ARTIFACT_SAMPLE, SQLEXEC_SAMPLE, SQL_AQCTIONS */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "TREE_VIEW_Data", function() { return TREE_VIEW_Data; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "RAW_SAMPLE", function() { return RAW_SAMPLE; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "parser_preview", function() { return parser_preview; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ARTIFACT_SAMPLE", function() { return ARTIFACT_SAMPLE; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SQLEXEC_SAMPLE", function() { return SQLEXEC_SAMPLE; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "SQL_AQCTIONS", function() { return SQL_AQCTIONS; });
var TREE_VIEW_Data = [
    { id: 1, parentId: 0 },
    { id: 11, parentId: 1 },
    { id: 12, parentId: 1 },
    { id: 13, parentId: 1 },
    { id: 131, parentId: 13 },
    { id: 132, parentId: 13 },
    { id: 133, parentId: 13 },
    { id: 2, parentId: 0 }
];
var RAW_SAMPLE = {
    projectId: 'workbench',
    path: 'test.csv',
    data: [
        'DOJ,Name,ID,ZipCode,Salary,Resignation\r',
        '2018-01-27,Saurav Paul,213,20191,23.40,01-27-2018\r',
        '2018-01-27,Saurav Paul,213,20191,54.56,01-27-2018\r',
        '2018-01-27,Alexey Sorokin,215,20191,84.70,2018-Jan-31 15:08:00\r',
        ' 2015-07-15,Saurav Paul,213,20191,99.70,01-27-2018\r'
    ]
};
var parser_preview = {
    lineSeparator: '\n',
    delimiter: ',',
    quoteChar: '"',
    quoteEscapeChar: '\\',
    headerSize: 1,
    fieldNamesLine: 1,
    rowsToInspect: 10000,
    delimiterType: 'delimited',
    description: 'Its delimited file inspecting to verify & understand the content of file',
    fields: [
        {
            name: 'DOJ',
            type: 'date',
            format: ['YYYY-MM-DD']
        },
        {
            name: 'Name',
            type: 'string'
        },
        {
            name: 'ID',
            type: 'long'
        },
        {
            name: 'ZipCode',
            type: 'long'
        },
        {
            name: 'Salary',
            type: 'double'
        },
        {
            name: 'Resignation',
            type: 'string',
            format: ['YYYY-MMM-DD HH:mm:ss', 'MM-DD-YYYY']
        }
    ],
    info: {
        totalLines: 5,
        dataRows: 4,
        maxFields: 6,
        minFields: 6,
        file: '/Users/spau0004/Desktop/test.csv'
    },
    samplesParsed: [
        {
            DOJ: '2018-01-27',
            Name: 'Saurav Paul',
            ID: '213',
            ZipCode: '20191',
            Salary: '54.56',
            Resignation: '01-27-2018'
        },
        {
            DOJ: '2018-01-27',
            Name: 'Alexey Sorokin',
            ID: '215',
            ZipCode: '20191',
            Salary: '84.70',
            Resignation: 'Alexey has resigned & his last day was on 2018-Jan-31 15:08:00'
        }
    ]
};
var ARTIFACT_SAMPLE = {
    artifacts: [
        {
            artifactName: 'MCT_DN_SESSION_SUMMARY',
            columns: [
                {
                    name: 'TRANSFER_DATE ',
                    type: 'date'
                },
                {
                    name: 'TRANSFER_DATE_ID',
                    type: 'integer'
                },
                {
                    name: 'TRANSFER_MONTH_ID',
                    type: 'integer'
                },
                {
                    name: 'OPCO',
                    type: 'string'
                }
            ]
        }
    ]
};
var SQLEXEC_SAMPLE = [
    {
        SEL_MB: 433670.01326084137,
        MONTH_YEAR: 'Aug-17',
        FAILED_ITEMS: 60904,
        FAILED_MB: 145021.8522052765,
        AVAIL_MB: 621064.1080217361,
        TARGET_MODEL: 'LGMP260',
        XFER_ITEMS: 621830,
        XFER_MB: 288465.82830142975,
        SEL_ITEMS: 685102,
        TARGET_OS: 'android',
        'sum(AVAILABLE_ITEMS)': 934445
    },
    {
        AVAIL_MB: 251823.43658542633,
        XFER_MB: 67241.89413356781,
        SEL_ITEMS: 153029,
        MONTH_YEAR: 'Aug-17',
        FAILED_ITEMS: 10851,
        FAILED_MB: 30701.61478805542,
        TARGET_MODEL: 'SM-J727T1',
        SEL_MB: 152983.2884979248,
        TARGET_OS: 'android',
        'sum(AVAILABLE_ITEMS)': 217587,
        XFER_ITEMS: 133955
    },
    {
        XFER_ITEMS: 754,
        AVAIL_MB: 6519.984823226929,
        FAILED_MB: 0,
        MONTH_YEAR: 'Oct-17',
        SEL_MB: 1808.7185382843018,
        TARGET_MODEL: '5049S',
        XFER_MB: 1808.7185382843018,
        SEL_ITEMS: 754,
        'sum(AVAILABLE_ITEMS)': 14447,
        TARGET_OS: 'android',
        FAILED_ITEMS: 0
    }
];
var SQL_AQCTIONS = [
    { statement: 'SELECT * FROM table_name;' },
    { statement: 'SELECT column1, column2 FROM table_name;' },
    { statement: 'SELECT CustomerName, City FROM Customers;' },
    {
        statement: 'SELECT Count(*) AS DistinctCountrie FROM (SELECT DISTINCT Country FROM Customers);'
    }
];


/***/ }),

/***/ "./src/app/modules/workbench/services/datasource.service.ts":
/*!******************************************************************!*\
  !*** ./src/app/modules/workbench/services/datasource.service.ts ***!
  \******************************************************************/
/*! exports provided: DatasourceService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DatasourceService", function() { return DatasourceService; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common/http */ "./node_modules/@angular/common/fesm5/http.js");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var _appConfig__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../../../appConfig */ "./appConfig.ts");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_7__);








var userProject = 'workbench';
var DatasourceService = /** @class */ (function () {
    function DatasourceService(http, jwt) {
        this.http = http;
        this.jwt = jwt;
        this.api = lodash_get__WEBPACK_IMPORTED_MODULE_7__(_appConfig__WEBPACK_IMPORTED_MODULE_5__["default"], 'api.url');
        this.isDuplicateChannel = this.isDuplicateChannel.bind(this);
        this.isDuplicateRoute = this.isDuplicateRoute.bind(this);
    }
    /**
     * Get list of all data sources
     *
     * @returns
     * @memberof DatasourceService
     */
    DatasourceService.prototype.getSourceList = function () {
        // This API supports for BE pagination. But as the channel number won't be huge, setting it to 10,000;
        // Refer to JIRA ID: SIP-4615 if more info needed about this API support.
        return this.http
            .get(this.api + "/ingestion/batch/channels?size=10000")
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Get list of all routes in a source
     *
     * @returns
     * @memberof DatasourceService
     */
    DatasourceService.prototype.getRoutesList = function (channelID) {
        // This API supports for BE pagination.
        // Refer to JIRA ID: SIP-4615 if more info needed about this API support.
        return this.http
            .get(this.api + "/ingestion/batch/channels/" + channelID + "/routes?size=10000")
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Get logs of a route
     *
     * @returns
     * @memberof DatasourceService
     */
    DatasourceService.prototype.getRoutesLogs = function (channelID, routeID) {
        // This API supports for BE pagination.
        // Refer to JIRA ID: SIP-4615 if more info needed about this API support.
        return this.http
            .get(this.api + "/ingestion/batch/logs/" + channelID + "/" + routeID)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    DatasourceService.prototype.isDuplicateChannel = function (channelName) {
        var endpoint = this.api + "/ingestion/batch/channels/duplicate?channelName=" + channelName;
        return this.http
            .get(endpoint)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(function (data) { return lodash_get__WEBPACK_IMPORTED_MODULE_7__(data, 'isDuplicate'); }), Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', false)));
    };
    DatasourceService.prototype.activateRoute = function (channelId, routeId) {
        return this.toggleRoute(channelId, routeId, false);
    };
    DatasourceService.prototype.deActivateRoute = function (channelId, routeId) {
        return this.toggleRoute(channelId, routeId, false);
    };
    DatasourceService.prototype.toggleRoute = function (channelId, routeId, activate) {
        var endpoint = this.api + "/ingestion/batch/channels/" + channelId + "/routes/" + routeId + "/" + (activate ? 'activate' : 'deactivate');
        var payload = {
            channelId: channelId,
            routeId: routeId
        };
        return this.http
            .put(endpoint, payload)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    DatasourceService.prototype.activateChannel = function (channelId) {
        return this.toggleChannel(channelId, true);
    };
    DatasourceService.prototype.deActivateChannel = function (channelId) {
        return this.toggleChannel(channelId, false);
    };
    DatasourceService.prototype.toggleChannel = function (channelId, activate) {
        var endpoint = this.api + "/ingestion/batch/channels/" + channelId + "/" + (activate ? 'activate' : 'deactivate');
        var payload = {
            channelId: channelId
        };
        return this.http
            .put(endpoint, payload)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    DatasourceService.prototype.isDuplicateRoute = function (_a) {
        var channelId = _a.channelId, routeName = _a.routeName;
        var endpoint = this.api + "/ingestion/batch/channels/" + channelId + "/duplicate-route?routeName=" + routeName;
        return this.http
            .get(endpoint)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["map"])(function (data) { return lodash_get__WEBPACK_IMPORTED_MODULE_7__(data, 'isDuplicate'); }), Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', false)));
    };
    /**
     * Delete a channel by ID
     *
     * @returns
     * @memberof DatasourceService
     */
    DatasourceService.prototype.deleteChannel = function (channelID) {
        return this.http
            .delete(this.api + "/ingestion/batch/channels/" + channelID)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Updates a Source
     *
     * @param {*} payload
     * @returns
     * @memberof DatasourceService
     */
    DatasourceService.prototype.updateSource = function (channelID, payload) {
        payload.modifiedBy = this.jwt.getUserName();
        var endpoint = this.api + "/ingestion/batch/channels/" + channelID;
        return this.http
            .put(endpoint, payload)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Creates a Source entry in Batch Ingestion Service
     *
     * @param {*} payload
     * @returns
     * @memberof DatasourceService
     */
    DatasourceService.prototype.createSource = function (payload) {
        payload.customerCode = this.jwt.customerCode;
        payload.createdBy = this.jwt.getUserName();
        payload.projectCode = userProject;
        payload.productCode = this.jwt.getProductName();
        var endpoint = this.api + "/ingestion/batch/channels";
        return this.http
            .post(endpoint, payload)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Creates a Route entry for a Channel in Batch Ingestion Service
     *
     * @param {*} payload
     * @returns
     * @memberof DatasourceService
     */
    DatasourceService.prototype.createRoute = function (channelID, payload) {
        payload.createdBy = this.jwt.getUserName();
        var endpoint = this.api + "/ingestion/batch/channels/" + channelID + "/routes";
        return this.http
            .post(endpoint, payload)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Updates a Route
     *
     * @param {*} payload
     * @returns
     * @memberof DatasourceService
     */
    DatasourceService.prototype.updateRoute = function (channelID, routeID, payload) {
        payload.modifiedBy = this.jwt.getUserName();
        var endpoint = this.api + "/ingestion/batch/channels/" + channelID + "/routes/" + routeID;
        return this.http
            .put(endpoint, payload)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Delete a Route by ID
     *
     * @returns
     * @memberof DatasourceService
     */
    DatasourceService.prototype.deleteRoute = function (channelID, routeID) {
        return this.http
            .delete(this.api + "/ingestion/batch/channels/" + channelID + "/routes/" + routeID)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Encrypts a given password with a key
     *
     * @param {*} payload
     * @returns {Observable<any>}
     * @memberof DatasourceService
     */
    DatasourceService.prototype.encryptPWD = function (payload) {
        var endpoint = this.api + "/ingestion/batch/internal/encrypt";
        return this.http
            .post(endpoint, JSON.stringify(payload))
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Decrypts a given password
     *
     * @param {*} payload
     * @returns {Observable<any>}
     * @memberof DatasourceService
     */
    DatasourceService.prototype.decryptPWD = function (payload) {
        var endpoint = this.api + "/ingestion/batch/internal/decrypt";
        return this.http
            .post(endpoint, JSON.stringify(payload))
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Test connectivity for a channel
     *
     * @param {*} channelID
     * @returns {Observable<any>}
     * @memberof DatasourceService
     */
    DatasourceService.prototype.testChannel = function (channelID) {
        return this.http
            .get(this.api + "/ingestion/batch/sftp/channels/" + channelID + "/status")
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Test connectivity for a route
     *
     * @param {*} routeID
     * @returns {Observable<any>}
     * @memberof DatasourceService
     */
    DatasourceService.prototype.testRoute = function (routeID) {
        return this.http
            .get(this.api + "/ingestion/batch/sftp/routes/" + routeID + "/status")
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Check channel connection using config
     *
     * @param {*} payload
     * @returns {Observable<any>}
     * @memberof DatasourceService
     */
    DatasourceService.prototype.testChannelWithBody = function (payload) {
        var endpoint = this.api + "/ingestion/batch/sftp/channels/test";
        return this.http
            .post(endpoint, payload)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Check route connection using config
     *
     * @param {*} payload
     * @returns {Observable<any>}
     * @memberof DatasourceService
     */
    DatasourceService.prototype.testRouteWithBody = function (payload) {
        var endpoint = this.api + "/ingestion/batch/sftp/routes/test";
        return this.http
            .post(endpoint, payload)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_3__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Handle Http operation that failed.
     * Let the app continue.
     * @param operation - name of the operation that failed
     * @param result - optional value to return as the observable result
     */
    DatasourceService.prototype.handleError = function (operation, result) {
        if (operation === void 0) { operation = 'operation'; }
        return function (error) {
            return Object(rxjs__WEBPACK_IMPORTED_MODULE_6__["of"])(result);
        };
    };
    DatasourceService = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])({
            providedIn: 'root'
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_common_http__WEBPACK_IMPORTED_MODULE_2__["HttpClient"], _common_services__WEBPACK_IMPORTED_MODULE_4__["JwtService"]])
    ], DatasourceService);
    return DatasourceService;
}());



/***/ }),

/***/ "./src/app/modules/workbench/services/workbench.service.ts":
/*!*****************************************************************!*\
  !*** ./src/app/modules/workbench/services/workbench.service.ts ***!
  \*****************************************************************/
/*! exports provided: WorkbenchService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WorkbenchService", function() { return WorkbenchService; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_1__);
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! lodash/forEach */ "./node_modules/lodash/forEach.js");
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(lodash_forEach__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/isUndefined */ "./node_modules/lodash/isUndefined.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_isUndefined__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_common_http__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/common/http */ "./node_modules/@angular/common/fesm5/http.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var _sample_data__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../sample-data */ "./src/app/modules/workbench/sample-data.ts");
/* harmony import */ var _appConfig__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../../../../../appConfig */ "./appConfig.ts");












var userProject = 'workbench';
var WorkbenchService = /** @class */ (function () {
    function WorkbenchService(http, jwt, router) {
        this.http = http;
        this.jwt = jwt;
        this.router = router;
        this.api = lodash_get__WEBPACK_IMPORTED_MODULE_1__(_appConfig__WEBPACK_IMPORTED_MODULE_11__["default"], 'api.url');
        this.wbAPI = this.api + "/internal/workbench/projects";
        this.getStagingData = this.getStagingData.bind(this);
        this.createFolder = this.createFolder.bind(this);
    }
    /** GET datasets from the server */
    WorkbenchService.prototype.getDatasets = function () {
        var endpoint = this.wbAPI + "/" + userProject + "/datasets";
        return this.http
            .get(endpoint)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_7__["catchError"])(this.handleError('data', [])));
    };
    /** GET Staging area tree list */
    WorkbenchService.prototype.getStagingData = function (path) {
        var endpoint = this.wbAPI + "/" + userProject + "/raw/directory";
        return this.http
            .post(endpoint, { path: path })
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_7__["catchError"])(this.handleError('data', [])));
    };
    /** GET raw preview from the server */
    WorkbenchService.prototype.getRawPreviewData = function (path) {
        var endpoint = this.wbAPI + "/" + userProject + "/raw/directory/preview";
        return this.http
            .post(endpoint, { path: path })
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_7__["catchError"])(this.handleError('data', [])));
    };
    /** GET parsed preview from the server */
    WorkbenchService.prototype.getParsedPreviewData = function (previewConfig) {
        var endpoint = this.wbAPI + "/" + userProject + "/raw/directory/inspect";
        return this.http.post(endpoint, previewConfig).pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_7__["catchError"])(function (e) {
            return Object(rxjs__WEBPACK_IMPORTED_MODULE_6__["of"])(e);
        }));
    };
    /** File mask search */
    WorkbenchService.prototype.filterFiles = function (mask, temmpFiles) {
        var selFiles = [];
        if (lodash_isUndefined__WEBPACK_IMPORTED_MODULE_3__(mask)) {
            return;
        }
        var wildcardSearch;
        if (this.startsWith(mask, '*')) {
            wildcardSearch = this.endsWith;
        }
        else if (this.endsWith(mask, '*')) {
            wildcardSearch = this.startsWith;
        }
        else {
            wildcardSearch = this.exactMatch;
        }
        var filemasksearch = mask.replace('*', '');
        for (var fileCounter = 0; fileCounter < temmpFiles.length; fileCounter++) {
            if (wildcardSearch(temmpFiles[fileCounter].name, filemasksearch)) {
                selFiles.push(temmpFiles[fileCounter]);
            }
        }
        return selFiles;
    };
    // string functions for filemask wild card search
    WorkbenchService.prototype.endsWith = function (str, suffix) {
        return str.indexOf(suffix, str.length - suffix.length) !== -1;
    };
    WorkbenchService.prototype.startsWith = function (str, suffix) {
        return str.indexOf(suffix) === 0;
    };
    WorkbenchService.prototype.exactMatch = function (str, suffix) {
        return str === suffix;
    };
    WorkbenchService.prototype.uploadFile = function (filesToUpload, path) {
        var endpoint = this.wbAPI + "/" + userProject + "/raw/directory/upload/files";
        var headers = new _angular_common_http__WEBPACK_IMPORTED_MODULE_5__["HttpHeaders"]();
        headers.set('Content-Type', null);
        headers.set('Accept', 'multipart/form-data');
        var params = new _angular_common_http__WEBPACK_IMPORTED_MODULE_5__["HttpParams"]();
        var formData = new FormData();
        lodash_forEach__WEBPACK_IMPORTED_MODULE_2__(filesToUpload, function (file) {
            formData.append('files', file, file.name);
        });
        formData.append('path', path);
        return this.http.post(endpoint, formData, { params: params, headers: headers });
    };
    WorkbenchService.prototype.validateMaxSize = function (fileList) {
        var size = 0;
        var maxSize = 26214400;
        lodash_forEach__WEBPACK_IMPORTED_MODULE_2__(fileList, function (file) {
            size += file.size;
        });
        if (size > maxSize) {
            return false;
        }
        return true;
    };
    WorkbenchService.prototype.validateFileTypes = function (fileList) {
        var isValid = true;
        lodash_forEach__WEBPACK_IMPORTED_MODULE_2__(fileList, function (file) {
            var fileName = file.name;
            var ext = fileName.substring(fileName.lastIndexOf('.') + 1);
            if (ext.toLowerCase() !== 'csv' && ext.toLowerCase() !== 'txt') {
                isValid = false;
            }
        });
        return isValid;
    };
    WorkbenchService.prototype.createFolder = function (path) {
        var endpoint = this.wbAPI + "/" + userProject + "/raw/directory/create";
        return this.http
            .post(endpoint, { path: path })
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_7__["catchError"])(this.handleError('data', [])));
    };
    /**
     * Service to fetch meta data of a dataset
     *
     * @param {string} projectName
     * @param {any} id
     * @returns {Observable<any>}
     * @memberof WorkbenchService
     */
    WorkbenchService.prototype.getDatasetDetails = function (id) {
        var endpoint = this.wbAPI + "/" + userProject + "/" + id;
        return this.http
            .get(endpoint)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_7__["catchError"])(this.handleError('data', _sample_data__WEBPACK_IMPORTED_MODULE_10__["ARTIFACT_SAMPLE"])));
    };
    WorkbenchService.prototype.triggerParser = function (payload) {
        var endpoint = this.wbAPI + "/" + userProject + "/datasets";
        return this.http
            .post(endpoint, payload)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_7__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Following 3 functions
     * To store, retrive and remove data from localstorage
     *
     * @param {any} metadata
     * @memberof WorkbenchService
     */
    WorkbenchService.prototype.setDataToLS = function (key, value) {
        localStorage.setItem(key, JSON.stringify(value));
    };
    WorkbenchService.prototype.getDataFromLS = function (key) {
        var dsMetada = JSON.parse(localStorage.getItem(key));
        return dsMetada;
    };
    WorkbenchService.prototype.removeDataFromLS = function (key) {
        localStorage.removeItem(key);
    };
    /**
     * Calls the sql executor component and fetches the output as data for preview grid
     *
     * @param {string} query
     * @returns {Observable<any>}
     * @memberof WorkbenchService
     */
    WorkbenchService.prototype.executeSqlQuery = function (query) {
        var endpoint = this.wbAPI + "/execute";
        return this.http
            .post(endpoint, { query: query })
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_7__["catchError"])(this.handleError('data', _sample_data__WEBPACK_IMPORTED_MODULE_10__["SQLEXEC_SAMPLE"])));
    };
    WorkbenchService.prototype.navigateToDetails = function (metadata) {
        this.setDataToLS('dsMetadata', metadata);
        this.router.navigate(['workbench', 'dataset', 'details']);
    };
    WorkbenchService.prototype.triggerDatasetPreview = function (name) {
        var endpoint = this.wbAPI + "/" + userProject + "/previews";
        return this.http
            .post(endpoint, { name: name })
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_7__["catchError"])(this.handleError('data', {})));
    };
    WorkbenchService.prototype.getDatasetPreviewData = function (id) {
        var endpoint = this.wbAPI + "/" + userProject + "/previews/" + id;
        return this.http.get(endpoint).pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_7__["catchError"])(function (e) {
            return Object(rxjs__WEBPACK_IMPORTED_MODULE_6__["of"])(e);
        }));
    };
    WorkbenchService.prototype.createSemantic = function (payload) {
        var endpoint = this.api + "/internal/semantic/" + userProject + "/create";
        payload.customerCode = this.jwt.customerCode;
        payload.username = this.jwt.getUserName();
        payload.projectCode = userProject;
        return this.http
            .post(endpoint, payload)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_7__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Gets list of all datapods avialble in semantic store for that particular project
     *
     * @returns
     * @memberof WorkbenchService
     */
    WorkbenchService.prototype.getListOfSemantic = function () {
        return this.http
            .get(this.api + "/internal/semantic/md?projectId=" + userProject)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_7__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Gets the detailed definition of particular semantic ID
     *
     * @param {*} id
     * @returns
     * @memberof WorkbenchService
     */
    WorkbenchService.prototype.getSemanticDetails = function (id) {
        return this.http
            .get(this.api + "/internal/semantic/" + userProject + "/" + id)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_7__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Updates the definition of particular semantic ID
     *
     * @param {*} payload
     * @returns
     * @memberof WorkbenchService
     */
    WorkbenchService.prototype.updateSemanticDetails = function (payload) {
        var semID = payload.id;
        var endpoint = this.api + "/internal/semantic/" + userProject + "/" + semID;
        payload.updatedBy = this.jwt.getUserName();
        return this.http
            .put(endpoint, payload)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_7__["catchError"])(this.handleError('data', {})));
    };
    /**
     * Handle Http operation that failed.
     * Let the app continue.
     * @param operation - name of the operation that failed
     * @param result - optional value to return as the observable result
     */
    WorkbenchService.prototype.handleError = function (operation, result) {
        if (operation === void 0) { operation = 'operation'; }
        return function (error) {
            return Object(rxjs__WEBPACK_IMPORTED_MODULE_6__["of"])(result);
        };
    };
    WorkbenchService = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_4__["Injectable"])({
            providedIn: 'root'
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_common_http__WEBPACK_IMPORTED_MODULE_5__["HttpClient"],
            _common_services__WEBPACK_IMPORTED_MODULE_9__["JwtService"],
            _angular_router__WEBPACK_IMPORTED_MODULE_8__["Router"]])
    ], WorkbenchService);
    return WorkbenchService;
}());



/***/ }),

/***/ "./src/app/modules/workbench/wb-comp-configs.ts":
/*!******************************************************!*\
  !*** ./src/app/modules/workbench/wb-comp-configs.ts ***!
  \******************************************************/
/*! exports provided: CSV_CONFIG, PARSER_CONFIG, STAGING_TREE, TYPE_CONVERSION, CHANNEL_TYPES */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CSV_CONFIG", function() { return CSV_CONFIG; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "PARSER_CONFIG", function() { return PARSER_CONFIG; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "STAGING_TREE", function() { return STAGING_TREE; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "TYPE_CONVERSION", function() { return TYPE_CONVERSION; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "CHANNEL_TYPES", function() { return CHANNEL_TYPES; });
var CSV_CONFIG = {
    file: '',
    lineSeparator: '\n',
    delimiter: ',',
    quoteChar: '',
    quoteEscapeChar: '\\',
    headerSize: 1,
    fieldNamesLine: '',
    dateFormats: [],
    rowsToInspect: 100,
    delimiterType: 'delimited',
    header: 'yes'
};
var PARSER_CONFIG = {
    parser: {
        fields: [],
        file: '',
        lineSeparator: '\\n',
        delimiter: ',',
        quoteChar: '',
        quoteEscape: '\\',
        headerSize: 1,
        fieldNameLine: ''
    },
    outputs: [
        {
            dataSet: 'test'
        }
    ],
    parameters: [
        {
            name: 'spark.master',
            value: 'spark://foobar'
        }
    ]
    // outputs: [{
    //   dataSet: '',
    //   mode: 'replace', // append
    //   description: ''
    // }],
    // parameters: [{
    //   name: 'spark.master',
    //   value: 'yarn'
    // }, {
    //   name: 'spark.executor.instances',
    //   value: '6'
    // }]
};
var STAGING_TREE = {
    name: 'Staging',
    size: Infinity,
    isDirectory: true,
    path: 'root'
};
var TYPE_CONVERSION = {
    text: 'string',
    string: 'string',
    double: 'double',
    integer: 'integer',
    date: 'date',
    float: 'float',
    keyword: 'string',
    long: 'long',
    decimal: 'double',
    timestamp: 'date'
};
var CHANNEL_TYPES = [
    {
        name: 'SFTP',
        uid: 'sftp',
        imgsrc: 'assets/img/sftp.png',
        supported: true
    },
    {
        name: 'Amazon S3',
        uid: 's3',
        imgsrc: 'assets/img/s3.png',
        supported: false
    },
    {
        name: 'MAPR',
        uid: 'mapr',
        imgsrc: 'assets/svg/mapr.svg',
        supported: false
    },
    {
        name: 'Elastic Search',
        uid: 'es',
        imgsrc: 'assets/svg/elastic.svg',
        supported: false
    },
    {
        name: 'MariaDB',
        uid: 'maridb',
        imgsrc: 'assets/img/mariadb.jpg',
        supported: false
    },
    {
        name: 'HDFS',
        uid: 'hdfs',
        imgsrc: 'assets/img/hadoop.jpg',
        supported: false
    },
    {
        name: 'MySQL',
        uid: 'mysql',
        imgsrc: 'assets/svg/mysql.svg',
        supported: false
    },
    {
        name: 'SQL Server',
        uid: 'sqlserver',
        imgsrc: 'assets/img/sqlserver.png',
        supported: false
    },
    {
        name: 'MongoDB',
        uid: 'mongodb',
        imgsrc: 'assets/img/mongodb.png',
        supported: false
    },
    {
        name: 'JDBC',
        uid: 'jdbc',
        imgsrc: 'assets/img/jdbc.png',
        supported: false
    }
];


/***/ }),

/***/ "./src/app/modules/workbench/workbench.module.ts":
/*!*******************************************************!*\
  !*** ./src/app/modules/workbench/workbench.module.ts ***!
  \*******************************************************/
/*! exports provided: WorkbenchModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "WorkbenchModule", function() { return WorkbenchModule; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/common */ "./node_modules/@angular/common/fesm5/common.js");
/* harmony import */ var _material_module__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../material.module */ "./src/app/material.module.ts");
/* harmony import */ var _angular_flex_layout__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/flex-layout */ "./node_modules/@angular/flex-layout/esm5/flex-layout.es5.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var devextreme_angular_core_template__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! devextreme-angular/core/template */ "./node_modules/devextreme-angular/core/template.js");
/* harmony import */ var devextreme_angular_core_template__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(devextreme_angular_core_template__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! devextreme-angular/ui/data-grid */ "./node_modules/devextreme-angular/ui/data-grid.js");
/* harmony import */ var devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_7__);
/* harmony import */ var angular_tree_component__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! angular-tree-component */ "./node_modules/angular-tree-component/dist/angular-tree-component.js");
/* harmony import */ var ng2_ace_editor__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ng2-ace-editor */ "./node_modules/ng2-ace-editor/index.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var angular_split__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! angular-split */ "./node_modules/angular-split/fesm5/angular-split.js");
/* harmony import */ var _routes__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ./routes */ "./src/app/modules/workbench/routes.ts");
/* harmony import */ var _components_workbench_page_workbench_page_component__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ./components/workbench-page/workbench-page.component */ "./src/app/modules/workbench/components/workbench-page/workbench-page.component.ts");
/* harmony import */ var _components_create_datasets_create_datasets_component__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ./components/create-datasets/create-datasets.component */ "./src/app/modules/workbench/components/create-datasets/create-datasets.component.ts");
/* harmony import */ var _components_create_datasets_select_rawdata_select_rawdata_component__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ./components/create-datasets/select-rawdata/select-rawdata.component */ "./src/app/modules/workbench/components/create-datasets/select-rawdata/select-rawdata.component.ts");
/* harmony import */ var _components_create_datasets_dataset_details_dataset_details_component__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ./components/create-datasets/dataset-details/dataset-details.component */ "./src/app/modules/workbench/components/create-datasets/dataset-details/dataset-details.component.ts");
/* harmony import */ var _components_create_datasets_rawpreview_dialog_rawpreview_dialog_component__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(/*! ./components/create-datasets/rawpreview-dialog/rawpreview-dialog.component */ "./src/app/modules/workbench/components/create-datasets/rawpreview-dialog/rawpreview-dialog.component.ts");
/* harmony import */ var _components_create_datasets_parser_preview_parser_preview_component__WEBPACK_IMPORTED_MODULE_18__ = __webpack_require__(/*! ./components/create-datasets/parser-preview/parser-preview.component */ "./src/app/modules/workbench/components/create-datasets/parser-preview/parser-preview.component.ts");
/* harmony import */ var _components_create_datasets_dateformat_dialog_dateformat_dialog_component__WEBPACK_IMPORTED_MODULE_19__ = __webpack_require__(/*! ./components/create-datasets/dateformat-dialog/dateformat-dialog.component */ "./src/app/modules/workbench/components/create-datasets/dateformat-dialog/dateformat-dialog.component.ts");
/* harmony import */ var _components_sql_executor_sql_executor_component__WEBPACK_IMPORTED_MODULE_20__ = __webpack_require__(/*! ./components/sql-executor/sql-executor.component */ "./src/app/modules/workbench/components/sql-executor/sql-executor.component.ts");
/* harmony import */ var _components_sql_executor_query_sql_script_component__WEBPACK_IMPORTED_MODULE_21__ = __webpack_require__(/*! ./components/sql-executor/query/sql-script.component */ "./src/app/modules/workbench/components/sql-executor/query/sql-script.component.ts");
/* harmony import */ var _components_sql_executor_preview_grid_sqlpreview_grid_page_component__WEBPACK_IMPORTED_MODULE_22__ = __webpack_require__(/*! ./components/sql-executor/preview-grid/sqlpreview-grid-page.component */ "./src/app/modules/workbench/components/sql-executor/preview-grid/sqlpreview-grid-page.component.ts");
/* harmony import */ var _components_sql_executor_dataset_details_dialog_details_dialog_component__WEBPACK_IMPORTED_MODULE_23__ = __webpack_require__(/*! ./components/sql-executor/dataset-details-dialog/details-dialog.component */ "./src/app/modules/workbench/components/sql-executor/dataset-details-dialog/details-dialog.component.ts");
/* harmony import */ var _components_dataset_detailedView_dataset_detail_view_component__WEBPACK_IMPORTED_MODULE_24__ = __webpack_require__(/*! ./components/dataset-detailedView/dataset-detail-view.component */ "./src/app/modules/workbench/components/dataset-detailedView/dataset-detail-view.component.ts");
/* harmony import */ var _components_semantic_management_index__WEBPACK_IMPORTED_MODULE_25__ = __webpack_require__(/*! ./components/semantic-management/index */ "./src/app/modules/workbench/components/semantic-management/index.ts");
/* harmony import */ var _components_data_objects_view_index__WEBPACK_IMPORTED_MODULE_26__ = __webpack_require__(/*! ./components/data-objects-view/index */ "./src/app/modules/workbench/components/data-objects-view/index.ts");
/* harmony import */ var _components_datasource_management_index__WEBPACK_IMPORTED_MODULE_27__ = __webpack_require__(/*! ./components/datasource-management/index */ "./src/app/modules/workbench/components/datasource-management/index.ts");
/* harmony import */ var _guards__WEBPACK_IMPORTED_MODULE_28__ = __webpack_require__(/*! ./guards */ "./src/app/modules/workbench/guards/index.ts");
/* harmony import */ var _admin_guards__WEBPACK_IMPORTED_MODULE_29__ = __webpack_require__(/*! ../admin/guards */ "./src/app/modules/admin/guards/index.ts");
/* harmony import */ var _common__WEBPACK_IMPORTED_MODULE_30__ = __webpack_require__(/*! ../../common */ "./src/app/common/index.ts");































var COMPONENTS = [
    _components_workbench_page_workbench_page_component__WEBPACK_IMPORTED_MODULE_13__["WorkbenchPageComponent"],
    _components_data_objects_view_index__WEBPACK_IMPORTED_MODULE_26__["DataobjectsComponent"],
    _components_data_objects_view_index__WEBPACK_IMPORTED_MODULE_26__["DatasetsCardPageComponent"],
    _components_data_objects_view_index__WEBPACK_IMPORTED_MODULE_26__["DatasetsGridPageComponent"],
    _components_create_datasets_create_datasets_component__WEBPACK_IMPORTED_MODULE_14__["CreateDatasetsComponent"],
    _components_create_datasets_select_rawdata_select_rawdata_component__WEBPACK_IMPORTED_MODULE_15__["SelectRawdataComponent"],
    _components_create_datasets_dataset_details_dataset_details_component__WEBPACK_IMPORTED_MODULE_16__["DatasetDetailsComponent"],
    _components_create_datasets_rawpreview_dialog_rawpreview_dialog_component__WEBPACK_IMPORTED_MODULE_17__["RawpreviewDialogComponent"],
    _components_create_datasets_parser_preview_parser_preview_component__WEBPACK_IMPORTED_MODULE_18__["ParserPreviewComponent"],
    _components_create_datasets_dateformat_dialog_dateformat_dialog_component__WEBPACK_IMPORTED_MODULE_19__["DateformatDialogComponent"],
    _components_data_objects_view_index__WEBPACK_IMPORTED_MODULE_26__["DatasetActionsComponent"],
    _components_sql_executor_sql_executor_component__WEBPACK_IMPORTED_MODULE_20__["SqlExecutorComponent"],
    _components_sql_executor_query_sql_script_component__WEBPACK_IMPORTED_MODULE_21__["SqlScriptComponent"],
    _components_sql_executor_preview_grid_sqlpreview_grid_page_component__WEBPACK_IMPORTED_MODULE_22__["SqlpreviewGridPageComponent"],
    _components_sql_executor_dataset_details_dialog_details_dialog_component__WEBPACK_IMPORTED_MODULE_23__["DetailsDialogComponent"],
    _components_dataset_detailedView_dataset_detail_view_component__WEBPACK_IMPORTED_MODULE_24__["DatasetDetailViewComponent"],
    _components_semantic_management_index__WEBPACK_IMPORTED_MODULE_25__["CreateSemanticComponent"],
    _components_semantic_management_index__WEBPACK_IMPORTED_MODULE_25__["ValidateSemanticComponent"],
    _components_semantic_management_index__WEBPACK_IMPORTED_MODULE_25__["SemanticDetailsDialogComponent"],
    _components_semantic_management_index__WEBPACK_IMPORTED_MODULE_25__["UpdateSemanticComponent"],
    _components_data_objects_view_index__WEBPACK_IMPORTED_MODULE_26__["DatapodsCardPageComponent"],
    _components_data_objects_view_index__WEBPACK_IMPORTED_MODULE_26__["DatapodsGridPageComponent"],
    _components_data_objects_view_index__WEBPACK_IMPORTED_MODULE_26__["DatapodActionsComponent"],
    _components_datasource_management_index__WEBPACK_IMPORTED_MODULE_27__["DatasourceComponent"],
    _components_datasource_management_index__WEBPACK_IMPORTED_MODULE_27__["LogsDialogComponent"],
    _components_datasource_management_index__WEBPACK_IMPORTED_MODULE_27__["CreateSourceDialogComponent"],
    _components_datasource_management_index__WEBPACK_IMPORTED_MODULE_27__["TestConnectivityComponent"],
    _components_datasource_management_index__WEBPACK_IMPORTED_MODULE_27__["CreateRouteDialogComponent"],
    _components_datasource_management_index__WEBPACK_IMPORTED_MODULE_27__["ConfirmActionDialogComponent"],
    _components_datasource_management_index__WEBPACK_IMPORTED_MODULE_27__["SourceFolderDialogComponent"]
];
var GUARDS = [_guards__WEBPACK_IMPORTED_MODULE_28__["DefaultWorkbenchPageGuard"], _admin_guards__WEBPACK_IMPORTED_MODULE_29__["IsAdminGuard"]];
var WorkbenchModule = /** @class */ (function () {
    function WorkbenchModule() {
    }
    WorkbenchModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["NgModule"])({
            imports: [
                _angular_common__WEBPACK_IMPORTED_MODULE_2__["CommonModule"],
                _angular_forms__WEBPACK_IMPORTED_MODULE_5__["FormsModule"],
                _material_module__WEBPACK_IMPORTED_MODULE_3__["MaterialModule"],
                _angular_forms__WEBPACK_IMPORTED_MODULE_5__["ReactiveFormsModule"],
                _angular_router__WEBPACK_IMPORTED_MODULE_10__["RouterModule"].forChild(_routes__WEBPACK_IMPORTED_MODULE_12__["routes"]),
                devextreme_angular_ui_data_grid__WEBPACK_IMPORTED_MODULE_7__["DxDataGridModule"],
                devextreme_angular_core_template__WEBPACK_IMPORTED_MODULE_6__["DxTemplateModule"],
                _angular_flex_layout__WEBPACK_IMPORTED_MODULE_4__["FlexLayoutModule"],
                angular_tree_component__WEBPACK_IMPORTED_MODULE_8__["TreeModule"],
                ng2_ace_editor__WEBPACK_IMPORTED_MODULE_9__["AceEditorModule"],
                _common__WEBPACK_IMPORTED_MODULE_30__["CommonModuleTs"],
                angular_split__WEBPACK_IMPORTED_MODULE_11__["AngularSplitModule"].forChild()
            ],
            declarations: COMPONENTS,
            entryComponents: COMPONENTS,
            providers: [GUARDS]
        })
    ], WorkbenchModule);
    return WorkbenchModule;
}());



/***/ })

}]);
//# sourceMappingURL=modules-workbench-workbench-module.js.map