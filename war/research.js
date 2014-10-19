
var protocols = {

  // On server-side exist Java class
  PathValue: function (page, gen, pos) {
    //this.user
    this.pageName = page;
    this.genName = gen;
    this.pointPos = pos;
  },

  DistributionElem: function(elem) {
    this.frequency = elem.frequency;
    this.enabled = elem.enabled;
  }
};

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

// Class
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
  this.userSummary = new UserSummary([]);
}

View.prototype.uploadFile = function () {
  // FIXME: http://stackoverflow.com/questions/166221/how-can-i-upload-files-asynchronously-with-jquery
  // Вроде бы трудно на голом jQ and Ajax - "Doing this kind of uploading hacks is not an enjoyable experience"
  // http://malsup.com/jquery/form/#ajaxForm
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
  var genNames = this.userSummary.getGenNames(currentPageName);
  _.each(genNames, function(e) { pageGens.append(new Option(e, e, true, true)); }); 
}

View.prototype.drawWordValue = function (word) {
  $("#word_holder_id").text(word);
}

View.prototype._markIsKnowIt = function() {
  // думается лучше выполнить синхронно, хотя если здесь, то все равно
  // http://stackoverflow.com/questions/133310/how-can-i-get-jquery-to-perform-a-synchronous-rather-than-asynchronous-ajax-re
  var page = this.getCurrentPageName();
  if (!page)
    return;

  var gen = this.getCurrentGenName();
  if (!gen)
    return;

  var pointPos = this.currentWordData.getPos();

  var point = new protocols.PathValue(page, gen, pointPos);

  this.dal.markIsDone(point);
}

// Actions
View.prototype.reload = function() {
  // Get user data
  var self = this;
  this.dal.getUserSummary(function(data) {
      self.userSummary.reset(JSON.parse(data));
      var pages = self.userSummary.getPageNames();
      self.resetPagesOptions(pages);
    });

  // FIXME: don't work in constructor
  $('#know_it').change(function() {
    if (self.currentWordData.isActive()) {
      var $this = $(this);
      if ($this.is(':checked')) {
        self._markIsKnowIt();
      }
    }
  });
}

View.prototype.onGetWordPackage = function () { 
  // Нужны еще данные - страница и имя генератора
  var self = this;

  //this._markIsKnowIt();  // FAKE

  // делаем запрос
  this.dal.getWordPkgAsync(function(data) {
      var v = JSON.parse(data);
      self.currentWordData.set(v);
      self.drawWordValue(v.word);

      // сбрасываем флаг "i know"
      $('#know_it').prop('checked', false);
    });
}

// Class
function PlotView(dal) { 
  this.dal = dal;
  this.store = {};
  this.previousPoint = null;
}

PlotView.prototype.onGetData = function () {
  var self = this;
  this.dal.getDistributionAsync(function(data) { 
    self.plot( $.parseJSON(data)); 
  });
}

PlotView.prototype.plot = function (distribution) {
  // FIXME: Нужно усреднять данные на отрезках, а через zoom увеличивать.
  var self = this;
  var allPoints = [];
  var disabledPoints = [];
  var numPoints = distribution.length;

  _.each(distribution, function(e, index) {
    var elem = new protocols.DistributionElem(e);  // FIXME: bad - можно и напрямую пользоваться
    self.store[index] = 'Position : ' + index;

    //if (elem.enabled)  // FAKE
      allPoints.push([index, elem.frequency]);

    if (!elem.enabled)
      disabledPoints.push([index, elem.frequency]);
  });

  // Функция рисования
  $.plot("#placeholder", [
    { data: allPoints, label: "frequency(x)"}, 
    { data: disabledPoints, label: "disabled", points: { show:true }, lines: {show: false}} ], 

    {
    series: {
      lines: {show: true},
      points: {show: false}
    },
    grid: {
      hoverable: true,  // FIXME: don't work
      clickable: true
    }
  });
}

PlotView.prototype._showTooltip = function (pos, item) {
  // Вывод подсказки по элементу
  if (item) {
    if (this.previousPoint != item.dataIndex) {

      this.previousPoint = item.dataIndex;

      $("#tooltip").remove();
      var x = item.datapoint[0].toFixed(2),
      y = item.datapoint[1].toFixed(2);

      this._drawTooltip(item.pageX, item.pageY, this.store[Math.floor(x)]);
    }
  } else {
    $("#tooltip").remove();
    this.previousPoint = null;            
  }
}

PlotView.prototype._drawTooltip = function (x, y, contents) {
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

// Zoom:
//   - До определенного момента кружочки не должны выключаться. 
// Например при zoom = 1, когда показываются все элементы.
PlotView.prototype.reset = function() {
  var self = this;
  this.previousPoint = null;
  $("#placeholder").bind("plothover", function (event, pos, item) {
    self._showTooltip(pos, item)
  });
}

// Ajax wrapper
function DataAccessLayer() { }

DataAccessLayer.prototype.onError = function (message) {
  alert(message);
}

DataAccessLayer.prototype.markIsDone = function (point) {
  var self = this;
  var uri = '/know_it';
  
  //$.get(uri, args)
  //  .error(function(data) { self.onError(data); });
  // FIXME: better sync()

  $.ajax({
    type: "PUT",
    url: uri,
    data : JSON.stringify(point)
  }).error(function(data) { self.onError(data); });;


}

DataAccessLayer.prototype.getWordPkgAsync = function (callback) {
  // делаем запрос
  var self = this;
  var uri = '/pkg';
  var args = {'name':'get_axis'};
  var _ = $.get(uri, args)
    .success(callback)
    .error(function(data) { self.onError(data); });
}

DataAccessLayer.prototype.getDistributionAsync = function (callback) {
  var self = this;
  var request_processor = '/research/get_distribution';
  var response_branch = {'name':'get_axis'};
  var jqxhr = $.get(request_processor, response_branch)
    .success(callback)
    .error(function(data) { self.onError(data); });
}

DataAccessLayer.prototype.getUserSummary = function (callback) {
  var self = this;
  // Get user data
  // Нужно по имени страницы получать список генераторов
  var uri = '/user_summary';
  var jqxhr = $.get(uri)
    .success(callback)
    .error(function(data) { self.onError(data); });
}

// State
// создаются до загрузки DOM?
var gDataAccessLayer = new DataAccessLayer();
var gView = new View(gDataAccessLayer);
var gPlotView = new PlotView(gDataAccessLayer);

$(function() {
  // Handler for .ready() called.
  gView.reload();
  gPlotView.reset();


  // http://hayageek.com/ajax-file-upload-jquery/
  // https://github.com/blueimp/jQuery-File-Upload/wiki/Basic-plugin - еще вариант
  var options = { 
    beforeSend: function() {
        $("#progress").show();
        //clear everything
        $("#bar").width('0%');
        $("#message").html("");
        $("#percent").html("0%");
    },
    uploadProgress: function(event, position, total, percentComplete) {
        $("#bar").width(percentComplete+'%');
        $("#percent").html(percentComplete+'%');
    },
    success: function() {
        $("#bar").width('100%');
        $("#percent").html('100%');
 
    },
    complete: function(response) {
        // FIXME: можно же в принципе ничего не выводить?
        //$("#message").html("<font color='green'>"+response.responseText+"</font>");

        // FIXME: обновить бы данные страницы - списки страниц и генераторов
    },
    error: function() {
        $("#message").html("<font color='red'> ERROR: unable to upload files</font>");
    }
  }; 
 
  $("#myForm").ajaxForm(options);

});