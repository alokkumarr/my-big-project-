(window["webpackJsonp"] = window["webpackJsonp"] || []).push([["modules-analyze-analyze-module"],{

/***/ "./node_modules/angular-2-local-storage/dist/index.js":
/*!************************************************************!*\
  !*** ./node_modules/angular-2-local-storage/dist/index.js ***!
  \************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

Object.defineProperty(exports, "__esModule", { value: true });
var local_storage_module_1 = __webpack_require__(/*! ./local-storage.module */ "./node_modules/angular-2-local-storage/dist/local-storage.module.js");
exports.LocalStorageModule = local_storage_module_1.LocalStorageModule;
var local_storage_service_1 = __webpack_require__(/*! ./local-storage.service */ "./node_modules/angular-2-local-storage/dist/local-storage.service.js");
exports.LocalStorageService = local_storage_service_1.LocalStorageService;
//# sourceMappingURL=index.js.map

/***/ }),

/***/ "./node_modules/angular-2-local-storage/dist/local-storage.module.js":
/*!***************************************************************************!*\
  !*** ./node_modules/angular-2-local-storage/dist/local-storage.module.js ***!
  \***************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
var local_storage_service_1 = __webpack_require__(/*! ./local-storage.service */ "./node_modules/angular-2-local-storage/dist/local-storage.service.js");
var LocalStorageModule = /** @class */ (function () {
    function LocalStorageModule() {
    }
    LocalStorageModule_1 = LocalStorageModule;
    LocalStorageModule.withConfig = function (userConfig) {
        if (userConfig === void 0) { userConfig = {}; }
        return {
            ngModule: LocalStorageModule_1,
            providers: [
                { provide: 'LOCAL_STORAGE_SERVICE_CONFIG', useValue: userConfig }
            ]
        };
    };
    LocalStorageModule = LocalStorageModule_1 = __decorate([
        core_1.NgModule({
            providers: [
                local_storage_service_1.LocalStorageService
            ]
        })
    ], LocalStorageModule);
    return LocalStorageModule;
    var LocalStorageModule_1;
}());
exports.LocalStorageModule = LocalStorageModule;
//# sourceMappingURL=local-storage.module.js.map

/***/ }),

/***/ "./node_modules/angular-2-local-storage/dist/local-storage.service.js":
/*!****************************************************************************!*\
  !*** ./node_modules/angular-2-local-storage/dist/local-storage.service.js ***!
  \****************************************************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
var rxjs_1 = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
var operators_1 = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");
var DEPRECATED = 'This function is deprecated.';
var LOCAL_STORAGE_NOT_SUPPORTED = 'LOCAL_STORAGE_NOT_SUPPORTED';
var LocalStorageService = /** @class */ (function () {
    function LocalStorageService(config) {
        var _this = this;
        this.isSupported = false;
        this.notifyOptions = {
            setItem: false,
            removeItem: false
        };
        this.prefix = 'ls';
        this.storageType = 'localStorage';
        this.errors = new rxjs_1.Subscriber();
        this.removeItems = new rxjs_1.Subscriber();
        this.setItems = new rxjs_1.Subscriber();
        this.warnings = new rxjs_1.Subscriber();
        var notifyOptions = config.notifyOptions, prefix = config.prefix, storageType = config.storageType;
        if (notifyOptions != null) {
            var setItem = notifyOptions.setItem, removeItem = notifyOptions.removeItem;
            this.setNotify(!!setItem, !!removeItem);
        }
        if (prefix != null) {
            this.setPrefix(prefix);
        }
        if (storageType != null) {
            this.setStorageType(storageType);
        }
        this.errors$ = new rxjs_1.Observable(function (observer) { return _this.errors = observer; }).pipe(operators_1.share());
        this.removeItems$ = new rxjs_1.Observable(function (observer) { return _this.removeItems = observer; }).pipe(operators_1.share());
        this.setItems$ = new rxjs_1.Observable(function (observer) { return _this.setItems = observer; }).pipe(operators_1.share());
        this.warnings$ = new rxjs_1.Observable(function (observer) { return _this.warnings = observer; }).pipe(operators_1.share());
        this.isSupported = this.checkSupport();
    }
    LocalStorageService.prototype.add = function (key, value) {
        if (console && console.warn) {
            console.warn(DEPRECATED);
            console.warn('Use `LocalStorageService.set` instead.');
        }
        return this.set(key, value);
    };
    LocalStorageService.prototype.clearAll = function (regularExpression) {
        // Setting both regular expressions independently
        // Empty strings result in catchall RegExp
        var prefixRegex = !!this.prefix ? new RegExp('^' + this.prefix) : new RegExp('');
        var testRegex = !!regularExpression ? new RegExp(regularExpression) : new RegExp('');
        if (!this.isSupported) {
            this.warnings.next(LOCAL_STORAGE_NOT_SUPPORTED);
            return false;
        }
        var prefixLength = this.prefix.length;
        for (var key in this.webStorage) {
            // Only remove items that are for this app and match the regular expression
            if (prefixRegex.test(key) && testRegex.test(key.substr(prefixLength))) {
                try {
                    this.remove(key.substr(prefixLength));
                }
                catch (e) {
                    this.errors.next(e.message);
                    return false;
                }
            }
        }
        return true;
    };
    LocalStorageService.prototype.deriveKey = function (key) {
        return "" + this.prefix + key;
    };
    LocalStorageService.prototype.get = function (key) {
        if (!this.isSupported) {
            this.warnings.next(LOCAL_STORAGE_NOT_SUPPORTED);
            return null;
        }
        var item = this.webStorage ? this.webStorage.getItem(this.deriveKey(key)) : null;
        // FIXME: not a perfect solution, since a valid 'null' string can't be stored
        if (!item || item === 'null') {
            return null;
        }
        try {
            return JSON.parse(item);
        }
        catch (e) {
            return null;
        }
    };
    LocalStorageService.prototype.getStorageType = function () {
        return this.storageType;
    };
    LocalStorageService.prototype.keys = function () {
        if (!this.isSupported) {
            this.warnings.next(LOCAL_STORAGE_NOT_SUPPORTED);
            return [];
        }
        var prefixLength = this.prefix.length;
        var keys = [];
        for (var key in this.webStorage) {
            // Only return keys that are for this app
            if (key.substr(0, prefixLength) === this.prefix) {
                try {
                    keys.push(key.substr(prefixLength));
                }
                catch (e) {
                    this.errors.next(e.message);
                    return [];
                }
            }
        }
        return keys;
    };
    LocalStorageService.prototype.length = function () {
        var count = 0;
        var storage = this.webStorage;
        for (var i = 0; i < storage.length; i++) {
            if (storage.key(i).indexOf(this.prefix) === 0) {
                count += 1;
            }
        }
        return count;
    };
    LocalStorageService.prototype.remove = function () {
        var _this = this;
        var keys = [];
        for (var _i = 0; _i < arguments.length; _i++) {
            keys[_i] = arguments[_i];
        }
        var result = true;
        keys.forEach(function (key) {
            if (!_this.isSupported) {
                _this.warnings.next(LOCAL_STORAGE_NOT_SUPPORTED);
                result = false;
            }
            try {
                _this.webStorage.removeItem(_this.deriveKey(key));
                if (_this.notifyOptions.removeItem) {
                    _this.removeItems.next({
                        key: key,
                        storageType: _this.storageType
                    });
                }
            }
            catch (e) {
                _this.errors.next(e.message);
                result = false;
            }
        });
        return result;
    };
    LocalStorageService.prototype.set = function (key, value) {
        // Let's convert `undefined` values to `null` to get the value consistent
        if (value === undefined) {
            value = null;
        }
        else {
            value = JSON.stringify(value);
        }
        if (!this.isSupported) {
            this.warnings.next(LOCAL_STORAGE_NOT_SUPPORTED);
            return false;
        }
        try {
            if (this.webStorage) {
                this.webStorage.setItem(this.deriveKey(key), value);
            }
            if (this.notifyOptions.setItem) {
                this.setItems.next({
                    key: key,
                    newvalue: value,
                    storageType: this.storageType
                });
            }
        }
        catch (e) {
            this.errors.next(e.message);
            return false;
        }
        return true;
    };
    LocalStorageService.prototype.checkSupport = function () {
        try {
            var supported = this.storageType in window
                && window[this.storageType] !== null;
            if (supported) {
                this.webStorage = window[this.storageType];
                // When Safari (OS X or iOS) is in private browsing mode, it
                // appears as though localStorage is available, but trying to
                // call .setItem throws an exception.
                //
                // "QUOTA_EXCEEDED_ERR: DOM Exception 22: An attempt was made
                // to add something to storage that exceeded the quota."
                var key = this.deriveKey("__" + Math.round(Math.random() * 1e7));
                this.webStorage.setItem(key, '');
                this.webStorage.removeItem(key);
            }
            return supported;
        }
        catch (e) {
            this.errors.next(e.message);
            return false;
        }
    };
    LocalStorageService.prototype.setPrefix = function (prefix) {
        this.prefix = prefix;
        // If there is a prefix set in the config let's use that with an appended
        // period for readability:
        var PERIOD = '.';
        if (this.prefix && !this.prefix.endsWith(PERIOD)) {
            this.prefix = !!this.prefix ? "" + this.prefix + PERIOD : '';
        }
    };
    LocalStorageService.prototype.setStorageType = function (storageType) {
        this.storageType = storageType;
    };
    LocalStorageService.prototype.setNotify = function (setItem, removeItem) {
        if (setItem != null) {
            this.notifyOptions.setItem = setItem;
        }
        if (removeItem != null) {
            this.notifyOptions.removeItem = removeItem;
        }
    };
    LocalStorageService = __decorate([
        core_1.Injectable(),
        __param(0, core_1.Inject('LOCAL_STORAGE_SERVICE_CONFIG')),
        __metadata("design:paramtypes", [Object])
    ], LocalStorageService);
    return LocalStorageService;
}());
exports.LocalStorageService = LocalStorageService;
//# sourceMappingURL=local-storage.service.js.map

/***/ }),

/***/ "./node_modules/blob/index.js":
/*!************************************!*\
  !*** ./node_modules/blob/index.js ***!
  \************************************/
/*! no static exports found */
/***/ (function(module, exports) {

/**
 * Create a blob builder even when vendor prefixes exist
 */

var BlobBuilder = global.BlobBuilder
  || global.WebKitBlobBuilder
  || global.MSBlobBuilder
  || global.MozBlobBuilder;

/**
 * Check if Blob constructor is supported
 */

var blobSupported = (function() {
  try {
    var a = new Blob(['hi']);
    return a.size === 2;
  } catch(e) {
    return false;
  }
})();

/**
 * Check if Blob constructor supports ArrayBufferViews
 * Fails in Safari 6, so we need to map to ArrayBuffers there.
 */

var blobSupportsArrayBufferView = blobSupported && (function() {
  try {
    var b = new Blob([new Uint8Array([1,2])]);
    return b.size === 2;
  } catch(e) {
    return false;
  }
})();

/**
 * Check if BlobBuilder is supported
 */

var blobBuilderSupported = BlobBuilder
  && BlobBuilder.prototype.append
  && BlobBuilder.prototype.getBlob;

/**
 * Helper function that maps ArrayBufferViews to ArrayBuffers
 * Used by BlobBuilder constructor and old browsers that didn't
 * support it in the Blob constructor.
 */

function mapArrayBufferViews(ary) {
  for (var i = 0; i < ary.length; i++) {
    var chunk = ary[i];
    if (chunk.buffer instanceof ArrayBuffer) {
      var buf = chunk.buffer;

      // if this is a subarray, make a copy so we only
      // include the subarray region from the underlying buffer
      if (chunk.byteLength !== buf.byteLength) {
        var copy = new Uint8Array(chunk.byteLength);
        copy.set(new Uint8Array(buf, chunk.byteOffset, chunk.byteLength));
        buf = copy.buffer;
      }

      ary[i] = buf;
    }
  }
}

function BlobBuilderConstructor(ary, options) {
  options = options || {};

  var bb = new BlobBuilder();
  mapArrayBufferViews(ary);

  for (var i = 0; i < ary.length; i++) {
    bb.append(ary[i]);
  }

  return (options.type) ? bb.getBlob(options.type) : bb.getBlob();
};

function BlobConstructor(ary, options) {
  mapArrayBufferViews(ary);
  return new Blob(ary, options || {});
};

module.exports = (function() {
  if (blobSupported) {
    return blobSupportsArrayBufferView ? global.Blob : BlobConstructor;
  } else if (blobBuilderSupported) {
    return BlobBuilderConstructor;
  } else {
    return undefined;
  }
})();


/***/ }),

/***/ "./node_modules/inherits/inherits_browser.js":
/*!***************************************************!*\
  !*** ./node_modules/inherits/inherits_browser.js ***!
  \***************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

if (typeof Object.create === 'function') {
  // implementation from standard node.js 'util' module
  module.exports = function inherits(ctor, superCtor) {
    ctor.super_ = superCtor
    ctor.prototype = Object.create(superCtor.prototype, {
      constructor: {
        value: ctor,
        enumerable: false,
        writable: true,
        configurable: true
      }
    });
  };
} else {
  // old school shim for old browsers
  module.exports = function inherits(ctor, superCtor) {
    ctor.super_ = superCtor
    var TempCtor = function () {}
    TempCtor.prototype = superCtor.prototype
    ctor.prototype = new TempCtor()
    ctor.prototype.constructor = ctor
  }
}


/***/ }),

/***/ "./node_modules/lodash/_basePick.js":
/*!******************************************!*\
  !*** ./node_modules/lodash/_basePick.js ***!
  \******************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var basePickBy = __webpack_require__(/*! ./_basePickBy */ "./node_modules/lodash/_basePickBy.js"),
    hasIn = __webpack_require__(/*! ./hasIn */ "./node_modules/lodash/hasIn.js");

/**
 * The base implementation of `_.pick` without support for individual
 * property identifiers.
 *
 * @private
 * @param {Object} object The source object.
 * @param {string[]} paths The property paths to pick.
 * @returns {Object} Returns the new object.
 */
function basePick(object, paths) {
  return basePickBy(object, paths, function(value, path) {
    return hasIn(object, path);
  });
}

module.exports = basePick;


/***/ }),

/***/ "./node_modules/lodash/fp/first.js":
/*!*****************************************!*\
  !*** ./node_modules/lodash/fp/first.js ***!
  \*****************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__(/*! ./head */ "./node_modules/lodash/fp/head.js");


/***/ }),

/***/ "./node_modules/lodash/fp/head.js":
/*!****************************************!*\
  !*** ./node_modules/lodash/fp/head.js ***!
  \****************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var convert = __webpack_require__(/*! ./convert */ "./node_modules/lodash/fp/convert.js"),
    func = convert('head', __webpack_require__(/*! ../head */ "./node_modules/lodash/head.js"), __webpack_require__(/*! ./_falseOptions */ "./node_modules/lodash/fp/_falseOptions.js"));

func.placeholder = __webpack_require__(/*! ./placeholder */ "./node_modules/lodash/fp/placeholder.js");
module.exports = func;


/***/ }),

/***/ "./node_modules/lodash/fp/pick.js":
/*!****************************************!*\
  !*** ./node_modules/lodash/fp/pick.js ***!
  \****************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var convert = __webpack_require__(/*! ./convert */ "./node_modules/lodash/fp/convert.js"),
    func = convert('pick', __webpack_require__(/*! ../pick */ "./node_modules/lodash/pick.js"));

func.placeholder = __webpack_require__(/*! ./placeholder */ "./node_modules/lodash/fp/placeholder.js");
module.exports = func;


/***/ }),

/***/ "./node_modules/lodash/pick.js":
/*!*************************************!*\
  !*** ./node_modules/lodash/pick.js ***!
  \*************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var basePick = __webpack_require__(/*! ./_basePick */ "./node_modules/lodash/_basePick.js"),
    flatRest = __webpack_require__(/*! ./_flatRest */ "./node_modules/lodash/_flatRest.js");

/**
 * Creates an object composed of the picked `object` properties.
 *
 * @static
 * @since 0.1.0
 * @memberOf _
 * @category Object
 * @param {Object} object The source object.
 * @param {...(string|string[])} [paths] The property paths to pick.
 * @returns {Object} Returns the new object.
 * @example
 *
 * var object = { 'a': 1, 'b': '2', 'c': 3 };
 *
 * _.pick(object, ['a', 'c']);
 * // => { 'a': 1, 'c': 3 }
 */
var pick = flatRest(function(object, paths) {
  return object == null ? {} : basePick(object, paths);
});

module.exports = pick;


/***/ }),

/***/ "./node_modules/lodash/slice.js":
/*!**************************************!*\
  !*** ./node_modules/lodash/slice.js ***!
  \**************************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

var baseSlice = __webpack_require__(/*! ./_baseSlice */ "./node_modules/lodash/_baseSlice.js"),
    isIterateeCall = __webpack_require__(/*! ./_isIterateeCall */ "./node_modules/lodash/_isIterateeCall.js"),
    toInteger = __webpack_require__(/*! ./toInteger */ "./node_modules/lodash/toInteger.js");

/**
 * Creates a slice of `array` from `start` up to, but not including, `end`.
 *
 * **Note:** This method is used instead of
 * [`Array#slice`](https://mdn.io/Array/slice) to ensure dense arrays are
 * returned.
 *
 * @static
 * @memberOf _
 * @since 3.0.0
 * @category Array
 * @param {Array} array The array to slice.
 * @param {number} [start=0] The start position.
 * @param {number} [end=array.length] The end position.
 * @returns {Array} Returns the slice of `array`.
 */
function slice(array, start, end) {
  var length = array == null ? 0 : array.length;
  if (!length) {
    return [];
  }
  if (end && typeof end != 'number' && isIterateeCall(array, start, end)) {
    start = 0;
    end = length;
  }
  else {
    start = start == null ? 0 : toInteger(start);
    end = end === undefined ? length : toInteger(end);
  }
  return baseSlice(array, start, end);
}

module.exports = slice;


/***/ }),

/***/ "./node_modules/util/support/isBufferBrowser.js":
/*!******************************************************!*\
  !*** ./node_modules/util/support/isBufferBrowser.js ***!
  \******************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = function isBuffer(arg) {
  return arg && typeof arg === 'object'
    && typeof arg.copy === 'function'
    && typeof arg.fill === 'function'
    && typeof arg.readUInt8 === 'function';
}

/***/ }),

/***/ "./node_modules/util/util.js":
/*!***********************************!*\
  !*** ./node_modules/util/util.js ***!
  \***********************************/
/*! no static exports found */
/***/ (function(module, exports, __webpack_require__) {

// Copyright Joyent, Inc. and other Node contributors.
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions:
//
// The above copyright notice and this permission notice shall be included
// in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.

var getOwnPropertyDescriptors = Object.getOwnPropertyDescriptors ||
  function getOwnPropertyDescriptors(obj) {
    var keys = Object.keys(obj);
    var descriptors = {};
    for (var i = 0; i < keys.length; i++) {
      descriptors[keys[i]] = Object.getOwnPropertyDescriptor(obj, keys[i]);
    }
    return descriptors;
  };

var formatRegExp = /%[sdj%]/g;
exports.format = function(f) {
  if (!isString(f)) {
    var objects = [];
    for (var i = 0; i < arguments.length; i++) {
      objects.push(inspect(arguments[i]));
    }
    return objects.join(' ');
  }

  var i = 1;
  var args = arguments;
  var len = args.length;
  var str = String(f).replace(formatRegExp, function(x) {
    if (x === '%%') return '%';
    if (i >= len) return x;
    switch (x) {
      case '%s': return String(args[i++]);
      case '%d': return Number(args[i++]);
      case '%j':
        try {
          return JSON.stringify(args[i++]);
        } catch (_) {
          return '[Circular]';
        }
      default:
        return x;
    }
  });
  for (var x = args[i]; i < len; x = args[++i]) {
    if (isNull(x) || !isObject(x)) {
      str += ' ' + x;
    } else {
      str += ' ' + inspect(x);
    }
  }
  return str;
};


// Mark that a method should not be used.
// Returns a modified function which warns once by default.
// If --no-deprecation is set, then it is a no-op.
exports.deprecate = function(fn, msg) {
  if (typeof process !== 'undefined' && process.noDeprecation === true) {
    return fn;
  }

  // Allow for deprecating things in the process of starting up.
  if (typeof process === 'undefined') {
    return function() {
      return exports.deprecate(fn, msg).apply(this, arguments);
    };
  }

  var warned = false;
  function deprecated() {
    if (!warned) {
      if (process.throwDeprecation) {
        throw new Error(msg);
      } else if (process.traceDeprecation) {
        console.trace(msg);
      } else {
        console.error(msg);
      }
      warned = true;
    }
    return fn.apply(this, arguments);
  }

  return deprecated;
};


var debugs = {};
var debugEnviron;
exports.debuglog = function(set) {
  if (isUndefined(debugEnviron))
    debugEnviron = process.env.NODE_DEBUG || '';
  set = set.toUpperCase();
  if (!debugs[set]) {
    if (new RegExp('\\b' + set + '\\b', 'i').test(debugEnviron)) {
      var pid = process.pid;
      debugs[set] = function() {
        var msg = exports.format.apply(exports, arguments);
        console.error('%s %d: %s', set, pid, msg);
      };
    } else {
      debugs[set] = function() {};
    }
  }
  return debugs[set];
};


/**
 * Echos the value of a value. Trys to print the value out
 * in the best way possible given the different types.
 *
 * @param {Object} obj The object to print out.
 * @param {Object} opts Optional options object that alters the output.
 */
/* legacy: obj, showHidden, depth, colors*/
function inspect(obj, opts) {
  // default options
  var ctx = {
    seen: [],
    stylize: stylizeNoColor
  };
  // legacy...
  if (arguments.length >= 3) ctx.depth = arguments[2];
  if (arguments.length >= 4) ctx.colors = arguments[3];
  if (isBoolean(opts)) {
    // legacy...
    ctx.showHidden = opts;
  } else if (opts) {
    // got an "options" object
    exports._extend(ctx, opts);
  }
  // set default options
  if (isUndefined(ctx.showHidden)) ctx.showHidden = false;
  if (isUndefined(ctx.depth)) ctx.depth = 2;
  if (isUndefined(ctx.colors)) ctx.colors = false;
  if (isUndefined(ctx.customInspect)) ctx.customInspect = true;
  if (ctx.colors) ctx.stylize = stylizeWithColor;
  return formatValue(ctx, obj, ctx.depth);
}
exports.inspect = inspect;


// http://en.wikipedia.org/wiki/ANSI_escape_code#graphics
inspect.colors = {
  'bold' : [1, 22],
  'italic' : [3, 23],
  'underline' : [4, 24],
  'inverse' : [7, 27],
  'white' : [37, 39],
  'grey' : [90, 39],
  'black' : [30, 39],
  'blue' : [34, 39],
  'cyan' : [36, 39],
  'green' : [32, 39],
  'magenta' : [35, 39],
  'red' : [31, 39],
  'yellow' : [33, 39]
};

// Don't use 'blue' not visible on cmd.exe
inspect.styles = {
  'special': 'cyan',
  'number': 'yellow',
  'boolean': 'yellow',
  'undefined': 'grey',
  'null': 'bold',
  'string': 'green',
  'date': 'magenta',
  // "name": intentionally not styling
  'regexp': 'red'
};


function stylizeWithColor(str, styleType) {
  var style = inspect.styles[styleType];

  if (style) {
    return '\u001b[' + inspect.colors[style][0] + 'm' + str +
           '\u001b[' + inspect.colors[style][1] + 'm';
  } else {
    return str;
  }
}


function stylizeNoColor(str, styleType) {
  return str;
}


function arrayToHash(array) {
  var hash = {};

  array.forEach(function(val, idx) {
    hash[val] = true;
  });

  return hash;
}


function formatValue(ctx, value, recurseTimes) {
  // Provide a hook for user-specified inspect functions.
  // Check that value is an object with an inspect function on it
  if (ctx.customInspect &&
      value &&
      isFunction(value.inspect) &&
      // Filter out the util module, it's inspect function is special
      value.inspect !== exports.inspect &&
      // Also filter out any prototype objects using the circular check.
      !(value.constructor && value.constructor.prototype === value)) {
    var ret = value.inspect(recurseTimes, ctx);
    if (!isString(ret)) {
      ret = formatValue(ctx, ret, recurseTimes);
    }
    return ret;
  }

  // Primitive types cannot have properties
  var primitive = formatPrimitive(ctx, value);
  if (primitive) {
    return primitive;
  }

  // Look up the keys of the object.
  var keys = Object.keys(value);
  var visibleKeys = arrayToHash(keys);

  if (ctx.showHidden) {
    keys = Object.getOwnPropertyNames(value);
  }

  // IE doesn't make error fields non-enumerable
  // http://msdn.microsoft.com/en-us/library/ie/dww52sbt(v=vs.94).aspx
  if (isError(value)
      && (keys.indexOf('message') >= 0 || keys.indexOf('description') >= 0)) {
    return formatError(value);
  }

  // Some type of object without properties can be shortcutted.
  if (keys.length === 0) {
    if (isFunction(value)) {
      var name = value.name ? ': ' + value.name : '';
      return ctx.stylize('[Function' + name + ']', 'special');
    }
    if (isRegExp(value)) {
      return ctx.stylize(RegExp.prototype.toString.call(value), 'regexp');
    }
    if (isDate(value)) {
      return ctx.stylize(Date.prototype.toString.call(value), 'date');
    }
    if (isError(value)) {
      return formatError(value);
    }
  }

  var base = '', array = false, braces = ['{', '}'];

  // Make Array say that they are Array
  if (isArray(value)) {
    array = true;
    braces = ['[', ']'];
  }

  // Make functions say that they are functions
  if (isFunction(value)) {
    var n = value.name ? ': ' + value.name : '';
    base = ' [Function' + n + ']';
  }

  // Make RegExps say that they are RegExps
  if (isRegExp(value)) {
    base = ' ' + RegExp.prototype.toString.call(value);
  }

  // Make dates with properties first say the date
  if (isDate(value)) {
    base = ' ' + Date.prototype.toUTCString.call(value);
  }

  // Make error with message first say the error
  if (isError(value)) {
    base = ' ' + formatError(value);
  }

  if (keys.length === 0 && (!array || value.length == 0)) {
    return braces[0] + base + braces[1];
  }

  if (recurseTimes < 0) {
    if (isRegExp(value)) {
      return ctx.stylize(RegExp.prototype.toString.call(value), 'regexp');
    } else {
      return ctx.stylize('[Object]', 'special');
    }
  }

  ctx.seen.push(value);

  var output;
  if (array) {
    output = formatArray(ctx, value, recurseTimes, visibleKeys, keys);
  } else {
    output = keys.map(function(key) {
      return formatProperty(ctx, value, recurseTimes, visibleKeys, key, array);
    });
  }

  ctx.seen.pop();

  return reduceToSingleString(output, base, braces);
}


function formatPrimitive(ctx, value) {
  if (isUndefined(value))
    return ctx.stylize('undefined', 'undefined');
  if (isString(value)) {
    var simple = '\'' + JSON.stringify(value).replace(/^"|"$/g, '')
                                             .replace(/'/g, "\\'")
                                             .replace(/\\"/g, '"') + '\'';
    return ctx.stylize(simple, 'string');
  }
  if (isNumber(value))
    return ctx.stylize('' + value, 'number');
  if (isBoolean(value))
    return ctx.stylize('' + value, 'boolean');
  // For some reason typeof null is "object", so special case here.
  if (isNull(value))
    return ctx.stylize('null', 'null');
}


function formatError(value) {
  return '[' + Error.prototype.toString.call(value) + ']';
}


function formatArray(ctx, value, recurseTimes, visibleKeys, keys) {
  var output = [];
  for (var i = 0, l = value.length; i < l; ++i) {
    if (hasOwnProperty(value, String(i))) {
      output.push(formatProperty(ctx, value, recurseTimes, visibleKeys,
          String(i), true));
    } else {
      output.push('');
    }
  }
  keys.forEach(function(key) {
    if (!key.match(/^\d+$/)) {
      output.push(formatProperty(ctx, value, recurseTimes, visibleKeys,
          key, true));
    }
  });
  return output;
}


function formatProperty(ctx, value, recurseTimes, visibleKeys, key, array) {
  var name, str, desc;
  desc = Object.getOwnPropertyDescriptor(value, key) || { value: value[key] };
  if (desc.get) {
    if (desc.set) {
      str = ctx.stylize('[Getter/Setter]', 'special');
    } else {
      str = ctx.stylize('[Getter]', 'special');
    }
  } else {
    if (desc.set) {
      str = ctx.stylize('[Setter]', 'special');
    }
  }
  if (!hasOwnProperty(visibleKeys, key)) {
    name = '[' + key + ']';
  }
  if (!str) {
    if (ctx.seen.indexOf(desc.value) < 0) {
      if (isNull(recurseTimes)) {
        str = formatValue(ctx, desc.value, null);
      } else {
        str = formatValue(ctx, desc.value, recurseTimes - 1);
      }
      if (str.indexOf('\n') > -1) {
        if (array) {
          str = str.split('\n').map(function(line) {
            return '  ' + line;
          }).join('\n').substr(2);
        } else {
          str = '\n' + str.split('\n').map(function(line) {
            return '   ' + line;
          }).join('\n');
        }
      }
    } else {
      str = ctx.stylize('[Circular]', 'special');
    }
  }
  if (isUndefined(name)) {
    if (array && key.match(/^\d+$/)) {
      return str;
    }
    name = JSON.stringify('' + key);
    if (name.match(/^"([a-zA-Z_][a-zA-Z_0-9]*)"$/)) {
      name = name.substr(1, name.length - 2);
      name = ctx.stylize(name, 'name');
    } else {
      name = name.replace(/'/g, "\\'")
                 .replace(/\\"/g, '"')
                 .replace(/(^"|"$)/g, "'");
      name = ctx.stylize(name, 'string');
    }
  }

  return name + ': ' + str;
}


function reduceToSingleString(output, base, braces) {
  var numLinesEst = 0;
  var length = output.reduce(function(prev, cur) {
    numLinesEst++;
    if (cur.indexOf('\n') >= 0) numLinesEst++;
    return prev + cur.replace(/\u001b\[\d\d?m/g, '').length + 1;
  }, 0);

  if (length > 60) {
    return braces[0] +
           (base === '' ? '' : base + '\n ') +
           ' ' +
           output.join(',\n  ') +
           ' ' +
           braces[1];
  }

  return braces[0] + base + ' ' + output.join(', ') + ' ' + braces[1];
}


// NOTE: These type checking functions intentionally don't use `instanceof`
// because it is fragile and can be easily faked with `Object.create()`.
function isArray(ar) {
  return Array.isArray(ar);
}
exports.isArray = isArray;

function isBoolean(arg) {
  return typeof arg === 'boolean';
}
exports.isBoolean = isBoolean;

function isNull(arg) {
  return arg === null;
}
exports.isNull = isNull;

function isNullOrUndefined(arg) {
  return arg == null;
}
exports.isNullOrUndefined = isNullOrUndefined;

function isNumber(arg) {
  return typeof arg === 'number';
}
exports.isNumber = isNumber;

function isString(arg) {
  return typeof arg === 'string';
}
exports.isString = isString;

function isSymbol(arg) {
  return typeof arg === 'symbol';
}
exports.isSymbol = isSymbol;

function isUndefined(arg) {
  return arg === void 0;
}
exports.isUndefined = isUndefined;

function isRegExp(re) {
  return isObject(re) && objectToString(re) === '[object RegExp]';
}
exports.isRegExp = isRegExp;

function isObject(arg) {
  return typeof arg === 'object' && arg !== null;
}
exports.isObject = isObject;

function isDate(d) {
  return isObject(d) && objectToString(d) === '[object Date]';
}
exports.isDate = isDate;

function isError(e) {
  return isObject(e) &&
      (objectToString(e) === '[object Error]' || e instanceof Error);
}
exports.isError = isError;

function isFunction(arg) {
  return typeof arg === 'function';
}
exports.isFunction = isFunction;

function isPrimitive(arg) {
  return arg === null ||
         typeof arg === 'boolean' ||
         typeof arg === 'number' ||
         typeof arg === 'string' ||
         typeof arg === 'symbol' ||  // ES6 symbol
         typeof arg === 'undefined';
}
exports.isPrimitive = isPrimitive;

exports.isBuffer = __webpack_require__(/*! ./support/isBuffer */ "./node_modules/util/support/isBufferBrowser.js");

function objectToString(o) {
  return Object.prototype.toString.call(o);
}


function pad(n) {
  return n < 10 ? '0' + n.toString(10) : n.toString(10);
}


var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep',
              'Oct', 'Nov', 'Dec'];

// 26 Feb 16:19:34
function timestamp() {
  var d = new Date();
  var time = [pad(d.getHours()),
              pad(d.getMinutes()),
              pad(d.getSeconds())].join(':');
  return [d.getDate(), months[d.getMonth()], time].join(' ');
}


// log is just a thin wrapper to console.log that prepends a timestamp
exports.log = function() {
  console.log('%s - %s', timestamp(), exports.format.apply(exports, arguments));
};


/**
 * Inherit the prototype methods from one constructor into another.
 *
 * The Function.prototype.inherits from lang.js rewritten as a standalone
 * function (not on Function.prototype). NOTE: If this file is to be loaded
 * during bootstrapping this function needs to be rewritten using some native
 * functions as prototype setup using normal JavaScript does not work as
 * expected during bootstrapping (see mirror.js in r114903).
 *
 * @param {function} ctor Constructor function which needs to inherit the
 *     prototype.
 * @param {function} superCtor Constructor function to inherit prototype from.
 */
exports.inherits = __webpack_require__(/*! inherits */ "./node_modules/inherits/inherits_browser.js");

exports._extend = function(origin, add) {
  // Don't do anything if add isn't an object
  if (!add || !isObject(add)) return origin;

  var keys = Object.keys(add);
  var i = keys.length;
  while (i--) {
    origin[keys[i]] = add[keys[i]];
  }
  return origin;
};

function hasOwnProperty(obj, prop) {
  return Object.prototype.hasOwnProperty.call(obj, prop);
}

var kCustomPromisifiedSymbol = typeof Symbol !== 'undefined' ? Symbol('util.promisify.custom') : undefined;

exports.promisify = function promisify(original) {
  if (typeof original !== 'function')
    throw new TypeError('The "original" argument must be of type Function');

  if (kCustomPromisifiedSymbol && original[kCustomPromisifiedSymbol]) {
    var fn = original[kCustomPromisifiedSymbol];
    if (typeof fn !== 'function') {
      throw new TypeError('The "util.promisify.custom" argument must be of type Function');
    }
    Object.defineProperty(fn, kCustomPromisifiedSymbol, {
      value: fn, enumerable: false, writable: false, configurable: true
    });
    return fn;
  }

  function fn() {
    var promiseResolve, promiseReject;
    var promise = new Promise(function (resolve, reject) {
      promiseResolve = resolve;
      promiseReject = reject;
    });

    var args = [];
    for (var i = 0; i < arguments.length; i++) {
      args.push(arguments[i]);
    }
    args.push(function (err, value) {
      if (err) {
        promiseReject(err);
      } else {
        promiseResolve(value);
      }
    });

    try {
      original.apply(this, args);
    } catch (err) {
      promiseReject(err);
    }

    return promise;
  }

  Object.setPrototypeOf(fn, Object.getPrototypeOf(original));

  if (kCustomPromisifiedSymbol) Object.defineProperty(fn, kCustomPromisifiedSymbol, {
    value: fn, enumerable: false, writable: false, configurable: true
  });
  return Object.defineProperties(
    fn,
    getOwnPropertyDescriptors(original)
  );
}

exports.promisify.custom = kCustomPromisifiedSymbol

function callbackifyOnRejected(reason, cb) {
  // `!reason` guard inspired by bluebird (Ref: https://goo.gl/t5IS6M).
  // Because `null` is a special error value in callbacks which means "no error
  // occurred", we error-wrap so the callback consumer can distinguish between
  // "the promise rejected with null" or "the promise fulfilled with undefined".
  if (!reason) {
    var newReason = new Error('Promise was rejected with a falsy value');
    newReason.reason = reason;
    reason = newReason;
  }
  return cb(reason);
}

function callbackify(original) {
  if (typeof original !== 'function') {
    throw new TypeError('The "original" argument must be of type Function');
  }

  // We DO NOT return the promise as it gives the user a false sense that
  // the promise is actually somehow related to the callback's execution
  // and that the callback throwing will reject the promise.
  function callbackified() {
    var args = [];
    for (var i = 0; i < arguments.length; i++) {
      args.push(arguments[i]);
    }

    var maybeCb = args.pop();
    if (typeof maybeCb !== 'function') {
      throw new TypeError('The last argument must be of type Function');
    }
    var self = this;
    var cb = function() {
      return maybeCb.apply(self, arguments);
    };
    // In true node style we process the callback on `nextTick` with all the
    // implications (stack, `uncaughtException`, `async_hooks`)
    original.apply(this, args)
      .then(function(ret) { process.nextTick(cb, null, ret) },
            function(rej) { process.nextTick(callbackifyOnRejected, rej, cb) });
  }

  Object.setPrototypeOf(callbackified, Object.getPrototypeOf(original));
  Object.defineProperties(callbackified,
                          getOwnPropertyDescriptors(original));
  return callbackified;
}
exports.callbackify = callbackify;


/***/ }),

/***/ "./src/app/common/local-storage-keys.ts":
/*!**********************************************!*\
  !*** ./src/app/common/local-storage-keys.ts ***!
  \**********************************************/
/*! exports provided: LAST_ANALYSES_CATEGORY_ID */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "LAST_ANALYSES_CATEGORY_ID", function() { return LAST_ANALYSES_CATEGORY_ID; });
var LAST_ANALYSES_CATEGORY_ID = 'lastAnalysesListId';


/***/ }),

/***/ "./src/app/modules/analyze/actions/analyze-actions-menu.component.html":
/*!*****************************************************************************!*\
  !*** ./src/app/modules/analyze/actions/analyze-actions-menu.component.html ***!
  \*****************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<ng-container *ngIf=\"actions.length && actions.length > 0\">\n  <button mat-icon-button [matMenuTriggerFor]=\"menu\" e2e=\"actions-menu-toggle\">\n    <mat-icon fontIcon=\"icon-more\"></mat-icon>\n  </button>\n  <mat-menu #menu=\"matMenu\">\n    <button *ngFor=\"let action of actions; trackBy: index\"\n            (click)=\"action.fn()\"\n            [disabled]=\"actionsToDisable[action.value]\"\n            [style.color]=\"action.color\"\n            [attr.e2e]=\"'actions-menu-selector-' + action.value\"\n            mat-menu-item\n    >\n      {{action.label}}\n    </button>\n  </mat-menu>\n</ng-container>\n"

/***/ }),

