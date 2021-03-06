var dal = dal || {};

// TODO: think try CoffeeScript
// FIXME: 
var protocols = {

  // On server-side exist Java class
  PathValue: function (page, gen, pos) {
    //this.user
    this.pageName = page;
    this.genName = gen;
    this.pointPos = pos;
  },

  DistributionElem: function(elem) {
  // http://stackoverflow.com/questions/135448/how-do-i-check-to-see-if-an-object-has-a-property-in-javascript
  //if (!_.has(elem, 'importancy')) {
  //  throw new UserException("No such property" + 'importancy');
  //}
    this.frequency = elem.importance;
    this.enabled = elem.unknown;  // плохо - может быть unknown
    this.inBoundary = elem.inBoundary;
  },

  TextPackage: function(name, text) {   
    this.name = name;
    this.text = text;
  }
};

// Ajax wrapper
//dal. 
function DataAccessLayer() { }

DataAccessLayer.prototype.onError = function (message) {
  alert("error: " + message);
}

// FIXME: Danger! Data races!! На сервер запросы приходят в случайном порядке!
// FIXME: add callbacks of futures
// http://stackoverflow.com/questions/26625671/rest-without-put
DataAccessLayer.prototype.markIsDone = function (point, successHandler, errorHandler) {
  var self = this;
  var uri = '/mark-known-and-get-new-word';
  $.ajax({
    type: "PUT",
    url: uri,
    data : JSON.stringify(point)
  })
  .success(successHandler)
  .error(errorHandler);
}

DataAccessLayer.prototype.putPage = function (page, done, error) {
  var self = this;
  var uri = '/research/accept_text';
  $.ajax({
    type: "POST",
    url: uri,
    data : JSON.stringify(page)
  })
  .success(done)
  .error(error);
}

// FIXME: нужны параметры
DataAccessLayer.prototype.getWordPkgAsync = function (successHandler, errorHandler, arg0) {
  // делаем запрос
  var self = this;
  var uri = '/pkg';  // FIXME: looks like shit
  var args = {'arg0': JSON.stringify(arg0)};
  $.get(uri, args)
    .success(successHandler)
    .error(errorHandler);
}

DataAccessLayer.prototype.getDistributionAsync = function (callback, arg0) {
  var self = this;
  var uri = '/research/get_distribution';
  var args = {'arg0': JSON.stringify(arg0)};
  $.get(uri, args)
    .success(callback)
    .error(function(data) { self.onError(data); });
}

DataAccessLayer.prototype.getUserSummary = function (successHandler, errorHandler) {
  var self = this;
  // Get user data
  // Нужно по имени страницы получать список генераторов
  var uri = '/user_summary';
  $.get(uri)
    .success(successHandler)
    .error(errorHandler);
}

DataAccessLayer.prototype.resetFullStore =  function () {
  var self = this;
  // Get user data
  // Нужно по имени страницы получать список генераторов
  var uri = '/reset_storage';
  $.get(uri)
    .error(function(data) { self.onError(data); });
}