/**
	abs. : ���������� ���� ��� left.htm
	
	file : LeftNamespace.js
*/
var LeftNamespace = (function () { 
/** @private */
	var _bOn;
	var _bUnLock;
	var _bFail;
	var _bFirst;	// ��� �� ����?
	var signLoading = '<table id="loadProgress"><tr><td><b>���������...<b></td></tr></table>';
		
	// ���� ��������� ������� �����������
	function _doIfLast() { if( _arr.length === 1) _removeLoadingBar(); }
	
	// �������� �����
	function _insertLoadingBar () {	$( signLoading ).insertBefore($('#leftData')); }
	function _removeLoadingBar () { 
		
	$('#loadProgress').hide(); 
	//$("#loadProgress").toggle("slow");
		//alert('block')
	//setTimeout(function() {
    //    $("#loadProgress").remove();
    //}, 3000);
	
	//setTimeout(
         //       function() {
         //           document.getElementById("loadProgress").style.display = 'none';
         //       },
         //       5000
         //   );
	
	}
	function _clear_all( ) { _bFirst=1; }
	
	// ������� ����������� ��������
	var _arr = [];

	// ��������� ��� ��������� ������� ����� - �� ��������!
	function _lastStepProcessing( gettedStr ) {
		var canDoNewRequest = true;
		_doIfLast();	// �� ������ �������!
		parent.SummatorInfo.otherDone( canDoNewRequest, gettedStr );
	}
	
	//
	function _setProgressBar( msg, color ) {
		document.getElementById("loadProgress").innerHTML = msg;
		document.getElementById("loadProgress").style.backgroundColor=color;
	}
	
	// ������� ������
	function _inputDataFilter( gettedRawStr, posConnectionState ) {
		var check = gettedRawStr.split(" ");
		var resultArray = 'None';
		if (check[0]=="The") _setProgressBar( "������ ����� ������ � ���������� ��������", "#ffff00" );
		else {
			var curinput;
			var data = gettedRawStr.split("&");
			var cur = data[0].split("=");
			
			cur = data[ posConnectionState ].split("=");
			if ( parseInt(cur[1],10) == 3 ) _setProgressBar( "��� ������ �� RS", "#ff0000" ); 
			if ( parseInt(cur[1],10) == 0 )	_setProgressBar( "RS ������� ����������", "#ffff00");
			
			// ��� ��! ������ ������ � ���������
			if ( parseInt(cur[1],10) == 1 )	resultArray = data; 
		}
		return resultArray;
	}
	
	function _ajx_uniProcessing( number, gettedRawStr, inRmap, nameHeader, getHtmlCode ) {
		// ��������� ����������
		var data =_inputDataFilter( gettedRawStr, 1 );
		if( data !== 'None' ) {
			var rmap = inRmap;	
			var forSerial = [ 'conn' ];
				
			// ��������� ������
			_addAnyToMainPanel( nameHeader+getHtmlCode( number ) );
			
			// ��������� ��� ������
			for( var key = 0; key < forSerial.length; key++ ) {
				rmap[ forSerial[ key ] ]( data, number );
			}
		}
	}
	
	// ������ �������-�����������
	var AjaxCallbacks = {
		ajx_tmn: function ( gettedRawStr ) {
			var data =_inputDataFilter( gettedRawStr, 33 );
			if( data !== 'None' ) {
				// ! ����� ���������� ��������
				// ����� ��������� �����������
				var rmap = map_ajx_tmn;		

				// ��� ���������� �������� �����!
				var data = gettedRawStr.split( "&" );
			
				// ����� ����� �������, ����� ������ �������� ���� �������� ������?
				// �������� ��������� - ����� ����� ������� ����� ��� ���������, �������
				//   ������ ��������������� ������
				var forSerial = [ 'nonamed' ];
				for( var key = 0; key < forSerial.length; key++ ) {
					rmap[ forSerial[ key ] ]( data );
				}
				_lastStepProcessing( gettedRawStr );
			}
		}
	};
	
	var typedMap = {
		ajx_vtv: [ 
			'<br><center><h1>��� ',
			'</h1></center>', 
			map_ajx_vtv,
			getVtvParHtmlCode,
			true
		],
		ajx_vtvmod : [
			'',
			'',
			map_ajx_vtvmod,
			getVtvModHtmlCode,
			false
		],
		ajx_pab : [
			'<center><h1>��� ',
			'</h1></center>', 
			map_ajx_pab,
			getPABHtmlCode,
			true
		],
		ajx_bcn : [
			'<br><center><h1>��� ',
			'</h1></center>',
			map_ajx_bcn,
			getBCNHtmlCode,
			true,
		],
		ajx_bd : [
			'<br><center><h1>�� ',
			'</h1></center>',
			map_ajx_bd,
			getBDHtmlCode,
			true
		]
	};
	
	// ���������� ����������
	var _uniFill = function ( numBlocks, currentAjaxName ) {
		var i = 0;
		for ( i = 1; i < numBlocks+1 ; i++ ) {
			AjaxCallbacks[ currentAjaxName+i ] = function(x) { 
				return function( gettedRawStr ) { 
					var vector = typedMap[ currentAjaxName ];
					var header = vector[ 0 ] + x + vector[ 1 ];
					if( !vector[ 4 ] ) header = '';
					_ajx_uniProcessing( x, gettedRawStr, vector[ 2 ], header, vector[ 3 ] );
					_lastStepProcessing( gettedRawStr );
				} 
			}( i );
		}
	};
	
	// ������� ��������� ���
	var _NUM_VTV_MAIN = 2;
	_uniFill( _NUM_VTV_MAIN, 'ajx_vtv' );
	
	// ��������� ���������� ���
	var _NUM_VTV_MOD = 2;
	_uniFill( _NUM_VTV_MOD, 'ajx_vtvmod' );
	
	// ��������� ���������
	var _NUM_BUM = 9;
	_uniFill( _NUM_BUM, 'ajx_pab' );

	// ��������
	var _NUM_BCN = 1;
	_uniFill( _NUM_BCN, 'ajx_bcn' );

	// ���������
	var _NUM_BD = 2;
	_uniFill( _NUM_BD, 'ajx_bd' );
	
	
	// ��������� � ������ ���� 
	function _leftDataPureProcessing( data ) {
		// ����� ������, ���������� � ������� ������������ ������ ����� �����. ��� �����
		var rmap = map_ajx_lft;
		
		// �������� ������ �� ��������� �����
		// �������� - ������ �� � ������� ��������
		if ( _bFirst == 1 ) {
			_bFirst = 0;
			_parseCfgSystem( data );
		}
		
		_appendToLeftPanel( '<center><h1>����� ��������� �����������</h1></center>' );
		// timestamp 
		_appendToLeftPanel( '<p align="center">������� ������� : '+getTimeStamp()+'</p>' );
		
		// �������� ��������� - ����� ����� ������� ����� ��� ���������, �������
		//   ������ ��������������� ������
		var forSerial = [ 'TrNa', 'TrOO', 'TrLc', 'Stas', /*'VtTy',*/ 'Chan', '_Pow', 'VFRW', 'MaxT' ];
		/**
		// 'NPow', 'NuVt', 'NuAB', 'NuPA', 'NuBC', 'NuDB',   
		// , 'MaxS', '_FRW', 'conn', 'cn_snmp', 'iFRWLow', 'iFRWMax', 
		// 'iPowLow', 'iPowMax', 'iTemLow', 'iTemMax', 'is0', 'is1'
		*/
		for( var key = 0; key < forSerial.length; key++ ) {
			rmap[ forSerial[ key ] ]( data );
		}
	}
	
	function _appendAObj( url, containerName, cbUpdate, cbClear ) {
		var aAjaxObj = new AjaxObj();
		aAjaxObj.update = cbUpdate;
		aAjaxObj.noOkHttpCode = function ( http_code ) { 
			if( http_code === 0) alert( "����� ������� �� �������, ��������� ����������" ); 
		};
		
		aAjaxObj.setPage( url );
			
		// �������� ����� ��� GET �������
		aAjaxObj.setMainContainerId( 'main' );
		aAjaxObj.setContainerId( containerName );
		
		_arr.push( aAjaxObj );
	}
	
	function _fillVtv( numberVtv ) {
		var pages = [ 'ajx_vtv'+numberVtv, 'ajx_vtvmod'+numberVtv ];
		for ( j = 0; j < pages.length; j++ ) {
			var url = CgiPages[ pages[ j ] ];
			
			// TEST
			var cb = AjaxCallbacks[ pages[ j ] ]; // !! �������� !!
			
			var update = cb;
			var clear =  function () {};
			_appendAObj( url, pages[ j ], update, clear );
		}
	}
	
	function _parseCfgSystem( data ) {
		// VARS
		var url;
		var update;
		var clear = function () {};	// ���� ������
		var pages = [];
		var cb; 	// callback to processing request
	
		// ���� ��� ����� ����������
		var trKeyName = _getIntValue( data, 6 )-1;
		// ����� PAB!! ����� - Power Amp. Blocks
		var numPAB = _getIntValue( data, 2 );
		
		// ���_i
		var numVtv = _getIntValue( data, 1 );
		var k;	// ���������� ����� �� ����� �����
		
		// ������� ���������
		page = 'ajx_tmn';
		url = CgiPages[ page ];
		update = AjaxCallbacks[ page ];
		_appendAObj( url, page, update, clear );
		
		// DOING
		var firstPartStr = "<span style='cursor: pointer;' onclick='on_vtv";
		if ( ( numVtv >= 1 ) && ( trKeyName != KV2 ) ) {
			if ( trKeyName > 3 && trKeyName < 22 ) { 
				_fillVtv(1);
			}
			// ����������
			if ( trKeyName <= 3 || trKeyName >= 22 ) { 
				_appendToLeftPanel( firstPartStr + "1a( )'>��� 1</span>" ); 
			}
		}
		if ( ( numVtv > 1 ) && ( trKeyName != KV2 ) ) { // ����������� ������-�� ������
			if ( trKeyName > 3 && trKeyName < 22 ) {
				_fillVtv(2);
			}
			// ����������
			if ( trKeyName <= 3 || trKeyName >= 22 ) { 
				_appendToLeftPanel( firstPartStr + "2a( )'>��� 2</span>" ); 
			}
		}

		//numPAB = 1;
		for ( k = 1; k < numPAB + 1; k++ ) {
			// ������� ������� ����������
			page = 'ajx_pab'+k;
			url = CgiPages[ page ];
			update = AjaxCallbacks[ page ];
			_appendAObj( url, page, update, clear );
		}

		// ���� ��������
		cur = data[ 4 ].split( "=" );
		var iNumBCV = parseInt( cur[ 1 ],10 );
		for ( k = 1; k < iNumBCV + 1; k++ ) {
			// ������� ������� ����������
			page = 'ajx_bcn'+k;
			url = CgiPages[ page ];
			update = AjaxCallbacks[ page ];
			_appendAObj( url, page, update, clear );
		}

		// � ��� ���? ���� ����������?
		cur = data[ 5 ].split( "=" );
		var iNumDB = parseInt( cur[ 1 ],10 );
		//iNumDB = 2;
		// !! ���� � 2!
		for ( k = 2; k < iNumDB + 1; k++ ) {
			// ������� ������� ����������
			page = 'ajx_bd'+k;
			url = CgiPages[ page ];
			update = AjaxCallbacks[ page ];
			_appendAObj( url, page, update, clear );
		}
	}

	function _printLeftData( gettedRawStr ) {
		var gettedDataPkg = gettedRawStr.split( "&" );
		var len = gettedDataPkg.length;
		for( var i = 0; i < len ; i++ ) {
			var oneElement = gettedDataPkg[ i ].split( "=" );
			_appendToLeftPanel( oneElement[ 0 ]+': '+i+ ', // '+oneElement[ 1 ]+' ['+i+']<br>' );
		}
	}
	
/** @public */
	return {
		// ��������� ������� �������� ��������
		processGettedData : function ( gettedRawStr ) {
			var gettedArray = _inputDataFilter( gettedRawStr, 17 );

			if ( gettedArray !== 'None' ) { 
				// ����� ������ ��������� ������
				var canDoSerialRequest = true;
				
				// ������ ����� ��������
				_addLeftPanel();
				_leftDataPureProcessing( gettedArray );
				
				// ����� � ������� �������, ������� ��������� ���������
				parent.SummatorInfo.firstDone( canDoSerialRequest, gettedRawStr );
			}

			// ������� ���������� ������
			//_printLeftData( gettedRawStr );
		},
		
		// ��������� ������ �������
		noConnect : function ( http_code ) {
			if( http_code === 0) alert( "����� ������� �� �������, ��������� ����������" ); 
		},
		
		// ��������� ������� �������� ��������
		onloadcheck : function ( ) {
			_bFirst = 1;
			_addAnyToMainPanel( signLoading );
		},
		
		// 
		getArrNextRequests : function() {
			return _arr;
		},
		
		// �������� � �������� �����
		insertProgressBar : function () { _insertLoadingBar(); },
		removeProgressBar : function () { _removeLoadingBar(); }
	};
})();