/* */ 
"format cjs";
"use strict";

var _interopRequireWildcard = function (obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (typeof obj === "object" && obj !== null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj["default"] = obj; return newObj; } };

exports.__esModule = true;
exports.Program = Program;

var _import = require("../../helpers/strict");

var strict = _interopRequireWildcard(_import);

function Program(program, parent, scope, file) {
  this.stop();

  strict.wrap(program, function () {
    program.body = file.dynamicImports.concat(program.body);
  });

  if (!file.transformers["es6.modules"].canTransform()) return;

  if (file.moduleFormatter.transform) {
    file.moduleFormatter.transform(program);
  }
}