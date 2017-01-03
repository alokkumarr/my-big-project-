import isString from 'lodash/isString';
import isFunction from 'lodash/isFunction';
import forEach from 'lodash/forEach';
import isUndefined from 'lodash/isUndefined';

export class EventHandler {
  constructor($q) {
    this._$q = $q;
    this._handlers = {};
  }

  on(eventName, fn, context) {
    if (isString(eventName) && eventName.length > 0 && isFunction(fn)) {
      if (!this._handlers.hasOwnProperty(eventName)) {
        this._handlers[eventName] = [];
      }

      const event = this._handlers[eventName];
      const bindedFn = fn.bind(context || this);

      event.push(bindedFn);

      return function removeHandler() {
        const idx = event.indexOf(bindedFn);

        if (idx !== -1) {
          event.splice(idx, 1);
        }
      };
    }
  }

  emit(eventName) {
    const promises = [];

    if (isString(eventName) && eventName.length > 0) {
      if (this._handlers.hasOwnProperty(eventName)) {
        const args = [];

        Array.prototype.push.apply(args, arguments);
        args.shift();

        forEach(this._handlers[eventName], function (fn) {
          const result = fn.apply(this, args);

          if (result) {
            const isPromise = isFunction(result.then);

            if (isPromise) {
              promises.push(result);
            }
          }
        });
      }
    }

    return this._$q.all(promises);
  }

  has(eventName) {
    return this._handlers[eventName] && (this._handlers[eventName]).length > 0;
  }

  clear(eventName, fn) {
    if (!isUndefined(eventName)) {
      if (isString(eventName) && eventName.length > 0) {
        if (isFunction(fn)) {
          const idx = this._handlers[eventName].indexOf(fn);

          if (idx !== -1) {
            this._handlers[eventName].splice(idx, 1);
          }
        } else {
          delete this._handlers[eventName];
        }
      }
    } else {
      this._handlers = {};
    }
  }
}

export default ($q) => {
  'ngInject';
  return new EventHandler($q);
};
