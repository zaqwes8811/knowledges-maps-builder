// State
var gUserSummary = new UserSummary([]);
var gDataAccessLayer = new DataAccessLayer();
var gView = new View(gDataAccessLayer);
var gPlotView = new PlotView(gDataAccessLayer);

function Point(page, gen, pos) {
  this.page = page;
  this.gen = gen;
  this.pos = pos;
}

// Speed up calls to hasOwnProperty
var hasOwnProperty = Object.prototype.hasOwnProperty;

function isEmpty(obj) {

    // null and undefined are "empty"
    if (obj == null) return true;

    // Assume if it has a length property with a non-zero value
    // that that property is correct.
    if (obj.length > 0)    return false;
    if (obj.length === 0)  return true;

    // Otherwise, does it have any properties of its own?
    // Note that this doesn't handle
    // toString and valueOf enumeration bugs in IE < 9
    for (var key in obj) {
        if (hasOwnProperty.call(obj, key)) return false;
    }

    return true;
}

// http://stackoverflow.com/questions/4994201/is-object-empty
function CurrentWordData() {
  this.data = {};
}

CurrentWordData.prototype.set = function (data) {
  this.data = data;
}

CurrentWordData.prototype.getPos = function () {
  return this.data.pointPos;
}

CurrentWordData.prototype.isActive = function () {
  return !isEmpty(this.data);
}

// Class
function UserSummary(listPagesSum) {
  this.raw = listPagesSum;
}

UserSummary.prototype.reset = function (listPagesSum) {
  this.raw = listPagesSum;
}

UserSummary.prototype.getGenNames = function (_pageName) {
  // ищем по списку
  var r = _.findWhere(this.raw, {pageName: _pageName});
  return r.genNames;
}

UserSummary.prototype.getPageNames = function () {
  return _.pluck(this.raw, 'pageName');
}

// Class
// http://www.electrictoolbox.com/jquery-add-option-select-jquery/
// http://stackoverflow.com/questions/47824/how-do-you-remove-all-the-options-of-a-select-box-and-then-add-one-option-and-se
function View(dal) {
  this.dal = dal;
  this.currentWordData = new CurrentWordData();

  var that = this;

  //var f = ;

  // init know checkbox 
  
}

View.prototype.getCurrentPageName = function () {
  return $('#pages > option:selected').text();
}

View.prototype.getCurrentGenName = function() {
  return $('#pageGenerators > option:selected').text();
};

View.prototype.resetPagesOptions = function(newNames) {
  var pageSelect = $('#pages');
  pageSelect.empty();

  var pageGens = $('#pageGenerators');
  pageGens.empty();
  
  _.each(newNames, function(e) { pageSelect.append(new Option(e, e, true, true)); });  
  
  var currentPageName = this.getCurrentPageName();
  var genNames = gUserSummary.getGenNames(currentPageName);
  _.each(genNames, function(e) { pageGens.append(new Option(e, e, true, true)); }); 
}

View.prototype.drawWordValue = function (word) {
  $("#word_holder_id").text(word);
}

View.prototype._markIsKnowIt = function(context) {
  var that = this;
  if (that.currentWordData.isActive()) {
    // this represents the checkbox that was checked
    // do something with it
    var $this = context;
    if ($this.is(':checked')) {
      // думается лучше выполнить синхронно, хотя если здесь, то все равно
      // http://stackoverflow.com/questions/133310/how-can-i-get-jquery-to-perform-a-synchronous-rather-than-asynchronous-ajax-re
      var page = that.getCurrentPageName();
      if (!page)
        return;

      var gen = that.getCurrentGenName();
      if (!gen)
        return;

      var pointPos = that.currentWordData.getPos();

      var point = new Point(page, gen, pointPos);

      that.dal.markIsDone(point);
    }
  }
}

// Actions
View.prototype.onCreate = function() {
  // Get user data
  var that = this;
  this.dal.getUserSummary(function(data) {
      gUserSummary.reset(JSON.parse(data));
      var pages = gUserSummary.getPageNames();
      that.resetPagesOptions(pages);
    });

  // FIXME: don't work
  $('#know_it').change(function() {
    that._markIsKnowIt($(this));
  });
}