/***/ "./src/app/modules/analyze/actions/analyze-actions-menu.component.ts":
/*!***************************************************************************!*\
  !*** ./src/app/modules/analyze/actions/analyze-actions-menu.component.ts ***!
  \***************************************************************************/
/*! exports provided: AnalyzeActionsMenuComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnalyzeActionsMenuComponent", function() { return AnalyzeActionsMenuComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! lodash/filter */ "./node_modules/lodash/filter.js");
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(lodash_filter__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/fp/pipe */ "./node_modules/lodash/fp/pipe.js");
/* harmony import */ var lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var lodash_fp_reduce__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/fp/reduce */ "./node_modules/lodash/fp/reduce.js");
/* harmony import */ var lodash_fp_reduce__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_reduce__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_isString__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/isString */ "./node_modules/lodash/isString.js");
/* harmony import */ var lodash_isString__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_isString__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var lodash_upperCase__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/upperCase */ "./node_modules/lodash/upperCase.js");
/* harmony import */ var lodash_upperCase__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_upperCase__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var _analyze_actions_service__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./analyze-actions.service */ "./src/app/modules/analyze/actions/analyze-actions.service.ts");
/* harmony import */ var _designer_types__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../designer/types */ "./src/app/modules/analyze/designer/types.ts");
/* harmony import */ var lodash_clone__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! lodash/clone */ "./node_modules/lodash/clone.js");
/* harmony import */ var lodash_clone__WEBPACK_IMPORTED_MODULE_10___default = /*#__PURE__*/__webpack_require__.n(lodash_clone__WEBPACK_IMPORTED_MODULE_10__);











var AnalyzeActionsMenuComponent = /** @class */ (function () {
    function AnalyzeActionsMenuComponent(_analyzeActionsService, _jwt) {
        var _this = this;
        this._analyzeActionsService = _analyzeActionsService;
        this._jwt = _jwt;
        this.afterEdit = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.afterExport = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.afterExecute = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.afterDelete = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.afterPublish = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.afterSchedule = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.detailsRequested = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.actionsToDisable = {};
        this.actions = [
            {
                label: 'Details',
                value: 'details',
                fn: function () {
                    _this.detailsRequested.emit(true);
                }
            },
            {
                label: 'Execute',
                value: 'execute',
                fn: this.execute.bind(this)
            },
            {
                label: 'Fork & Edit',
                value: 'fork',
                fn: this.fork.bind(this)
            },
            {
                label: 'Edit',
                value: 'edit',
                fn: this.edit.bind(this)
            },
            {
                label: 'Publish',
                value: 'publish',
                fn: this.publish.bind(this, 'publish')
            },
            {
                label: 'Schedule',
                value: 'publish',
                fn: this.publish.bind(this, 'schedule')
            },
            {
                label: 'Export',
                value: 'export',
                fn: this.export.bind(this)
            },
            {
                label: 'Delete',
                value: 'delete',
                fn: this.delete.bind(this),
                color: 'red'
            }
        ];
    }
    Object.defineProperty(AnalyzeActionsMenuComponent.prototype, "disabledActions", {
        set: function (actionsToDisable) {
            this.actionsToDisable = lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_3__(function (actionsToDisableString) {
                return lodash_isString__WEBPACK_IMPORTED_MODULE_5__(actionsToDisableString)
                    ? actionsToDisableString.split('-')
                    : [];
            }, lodash_fp_reduce__WEBPACK_IMPORTED_MODULE_4__(function (acc, action) {
                acc[action] = true;
                return acc;
            }, {}))(actionsToDisable);
        },
        enumerable: true,
        configurable: true
    });
    AnalyzeActionsMenuComponent.prototype.ngOnInit = function () {
        var _this = this;
        var privilegeMap = { print: 'export', details: 'access' };
        var actionsToExclude = lodash_isString__WEBPACK_IMPORTED_MODULE_5__(this.exclude)
            ? this.exclude.split('-')
            : [];
        this.actions = lodash_filter__WEBPACK_IMPORTED_MODULE_2__(this.actions, function (_a) {
            var value = _a.value;
            var notExcluded = !actionsToExclude.includes(value);
            var privilegeName = lodash_upperCase__WEBPACK_IMPORTED_MODULE_6__(privilegeMap[value] || value);
            var hasPriviledge = _this._jwt.hasPrivilege(privilegeName, {
                subCategoryId: Object(_designer_types__WEBPACK_IMPORTED_MODULE_9__["isDSLAnalysis"])(_this.analysis)
                    ? _this.analysis.category
                    : _this.analysis.categoryId,
                creatorId: _this.analysis.userId ||
                    _this.analysis.createdBy
            });
            return notExcluded && hasPriviledge;
        });
    };
    AnalyzeActionsMenuComponent.prototype.edit = function () {
        this._analyzeActionsService.edit(this.analysis);
    };
    AnalyzeActionsMenuComponent.prototype.fork = function () {
        this._analyzeActionsService.fork(this.analysis);
    };
    AnalyzeActionsMenuComponent.prototype.execute = function () {
        var _this = this;
        this._analyzeActionsService.execute(this.analysis).then(function (analysis) {
            if (analysis) {
                _this.afterExecute.emit(analysis);
            }
        });
    };
    AnalyzeActionsMenuComponent.prototype.delete = function () {
        var _this = this;
        this._analyzeActionsService.delete(this.analysis).then(function (wasSuccessful) {
            if (wasSuccessful) {
                _this.afterDelete.emit(_this.analysis);
            }
        });
    };
    AnalyzeActionsMenuComponent.prototype.publish = function (type) {
        var _this = this;
        var analysis = lodash_clone__WEBPACK_IMPORTED_MODULE_10__(this.analysis);
        this._analyzeActionsService
            .publish(analysis, type)
            .then(function (publishedAnalysis) {
            _this.analysis = publishedAnalysis;
            _this.afterPublish.emit(publishedAnalysis);
        });
    };
    AnalyzeActionsMenuComponent.prototype.export = function () {
        this.afterExport.emit();
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AnalyzeActionsMenuComponent.prototype, "afterEdit", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AnalyzeActionsMenuComponent.prototype, "afterExport", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AnalyzeActionsMenuComponent.prototype, "afterExecute", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AnalyzeActionsMenuComponent.prototype, "afterDelete", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AnalyzeActionsMenuComponent.prototype, "afterPublish", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AnalyzeActionsMenuComponent.prototype, "afterSchedule", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AnalyzeActionsMenuComponent.prototype, "detailsRequested", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], AnalyzeActionsMenuComponent.prototype, "analysis", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", String)
    ], AnalyzeActionsMenuComponent.prototype, "exclude", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])('actionsToDisable'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", String),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [String])
    ], AnalyzeActionsMenuComponent.prototype, "disabledActions", null);
    AnalyzeActionsMenuComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'analyze-actions-menu-u',
            template: __webpack_require__(/*! ./analyze-actions-menu.component.html */ "./src/app/modules/analyze/actions/analyze-actions-menu.component.html")
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_analyze_actions_service__WEBPACK_IMPORTED_MODULE_8__["AnalyzeActionsService"],
            _common_services__WEBPACK_IMPORTED_MODULE_7__["JwtService"]])
    ], AnalyzeActionsMenuComponent);
    return AnalyzeActionsMenuComponent;
}());



/***/ }),

/***/ "./src/app/modules/analyze/actions/index.ts":
/*!**************************************************!*\
  !*** ./src/app/modules/analyze/actions/index.ts ***!
  \**************************************************/
/*! exports provided: AnalyzeActionsModule, AnalyzeActionsService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnalyzeActionsModule", function() { return AnalyzeActionsModule; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../common */ "./src/app/common/index.ts");
/* harmony import */ var _analyze_actions_menu_component__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./analyze-actions-menu.component */ "./src/app/modules/analyze/actions/analyze-actions-menu.component.ts");
/* harmony import */ var _analyze_actions_service__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./analyze-actions.service */ "./src/app/modules/analyze/actions/analyze-actions.service.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AnalyzeActionsService", function() { return _analyze_actions_service__WEBPACK_IMPORTED_MODULE_4__["AnalyzeActionsService"]; });

/* harmony import */ var _services_execute_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../services/execute.service */ "./src/app/modules/analyze/services/execute.service.ts");
/* harmony import */ var _services_publish_service__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../services/publish.service */ "./src/app/modules/analyze/services/publish.service.ts");







var AnalyzeActionsModule = /** @class */ (function () {
    function AnalyzeActionsModule() {
    }
    AnalyzeActionsModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["NgModule"])({
            imports: [
                _common__WEBPACK_IMPORTED_MODULE_2__["CommonModuleTs"]
            ],
            declarations: [_analyze_actions_menu_component__WEBPACK_IMPORTED_MODULE_3__["AnalyzeActionsMenuComponent"]],
            entryComponents: [_analyze_actions_menu_component__WEBPACK_IMPORTED_MODULE_3__["AnalyzeActionsMenuComponent"]],
            providers: [
                _analyze_actions_service__WEBPACK_IMPORTED_MODULE_4__["AnalyzeActionsService"],
                _services_execute_service__WEBPACK_IMPORTED_MODULE_5__["ExecuteService"],
                _services_publish_service__WEBPACK_IMPORTED_MODULE_6__["PublishService"]
            ],
            exports: [
                _analyze_actions_menu_component__WEBPACK_IMPORTED_MODULE_3__["AnalyzeActionsMenuComponent"]
            ]
        })
    ], AnalyzeActionsModule);
    return AnalyzeActionsModule;
}());




/***/ }),

/***/ "./src/app/modules/analyze/analyze.module.ts":
/*!***************************************************!*\
  !*** ./src/app/modules/analyze/analyze.module.ts ***!
  \***************************************************/
/*! exports provided: AnalyzeModule */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnalyzeModule", function() { return AnalyzeModule; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_common__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/common */ "./node_modules/@angular/common/fesm5/common.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _ngxs_store__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @ngxs/store */ "./node_modules/@ngxs/store/fesm5/ngxs-store.js");
/* harmony import */ var _angular_forms__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! @angular/forms */ "./node_modules/@angular/forms/fesm5/forms.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var angular_2_local_storage__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! angular-2-local-storage */ "./node_modules/angular-2-local-storage/dist/index.js");
/* harmony import */ var angular_2_local_storage__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(angular_2_local_storage__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var _view__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./view */ "./src/app/modules/analyze/view/index.ts");
/* harmony import */ var _executed_view__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./executed-view */ "./src/app/modules/analyze/executed-view/index.ts");
/* harmony import */ var _actions__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./actions */ "./src/app/modules/analyze/actions/index.ts");
/* harmony import */ var _designer_index__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./designer/index */ "./src/app/modules/analyze/designer/index.ts");
/* harmony import */ var _routes__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ./routes */ "./src/app/modules/analyze/routes.ts");
/* harmony import */ var _guards__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ./guards */ "./src/app/modules/analyze/guards/index.ts");
/* harmony import */ var _common__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ../../common */ "./src/app/common/index.ts");
/* harmony import */ var _common_components_charts__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ../../common/components/charts */ "./src/app/common/components/charts/index.ts");
/* harmony import */ var _publish__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ./publish */ "./src/app/modules/analyze/publish/index.ts");
/* harmony import */ var _analyze_global_module__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ./analyze.global.module */ "./src/app/modules/analyze/analyze.global.module.ts");
/* harmony import */ var _designer_filter__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(/*! ./designer/filter */ "./src/app/modules/analyze/designer/filter/index.ts");
/* harmony import */ var _page__WEBPACK_IMPORTED_MODULE_18__ = __webpack_require__(/*! ./page */ "./src/app/modules/analyze/page/index.ts");
/* harmony import */ var _state_analyze_state__WEBPACK_IMPORTED_MODULE_19__ = __webpack_require__(/*! ./state/analyze.state */ "./src/app/modules/analyze/state/analyze.state.ts");





















var COMPONENTS = [
    _designer_index__WEBPACK_IMPORTED_MODULE_10__["DesignerPageComponent"],
    _page__WEBPACK_IMPORTED_MODULE_18__["AnalyzePageComponent"]
];
var GUARDS = [_guards__WEBPACK_IMPORTED_MODULE_12__["DefaultAnalyzeCategoryGuard"]];
var AnalyzeModule = /** @class */ (function () {
    function AnalyzeModule() {
    }
    AnalyzeModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_2__["NgModule"])({
            imports: [
                _ngxs_store__WEBPACK_IMPORTED_MODULE_3__["NgxsModule"].forFeature([_state_analyze_state__WEBPACK_IMPORTED_MODULE_19__["AnalyzeState"]]),
                _angular_common__WEBPACK_IMPORTED_MODULE_1__["CommonModule"],
                angular_2_local_storage__WEBPACK_IMPORTED_MODULE_6__["LocalStorageModule"].withConfig({
                    prefix: 'symmetra',
                    storageType: 'localStorage'
                }),
                _designer_index__WEBPACK_IMPORTED_MODULE_10__["AnalyzeDesignerModule"],
                _analyze_global_module__WEBPACK_IMPORTED_MODULE_16__["AnalyzeModuleGlobal"].forRoot(),
                _angular_router__WEBPACK_IMPORTED_MODULE_5__["RouterModule"].forChild(_routes__WEBPACK_IMPORTED_MODULE_11__["routes"]),
                _common__WEBPACK_IMPORTED_MODULE_13__["CommonModuleTs"],
                _angular_forms__WEBPACK_IMPORTED_MODULE_4__["FormsModule"],
                _angular_forms__WEBPACK_IMPORTED_MODULE_4__["ReactiveFormsModule"],
                _common_components_charts__WEBPACK_IMPORTED_MODULE_14__["UChartModule"],
                _view__WEBPACK_IMPORTED_MODULE_7__["AnalyzeViewModule"],
                _executed_view__WEBPACK_IMPORTED_MODULE_8__["ExecutedViewModule"],
                _actions__WEBPACK_IMPORTED_MODULE_9__["AnalyzeActionsModule"],
                _designer_filter__WEBPACK_IMPORTED_MODULE_17__["AnalyzeFilterModule"],
                _publish__WEBPACK_IMPORTED_MODULE_15__["AnalyzePublishDialogModule"]
            ],
            declarations: COMPONENTS.slice(),
            entryComponents: COMPONENTS,
            providers: GUARDS.slice(),
            exports: [_page__WEBPACK_IMPORTED_MODULE_18__["AnalyzePageComponent"]]
        })
    ], AnalyzeModule);
    return AnalyzeModule;
}());



/***/ }),

/***/ "./src/app/modules/analyze/executed-view/chart/executed-chart-view.component.html":
/*!****************************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/chart/executed-chart-view.component.html ***!
  \****************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<chart-grid [analysis]=\"analysis\"\n            [data]=\"data\"\n            [updater]=\"updater\">\n</chart-grid>\n"

/***/ }),

/***/ "./src/app/modules/analyze/executed-view/chart/executed-chart-view.component.ts":
/*!**************************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/chart/executed-chart-view.component.ts ***!
  \**************************************************************************************/
/*! exports provided: ExecutedChartViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExecutedChartViewComponent", function() { return ExecutedChartViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");



var ExecutedChartViewComponent = /** @class */ (function () {
    function ExecutedChartViewComponent() {
    }
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_2__["BehaviorSubject"])
    ], ExecutedChartViewComponent.prototype, "updater", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], ExecutedChartViewComponent.prototype, "analysis", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], ExecutedChartViewComponent.prototype, "data", void 0);
    ExecutedChartViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'executed-chart-view',
            template: __webpack_require__(/*! ./executed-chart-view.component.html */ "./src/app/modules/analyze/executed-view/chart/executed-chart-view.component.html")
        })
    ], ExecutedChartViewComponent);
    return ExecutedChartViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/analyze/executed-view/chart/index.ts":
/*!**************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/chart/index.ts ***!
  \**************************************************************/
/*! exports provided: ExecutedChartViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _executed_chart_view_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./executed-chart-view.component */ "./src/app/modules/analyze/executed-view/chart/executed-chart-view.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ExecutedChartViewComponent", function() { return _executed_chart_view_component__WEBPACK_IMPORTED_MODULE_0__["ExecutedChartViewComponent"]; });




/***/ }),

/***/ "./src/app/modules/analyze/executed-view/executed-view.component.html":
/*!****************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/executed-view.component.html ***!
  \****************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<mat-sidenav-container class=\"analysis-details-container\" [autosize]=\"false\">\n  <mat-sidenav\n    #detailsSidenav\n    mode=\"over\"\n    (openedChange)=\"onSidenavChange($event)\"\n    [opened]=\"false\"\n    position=\"end\"\n  >\n    <mat-card *ngIf=\"analysis\">\n      <mat-expansion-panel [expanded]=\"true\">\n        <mat-expansion-panel-header>\n          <span class=\"expansion-title\" i18n>Analysis Details</span>\n        </mat-expansion-panel-header>\n        <mat-divider></mat-divider>\n\n        <div class=\"detail-top\">\n          <strong *ngIf=\"analysis.userFullName || analysis.createdBy\">Created By:</strong>\n          {{ analysis.userFullName || analysis.createdBy | changeCase: 'title' }}\n        </div>\n        <div>\n          <strong *ngIf=\"analysis.createdTimestamp || analysis.createdTime\">Created Time:</strong>\n          {{ utcToLocal(analysis.createdTimestamp) || utcToLocal(analysis.createdTime)}}\n        </div>\n        <div>\n          <strong *ngIf=\"analysis.updatedUserName || analysis.modifiedBy\">Last Modified By:</strong>\n          {{ analysis.updatedUserName || analysis.modifiedBy}}\n        </div>\n        <div>\n          <strong *ngIf=\"analysis.updatedTimestamp || analysis.modifiedTime\">Last Modified Time:</strong>\n          {{ utcToLocal(analysis.updatedTimestamp) || utcToLocal(analysis.modifiedTime) }}\n        </div>\n        <div>\n          <strong *ngIf=\"executedBy\">Executed By:</strong> {{ executedBy }}\n        </div>\n        <div>\n          <strong *ngIf=\"executedAt\">Execution Time:</strong>\n          {{ executedAt }}\n        </div>\n        <div class=\"analysis-detail-description\">\n          <strong *ngIf=\"analysis.description\">Description</strong>\n          <p>{{ analysis.description }}</p>\n        </div>\n        <div>\n          <strong *ngIf=\"analysis.scheduleHuman\">Schedule:</strong>\n          {{ analysis.scheduleHuman }}\n        </div>\n      </mat-expansion-panel>\n    </mat-card>\n\n    <mat-card>\n      <mat-expansion-panel [expanded]=\"false\">\n        <mat-expansion-panel-header>\n          <span class=\"expansion-title\" i18n>Previous Versions</span>\n        </mat-expansion-panel-header>\n\n        <mat-divider></mat-divider>\n        <executed-list\n          *ngIf=\"analysis && analyses && analyses.length\"\n          style=\"padding-top: 20px;\"\n          [analyses]=\"analyses\"\n          [analysis]=\"analysis\"\n          (selectExecution)=\"onSelectExecution($event)\"\n        ></executed-list>\n      </mat-expansion-panel>\n    </mat-card>\n  </mat-sidenav>\n  <mat-sidenav-content\n    [ngClass]=\"analysis && analysis.type === 'chart' ? 'executed-view' : ''\"\n  >\n    <div fxLayout=\"column\">\n      <mat-toolbar fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n        <div fxLayout=\"row\">\n          <button\n            (click)=\"goBackToMainPage(analysis)\"\n            mat-icon-button\n            style=\"margin-right: 10px;\"\n          >\n            <mat-icon\n              class=\"back-button-icon\"\n              fontIcon=\"icon-arrow-left\"\n            ></mat-icon>\n          </button>\n          <div fxLayout=\"column\" fxLayoutAlign=\"center center\">\n            <div *ngIf=\"analysis\">\n              <span class=\"analysis__title\" e2e=\"analysis__title\">{{\n                analysis.name\n              }}</span>\n              <label *ngIf=\"isExecuting\" class=\"execution-tag\" i18n\n                >Executing</label\n              >\n            </div>\n            <div *ngIf=\"analysis\" class=\"analysis__subheader\">\n              <!-- {{ analysis.metrics }} -->\n            </div>\n          </div>\n        </div>\n        <div>\n          <button\n            *ngIf=\"canUserEdit\"\n            mat-button\n            e2e=\"action-edit-btn\"\n            (click)=\"edit()\"\n          >\n            <span i18n>Edit</span>\n          </button>\n          <button\n            *ngIf=\"canUserFork\"\n            mat-button\n            e2e=\"action-fork-btn\"\n            (click)=\"fork()\"\n          >\n            <span i18n>Fork & Edit</span>\n          </button>\n\n          <analyze-actions-menu-u\n            *ngIf=\"analysis\"\n            [analysis]=\"analysis\"\n            exclude=\"fork-edit\"\n            (detailsRequested)=\"detailsSidenav.toggle()\"\n            [actionsToDisable]=\"isExecuting ? 'execute' : ''\"\n            (afterDelete)=\"afterDelete(analysis)\"\n            (afterPublish)=\"afterPublish($event)\"\n            (afterExport)=\"exportData()\"\n          ></analyze-actions-menu-u>\n        </div>\n      </mat-toolbar>\n      <mat-card class=\"executed-view-analysis\">\n        <filter-chips-u\n          *ngIf=\"\n            analysis &&\n            executedAnalysis &&\n            !(analysis.type === 'report' && analysis.edit) &&\n            (executedAnalysis.sipQuery || executedAnalysis.sqlBuilder).filters\n              .length > 0\n          \"\n          [filters]=\"filters\"\n          [readonly]=\"true\"\n          [artifacts]=\"metric && metric.artifacts\"\n        ></filter-chips-u>\n\n        <div *ngIf=\"!executionId && !hasExecution && noPreviousExecution\">\n          <div\n            class=\"prompt-execution-container\"\n            fxLayout=\"column\"\n            fxLayoutAlign=\"center center\"\n          >\n            <span i18n\n              >This analysis hasn't been been executed yet. Execute it\n              now?</span\n            >\n            <button\n              mat-raised-button\n              color=\"primary\"\n              [disabled]=\"isExecuting\"\n              (click)=\"executeAnalysis(analysis)\"\n              i18n\n            >\n              Execute\n            </button>\n          </div>\n        </div>\n\n        <div\n          *ngIf=\"analysis && (executionId || hasExecution)\"\n          [ngSwitch]=\"analysis.type\"\n        >\n          <executed-report-view\n            *ngSwitchCase=\"\n              analysis.type === 'report' || analysis.type === 'esReport'\n                ? analysis.type\n                : ''\n            \"\n            [analysis]=\"executedAnalysis\"\n            [dataLoader]=\"dataLoader\"\n          >\n          </executed-report-view>\n          <executed-pivot-view\n            *ngSwitchCase=\"'pivot'\"\n            [analysis]=\"executedAnalysis\"\n            [data]=\"data\"\n            [updater]=\"pivotUpdater$\"\n          >\n          </executed-pivot-view>\n          <executed-chart-view\n            *ngSwitchCase=\"'chart'\"\n            [analysis]=\"executedAnalysis\"\n            [data]=\"data\"\n            [updater]=\"chartUpdater$\"\n            class=\"executed-chart-analysis\"\n          >\n          </executed-chart-view>\n\n          <div *ngSwitchCase=\"'map'\">\n            <executed-map-view\n              *ngIf=\"analysis.type | isAnalysisType: 'map':analysis?.chartType\"\n              [analysis]=\"executedAnalysis\"\n              [data]=\"data\"\n            >\n            </executed-map-view>\n            <executed-map-chart-view\n              *ngIf=\"\n                analysis.type | isAnalysisType: 'mapChart':analysis?.chartType\n              \"\n              [analysis]=\"executedAnalysis\"\n              [data]=\"data\"\n              [updater]=\"chartUpdater$\"\n              [actionBus]=\"chartActionBus$\"\n            >\n            </executed-map-chart-view>\n          </div>\n        </div>\n      </mat-card>\n    </div>\n  </mat-sidenav-content>\n</mat-sidenav-container>\n"

/***/ }),

