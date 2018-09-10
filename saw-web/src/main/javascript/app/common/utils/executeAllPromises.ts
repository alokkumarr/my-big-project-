export function executeAllPromises(promises) {
  // Wrap all Promises in a Promise that will always "resolve"
  const resolvingPromises = promises.map(promise => {
    return new Promise(resolve => {
      const payload = new Array(2);
      promise.then(result => {
        payload[0] = result;
      })
      .catch(error => {
        payload[1] = error;
      })
       /*
          * The wrapped Promise returns an array:
          * The first position in the array holds the result (if any)
          * The second position in the array holds the error (if any)
          */
      .then(() => resolve(payload));
    });
  });

  const results = [];

  // Execute all wrapped Promises
  return Promise.all(resolvingPromises)
    .then(items => {
      items.forEach(([result, error]) => {
        if (result) {
          results.push({result});
        } else {
          results.push({error});
        }
      });

      return results;
    });
}
