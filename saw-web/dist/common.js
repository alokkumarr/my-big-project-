(window["webpackJsonp"] = window["webpackJsonp"] || []).push([["common"],{

/***/ "./src/app/common/utils/cron2Readable.ts":
/*!***********************************************!*\
  !*** ./src/app/common/utils/cron2Readable.ts ***!
  \***********************************************/
/*! exports provided: generateSchedule, convertToLocal */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
__webpack_require__.r(__webpack_exports__);
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "generateSchedule", function() { return generateSchedule; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "convertToLocal", function() { return convertToLocal; });
/* harmony import */ var cronstrue__WEBPACK_IMPORTED_MODULE_0__ = __webpack_require__(/*! cronstrue */ "./node_modules/cronstrue/dist/cronstrue.js");
/* harmony import */ var cronstrue__WEBPACK_IMPORTED_MODULE_0___default = /*#__PURE__*/__webpack_require__.n(cronstrue__WEBPACK_IMPORTED_MODULE_0__);
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_1__ = __webpack_require__(/*! lodash/isEmpty */ "./node_modules/lodash/isEmpty.js");
/* harmony import */ var lodash_isEmpty__WEBPACK_IMPORTED_MODULE_1___default = /*#__PURE__*/__webpack_require__.n(lodash_isEmpty__WEBPACK_IMPORTED_MODULE_1__);
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_2__ = __webpack_require__(/*! lodash/isUndefined */ "./node_modules/lodash/isUndefined.js");
/* harmony import */ var lodash_isUndefined__WEBPACK_IMPORTED_MODULE_2___default = /*#__PURE__*/__webpack_require__.n(lodash_isUndefined__WEBPACK_IMPORTED_MODULE_2__);
/* harmony import */ var lodash_isString__WEBPACK_IMPORTED_MODULE_3__ = __webpack_require__(/*! lodash/isString */ "./node_modules/lodash/isString.js");
/* harmony import */ var lodash_isString__WEBPACK_IMPORTED_MODULE_3___default = /*#__PURE__*/__webpack_require__.n(lodash_isString__WEBPACK_IMPORTED_MODULE_3__);
/* harmony import */ var moment_timezone__WEBPACK_IMPORTED_MODULE_4__ = __webpack_require__(/*! moment-timezone */ "./node_modules/moment-timezone/index.js");
/* harmony import */ var moment_timezone__WEBPACK_IMPORTED_MODULE_4___default = /*#__PURE__*/__webpack_require__.n(moment_timezone__WEBPACK_IMPORTED_MODULE_4__);





function generateSchedule(cronExpression, activeTab, timezone) {
    if (lodash_isUndefined__WEBPACK_IMPORTED_MODULE_2__(cronExpression) && lodash_isUndefined__WEBPACK_IMPORTED_MODULE_2__(activeTab)) {
        return '';
    }
    else if (activeTab === 'immediate') {
        // cronExpression won't be present if it's an immediate scheduled entity.
        return '';
    }
    else if (!lodash_isString__WEBPACK_IMPORTED_MODULE_3__(cronExpression)) {
        throw new Error("generateSchedule expects a string as a first parameter, not: " + typeof cronExpression);
    }
    if (lodash_isEmpty__WEBPACK_IMPORTED_MODULE_1__(cronExpression)) {
        return '';
    }
    if (activeTab === 'hourly') {
        // there is no time stamp in hourly cron hence converting to utc and local is not required.
        return cronstrue__WEBPACK_IMPORTED_MODULE_0___default.a.toString(cronExpression);
    }
    var localCron = convertToLocal(cronExpression, timezone);
    return cronstrue__WEBPACK_IMPORTED_MODULE_0___default.a.toString(localCron);
}
function convertToLocal(cronUTC, timezone) {
    var splitArray = cronUTC.split(' ');
    var timeInLocal = moment_timezone__WEBPACK_IMPORTED_MODULE_4__["tz"](splitArray[1] + " " + splitArray[2], 'mm HH', timezone || 'Etc/GMT').toDate();
    var extractMinuteHour = moment_timezone__WEBPACK_IMPORTED_MODULE_4__(timeInLocal).format('mm HH').split(' ');
    splitArray[1] = extractMinuteHour[0];
    splitArray[2] = extractMinuteHour[1];
    return splitArray.join(' ');
}


/***/ })

}]);
//# sourceMappingURL=common.js.map