/***/ "./src/app/modules/analyze/executed-view/executed-view.component.scss":
/*!****************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/executed-view.component.scss ***!
  \****************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%; }\n\nfilter-chips-u {\n  padding: 11px 0 10px 10px; }\n\n.back-button-icon.mat-icon {\n  font-size: 32px;\n  line-height: 32px;\n  width: 32px;\n  height: 32px; }\n\n.prompt-execution-container {\n  padding: 50px;\n  text-align: center;\n  font-size: 14px; }\n\n.prompt-execution-container button {\n    margin-top: 15px; }\n\n.mat-card {\n  color: #5c6670;\n  padding: 0;\n  margin: 10px; }\n\n.mat-toolbar {\n  background-color: transparent;\n  color: #5c6670; }\n\n.mat-toolbar .analysis__title {\n    font-size: 24px; }\n\n.mat-toolbar .analysis__subheader {\n    font-size: 16px; }\n\n::ng-deep mat-sidenav-container.analysis-details-container {\n  height: 100%; }\n\n::ng-deep mat-sidenav-container.analysis-details-container ::ng-deep mat-sidenav {\n    width: 400px; }\n\n::ng-deep mat-sidenav-container.analysis-details-container ::ng-deep .mat-drawer-backdrop:not(.mat-drawer-shown) ~ mat-sidenav * {\n    display: none; }\n\n.mat-drawer-content.executed-view {\n  overflow-y: scroll; }\n\n.execution-tag {\n  padding: 0 2px;\n  font-size: 11px;\n  text-transform: uppercase;\n  background-color: #ff9000;\n  color: #fff;\n  vertical-align: top; }\n\n.expansion-title {\n  font-size: 20px;\n  font-weight: 500;\n  letter-spacing: 0.005em;\n  color: #5c6670; }\n\n.detail-top {\n  padding-top: 10px; }\n\n.analysis-detail-description {\n  margin: 1em 0; }\n\n.analysis-detail-description p {\n    margin: 0; }\n\n.chart-data-grid .dx-datagrid .dx-datagrid-rowsview .dx-datagrid-content table tbody tr.dx-data-row td {\n  text-align: left !important; }\n\n.chart-data-grid .dx-datagrid .dx-datagrid-headers .dx-datagrid-content table tbody tr.dx-header-row td {\n  text-align: left !important; }\n\n.executed-view-analysis {\n  margin: 0 10px 0 10px; }\n\n.executed-chart-analysis {\n  height: calc(100vh - 270px) !important; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FuYWx5emUvZXhlY3V0ZWQtdmlldy9leGVjdXRlZC12aWV3LmNvbXBvbmVudC5zY3NzIiwiL1VzZXJzL2Jhcm5hbXVtdHlhbi9Qcm9qZWN0cy9tb2R1cy9zaXAvc2F3LXdlYi9zcmMvdGhlbWVzL2Jhc2UvX2NvbG9ycy5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUlBO0VBQ0UsV0FBVyxFQUFBOztBQUdiO0VBQ0UseUJBQXlCLEVBQUE7O0FBSTNCO0VBQ0UsZUFacUI7RUFhckIsaUJBYnFCO0VBY3JCLFdBZHFCO0VBZXJCLFlBZnFCLEVBQUE7O0FBa0J2QjtFQUNFLGFBQWE7RUFDYixrQkFBa0I7RUFDbEIsZUFBZSxFQUFBOztBQUhqQjtJQU1JLGdCQUFnQixFQUFBOztBQUlwQjtFQUNFLGNDZHVCO0VEZXZCLFVBQVU7RUFDVixZQUFZLEVBQUE7O0FBR2Q7RUFDRSw2QkFBNkI7RUFDN0IsY0NyQnVCLEVBQUE7O0FEbUJ6QjtJQUtJLGVBQWUsRUFBQTs7QUFMbkI7SUFTSSxlQUFlLEVBQUE7O0FBSW5CO0VBQ0UsWUFBWSxFQUFBOztBQURkO0lBSUksWUFBWSxFQUFBOztBQUpoQjtJQVNNLGFBQWEsRUFBQTs7QUFJbkI7RUFDRSxrQkFBa0IsRUFBQTs7QUFHcEI7RUFDRSxjQUFjO0VBQ2QsZUFBZTtFQUNmLHlCQUF5QjtFQUN6Qix5QkN4Q3dCO0VEeUN4QixXQ2pEa0I7RURrRGxCLG1CQUFtQixFQUFBOztBQUdyQjtFQUNFLGVBQWU7RUFDZixnQkFBZ0I7RUFDaEIsdUJBQXVCO0VBQ3ZCLGNDOUR1QixFQUFBOztBRGlFekI7RUFDRSxpQkFBaUIsRUFBQTs7QUFHbkI7RUFDRSxhQUFhLEVBQUE7O0FBRGY7SUFJSSxTQUFTLEVBQUE7O0FBSWI7RUFDRSwyQkFBMkIsRUFBQTs7QUFHN0I7RUFDRSwyQkFBMkIsRUFBQTs7QUFHN0I7RUFDRSxxQkFBcUIsRUFBQTs7QUFHdkI7RUFDRSxzQ0FBc0MsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvYW5hbHl6ZS9leGVjdXRlZC12aWV3L2V4ZWN1dGVkLXZpZXcuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyIgIEBpbXBvcnQgXCJzcmMvdGhlbWVzL2Jhc2UvY29sb3JzXCI7XG5cbiRiYWNrLWJ1dHRvbi1zaXplOiAzMnB4O1xuXG46aG9zdCB7XG4gIHdpZHRoOiAxMDAlO1xufVxuXG5maWx0ZXItY2hpcHMtdSB7XG4gIHBhZGRpbmc6IDExcHggMCAxMHB4IDEwcHg7XG59XG5cblxuLmJhY2stYnV0dG9uLWljb24ubWF0LWljb24ge1xuICBmb250LXNpemU6ICRiYWNrLWJ1dHRvbi1zaXplO1xuICBsaW5lLWhlaWdodDogJGJhY2stYnV0dG9uLXNpemU7XG4gIHdpZHRoOiAkYmFjay1idXR0b24tc2l6ZTtcbiAgaGVpZ2h0OiAkYmFjay1idXR0b24tc2l6ZTtcbn1cblxuLnByb21wdC1leGVjdXRpb24tY29udGFpbmVyIHtcbiAgcGFkZGluZzogNTBweDtcbiAgdGV4dC1hbGlnbjogY2VudGVyO1xuICBmb250LXNpemU6IDE0cHg7XG5cbiAgYnV0dG9uIHtcbiAgICBtYXJnaW4tdG9wOiAxNXB4O1xuICB9XG59XG5cbi5tYXQtY2FyZCB7XG4gIGNvbG9yOiAkcHJpbWFyeS1ncmV5LWc0O1xuICBwYWRkaW5nOiAwO1xuICBtYXJnaW46IDEwcHg7XG59XG5cbi5tYXQtdG9vbGJhciB7XG4gIGJhY2tncm91bmQtY29sb3I6IHRyYW5zcGFyZW50O1xuICBjb2xvcjogJHByaW1hcnktZ3JleS1nNDtcblxuICAuYW5hbHlzaXNfX3RpdGxlIHtcbiAgICBmb250LXNpemU6IDI0cHg7XG4gIH1cblxuICAuYW5hbHlzaXNfX3N1YmhlYWRlciB7XG4gICAgZm9udC1zaXplOiAxNnB4O1xuICB9XG59XG5cbjo6bmctZGVlcCBtYXQtc2lkZW5hdi1jb250YWluZXIuYW5hbHlzaXMtZGV0YWlscy1jb250YWluZXIge1xuICBoZWlnaHQ6IDEwMCU7XG5cbiAgOjpuZy1kZWVwIG1hdC1zaWRlbmF2IHtcbiAgICB3aWR0aDogNDAwcHg7XG4gIH1cblxuICA6Om5nLWRlZXAgLm1hdC1kcmF3ZXItYmFja2Ryb3A6bm90KC5tYXQtZHJhd2VyLXNob3duKSB+IG1hdC1zaWRlbmF2IHtcbiAgICAqIHtcbiAgICAgIGRpc3BsYXk6IG5vbmU7XG4gICAgfVxuICB9XG59XG4ubWF0LWRyYXdlci1jb250ZW50LmV4ZWN1dGVkLXZpZXcge1xuICBvdmVyZmxvdy15OiBzY3JvbGw7XG59XG5cbi5leGVjdXRpb24tdGFnIHtcbiAgcGFkZGluZzogMCAycHg7XG4gIGZvbnQtc2l6ZTogMTFweDtcbiAgdGV4dC10cmFuc2Zvcm06IHVwcGVyY2FzZTtcbiAgYmFja2dyb3VuZC1jb2xvcjogJHNlY29uZGFyeS1vcmFuZ2U7XG4gIGNvbG9yOiAkcHJpbWFyeS13aGl0ZTtcbiAgdmVydGljYWwtYWxpZ246IHRvcDtcbn1cblxuLmV4cGFuc2lvbi10aXRsZSB7XG4gIGZvbnQtc2l6ZTogMjBweDtcbiAgZm9udC13ZWlnaHQ6IDUwMDtcbiAgbGV0dGVyLXNwYWNpbmc6IDAuMDA1ZW07XG4gIGNvbG9yOiAkcHJpbWFyeS1ncmV5LWc0O1xufVxuXG4uZGV0YWlsLXRvcCB7XG4gIHBhZGRpbmctdG9wOiAxMHB4O1xufVxuXG4uYW5hbHlzaXMtZGV0YWlsLWRlc2NyaXB0aW9uIHtcbiAgbWFyZ2luOiAxZW0gMDtcblxuICBwIHtcbiAgICBtYXJnaW46IDA7XG4gIH1cbn1cblxuLmNoYXJ0LWRhdGEtZ3JpZCAuZHgtZGF0YWdyaWQgLmR4LWRhdGFncmlkLXJvd3N2aWV3IC5keC1kYXRhZ3JpZC1jb250ZW50IHRhYmxlIHRib2R5IHRyLmR4LWRhdGEtcm93IHRkIHtcbiAgdGV4dC1hbGlnbjogbGVmdCAhaW1wb3J0YW50O1xufVxuXG4uY2hhcnQtZGF0YS1ncmlkIC5keC1kYXRhZ3JpZCAuZHgtZGF0YWdyaWQtaGVhZGVycyAuZHgtZGF0YWdyaWQtY29udGVudCB0YWJsZSB0Ym9keSB0ci5keC1oZWFkZXItcm93IHRkIHtcbiAgdGV4dC1hbGlnbjogbGVmdCAhaW1wb3J0YW50O1xufVxuXG4uZXhlY3V0ZWQtdmlldy1hbmFseXNpcyB7XG4gIG1hcmdpbjogMCAxMHB4IDAgMTBweDtcbn1cblxuLmV4ZWN1dGVkLWNoYXJ0LWFuYWx5c2lzIHtcbiAgaGVpZ2h0OiBjYWxjKDEwMHZoIC0gMjcwcHgpICFpbXBvcnRhbnQ7XG59XG4iLCIvLyBCcmFuZGluZyBjb2xvcnNcbiRwcmltYXJ5LWJsdWUtYjE6ICMxYTg5ZDQ7XG4kcHJpbWFyeS1ibHVlLWIyOiAjMDA3N2JlO1xuJHByaW1hcnktYmx1ZS1iMzogIzIwNmJjZTtcbiRwcmltYXJ5LWJsdWUtYjQ6ICMxZDNhYjI7XG5cbiRwcmltYXJ5LWhvdmVyLWJsdWU6ICMxZDYxYjE7XG4kZ3JpZC1ob3Zlci1jb2xvcjogI2Y1ZjlmYztcbiRncmlkLWhlYWRlci1iZy1jb2xvcjogI2Q3ZWFmYTtcbiRncmlkLWhlYWRlci1jb2xvcjogIzBiNGQ5OTtcbiRncmlkLXRleHQtY29sb3I6ICM0NjQ2NDY7XG4kZ3JleS10ZXh0LWNvbG9yOiAjNjM2MzYzO1xuXG4kc2VsZWN0aW9uLWhpZ2hsaWdodC1jb2w6IHJnYmEoMCwgMTQwLCAyNjAsIDAuMik7XG4kcHJpbWFyeS1ncmV5LWcxOiAjZDFkM2QzO1xuJHByaW1hcnktZ3JleS1nMjogIzk5OTtcbiRwcmltYXJ5LWdyZXktZzM6ICM3MzczNzM7XG4kcHJpbWFyeS1ncmV5LWc0OiAjNWM2NjcwO1xuJHByaW1hcnktZ3JleS1nNTogIzMxMzEzMTtcbiRwcmltYXJ5LWdyZXktZzY6ICNmNWY1ZjU7XG4kcHJpbWFyeS1ncmV5LWc3OiAjM2QzZDNkO1xuXG4kcHJpbWFyeS13aGl0ZTogI2ZmZjtcbiRwcmltYXJ5LWJsYWNrOiAjMDAwO1xuJHByaW1hcnktcmVkOiAjYWIwZTI3O1xuJHByaW1hcnktZ3JlZW46ICM3M2I0MjE7XG4kcHJpbWFyeS1vcmFuZ2U6ICNmMDc2MDE7XG5cbiRzZWNvbmRhcnktZ3JlZW46ICM2ZmIzMjA7XG4kc2Vjb25kYXJ5LXllbGxvdzogI2ZmYmUwMDtcbiRzZWNvbmRhcnktb3JhbmdlOiAjZmY5MDAwO1xuJHNlY29uZGFyeS1yZWQ6ICNkOTNlMDA7XG4kc2Vjb25kYXJ5LWJlcnJ5OiAjYWMxNDVhO1xuJHNlY29uZGFyeS1wdXJwbGU6ICM5MTQxOTE7XG5cbiRzdHJpbmctdHlwZS1jb2xvcjogIzQ5OTViMjtcbiRudW1iZXItdHlwZS1jb2xvcjogIzAwYjE4MDtcbiRnZW8tdHlwZS1jb2xvcjogIzg0NWVjMjtcbiRkYXRlLXR5cGUtY29sb3I6ICNkMTk2MjE7XG5cbiR0eXBlLWNoaXAtb3BhY2l0eTogMTtcbiRzdHJpbmctdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRzdHJpbmctdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRudW1iZXItdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRudW1iZXItdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRnZW8tdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRnZW8tdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcbiRkYXRlLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZGF0ZS10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuXG4kcmVwb3J0LWRlc2lnbmVyLXNldHRpbmdzLWJnLWNvbG9yOiAjZjVmOWZjO1xuJGJhY2tncm91bmQtY29sb3I6ICNmNWY5ZmM7XG4iXX0= */"

/***/ }),

/***/ "./src/app/modules/analyze/executed-view/executed-view.component.ts":
/*!**************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/executed-view.component.ts ***!
  \**************************************************************************/
/*! exports provided: ExecutedViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExecutedViewComponent", function() { return ExecutedViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/find */ "./node_modules/lodash/find.js");
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_find__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var moment__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! moment */ "./node_modules/moment/moment.js");
/* harmony import */ var moment__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(moment__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var rxjs_operators__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! rxjs/operators */ "./node_modules/rxjs/_esm5/operators/index.js");
/* harmony import */ var lodash_clone__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! lodash/clone */ "./node_modules/lodash/clone.js");
/* harmony import */ var lodash_clone__WEBPACK_IMPORTED_MODULE_9___default = /*#__PURE__*/__webpack_require__.n(lodash_clone__WEBPACK_IMPORTED_MODULE_9__);
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! lodash/forEach */ "./node_modules/lodash/forEach.js");
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_10___default = /*#__PURE__*/__webpack_require__.n(lodash_forEach__WEBPACK_IMPORTED_MODULE_10__);
/* harmony import */ var _services_analyze_service__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../services/analyze.service */ "./src/app/modules/analyze/services/analyze.service.ts");
/* harmony import */ var _services_analyze_export_service__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ../services/analyze-export.service */ "./src/app/modules/analyze/services/analyze-export.service.ts");
/* harmony import */ var _services_execute_service__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ../services/execute.service */ "./src/app/modules/analyze/services/execute.service.ts");
/* harmony import */ var _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ../../../common/services/toastMessage.service */ "./src/app/common/services/toastMessage.service.ts");
/* harmony import */ var _common_utils_dataFlattener__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ../../../common/utils/dataFlattener */ "./src/app/common/utils/dataFlattener.ts");
/* harmony import */ var _actions__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ../actions */ "./src/app/modules/analyze/actions/index.ts");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var util__WEBPACK_IMPORTED_MODULE_18__ = __webpack_require__(/*! util */ "./node_modules/util/util.js");
/* harmony import */ var util__WEBPACK_IMPORTED_MODULE_18___default = /*#__PURE__*/__webpack_require__.n(util__WEBPACK_IMPORTED_MODULE_18__);
/* harmony import */ var _designer_types__WEBPACK_IMPORTED_MODULE_19__ = __webpack_require__(/*! ../designer/types */ "./src/app/modules/analyze/designer/types.ts");
/* harmony import */ var _consts__WEBPACK_IMPORTED_MODULE_20__ = __webpack_require__(/*! ./../consts */ "./src/app/modules/analyze/consts.ts");





















var ExecutedViewComponent = /** @class */ (function () {
    function ExecutedViewComponent(_executeService, _analyzeService, _router, _route, _analyzeActionsService, _jwt, _analyzeExportService, _toastMessage) {
        this._executeService = _executeService;
        this._analyzeService = _analyzeService;
        this._router = _router;
        this._route = _route;
        this._analyzeActionsService = _analyzeActionsService;
        this._jwt = _jwt;
        this._analyzeExportService = _analyzeExportService;
        this._toastMessage = _toastMessage;
        this.canUserPublish = false;
        this.canUserFork = false;
        this.canUserEdit = false;
        this.canUserExecute = false;
        this.isExecuting = false;
        this.noPreviousExecution = false;
        this.hasExecution = false;
        this.pivotUpdater$ = new rxjs__WEBPACK_IMPORTED_MODULE_7__["Subject"]();
        this.chartUpdater$ = new rxjs__WEBPACK_IMPORTED_MODULE_7__["BehaviorSubject"]({});
        this.chartActionBus$ = new rxjs__WEBPACK_IMPORTED_MODULE_7__["Subject"]();
        this.filters = [];
        this.onExecutionEvent = this.onExecutionEvent.bind(this);
        this.onExecutionsEvent = this.onExecutionsEvent.bind(this);
    }
    ExecutedViewComponent.prototype.ngOnInit = function () {
        var _this = this;
        Object(rxjs__WEBPACK_IMPORTED_MODULE_7__["combineLatest"])(this._route.params, this._route.queryParams)
            .pipe(Object(rxjs_operators__WEBPACK_IMPORTED_MODULE_8__["debounce"])(function () { return Object(rxjs__WEBPACK_IMPORTED_MODULE_7__["timer"])(100); }))
            .subscribe(function (_a) {
            var params = _a[0], queryParams = _a[1];
            _this.onParamsChange(params, queryParams);
        });
        this.canAutoRefresh = this._jwt.hasCustomConfig(_common_services__WEBPACK_IMPORTED_MODULE_17__["CUSTOM_JWT_CONFIG"].ES_ANALYSIS_AUTO_REFRESH);
    };
    ExecutedViewComponent.prototype.fetchFilters = function (analysis) {
        var queryBuilder = Object(_designer_types__WEBPACK_IMPORTED_MODULE_19__["isDSLAnalysis"])(analysis)
            ? analysis.sipQuery
            : analysis.sqlBuilder;
        this.filters = Object(_designer_types__WEBPACK_IMPORTED_MODULE_19__["isDSLAnalysis"])(analysis)
            ? this.generateDSLDateFilters(queryBuilder.filters)
            : queryBuilder.filters;
    };
    ExecutedViewComponent.prototype.generateDSLDateFilters = function (filters) {
        lodash_forEach__WEBPACK_IMPORTED_MODULE_10__(filters, function (filtr) {
            if (!filtr.isRuntimeFilter &&
                !filtr.isGlobalFilter &&
                (filtr.type === 'date' && filtr.model.operator === 'BTW')) {
                filtr.model.gte = moment__WEBPACK_IMPORTED_MODULE_6__(filtr.model.value).format('YYYY-MM-DD');
                filtr.model.lte = moment__WEBPACK_IMPORTED_MODULE_6__(filtr.model.otherValue).format('YYYY-MM-DD');
                filtr.model.preset = _consts__WEBPACK_IMPORTED_MODULE_20__["CUSTOM_DATE_PRESET_VALUE"];
            }
        });
        return filters;
    };
    ExecutedViewComponent.prototype.onParamsChange = function (params, queryParams) {
        var _this = this;
        var analysisId = params.analysisId;
        var awaitingExecution = queryParams.awaitingExecution, loadLastExecution = queryParams.loadLastExecution, executionId = queryParams.executionId, isDSL = queryParams.isDSL;
        this.executionId = executionId;
        this.loadAnalysisById(analysisId, isDSL === 'true').then(function (analysis) {
            _this.analysis = analysis;
            _this.setPrivileges(analysis);
            _this.fetchFilters(analysis);
            /* If an execution is not already going on, create a new execution
             * as applicable. */
            _this.executeIfNotWaiting(analysis, 
            /* awaitingExecution and loadLastExecution paramaters are supposed to be boolean,
             * but all query params come as strings. So typecast them properly */
            awaitingExecution === 'true', loadLastExecution === 'true', executionId);
        });
        this.executionsSub = this._executeService.subscribe(analysisId, this.onExecutionsEvent);
    };
    ExecutedViewComponent.prototype.ngOnDestroy = function () {
        if (this.executionsSub) {
            this.executionsSub.unsubscribe();
        }
    };
    ExecutedViewComponent.prototype.executeIfNotWaiting = function (analysis, awaitingExecution, loadLastExecution, executionId) {
        if (awaitingExecution) {
            return;
        }
        var isDataLakeReport = analysis.type === 'report';
        if (executionId ||
            loadLastExecution ||
            isDataLakeReport ||
            !this.canAutoRefresh) {
            this.loadExecutedAnalysesAndExecutionData(analysis.id, executionId, analysis.type, null);
        }
        else {
            this.executeAnalysis(analysis, _services_analyze_service__WEBPACK_IMPORTED_MODULE_11__["EXECUTION_MODES"].LIVE);
        }
    };
    ExecutedViewComponent.prototype.onSidenavChange = function (isOpen) {
        var _this = this;
        if (isOpen && !this.analyses) {
            this.loadExecutedAnalyses(this.analysis.id, Object(_designer_types__WEBPACK_IMPORTED_MODULE_19__["isDSLAnalysis"])(this.analysis)).then(function (analyses) {
                var lastExecutionId = lodash_get__WEBPACK_IMPORTED_MODULE_4__(analyses, '[0].id', null);
                if (!_this.executionId && lastExecutionId) {
                    _this.executionId = lastExecutionId;
                    if (!_this.executedAt) {
                        _this.setExecutedAt(_this.executionId);
                    }
                }
            });
        }
    };
    ExecutedViewComponent.prototype.onExecutionsEvent = function (e) {
        if (!e.subject.isStopped) {
            e.subject.subscribe(this.onExecutionEvent);
        }
    };
    ExecutedViewComponent.prototype.onExecutionEvent = function (_a) {
        var _this = this;
        var state = _a.state, response = _a.response;
        /* prettier-ignore */
        switch (state) {
            case _services_execute_service__WEBPACK_IMPORTED_MODULE_13__["EXECUTION_STATES"].SUCCESS:
                setTimeout(function () {
                    _this.onExecutionSuccess(response);
                }, 500);
                break;
            case _services_execute_service__WEBPACK_IMPORTED_MODULE_13__["EXECUTION_STATES"].ERROR:
                this.onExecutionError();
                break;
            default:
        }
        this.isExecuting = state === _services_execute_service__WEBPACK_IMPORTED_MODULE_13__["EXECUTION_STATES"].EXECUTING;
    };
    ExecutedViewComponent.prototype.onExecutionSuccess = function (response) {
        var _this = this;
        if (Object(util__WEBPACK_IMPORTED_MODULE_18__["isUndefined"])(this.analysis)) {
            return;
        }
        var thereIsDataLoaded = this.data || this.dataLoader;
        var isDataLakeReport = lodash_get__WEBPACK_IMPORTED_MODULE_4__(this.analysis, 'type') === 'report';
        this.onetimeExecution = response.executionType !== _services_analyze_service__WEBPACK_IMPORTED_MODULE_11__["EXECUTION_MODES"].PUBLISH;
        this.filters = Object(_designer_types__WEBPACK_IMPORTED_MODULE_19__["isDSLAnalysis"])(this.analysis)
            ? this.generateDSLDateFilters(response.queryBuilder.filters)
            : response.queryBuilder.filters;
        if (isDataLakeReport && thereIsDataLoaded) {
            this._toastMessage.success('Tap this message to reload data.', 'Execution finished', {
                timeOut: 0,
                extendedTimeOut: 0,
                closeButton: true,
                onclick: function () {
                    return _this.loadExecutedAnalysesAndExecutionData(lodash_get__WEBPACK_IMPORTED_MODULE_4__(_this.analysis, 'id'), response.executionId, lodash_get__WEBPACK_IMPORTED_MODULE_4__(_this.analysis, 'type'), response);
                }
            });
        }
        else {
            this.loadExecutedAnalysesAndExecutionData(lodash_get__WEBPACK_IMPORTED_MODULE_4__(this.analysis, 'id'), response.executionId, lodash_get__WEBPACK_IMPORTED_MODULE_4__(this.analysis, 'type'), response);
        }
    };
    ExecutedViewComponent.prototype.onExecutionError = function () {
        this.onetimeExecution = false;
        this.loadExecutedAnalysesAndExecutionData(this.analysis.id, null, this.analysis.type, null);
    };
    ExecutedViewComponent.prototype.onSelectExecution = function (executionId) {
        if (!executionId) {
            return;
        }
        this.detailsSidenav && this.detailsSidenav.close();
        window['siden'] = this.detailsSidenav;
        this.onetimeExecution = false;
        this._router.navigate(['analyze', 'analysis', this.analysis.id, 'executed'], {
            queryParams: {
                executionId: executionId,
                awaitingExecution: false,
                loadLastExecution: false,
                isDSL: Object(_designer_types__WEBPACK_IMPORTED_MODULE_19__["isDSLAnalysis"])(this.analysis)
            }
        });
    };
    ExecutedViewComponent.prototype.executeAnalysis = function (analysis, mode) {
        var _this = this;
        this._analyzeActionsService
            .execute(analysis, mode)
            .then(function (executionStarted) {
            // this.afterExecuteLaunched(analysis);
            if (!executionStarted && !_this.analyses) {
                // at least load the executed analyses if none are loaded
                _this.loadExecutedAnalysesAndExecutionData(analysis.id, null, analysis.type, null);
            }
        });
    };
    ExecutedViewComponent.prototype.loadExecutedAnalysesAndExecutionData = function (analysisId, executionId, analysisType, executeResponse) {
        this.executionId = executionId;
        this.loadDataOrSetDataLoader(analysisId, executionId, analysisType, executeResponse);
    };
    ExecutedViewComponent.prototype.gotoLastPublished = function (analysis, _a) {
        var _this = this;
        var executionId = _a.executionId;
        return function () {
            _this._toastMessage.clear();
            _this._router.navigate(['analyze', 'analysis', analysis.id, 'executed'], {
                queryParams: {
                    executionId: executionId,
                    awaitingExecution: false,
                    loadLastExecution: true
                }
            });
        };
    };
    ExecutedViewComponent.prototype.setExecutedBy = function (executedBy) {
        this.executedBy =
            executedBy ||
                (this.onetimeExecution ? this._jwt.getLoginId() : 'Scheduled');
    };
    ExecutedViewComponent.prototype.setExecutedAt = function (executionId) {
        var finished = (lodash_find__WEBPACK_IMPORTED_MODULE_5__(this.analyses, function (execution) { return execution.id === executionId; }) || {
            finished: null
        }).finished;
        this.executedAt = finished ? this.utcToLocal(finished) : this.executedAt;
    };
    ExecutedViewComponent.prototype.loadExecutedAnalyses = function (analysisId, isDSL) {
        var _this = this;
        return this._analyzeService
            .getPublishedAnalysesByAnalysisId(analysisId, isDSL)
            .then(function (analyses) {
            _this.analyses = analyses;
            _this.noPreviousExecution = !analyses || !analyses.length;
            _this.setExecutedAt(_this.executionId);
            return analyses;
        }, function (err) {
            throw err;
        });
    };
    ExecutedViewComponent.prototype.loadAnalysisById = function (analysisId, isDSL) {
        var _this = this;
        return this._analyzeService
            .readAnalysis(analysisId, isDSL)
            .then(function (analysis) {
            _this.analysis = analysis;
            // this._analyzeService
            //   .getLastExecutionData(this.analysis.id, {
            //     analysisType: this.analysis.type
            //   })
            //   .then(data => {
            //     console.log(data);
            //   });
            _this.executedAnalysis = tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, _this.analysis);
            /* Get metrics to get full artifacts. Needed to show filters for fields
            that aren't selected for data */
            return _this._analyzeService
                .getArtifactsForDataSet(_this.analysis.semanticId)
                .toPromise();
        })
            .then(function (metric) {
            _this.metric = metric;
            return _this.analysis;
        });
    };
    ExecutedViewComponent.prototype.loadDataOrSetDataLoader = function (analysisId, executionId, analysisType, executeResponse) {
        var _this = this;
        if (executeResponse === void 0) { executeResponse = null; }
        // report type data will be loaded by the report grid, because of the paging mechanism
        var isReportType = ['report', 'esReport'].includes(analysisType);
        if (isReportType) {
            /* The Execution data loader defers data loading to the report grid, so it can load the data needed depending on paging */
            if (executeResponse) {
                executeResponse.data = lodash_clone__WEBPACK_IMPORTED_MODULE_9__(Object(_common_utils_dataFlattener__WEBPACK_IMPORTED_MODULE_15__["flattenReportData"])(executeResponse.data, this.executedAnalysis));
                // resolve the data that is sent by the execution
                // and the paginated data after that
                this.executedAnalysis = tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, this.analysis, (Object(_designer_types__WEBPACK_IMPORTED_MODULE_19__["isDSLAnalysis"])(this.executedAnalysis)
                    ? {
                        sipQuery: executeResponse.queryBuilder || this.executedAnalysis.sipQuery
                    }
                    : {
                        sqlBuilder: executeResponse.queryBuilder ||
                            this.executedAnalysis.sqlBuilder
                    }));
                this.fetchFilters(this.executedAnalysis);
                this.setExecutedBy(executeResponse.executedBy);
                this.executedAt = this.utcToLocal(executeResponse.executedAt);
                var isItFirstTime_1 = true;
                this.dataLoader = function (options) {
                    if (isItFirstTime_1) {
                        isItFirstTime_1 = false;
                        return Promise.resolve({
                            data: executeResponse.data,
                            totalCount: executeResponse.count
                        });
                    }
                    return _this.loadExecutionData(analysisId, executionId, analysisType, _this.onetimeExecution
                        ? tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, options, { executionType: _services_analyze_service__WEBPACK_IMPORTED_MODULE_11__["EXECUTION_DATA_MODES"].ONETIME }) : options);
                };
            }
            else {
                /* Mark hasExecution temporarily as true to allow fetch of data.
                   It'll be marked false if data is not found (no execution exists).
                */
                this.hasExecution = true;
                this.dataLoader = function (options) {
                    return _this.loadExecutionData(analysisId, executionId, analysisType, _this.onetimeExecution
                        ? tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, options, { executionType: _services_analyze_service__WEBPACK_IMPORTED_MODULE_11__["EXECUTION_DATA_MODES"].ONETIME }) : options);
                };
            }
        }
        else {
            if (executeResponse) {
                this.executedAnalysis = tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, this.analysis, (Object(_designer_types__WEBPACK_IMPORTED_MODULE_19__["isDSLAnalysis"])(this.executedAnalysis)
                    ? {
                        sipQuery: executeResponse.queryBuilder || this.executedAnalysis.sipQuery
                    }
                    : {
                        sqlBuilder: executeResponse.queryBuilder ||
                            this.executedAnalysis.sqlBuilder
                    }));
                this.setExecutedBy(executeResponse.executedBy);
                this.executedAt = this.utcToLocal(executeResponse.executedAt);
                this.data = this.flattenData(executeResponse.data, this.executedAnalysis);
            }
            else {
                this.loadExecutionData(analysisId, executionId, analysisType).then(function (_a) {
                    var data = _a.data;
                    _this.data = _this.flattenData(data, _this.executedAnalysis);
                });
            }
        }
    };
    ExecutedViewComponent.prototype.utcToLocal = function (utcTime) {
        return moment__WEBPACK_IMPORTED_MODULE_6__["utc"](utcTime)
            .local()
            .format('YYYY/MM/DD h:mm A');
    };
    ExecutedViewComponent.prototype.flattenData = function (data, analysis) {
        /* prettier-ignore */
        switch (analysis.type) {
            case 'pivot':
                return Object(_common_utils_dataFlattener__WEBPACK_IMPORTED_MODULE_15__["flattenPivotData"])(data, analysis.sipQuery || analysis.sqlBuilder);
            case 'chart':
            case 'map':
                return Object(_common_utils_dataFlattener__WEBPACK_IMPORTED_MODULE_15__["flattenChartData"])(data, Object(_designer_types__WEBPACK_IMPORTED_MODULE_19__["isDSLAnalysis"])(analysis) ? analysis.sipQuery : analysis.sqlBuilder);
            default:
                return data;
        }
    };
    ExecutedViewComponent.prototype.loadExecutionData = function (analysisId, executionId, analysisType, options) {
        var _this = this;
        if (options === void 0) { options = {}; }
        options.analysisType = analysisType;
        options.isDSL = Object(_designer_types__WEBPACK_IMPORTED_MODULE_19__["isDSLAnalysis"])(this.analysis);
        return (executionId
            ? this._analyzeService.getExecutionData(analysisId, executionId, options)
            : this._analyzeService.getLastExecutionData(analysisId, options)).then(function (_a) {
            var data = _a.data, count = _a.count, queryBuilder = _a.queryBuilder, executedBy = _a.executedBy;
            /* Check if a successful execution data is returned. */
            _this.hasExecution =
                Boolean(data.length) || Boolean(queryBuilder) || Boolean(executedBy);
            /* If there's no execution id (loading last execution) and there's no
              execution data as well, then we can deduce there's no previous
              execution for this analysis present */
            _this.noPreviousExecution = !executionId && !_this.hasExecution;
            if (_this.executedAnalysis && queryBuilder) {
                if (Object(_designer_types__WEBPACK_IMPORTED_MODULE_19__["isDSLAnalysis"])(_this.executedAnalysis)) {
                    _this.executedAnalysis.sipQuery = queryBuilder;
                }
                else {
                    _this.executedAnalysis.sqlBuilder = queryBuilder;
                }
            }
            var isReportType = ['report', 'esReport'].includes(analysisType);
            if (isReportType) {
                data = lodash_clone__WEBPACK_IMPORTED_MODULE_9__(Object(_common_utils_dataFlattener__WEBPACK_IMPORTED_MODULE_15__["flattenReportData"])(data, _this.analysis));
            }
            _this.setExecutedBy(executedBy);
            _this.setExecutedAt(executionId);
            return { data: data, totalCount: count };
        }, function (err) {
            throw err;
        });
    };
    ExecutedViewComponent.prototype.setPrivileges = function (analysis) {
        var categoryId = Object(_designer_types__WEBPACK_IMPORTED_MODULE_19__["isDSLAnalysis"])(analysis)
            ? analysis.category
            : analysis.categoryId;
        var userId = Object(_designer_types__WEBPACK_IMPORTED_MODULE_19__["isDSLAnalysis"])(analysis)
            ? analysis.createdBy
            : analysis.userId;
        this.canUserPublish = this._jwt.hasPrivilege('PUBLISH', {
            subCategoryId: categoryId
        });
        this.canUserFork = this._jwt.hasPrivilege('FORK', {
            subCategoryId: categoryId
        });
        this.canUserExecute = this._jwt.hasPrivilege('EXECUTE', {
            subCategoryId: categoryId
        });
        this.canUserEdit = this._jwt.hasPrivilege('EDIT', {
            subCategoryId: categoryId,
            creatorId: userId
        });
    };
    ExecutedViewComponent.prototype.goBackToMainPage = function (analysis) {
        this._router.navigate([
            'analyze',
            Object(_designer_types__WEBPACK_IMPORTED_MODULE_19__["isDSLAnalysis"])(analysis) ? analysis.category : analysis.categoryId
        ]);
    };
    ExecutedViewComponent.prototype.edit = function () {
        this._analyzeActionsService.edit(this.analysis);
    };
    ExecutedViewComponent.prototype.fork = function () {
        this._analyzeActionsService.fork(this.analysis);
    };
    ExecutedViewComponent.prototype.gotoForkedAnalysis = function (analysis) {
        this._router.navigate(['analyze', 'analysis', analysis.id, 'executed'], {
            queryParams: {
                executionId: null,
                awaitingExecution: true,
                loadLastExecution: false
            }
        });
    };
    ExecutedViewComponent.prototype.afterDelete = function (analysis) {
        this.goBackToMainPage(analysis);
    };
    ExecutedViewComponent.prototype.afterPublish = function (analysis) {
        if (analysis) {
            this.goBackToMainPage(analysis);
        }
    };
    ExecutedViewComponent.prototype.exportData = function () {
        /* prettier-ignore */
        switch (this.analysis.type) {
            case 'pivot':
                // export from front end
                this.pivotUpdater$.next({
                    export: true
                });
                break;
            case 'chart':
                // TODO add export for Maps
                this.chartUpdater$.next({ export: true });
                this.chartActionBus$.next({ export: true });
                break;
            default:
                var executionType = this.onetimeExecution ? _services_analyze_service__WEBPACK_IMPORTED_MODULE_11__["EXECUTION_DATA_MODES"].ONETIME : _services_analyze_service__WEBPACK_IMPORTED_MODULE_11__["EXECUTION_DATA_MODES"].NORMAL;
                this._analyzeExportService.export(this.executedAnalysis, this.executionId, executionType);
        }
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_2__["ViewChild"])('detailsSidenav'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_material__WEBPACK_IMPORTED_MODULE_1__["MatSidenav"])
    ], ExecutedViewComponent.prototype, "detailsSidenav", void 0);
    ExecutedViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_2__["Component"])({
            selector: 'executed-view',
            template: __webpack_require__(/*! ./executed-view.component.html */ "./src/app/modules/analyze/executed-view/executed-view.component.html"),
            styles: [__webpack_require__(/*! ./executed-view.component.scss */ "./src/app/modules/analyze/executed-view/executed-view.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_services_execute_service__WEBPACK_IMPORTED_MODULE_13__["ExecuteService"],
            _services_analyze_service__WEBPACK_IMPORTED_MODULE_11__["AnalyzeService"],
            _angular_router__WEBPACK_IMPORTED_MODULE_3__["Router"],
            _angular_router__WEBPACK_IMPORTED_MODULE_3__["ActivatedRoute"],
            _actions__WEBPACK_IMPORTED_MODULE_16__["AnalyzeActionsService"],
            _common_services__WEBPACK_IMPORTED_MODULE_17__["JwtService"],
            _services_analyze_export_service__WEBPACK_IMPORTED_MODULE_12__["AnalyzeExportService"],
            _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_14__["ToastService"]])
    ], ExecutedViewComponent);
    return ExecutedViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/analyze/executed-view/index.ts":
