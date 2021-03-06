/* */ 
"format cjs";
"use strict";

var _interopRequireWildcard = function (obj) { if (obj && obj.__esModule) { return obj; } else { var newObj = {}; if (typeof obj === "object" && obj !== null) { for (var key in obj) { if (Object.prototype.hasOwnProperty.call(obj, key)) newObj[key] = obj[key]; } } newObj["default"] = obj; return newObj; } };

var _interopRequireDefault = function (obj) { return obj && obj.__esModule ? obj : { "default": obj }; };

var _classCallCheck = function (instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } };

var _createClass = (function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; })();

exports.__esModule = true;

var _PathHoister = require("./hoister");

var _PathHoister2 = _interopRequireDefault(_PathHoister);

var _import = require("./virtual-types");

var virtualTypes = _interopRequireWildcard(_import);

var _isBoolean = require("lodash/lang/isBoolean");

var _isBoolean2 = _interopRequireDefault(_isBoolean);

var _isNumber = require("lodash/lang/isNumber");

var _isNumber2 = _interopRequireDefault(_isNumber);

var _isRegExp = require("lodash/lang/isRegExp");

var _isRegExp2 = _interopRequireDefault(_isRegExp);

var _isString = require("lodash/lang/isString");

var _isString2 = _interopRequireDefault(_isString);

var _codeFrame = require("../../helpers/code-frame");

var _codeFrame2 = _interopRequireDefault(_codeFrame);

var _parse = require("../../helpers/parse");

var _parse2 = _interopRequireDefault(_parse);

var _explode = require("../visitors");

var _traverse2 = require("../index");

var _traverse3 = _interopRequireDefault(_traverse2);

var _includes = require("lodash/collection/includes");

var _includes2 = _interopRequireDefault(_includes);

var _assign = require("lodash/object/assign");

var _assign2 = _interopRequireDefault(_assign);

var _extend = require("lodash/object/extend");

var _extend2 = _interopRequireDefault(_extend);

var _Scope = require("../scope");

var _Scope2 = _interopRequireDefault(_Scope);

var _import2 = require("../../types");

var t = _interopRequireWildcard(_import2);

var hoistVariablesVisitor = _explode.explode({
  Function: function Function() {
    this.skip();
  },

  VariableDeclaration: function VariableDeclaration(node, parent, scope) {
    if (node.kind !== "var") return;

    var bindings = this.getBindingIdentifiers();
    for (var key in bindings) {
      scope.push({ id: bindings[key] });
    }

    var exprs = [];

    for (var i = 0; i < node.declarations.length; i++) {
      var declar = node.declarations[i];
      if (declar.init) {
        exprs.push(t.expressionStatement(t.assignmentExpression("=", declar.id, declar.init)));
      }
    }

    return exprs;
  }
});

