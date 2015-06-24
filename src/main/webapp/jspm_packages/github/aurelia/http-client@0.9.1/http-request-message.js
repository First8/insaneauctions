/* */ 
System.register(['./headers', './request-message-processor', './transformers'], function (_export) {
  'use strict';

  var Headers, RequestMessageProcessor, timeoutTransformer, credentialsTransformer, progressTransformer, responseTypeTransformer, headerTransformer, contentTransformer, HttpRequestMessage;

  _export('createHttpRequestMessageProcessor', createHttpRequestMessageProcessor);

  function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError('Cannot call a class as a function'); } }

  function createHttpRequestMessageProcessor() {
    return new RequestMessageProcessor(XMLHttpRequest, [timeoutTransformer, credentialsTransformer, progressTransformer, responseTypeTransformer, contentTransformer, headerTransformer]);
  }

  return {
    setters: [function (_headers) {
      Headers = _headers.Headers;
    }, function (_requestMessageProcessor) {
      RequestMessageProcessor = _requestMessageProcessor.RequestMessageProcessor;
    }, function (_transformers) {
      timeoutTransformer = _transformers.timeoutTransformer;
      credentialsTransformer = _transformers.credentialsTransformer;
      progressTransformer = _transformers.progressTransformer;
      responseTypeTransformer = _transformers.responseTypeTransformer;
      headerTransformer = _transformers.headerTransformer;
      contentTransformer = _transformers.contentTransformer;
    }],
    execute: function () {
      HttpRequestMessage = function HttpRequestMessage(method, url, content, headers) {
        _classCallCheck(this, HttpRequestMessage);

        this.method = method;
        this.url = url;
        this.content = content;
        this.headers = headers || new Headers();
        this.responseType = 'json';
      };

      _export('HttpRequestMessage', HttpRequestMessage);
    }
  };
});