/*!********************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/index.ts ***!
  \********************************************************/
/*! exports provided: ExecutedViewModule, ExecutedViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExecutedViewModule", function() { return ExecutedViewModule; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../common */ "./src/app/common/index.ts");
/* harmony import */ var _actions__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../actions */ "./src/app/modules/analyze/actions/index.ts");
/* harmony import */ var _executed_view_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./executed-view.component */ "./src/app/modules/analyze/executed-view/executed-view.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ExecutedViewComponent", function() { return _executed_view_component__WEBPACK_IMPORTED_MODULE_4__["ExecutedViewComponent"]; });

/* harmony import */ var _list__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./list */ "./src/app/modules/analyze/executed-view/list/index.ts");
/* harmony import */ var _report__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./report */ "./src/app/modules/analyze/executed-view/report/index.ts");
/* harmony import */ var _pivot__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./pivot */ "./src/app/modules/analyze/executed-view/pivot/index.ts");
/* harmony import */ var _chart__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./chart */ "./src/app/modules/analyze/executed-view/chart/index.ts");
/* harmony import */ var _map__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./map */ "./src/app/modules/analyze/executed-view/map/index.ts");
/* harmony import */ var _map_chart__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ./map-chart */ "./src/app/modules/analyze/executed-view/map-chart/index.ts");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var _services_execute_service__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ../services/execute.service */ "./src/app/modules/analyze/services/execute.service.ts");
/* harmony import */ var _common_components_charts__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ../../../common/components/charts */ "./src/app/common/components/charts/index.ts");
/* harmony import */ var _services_analyze_export_service__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ../services/analyze-export.service */ "./src/app/modules/analyze/services/analyze-export.service.ts");
/* harmony import */ var _designer_filter__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! ../designer/filter */ "./src/app/modules/analyze/designer/filter/index.ts");
















var COMPONENTS = [
    _executed_view_component__WEBPACK_IMPORTED_MODULE_4__["ExecutedViewComponent"],
    _list__WEBPACK_IMPORTED_MODULE_5__["ExecutedListComponent"],
    _report__WEBPACK_IMPORTED_MODULE_6__["ExecutedReportViewComponent"],
    _pivot__WEBPACK_IMPORTED_MODULE_7__["ExecutedPivotViewComponent"],
    _chart__WEBPACK_IMPORTED_MODULE_8__["ExecutedChartViewComponent"],
    _map__WEBPACK_IMPORTED_MODULE_9__["ExecutedMapViewComponent"],
    _map_chart__WEBPACK_IMPORTED_MODULE_10__["ExecutedMapChartViewComponent"]
];
var ExecutedViewModule = /** @class */ (function () {
    function ExecutedViewModule() {
    }
    ExecutedViewModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["NgModule"])({
            imports: [
                _common__WEBPACK_IMPORTED_MODULE_2__["CommonModuleTs"],
                _designer_filter__WEBPACK_IMPORTED_MODULE_15__["AnalyzeFilterModule"],
                _actions__WEBPACK_IMPORTED_MODULE_3__["AnalyzeActionsModule"],
                _common_components_charts__WEBPACK_IMPORTED_MODULE_13__["UChartModule"]
            ],
            declarations: COMPONENTS.slice(),
            entryComponents: COMPONENTS,
            providers: [
                _services_execute_service__WEBPACK_IMPORTED_MODULE_12__["ExecuteService"],
                _common_services__WEBPACK_IMPORTED_MODULE_11__["ToastService"],
                _services_analyze_export_service__WEBPACK_IMPORTED_MODULE_14__["AnalyzeExportService"],
                _common_services__WEBPACK_IMPORTED_MODULE_11__["ChartService"]
            ],
            exports: [_executed_view_component__WEBPACK_IMPORTED_MODULE_4__["ExecutedViewComponent"]]
        })
    ], ExecutedViewModule);
    return ExecutedViewModule;
}());




/***/ }),

/***/ "./src/app/modules/analyze/executed-view/list/executed-list.component.html":
/*!*********************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/list/executed-list.component.html ***!
  \*********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<dx-data-grid [customizeColumns]=\"config.customizeColumns\"\n              [columnAutoWidth]=\"config.columnAutoWidth\"\n              [columnMinWidth]=\"config.columnMinWidth\"\n              [columnResizingMode]=\"config.columnResizingMode\"\n              [allowColumnReordering]=\"config.allowColumnReordering\"\n              [allowColumnResizing]=\"config.allowColumnResizing\"\n              [showColumnHeaders]=\"config.showColumnHeaders\"\n              [showColumnLines]=\"config.showColumnLines\"\n              [showRowLines]=\"config.showRowLines\"\n              [showBorders]=\"config.showBorders\"\n              [rowAlternationEnabled]=\"config.rowAlternationEnabled\"\n              [hoverStateEnabled]=\"config.hoverStateEnabled\"\n              [wordWrapEnabled]=\"config.wordWrapEnabled\"\n              [scrolling]=\"config.scrolling\"\n              [sorting]=\"config.sorting\"\n              [dataSource]=\"analyses\"\n              [columns]=\"config.columns\"\n              [pager]=\"config.pager\"\n              [paging]=\"config.paging\"\n              (onRowClick)=\"config.onRowClick($event)\"\n>\n  </dx-data-grid>\n"

/***/ }),

/***/ "./src/app/modules/analyze/executed-view/list/executed-list.component.ts":
/*!*******************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/list/executed-list.component.ts ***!
  \*******************************************************************************/
/*! exports provided: ExecutedListComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExecutedListComponent", function() { return ExecutedListComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var moment__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! moment */ "./node_modules/moment/moment.js");
/* harmony import */ var moment__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(moment__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../../common/services/dxDataGrid.service */ "./src/app/common/services/dxDataGrid.service.ts");






var ExecutedListComponent = /** @class */ (function () {
    function ExecutedListComponent(_DxDataGridService, _router) {
        this._DxDataGridService = _DxDataGridService;
        this._router = _router;
        this.selectExecution = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
    }
    Object.defineProperty(ExecutedListComponent.prototype, "setAnalyses", {
        set: function (analyses) {
            this.analyses = analyses;
            this.config = this.getGridConfig();
        },
        enumerable: true,
        configurable: true
    });
    ExecutedListComponent.prototype.goToExecution = function (executedAnalysis) {
        this._router.navigate(['analyze', 'analysis', this.analysis.id, 'executed'], {
            queryParams: {
                executionId: executedAnalysis.id,
                awaitingExecution: false,
                loadLastExecution: false
            }
        });
    };
    ExecutedListComponent.prototype.getGridConfig = function () {
        var _this = this;
        var columns = [
            {
                caption: 'DATE',
                dataField: 'finished',
                dataType: 'date',
                calculateCellValue: function (rowData) {
                    return rowData.finishedTime
                        ? moment__WEBPACK_IMPORTED_MODULE_3__["utc"](rowData.finishedTime)
                            .local()
                            .format('YYYY/MM/DD h:mm A')
                        : null;
                },
                allowSorting: true,
                alignment: 'left',
                width: '40%'
            },
            {
                caption: 'TYPE',
                dataField: 'executionType',
                allowSorting: true,
                alignment: 'left',
                width: '30%'
            },
            {
                caption: 'STATUS',
                dataField: 'status',
                allowSorting: true,
                alignment: 'center',
                encodeHtml: false,
                width: '30%',
                calculateCellValue: function (data) {
                    return !data.status || data.status.toLowerCase() === 'success'
                        ? '<i class="icon-checkmark" style="font-size: 16px; color: green; margin-left: 10px"></i>'
                        : '<i class="icon-close" style="font-size: 10px; color: red; margin-left: 10px"></i>';
                }
            }
        ];
        return this._DxDataGridService.mergeWithDefaultConfig({
            onRowClick: function (row) {
                _this.selectExecution.emit(lodash_get__WEBPACK_IMPORTED_MODULE_4__(row, 'data.id') || lodash_get__WEBPACK_IMPORTED_MODULE_4__(row, 'data.executionId'));
            },
            columns: columns,
            paging: {
                pageSize: 10
            },
            pager: {
                showPageSizeSelector: true,
                showInfo: true
            }
        });
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])('analyses'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Array])
    ], ExecutedListComponent.prototype, "setAnalyses", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], ExecutedListComponent.prototype, "analysis", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], ExecutedListComponent.prototype, "selectExecution", void 0);
    ExecutedListComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'executed-list',
            template: __webpack_require__(/*! ./executed-list.component.html */ "./src/app/modules/analyze/executed-view/list/executed-list.component.html"),
            styles: ["\n      :host {\n        display: block;\n      }"]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_5__["DxDataGridService"],
            _angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"]])
    ], ExecutedListComponent);
    return ExecutedListComponent;
}());



/***/ }),

/***/ "./src/app/modules/analyze/executed-view/list/index.ts":
/*!*************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/list/index.ts ***!
  \*************************************************************/
/*! exports provided: ExecutedListComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _executed_list_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./executed-list.component */ "./src/app/modules/analyze/executed-view/list/executed-list.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ExecutedListComponent", function() { return _executed_list_component__WEBPACK_IMPORTED_MODULE_0__["ExecutedListComponent"]; });




/***/ }),

/***/ "./src/app/modules/analyze/executed-view/map-chart/executed-map-chart-view.component.html":
/*!************************************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/map-chart/executed-map-chart-view.component.html ***!
  \************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div style=\"width:100%;float: left;\">\n  <h1 style=\"padding:0;\" class=\"chart report-title\" [textContent]=\"analysis.chartTitle || analysis.name\"></h1>\n</div>\n\n<map-chart-viewer [chartType]=\"analysis.chartType\"\n                  [data]=\"data\"\n                  [analysis]=\"analysis\"\n                  [updater]=\"updater\"\n                  [actionBus]=\"actionBus\"\n>\n</map-chart-viewer>\n"

/***/ }),

/***/ "./src/app/modules/analyze/executed-view/map-chart/executed-map-chart-view.component.scss":
/*!************************************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/map-chart/executed-map-chart-view.component.scss ***!
  \************************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".chart.report-title {\n  margin: 0;\n  padding: 12px 0 12px;\n  text-align: center;\n  font-weight: normal;\n  font-size: 18px; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FuYWx5emUvZXhlY3V0ZWQtdmlldy9tYXAtY2hhcnQvZXhlY3V0ZWQtbWFwLWNoYXJ0LXZpZXcuY29tcG9uZW50LnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBSUE7RUFDRSxTQUFTO0VBQ1Qsb0JBTDBCO0VBTTFCLGtCQUFrQjtFQUNsQixtQkFBbUI7RUFDbkIsZUFQcUIsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvYW5hbHl6ZS9leGVjdXRlZC12aWV3L21hcC1jaGFydC9leGVjdXRlZC1tYXAtY2hhcnQtdmlldy5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIiRoZWFkZXItcGFkZGluZy10b3A6IDEycHg7XG4kaGVhZGVyLXBhZGRpbmctYm90dG9tOiAxMnB4O1xuJGhlYWRlci1mb250LXNpemU6IDE4cHg7XG5cbi5jaGFydC5yZXBvcnQtdGl0bGUge1xuICBtYXJnaW46IDA7XG4gIHBhZGRpbmc6ICRoZWFkZXItcGFkZGluZy10b3AgMCAkaGVhZGVyLXBhZGRpbmctYm90dG9tO1xuICB0ZXh0LWFsaWduOiBjZW50ZXI7XG4gIGZvbnQtd2VpZ2h0OiBub3JtYWw7XG4gIGZvbnQtc2l6ZTogJGhlYWRlci1mb250LXNpemU7XG59XG4iXX0= */"

/***/ }),

/***/ "./src/app/modules/analyze/executed-view/map-chart/executed-map-chart-view.component.ts":
/*!**********************************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/map-chart/executed-map-chart-view.component.ts ***!
  \**********************************************************************************************/
/*! exports provided: ExecutedMapChartViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExecutedMapChartViewComponent", function() { return ExecutedMapChartViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");



var ExecutedMapChartViewComponent = /** @class */ (function () {
    function ExecutedMapChartViewComponent() {
    }
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_2__["BehaviorSubject"])
    ], ExecutedMapChartViewComponent.prototype, "updater", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_2__["Subject"])
    ], ExecutedMapChartViewComponent.prototype, "actionBus", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], ExecutedMapChartViewComponent.prototype, "analysis", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], ExecutedMapChartViewComponent.prototype, "data", void 0);
    ExecutedMapChartViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'executed-map-chart-view',
            template: __webpack_require__(/*! ./executed-map-chart-view.component.html */ "./src/app/modules/analyze/executed-view/map-chart/executed-map-chart-view.component.html"),
            styles: [__webpack_require__(/*! ./executed-map-chart-view.component.scss */ "./src/app/modules/analyze/executed-view/map-chart/executed-map-chart-view.component.scss")]
        })
    ], ExecutedMapChartViewComponent);
    return ExecutedMapChartViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/analyze/executed-view/map-chart/index.ts":
/*!******************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/map-chart/index.ts ***!
  \******************************************************************/
/*! exports provided: ExecutedMapChartViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _executed_map_chart_view_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./executed-map-chart-view.component */ "./src/app/modules/analyze/executed-view/map-chart/executed-map-chart-view.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ExecutedMapChartViewComponent", function() { return _executed_map_chart_view_component__WEBPACK_IMPORTED_MODULE_0__["ExecutedMapChartViewComponent"]; });




/***/ }),

/***/ "./src/app/modules/analyze/executed-view/map/executed-map-view.component.html":
/*!************************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/map/executed-map-view.component.html ***!
  \************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<map-box\n  [data]=\"data\"\n  [sqlBuilder]=\"analysis.sqlBuilder\"\n  [mapSettings]=\"analysis.mapOptions\"\n></map-box>\n"

/***/ }),

/***/ "./src/app/modules/analyze/executed-view/map/executed-map-view.component.ts":
/*!**********************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/map/executed-map-view.component.ts ***!
  \**********************************************************************************/
/*! exports provided: ExecutedMapViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExecutedMapViewComponent", function() { return ExecutedMapViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");



var ExecutedMapViewComponent = /** @class */ (function () {
    function ExecutedMapViewComponent() {
    }
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_2__["Subject"])
    ], ExecutedMapViewComponent.prototype, "actionBus", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], ExecutedMapViewComponent.prototype, "analysis", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], ExecutedMapViewComponent.prototype, "data", void 0);
    ExecutedMapViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'executed-map-view',
            template: __webpack_require__(/*! ./executed-map-view.component.html */ "./src/app/modules/analyze/executed-view/map/executed-map-view.component.html"),
            styles: [":host {\n    display: block;\n    height: calc(100vh - 250px);\n  }"]
        })
    ], ExecutedMapViewComponent);
    return ExecutedMapViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/analyze/executed-view/map/index.ts":
/*!************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/map/index.ts ***!
  \************************************************************/
/*! exports provided: ExecutedMapViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _executed_map_view_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./executed-map-view.component */ "./src/app/modules/analyze/executed-view/map/executed-map-view.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ExecutedMapViewComponent", function() { return _executed_map_view_component__WEBPACK_IMPORTED_MODULE_0__["ExecutedMapViewComponent"]; });




/***/ }),

/***/ "./src/app/modules/analyze/executed-view/pivot/executed-pivot-view.component.html":
/*!****************************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/pivot/executed-pivot-view.component.html ***!
  \****************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<pivot-grid *ngIf=\"analysis && data && analysis.type === 'pivot'\" class=\"executed-view-pivot\"\n            [artifactColumns]=\"artifactColumns\"\n            [sorts]=\"sorts\"\n            [updater]=\"updater\"\n            [data]=\"data\"\n            [showFieldDetails]=\"true\">\n</pivot-grid>\n"

/***/ }),

/***/ "./src/app/modules/analyze/executed-view/pivot/executed-pivot-view.component.scss":
/*!****************************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/pivot/executed-pivot-view.component.scss ***!
  \****************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "::ng-deep .executed-view-pivot {\n  overflow: hidden !important; }\n  ::ng-deep .executed-view-pivot dx-pivot-grid {\n    height: calc(100vh - 300px) !important; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FuYWx5emUvZXhlY3V0ZWQtdmlldy9waXZvdC9leGVjdXRlZC1waXZvdC12aWV3LmNvbXBvbmVudC5zY3NzIl0sIm5hbWVzIjpbXSwibWFwcGluZ3MiOiJBQUFBO0VBQ0UsMkJBQTJCLEVBQUE7RUFEN0I7SUFHSSxzQ0FBc0MsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvYW5hbHl6ZS9leGVjdXRlZC12aWV3L3Bpdm90L2V4ZWN1dGVkLXBpdm90LXZpZXcuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyI6Om5nLWRlZXAgLmV4ZWN1dGVkLXZpZXctcGl2b3Qge1xuICBvdmVyZmxvdzogaGlkZGVuICFpbXBvcnRhbnQ7XG4gIGR4LXBpdm90LWdyaWQge1xuICAgIGhlaWdodDogY2FsYygxMDB2aCAtIDMwMHB4KSAhaW1wb3J0YW50O1xuICB9XG59XG4iXX0= */"

/***/ }),

/***/ "./src/app/modules/analyze/executed-view/pivot/executed-pivot-view.component.ts":
/*!**************************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/pivot/executed-pivot-view.component.ts ***!
  \**************************************************************************************/
/*! exports provided: ExecutedPivotViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExecutedPivotViewComponent", function() { return ExecutedPivotViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var rxjs__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! rxjs */ "./node_modules/rxjs/_esm5/index.js");
/* harmony import */ var _types__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../types */ "./src/app/modules/analyze/types.ts");
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/map */ "./node_modules/lodash/map.js");
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_map__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/find */ "./node_modules/lodash/find.js");
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_find__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/forEach */ "./node_modules/lodash/forEach.js");
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_forEach__WEBPACK_IMPORTED_MODULE_6__);







var ExecutedPivotViewComponent = /** @class */ (function () {
    function ExecutedPivotViewComponent() {
    }
    Object.defineProperty(ExecutedPivotViewComponent.prototype, "setAnalysis", {
        set: function (analysis) {
            this.analysis = analysis;
            this.artifactColumns = this.getArtifactColumns(analysis);
            this.sorts = analysis.sipQuery.sorts;
        },
        enumerable: true,
        configurable: true
    });
    /* Use sqlBuilder to update selected fields in artifacts */
    ExecutedPivotViewComponent.prototype.getArtifactColumns = function (analysis) {
        var row = [];
        var data = [];
        var column = [];
        if (Object(_types__WEBPACK_IMPORTED_MODULE_3__["isDSLAnalysis"])(analysis)) {
            lodash_forEach__WEBPACK_IMPORTED_MODULE_6__(analysis.sipQuery.artifacts, function (table) {
                lodash_forEach__WEBPACK_IMPORTED_MODULE_6__(table.fields, function (field) {
                    if (field.type === 'date') {
                        field.dateInterval = field.groupInterval;
                    }
                    if (field.area === 'row') {
                        row.push(field);
                    }
                    if (field.area === 'data') {
                        data.push(field);
                    }
                    if (field.area === 'column') {
                        column.push(field);
                    }
                });
            });
        }
        /* These counters are for legacy purpose. If areaIndex is not saved
         * in a field in sqlBuilder, then these will be used to get an area
         * index instead. This may result in re-ordering of columns */
        var rowId = 0, colId = 0, dataId = 0;
        return lodash_map__WEBPACK_IMPORTED_MODULE_4__(analysis.sipQuery.artifacts[0].fields, function (artifactColumn) {
            /* Find out if this column has been selected in row, column or data area */
            var isRow = lodash_find__WEBPACK_IMPORTED_MODULE_5__(row, function (c) { return c.columnName === artifactColumn.columnName; });
            var isColumn = lodash_find__WEBPACK_IMPORTED_MODULE_5__(column, function (c) { return c.columnName === artifactColumn.columnName; });
            var isData = lodash_find__WEBPACK_IMPORTED_MODULE_5__(data, function (c) { return c.columnName === artifactColumn.columnName; });
            /* If column wasn't selected in any area, mark it unselected and return */
            if (!isRow && !isColumn && !isData) {
                return tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, artifactColumn, { checked: false, area: null, areaIndex: null });
            }
            /* Otherwise, update area related fields accordingly */
            if (isRow) {
                return tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, artifactColumn, { checked: true, area: 'row', areaIndex: isRow.areaIndex || rowId++ });
            }
            else if (isColumn) {
                return tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, artifactColumn, { checked: true, area: 'column', areaIndex: isColumn.areaIndex || colId++ });
            }
            else if (isData) {
                return tslib__WEBPACK_IMPORTED_MODULE_0__["__assign"]({}, artifactColumn, { checked: true, area: 'data', areaIndex: isData.areaIndex || dataId++ });
            }
        });
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])('analysis'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object])
    ], ExecutedPivotViewComponent.prototype, "setAnalysis", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], ExecutedPivotViewComponent.prototype, "data", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", rxjs__WEBPACK_IMPORTED_MODULE_2__["Subject"])
    ], ExecutedPivotViewComponent.prototype, "updater", void 0);
    ExecutedPivotViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'executed-pivot-view',
            template: __webpack_require__(/*! ./executed-pivot-view.component.html */ "./src/app/modules/analyze/executed-view/pivot/executed-pivot-view.component.html"),
            styles: [__webpack_require__(/*! ./executed-pivot-view.component.scss */ "./src/app/modules/analyze/executed-view/pivot/executed-pivot-view.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [])
    ], ExecutedPivotViewComponent);
    return ExecutedPivotViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/analyze/executed-view/pivot/index.ts":
/*!**************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/pivot/index.ts ***!
  \**************************************************************/
/*! exports provided: ExecutedPivotViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _executed_pivot_view_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./executed-pivot-view.component */ "./src/app/modules/analyze/executed-view/pivot/executed-pivot-view.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ExecutedPivotViewComponent", function() { return _executed_pivot_view_component__WEBPACK_IMPORTED_MODULE_0__["ExecutedPivotViewComponent"]; });




/***/ }),

/***/ "./src/app/modules/analyze/executed-view/report/executed-report-view.component.html":
/*!******************************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/report/executed-report-view.component.html ***!
  \******************************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div class=\"view-analysis-grid\" [ngStyle]=\"{'height': analysis.sqlBuilder.filters.length !== 0 ? 'calc(100vh - 312px)' : 'calc(100vh - 193px)'}\">\n  <report-grid-upgraded *ngIf=\"analysis && dataLoader\"\n                        (change)=\"onReportGridChange($event)\"\n                        [artifacts]=\"artifacts\"\n                        [analysis]=\"analysis\"\n                        [sorts]=\"analysis.sqlBuilder.sorts || analysis.sqlBuilder.orderByColumns\"\n                        [dataLoader]=\"dataLoader\">\n  </report-grid-upgraded>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/analyze/executed-view/report/executed-report-view.component.ts":
/*!****************************************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/report/executed-report-view.component.ts ***!
  \****************************************************************************************/
/*! exports provided: ExecutedReportViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "ExecutedReportViewComponent", function() { return ExecutedReportViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");


var ExecutedReportViewComponent = /** @class */ (function () {
    function ExecutedReportViewComponent() {
    }
    Object.defineProperty(ExecutedReportViewComponent.prototype, "setAnalysis", {
        set: function (analysis) {
            this.analysis = analysis;
            // if in query mode, don't send the artifacts, just use the column names in the data
            // TODO use the columns from the query
            var isEsReport = analysis.type === 'esReport';
            var isInQueryMode = analysis.edit;
            var dataFields = analysis.sqlBuilder.dataFields;
            if (isInQueryMode) {
                this.artifacts = null;
            }
            else if (isEsReport) {
                var containsArtifacts = dataFields[0].tableName;
                if (containsArtifacts) {
                    this.artifacts = dataFields;
                }
                else {
                    // for backward compatibility we have to check if we have the artifacts, or artifact columns
                    this.artifacts = [
                        { columns: dataFields, artifactName: '' }
                    ];
                }
            }
            else {
                // DL report
                this.artifacts = dataFields;
            }
        },
        enumerable: true,
        configurable: true
    });
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])('analysis'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object])
    ], ExecutedReportViewComponent.prototype, "setAnalysis", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Function)
    ], ExecutedReportViewComponent.prototype, "dataLoader", void 0);
    ExecutedReportViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'executed-report-view',
            template: __webpack_require__(/*! ./executed-report-view.component.html */ "./src/app/modules/analyze/executed-view/report/executed-report-view.component.html")
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [])
    ], ExecutedReportViewComponent);
    return ExecutedReportViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/analyze/executed-view/report/index.ts":
/*!***************************************************************!*\
  !*** ./src/app/modules/analyze/executed-view/report/index.ts ***!
  \***************************************************************/
/*! exports provided: ExecutedReportViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _executed_report_view_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./executed-report-view.component */ "./src/app/modules/analyze/executed-view/report/executed-report-view.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "ExecutedReportViewComponent", function() { return _executed_report_view_component__WEBPACK_IMPORTED_MODULE_0__["ExecutedReportViewComponent"]; });




/***/ }),

/***/ "./src/app/modules/analyze/guards/default-analyze-category.guard.ts":
/*!**************************************************************************!*\
  !*** ./src/app/modules/analyze/guards/default-analyze-category.guard.ts ***!
  \**************************************************************************/
/*! exports provided: DefaultAnalyzeCategoryGuard */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "DefaultAnalyzeCategoryGuard", function() { return DefaultAnalyzeCategoryGuard; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var lodash_fp_first__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/fp/first */ "./node_modules/lodash/fp/first.js");
/* harmony import */ var lodash_fp_first__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_first__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var lodash_fp_get__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/fp/get */ "./node_modules/lodash/fp/get.js");
/* harmony import */ var lodash_fp_get__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_get__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/fp/pipe */ "./node_modules/lodash/fp/pipe.js");
/* harmony import */ var lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var _common_services_menu_service__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../../common/services/menu.service */ "./src/app/common/services/menu.service.ts");
/* harmony import */ var _common_local_storage_keys__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../common/local-storage-keys */ "./src/app/common/local-storage-keys.ts");








var DefaultAnalyzeCategoryGuard = /** @class */ (function () {
    function DefaultAnalyzeCategoryGuard(_router, _menu) {
        this._router = _router;
        this._menu = _menu;
    }
    DefaultAnalyzeCategoryGuard.prototype.canActivate = function (route) {
        var _this = this;
        var params = lodash_fp_get__WEBPACK_IMPORTED_MODULE_4__('children[0].paramMap', route);
        if (params && params.keys.length) {
            return true;
        }
        var queryParams = lodash_fp_get__WEBPACK_IMPORTED_MODULE_4__('children[0].queryParamMap', route);
        if (queryParams && queryParams.keys.length) {
            return true;
        }
        return this._menu.getMenu('ANALYZE').then(function (menu) {
            return _this.goToDefaultChildStateIfNeeded(menu);
        });
    };
    DefaultAnalyzeCategoryGuard.prototype.goToDefaultChildStateIfNeeded = function (menu) {
        var id = window.localStorage[_common_local_storage_keys__WEBPACK_IMPORTED_MODULE_7__["LAST_ANALYSES_CATEGORY_ID"]] ||
            lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_5__(lodash_fp_first__WEBPACK_IMPORTED_MODULE_3__, lodash_fp_get__WEBPACK_IMPORTED_MODULE_4__('children'), lodash_fp_first__WEBPACK_IMPORTED_MODULE_3__, lodash_fp_get__WEBPACK_IMPORTED_MODULE_4__('id'))(menu);
        this._router.navigate(['analyze', id]);
        return false;
    };
    DefaultAnalyzeCategoryGuard = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"], _common_services_menu_service__WEBPACK_IMPORTED_MODULE_6__["MenuService"]])
    ], DefaultAnalyzeCategoryGuard);
    return DefaultAnalyzeCategoryGuard;
}());



/***/ }),

/***/ "./src/app/modules/analyze/guards/index.ts":
/*!*************************************************!*\
  !*** ./src/app/modules/analyze/guards/index.ts ***!
  \*************************************************/
