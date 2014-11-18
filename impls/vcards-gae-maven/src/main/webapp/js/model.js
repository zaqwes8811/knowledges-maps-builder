
// FIXME: в jQuery есть похоже такая функция
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
  //this.markedToKnow = false;  // FIXME: connect it
}

CurrentWordData.prototype.set = function (data) {
  this.data = data;
}

CurrentWordData.prototype.getImportance = function () {
  return this.data.importance;
}

CurrentWordData.prototype.getPos = function () {
  return this.data.pointPos;
}

CurrentWordData.prototype.isActive = function () {
  return !isEmpty(this.data);
}

//CurrentWordData



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