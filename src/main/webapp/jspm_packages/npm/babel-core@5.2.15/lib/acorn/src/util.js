/* */ 
"format cjs";
"use strict";

exports.__esModule = true;
exports.isArray = isArray;

// Checks if an object has a property.

exports.has = has;

function isArray(obj) {
  return Object.prototype.toString.call(obj) === "[object Array]";
}

function has(obj, propName) {
  return Object.prototype.hasOwnProperty.call(obj, propName);
}