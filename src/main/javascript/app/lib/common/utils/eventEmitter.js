import isString from 'lodash/isString';
import isFunction from 'lodash/isFunction';
import isEmpty from 'lodash/isEmpty';
import forEach from 'lodash/forEach';
import isUndefined from 'lodash/isUndefined';

export default class EventEmitter {
  constructor() {
    this.$events = {};
  }

  on(eventName, fn, context) {
    if (isString(eventName) && !isEmpty(eventName) && isFunction(fn)) {
      if (!(eventName in this.$events)) {
        this.$events[eventName] = [];
      }

      const event = this.$events[eventName];
      const bindedFn = fn.bind(context || this);

      event.push(bindedFn);

      return () => {
        const idx = event.indexOf(bindedFn);

        if (idx !== -1) {
          event.splice(idx, 1);
        }
      };
    }
  }

  emit(eventName, ...args) {
    if (isString(eventName) && !isEmpty(eventName)) {
      if (eventName in this.$events) {
        forEach(this.$events[eventName], fn => {
          fn.apply(this, args);
        });
      }
    }
  }

  has(eventName) {
    return eventName in this.$events && !isEmpty(this.$events[eventName]);
  }

  clear(eventName, fn) {
    if (!isUndefined(eventName)) {
      if (isString(eventName) && eventName.length > 0) {
        if (isFunction(fn)) {
          const idx = this.$events[eventName].indexOf(fn);

          if (idx !== -1) {
            this.$events[eventName].splice(idx, 1);
          }
        } else {
          delete this.$events[eventName];
        }
      }
    } else {
      this.$events = {};
    }
  }
}