var TraversalPath = (function () {
  function TraversalPath(parent, container) {
    _classCallCheck(this, TraversalPath);

    this.container = container;
    this.parent = parent;
    this.data = {};
  }

  /**
   * Description
   */

  TraversalPath.get = function get(parentPath, context, parent, container, key, file) {
    var targetNode = container[key];
    var paths = container._paths = container._paths || [];
    var path;

    for (var i = 0; i < paths.length; i++) {
      var pathCheck = paths[i];
      if (pathCheck.node === targetNode) {
        path = pathCheck;
        break;
      }
    }

    if (!path) {
      path = new TraversalPath(parent, container);
      paths.push(path);
    }

    path.setContext(parentPath, context, key, file);

    return path;
  };

  /**
   * Description
   */

  TraversalPath.getScope = function getScope(path, scope, file) {
    var ourScope = scope;

    // we're entering a new scope so let's construct it!
    if (path.isScope()) {
      ourScope = new _Scope2["default"](path, scope, file);
    }

    return ourScope;
  };

  /**
   * Description
   */

  TraversalPath.prototype.getAncestry = function getAncestry() {
    var ancestry = [];

    var path = this.parentPath;
    while (path) {
      ancestry.push(path.node);
      path = path.parentPath;
    }

    return ancestry;
  };

  /**
   * Description
   */

  TraversalPath.prototype.queueNode = function queueNode(path) {
    if (this.context) {
      this.context.queue.push(path);
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype.insertBefore = function insertBefore(nodes) {
    nodes = this._verifyNodeList(nodes);

    if (this.parentPath.isExpressionStatement() || this.parentPath.isLabeledStatement()) {
      return this.parentPath.insertBefore(nodes);
    } else if (this.isPreviousType("Expression") || this.parentPath.isForStatement() && this.key === "init") {
      if (this.node) nodes.push(this.node);
      this.replaceExpressionWithStatements(nodes);
    } else if (this.isPreviousType("Statement") || !this.type) {
      this._maybePopFromStatements(nodes);
      if (Array.isArray(this.container)) {
        this._containerInsertBefore(nodes);
      } else if (this.isStatementOrBlock()) {
        if (this.node) nodes.push(this.node);
        this.container[this.key] = t.blockStatement(nodes);
        this.checkSelf();
      } else {
        throw new Error("We don't know what to do with this node type. We were previously a Statement but we can't fit in here?");
      }
    } else {
      throw new Error("No clue what to do with this node type.");
    }
  };

  TraversalPath.prototype._containerInsert = function _containerInsert(from, nodes) {
    this.updateSiblingKeys(from, nodes.length);

    var paths = [];

    for (var i = 0; i < nodes.length; i++) {
      var to = from + i;
      var node = nodes[i];
      this.container.splice(to, 0, node);

      if (this.context) {
        var path = this.context.create(this.parent, this.container, to);
        paths.push(path);
        this.queueNode(path);
      } else {
        paths.push(TraversalPath.get(this, null, node, this.container, to));
      }
    }

    this.checkPaths(paths);
  };

  TraversalPath.prototype._containerInsertBefore = function _containerInsertBefore(nodes) {
    this._containerInsert(this.key, nodes);
  };

  TraversalPath.prototype._containerInsertAfter = function _containerInsertAfter(nodes) {
    this._containerInsert(this.key + 1, nodes);
  };

  TraversalPath.prototype._maybePopFromStatements = function _maybePopFromStatements(nodes) {
    var last = nodes[nodes.length - 1];
    if (t.isExpressionStatement(last) && t.isIdentifier(last.expression) && !this.isCompletionRecord()) {
      nodes.pop();
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype.isCompletionRecord = function isCompletionRecord() {
    var path = this;

    do {
      var container = path.container;
      if (Array.isArray(container) && path.key !== container.length - 1) {
        return false;
      }
    } while (path = path.parentPath && !path.isProgram());

    return true;
  };

  /**
   * Description
   */

  TraversalPath.prototype.isStatementOrBlock = function isStatementOrBlock() {
    if (t.isLabeledStatement(this.parent) || t.isBlockStatement(this.container)) {
      return false;
    } else {
      return _includes2["default"](t.STATEMENT_OR_BLOCK_KEYS, this.key);
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype.insertAfter = function insertAfter(nodes) {
    nodes = this._verifyNodeList(nodes);

    if (this.parentPath.isExpressionStatement() || this.parentPath.isLabeledStatement()) {
      return this.parentPath.insertAfter(nodes);
    } else if (this.isPreviousType("Expression") || this.parentPath.isForStatement() && this.key === "init") {
      if (this.node) {
        var temp = this.scope.generateTemp();
        nodes.unshift(t.expressionStatement(t.assignmentExpression("=", temp, this.node)));
        nodes.push(t.expressionStatement(temp));
      }
      this.replaceExpressionWithStatements(nodes);
    } else if (this.isPreviousType("Statement") || !this.type) {
      this._maybePopFromStatements(nodes);
      if (Array.isArray(this.container)) {
        this._containerInsertAfter(nodes);
      } else if (this.isStatementOrBlock()) {
        if (this.node) nodes.unshift(this.node);
        this.container[this.key] = t.blockStatement(nodes);
        this.checkSelf();
      } else {
        throw new Error("We don't know what to do with this node type. We were previously a Statement but we can't fit in here?");
      }
    } else {
      throw new Error("No clue what to do with this node type.");
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype.updateSiblingKeys = function updateSiblingKeys(fromIndex, incrementBy) {
    var paths = this.container._paths;
    for (var i = 0; i < paths.length; i++) {
      var path = paths[i];
      if (path.key >= fromIndex) {
        path.key += incrementBy;
      }
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype.setData = function setData(key, val) {
    return this.data[key] = val;
  };

  /**
   * Description
   */

  TraversalPath.prototype.getData = function getData(key, def) {
    var val = this.data[key];
    if (!val && def) val = this.data[key] = def;
    return val;
  };

  /**
   * Description
   */

  TraversalPath.prototype.setScope = function setScope(file) {
    var target = this.context || this.parentPath;
    this.scope = TraversalPath.getScope(this, target && target.scope, file);
  };

  /**
   * Description
   */

  TraversalPath.prototype.clearContext = function clearContext() {
    this.context = null;
  };

  /**
   * Description
   */

  TraversalPath.prototype.setContext = function setContext(parentPath, context, key, file) {
    this.shouldSkip = false;
    this.shouldStop = false;
    this.removed = false;

    this.parentPath = parentPath || this.parentPath;
    this.key = key;

    if (context) {
      this.context = context;
      this.state = context.state;
      this.opts = context.opts;
    }

    this.type = this.node && this.node.type;

    var log = file && this.type === "Program";
    if (log) file.log.debug("Start scope building");
    this.setScope(file);
    if (log) file.log.debug("End scope building");
  };

  TraversalPath.prototype._remove = function _remove() {
    if (Array.isArray(this.container)) {
      this.container.splice(this.key, 1);
      this.updateSiblingKeys(this.key, -1);
    } else {
      this.container[this.key] = null;
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype.remove = function remove() {
    this._remove();
    this.removed = true;

    var parentPath = this.parentPath;
    var parent = this.parent;
    if (!parentPath) return;

    // we've just removed the last declarator of a variable declaration so there's no point in
    // keeping it
    if (parentPath.isVariableDeclaration() && parent.declarations.length === 0) {
      return parentPath.remove();
    }

    // we're the child of an expression statement so we should remove the parent
    if (parentPath.isExpressionStatement()) {
      return parentPath.remove();
    }

    // we've just removed the second element of a sequence expression so let's turn that sequence
    // expression into a regular expression
    if (parentPath.isSequenceExpression() && parent.expressions.length === 1) {
      parentPath.replaceWith(parent.expressions[0]);
    }

    // we're in a binary expression, better remove it and replace it with the last expression
    if (parentPath.isBinary()) {
      if (this.key === "left") {
        parentPath.replaceWith(parent.right);
      } else {
        // key === "right"
        parentPath.replaceWith(parent.left);
      }
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype.skip = function skip() {
    this.shouldSkip = true;
  };

  /**
   * Description
   */

  TraversalPath.prototype.stop = function stop() {
    this.shouldStop = true;
    this.shouldSkip = true;
  };

  /**
   * Description
   */

  TraversalPath.prototype.errorWithNode = function errorWithNode(msg) {
    var Error = arguments[1] === undefined ? SyntaxError : arguments[1];

    var loc = this.node.loc.start;
    var err = new Error("Line " + loc.line + ": " + msg);
    err.loc = loc;
    return err;
  };

  /**
   * Description
   */

  TraversalPath.prototype.replaceInline = function replaceInline(nodes) {
    if (Array.isArray(nodes)) {
      if (Array.isArray(this.container)) {
        nodes = this._verifyNodeList(nodes);
        this._containerInsertAfter(nodes);
        return this.remove();
      } else {
        return this.replaceWithMultiple(nodes);
      }
    } else {
      return this.replaceWith(nodes);
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype._verifyNodeList = function _verifyNodeList(nodes) {
    if (nodes.constructor !== Array) {
      nodes = [nodes];
    }

    for (var i = 0; i < nodes.length; i++) {
      var node = nodes[i];
      if (!node) {
        throw new Error("Node list has falsy node with the index of " + i);
      } else if (typeof node !== "object") {
        throw new Error("Node list contains a non-object node with the index of " + i);
      } else if (!node.type) {
        throw new Error("Node list contains a node without a type with the index of " + i);
      }
    }

    return nodes;
  };

  /**
   * Description
   */

  TraversalPath.prototype.unshiftContainer = function unshiftContainer(containerKey, nodes) {
    nodes = this._verifyNodeList(nodes);

    // get the first path and insert our nodes before it, if it doesn't exist then it
    // doesn't matter, our nodes will be inserted anyway

    var container = this.node[containerKey];
    var path = TraversalPath.get(this, null, this.node, container, 0);

    return path.insertBefore(nodes);
  };

  /**
   * Description
   */

  TraversalPath.prototype.pushContainer = function pushContainer(containerKey, nodes) {
    nodes = this._verifyNodeList(nodes);

    // get an invisible path that represents the last node + 1 and replace it with our
    // nodes, effectively inlining it

    var container = this.node[containerKey];
    var i = container.length;
    var path = TraversalPath.get(this, null, this.node, container, i);

    return path.replaceWith(nodes, true);
  };

  /**
   * Description
   */

  TraversalPath.prototype.replaceWithMultiple = function replaceWithMultiple(nodes) {
    nodes = this._verifyNodeList(nodes);
    t.inheritsComments(nodes[0], this.node);
    this.container[this.key] = null;
    this.insertAfter(nodes);
    if (!this.node) this.remove();
  };

  /**
   * Description
   */

  TraversalPath.prototype.replaceWithSourceString = function replaceWithSourceString(replacement) {
    try {
      replacement = "(" + replacement + ")";
      replacement = _parse2["default"](replacement);
    } catch (err) {
      var loc = err.loc;
      if (loc) {
        err.message += " - make sure this is an expression.";
        err.message += "\n" + _codeFrame2["default"](replacement, loc.line, loc.column + 1);
      }
      throw err;
    }

    replacement = replacement.program.body[0].expression;
    _traverse3["default"].removeProperties(replacement);
    return this.replaceWith(replacement);
  };

  /**
   * Description
   */

  TraversalPath.prototype.replaceWith = function replaceWith(replacement, whateverAllowed) {
    if (this.removed) {
      throw new Error("You can't replace this node, we've already removed it");
    }

    if (!replacement) {
      throw new Error("You passed `path.replaceWith()` a falsy node, use `path.remove()` instead");
    }

    if (this.node === replacement) {
      return this.checkSelf();
    }

    // normalise inserting an entire AST
    if (t.isProgram(replacement)) {
      replacement = replacement.body;
      whateverAllowed = true;
    }

    if (Array.isArray(replacement)) {
      if (whateverAllowed) {
        return this.replaceWithMultiple(replacement);
      } else {
        throw new Error("Don't use `path.replaceWith()` with an array of nodes, use `path.replaceWithMultiple()`");
      }
    }

    if (typeof replacement === "string") {
      if (whateverAllowed) {
        return this.replaceWithSourceString(replacement);
      } else {
        throw new Error("Don't use `path.replaceWith()` with a string, use `path.replaceWithSourceString()`");
      }
    }

    // replacing a statement with an expression so wrap it in an expression statement
    if (this.isPreviousType("Statement") && t.isExpression(replacement)) {
      replacement = t.expressionStatement(replacement);
    }

    // replacing an expression with a statement so let's explode it
    if (this.isPreviousType("Expression") && t.isStatement(replacement)) {
      return this.replaceExpressionWithStatements([replacement]);
    }

    var oldNode = this.node;
    if (oldNode) t.inheritsComments(replacement, oldNode);

    // replace the node
    this.container[this.key] = replacement;
    this.type = replacement.type;

    // potentially create new scope
    this.setScope();

    this.checkSelf();
  };

  /**
   * Description
   */

  TraversalPath.prototype.checkSelf = function checkSelf() {
    this.checkPaths(this);
  };

  /**
   * Description
   */

  TraversalPath.prototype.checkPaths = function checkPaths(paths) {
    var scope = this.scope;
    var file = scope && scope.file;
    if (file) file.checkPath(paths);
  };

  /**
   * Description
   */

  TraversalPath.prototype.getStatementParent = function getStatementParent() {
    var path = this;

    do {
      if (!path.parentPath || Array.isArray(path.container) && path.isStatement()) {
        break;
      } else {
        path = path.parentPath;
      }
    } while (path);

    if (path && (path.isProgram() || path.isFile())) {
      throw new Error("File/Program node, we can't possibly find a statement parent to this");
    }

    return path;
  };

  /**
   * Description
   */

  TraversalPath.prototype.getLastStatements = function getLastStatements() {
    var paths = [];

    var add = function add(path) {
      if (path) paths = paths.concat(path.getLastStatements());
    };

    if (this.isIfStatement()) {
      add(this.get("consequent"));
      add(this.get("alternate"));
    } else if (this.isDoExpression()) {
      add(this.get("body"));
    } else if (this.isProgram() || this.isBlockStatement()) {
      add(this.get("body").pop());
    } else {
      paths.push(this);
    }

    return paths;
  };

  /**
   * Description
   */

  TraversalPath.prototype.replaceExpressionWithStatements = function replaceExpressionWithStatements(nodes) {
    var toSequenceExpression = t.toSequenceExpression(nodes, this.scope);

    if (toSequenceExpression) {
      return this.replaceWith(toSequenceExpression);
    } else {
      var container = t.functionExpression(null, [], t.blockStatement(nodes));
      container.shadow = true;

      // add implicit returns to all ending expression statements
      var last = this.getLastStatements();
      for (var i = 0; i < last.length; i++) {
        var lastNode = last[i];
        if (lastNode.isExpressionStatement()) {
          lastNode.replaceWith(t.returnStatement(lastNode.node.expression));
        }
      }

      this.replaceWith(t.callExpression(container, []));

      this.traverse(hoistVariablesVisitor);

      return this.node;
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype.call = function call(key) {
    var node = this.node;
    if (!node) return;

    var opts = this.opts;
    var fns = [].concat(opts[key]);

    if (opts[node.type]) {
      fns = fns.concat(opts[node.type][key]);
    }

    var _arr = fns;
    for (var _i = 0; _i < _arr.length; _i++) {
      var fn = _arr[_i];
      if (!fn) continue;

      // call the function with the params (node, parent, scope, state)
      var replacement = fn.call(this, node, this.parent, this.scope, this.state);
      if (replacement) this.replaceWith(replacement, true);

      if (this.shouldStop) break;
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype.isBlacklisted = function isBlacklisted() {
    var blacklist = this.opts.blacklist;
    return blacklist && blacklist.indexOf(this.node.type) > -1;
  };

  /**
   * Description
   */

  TraversalPath.prototype.visit = function visit() {
    if (this.isBlacklisted()) return false;
    if (this.opts.shouldSkip(this)) return false;

    this.call("enter");

    if (this.shouldSkip) {
      return this.shouldStop;
    }

    var node = this.node;
    var opts = this.opts;

    if (node) {
      if (Array.isArray(node)) {
        // traverse over these replacement nodes we purposely don't call exitNode
        // as the original node has been destroyed
        for (var i = 0; i < node.length; i++) {
          _traverse3["default"].node(node[i], opts, this.scope, this.state, this);
        }
      } else {
        _traverse3["default"].node(node, opts, this.scope, this.state, this);
        this.call("exit");
      }
    }

    return this.shouldStop;
  };

  /**
   * Description
   */

  TraversalPath.prototype.getSibling = function getSibling(key) {
    return TraversalPath.get(this.parentPath, null, this.parent, this.container, key, this.file);
  };

  /**
   * Description
   */

  TraversalPath.prototype.get = function get(key) {
    var parts = key.split(".");
    if (parts.length === 1) {
      // "foo"
      return this._getKey(key);
    } else {
      // "foo.bar"
      return this._getPattern(parts);
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype._getKey = function _getKey(key) {
    var _this = this;

    var node = this.node;
    var container = node[key];

    if (Array.isArray(container)) {
      // requested a container so give them all the paths
      return container.map(function (_, i) {
        return TraversalPath.get(_this, null, node, container, i);
      });
    } else {
      return TraversalPath.get(this, null, node, node, key);
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype._getPattern = function _getPattern(parts) {
    var path = this;
    for (var i = 0; i > parts.length; i++) {
      var part = parts[i];
      if (part === ".") {
        path = path.parentPath;
      } else {
        if (Array.isArray(path)) {
          path = path[part];
        } else {
          path = path.get(part);
        }
      }
    }
    return path;
  };

  /**
   * Description
   */

  TraversalPath.prototype.has = function has(key) {
    var val = this.node[key];
    if (val && Array.isArray(val)) {
      return !!val.length;
    } else {
      return !!val;
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype.is = function is(key) {
    return this.has(key);
  };

  /**
   * Description
   */

  TraversalPath.prototype.isnt = function isnt(key) {
    return !this.has(key);
  };

  /**
   * Description
   */

  TraversalPath.prototype.getTypeAnnotation = function getTypeAnnotation() {
    if (this.typeInfo) {
      return this.typeInfo;
    }

    var info = this.typeInfo = {
      inferred: false,
      annotation: null
    };

    var type = this.node.typeAnnotation;

    if (!type) {
      info.inferred = true;
      type = this.inferType(this);
    }

    if (type) {
      if (t.isTypeAnnotation(type)) type = type.typeAnnotation;
      info.annotation = type;
    }

    return info;
  };

  /**
   * Description
   */

  TraversalPath.prototype.resolve = function resolve() {
    if (this.isVariableDeclarator()) {
      if (this.get("id").isIdentifier()) {
        return this.get("init").resolve();
      } else {}
    } else if (this.isIdentifier()) {
      var binding = this.scope.getBinding(this.node.name);
      if (!binding || !binding.constant) return;

      // todo: take into consideration infinite recursion #1149
      return;

      if (binding.path === this) {
        return this;
      } else {
        return binding.path.resolve();
      }
    } else if (this.isMemberExpression()) {
      // this is dangerous, as non-direct target assignments will mutate it's state
      // making this resolution inaccurate

      var targetKey = this.toComputedKey();
      if (!t.isLiteral(targetKey)) return;
      var targetName = targetKey.value;

      var target = this.get("object").resolve();
      if (!target || !target.isObjectExpression()) return;

      var props = target.get("properties");
      for (var i = 0; i < props.length; i++) {
        var prop = props[i];
        if (!prop.isProperty()) continue;

        var key = prop.get("key");

        // { foo: obj }
        var match = prop.isnt("computed") && key.isIdentifier({ name: targetName });

        // { "foo": "obj" } or { ["foo"]: "obj" }
        match = match || key.isLiteral({ value: targetName });

        if (match) return prop.get("value");
      }
    } else {
      return this;
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype.inferType = function inferType(path) {
    path = path.resolve();
    if (!path) return;

    if (path.isRestElement() || path.parentPath.isRestElement() || path.isArrayExpression()) {
      return t.genericTypeAnnotation(t.identifier("Array"));
    }

    if (path.parentPath.isTypeCastExpression()) {
      return path.parentPath.node.typeAnnotation;
    }

    if (path.isTypeCastExpression()) {
      return path.node.typeAnnotation;
    }

    if (path.isObjectExpression()) {
      return t.genericTypeAnnotation(t.identifier("Object"));
    }

    if (path.isFunction()) {
      return t.identifier("Function");
    }

    if (path.isLiteral()) {
      var value = path.node.value;
      if (_isString2["default"](value)) return t.stringTypeAnnotation();
      if (_isNumber2["default"](value)) return t.numberTypeAnnotation();
      if (_isBoolean2["default"](value)) return t.booleanTypeAnnotation();
    }

    if (path.isCallExpression()) {
      var callee = path.get("callee").resolve();
      if (callee && callee.isFunction()) return callee.node.returnType;
    }
  };

  /**
   * Description
   */

  TraversalPath.prototype.isPreviousType = function isPreviousType(type) {
    return t.isType(this.type, type);
  };

  /**
   * Description
   */

  TraversalPath.prototype.isTypeGeneric = function isTypeGeneric(genericName) {
    var opts = arguments[1] === undefined ? {} : arguments[1];

    var typeInfo = this.getTypeAnnotation();
    var type = typeInfo.annotation;
    if (!type) return false;

    if (type.inferred && opts.inference === false) {
      return false;
    }

    if (!t.isGenericTypeAnnotation(type) || !t.isIdentifier(type.id, { name: genericName })) {
      return false;
    }

    if (opts.requireTypeParameters && !type.typeParameters) {
      return false;
    }

    return true;
  };

  /**
   * Description
   */

  TraversalPath.prototype.getBindingIdentifiers = function getBindingIdentifiers() {
    return t.getBindingIdentifiers(this.node);
  };

  /**
   * Description
   */

  TraversalPath.prototype.traverse = (function (_traverse) {
    function traverse(_x, _x2) {
      return _traverse.apply(this, arguments);
    }

    traverse.toString = function () {
      return _traverse.toString();
    };

    return traverse;
  })(function (visitor, state) {
    _traverse3["default"](this.node, visitor, this.scope, state, this);
  });

  /**
   * Description
   */

  TraversalPath.prototype.hoist = function hoist() {
    var scope = arguments[0] === undefined ? this.scope : arguments[0];

    var hoister = new _PathHoister2["default"](this, scope);
    return hoister.run();
  };

  /**
   * Match the current node if it matches the provided `pattern`.
   *
   * For example, given the match `React.createClass` it would match the
   * parsed nodes of `React.createClass` and `React["createClass"]`.
   */

  TraversalPath.prototype.matchesPattern = function matchesPattern(pattern, allowPartial) {
    var parts = pattern.split(".");

    // not a member expression
    if (!this.isMemberExpression()) return false;

    var search = [this.node];
    var i = 0;

    function matches(name) {
      var part = parts[i];
      return part === "*" || name === part;
    }

    while (search.length) {
      var node = search.shift();

      if (allowPartial && i === parts.length) {
        return true;
      }

      if (t.isIdentifier(node)) {
        // this part doesn't match
        if (!matches(node.name)) return false;
      } else if (t.isLiteral(node)) {
        // this part doesn't match
        if (!matches(node.value)) return false;
      } else if (t.isMemberExpression(node)) {
        if (node.computed && !t.isLiteral(node.property)) {
          // we can't deal with this
          return false;
        } else {
          search.push(node.object);
          search.push(node.property);
          continue;
        }
      } else {
        // we can't deal with this
        return false;
      }

      // too many parts
      if (++i > parts.length) {
        return false;
      }
    }

    return true;
  };

  _createClass(TraversalPath, [{
    key: "node",
    get: function () {
      if (this.removed) {
        return null;
      } else {
        return this.container[this.key];
      }
    },
    set: function (replacement) {
      throw new Error("Don't use `path.node = newNode;`, use `path.replaceWith(newNode)` or `path.replaceWithMultiple([newNode])`");
    }
  }]);

  return TraversalPath;
})();

exports["default"] = TraversalPath;

_assign2["default"](TraversalPath.prototype, require("./evaluation"));
_assign2["default"](TraversalPath.prototype, require("./conversion"));

var _loop = function (type) {
  if (type[0] === "_") return "continue";

  TraversalPath.prototype["is" + type] = function (opts) {
    return virtualTypes[type].checkPath(this, opts);
  };
};

for (var type in virtualTypes) {
  var _ret = _loop(type);

  if (_ret === "continue") continue;
}

var _arr2 = t.TYPES;

var _loop2 = function () {
  var type = _arr2[_i2];
  var typeKey = "is" + type;
  TraversalPath.prototype[typeKey] = function (opts) {
    return t[typeKey](this.node, opts);
  };
};

for (var _i2 = 0; _i2 < _arr2.length; _i2++) {
  _loop2();
}
module.exports = exports["default"];

// otherwise it's a request for a destructuring declarator and i'm not
// ready to resolve those just yet