/*! exports provided: DefaultAnalyzeCategoryGuard */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _default_analyze_category_guard__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./default-analyze-category.guard */ "./src/app/modules/analyze/guards/default-analyze-category.guard.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "DefaultAnalyzeCategoryGuard", function() { return _default_analyze_category_guard__WEBPACK_IMPORTED_MODULE_0__["DefaultAnalyzeCategoryGuard"]; });




/***/ }),

/***/ "./src/app/modules/analyze/page/analyze-page.component.html":
/*!******************************************************************!*\
  !*** ./src/app/modules/analyze/page/analyze-page.component.html ***!
  \******************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<router-outlet></router-outlet>\n"

/***/ }),

/***/ "./src/app/modules/analyze/page/analyze-page.component.ts":
/*!****************************************************************!*\
  !*** ./src/app/modules/analyze/page/analyze-page.component.ts ***!
  \****************************************************************/
/*! exports provided: AnalyzePageComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnalyzePageComponent", function() { return AnalyzePageComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var lodash_split__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/split */ "./node_modules/lodash/split.js");
/* harmony import */ var lodash_split__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_split__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var _common_local_storage_keys__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../common/local-storage-keys */ "./src/app/common/local-storage-keys.ts");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");






var AnalyzePageComponent = /** @class */ (function () {
    function AnalyzePageComponent(_jwt, _router) {
        var _this = this;
        this._jwt = _jwt;
        this._router = _router;
        this._router.events.subscribe(function (event) {
            _this.saveAnalyzeCategoryId(event);
        });
    }
    AnalyzePageComponent.prototype.saveAnalyzeCategoryId = function (event) {
        if (event instanceof _angular_router__WEBPACK_IMPORTED_MODULE_2__["NavigationEnd"]) {
            var _a = lodash_split__WEBPACK_IMPORTED_MODULE_3__(event.url, '/'), base = _a[0], id = _a[1];
            if (base === 'analyze' && id) {
                var key = _common_local_storage_keys__WEBPACK_IMPORTED_MODULE_4__["LAST_ANALYSES_CATEGORY_ID"] + "-" + this._jwt.getUserId();
                window.localStorage[key] = id;
            }
        }
    };
    AnalyzePageComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'analyze-page',
            template: __webpack_require__(/*! ./analyze-page.component.html */ "./src/app/modules/analyze/page/analyze-page.component.html")
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services__WEBPACK_IMPORTED_MODULE_5__["JwtService"], _angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"]])
    ], AnalyzePageComponent);
    return AnalyzePageComponent;
}());



/***/ }),

/***/ "./src/app/modules/analyze/page/index.ts":
/*!***********************************************!*\
  !*** ./src/app/modules/analyze/page/index.ts ***!
  \***********************************************/
/*! exports provided: AnalyzePageComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _analyze_page_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./analyze-page.component */ "./src/app/modules/analyze/page/analyze-page.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AnalyzePageComponent", function() { return _analyze_page_component__WEBPACK_IMPORTED_MODULE_0__["AnalyzePageComponent"]; });




/***/ }),

/***/ "./src/app/modules/analyze/publish/index.ts":
/*!**************************************************!*\
  !*** ./src/app/modules/analyze/publish/index.ts ***!
  \**************************************************/
/*! exports provided: AnalyzePublishDialogModule, AnalyzePublishDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnalyzePublishDialogModule", function() { return AnalyzePublishDialogModule; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _dialog_analyze_publish__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./dialog/analyze-publish */ "./src/app/modules/analyze/publish/dialog/analyze-publish/index.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AnalyzePublishDialogComponent", function() { return _dialog_analyze_publish__WEBPACK_IMPORTED_MODULE_2__["AnalyzePublishDialogComponent"]; });

/* harmony import */ var _dialog_analyze_schedule__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./dialog/analyze-schedule */ "./src/app/modules/analyze/publish/dialog/analyze-schedule/index.ts");
/* harmony import */ var _common__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../../common */ "./src/app/common/index.ts");





var COMPONENTS = [
    _dialog_analyze_publish__WEBPACK_IMPORTED_MODULE_2__["AnalyzePublishDialogComponent"],
    _dialog_analyze_schedule__WEBPACK_IMPORTED_MODULE_3__["AnalyzeScheduleDialogComponent"]
];
var AnalyzePublishDialogModule = /** @class */ (function () {
    function AnalyzePublishDialogModule() {
    }
    AnalyzePublishDialogModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["NgModule"])({
            imports: [
                _common__WEBPACK_IMPORTED_MODULE_4__["CommonModuleTs"]
            ],
            declarations: COMPONENTS,
            entryComponents: COMPONENTS,
            exports: [_dialog_analyze_publish__WEBPACK_IMPORTED_MODULE_2__["AnalyzePublishDialogComponent"]]
        })
    ], AnalyzePublishDialogModule);
    return AnalyzePublishDialogModule;
}());




/***/ }),

/***/ "./src/app/modules/analyze/routes.ts":
/*!*******************************************!*\
  !*** ./src/app/modules/analyze/routes.ts ***!
  \*******************************************/
/*! exports provided: routes */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "routes", function() { return routes; });
/* harmony import */ var _view__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./view */ "./src/app/modules/analyze/view/index.ts");
/* harmony import */ var _executed_view__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! ./executed-view */ "./src/app/modules/analyze/executed-view/index.ts");
/* harmony import */ var _page__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ./page */ "./src/app/modules/analyze/page/index.ts");
/* harmony import */ var _designer__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ./designer */ "./src/app/modules/analyze/designer/index.ts");
/* harmony import */ var _common_guards__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../common/guards */ "./src/app/common/guards/index.ts");
/* harmony import */ var _guards__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./guards */ "./src/app/modules/analyze/guards/index.ts");






var routes = [
    {
        // name: 'analyze',
        path: '',
        canActivate: [_common_guards__WEBPACK_IMPORTED_MODULE_4__["IsUserLoggedInGuard"], _guards__WEBPACK_IMPORTED_MODULE_5__["DefaultAnalyzeCategoryGuard"]],
        canActivateChild: [_common_guards__WEBPACK_IMPORTED_MODULE_4__["IsUserLoggedInGuard"]],
        component: _page__WEBPACK_IMPORTED_MODULE_2__["AnalyzePageComponent"],
        children: [
            {
                // name: 'analyze.executedDetail',
                path: 'analysis/:analysisId/executed',
                component: _executed_view__WEBPACK_IMPORTED_MODULE_1__["ExecutedViewComponent"]
                // params: {
                //   analysis: null,
                //   awaitingExecution: false,
                //   loadLastExecution: false,
                //   executionId: null
                // }
            },
            {
                path: 'designer',
                component: _designer__WEBPACK_IMPORTED_MODULE_3__["DesignerPageComponent"]
            },
            {
                // name: 'analyze.view',
                path: ':id',
                component: _view__WEBPACK_IMPORTED_MODULE_0__["AnalyzeViewComponent"]
            }
        ]
    }
];


/***/ }),

/***/ "./src/app/modules/analyze/services/analyze-export.service.ts":
/*!********************************************************************!*\
  !*** ./src/app/modules/analyze/services/analyze-export.service.ts ***!
  \********************************************************************/
/*! exports provided: AnalyzeExportService */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnalyzeExportService", function() { return AnalyzeExportService; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var lodash_fp_pick__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! lodash/fp/pick */ "./node_modules/lodash/fp/pick.js");
/* harmony import */ var lodash_fp_pick__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_pick__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/fp/pipe */ "./node_modules/lodash/fp/pipe.js");
/* harmony import */ var lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var lodash_fp_map__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/fp/map */ "./node_modules/lodash/fp/map.js");
/* harmony import */ var lodash_fp_map__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_map__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/map */ "./node_modules/lodash/map.js");
/* harmony import */ var lodash_map__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_map__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var lodash_flatMap__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/flatMap */ "./node_modules/lodash/flatMap.js");
/* harmony import */ var lodash_flatMap__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_flatMap__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var lodash_replace__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! lodash/replace */ "./node_modules/lodash/replace.js");
/* harmony import */ var lodash_replace__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(lodash_replace__WEBPACK_IMPORTED_MODULE_7__);
/* harmony import */ var lodash_indexOf__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! lodash/indexOf */ "./node_modules/lodash/indexOf.js");
/* harmony import */ var lodash_indexOf__WEBPACK_IMPORTED_MODULE_8___default = /*#__PURE__*/__webpack_require__.n(lodash_indexOf__WEBPACK_IMPORTED_MODULE_8__);
/* harmony import */ var lodash_slice__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! lodash/slice */ "./node_modules/lodash/slice.js");
/* harmony import */ var lodash_slice__WEBPACK_IMPORTED_MODULE_9___default = /*#__PURE__*/__webpack_require__.n(lodash_slice__WEBPACK_IMPORTED_MODULE_9__);
/* harmony import */ var json_2_csv__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! json-2-csv */ "./node_modules/json-2-csv/lib/converter.js");
/* harmony import */ var json_2_csv__WEBPACK_IMPORTED_MODULE_10___default = /*#__PURE__*/__webpack_require__.n(json_2_csv__WEBPACK_IMPORTED_MODULE_10__);
/* harmony import */ var lodash_keys__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! lodash/keys */ "./node_modules/lodash/keys.js");
/* harmony import */ var lodash_keys__WEBPACK_IMPORTED_MODULE_11___default = /*#__PURE__*/__webpack_require__.n(lodash_keys__WEBPACK_IMPORTED_MODULE_11__);
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! lodash/forEach */ "./node_modules/lodash/forEach.js");
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_12___default = /*#__PURE__*/__webpack_require__.n(lodash_forEach__WEBPACK_IMPORTED_MODULE_12__);
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! lodash/isUndefined */ "./node_modules/lodash/isUndefined.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_13___default = /*#__PURE__*/__webpack_require__.n(lodash_isUndefined__WEBPACK_IMPORTED_MODULE_13__);
/* harmony import */ var file_saver__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! file-saver */ "./node_modules/file-saver/FileSaver.js");
/* harmony import */ var file_saver__WEBPACK_IMPORTED_MODULE_14___default = /*#__PURE__*/__webpack_require__.n(file_saver__WEBPACK_IMPORTED_MODULE_14__);
/* harmony import */ var blob__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! blob */ "./node_modules/blob/index.js");
/* harmony import */ var blob__WEBPACK_IMPORTED_MODULE_15___default = /*#__PURE__*/__webpack_require__.n(blob__WEBPACK_IMPORTED_MODULE_15__);
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_16___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_16__);
/* harmony import */ var _actions__WEBPACK_IMPORTED_MODULE_17__ = __webpack_require__(/*! ../actions */ "./src/app/modules/analyze/actions/index.ts");
/* harmony import */ var _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_18__ = __webpack_require__(/*! ../../../common/services/toastMessage.service */ "./src/app/common/services/toastMessage.service.ts");
/* harmony import */ var _common_utils_dataFlattener__WEBPACK_IMPORTED_MODULE_19__ = __webpack_require__(/*! ./../../../common/utils/dataFlattener */ "./src/app/common/utils/dataFlattener.ts");




















var AnalyzeExportService = /** @class */ (function () {
    function AnalyzeExportService(_analyzeActionsService, _toastMessage) {
        this._analyzeActionsService = _analyzeActionsService;
        this._toastMessage = _toastMessage;
    }
    AnalyzeExportService.prototype.export = function (analysis, executionId, executionType) {
        var _this = this;
        if (executionType === void 0) { executionType = 'normal'; }
        var analysisId = analysis.id;
        var analysisType = analysis.type;
        this._analyzeActionsService
            .exportAnalysis(analysisId, executionId, analysisType, executionType)
            .then(function (data) {
            var exportData = lodash_get__WEBPACK_IMPORTED_MODULE_16__(data, 'data');
            var fields = _this.getCheckedFieldsForExport(analysis, exportData);
            fields = _this.checkColumnName(fields);
            var columnNames = lodash_map__WEBPACK_IMPORTED_MODULE_5__(fields, 'columnName');
            var exportOptions = {
                trimHeaderFields: false,
                emptyFieldValue: '',
                checkSchemaDifferences: false,
                delimiter: {
                    wrap: '"',
                    eol: '\r\n'
                },
                columnNames: columnNames
            };
            exportData = ['report', 'esReport'].includes(analysisType)
                ? Object(_common_utils_dataFlattener__WEBPACK_IMPORTED_MODULE_19__["checkNullinReportData"])(exportData)
                : exportData;
            Object(json_2_csv__WEBPACK_IMPORTED_MODULE_10__["json2csv"])(exportData, function (err, csv) {
                if (err) {
                    _this._toastMessage.error('There was an error while exporting, please try again witha different dataset.');
                }
                var csvWithDisplayNames = _this.replaceCSVHeader(csv, fields, analysis);
                _this.exportCSV(csvWithDisplayNames, analysis.name);
            }, exportOptions);
        });
    };
    AnalyzeExportService.prototype.replaceCSVHeader = function (csv, fields, analysis) {
        var firstNewLine = lodash_indexOf__WEBPACK_IMPORTED_MODULE_8__(csv, '\n');
        var firstRow = lodash_slice__WEBPACK_IMPORTED_MODULE_9__(csv, 0, firstNewLine).join('');
        var firstRowColumns = firstRow
            .split(',')
            .map(function (columnName) { return columnName.replace(/"/g, '').trim(); });
        /* Following logic replaces column names in CSV header row with their
           display names or aliases, while preserving the order they appear in
          */
        var displayNames = firstRowColumns
            .map(function (columnName) {
            var field = fields.find(function (f) { return f.columnName === columnName; });
            if (!field) {
                return columnName;
            }
            if (field.aggregate === 'distinctCount' && analysis.type === 'report') {
                return "distinctCount(" + (field.alias || field.displayName) + ")";
            }
            return field.alias || field.displayName;
        })
            .join(',');
        return lodash_replace__WEBPACK_IMPORTED_MODULE_7__(csv, firstRow, displayNames);
    };
    AnalyzeExportService.prototype.getCheckedFieldsForExport = function (analysis, data) {
        /* If report was using designer mode, find checked columns */
        if (!analysis.edit) {
            return lodash_flatMap__WEBPACK_IMPORTED_MODULE_6__(analysis.sqlBuilder.dataFields, function (artifact) {
                return lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_3__(lodash_fp_map__WEBPACK_IMPORTED_MODULE_4__(lodash_fp_pick__WEBPACK_IMPORTED_MODULE_2__(['columnName', 'alias', 'displayName', 'aggregate'])))(artifact.columns);
            });
        }
        /* If report was using sql mode, we don't really have any info
           about columns. Keys from individual data nodes are used as
           column names */
        if (data.length > 0) {
            return lodash_map__WEBPACK_IMPORTED_MODULE_5__(lodash_keys__WEBPACK_IMPORTED_MODULE_11__(data[0]), function (col) { return ({
                label: col,
                columnName: col,
                displayName: col,
                type: 'string'
            }); });
        }
    };
    AnalyzeExportService.prototype.checkColumnName = function (columns) {
        var _this = this;
        lodash_forEach__WEBPACK_IMPORTED_MODULE_12__(columns, function (column) {
            column.columnName = _this.getColumnName(column.columnName);
        });
        return columns;
    };
    AnalyzeExportService.prototype.getColumnName = function (columnName) {
        // take out the .keyword form the columnName
        // if there is one
        if (!lodash_isUndefined__WEBPACK_IMPORTED_MODULE_13__(columnName)) {
            var split = columnName.split('.');
            if (split[1]) {
                return split[0];
            }
            return columnName;
        }
    };
    AnalyzeExportService.prototype.exportCSV = function (str, fileName) {
        var blob = new blob__WEBPACK_IMPORTED_MODULE_15__([str], { type: 'text/csv;charset=utf-8' });
        Object(file_saver__WEBPACK_IMPORTED_MODULE_14__["saveAs"])(blob, (fileName || 'export') + ".csv");
    };
    AnalyzeExportService = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Injectable"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_actions__WEBPACK_IMPORTED_MODULE_17__["AnalyzeActionsService"],
            _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_18__["ToastService"]])
    ], AnalyzeExportService);
    return AnalyzeExportService;
}());



/***/ }),

/***/ "./src/app/modules/analyze/state/analyze.state.ts":
/*!********************************************************!*\
  !*** ./src/app/modules/analyze/state/analyze.state.ts ***!
  \********************************************************/
/*! exports provided: AnalyzeState */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnalyzeState", function() { return AnalyzeState; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _ngxs_store__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @ngxs/store */ "./node_modules/@ngxs/store/fesm5/ngxs-store.js");


var AnalyzeState = /** @class */ (function () {
    function AnalyzeState() {
    }
    AnalyzeState = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_ngxs_store__WEBPACK_IMPORTED_MODULE_1__["State"])({
            name: 'analyze',
            children: []
        })
    ], AnalyzeState);
    return AnalyzeState;
}());



/***/ }),

/***/ "./src/app/modules/analyze/view/analyses-filter.pipe.ts":
/*!**************************************************************!*\
  !*** ./src/app/modules/analyze/view/analyses-filter.pipe.ts ***!
  \**************************************************************/
/*! exports provided: AnalysesFilterPipe */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnalysesFilterPipe", function() { return AnalysesFilterPipe; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! lodash/filter */ "./node_modules/lodash/filter.js");
/* harmony import */ var lodash_filter__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(lodash_filter__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/find */ "./node_modules/lodash/find.js");
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_find__WEBPACK_IMPORTED_MODULE_3__);




var AnalysesFilterPipe = /** @class */ (function () {
    function AnalysesFilterPipe() {
    }
    AnalysesFilterPipe.prototype.transform = function (analyses, type, cronJobs) {
        var _this = this;
        if (type === 'all') {
            return analyses;
        }
        return lodash_filter__WEBPACK_IMPORTED_MODULE_2__(analyses, function (analysis) {
            switch (type) {
                case 'scheduled':
                    return _this.isInCronJobs(cronJobs, analysis.id);
                case 'report':
                    return analysis.type === 'esReport' || analysis.type === type;
                default:
                    return type === analysis.type;
            }
        });
    };
    AnalysesFilterPipe.prototype.isInCronJobs = function (cronJobs, id) {
        var cronJob = lodash_find__WEBPACK_IMPORTED_MODULE_3__(cronJobs, function (cron) { return cron.jobDetails.analysisID === id; });
        return Boolean(cronJob);
    };
    AnalysesFilterPipe = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Pipe"])({
            name: 'analysesFilter'
        })
    ], AnalysesFilterPipe);
    return AnalysesFilterPipe;
}());



/***/ }),

/***/ "./src/app/modules/analyze/view/analyze-view.component.html":
/*!******************************************************************!*\
  !*** ./src/app/modules/analyze/view/analyze-view.component.html ***!
  \******************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<mat-toolbar fxLayout=\"row\" fxLayoutAlign=\"space-between center\">\n  <div fxLayout=\"row\" fxFlex=\"40\">\n    <div class=\"category-title\" e2e=\"category-title\">\n      {{ categoryName | async }}\n    </div>\n    <search-box\n      placeholder=\"Search Analyses\"\n      [value]=\"filterObj.searchTerm\"\n      (searchTermChange)=\"applySearchFilter($event)\"\n      [delay]=\"1000\"\n    >\n    </search-box>\n  </div>\n\n  <div fxLayout=\"row\" fxLayoutAlign=\"start center\" fxFlex=\"40\">\n    <mat-radio-group\n      [value]=\"viewMode\"\n      (change)=\"onViewChange($event.value)\"\n      class=\"switch-view\"\n    >\n      <mat-radio-button [value]=\"LIST_VIEW\" e2e=\"analyze-list-view\">\n        <mat-icon\n          fontIcon=\"icon-list-view\"\n          style=\"font-size: 22px;\"\n          i18n-matTooltip\n          matTooltip=\"List view\"\n        ></mat-icon>\n      </mat-radio-button>\n      <mat-radio-button [value]=\"CARD_VIEW\" e2e=\"analyze-card-view\">\n        <mat-icon\n          fontIcon=\"icon-tile-view-solid\"\n          style=\"font-size: 18px;\"\n          i18n-matTooltip\n          matTooltip=\"Card view\"\n        ></mat-icon>\n      </mat-radio-button>\n    </mat-radio-group>\n\n    <mat-form-field\n      e2e=\"analysis-type-selector\"\n      class=\"select-form-field\"\n      appearance=\"outline\"\n      style=\"max-width: 170px; font-size: 14px; margin: 0;\"\n    >\n      <mat-select\n        class=\"select-type\"\n        (selectionChange)=\"onAnalysisTypeChange($event.value)\"\n        [value]=\"filterObj.analysisType\"\n      >\n        <mat-option *ngFor=\"let type of analysisTypes\" [value]=\"type.value\">\n          {{ type.label }}\n        </mat-option>\n      </mat-select>\n    </mat-form-field>\n  </div>\n  <div fxLayout=\"row\" fxLayoutAlign=\"end center\" fxFlex=\"20\">\n    <button\n      (click)=\"openNewAnalysisModal()\"\n      mat-raised-button\n      e2e=\"open-new-analysis-modal\"\n      color=\"primary\"\n    >\n      + <span i18n>ANALYSIS</span>\n    </button>\n  </div>\n</mat-toolbar>\n<mat-card [ngSwitch]=\"viewMode\" class=\"analyses-container\">\n  <analyze-list-view\n    *ngSwitchCase=\"LIST_VIEW\"\n    (action)=\"onAction($event)\"\n    [semanticLayerDataMap]=\"semanticLayerDataMap\"\n    [analyses]=\"\n      filteredAnalyses | analysesFilter: filterObj.analysisType:cronJobs\n    \"\n    [analysisType]=\"filterObj.analysisType\"\n    [searchTerm]=\"filterObj.searchTermValue\"\n    [cronJobs]=\"cronJobs\"\n  >\n  </analyze-list-view>\n  <analyze-card-view\n    *ngSwitchCase=\"CARD_VIEW\"\n    (action)=\"onAction($event)\"\n    [analyses]=\"\n      filteredAnalyses | analysesFilter: filterObj.analysisType:cronJobs\n    \"\n    [analysisType]=\"filterObj.analysisType\"\n    [highlightTerm]=\"filterObj.searchTermValue\"\n    [cronJobs]=\"cronJobs\"\n  >\n  </analyze-card-view>\n</mat-card>\n"

/***/ }),

/***/ "./src/app/modules/analyze/view/analyze-view.component.scss":
/*!******************************************************************!*\
  !*** ./src/app/modules/analyze/view/analyze-view.component.scss ***!
  \******************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  width: 100%; }\n\n.category-title {\n  color: #636363;\n  font-weight: bold; }\n\n.analyses-container {\n  margin: 0 10px;\n  padding: 0; }\n\n.mat-toolbar.mat-toolbar-single-row {\n  background-color: transparent; }\n\n.select-type {\n  width: 140px;\n  font-size: 16px; }\n\n.switch-view {\n  margin-right: 20px;\n  display: inline-flex; }\n\n.switch-view mat-radio-button {\n    margin: 0;\n    padding-right: 7px; }\n\n.switch-view mat-radio-button mat-icon {\n      font-size: 14px;\n      color: #5c6670; }\n\n.switch-view mat-radio-button.mat-radio-checked mat-icon {\n      color: #0077be; }\n\n.switch-view mat-radio-button ::ng-deep .mat-radio-container {\n      display: none; }\n\n.switch-view mat-radio-button ::ng-deep .mat-radio-label {\n      margin: 0; }\n\n.switch-view mat-radio-button ::ng-deep .mat-radio-label mat-icon {\n        text-align: center; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FuYWx5emUvdmlldy9hbmFseXplLXZpZXcuY29tcG9uZW50LnNjc3MiLCIvVXNlcnMvYmFybmFtdW10eWFuL1Byb2plY3RzL21vZHVzL3NpcC9zYXctd2ViL3NyYy90aGVtZXMvYmFzZS9fY29sb3JzLnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSxXQUFXLEVBQUE7O0FBR2I7RUFDRSxjQ0l1QjtFREh2QixpQkFBaUIsRUFBQTs7QUFHbkI7RUFDRSxjQUFjO0VBQ2QsVUFBVSxFQUFBOztBQUdaO0VBQ0UsNkJBQTZCLEVBQUE7O0FBRy9CO0VBQ0UsWUFBWTtFQUNaLGVBQWUsRUFBQTs7QUFHakI7RUFDRSxrQkFBa0I7RUFDbEIsb0JBQW9CLEVBQUE7O0FBRnRCO0lBS0ksU0FBUztJQUNULGtCQUFrQixFQUFBOztBQU50QjtNQVNNLGVBQWU7TUFDZixjQ2xCbUIsRUFBQTs7QURRekI7TUFlUSxjQ3RDaUIsRUFBQTs7QUR1QnpCO01Bb0JNLGFBQWEsRUFBQTs7QUFwQm5CO01Bd0JNLFNBQVMsRUFBQTs7QUF4QmY7UUEyQlEsa0JBQWtCLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9tb2R1bGVzL2FuYWx5emUvdmlldy9hbmFseXplLXZpZXcuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0IFwic3JjL3RoZW1lcy9iYXNlL2NvbG9yc1wiO1xuXG46aG9zdCB7XG4gIHdpZHRoOiAxMDAlO1xufVxuXG4uY2F0ZWdvcnktdGl0bGUge1xuICBjb2xvcjogJGdyZXktdGV4dC1jb2xvcjtcbiAgZm9udC13ZWlnaHQ6IGJvbGQ7XG59XG5cbi5hbmFseXNlcy1jb250YWluZXIge1xuICBtYXJnaW46IDAgMTBweDtcbiAgcGFkZGluZzogMDtcbn1cblxuLm1hdC10b29sYmFyLm1hdC10b29sYmFyLXNpbmdsZS1yb3cge1xuICBiYWNrZ3JvdW5kLWNvbG9yOiB0cmFuc3BhcmVudDtcbn1cblxuLnNlbGVjdC10eXBlIHtcbiAgd2lkdGg6IDE0MHB4O1xuICBmb250LXNpemU6IDE2cHg7XG59XG5cbi5zd2l0Y2gtdmlldyB7XG4gIG1hcmdpbi1yaWdodDogMjBweDtcbiAgZGlzcGxheTogaW5saW5lLWZsZXg7XG5cbiAgbWF0LXJhZGlvLWJ1dHRvbiB7XG4gICAgbWFyZ2luOiAwO1xuICAgIHBhZGRpbmctcmlnaHQ6IDdweDtcblxuICAgIG1hdC1pY29uIHtcbiAgICAgIGZvbnQtc2l6ZTogMTRweDtcbiAgICAgIGNvbG9yOiAkcHJpbWFyeS1ncmV5LWc0O1xuICAgIH1cblxuICAgICYubWF0LXJhZGlvLWNoZWNrZWQge1xuICAgICAgbWF0LWljb24ge1xuICAgICAgICBjb2xvcjogJHByaW1hcnktYmx1ZS1iMjtcbiAgICAgIH1cbiAgICB9XG5cbiAgICA6Om5nLWRlZXAgLm1hdC1yYWRpby1jb250YWluZXIge1xuICAgICAgZGlzcGxheTogbm9uZTtcbiAgICB9XG5cbiAgICA6Om5nLWRlZXAgLm1hdC1yYWRpby1sYWJlbCB7XG4gICAgICBtYXJnaW46IDA7XG5cbiAgICAgIG1hdC1pY29uIHtcbiAgICAgICAgdGV4dC1hbGlnbjogY2VudGVyO1xuICAgICAgfVxuICAgIH1cbiAgfVxufVxuIiwiLy8gQnJhbmRpbmcgY29sb3JzXG4kcHJpbWFyeS1ibHVlLWIxOiAjMWE4OWQ0O1xuJHByaW1hcnktYmx1ZS1iMjogIzAwNzdiZTtcbiRwcmltYXJ5LWJsdWUtYjM6ICMyMDZiY2U7XG4kcHJpbWFyeS1ibHVlLWI0OiAjMWQzYWIyO1xuXG4kcHJpbWFyeS1ob3Zlci1ibHVlOiAjMWQ2MWIxO1xuJGdyaWQtaG92ZXItY29sb3I6ICNmNWY5ZmM7XG4kZ3JpZC1oZWFkZXItYmctY29sb3I6ICNkN2VhZmE7XG4kZ3JpZC1oZWFkZXItY29sb3I6ICMwYjRkOTk7XG4kZ3JpZC10ZXh0LWNvbG9yOiAjNDY0NjQ2O1xuJGdyZXktdGV4dC1jb2xvcjogIzYzNjM2MztcblxuJHNlbGVjdGlvbi1oaWdobGlnaHQtY29sOiByZ2JhKDAsIDE0MCwgMjYwLCAwLjIpO1xuJHByaW1hcnktZ3JleS1nMTogI2QxZDNkMztcbiRwcmltYXJ5LWdyZXktZzI6ICM5OTk7XG4kcHJpbWFyeS1ncmV5LWczOiAjNzM3MzczO1xuJHByaW1hcnktZ3JleS1nNDogIzVjNjY3MDtcbiRwcmltYXJ5LWdyZXktZzU6ICMzMTMxMzE7XG4kcHJpbWFyeS1ncmV5LWc2OiAjZjVmNWY1O1xuJHByaW1hcnktZ3JleS1nNzogIzNkM2QzZDtcblxuJHByaW1hcnktd2hpdGU6ICNmZmY7XG4kcHJpbWFyeS1ibGFjazogIzAwMDtcbiRwcmltYXJ5LXJlZDogI2FiMGUyNztcbiRwcmltYXJ5LWdyZWVuOiAjNzNiNDIxO1xuJHByaW1hcnktb3JhbmdlOiAjZjA3NjAxO1xuXG4kc2Vjb25kYXJ5LWdyZWVuOiAjNmZiMzIwO1xuJHNlY29uZGFyeS15ZWxsb3c6ICNmZmJlMDA7XG4kc2Vjb25kYXJ5LW9yYW5nZTogI2ZmOTAwMDtcbiRzZWNvbmRhcnktcmVkOiAjZDkzZTAwO1xuJHNlY29uZGFyeS1iZXJyeTogI2FjMTQ1YTtcbiRzZWNvbmRhcnktcHVycGxlOiAjOTE0MTkxO1xuXG4kc3RyaW5nLXR5cGUtY29sb3I6ICM0OTk1YjI7XG4kbnVtYmVyLXR5cGUtY29sb3I6ICMwMGIxODA7XG4kZ2VvLXR5cGUtY29sb3I6ICM4NDVlYzI7XG4kZGF0ZS10eXBlLWNvbG9yOiAjZDE5NjIxO1xuXG4kdHlwZS1jaGlwLW9wYWNpdHk6IDE7XG4kc3RyaW5nLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkc3RyaW5nLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kbnVtYmVyLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkbnVtYmVyLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZ2VvLXR5cGUtY2hpcC1jb2xvcjogcmdiYSgkZ2VvLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG4kZGF0ZS10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGRhdGUtdHlwZS1jb2xvciwgJHR5cGUtY2hpcC1vcGFjaXR5KTtcblxuJHJlcG9ydC1kZXNpZ25lci1zZXR0aW5ncy1iZy1jb2xvcjogI2Y1ZjlmYztcbiRiYWNrZ3JvdW5kLWNvbG9yOiAjZjVmOWZjO1xuIl19 */"

/***/ }),

/***/ "./src/app/modules/analyze/view/analyze-view.component.ts":
/*!****************************************************************!*\
  !*** ./src/app/modules/analyze/view/analyze-view.component.ts ***!
  \****************************************************************/
/*! exports provided: AnalyzeViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnalyzeViewComponent", function() { return AnalyzeViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_router__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/router */ "./node_modules/@angular/router/fesm5/router.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var angular_2_local_storage__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! angular-2-local-storage */ "./node_modules/angular-2-local-storage/dist/index.js");
/* harmony import */ var angular_2_local_storage__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(angular_2_local_storage__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_findIndex__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/findIndex */ "./node_modules/lodash/findIndex.js");
/* harmony import */ var lodash_findIndex__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_findIndex__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var lodash_reduce__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/reduce */ "./node_modules/lodash/reduce.js");
/* harmony import */ var lodash_reduce__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_reduce__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var _services_analyze_service__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../services/analyze.service */ "./src/app/modules/analyze/services/analyze.service.ts");
/* harmony import */ var _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../../../common/services/toastMessage.service */ "./src/app/common/services/toastMessage.service.ts");
/* harmony import */ var _common_services_local_search_service__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! ../../../common/services/local-search.service */ "./src/app/common/services/local-search.service.ts");
/* harmony import */ var _new_dialog__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! ./new-dialog */ "./src/app/modules/analyze/view/new-dialog/index.ts");
/* harmony import */ var _services_execute_service__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! ../services/execute.service */ "./src/app/modules/analyze/services/execute.service.ts");
/* harmony import */ var _designer_types__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ../designer/types */ "./src/app/modules/analyze/designer/types.ts");














