/**
 * FileController
 *
 * @description :: Server-side actions for handling incoming requests.
 * @help        :: See https://sailsjs.com/docs/concepts/actions
 */

module.exports = {
   get: function (req, res) {
		res.sendfile(".tmp/uploads/" + req.query.path);
  },
  _config: {
    rest: false,
    shortcuts: false
  }
};
