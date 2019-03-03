//========================================================

function DictResearch() 
{
	this.fn = "";
}

DictResearch.prototype.onUploadTextFile = function () 
{
	if (!this.fn) {
		return;    
	}

	var reader = new FileReader();
	var self = this;
	reader.onload = function(e) {
		var text = reader.result;
		var page = new protocols.TextPackage(this.fn, text);
		self.sendPage(page);
	};

	reader.readAsText(this.fn);
}

DictResearch.prototype.setFn = function (fn) 
{
	this.fn = fn;
}

DictResearch.prototype.sendPage = function(page) 
{
	var self = this;

	var error = function(e) 
	{
		// try {
		// 	var m = gMessageBuilder.buildError('Error occure, Master');
		// 	self.RemoveAfter(m, 2);
		// 	self.log.push(m);
		// } catch (ex) {
		//   // https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Statements/try...catch
		//   //if (e if e instanceof RangeError)
		// }
	};

	var done = function(data) 
	{
		// var m = gMessageBuilder.buildInfo('Done, Master');
		// self.RemoveAfter(m, 2);
		// self.log.push(m);
		// self.UpdateUserInfo();
	};

	$.ajax({
		type: "POST",
		url: '/research/accept_dict',
		data : JSON.stringify(page)
	})
	.success(done)
	.error(error);
}

//========================================================

var gDictResearch = new DictResearch();

$(function() {
	$("#fileInput").change(function(e) {
		var fileInput = $("#fileInput");
  		var fn = fileInput[0].files[0];
		gDictResearch.setFn(fn);
	})
});