var VIEW_KEY = 'analyseReportView';
var SEARCH_CONFIG = [
    { keyword: 'NAME', fieldName: 'name' },
    { keyword: 'TYPE', fieldName: 'type' },
    { keyword: 'CREATOR', fieldName: 'userFullName' },
    {
        keyword: 'CREATED',
        fieldName: 'new Date(rowData.createdTimestamp).toDateString()'
    },
    { keyword: 'METRIC', fieldName: 'metricName' }
];
var AnalyzeViewComponent = /** @class */ (function () {
    function AnalyzeViewComponent(_analyzeService, _router, _route, _localStorage, _jwt, _localSearch, _toastMessage, _dialog, _executeService) {
        this._analyzeService = _analyzeService;
        this._router = _router;
        this._route = _route;
        this._localStorage = _localStorage;
        this._jwt = _jwt;
        this._localSearch = _localSearch;
        this._toastMessage = _toastMessage;
        this._dialog = _dialog;
        this._executeService = _executeService;
        this.analyses = [];
        this.LIST_VIEW = 'list';
        this.CARD_VIEW = 'card';
        this.viewMode = this.LIST_VIEW;
        this.privileges = {
            create: false
        };
        this.analysisTypes = [
            ['all', 'All'],
            ['chart', 'Chart'],
            ['report', 'Report'],
            ['pivot', 'Pivot'],
            ['scheduled', 'Scheduled']
        ].map(function (_a) {
            var value = _a[0], label = _a[1];
            return ({ value: value, label: label });
        });
        this.filterObj = {
            analysisType: this.analysisTypes[0].value,
            searchTerm: '',
            searchTermValue: ''
        };
    }
    AnalyzeViewComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.semanticLayerData$ = this._analyzeService.getSemanticLayerData();
        this.semanticLayerData$.then(function (data) {
            console.log('semantic', data);
            _this.semanticLayerDataMap = lodash_reduce__WEBPACK_IMPORTED_MODULE_6__(data, function (accumulator, metric) {
                accumulator[metric.id] = metric;
                return accumulator;
            }, {});
        });
        this._route.params.subscribe(function (params) {
            _this.onParamsChange(params);
        });
        var savedView = this._localStorage.get(VIEW_KEY);
        this.viewMode = [this.LIST_VIEW, this.CARD_VIEW].includes(savedView)
            ? savedView
            : this.LIST_VIEW;
    };
    AnalyzeViewComponent.prototype.onParamsChange = function (params) {
        this.analysisId = params.id;
        this.categoryName = this._analyzeService
            .getCategory(this.analysisId)
            .then(function (category) { return category.name; });
        this.loadAnalyses(this.analysisId);
        this.getCronJobs(this.analysisId);
    };
    AnalyzeViewComponent.prototype.onAction = function (event) {
        var _this = this;
        switch (event.action) {
            case 'fork': {
                var analysis_1 = event.analysis, requestExecution_1 = event.requestExecution;
                if (analysis_1) {
                    this.loadAnalyses(this.analysisId).then(function () {
                        if (requestExecution_1) {
                            _this._executeService.executeAnalysis(analysis_1, _services_analyze_service__WEBPACK_IMPORTED_MODULE_8__["EXECUTION_MODES"].PUBLISH);
                        }
                    });
                }
                break;
            }
            case 'edit': {
                var analysis = event.analysis, requestExecution = event.requestExecution;
                if (analysis) {
                    this.spliceAnalyses(analysis, true);
                }
                if (requestExecution) {
                    this._executeService.executeAnalysis(analysis, _services_analyze_service__WEBPACK_IMPORTED_MODULE_8__["EXECUTION_MODES"].PUBLISH);
                }
                break;
            }
            case 'delete':
                this.spliceAnalyses(event.analysis, false);
                break;
            case 'execute':
                if (event.analysis) {
                    this.goToAnalysis(event.analysis);
                }
                break;
            case 'publish':
                this.afterPublish(event.analysis);
                this.spliceAnalyses(event.analysis, true);
                break;
        }
    };
    AnalyzeViewComponent.prototype.onViewChange = function (view) {
        this.viewMode = view;
        this._localStorage.set(VIEW_KEY, view);
    };
    AnalyzeViewComponent.prototype.onAnalysisTypeChange = function (type) {
        this.filterObj.analysisType = type;
    };
    AnalyzeViewComponent.prototype.goToAnalysis = function (analysis) {
        var isDSL = analysis.sipQuery ? true : false;
        this._router.navigate(['analyze', 'analysis', analysis.id, 'executed'], {
            queryParams: {
                isDSL: isDSL
            }
        });
    };
    AnalyzeViewComponent.prototype.afterPublish = function (analysis) {
        this.getCronJobs(this.analysisId);
        /* Update the new analysis in the current list */
        this._router.navigate([
            'analyze',
            Object(_designer_types__WEBPACK_IMPORTED_MODULE_13__["isDSLAnalysis"])(analysis) ? analysis.category : analysis.categoryId
        ]);
    };
    AnalyzeViewComponent.prototype.spliceAnalyses = function (analysis, replace) {
        var index = lodash_findIndex__WEBPACK_IMPORTED_MODULE_5__(this.analyses, function (_a) {
            var id = _a.id;
            return id === analysis.id;
        });
        var filteredIndex = lodash_findIndex__WEBPACK_IMPORTED_MODULE_5__(this.filteredAnalyses, function (_a) {
            var id = _a.id;
            return id === analysis.id;
        });
        if (replace) {
            this.analyses.splice(index, 1, analysis);
            this.filteredAnalyses.splice(filteredIndex, 1, analysis);
        }
        else {
            this.analyses.splice(index, 1);
            this.filteredAnalyses.splice(filteredIndex, 1);
        }
    };
    AnalyzeViewComponent.prototype.openNewAnalysisModal = function () {
        var _this = this;
        this.semanticLayerData$.then(function (metrics) {
            _this._dialog
                .open(_new_dialog__WEBPACK_IMPORTED_MODULE_11__["AnalyzeNewDialogComponent"], {
                width: 'auto',
                height: 'auto',
                autoFocus: false,
                data: {
                    metrics: metrics,
                    id: _this.analysisId
                }
            })
                .afterClosed()
                .subscribe(function (event) {
                if (!event) {
                    return;
                }
                var analysis = event.analysis, requestExecution = event.requestExecution;
                if (analysis) {
                    _this.loadAnalyses(_this.analysisId).then(function () {
                        if (requestExecution) {
                            _this._executeService.executeAnalysis(analysis, _services_analyze_service__WEBPACK_IMPORTED_MODULE_8__["EXECUTION_MODES"].PUBLISH);
                        }
                    });
                }
            });
        });
    };
    AnalyzeViewComponent.prototype.loadAnalyses = function (analysisId) {
        var _this = this;
        return this._analyzeService.getAnalysesFor(analysisId).then(function (analyses) {
            _this.analyses = analyses;
            _this.filteredAnalyses = analyses.slice();
        });
    };
    AnalyzeViewComponent.prototype.getCronJobs = function (analysisId) {
        var _this = this;
        var token = this._jwt.getTokenObj();
        var requestModel = {
            categoryId: analysisId,
            groupkey: token.ticket.custCode
        };
        this._analyzeService
            .getAllCronJobs(requestModel)
            .then(function (response) {
            if (response.statusCode === 200) {
                _this.cronJobs = lodash_reduce__WEBPACK_IMPORTED_MODULE_6__(response.data, function (accumulator, cron) {
                    var analysisID = cron.jobDetails.analysisID;
                    accumulator[analysisID] = cron;
                    return accumulator;
                }, {});
            }
            else {
                _this.cronJobs = {};
            }
        })
            .catch(function (err) {
            _this._toastMessage.error(err.message);
        });
    };
    AnalyzeViewComponent.prototype.applySearchFilter = function (value) {
        var _this = this;
        this.filterObj.searchTerm = value;
        var searchCriteria = this._localSearch.parseSearchTerm(this.filterObj.searchTerm);
        this.filterObj.searchTermValue = searchCriteria.trimmedTerm;
        this._localSearch
            .doSearch(searchCriteria, this.analyses, SEARCH_CONFIG)
            .then(function (data) {
            _this.filteredAnalyses = data;
        }, function (err) {
            _this._toastMessage.error(err.message);
        });
    };
    AnalyzeViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'analyze-view-u',
            template: __webpack_require__(/*! ./analyze-view.component.html */ "./src/app/modules/analyze/view/analyze-view.component.html"),
            styles: [__webpack_require__(/*! ./analyze-view.component.scss */ "./src/app/modules/analyze/view/analyze-view.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_services_analyze_service__WEBPACK_IMPORTED_MODULE_8__["AnalyzeService"],
            _angular_router__WEBPACK_IMPORTED_MODULE_2__["Router"],
            _angular_router__WEBPACK_IMPORTED_MODULE_2__["ActivatedRoute"],
            angular_2_local_storage__WEBPACK_IMPORTED_MODULE_4__["LocalStorageService"],
            _common_services__WEBPACK_IMPORTED_MODULE_7__["JwtService"],
            _common_services_local_search_service__WEBPACK_IMPORTED_MODULE_10__["LocalSearchService"],
            _common_services_toastMessage_service__WEBPACK_IMPORTED_MODULE_9__["ToastService"],
            _angular_material__WEBPACK_IMPORTED_MODULE_3__["MatDialog"],
            _services_execute_service__WEBPACK_IMPORTED_MODULE_12__["ExecuteService"]])
    ], AnalyzeViewComponent);
    return AnalyzeViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/analyze/view/card-view/analyze-card-view.component.html":
/*!*********************************************************************************!*\
  !*** ./src/app/modules/analyze/view/card-view/analyze-card-view.component.html ***!
  \*********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div fxLayout=\"row wrap\"\n     fxLayoutAlign=\"start start\"\n>\n  <div *ngFor=\"let analysis of analyses\"\n       fxFlex.md=\"50\"\n       fxFlex.lt-md=\"100\"\n       fxFlex.gt-md=\"33\"\n  >\n    <analyze-card (action)=\"action.emit($event)\"\n                    [highlightTerm]=\"highlightTerm\"\n                    [analysis]=\"analysis\"\n                    [cronJobs]=\"cronJobs\"\n    ></analyze-card>\n  </div>\n\n  <div class=\"analyze-card-view__no-results\"\n        *ngIf=\"highlightTerm && !analyses.length\">\n    <span i18n>No matching results</span>\n  </div>\n  <div class=\"analyze-card-view__no-results\"\n      *ngIf=\"!highlightTerm && analysisType !== 'all' && analyses && !analyses.length\">\n    <span i18n>There are no Analyses of selected type.</span>\n  </div>\n  <div class=\"analyze-card-view__no-results\"\n      *ngIf=\"!highlightTerm && analysisType === 'all' && analyses && !analyses.length\">\n    <span i18n>There are no Analyses for this category.</span>\n  </div>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/analyze/view/card-view/analyze-card-view.component.scss":
/*!*********************************************************************************!*\
  !*** ./src/app/modules/analyze/view/card-view/analyze-card-view.component.scss ***!
  \*********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  display: block;\n  height: calc(100vh - 74px - 60px - 64px);\n  overflow: auto;\n  background-color: whitesmoke; }\n\n.analyze-card-view__no-results {\n  width: 100%;\n  color: #B2B2B2;\n  text-align: center; }\n\n.analyze-card-view__no-results span {\n    font-size: 28px;\n    word-break: break-word; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FuYWx5emUvdmlldy9jYXJkLXZpZXcvYW5hbHl6ZS1jYXJkLXZpZXcuY29tcG9uZW50LnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBQUE7RUFDRSxjQUFjO0VBQ2Qsd0NBQXdDO0VBQ3hDLGNBQWM7RUFDZCw0QkFBNEIsRUFBQTs7QUFHOUI7RUFDRSxXQUFXO0VBQ1gsY0FBYztFQUNkLGtCQUFrQixFQUFBOztBQUhwQjtJQU1JLGVBQWU7SUFDZixzQkFBc0IsRUFBQSIsImZpbGUiOiJzcmMvYXBwL21vZHVsZXMvYW5hbHl6ZS92aWV3L2NhcmQtdmlldy9hbmFseXplLWNhcmQtdmlldy5jb21wb25lbnQuc2NzcyIsInNvdXJjZXNDb250ZW50IjpbIjpob3N0IHtcbiAgZGlzcGxheTogYmxvY2s7XG4gIGhlaWdodDogY2FsYygxMDB2aCAtIDc0cHggLSA2MHB4IC0gNjRweCk7XG4gIG92ZXJmbG93OiBhdXRvO1xuICBiYWNrZ3JvdW5kLWNvbG9yOiB3aGl0ZXNtb2tlO1xufVxuXG4uYW5hbHl6ZS1jYXJkLXZpZXdfX25vLXJlc3VsdHMge1xuICB3aWR0aDogMTAwJTtcbiAgY29sb3I6ICNCMkIyQjI7XG4gIHRleHQtYWxpZ246IGNlbnRlcjtcblxuICBzcGFuIHtcbiAgICBmb250LXNpemU6IDI4cHg7XG4gICAgd29yZC1icmVhazogYnJlYWstd29yZDtcbiAgfVxufVxuIl19 */"

/***/ }),

/***/ "./src/app/modules/analyze/view/card-view/analyze-card-view.component.ts":
/*!*******************************************************************************!*\
  !*** ./src/app/modules/analyze/view/card-view/analyze-card-view.component.ts ***!
  \*******************************************************************************/
/*! exports provided: AnalyzeCardViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnalyzeCardViewComponent", function() { return AnalyzeCardViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");


var AnalyzeCardViewComponent = /** @class */ (function () {
    function AnalyzeCardViewComponent() {
        this.action = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
    }
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AnalyzeCardViewComponent.prototype, "action", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array)
    ], AnalyzeCardViewComponent.prototype, "analyses", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", String)
    ], AnalyzeCardViewComponent.prototype, "analysisType", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", String)
    ], AnalyzeCardViewComponent.prototype, "highlightTerm", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], AnalyzeCardViewComponent.prototype, "cronJobs", void 0);
    AnalyzeCardViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'analyze-card-view',
            template: __webpack_require__(/*! ./analyze-card-view.component.html */ "./src/app/modules/analyze/view/card-view/analyze-card-view.component.html"),
            styles: [__webpack_require__(/*! ./analyze-card-view.component.scss */ "./src/app/modules/analyze/view/card-view/analyze-card-view.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [])
    ], AnalyzeCardViewComponent);
    return AnalyzeCardViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/analyze/view/card-view/index.ts":
/*!*********************************************************!*\
  !*** ./src/app/modules/analyze/view/card-view/index.ts ***!
  \*********************************************************/
/*! exports provided: AnalyzeCardViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _analyze_card_view_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./analyze-card-view.component */ "./src/app/modules/analyze/view/card-view/analyze-card-view.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AnalyzeCardViewComponent", function() { return _analyze_card_view_component__WEBPACK_IMPORTED_MODULE_0__["AnalyzeCardViewComponent"]; });




/***/ }),

/***/ "./src/app/modules/analyze/view/card/analyze-card.component.html":
/*!***********************************************************************!*\
  !*** ./src/app/modules/analyze/view/card/analyze-card.component.html ***!
  \***********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<mat-card e2e=\"analysis-card\">\n  <mat-card-header fxLayout=\"row\" fxLayoutAlign=\"end center\">\n    <mat-chip\n      class=\"tag-chip status-chip mat-basic-chip\"\n      *ngIf=\"isExecuting\"\n      i18n\n    >\n      EXECUTING\n    </mat-chip>\n    <mat-chip\n      class=\"tag-chip mat-basic-chip\"\n      [innerHtml]=\"getType(analysis.type) | highlight: highlightTerm\"\n    ></mat-chip>\n  </mat-card-header>\n\n  <mat-card-content\n    fxLayout=\"row\"\n    fxLayoutAlign=\"center center\"\n    [attr.e2e]=\"typeIdentifier\"\n  >\n    <div\n      class=\"saw-ac-placeholder\"\n      [ngClass]=\"placeholderClass\"\n      [queryParams]=\"{ isDSL: !!analysis.sipQuery }\"\n      routerLink=\"../analysis/{{ analysis.id }}/executed\"\n    ></div>\n  </mat-card-content>\n  <mat-divider></mat-divider>\n  <mat-card-footer fxLayout=\"row\" fxLayoutAlign=\"space-between start\">\n    <div>\n      <mat-card-title>\n        <a\n          [innerHtml]=\"analysis.name | highlight: highlightTerm\"\n          routerLink=\"../analysis/{{ analysis.id }}/executed\"\n          [queryParams]=\"{ isDSL: !!analysis.sipQuery }\"\n          e2e=\"analysis-name\"\n        ></a>\n      </mat-card-title>\n      <mat-card-subtitle>\n        <span\n          [innerHtml]=\"\n            (analysis.metrics || []).join(', ') | highlight: highlightTerm\n          \"\n        ></span>\n        <span>\n          <mat-icon fontIcon=\"icon-calendar\"></mat-icon>\n          {{ schedule || 'No Schedule Set' }}\n        </span>\n      </mat-card-subtitle>\n    </div>\n    <mat-card-actions fxLayout=\"row\" fxLayoutAlign=\"center center\">\n      <button\n        mat-icon-button\n        (click)=\"fork(analysis)\"\n        e2e=\"action-fork-btn\"\n        *ngIf=\"canUserFork\"\n        class=\"list-action-button\"\n        i18n-matTooltip=\"Fork Analysis\"\n        matTooltip=\"Fork Analysis\"\n      >\n        <mat-icon fontIcon=\"icon-fork\"></mat-icon>\n      </button>\n      <analyze-actions-menu-u\n        [analysis]=\"analysis\"\n        exclude=\"fork-export-details\"\n        [actionsToDisable]=\"isExecuting ? 'execute' : ''\"\n        (afterDelete)=\"afterDelete($event)\"\n        (afterExecute)=\"afterExecute($event)\"\n        (afterPublish)=\"afterPublish($event)\"\n        (afterEdit)=\"afterEdit($event)\"\n      ></analyze-actions-menu-u>\n    </mat-card-actions>\n  </mat-card-footer>\n</mat-card>\n"

/***/ }),

/***/ "./src/app/modules/analyze/view/card/analyze-card.component.scss":
/*!***********************************************************************!*\
  !*** ./src/app/modules/analyze/view/card/analyze-card.component.scss ***!
  \***********************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  display: block;\n  margin: 5px; }\n\n.saw-ac-placeholder {\n  height: 159px;\n  width: 434px;\n  cursor: pointer;\n  background-size: contain;\n  background-repeat: no-repeat; }\n\n.saw-ac-placeholder.m-report {\n    background-image: url(\"/assets/img/charts/report.png\"); }\n\n.saw-ac-placeholder.m-esReport {\n    background-image: url(\"/assets/img/charts/report.png\"); }\n\n.saw-ac-placeholder.m-pivot {\n    background-image: url(\"/assets/img/charts/pivot.png\"); }\n\n.saw-ac-placeholder.m-chart-bubble {\n    background-image: url(\"/assets/img/charts/chart-bubble.png\"); }\n\n.saw-ac-placeholder.m-chart-column {\n    background-image: url(\"/assets/img/charts/chart-column.png\"); }\n\n.saw-ac-placeholder.m-chart-area {\n    background-image: url(\"/assets/img/charts/chart-area.svg\"); }\n\n.saw-ac-placeholder.m-chart-combo {\n    background-image: url(\"/assets/img/charts/chart-combo.svg\"); }\n\n.saw-ac-placeholder.m-chart-bar {\n    background-image: url(\"/assets/img/charts/chart-bar.png\"); }\n\n.saw-ac-placeholder.m-chart-donut {\n    background-image: url(\"/assets/img/charts/chart-donut.png\"); }\n\n.saw-ac-placeholder.m-chart-line {\n    background-image: url(\"/assets/img/charts/chart-line.png\"); }\n\n.saw-ac-placeholder.m-chart-pie {\n    background-image: url(\"/assets/img/charts/chart-pie.png\"); }\n\n.saw-ac-placeholder.m-chart-scatter {\n    background-image: url(\"/assets/img/charts/chart-scatter.png\"); }\n\n.saw-ac-placeholder.m-chart-stack {\n    background-image: url(\"/assets/img/charts/chart-stack.png\"); }\n\n.saw-ac-placeholder.m-chart-geo {\n    background-image: url(\"/assets/img/charts/chart-geo-location.png\"); }\n\n.saw-ac-placeholder.m-chart-tsspline {\n    background-image: url(\"/assets/img/charts/timeseries-chart.png\"); }\n\n.saw-ac-placeholder.m-chart-tsPane {\n    background-image: url(\"/assets/img/charts/timeseries-multi-chart.png\"); }\n\n.saw-ac-placeholder.m-map-map {\n    background-image: url(\"/assets/img/charts/chart-geo-location.png\"); }\n\n.saw-ac-placeholder.m-map-chart_scale {\n    background-image: url(\"/assets/img/charts/chart-geo-location.png\"); }\n\nmat-card-title.mat-card-title {\n  padding: 0;\n  margin: 0; }\n\nmat-card-title.mat-card-title > a {\n    font-size: 18px;\n    font-weight: 700; }\n\nmat-card-subtitle.mat-card-subtitle {\n  padding-top: 0 !important;\n  margin: 0; }\n\nmat-card-subtitle.mat-card-subtitle > span {\n    font-size: 12px; }\n\nmat-card-footer.mat-card-footer {\n  padding: 0 20px; }\n\n.mat-button-wrapper > mat-icon {\n  color: #5c6670; }\n\nmat-chip.tag-chip {\n  color: #5c6670; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FuYWx5emUvdmlldy9jYXJkL2FuYWx5emUtY2FyZC5jb21wb25lbnQuc2NzcyIsIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL3RoZW1lcy9iYXNlL19jb2xvcnMuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUNFLGNBQWM7RUFDZCxXQUFXLEVBQUE7O0FBR2I7RUFDRSxhQUFhO0VBQ2IsWUFBWTtFQUNaLGVBQWU7RUFDZix3QkFBd0I7RUFDeEIsNEJBQTRCLEVBQUE7O0FBTDlCO0lBUUksc0RBQXNELEVBQUE7O0FBUjFEO0lBWUksc0RBQXNELEVBQUE7O0FBWjFEO0lBZ0JJLHFEQUFxRCxFQUFBOztBQWhCekQ7SUFvQkksNERBQTRELEVBQUE7O0FBcEJoRTtJQXdCSSw0REFBNEQsRUFBQTs7QUF4QmhFO0lBNEJJLDBEQUEwRCxFQUFBOztBQTVCOUQ7SUFnQ0ksMkRBQTJELEVBQUE7O0FBaEMvRDtJQW9DSSx5REFBeUQsRUFBQTs7QUFwQzdEO0lBd0NJLDJEQUEyRCxFQUFBOztBQXhDL0Q7SUE0Q0ksMERBQTBELEVBQUE7O0FBNUM5RDtJQWdESSx5REFBeUQsRUFBQTs7QUFoRDdEO0lBb0RJLDZEQUE2RCxFQUFBOztBQXBEakU7SUF3REksMkRBQTJELEVBQUE7O0FBeEQvRDtJQTRESSxrRUFBa0UsRUFBQTs7QUE1RHRFO0lBZ0VJLGdFQUFnRSxFQUFBOztBQWhFcEU7SUFvRUksc0VBQXNFLEVBQUE7O0FBcEUxRTtJQXdFSSxrRUFBa0UsRUFBQTs7QUF4RXRFO0lBNEVJLGtFQUFrRSxFQUFBOztBQUl0RTtFQUNFLFVBQVU7RUFDVixTQUFTLEVBQUE7O0FBRlg7SUFLSSxlQUFlO0lBQ2YsZ0JBQWdCLEVBQUE7O0FBSXBCO0VBQ0UseUJBQXlCO0VBQ3pCLFNBQVMsRUFBQTs7QUFGWDtJQUtJLGVBQWUsRUFBQTs7QUFJbkI7RUFDRSxlQUFlLEVBQUE7O0FBR2pCO0VBQ0UsY0M5RnVCLEVBQUE7O0FEaUd6QjtFQUNFLGNDbEd1QixFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy9hbmFseXplL3ZpZXcvY2FyZC9hbmFseXplLWNhcmQuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0IFwic3JjL3RoZW1lcy9iYXNlL2NvbG9yc1wiO1xuXG46aG9zdCB7XG4gIGRpc3BsYXk6IGJsb2NrO1xuICBtYXJnaW46IDVweDtcbn1cblxuLnNhdy1hYy1wbGFjZWhvbGRlciB7XG4gIGhlaWdodDogMTU5cHg7XG4gIHdpZHRoOiA0MzRweDtcbiAgY3Vyc29yOiBwb2ludGVyO1xuICBiYWNrZ3JvdW5kLXNpemU6IGNvbnRhaW47XG4gIGJhY2tncm91bmQtcmVwZWF0OiBuby1yZXBlYXQ7XG5cbiAgJi5tLXJlcG9ydCB7XG4gICAgYmFja2dyb3VuZC1pbWFnZTogdXJsKCcvYXNzZXRzL2ltZy9jaGFydHMvcmVwb3J0LnBuZycpO1xuICB9XG5cbiAgJi5tLWVzUmVwb3J0IHtcbiAgICBiYWNrZ3JvdW5kLWltYWdlOiB1cmwoJy9hc3NldHMvaW1nL2NoYXJ0cy9yZXBvcnQucG5nJyk7XG4gIH1cblxuICAmLm0tcGl2b3Qge1xuICAgIGJhY2tncm91bmQtaW1hZ2U6IHVybCgnL2Fzc2V0cy9pbWcvY2hhcnRzL3Bpdm90LnBuZycpO1xuICB9XG5cbiAgJi5tLWNoYXJ0LWJ1YmJsZSB7XG4gICAgYmFja2dyb3VuZC1pbWFnZTogdXJsKCcvYXNzZXRzL2ltZy9jaGFydHMvY2hhcnQtYnViYmxlLnBuZycpO1xuICB9XG5cbiAgJi5tLWNoYXJ0LWNvbHVtbiB7XG4gICAgYmFja2dyb3VuZC1pbWFnZTogdXJsKCcvYXNzZXRzL2ltZy9jaGFydHMvY2hhcnQtY29sdW1uLnBuZycpO1xuICB9XG5cbiAgJi5tLWNoYXJ0LWFyZWEge1xuICAgIGJhY2tncm91bmQtaW1hZ2U6IHVybCgnL2Fzc2V0cy9pbWcvY2hhcnRzL2NoYXJ0LWFyZWEuc3ZnJyk7XG4gIH1cblxuICAmLm0tY2hhcnQtY29tYm8ge1xuICAgIGJhY2tncm91bmQtaW1hZ2U6IHVybCgnL2Fzc2V0cy9pbWcvY2hhcnRzL2NoYXJ0LWNvbWJvLnN2ZycpO1xuICB9XG5cbiAgJi5tLWNoYXJ0LWJhciB7XG4gICAgYmFja2dyb3VuZC1pbWFnZTogdXJsKCcvYXNzZXRzL2ltZy9jaGFydHMvY2hhcnQtYmFyLnBuZycpO1xuICB9XG5cbiAgJi5tLWNoYXJ0LWRvbnV0IHtcbiAgICBiYWNrZ3JvdW5kLWltYWdlOiB1cmwoJy9hc3NldHMvaW1nL2NoYXJ0cy9jaGFydC1kb251dC5wbmcnKTtcbiAgfVxuXG4gICYubS1jaGFydC1saW5lIHtcbiAgICBiYWNrZ3JvdW5kLWltYWdlOiB1cmwoJy9hc3NldHMvaW1nL2NoYXJ0cy9jaGFydC1saW5lLnBuZycpO1xuICB9XG5cbiAgJi5tLWNoYXJ0LXBpZSB7XG4gICAgYmFja2dyb3VuZC1pbWFnZTogdXJsKCcvYXNzZXRzL2ltZy9jaGFydHMvY2hhcnQtcGllLnBuZycpO1xuICB9XG5cbiAgJi5tLWNoYXJ0LXNjYXR0ZXIge1xuICAgIGJhY2tncm91bmQtaW1hZ2U6IHVybCgnL2Fzc2V0cy9pbWcvY2hhcnRzL2NoYXJ0LXNjYXR0ZXIucG5nJyk7XG4gIH1cblxuICAmLm0tY2hhcnQtc3RhY2sge1xuICAgIGJhY2tncm91bmQtaW1hZ2U6IHVybCgnL2Fzc2V0cy9pbWcvY2hhcnRzL2NoYXJ0LXN0YWNrLnBuZycpO1xuICB9XG5cbiAgJi5tLWNoYXJ0LWdlbyB7XG4gICAgYmFja2dyb3VuZC1pbWFnZTogdXJsKCcvYXNzZXRzL2ltZy9jaGFydHMvY2hhcnQtZ2VvLWxvY2F0aW9uLnBuZycpO1xuICB9XG5cbiAgJi5tLWNoYXJ0LXRzc3BsaW5lIHtcbiAgICBiYWNrZ3JvdW5kLWltYWdlOiB1cmwoJy9hc3NldHMvaW1nL2NoYXJ0cy90aW1lc2VyaWVzLWNoYXJ0LnBuZycpO1xuICB9XG5cbiAgJi5tLWNoYXJ0LXRzUGFuZSB7XG4gICAgYmFja2dyb3VuZC1pbWFnZTogdXJsKCcvYXNzZXRzL2ltZy9jaGFydHMvdGltZXNlcmllcy1tdWx0aS1jaGFydC5wbmcnKTtcbiAgfVxuXG4gICYubS1tYXAtbWFwIHtcbiAgICBiYWNrZ3JvdW5kLWltYWdlOiB1cmwoJy9hc3NldHMvaW1nL2NoYXJ0cy9jaGFydC1nZW8tbG9jYXRpb24ucG5nJyk7XG4gIH1cblxuICAmLm0tbWFwLWNoYXJ0X3NjYWxlIHtcbiAgICBiYWNrZ3JvdW5kLWltYWdlOiB1cmwoJy9hc3NldHMvaW1nL2NoYXJ0cy9jaGFydC1nZW8tbG9jYXRpb24ucG5nJyk7XG4gIH1cbn1cblxubWF0LWNhcmQtdGl0bGUubWF0LWNhcmQtdGl0bGUge1xuICBwYWRkaW5nOiAwO1xuICBtYXJnaW46IDA7XG5cbiAgPiBhIHtcbiAgICBmb250LXNpemU6IDE4cHg7XG4gICAgZm9udC13ZWlnaHQ6IDcwMDtcbiAgfVxufVxuXG5tYXQtY2FyZC1zdWJ0aXRsZS5tYXQtY2FyZC1zdWJ0aXRsZSB7XG4gIHBhZGRpbmctdG9wOiAwICFpbXBvcnRhbnQ7XG4gIG1hcmdpbjogMDtcblxuICA+IHNwYW4ge1xuICAgIGZvbnQtc2l6ZTogMTJweDtcbiAgfVxufVxuXG5tYXQtY2FyZC1mb290ZXIubWF0LWNhcmQtZm9vdGVyIHtcbiAgcGFkZGluZzogMCAyMHB4O1xufVxuXG4ubWF0LWJ1dHRvbi13cmFwcGVyID4gbWF0LWljb24ge1xuICBjb2xvcjogJHByaW1hcnktZ3JleS1nNDtcbn1cblxubWF0LWNoaXAudGFnLWNoaXAge1xuICBjb2xvcjogJHByaW1hcnktZ3JleS1nNDtcbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/analyze/view/card/analyze-card.component.ts":
/*!*********************************************************************!*\
  !*** ./src/app/modules/analyze/view/card/analyze-card.component.ts ***!
  \*********************************************************************/
/*! exports provided: AnalyzeCardComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnalyzeCardComponent", function() { return AnalyzeCardComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _actions__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../actions */ "./src/app/modules/analyze/actions/index.ts");
/* harmony import */ var _services_execute_service__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../../services/execute.service */ "./src/app/modules/analyze/services/execute.service.ts");
/* harmony import */ var _designer_types__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ../../designer/types */ "./src/app/modules/analyze/designer/types.ts");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../../common/services */ "./src/app/common/services/index.ts");
/* harmony import */ var _common_utils_cron2Readable__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../../../common/utils/cron2Readable */ "./src/app/common/utils/cron2Readable.ts");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! lodash/isUndefined */ "./node_modules/lodash/isUndefined.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(lodash_isUndefined__WEBPACK_IMPORTED_MODULE_7__);








