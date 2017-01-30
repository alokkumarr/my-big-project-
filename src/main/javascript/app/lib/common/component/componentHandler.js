class ComponentHandler {
  constructor($eventHandler) {
    this._instances = {};

    this.events = $eventHandler;
  }

  register(key, instance) {
    if (key && instance) {
      let coll = this._instances[key];

      if (!coll) {
        coll = this._instances[key] = [];
      }

      coll.push(instance);

      this.events.emit('$onInstanceAdded', {
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

          this.events.emit('$onInstanceRemoved', {
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
}

export default $eventHandler => {
  'ngInject';
  return new ComponentHandler($eventHandler);
};