View.prototype.onGetWordPackage = function () { 
  // Нужны еще данные - страница и имя генератора
  var that = this;

  // делаем запрос
  this.dal.getWordPkgAsync(function(data) {
      var v = JSON.parse(data);
      that.currentWordData.set(v);
      that.drawWordValue(v.word);
    });
}

// Class
function PlotView(dal) { 
  this.dal = dal;
  this.g_map = {};
}

PlotView.prototype.onGetData = function () {
  var that = this;
  this.dal.getDistributionAsync(function(data) { 
    that.plot(data); 
  });
}

PlotView.prototype.plot = function (data) {
  var getted_axises = $.parseJSON(data);
  
  // FIXME: Нужно усреднять данные на отрезках, а через zoom увеличивать.
  var zoomed_data = [];
  for(var i = 0; i < getted_axises.length; ++i) {
    tmp = []
    for(var name in getted_axises[i]) {
      if (getted_axises[i].hasOwnProperty(name)) {
        tmp.push(i);
        tmp.push(getted_axises[i][name]);
      }
      this.g_map[tmp[0]] = 'Position : '+tmp[0]+'/'+name+'/'+tmp[1]
    }
    zoomed_data.push(tmp);
  }

  // Функция рисования
  var _plot = $.plot("#placeholder", [{ data: zoomed_data, label: "distr(x)"}], {
    series: {
      lines: {show: true},
      points: {show: false}
    },
    grid: {
      hoverable: true,
      clickable: true
    }
  });
}

// Zoom:
//   - До определенного момента кружочки не должны выключаться. 
// Например при zoom = 1, когда показываются все элементы.
PlotView.prototype.reset = function() {
  function showTooltip(x, y, contents) {
    $("<div id='tooltip'>" + contents + "</div>").css({
      position: "absolute",
      display: "none",
      top: y + 5,
      left: x + 5,
      border: "1px solid #fdd",
      padding: "2px",
      "background-color": "#fee",
      opacity: 0.80
    }).appendTo("body").fadeIn(200);
  }

  var previousPoint = null;
  $("#placeholder").bind("plothover", function (event, pos, item) {
    // Вывод подсказки по элементу
    if (item) {
      if (previousPoint != item.dataIndex) {

        previousPoint = item.dataIndex;

        $("#tooltip").remove();
        var x = item.datapoint[0].toFixed(2),
        y = item.datapoint[1].toFixed(2);

        showTooltip(item.pageX, item.pageY, this.g_map[Math.floor(x)]);
      }
    } else {
      $("#tooltip").remove();
      previousPoint = null;            
    }
  });
}

// Ajax wrapper
function DataAccessLayer() { }

DataAccessLayer.prototype.onError = function (message) {
  alert(message);
}

DataAccessLayer.prototype.markIsDone = function (point) {
  var uri = '/pkg';
  var args = point;
  $.get(uri, args)
    .error(function(data) { this.onError(data); });
  // FIXME: better sync()
}

DataAccessLayer.prototype.getWordPkgAsync = function (callback) {
    // делаем запрос
  var uri = '/pkg';
  var args = {'name':'get_axis'};
  var _ = $.get(uri, args)
    .success(callback)
    .error(function(data) { this.onError(data); });
}

DataAccessLayer.prototype.getDistributionAsync = function (callback) {
  var request_processor = '/research/get_distribution';
  var response_branch = {'name':'get_axis'};
  var jqxhr = $.get(request_processor, response_branch)
    .success(callback)
    .error(function(data) { gDataAccessLayer.onError(data); });
}

DataAccessLayer.prototype.getUserSummary = function (callback) {
  // Get user data
  // Нужно по имени страницы получать список генераторов
  var uri = '/user_summary';
  var jqxhr = $.get(uri)
    .success(callback)
    .error(function(data) { this.onError(data); });
}

$(function() {
  // Handler for .ready() called.
  gView.onCreate();
  gPlotView.reset();


});