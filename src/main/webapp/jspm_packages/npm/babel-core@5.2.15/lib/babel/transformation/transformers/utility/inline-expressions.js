/* */ 
"format cjs";
"use strict";

var _interopRequireWildcard = function (obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (typeof obj === "object" && obj !== null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj["default"] = obj; return newObj; } };

exports.__esModule = true;
exports.Expression = Expression;
exports.Identifier = Identifier;

var _import = require("../../../types");

var t = _interopRequireWildcard(_import);

var metadata = {
  optional: true
};

exports.metadata = metadata;

function Expression(node, parent, scope) {
  var res = this.evaluate();
  if (res.confident) return t.valueToNode(res.value);
}

function Identifier() {}

// override Expression