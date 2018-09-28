import { Injectable } from '@angular/core';
import EventEmitter from '../utils//eventEmitter';

@Injectable()
export class ComponentHandler {
  _instances: Object = {};
  _$eventEmitter = new EventEmitter();

  register(key, instance) {
    if (key && instance) {
      let coll = this._instances[key];

      if (!coll) {
        coll = [];
        this._instances[key] = coll;
      }

      coll.push(instance);

      this.$emit('$onInstanceAdded', {
        key,
        instance
      });
    }

    return () => {
      return this.unregister(key, instance);
    };
  }

  unregister(key, instance) {
    if (key && instance) {
      const coll = this._instances[key];

      if (coll) {
        const idx = coll.indexOf(instance);

        if (idx !== -1) {
          coll.splice(idx, 1);

          if (coll.length === 0) {
            delete this._instances[key];
          }

          this.$emit('$onInstanceRemoved', {
            key,
            instance
          });

          return true;
        }
      }
    }

    return false;
  }

  get(key) {
    return (this._instances[key] || []).slice();
  }

  on(...args) {
    return this._$eventEmitter.on.apply(this._$eventEmitter, args);
  }

  $emit(...args) {
    this._$eventEmitter.emit.apply(this._$eventEmitter, args);
  }
}