var AnalyzeCardComponent = /** @class */ (function () {
    function AnalyzeCardComponent(_analyzeActionsService, _jwt, _executeService) {
        this._analyzeActionsService = _analyzeActionsService;
        this._jwt = _jwt;
        this._executeService = _executeService;
        this.action = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.canUserFork = false;
        this.isExecuting = false;
        this.onExecutionEvent = this.onExecutionEvent.bind(this);
        this.onExecutionsEvent = this.onExecutionsEvent.bind(this);
    }
    Object.defineProperty(AnalyzeCardComponent.prototype, "cronJobs", {
        set: function (cronJobs) {
            var cron = lodash_isUndefined__WEBPACK_IMPORTED_MODULE_7__(cronJobs) ? '' : cronJobs[this.analysis.id];
            if (!cron) {
                this.schedule = '';
                return;
            }
            var _a = cron.jobDetails, cronExpression = _a.cronExpression, activeTab = _a.activeTab, timezone = _a.timezone;
            this.schedule = Object(_common_utils_cron2Readable__WEBPACK_IMPORTED_MODULE_6__["generateSchedule"])(cronExpression, activeTab, timezone);
        },
        enumerable: true,
        configurable: true
    });
    AnalyzeCardComponent.prototype.ngOnInit = function () {
        this.canUserFork = this._jwt.hasPrivilege('FORK', {
            subCategoryId: Object(_designer_types__WEBPACK_IMPORTED_MODULE_4__["isDSLAnalysis"])(this.analysis)
                ? this.analysis.category
                : this.analysis.categoryId
        });
        var _a = this.analysis, type = _a.type, id = _a.id;
        var chartType = Object(_designer_types__WEBPACK_IMPORTED_MODULE_4__["isDSLAnalysis"])(this.analysis)
            ? this.analysis.chartOptions.chartType
            : this.analysis.chartType;
        chartType = type === 'chart' ? chartType : '';
        this.placeholderClass = "m-" + type + (chartType ? "-" + chartType : '');
        this.typeIdentifier = "analysis-type:" + type + (chartType ? ":" + chartType : '');
        this._executeService.subscribe(id, this.onExecutionsEvent);
    };
    AnalyzeCardComponent.prototype.onExecutionsEvent = function (e) {
        if (!e.subject.isStopped) {
            e.subject.subscribe(this.onExecutionEvent);
        }
    };
    AnalyzeCardComponent.prototype.onExecutionEvent = function (e) {
        this.isExecuting = e.state === _services_execute_service__WEBPACK_IMPORTED_MODULE_3__["EXECUTION_STATES"].EXECUTING;
    };
    AnalyzeCardComponent.prototype.afterDelete = function (analysis) {
        this.action.emit({
            action: 'delete',
            analysis: analysis
        });
    };
    AnalyzeCardComponent.prototype.afterExecute = function (analysis) {
        this.action.emit({
            action: 'execute',
            analysis: analysis
        });
    };
    AnalyzeCardComponent.prototype.afterPublish = function (analysis) {
        this.action.emit({
            action: 'publish',
            analysis: analysis
        });
    };
    AnalyzeCardComponent.prototype.afterEdit = function (_a) {
        var analysis = _a.analysis, requestExecution = _a.requestExecution;
        this.action.emit({
            action: 'edit',
            analysis: analysis,
            requestExecution: requestExecution
        });
    };
    AnalyzeCardComponent.prototype.fork = function (an) {
        this._analyzeActionsService.fork(an);
    };
    AnalyzeCardComponent.prototype.getType = function (type) {
        var analysisType = type;
        if (analysisType === 'esReport') {
            analysisType = 'REPORT';
        }
        return analysisType.toUpperCase();
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AnalyzeCardComponent.prototype, "action", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], AnalyzeCardComponent.prototype, "analysis", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", String)
    ], AnalyzeCardComponent.prototype, "analysisType", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", String)
    ], AnalyzeCardComponent.prototype, "highlightTerm", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Object])
    ], AnalyzeCardComponent.prototype, "cronJobs", null);
    AnalyzeCardComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'analyze-card',
            template: __webpack_require__(/*! ./analyze-card.component.html */ "./src/app/modules/analyze/view/card/analyze-card.component.html"),
            styles: [__webpack_require__(/*! ./analyze-card.component.scss */ "./src/app/modules/analyze/view/card/analyze-card.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_actions__WEBPACK_IMPORTED_MODULE_2__["AnalyzeActionsService"],
            _common_services__WEBPACK_IMPORTED_MODULE_5__["JwtService"],
            _services_execute_service__WEBPACK_IMPORTED_MODULE_3__["ExecuteService"]])
    ], AnalyzeCardComponent);
    return AnalyzeCardComponent;
}());



/***/ }),

/***/ "./src/app/modules/analyze/view/card/index.ts":
/*!****************************************************!*\
  !*** ./src/app/modules/analyze/view/card/index.ts ***!
  \****************************************************/
/*! exports provided: AnalyzeCardComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _analyze_card_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./analyze-card.component */ "./src/app/modules/analyze/view/card/analyze-card.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AnalyzeCardComponent", function() { return _analyze_card_component__WEBPACK_IMPORTED_MODULE_0__["AnalyzeCardComponent"]; });




/***/ }),

/***/ "./src/app/modules/analyze/view/index.ts":
/*!***********************************************!*\
  !*** ./src/app/modules/analyze/view/index.ts ***!
  \***********************************************/
/*! exports provided: AnalyzeViewModule, AnalyzeViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnalyzeViewModule", function() { return AnalyzeViewModule; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _common__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! ../../../common */ "./src/app/common/index.ts");
/* harmony import */ var _actions__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! ../actions */ "./src/app/modules/analyze/actions/index.ts");
/* harmony import */ var _analyze_view_component__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! ./analyze-view.component */ "./src/app/modules/analyze/view/analyze-view.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AnalyzeViewComponent", function() { return _analyze_view_component__WEBPACK_IMPORTED_MODULE_4__["AnalyzeViewComponent"]; });

/* harmony import */ var _card__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ./card */ "./src/app/modules/analyze/view/card/index.ts");
/* harmony import */ var _card_view__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ./card-view */ "./src/app/modules/analyze/view/card-view/index.ts");
/* harmony import */ var _list_view__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ./list-view */ "./src/app/modules/analyze/view/list-view/index.ts");
/* harmony import */ var _new_dialog__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ./new-dialog */ "./src/app/modules/analyze/view/new-dialog/index.ts");
/* harmony import */ var _analyses_filter_pipe__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ./analyses-filter.pipe */ "./src/app/modules/analyze/view/analyses-filter.pipe.ts");










var COMPONENTS = [
    _analyze_view_component__WEBPACK_IMPORTED_MODULE_4__["AnalyzeViewComponent"],
    _card_view__WEBPACK_IMPORTED_MODULE_6__["AnalyzeCardViewComponent"],
    _list_view__WEBPACK_IMPORTED_MODULE_7__["AnalyzeListViewComponent"],
    _card__WEBPACK_IMPORTED_MODULE_5__["AnalyzeCardComponent"],
    _new_dialog__WEBPACK_IMPORTED_MODULE_8__["AnalyzeNewDialogComponent"]
];
var AnalyzeViewModule = /** @class */ (function () {
    function AnalyzeViewModule() {
    }
    AnalyzeViewModule = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["NgModule"])({
            imports: [
                _common__WEBPACK_IMPORTED_MODULE_2__["CommonModuleTs"],
                _actions__WEBPACK_IMPORTED_MODULE_3__["AnalyzeActionsModule"]
            ],
            declarations: COMPONENTS.concat([
                _analyses_filter_pipe__WEBPACK_IMPORTED_MODULE_9__["AnalysesFilterPipe"]
            ]),
            entryComponents: COMPONENTS,
            exports: [_analyze_view_component__WEBPACK_IMPORTED_MODULE_4__["AnalyzeViewComponent"]]
        })
    ], AnalyzeViewModule);
    return AnalyzeViewModule;
}());




/***/ }),

/***/ "./src/app/modules/analyze/view/list-view/analyze-list-view.component.html":
/*!*********************************************************************************!*\
  !*** ./src/app/modules/analyze/view/list-view/analyze-list-view.component.html ***!
  \*********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<div style=\"height: calc(100vh - 51px - 70px - 65px);\">\n  <dx-data-grid\n    [customizeColumns]=\"config.customizeColumns\"\n    [columnAutoWidth]=\"config.columnAutoWidth\"\n    [columnMinWidth]=\"config.columnMinWidth\"\n    [columnResizingMode]=\"config.columnResizingMode\"\n    [allowColumnReordering]=\"config.allowColumnReordering\"\n    [allowColumnResizing]=\"config.allowColumnResizing\"\n    [showColumnHeaders]=\"config.showColumnHeaders\"\n    [showColumnLines]=\"config.showColumnLines\"\n    [showRowLines]=\"config.showRowLines\"\n    [showBorders]=\"config.showBorders\"\n    [rowAlternationEnabled]=\"config.rowAlternationEnabled\"\n    [hoverStateEnabled]=\"config.hoverStateEnabled\"\n    [wordWrapEnabled]=\"config.wordWrapEnabled\"\n    [scrolling]=\"config.scrolling\"\n    [sorting]=\"config.sorting\"\n    [dataSource]=\"analyses\"\n    [columns]=\"config.columns\"\n    [pager]=\"config.pager\"\n    [paging]=\"config.paging\"\n    [width]=\"config.width\"\n    [height]=\"config.height\"\n  >\n    <dxo-scrolling\n      mode=\"virtual\"\n      showScrollbar=\"always\"\n      [useNative]=\"false\"\n    ></dxo-scrolling>\n\n    <div *dxTemplate=\"let cell of 'linkCellTemplate'\">\n      <a\n        [innerHtml]=\"cell.text | highlight: searchTerm\"\n        [queryParams]=\"{ isDSL: !!cell.data.sipQuery }\"\n        routerLink=\"../analysis/{{ cell.data.id }}/executed\"\n      >\n      </a>\n\n      <mat-chip\n        class=\"tag-chip status-chip mat-basic-chip\"\n        *ngIf=\"executions[cell.data.id] === executingState\"\n        i18n\n      >\n        EXECUTING\n      </mat-chip>\n    </div>\n\n    <div\n      *dxTemplate=\"let cell of 'typeCellTemplate'\"\n      [innerHtml]=\"cell.text | highlight: searchTerm\"\n    ></div>\n\n    <div\n      *dxTemplate=\"let cell of 'highlightCellTemplate'\"\n      [innerHtml]=\"cell.text | highlight: searchTerm\"\n    ></div>\n\n    <div *dxTemplate=\"let cell of 'dateCellTemplate'\">\n      {{ cell.text | date: 'dd/MMM/yy' }}\n    </div>\n\n    <div *dxTemplate=\"let cell of 'actionCellTemplate'\">\n      <div\n        fxLayout=\"row\"\n        fxLayoutAlign=\"center center\"\n        class=\"list-action__container\"\n      >\n        <button\n          mat-icon-button\n          *ngIf=\"canUserFork\"\n          (click)=\"fork(cell.data)\"\n          class=\"list-action__button\"\n          i18n-matTooltip=\"Fork Analysis\"\n          matTooltip=\"Fork Analysis\"\n        >\n          <mat-icon fontIcon=\"icon-fork\"></mat-icon>\n        </button>\n        <analyze-actions-menu-u\n          class=\"list-view-actions-menu\"\n          [analysis]=\"cell.data\"\n          [actionsToDisable]=\"\n            executions[cell.data.id] === executingState ? 'execute' : ''\n          \"\n          exclude=\"fork-export-details\"\n          (afterDelete)=\"afterDelete($event)\"\n          (afterExecute)=\"afterExecute($event)\"\n          (afterPublish)=\"afterPublish($event)\"\n          (afterEdit)=\"afterEdit($event)\"\n        ></analyze-actions-menu-u>\n      </div>\n    </div>\n  </dx-data-grid>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/analyze/view/list-view/analyze-list-view.component.scss":
/*!*********************************************************************************!*\
  !*** ./src/app/modules/analyze/view/list-view/analyze-list-view.component.scss ***!
  \*********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ".branded-column-name {\n  color: #0077be;\n  font-weight: bolder; }\n\n.list-action__container {\n  max-height: 24px;\n  height: 24px; }\n\n.list-action__container .list-action__button {\n    display: inline-flex;\n    min-width: 24px;\n    min-height: 24px;\n    line-height: 24px;\n    height: 24px; }\n\n.list-view-actions-menu {\n  width: 30%; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FuYWx5emUvdmlldy9saXN0LXZpZXcvYW5hbHl6ZS1saXN0LXZpZXcuY29tcG9uZW50LnNjc3MiLCIvVXNlcnMvYmFybmFtdW10eWFuL1Byb2plY3RzL21vZHVzL3NpcC9zYXctd2ViL3NyYy90aGVtZXMvYmFzZS9fY29sb3JzLnNjc3MiXSwibmFtZXMiOltdLCJtYXBwaW5ncyI6IkFBRUE7RUFDRSxjQ0R1QjtFREV2QixtQkFBbUIsRUFBQTs7QUFHckI7RUFDRSxnQkFBZ0I7RUFDaEIsWUFBWSxFQUFBOztBQUZkO0lBS0ksb0JBQW9CO0lBQ3BCLGVBQWU7SUFDZixnQkFBZ0I7SUFDaEIsaUJBQWlCO0lBQ2pCLFlBQVksRUFBQTs7QUFJaEI7RUFDRSxVQUFVLEVBQUEiLCJmaWxlIjoic3JjL2FwcC9tb2R1bGVzL2FuYWx5emUvdmlldy9saXN0LXZpZXcvYW5hbHl6ZS1saXN0LXZpZXcuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0IFwic3JjL3RoZW1lcy9iYXNlL2NvbG9yc1wiO1xuXG4uYnJhbmRlZC1jb2x1bW4tbmFtZSB7XG4gIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIyO1xuICBmb250LXdlaWdodDogYm9sZGVyO1xufVxuXG4ubGlzdC1hY3Rpb25fX2NvbnRhaW5lciB7XG4gIG1heC1oZWlnaHQ6IDI0cHg7XG4gIGhlaWdodDogMjRweDtcblxuICAubGlzdC1hY3Rpb25fX2J1dHRvbiB7XG4gICAgZGlzcGxheTogaW5saW5lLWZsZXg7XG4gICAgbWluLXdpZHRoOiAyNHB4O1xuICAgIG1pbi1oZWlnaHQ6IDI0cHg7XG4gICAgbGluZS1oZWlnaHQ6IDI0cHg7XG4gICAgaGVpZ2h0OiAyNHB4O1xuICB9XG59XG5cbi5saXN0LXZpZXctYWN0aW9ucy1tZW51IHtcbiAgd2lkdGg6IDMwJTtcbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/analyze/view/list-view/analyze-list-view.component.ts":
/*!*******************************************************************************!*\
  !*** ./src/app/modules/analyze/view/list-view/analyze-list-view.component.ts ***!
  \*******************************************************************************/
/*! exports provided: AnalyzeListViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnalyzeListViewComponent", function() { return AnalyzeListViewComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! lodash/forEach */ "./node_modules/lodash/forEach.js");
/* harmony import */ var lodash_forEach__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(lodash_forEach__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/isEmpty */ "./node_modules/lodash/isEmpty.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_isEmpty__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var _common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! ../../../../common/services/dxDataGrid.service */ "./src/app/common/services/dxDataGrid.service.ts");
/* harmony import */ var _actions__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! ../../actions */ "./src/app/modules/analyze/actions/index.ts");
/* harmony import */ var _common_utils_cron2Readable__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! ../../../../common/utils/cron2Readable */ "./src/app/common/utils/cron2Readable.ts");
/* harmony import */ var _services_execute_service__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! ../../services/execute.service */ "./src/app/modules/analyze/services/execute.service.ts");
/* harmony import */ var _common_services__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! ../../../../common/services */ "./src/app/common/services/index.ts");










var AnalyzeListViewComponent = /** @class */ (function () {
    function AnalyzeListViewComponent(_DxDataGridService, _analyzeActionsService, _jwt, _executeService) {
        this._DxDataGridService = _DxDataGridService;
        this._analyzeActionsService = _analyzeActionsService;
        this._jwt = _jwt;
        this._executeService = _executeService;
        this.action = new _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"]();
        this.canUserFork = false;
        this.executions = {};
        this.executingState = _services_execute_service__WEBPACK_IMPORTED_MODULE_8__["EXECUTION_STATES"].EXECUTING;
    }
    Object.defineProperty(AnalyzeListViewComponent.prototype, "setAnalyses", {
        set: function (analyses) {
            this.analyses = analyses;
            if (!lodash_isEmpty__WEBPACK_IMPORTED_MODULE_3__(analyses)) {
                this.canUserFork = this._jwt.hasPrivilege('FORK', {
                    subCategoryId: analyses[0].categoryId
                });
            }
        },
        enumerable: true,
        configurable: true
    });
    AnalyzeListViewComponent.prototype.ngOnInit = function () {
        this.config = this.getGridConfig();
        this.onExecutionEvent = this.onExecutionEvent.bind(this);
        this._executeService.subscribeToAllExecuting(this.onExecutionEvent);
    };
    AnalyzeListViewComponent.prototype.onExecutionEvent = function (e) {
        this.executions = e;
    };
    AnalyzeListViewComponent.prototype.afterDelete = function (analysis) {
        this.action.emit({
            action: 'delete',
            analysis: analysis
        });
    };
    AnalyzeListViewComponent.prototype.afterExecute = function (analysis) {
        this.action.emit({
            action: 'execute',
            analysis: analysis
        });
    };
    AnalyzeListViewComponent.prototype.afterPublish = function (analysis) {
        this.action.emit({
            action: 'publish',
            analysis: analysis
        });
    };
    AnalyzeListViewComponent.prototype.afterEdit = function (_a) {
        var analysis = _a.analysis, requestExecution = _a.requestExecution;
        this.action.emit({
            action: 'edit',
            analysis: analysis,
            requestExecution: requestExecution
        });
    };
    AnalyzeListViewComponent.prototype.fork = function (an) {
        this._analyzeActionsService.fork(an);
    };
    AnalyzeListViewComponent.prototype.getRowType = function (rowData) {
        var analysisType = rowData.type;
        if (analysisType === 'esReport') {
            analysisType = 'REPORT';
        }
        return analysisType.toUpperCase();
    };
    AnalyzeListViewComponent.prototype.getGridConfig = function () {
        var _this = this;
        var columns = [
            {
                caption: 'NAME',
                dataField: 'name',
                width: '20%',
                cellTemplate: 'linkCellTemplate',
                cssClass: 'branded-column-name'
            },
            {
                caption: 'METRICS',
                dataField: 'metrics',
                width: '20%',
                calculateCellValue: function (rowData) {
                    return lodash_get__WEBPACK_IMPORTED_MODULE_4__(_this.semanticLayerDataMap, rowData.semanticId + ".metricName");
                },
                cellTemplate: 'highlightCellTemplate'
            },
            {
                caption: 'SCHEDULED',
                calculateCellValue: function (rowData) {
                    var cron = !_this.cronJobs ? '' : _this.cronJobs[rowData.id];
                    if (!cron) {
                        return 'No Schedule Set';
                    }
                    var _a = cron.jobDetails, cronExpression = _a.cronExpression, activeTab = _a.activeTab, timezone = _a.timezone;
                    return Object(_common_utils_cron2Readable__WEBPACK_IMPORTED_MODULE_7__["generateSchedule"])(cronExpression, activeTab, timezone);
                },
                width: '12%'
            },
            {
                caption: 'TYPE',
                dataField: 'type',
                width: '8%',
                calculateCellValue: function (rowData) { return _this.getRowType(rowData); },
                cellTemplate: 'typeCellTemplate'
            },
            {
                caption: 'LAST MODIFIED BY',
                width: '20%',
                calculateCellValue: function (rowData) {
                    return (rowData.modifiedBy || rowData.createdBy || '').toUpperCase();
                },
                cellTemplate: 'highlightCellTemplate'
            },
            {
                caption: 'LAST MODIFIED ON',
                width: '10%',
                calculateCellValue: function (rowData) {
                    return rowData.modifiedTime || rowData.createdTime || null;
                },
                cellTemplate: 'dateCellTemplate'
            },
            {
                caption: '',
                cellTemplate: 'actionCellTemplate'
            }
        ];
        return this._DxDataGridService.mergeWithDefaultConfig({
            columns: columns,
            paging: {
                pageSize: 10
            },
            pager: {
                showPageSizeSelector: true,
                showInfo: true
            },
            width: '100%',
            height: '100%',
            customizeColumns: function (cols) {
                var last = cols.length - 1;
                lodash_forEach__WEBPACK_IMPORTED_MODULE_2__(cols, function (col, i) {
                    if (i === last) {
                        col.allowSorting = false;
                        col.alignment = 'center';
                    }
                    else {
                        col.allowSorting = true;
                        col.alignment = 'left';
                    }
                });
            }
        });
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Output"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_core__WEBPACK_IMPORTED_MODULE_1__["EventEmitter"])
    ], AnalyzeListViewComponent.prototype, "action", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])('analyses'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Array),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [Array])
    ], AnalyzeListViewComponent.prototype, "setAnalyses", null);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", String)
    ], AnalyzeListViewComponent.prototype, "analysisType", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", String)
    ], AnalyzeListViewComponent.prototype, "searchTerm", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], AnalyzeListViewComponent.prototype, "cronJobs", void 0);
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Input"])(),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", Object)
    ], AnalyzeListViewComponent.prototype, "semanticLayerDataMap", void 0);
    AnalyzeListViewComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'analyze-list-view',
            template: __webpack_require__(/*! ./analyze-list-view.component.html */ "./src/app/modules/analyze/view/list-view/analyze-list-view.component.html"),
            styles: [__webpack_require__(/*! ./analyze-list-view.component.scss */ "./src/app/modules/analyze/view/list-view/analyze-list-view.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_common_services_dxDataGrid_service__WEBPACK_IMPORTED_MODULE_5__["DxDataGridService"],
            _actions__WEBPACK_IMPORTED_MODULE_6__["AnalyzeActionsService"],
            _common_services__WEBPACK_IMPORTED_MODULE_9__["JwtService"],
            _services_execute_service__WEBPACK_IMPORTED_MODULE_8__["ExecuteService"]])
    ], AnalyzeListViewComponent);
    return AnalyzeListViewComponent;
}());



/***/ }),

/***/ "./src/app/modules/analyze/view/list-view/index.ts":
/*!*********************************************************!*\
  !*** ./src/app/modules/analyze/view/list-view/index.ts ***!
  \*********************************************************/
/*! exports provided: AnalyzeListViewComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _analyze_list_view_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./analyze-list-view.component */ "./src/app/modules/analyze/view/list-view/analyze-list-view.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AnalyzeListViewComponent", function() { return _analyze_list_view_component__WEBPACK_IMPORTED_MODULE_0__["AnalyzeListViewComponent"]; });




/***/ }),

/***/ "./src/app/modules/analyze/view/new-dialog/analyze-new-dialog.component.html":
/*!***********************************************************************************!*\
  !*** ./src/app/modules/analyze/view/new-dialog/analyze-new-dialog.component.html ***!
  \***********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = "<h2 mat-dialog-title i18n>\n  Create New Analysis\n  <button mat-icon-button\n          (click)=\"_dialogRef.close()\">\n    <mat-icon fontIcon=\"icon-close\"></mat-icon>\n  </button>\n</h2>\n<mat-divider></mat-divider>\n<mat-dialog-content>\n\n  <mat-horizontal-stepper linear=\"true\" #newAnalysisStepper>\n\n    <ng-template matStepperIcon=\"done\">\n      <mat-icon fontIcon=\"icon-checkmark\"></mat-icon>\n    </ng-template>\n    <ng-template matStepperIcon=\"edit\">\n      <mat-icon fontIcon=\"icon-checkmark\"></mat-icon>\n    </ng-template>\n\n    <mat-step label=\"Select Analysis Type\" [completed]=\"!!selectedMethod\">\n      <div class=\"container\">\n        <div *ngFor=\"let category of methodCategories; trackBy: trackById\">\n          <div class=\"md-body-1\" flex>{{category.label}}</div>\n          <choice-group-u (change)=\"onMethodSelected($event)\"\n                          [items]=\"category.children\"\n                          [value]=\"selectedMethod\"\n          ></choice-group-u>\n        </div>\n      </div>\n    </mat-step>\n\n    <mat-step label=\"Select Datapod\" [completed]=\"!!selectedMetric\">\n      <mat-form-field appearance=\"outline\">\n        <mat-icon matPrefix fontIcon=\"icon-search\"></mat-icon>\n        <input matInput\n               autocomplete=\"off\"\n               [attr.e2e]=\"'metric-search-input-field'\"\n               [(ngModel)]=\"searchMetric\" placeholder=\"Search\">\n        <mat-icon matSuffix (click)=\"searchMetric = ''\" *ngIf=\"searchMetric\" fontIcon=\"icon-close\"></mat-icon>\n      </mat-form-field>\n      <button mat-button\n              matTooltip=\"Sort\"\n              (click)=\"toggleSort()\">\n        <mat-icon style=\"vertical-align: baseline\" fontIcon=\"icon-sort2\"></mat-icon>\n        Sort\n      </button>\n      <mat-tab-group>\n        <mat-tab *ngFor=\"let metricCategory of supportedMetricCategories\">\n          <ng-template mat-tab-label>\n            <mat-icon class=\"tab-icon\" [svgIcon]=\"getCategoryIcon(metricCategory)\"></mat-icon>\n            {{getMetricCategoryLabel(metricCategory)}}\n          </ng-template>\n          <mat-radio-group (change)=\"onMetricSelected($event.value)\" fxLayout=\"row wrap\" class=\"container\" [value]=\"selectedMetric\">\n            <mat-radio-button *ngFor=\"let metric of metricCategory.metrics | filter:'metricName':searchMetric; trackBy: trackById\"\n                              class=\"metric-radio-button\"\n                              [attr.e2e]=\"'metric-name-' + metric.metricName\"\n                              [value]=\"metric\"\n            >\n              <mat-icon class=\"category-icon\" [svgIcon]=\"getCategoryIcon(metricCategory)\"></mat-icon>\n              <div class=\"category-detail\">\n                {{metric.metricName}}\n                <span class=\"metric-created-time\" *ngIf=\"metric.createdAt\" [textContent]=\"'Created: ' + metric.createdAt\"></span>\n                <span class=\"metric-created-time\" *ngIf=\"metric.updatedAt\" [textContent]=\"'Updated: ' + metric.updatedAt\"></span>\n              </div>\n            </mat-radio-button>\n          </mat-radio-group>\n        </mat-tab>\n      </mat-tab-group>\n    </mat-step>\n\n  </mat-horizontal-stepper>\n</mat-dialog-content>\n<mat-divider></mat-divider>\n<div fxLayout=\"row-reverse\" fxLayoutAlign=\"space-between center\" class=\"dialog-actions\">\n  <button (click)=\"nextStep()\"\n          [disabled]=\"nextButtonDisabled()\"\n          e2e=\"create-analysis-btn\"\n          color=\"primary\"\n          mat-raised-button i18n\n          >{{newAnalysisStepper.selectedIndex === 1 ? 'Create' : 'Next'}}</button>\n  <button (click)=\"previousStep()\"\n          e2e=\"create-analysis-back-button\"\n          *ngIf=\"stepper.selectedIndex > 0\"\n          mat-button\n          i18n\n  >Back</button>\n</div>\n"

/***/ }),

/***/ "./src/app/modules/analyze/view/new-dialog/analyze-new-dialog.component.scss":
/*!***********************************************************************************!*\
  !*** ./src/app/modules/analyze/view/new-dialog/analyze-new-dialog.component.scss ***!
  \***********************************************************************************/
