/* */ 
"format cjs";
"use strict";

var _interopRequireWildcard = function (obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (typeof obj === "object" && obj !== null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj["default"] = obj; return newObj; } };

var _interopRequireDefault = function (obj) { return obj && obj.__esModule ? obj : { "default": obj }; };

exports.__esModule = true;

var _explode = require("./explode-assignable-expression");

var _explode2 = _interopRequireDefault(_explode);

var _import = require("../../types");

var t = _interopRequireWildcard(_import);

exports["default"] = function (exports, opts) {
  var buildAssignment = function buildAssignment(left, right) {
    return t.assignmentExpression("=", left, right);
  };

  exports.ExpressionStatement = function (node, parent, scope, file) {
    // hit the `AssignmentExpression` one below
    if (this.isCompletionRecord()) return;

    var expr = node.expression;
    if (!opts.is(expr, file)) return;

    var nodes = [];

    var exploded = _explode2["default"](expr.left, nodes, file, scope);

    nodes.push(t.ifStatement(opts.build(exploded.uid, file), t.expressionStatement(buildAssignment(exploded.ref, expr.right))));

    return nodes;
  };

  exports.AssignmentExpression = function (node, parent, scope, file) {
    if (!opts.is(node, file)) return;

    var nodes = [];
    var exploded = _explode2["default"](node.left, nodes, file, scope);

    nodes.push(t.logicalExpression("&&", opts.build(exploded.uid, file), buildAssignment(exploded.ref, node.right)));

    // todo: duplicate expression node
    nodes.push(exploded.ref);

    return nodes;
  };
};

;
module.exports = exports["default"];