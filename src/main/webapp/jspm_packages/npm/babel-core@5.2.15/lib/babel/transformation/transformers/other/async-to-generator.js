/* */ 
"format cjs";
"use strict";

var _interopRequireDefault = function (obj) { return obj && obj.__esModule ? obj : { "default": obj }; };

exports.__esModule = true;

var _remapAsyncToGenerator = require("../../helpers/remap-async-to-generator");

var _remapAsyncToGenerator2 = _interopRequireDefault(_remapAsyncToGenerator);

var _manipulateOptions = require("./bluebird-coroutines");

exports.manipulateOptions = _manipulateOptions.manipulateOptions;
var metadata = {
  optional: true
};

exports.metadata = metadata;
exports.Function = function (node, parent, scope, file) {
  if (!node.async || node.generator) return;

  return _remapAsyncToGenerator2["default"](node, file.addHelper("async-to-generator"), scope);
};