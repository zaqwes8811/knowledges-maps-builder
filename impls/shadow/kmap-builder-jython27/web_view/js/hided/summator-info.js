/**
	abs. : ������ ������ ��� ������������ �������� ������
	
	file : SummatorInfo.js
	
	thinks : 
		������ �������� ������ ������� ��������, ��� ��� ����� ������ ��� 
			��������� ��������� ������. �� ����� ��������, ��� ������ ����� ������� 
			���� ����� ������ � �� ��� ����������� ��� ������ ����������� �������
			
		����� ��������� ��� ������ � ����������� �������� ���� ��� ��������. ��� ������. ����. �������������
			�����?
			
	����� : ���������� ��������� �� ������ �����, ������� js...
		������� �������� ����������� ���, � ���������� � js �������� �� ������� ��������
*/
var SummatorInfo = (function() {
/** @private */
	// ���������� �������
	var _processingDataCallback;
	var _noOkHttpCode;
	
	// ������ ������ � ������� ��������
	var _initAjaxObj = new AjaxObj();
	
	// ������
	var _buttonIDs = [ "printButton" ];
	var _imagesPath = "/img/";
	
	var _onFirstResponseGetted = function (){
		// ������ ������ �������
		var arr = summary.LeftNamespace.getArrNextRequests();
		if ( arr.length > 0 ) { 
			var first = arr[ 0 ];
			first.doGetRequest(); // ������� �� �������
		}
	};
	

/** @public */
	return {
		// ���������� �� ������ �������
		onLoadFrameset : function () {
			_processingDataCallback = summary.LeftNamespace.processGettedData;
			_noOkHttpCode = summary.LeftNamespace.noConnect;
			
			// ������ ������ �� ������ �� ���� ������ ����� ��� ����� ������� �������
			// �������������� ������
			_initAjaxObj.update = _processingDataCallback;
			_initAjaxObj.noOkHttpCode = _noOkHttpCode;
			
			// �������� ����� ��� GET �������
			_initAjaxObj.setMainContainerId( 'main' );
			_initAjaxObj.setContainerId( 'leftPanelTest' );
			
			// ������ GET ������ - � ������� ��������
			_initAjaxObj.getData( CgiPages[ "ajx_lft" ] );
		},

		// ������� ������ !������
		callPrint : function () {
			// ������� ������������������
			parent.summary.focus();
			parent.summary.print();
		},
		
		// ��������� ������� �������� �������� c ������� ������
		onLoadBottom : function ( ) {
			// ������ ������
			tvct.buttonHovers( _buttonIDs, _imagesPath );
		},
		
		// ������ � ������� �������� ��������
		firstDone : function( canDoSerialRequest, gettedStr ) {
			// ����� �� ��������� ����� ��������
			if ( canDoSerialRequest ) {
				_onFirstResponseGetted();
			}
		},
		
		// ������
		otherDone : function ( canDoNewRequest, value ) {
			// ��������, �������� ������ ���� ��������!
			var arr = summary.LeftNamespace.getArrNextRequests();
			
			//���������� ������� ������ �� �������
			if ( canDoNewRequest ) {
				var last = arr.shift();
				
				// ��������� ��� ��������
				//last.preView( value );
				
				// ���������
				if ( arr.length > 0 ) {
					arr[0].doGetRequest();
				}
			}
		}
	};
})();