/*! no static exports found */
/***/ (function(module, exports) {

module.exports = ":host {\n  z-index: 80;\n  display: flex;\n  flex-direction: column;\n  width: 700px;\n  height: 620px; }\n\nh2.mat-dialog-title {\n  display: flex;\n  justify-content: space-between;\n  align-items: flex-start;\n  margin: 0; }\n\nh2.mat-dialog-title ::ng-deep span.mat-button-wrapper {\n    position: relative;\n    top: -10px; }\n\n.mat-dialog-content {\n  padding: 0 20px;\n  overflow: hidden;\n  flex-grow: 1;\n  max-height: unset; }\n\nmat-form-field {\n  width: 50%;\n  margin-left: 5px; }\n\nmat-form-field ::ng-deep .mat-form-field-infix {\n    padding: 1em 0 0.5em 0;\n    border: none; }\n\nmat-form-field ::ng-deep .mat-form-field-infix input {\n      line-height: 21px; }\n\nmat-form-field ::ng-deep .mat-form-field-suffix,\n  mat-form-field ::ng-deep .mat-form-field-prefix {\n    top: 0em; }\n\nmat-form-field ::ng-deep .mat-form-field-suffix {\n    font-size: 10px;\n    top: -2px;\n    cursor: pointer; }\n\nmat-horizontal-stepper ::ng-deep .mat-horizontal-stepper-header-container {\n  margin: 0 auto; }\n\nmat-tab-group ::ng-deep .mat-tab-body-wrapper {\n  padding-top: 5px; }\n\nmat-tab-group ::ng-deep .mat-tab-label-active {\n  color: #0077be; }\n\nmat-tab-group ::ng-deep .mat-tab-label {\n  min-width: 100px;\n  height: 30px; }\n\nmat-tab-group ::ng-deep mat-ink-bar {\n  width: 100px; }\n\nmat-tab-group .tab-icon {\n  padding-right: 5px;\n  width: 14px;\n  height: 14px; }\n\nmat-radio-group {\n  overflow: auto;\n  height: 335px;\n  align-content: start; }\n\n.metric-radio-button {\n  border: 1px solid #d1d3d3;\n  border-radius: 5px;\n  margin: 0 2px 5px;\n  width: 32%;\n  max-height: 64px; }\n\n.metric-radio-button ::ng-deep label.mat-radio-label {\n    height: 60px;\n    width: 100%; }\n\n.metric-radio-button ::ng-deep .mat-radio-container {\n    display: none; }\n\n.metric-radio-button ::ng-deep .mat-radio-label-content {\n    padding: 0;\n    font-weight: bold;\n    width: 100%;\n    display: flex;\n    color: #5c6670;\n    align-items: center;\n    justify-content: space-around;\n    flex-direction: row-reverse; }\n\n.metric-radio-button ::ng-deep .mat-radio-label-content .category-icon {\n      display: flex;\n      padding-right: 10px;\n      width: 20px;\n      height: 20px;\n      color: #0077be; }\n\n.metric-radio-button ::ng-deep .mat-radio-label-content .category-detail {\n      display: flex;\n      flex-direction: column;\n      padding-left: 6px;\n      flex-grow: 1; }\n\n.metric-radio-button .metric-created-time {\n    font-weight: normal;\n    font-size: 12px;\n    color: #999; }\n\n::ng-deep .metric-radio-button.mat-radio-checked {\n    background: #0077be; }\n\n::ng-deep .metric-radio-button.mat-radio-checked ::ng-deep .mat-radio-label-content {\n      color: white !important; }\n\n::ng-deep .metric-radio-button.mat-radio-checked ::ng-deep .mat-radio-label-content .category-icon {\n        color: white; }\n\n::ng-deep .metric-radio-button.mat-radio-checked .metric-created-time {\n      color: white !important; }\n\n.section > label {\n  color: #0077be;\n  display: block;\n  padding: 10px 0;\n  font-size: 16px;\n  font-weight: 400;\n  letter-spacing: 0.01em;\n  line-height: 24px; }\n\n.section .container {\n  color: #5c6670;\n  padding: 0 10px; }\n\n.section .container .mat-radio-label {\n    max-width: 100%;\n    white-space: normal;\n    vertical-align: middle;\n    font-size: 14px;\n    font-weight: 600;\n    color: #596574; }\n\n.section .container .mat-radio-label-content {\n    word-wrap: break-word;\n    display: inline-block;\n    width: 186px;\n    padding-bottom: 8px; }\n\n.dialog-actions {\n  padding: 5px 0; }\n\n/*# sourceMappingURL=data:application/json;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL2FwcC9tb2R1bGVzL2FuYWx5emUvdmlldy9uZXctZGlhbG9nL2FuYWx5emUtbmV3LWRpYWxvZy5jb21wb25lbnQuc2NzcyIsIi9Vc2Vycy9iYXJuYW11bXR5YW4vUHJvamVjdHMvbW9kdXMvc2lwL3Nhdy13ZWIvc3JjL3RoZW1lcy9iYXNlL19jb2xvcnMuc2NzcyJdLCJuYW1lcyI6W10sIm1hcHBpbmdzIjoiQUFFQTtFQUNFLFdBQVc7RUFDWCxhQUFhO0VBQ2Isc0JBQXNCO0VBQ3RCLFlBQVk7RUFDWixhQUFhLEVBQUE7O0FBR2Y7RUFDRSxhQUFhO0VBQ2IsOEJBQThCO0VBQzlCLHVCQUF1QjtFQUN2QixTQUFTLEVBQUE7O0FBSlg7SUFPSSxrQkFBa0I7SUFDbEIsVUFBVSxFQUFBOztBQUlkO0VBQ0UsZUFBZTtFQUNmLGdCQUFnQjtFQUNoQixZQUFZO0VBQ1osaUJBQWlCLEVBQUE7O0FBR25CO0VBQ0UsVUFBVTtFQUNWLGdCQUFnQixFQUFBOztBQUZsQjtJQUtJLHNCQUFzQjtJQUN0QixZQUFZLEVBQUE7O0FBTmhCO01BU00saUJBQWlCLEVBQUE7O0FBVHZCOztJQWVJLFFBQVEsRUFBQTs7QUFmWjtJQW1CSSxlQUFlO0lBQ2YsU0FBUztJQUNULGVBQWUsRUFBQTs7QUFJbkI7RUFFSSxjQUFjLEVBQUE7O0FBSWxCO0VBRUksZ0JBQWdCLEVBQUE7O0FBRnBCO0VBTUksY0NoRXFCLEVBQUE7O0FEMER6QjtFQVVJLGdCQUFnQjtFQUNoQixZQUFZLEVBQUE7O0FBWGhCO0VBZUksWUFBWSxFQUFBOztBQWZoQjtFQW1CSSxrQkFBa0I7RUFDbEIsV0FBVztFQUNYLFlBQVksRUFBQTs7QUFJaEI7RUFDRSxjQUFjO0VBQ2QsYUFBYTtFQUNiLG9CQUFvQixFQUFBOztBQUd0QjtFQUNFLHlCQzlFdUI7RUQrRXZCLGtCQUFrQjtFQUNsQixpQkFBaUI7RUFDakIsVUFBVTtFQUNWLGdCQUFnQixFQUFBOztBQUxsQjtJQVFJLFlBQVk7SUFDWixXQUFXLEVBQUE7O0FBVGY7SUFhSSxhQUFhLEVBQUE7O0FBYmpCO0lBaUJJLFVBQVU7SUFDVixpQkFBaUI7SUFDakIsV0FBVztJQUNYLGFBQWE7SUFDYixjQy9GcUI7SURnR3JCLG1CQUFtQjtJQUNuQiw2QkFBNkI7SUFDN0IsMkJBQTJCLEVBQUE7O0FBeEIvQjtNQTJCTSxhQUFhO01BQ2IsbUJBQW1CO01BQ25CLFdBQVc7TUFDWCxZQUFZO01BQ1osY0N4SG1CLEVBQUE7O0FEeUZ6QjtNQW1DTSxhQUFhO01BQ2Isc0JBQXNCO01BQ3RCLGlCQUFpQjtNQUNqQixZQUFZLEVBQUE7O0FBdENsQjtJQTJDSSxtQkFBbUI7SUFDbkIsZUFBZTtJQUNmLFdDekhrQixFQUFBOztBRDRIcEI7SUFDRSxtQkMxSXFCLEVBQUE7O0FEeUl2QjtNQUlJLHVCQUF1QixFQUFBOztBQUozQjtRQU9NLFlBQVksRUFBQTs7QUFQbEI7TUFZSSx1QkFBdUIsRUFBQTs7QUFLN0I7RUFFSSxjQzVKcUI7RUQ2SnJCLGNBQWM7RUFDZCxlQUFlO0VBQ2YsZUFBZTtFQUNmLGdCQUFnQjtFQUNoQixzQkFBc0I7RUFDdEIsaUJBQWlCLEVBQUE7O0FBUnJCO0VBWUksY0N2SnFCO0VEd0pyQixlQUFlLEVBQUE7O0FBYm5CO0lBZ0JNLGVBQWU7SUFDZixtQkFBbUI7SUFDbkIsc0JBQXNCO0lBQ3RCLGVBQWU7SUFDZixnQkFBZ0I7SUFDaEIsY0FBYyxFQUFBOztBQXJCcEI7SUF5Qk0scUJBQXFCO0lBQ3JCLHFCQUFxQjtJQUNyQixZQUFZO0lBQ1osbUJBQW1CLEVBQUE7O0FBS3pCO0VBQ0UsY0FBYyxFQUFBIiwiZmlsZSI6InNyYy9hcHAvbW9kdWxlcy9hbmFseXplL3ZpZXcvbmV3LWRpYWxvZy9hbmFseXplLW5ldy1kaWFsb2cuY29tcG9uZW50LnNjc3MiLCJzb3VyY2VzQ29udGVudCI6WyJAaW1wb3J0ICdzcmMvdGhlbWVzL2Jhc2UvY29sb3JzJztcblxuOmhvc3Qge1xuICB6LWluZGV4OiA4MDtcbiAgZGlzcGxheTogZmxleDtcbiAgZmxleC1kaXJlY3Rpb246IGNvbHVtbjtcbiAgd2lkdGg6IDcwMHB4O1xuICBoZWlnaHQ6IDYyMHB4O1xufVxuXG5oMi5tYXQtZGlhbG9nLXRpdGxlIHtcbiAgZGlzcGxheTogZmxleDtcbiAganVzdGlmeS1jb250ZW50OiBzcGFjZS1iZXR3ZWVuO1xuICBhbGlnbi1pdGVtczogZmxleC1zdGFydDtcbiAgbWFyZ2luOiAwO1xuXG4gIDo6bmctZGVlcCBzcGFuLm1hdC1idXR0b24td3JhcHBlciB7XG4gICAgcG9zaXRpb246IHJlbGF0aXZlO1xuICAgIHRvcDogLTEwcHg7XG4gIH1cbn1cblxuLm1hdC1kaWFsb2ctY29udGVudCB7XG4gIHBhZGRpbmc6IDAgMjBweDtcbiAgb3ZlcmZsb3c6IGhpZGRlbjtcbiAgZmxleC1ncm93OiAxO1xuICBtYXgtaGVpZ2h0OiB1bnNldDtcbn1cblxubWF0LWZvcm0tZmllbGQge1xuICB3aWR0aDogNTAlO1xuICBtYXJnaW4tbGVmdDogNXB4O1xuXG4gIDo6bmctZGVlcCAubWF0LWZvcm0tZmllbGQtaW5maXgge1xuICAgIHBhZGRpbmc6IDFlbSAwIDAuNWVtIDA7XG4gICAgYm9yZGVyOiBub25lO1xuXG4gICAgaW5wdXQge1xuICAgICAgbGluZS1oZWlnaHQ6IDIxcHg7XG4gICAgfVxuICB9XG5cbiAgOjpuZy1kZWVwIC5tYXQtZm9ybS1maWVsZC1zdWZmaXgsXG4gIDo6bmctZGVlcCAubWF0LWZvcm0tZmllbGQtcHJlZml4IHtcbiAgICB0b3A6IDBlbTtcbiAgfVxuXG4gIDo6bmctZGVlcCAubWF0LWZvcm0tZmllbGQtc3VmZml4IHtcbiAgICBmb250LXNpemU6IDEwcHg7XG4gICAgdG9wOiAtMnB4O1xuICAgIGN1cnNvcjogcG9pbnRlcjtcbiAgfVxufVxuXG5tYXQtaG9yaXpvbnRhbC1zdGVwcGVyIHtcbiAgOjpuZy1kZWVwIC5tYXQtaG9yaXpvbnRhbC1zdGVwcGVyLWhlYWRlci1jb250YWluZXIge1xuICAgIG1hcmdpbjogMCBhdXRvO1xuICB9XG59XG5cbm1hdC10YWItZ3JvdXAge1xuICA6Om5nLWRlZXAgLm1hdC10YWItYm9keS13cmFwcGVyIHtcbiAgICBwYWRkaW5nLXRvcDogNXB4O1xuICB9XG5cbiAgOjpuZy1kZWVwIC5tYXQtdGFiLWxhYmVsLWFjdGl2ZSB7XG4gICAgY29sb3I6ICRwcmltYXJ5LWJsdWUtYjI7XG4gIH1cblxuICA6Om5nLWRlZXAgLm1hdC10YWItbGFiZWwge1xuICAgIG1pbi13aWR0aDogMTAwcHg7XG4gICAgaGVpZ2h0OiAzMHB4O1xuICB9XG5cbiAgOjpuZy1kZWVwIG1hdC1pbmstYmFyIHtcbiAgICB3aWR0aDogMTAwcHg7XG4gIH1cblxuICAudGFiLWljb24ge1xuICAgIHBhZGRpbmctcmlnaHQ6IDVweDtcbiAgICB3aWR0aDogMTRweDtcbiAgICBoZWlnaHQ6IDE0cHg7XG4gIH1cbn1cblxubWF0LXJhZGlvLWdyb3VwIHtcbiAgb3ZlcmZsb3c6IGF1dG87XG4gIGhlaWdodDogMzM1cHg7XG4gIGFsaWduLWNvbnRlbnQ6IHN0YXJ0O1xufVxuXG4ubWV0cmljLXJhZGlvLWJ1dHRvbiB7XG4gIGJvcmRlcjogMXB4IHNvbGlkICRwcmltYXJ5LWdyZXktZzE7XG4gIGJvcmRlci1yYWRpdXM6IDVweDtcbiAgbWFyZ2luOiAwIDJweCA1cHg7XG4gIHdpZHRoOiAzMiU7XG4gIG1heC1oZWlnaHQ6IDY0cHg7XG5cbiAgOjpuZy1kZWVwIGxhYmVsLm1hdC1yYWRpby1sYWJlbCB7XG4gICAgaGVpZ2h0OiA2MHB4O1xuICAgIHdpZHRoOiAxMDAlO1xuICB9XG5cbiAgOjpuZy1kZWVwIC5tYXQtcmFkaW8tY29udGFpbmVyIHtcbiAgICBkaXNwbGF5OiBub25lO1xuICB9XG5cbiAgOjpuZy1kZWVwIC5tYXQtcmFkaW8tbGFiZWwtY29udGVudCB7XG4gICAgcGFkZGluZzogMDtcbiAgICBmb250LXdlaWdodDogYm9sZDtcbiAgICB3aWR0aDogMTAwJTtcbiAgICBkaXNwbGF5OiBmbGV4O1xuICAgIGNvbG9yOiAkcHJpbWFyeS1ncmV5LWc0O1xuICAgIGFsaWduLWl0ZW1zOiBjZW50ZXI7XG4gICAganVzdGlmeS1jb250ZW50OiBzcGFjZS1hcm91bmQ7XG4gICAgZmxleC1kaXJlY3Rpb246IHJvdy1yZXZlcnNlO1xuXG4gICAgLmNhdGVnb3J5LWljb24ge1xuICAgICAgZGlzcGxheTogZmxleDtcbiAgICAgIHBhZGRpbmctcmlnaHQ6IDEwcHg7XG4gICAgICB3aWR0aDogMjBweDtcbiAgICAgIGhlaWdodDogMjBweDtcbiAgICAgIGNvbG9yOiAkcHJpbWFyeS1ibHVlLWIyO1xuICAgIH1cblxuICAgIC5jYXRlZ29yeS1kZXRhaWwge1xuICAgICAgZGlzcGxheTogZmxleDtcbiAgICAgIGZsZXgtZGlyZWN0aW9uOiBjb2x1bW47XG4gICAgICBwYWRkaW5nLWxlZnQ6IDZweDtcbiAgICAgIGZsZXgtZ3JvdzogMTtcbiAgICB9XG4gIH1cblxuICAubWV0cmljLWNyZWF0ZWQtdGltZSB7XG4gICAgZm9udC13ZWlnaHQ6IG5vcm1hbDtcbiAgICBmb250LXNpemU6IDEycHg7XG4gICAgY29sb3I6ICRwcmltYXJ5LWdyZXktZzI7XG4gIH1cblxuICA6Om5nLWRlZXAgJi5tYXQtcmFkaW8tY2hlY2tlZCB7XG4gICAgYmFja2dyb3VuZDogJHByaW1hcnktYmx1ZS1iMjtcblxuICAgIDo6bmctZGVlcCAubWF0LXJhZGlvLWxhYmVsLWNvbnRlbnQge1xuICAgICAgY29sb3I6IHdoaXRlICFpbXBvcnRhbnQ7XG5cbiAgICAgIC5jYXRlZ29yeS1pY29uIHtcbiAgICAgICAgY29sb3I6IHdoaXRlO1xuICAgICAgfVxuICAgIH1cblxuICAgIC5tZXRyaWMtY3JlYXRlZC10aW1lIHtcbiAgICAgIGNvbG9yOiB3aGl0ZSAhaW1wb3J0YW50O1xuICAgIH1cbiAgfVxufVxuXG4uc2VjdGlvbiB7XG4gICYgPiBsYWJlbCB7XG4gICAgY29sb3I6ICRwcmltYXJ5LWJsdWUtYjI7XG4gICAgZGlzcGxheTogYmxvY2s7XG4gICAgcGFkZGluZzogMTBweCAwO1xuICAgIGZvbnQtc2l6ZTogMTZweDtcbiAgICBmb250LXdlaWdodDogNDAwO1xuICAgIGxldHRlci1zcGFjaW5nOiAwLjAxZW07XG4gICAgbGluZS1oZWlnaHQ6IDI0cHg7XG4gIH1cblxuICAuY29udGFpbmVyIHtcbiAgICBjb2xvcjogJHByaW1hcnktZ3JleS1nNDtcbiAgICBwYWRkaW5nOiAwIDEwcHg7XG5cbiAgICAubWF0LXJhZGlvLWxhYmVsIHtcbiAgICAgIG1heC13aWR0aDogMTAwJTtcbiAgICAgIHdoaXRlLXNwYWNlOiBub3JtYWw7XG4gICAgICB2ZXJ0aWNhbC1hbGlnbjogbWlkZGxlO1xuICAgICAgZm9udC1zaXplOiAxNHB4O1xuICAgICAgZm9udC13ZWlnaHQ6IDYwMDtcbiAgICAgIGNvbG9yOiAjNTk2NTc0O1xuICAgIH1cblxuICAgIC5tYXQtcmFkaW8tbGFiZWwtY29udGVudCB7XG4gICAgICB3b3JkLXdyYXA6IGJyZWFrLXdvcmQ7XG4gICAgICBkaXNwbGF5OiBpbmxpbmUtYmxvY2s7XG4gICAgICB3aWR0aDogMTg2cHg7XG4gICAgICBwYWRkaW5nLWJvdHRvbTogOHB4O1xuICAgIH1cbiAgfVxufVxuXG4uZGlhbG9nLWFjdGlvbnMge1xuICBwYWRkaW5nOiA1cHggMDtcbn1cbiIsIi8vIEJyYW5kaW5nIGNvbG9yc1xuJHByaW1hcnktYmx1ZS1iMTogIzFhODlkNDtcbiRwcmltYXJ5LWJsdWUtYjI6ICMwMDc3YmU7XG4kcHJpbWFyeS1ibHVlLWIzOiAjMjA2YmNlO1xuJHByaW1hcnktYmx1ZS1iNDogIzFkM2FiMjtcblxuJHByaW1hcnktaG92ZXItYmx1ZTogIzFkNjFiMTtcbiRncmlkLWhvdmVyLWNvbG9yOiAjZjVmOWZjO1xuJGdyaWQtaGVhZGVyLWJnLWNvbG9yOiAjZDdlYWZhO1xuJGdyaWQtaGVhZGVyLWNvbG9yOiAjMGI0ZDk5O1xuJGdyaWQtdGV4dC1jb2xvcjogIzQ2NDY0NjtcbiRncmV5LXRleHQtY29sb3I6ICM2MzYzNjM7XG5cbiRzZWxlY3Rpb24taGlnaGxpZ2h0LWNvbDogcmdiYSgwLCAxNDAsIDI2MCwgMC4yKTtcbiRwcmltYXJ5LWdyZXktZzE6ICNkMWQzZDM7XG4kcHJpbWFyeS1ncmV5LWcyOiAjOTk5O1xuJHByaW1hcnktZ3JleS1nMzogIzczNzM3MztcbiRwcmltYXJ5LWdyZXktZzQ6ICM1YzY2NzA7XG4kcHJpbWFyeS1ncmV5LWc1OiAjMzEzMTMxO1xuJHByaW1hcnktZ3JleS1nNjogI2Y1ZjVmNTtcbiRwcmltYXJ5LWdyZXktZzc6ICMzZDNkM2Q7XG5cbiRwcmltYXJ5LXdoaXRlOiAjZmZmO1xuJHByaW1hcnktYmxhY2s6ICMwMDA7XG4kcHJpbWFyeS1yZWQ6ICNhYjBlMjc7XG4kcHJpbWFyeS1ncmVlbjogIzczYjQyMTtcbiRwcmltYXJ5LW9yYW5nZTogI2YwNzYwMTtcblxuJHNlY29uZGFyeS1ncmVlbjogIzZmYjMyMDtcbiRzZWNvbmRhcnkteWVsbG93OiAjZmZiZTAwO1xuJHNlY29uZGFyeS1vcmFuZ2U6ICNmZjkwMDA7XG4kc2Vjb25kYXJ5LXJlZDogI2Q5M2UwMDtcbiRzZWNvbmRhcnktYmVycnk6ICNhYzE0NWE7XG4kc2Vjb25kYXJ5LXB1cnBsZTogIzkxNDE5MTtcblxuJHN0cmluZy10eXBlLWNvbG9yOiAjNDk5NWIyO1xuJG51bWJlci10eXBlLWNvbG9yOiAjMDBiMTgwO1xuJGdlby10eXBlLWNvbG9yOiAjODQ1ZWMyO1xuJGRhdGUtdHlwZS1jb2xvcjogI2QxOTYyMTtcblxuJHR5cGUtY2hpcC1vcGFjaXR5OiAxO1xuJHN0cmluZy10eXBlLWNoaXAtY29sb3I6IHJnYmEoJHN0cmluZy10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJG51bWJlci10eXBlLWNoaXAtY29sb3I6IHJnYmEoJG51bWJlci10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGdlby10eXBlLWNoaXAtY29sb3I6IHJnYmEoJGdlby10eXBlLWNvbG9yLCAkdHlwZS1jaGlwLW9wYWNpdHkpO1xuJGRhdGUtdHlwZS1jaGlwLWNvbG9yOiByZ2JhKCRkYXRlLXR5cGUtY29sb3IsICR0eXBlLWNoaXAtb3BhY2l0eSk7XG5cbiRyZXBvcnQtZGVzaWduZXItc2V0dGluZ3MtYmctY29sb3I6ICNmNWY5ZmM7XG4kYmFja2dyb3VuZC1jb2xvcjogI2Y1ZjlmYztcbiJdfQ== */"

/***/ }),

/***/ "./src/app/modules/analyze/view/new-dialog/analyze-new-dialog.component.ts":
/*!*********************************************************************************!*\
  !*** ./src/app/modules/analyze/view/new-dialog/analyze-new-dialog.component.ts ***!
  \*********************************************************************************/
/*! exports provided: AnalyzeNewDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "AnalyzeNewDialogComponent", function() { return AnalyzeNewDialogComponent; });
/* harmony import */ var tslib__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! tslib */ "./node_modules/tslib/tslib.es6.js");
/* harmony import */ var _angular_core__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! @angular/core */ "./node_modules/@angular/core/fesm5/core.js");
/* harmony import */ var _angular_material__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! @angular/material */ "./node_modules/@angular/material/esm5/material.es5.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/get */ "./node_modules/lodash/get.js");
/* harmony import */ var lodash_get__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_get__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var lodash_some__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! lodash/some */ "./node_modules/lodash/some.js");
/* harmony import */ var lodash_some__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(lodash_some__WEBPACK_IMPORTED_MODULE_4__);
/* harmony import */ var lodash_startsWith__WEBPACK_IMPORTED_MODULE_5__ = __webpack_require__(/*! lodash/startsWith */ "./node_modules/lodash/startsWith.js");
/* harmony import */ var lodash_startsWith__WEBPACK_IMPORTED_MODULE_5___default = /*#__PURE__*/__webpack_require__.n(lodash_startsWith__WEBPACK_IMPORTED_MODULE_5__);
/* harmony import */ var lodash_values__WEBPACK_IMPORTED_MODULE_6__ = __webpack_require__(/*! lodash/values */ "./node_modules/lodash/values.js");
/* harmony import */ var lodash_values__WEBPACK_IMPORTED_MODULE_6___default = /*#__PURE__*/__webpack_require__.n(lodash_values__WEBPACK_IMPORTED_MODULE_6__);
/* harmony import */ var lodash_startCase__WEBPACK_IMPORTED_MODULE_7__ = __webpack_require__(/*! lodash/startCase */ "./node_modules/lodash/startCase.js");
/* harmony import */ var lodash_startCase__WEBPACK_IMPORTED_MODULE_7___default = /*#__PURE__*/__webpack_require__.n(lodash_startCase__WEBPACK_IMPORTED_MODULE_7__);
/* harmony import */ var lodash_fp_filter__WEBPACK_IMPORTED_MODULE_8__ = __webpack_require__(/*! lodash/fp/filter */ "./node_modules/lodash/fp/filter.js");
/* harmony import */ var lodash_fp_filter__WEBPACK_IMPORTED_MODULE_8___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_filter__WEBPACK_IMPORTED_MODULE_8__);
/* harmony import */ var lodash_fp_orderBy__WEBPACK_IMPORTED_MODULE_9__ = __webpack_require__(/*! lodash/fp/orderBy */ "./node_modules/lodash/fp/orderBy.js");
/* harmony import */ var lodash_fp_orderBy__WEBPACK_IMPORTED_MODULE_9___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_orderBy__WEBPACK_IMPORTED_MODULE_9__);
/* harmony import */ var lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_10__ = __webpack_require__(/*! lodash/fp/pipe */ "./node_modules/lodash/fp/pipe.js");
/* harmony import */ var lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_10___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_10__);
/* harmony import */ var lodash_fp_reduce__WEBPACK_IMPORTED_MODULE_11__ = __webpack_require__(/*! lodash/fp/reduce */ "./node_modules/lodash/fp/reduce.js");
/* harmony import */ var lodash_fp_reduce__WEBPACK_IMPORTED_MODULE_11___default = /*#__PURE__*/__webpack_require__.n(lodash_fp_reduce__WEBPACK_IMPORTED_MODULE_11__);
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_12__ = __webpack_require__(/*! lodash/find */ "./node_modules/lodash/find.js");
/* harmony import */ var lodash_find__WEBPACK_IMPORTED_MODULE_12___default = /*#__PURE__*/__webpack_require__.n(lodash_find__WEBPACK_IMPORTED_MODULE_12__);
/* harmony import */ var _consts__WEBPACK_IMPORTED_MODULE_13__ = __webpack_require__(/*! ../../consts */ "./src/app/modules/analyze/consts.ts");
/* harmony import */ var _services_analyze_dialog_service__WEBPACK_IMPORTED_MODULE_14__ = __webpack_require__(/*! ../../services/analyze-dialog.service */ "./src/app/modules/analyze/services/analyze-dialog.service.ts");
/* harmony import */ var _angular_material_stepper__WEBPACK_IMPORTED_MODULE_15__ = __webpack_require__(/*! @angular/material/stepper */ "./node_modules/@angular/material/esm5/stepper.es5.js");
/* harmony import */ var _common_pipes_filter_pipe__WEBPACK_IMPORTED_MODULE_16__ = __webpack_require__(/*! ../../../../common/pipes/filter.pipe */ "./src/app/common/pipes/filter.pipe.ts");

















var AnalyzeNewDialogComponent = /** @class */ (function () {
    function AnalyzeNewDialogComponent(_analyzeDialogService, _dialogRef, filterPipe, data) {
        this._analyzeDialogService = _analyzeDialogService;
        this._dialogRef = _dialogRef;
        this.filterPipe = filterPipe;
        this.data = data;
        this.methodCategories = _consts__WEBPACK_IMPORTED_MODULE_13__["ANALYSIS_METHODS"];
        this.supportedMetricCategories = [];
        this._sortOrder = 'asc';
    }
    AnalyzeNewDialogComponent.prototype.onMetricSelected = function (metric) {
        this.selectedMetric = metric;
    };
    AnalyzeNewDialogComponent.prototype.onMethodSelected = function (method) {
        this.selectedMethod = method.type ? method : null;
        this.setSupportedMetrics(method);
    };
    AnalyzeNewDialogComponent.prototype.trackById = function (index, metric) {
        return metric.id;
    };
    /**
     * searchCount
     * Returns the number of metrics matching the search criteria
     *
     * @param metrics
     * @returns {number}
     */
    AnalyzeNewDialogComponent.prototype.searchCount = function (metrics) {
        return this.filterPipe.transform(metrics, 'metricName', this.searchMetric)
            .length;
    };
    AnalyzeNewDialogComponent.prototype.setSupportedMetrics = function (method) {
        this._sortOrder = 'asc';
        this.supportedMetricCategories = lodash_fp_pipe__WEBPACK_IMPORTED_MODULE_10__(lodash_fp_filter__WEBPACK_IMPORTED_MODULE_8__(function (metric) {
            if (method.type === 'map:map') {
                var mapSupport = lodash_find__WEBPACK_IMPORTED_MODULE_12__(metric.supports, function (_a) {
                    var category = _a.category;
                    return category === 'map';
                });
                if (!mapSupport) {
                    return false;
                }
                var doesSupportsMap = lodash_some__WEBPACK_IMPORTED_MODULE_4__(mapSupport.children, function (_a) {
                    var type = _a.type;
                    return type === 'map:map';
                });
                return doesSupportsMap;
            }
            if (lodash_startsWith__WEBPACK_IMPORTED_MODULE_5__(method.type, 'map:chart')) {
                var mapChartSupport = lodash_find__WEBPACK_IMPORTED_MODULE_12__(metric.supports, function (_a) {
                    var category = _a.category;
                    return category === 'map';
                });
                if (!mapChartSupport) {
                    return false;
                }
                var doesSupportsMapChart = lodash_some__WEBPACK_IMPORTED_MODULE_4__(mapChartSupport.children, function (_a) {
                    var type = _a.type;
                    return type === 'map:chart';
                });
                return doesSupportsMapChart;
            }
            var isEsMetric = lodash_get__WEBPACK_IMPORTED_MODULE_3__(metric, 'esRepository.storageType') === 'ES';
            return isEsMetric || method.type === 'table:report';
        }), lodash_fp_orderBy__WEBPACK_IMPORTED_MODULE_9__(['metricName'], [this._sortOrder]), lodash_fp_reduce__WEBPACK_IMPORTED_MODULE_11__(function (acc, metric) {
            var category = lodash_startCase__WEBPACK_IMPORTED_MODULE_7__(metric.category || 'Default');
            acc[category] = acc[category] || {
                label: category,
                metrics: []
            };
            acc[category].metrics.push(metric);
            return acc;
        }, {}), lodash_values__WEBPACK_IMPORTED_MODULE_6__)(this.data.metrics);
        this.selectedMetric = null;
        this.searchMetric = '';
    };
    AnalyzeNewDialogComponent.prototype.nextButtonDisabled = function () {
        if (this.stepper.selectedIndex === 0) {
            return !this.selectedMethod;
        }
        else if (this.stepper.selectedIndex === 1) {
            return !this.selectedMethod || !this.selectedMetric;
        }
    };
    AnalyzeNewDialogComponent.prototype.toggleSort = function () {
        this._sortOrder = this._sortOrder === 'asc' ? 'desc' : 'asc';
        this.supportedMetricCategories[0].metrics = lodash_fp_orderBy__WEBPACK_IMPORTED_MODULE_9__(['metricName'], [this._sortOrder], this.supportedMetricCategories[0].metrics);
    };
    AnalyzeNewDialogComponent.prototype.previousStep = function () {
        this.stepper.previous();
    };
    AnalyzeNewDialogComponent.prototype.nextStep = function () {
        if (this.stepper.selectedIndex === 0) {
            this.stepper.next();
        }
        else if (!this.nextButtonDisabled()) {
            this.createAnalysis();
        }
    };
    AnalyzeNewDialogComponent.prototype.getAnalysisType = function (method, metric) {
        var _a = method.type.split(':'), first = _a[0], second = _a[1];
        switch (first) {
            case 'chart':
            case 'map':
                return {
                    type: first,
                    chartType: second
                };
            case 'table':
                // handle esReport edge case
                var metricType = lodash_get__WEBPACK_IMPORTED_MODULE_3__(metric, 'esRepository.storageType');
                var isEsMetric = metricType === 'ES';
                if (second === 'report' && isEsMetric) {
                    return { type: 'esReport' };
                }
                return { type: second };
        }
    };
    AnalyzeNewDialogComponent.prototype.createAnalysis = function () {
        var _this = this;
        var _a = this.selectedMetric, semanticId = _a.id, metricName = _a.metricName, supports = _a.supports;
        var _b = this.getAnalysisType(this.selectedMethod, this.selectedMetric), type = _b.type, chartType = _b.chartType;
        var model = {
            type: type,
            chartType: chartType,
            categoryId: this.data.id,
            semanticId: semanticId,
            metricName: metricName,
            supports: supports
        };
        this._dialogRef.afterClosed().subscribe(function () {
            _this._analyzeDialogService.openNewAnalysisDialog(model);
        });
        this._dialogRef.close();
    };
    AnalyzeNewDialogComponent.prototype.getCategoryIcon = function (metricCategory) {
        var name = lodash_startCase__WEBPACK_IMPORTED_MODULE_7__(metricCategory.label);
        var icon = lodash_get__WEBPACK_IMPORTED_MODULE_3__(_consts__WEBPACK_IMPORTED_MODULE_13__["DATAPOD_CATEGORIES_OBJ"][name], 'icon');
        return icon || '';
    };
    AnalyzeNewDialogComponent.prototype.getMetricCategoryLabel = function (metricCategory) {
        var metricCount = this.searchCount(metricCategory.metrics);
        var label = metricCategory.label;
        return label + " (" + metricCount + ")";
    };
    tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["ViewChild"])('newAnalysisStepper'),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:type", _angular_material_stepper__WEBPACK_IMPORTED_MODULE_15__["MatHorizontalStepper"])
    ], AnalyzeNewDialogComponent.prototype, "stepper", void 0);
    AnalyzeNewDialogComponent = tslib__WEBPACK_IMPORTED_MODULE_0__["__decorate"]([
        Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Component"])({
            selector: 'analyze-new-dialog',
            template: __webpack_require__(/*! ./analyze-new-dialog.component.html */ "./src/app/modules/analyze/view/new-dialog/analyze-new-dialog.component.html"),
            providers: [_common_pipes_filter_pipe__WEBPACK_IMPORTED_MODULE_16__["FilterPipe"]],
            styles: [__webpack_require__(/*! ./analyze-new-dialog.component.scss */ "./src/app/modules/analyze/view/new-dialog/analyze-new-dialog.component.scss")]
        }),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__param"](3, Object(_angular_core__WEBPACK_IMPORTED_MODULE_1__["Inject"])(_angular_material__WEBPACK_IMPORTED_MODULE_2__["MAT_DIALOG_DATA"])),
        tslib__WEBPACK_IMPORTED_MODULE_0__["__metadata"]("design:paramtypes", [_services_analyze_dialog_service__WEBPACK_IMPORTED_MODULE_14__["AnalyzeDialogService"],
            _angular_material__WEBPACK_IMPORTED_MODULE_2__["MatDialogRef"],
            _common_pipes_filter_pipe__WEBPACK_IMPORTED_MODULE_16__["FilterPipe"], Object])
    ], AnalyzeNewDialogComponent);
    return AnalyzeNewDialogComponent;
}());



/***/ }),

/***/ "./src/app/modules/analyze/view/new-dialog/index.ts":
/*!**********************************************************!*\
  !*** ./src/app/modules/analyze/view/new-dialog/index.ts ***!
  \**********************************************************/
/*! exports provided: AnalyzeNewDialogComponent */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony import */ var _analyze_new_dialog_component__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! ./analyze-new-dialog.component */ "./src/app/modules/analyze/view/new-dialog/analyze-new-dialog.component.ts");
/* harmony reexport (safe) */ __webpack_require__.d(__webpack_exports__, "AnalyzeNewDialogComponent", function() { return _analyze_new_dialog_component__WEBPACK_IMPORTED_MODULE_0__["AnalyzeNewDialogComponent"]; });




/***/ })

}]);
//# sourceMappingURL=modules-analyze-analyze-module.js.map