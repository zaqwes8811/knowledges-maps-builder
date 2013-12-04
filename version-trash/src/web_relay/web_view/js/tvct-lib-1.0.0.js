/**
  file: tvct_lib.js
  
  thinks:
    ��� ���� html ������ ������ ����. ����������, �� ��� ����������� ����� ���� 
      ��� �� ���� �� � �����
      
    ! � ������������ �������� �� ������ ���� this
      Because this is so easy to get wrong, limit its use to those places where it is required:
        in constructors
        in methods of objects (including in the creation of closures)
*/
var TR_STS = [ "�����","�������������","�����" ];
var TR_OO = [ "��������","�������" ];
var TR_OO2 = [ "-","��������","�������" ];
var TR_LC = [ "������", "������" ];
var TR_CNTMODE = [ "�������","�������������" ];
var TR_LOAD = [ "�������","����������" ];
var TR_AD = [ "����������","��������" ];
var TR_RESERV = [ "��������","���������" ];
var TR_RESERV2 = [ "-","��������","���������" ];
var TR_CNTREADY = [ "�� ����� � ����������","����� � ����������" ];
var TR_READY = [ "�� �����","�����" ];
var TR_INPSTR = [ "����","������" ];
var TR_OUTSYNC = [ "����","���" ];
var TR_OKFA = [ "�����","�����" ];
var TR_OKFA2 = [ "�����","�� �����" ];
var TR_WRK = [ "�� ������","������" ];
var TR_NET = [ "-","-","-","���","���" ];
var TR_QAM = [ "-","QPSK","QAM-16","QAM-64" ];
var TR_RATE = [ "-","1/2","2/3","3/4","5/6","7/8" ];
var TR_GI = [ "-","1/4","1/8","1/16","1/32" ];
var TR_HEIR = [ "-","���","H1","H2","H4" ];
var TR_CARNUM = [ "-","2K","4K","8K" ];
var TR_TEST = [ "-","��������","���","���","��� ����","����-2" ];
var TRNAM = [ 
  "���� 1/0,5 ����������",
  "���� 2/1 ����������",
  "���� 5/2,5 ����������",
  "���� 10/5 ����������",
  "���� 1/0,5 ��������",
  "���� 2/1 ��������",
  "���� 5/2,5 ��������",
  "���� 10/5 ��������",
  /*"����-1 1000 ��",
  "����-2 2000 ��",
  "����-5 5000 ��",*/
  
  "���-1",
  "���-2",
  "���-5",
  
  "����-10",
  "����-0,01",
  "����-0,1",
  "����-0,2",
  "����-0,5",
  "���-1",
  "���-2",
  "���-0,6",
  "����-�-1,0-� 1000 ��",
  "����-�-2,5-� 2000 ��",
  "����-�-5,0-� 5000 ��",
  "����-� 5/2,5 ����������" ];
var TYPE_VTV = [ "-","��� ��� ��������","���-���������� ������������",
  "��� DVB-T","��� DVB-T2","��� DVB-T","��� DVB-T2" ];

var MX_NPAB = 12;

var CgiPages = {
  // ���
  ajx_vtvmod1: "/cgi-bin/ajx_vtvmod1.exe",
  ajx_vtvmod2: "/cgi-bin/ajx_vtvmod2.exe",
  ajx_vtv1: "/cgi-bin/ajx_vtv1.exe",
  ajx_vtv2: "/cgi-bin/ajx_vtv2.exe",
  // PowerAmp
  ajx_pab1: "/cgi-bin/ajx_pab1.exe",
  ajx_pab2: "/cgi-bin/ajx_pab2.exe",
  ajx_pab3: "/cgi-bin/ajx_pab3.exe",
  ajx_pab4: "/cgi-bin/ajx_pab4.exe",
  ajx_pab5: "/cgi-bin/ajx_pab5.exe",
  ajx_pab6: "/cgi-bin/ajx_pab6.exe",
  ajx_pab7: "/cgi-bin/ajx_pab7.exe",
  ajx_pab8: "/cgi-bin/ajx_pab8.exe",
  ajx_pab9: "/cgi-bin/ajx_pab9.exe",
  ajx_pab10: "/cgi-bin/ajx_pab10.exe",
  ajx_pab11: "/cgi-bin/ajx_pab11.exe",
  ajx_pab12: "/cgi-bin/ajx_pab12.exe",
  
  // 
  ajx_bcn1: "/cgi-bin/ajx_bcn1.exe",
  ajx_bcn2: "/cgi-bin/ajx_bcn2.exe",
  ajx_bcn: "/cgi-bin/ajx_bcn",
  
  //
  ajx_bd1: "/cgi-bin/ajx_bd1.exe",
  ajx_bd2: "/cgi-bin/ajx_bd2.exe",
  ajx_bd: "/cgi-bin/ajx_bd",
  
  //
  ajx_logstrnum: "/cgi-bin/ajx_logstrnum.exe",
  ajx_log: "/cgi-bin/ajx_log.exe",
  
  //
  ajx_lft: "/cgi-bin/ajx_lft.exe",
  
  //
  ajx_tmn: "/cgi-bin/ajx_tmn.exe",
  ajx_mtrctl:  "/cgi-bin/ajx_mtrctl.exe", //set predcor
  
  //
  ajx_sysonof:  "/cgi-bin/ajx_sysonof.exe",
  ajx_getsys:  "/cgi-bin/ajx_getsys.exe",
  ajx_getnet:  "/cgi-bin/ajx_getnet.exe",
  ajx_setsys:  "/cgi-bin/ajx_setsys.exe",
  ajx_setnet:  "/cgi-bin/ajx_setnet.exe",
  ajx_restrs:  "/cgi-bin/ajx_restrs.exe",
  ajx_passwd:  "/cgi-bin/ajx_passwd.exe",
  frc_rst:  "/cgi-bin/frc_rst.exe"
};

// ��������� �� ��������� � ����������
// �� ���� �� ������������
function flash_led( ) {
  //change led
  // on
  if ( pt === 0 ) {
    document.getElementById( "mod_er" ).src=led_mod.src;
    document.getElementById( "mdi_er" ).src=led_mdi.src;
    pt=1;
  }
  //off
  else {
    document.getElementById( "mod_er" ).src=led_of.src;
    document.getElementById( "mdi_er" ).src=led_of.src;
    pt=0;
  }

  window.setTimeout( "flash_led( )",1000 );
}

/*
function showit( ) {
  var bl = document.getElementById( "sys_mod" );
  bl.style.visibility = "visible";
  setTimeout( "hideit( )",1000 );
}

function hideit( ) {
  var bl = document.getElementById( "sys_mod" );
  bl.style.visibility = "hidden";
  setTimeout( "showit( )",500 );
}*/

// ����������� ���������
function showHint( id,s ) {
  var sdiv=document.getElementById( id );
  if( s ) sdiv.style.display='block';
  else sdiv.style.display='none';
}

function checkBRNG( ) {
  if ( document.forms[ 0 ].checkauto.checked ) { document.forms[ 0 ].brng.value = "1"; }
  else { document.forms[ 0 ].brng.value = "0"; }
}

/**
  abs.: ����� ��� ������� ������������ ����
    ����� ��������� ��������� ���������� ( Closure, Dojo,... )
*/
var tvct = (function(){
/** @private */
  function _getStrValue( data, pos ) {
    var cur = data[ pos ].split( "=" );
    var value = cur[ 1 ];
    return value;
  }
    
  function _alertUser() {
    alert( 'private member' );
  }
  
  // to public
  function checkPW( fld ) {
    var inputStr = fld.value;
    for ( var i=0;i<inputStr.length;i++ ) {
      var oneChar = inputStr.charAt( i );
      if ( ( oneChar < "0" || oneChar > "9" ) ) {
        alert( "� ��� ���� ������ �������� ������ ���������� �����" );
        fld.value="1000";
        return;
      }
    }
    if ( ( parseInt( inputStr,10 )<0 ) || ( parseInt( inputStr,10 )>1000000 ) ) {
      alert( "��������� �������� ���������� ��������� 1000000" );
      fld.value="1000";
    }
  }
  function isNumber( inputStr ) {
    for ( var i=0;i<inputStr.length;i++ ) {
      var oneChar = inputStr.charAt( i );
      if ( ( oneChar < "0" || oneChar > "9" ) && ( oneChar!="-" ) ) {
        alert( "� ��� ���� ������ �������� ������ ���������� �����" );
        return false;
      }
    }
    return true;
  }
  function checkHex( fld ) {
    var inputStr = fld.value.toUpperCase( );
    if ( !isHex( inputStr ) ) {
      fld.value = "0";
    }
  }
  function isHex( inputStr ) {
    for ( var i=0;i<inputStr.length;i++ ) {
      var oneChar = inputStr.charAt( i );
      if ( ( oneChar < "0" || oneChar > "9" ) ) {
        if ( ( oneChar < "A" || oneChar > "F" ) ) {
          alert( "� ��� ���� ������ �������� ������ ����������������� �����" );
          return false;
        }
      }
    }
    return true;
  }

/** @public  */
  return {
    // Ajax ������� � ������
    getXMLHttpRequest: function() {
      var result = null;
      if ( window.XMLHttpRequest ) {
        try {
          result = new XMLHttpRequest( );
        }
        catch ( e ){}
      } else if ( window.ActiveXObject ) {
        try {
          result = new ActiveXObject( 'Msxml2.XMLHTTP' );
        }
        catch ( e ) {
          try {
            result = new ActiveXObject( 'Microsoft.XMLHTTP' );
          }
          catch ( e ){
            // alert( 'Caught Exception: ' + e.description );
            // � ����� � ����� XMLHttpRequest � Firefox ���������� ����������� ������
            // Bugzilla Bug 238559 XMLHttpRequest needs a way to report networking errors
            // https://bugzilla.mozilla.org/show_bug.cgi?id=238559
            alert( "Exception : tvct-lib.js file. Create Ajax object." );
          }
        }
      }
      return result;
    },
    
    // ������ ������
    buttonHovers: function ( namesButtons, imagesPath ) {
      preloads = new Object( );
      preloads[ 0 ] = new Image( ); preloads[ 0 ].src = imagesPath + "button_left_xon.gif";
      preloads[ 1 ] = new Image( ); preloads[ 1 ].src = imagesPath + "button_right_xon.gif";

      var buttons = [];
      var i;
      for ( i = 0; i < namesButtons.length; i++ ) {
        buttons[ i ] = document.getElementById( namesButtons[ i ] );
      }

      for ( i = 0; i < buttons.length; i++ ) {
        buttons[ i ].className = "buttonSubmit";
        
        var buttonLeft = document.createElement( 'img' );
        buttonLeft.src = imagesPath + "button_left.gif";
        buttonLeft.className = "buttonImg";
        buttons[ i ].parentNode.insertBefore( buttonLeft, buttons[ i ] );
        
        var buttonRight = document.createElement( 'img' );
        buttonRight.src = imagesPath + "button_right.gif";
        buttonRight.className = "buttonImg";
        if( buttons[ i ].nextSibling ) {
          buttons[ i ].parentNode.insertBefore( buttonRight, buttons[ i ].nextSibling );
        } else {
          buttons[ i ].parentNode.appendChild( buttonRight );
        }
        buttons[ i ].onmouseover = function( ) {
          this.className += "Hovered";
          this.previousSibling.src = imagesPath + "button_left_xon.gif";
          this.nextSibling.src = imagesPath + "button_right_xon.gif";
        };
        buttons[ i ].onmouseout = function( ) {
          this.className = this.className.replace( /Hovered/g, "" );
          this.previousSibling.src = imagesPath + "button_left.gif";
          this.nextSibling.src = imagesPath + "button_right.gif";
        };
      }
    },
    
    // �������� ������ �������� ���� [ name1=3, name2=,..] � ���������� �������� ���� �� �������
    getStrValue: function( data, pos ) {
      return _getStrValue( data, pos );
    },
    getIntValue: function( data, pos ) {
      var strValue = _getStrValue( data, pos );
      var intValue = parseInt( strValue, 10 );
      return intValue;
    }
  };
})();

/**
  thinks: 
    ��� c ���� ����� ������ �����������
    ! ������ ���������� ������ !
    
  nfo: 
  
  use-cases :
    1. ��������� ����� - ������ �� �������
    2. ������� ������ �� � �������� ��������� �����

*/
function bind(func, context) {
  return function() { 
    return func.apply(context, arguments); 
  };
}

function AjaxObj() { 
  // ������ ��������� � ������������
/** @private */
  // virtuals
  var self = this;  // ��������� this � ���������
  
  // �������� �����
  var _mainContainerId = 'main';
  var _container = 'vtv';
  
  // 
  var _ajaxRequestGet = tvct.getXMLHttpRequest();
    var _urlToGet = '';
  var _ajaxRequestPost = tvct.getXMLHttpRequest();
    var _postPage = "";
  var _numTriesConnect = 0;  // ������ ����� ���������� � ����
  var MAX_TRIES = 4;
  var _sendEna = true;  // ���������� �������� �������. ������ ��������������
  
  // ����� �� ��������
  var _appendToPanel = function ( what ) { $('#'+_container+'').append( what + '<br>' ); };
  var _addPanel = function() { 
    $('#'+_mainContainerId+'').append( '<p>Name:'+_container+'</p>'); 
    $('#'+_mainContainerId+'').append( '<div style="background: #E0FFE0;" id="'+_container+'"></div>'); 
  };
  
  //
  var _makeUrl = function ( page ) {
    var result = page+"?"+"r="+Math.random( );
    return result;
  };
  
  // processing GET change state
  var _processReqChange = function( ) {
    try {
      if ( _ajaxRequestGet.readyState == 4 ) {
        if ( _ajaxRequestGet.status == 200 ) {
          // ����� ��� ���, �� ����� ���� ���
          _numTriesConnect = 0;
          
          // ��������� �������
          _sendEna = false;
          
          //
          var result = _ajaxRequestGet.responseText;
          self.update( result );
        } else {
          // ������ ���� ������ http-�������
          self.noOkHttpCode( _ajaxRequestGet.status );
        }
      }
    }
    catch( e ) { 
      alert( "Exception : tvct-lib.js file. Ajax GET-response processing." );
    }
  };
  
  var _sendGetRequest = function () {
    _ajaxRequestGet = tvct.getXMLHttpRequest();
    if ( _ajaxRequestGet  ) {
      // ������� Url
      var url = _makeUrl( _urlToGet );

      // �����������
      _ajaxRequestGet.open( "GET", url, true );
      _ajaxRequestGet.onreadystatechange = _processReqChange;
      _ajaxRequestGet.send( null );
    } else {
      alert( "������ ��� �������� XMLHttpRequest �������" );
    }
    
    _numTriesConnect = _numTriesConnect+1;
    if ( _numTriesConnect >= MAX_TRIES ) {
      self.triesIsOver( );
    }
  };
  
  // processing POST change state
  var _processPostReqChange = function ( ) {
    try {
      if ( _ajaxRequestPost.readyState == 4 ) {
        if ( _ajaxRequestPost.status == 200 ) {
          var result = _ajaxRequestPost.responseText;
          self.splitRequest( result );
        } else {
          alert( "�� ������� ���������" );
        }
      }
    }
    catch( e ) { alert( "Exception : tvct-lib.js file. Ajax POST-response processing." ); }
  };
  
  var _repeatIfNeed = function () {
    if ( _numTriesConnect > 0 ) {
      _sendGetRequest();
      
    }
  };
  
/** @public */
  // Virtual 
  // for GET
  this.update = function ( gettedStr ) {};  // ������������ ���������� �����
  this.noOkHttpCode = function ( codeState ) {
    /* ������ ������ http-������� */
  };  // �������� � ������ ������ ajax ( 4 and not 200 )
  // for POST
  this.triesIsOver = function () { };
  this.splitRequest = function ( gettedStr ) {};  
  
  // No virtual
  this.setMainContainerId = function ( id ) { _mainContainerId = id; };
  this.setContainerId = function ( id ) { _container = id; };
  
  // ��������������� ��������
  this.preView = function ( gettedStr ) {
    var gettedDataPkg = gettedStr.split( "&" );
    _addPanel();
    var len = gettedDataPkg.length;
    for( var i = 0; i < len ; i++ ) {
      var oneElement = gettedDataPkg[ i ].split( "=" );
      _appendToPanel( oneElement[ 0 ]+': '+i+ ', // '+oneElement[ 1 ]+' ['+i+']' );
    }
    _appendToPanel( '' );
  };
  
  // ���������� ���������
  this.processing = function ( gettedDataPkg, callbacksMap ) {
    // !! �� ������������ ������� ������
    for( var key in callbacksMap ) if ( callbacksMap.hasOwnProperty( key ) ) {
      callbacksMap[ key ]( gettedDataPkg );
    }
  };

  // ������� GET-������
  // ������ ������ �����
  this.getData = function( page ) {
    if ( _sendEna ) {
      _urlToGet = page;
      _sendGetRequest();
    }
  };

  // ���������� ������ - ��������� url ��������, ����� - ��������
  this.setPage = function ( url ) {
    _urlToGet = url;
  };
  this.doGetRequest = function(  ) {
    this.getData( _urlToGet );
  };

  // ������� POST-������ - no tested
  this.postData = function ( val, page ) {
    _ajaxRequestPost = tvct.getXMLHttpRequest();
    if ( _ajaxRequestPost ) {
      _ajaxRequestPost.open( "POST", page, true );
      try {
        _ajaxRequestPost.setRequestHeader( "Content-Type", "application/x-www-form-urlencoded" );
      }
      catch( e ){ 
        alert("Exception : tvct-lib.js file. Ajax POST-request send.");
      }

      var params;
      params = "cmd="+val.toString( );
      _ajaxRequestPost.onreadystatechange = _processPostReqChange;
      _ajaxRequestPost.send( params );
    }
  };
}

// pseudo for lib calls
var _getStrValue = tvct.getStrValue;
var _getIntValue = tvct.getIntValue;
  
// Const
var KV2 = 76;  // ��������������

// �������� ����� �������
// to lib
// src : Mon Oct 08 2012 15:13:30 GMT+0400 (���������� ����� (����))
function getTimeStamp() {
  /*var MonthMap = {
    Jan : "������",  Feb : "�������", Mar : "�����", Apr : "������",
    May : "���", Jun : "����", Jul : "����", Aug : "�������",
    Sep : "��������", Oct : "�������", Nov : "������", Dec : "�������"
  };*/
  var MonthMap = {
    0 : "������",  1 : "�������", 2 : "�����", 3 : "������",
    4 : "���", 5 : "����", 6 : "����", 7 : "�������",
    8 : "��������", 9 : "�������", 10 : "������", 11 : "�������"
  };

  var timestamp = new Date();
  var mins = '0'+timestamp.getUTCMinutes();  // 0xx ��� 0x
  var secs = '0'+timestamp.getUTCSeconds();  // 0xx ��� 0x
  var timeDay = (timestamp.getUTCHours()+4)+':'+
    mins[ mins.length-2]+mins[ mins.length-1]+':'+
    secs[ secs.length-2]+secs[ secs.length-1];
  var summary = [];
  summary.push( timeDay );  // ����� ���
  summary.push( timestamp.getUTCDate() );  // �����
  summary.push( MonthMap[ timestamp.getUTCMonth() ] );  // �����
  summary.push( timestamp.getUTCFullYear() );
  var result = summary.join( ' ' );
  return result;
}

function yel_fon(cur,curinput) {
  if (parseInt(cur[1],10)==1) curinput.style.backgroundColor="#ffff00";
  else curinput.style.backgroundColor="#FAFAFA";
}
function red_fon(cur,curinput) {
  if (parseInt(cur[1],10)==1)  curinput.style.backgroundColor="#ff3500";
  else curinput.style.backgroundColor="#FAFAFA";
}

function yel_green(cur,curinput) { 
  if (parseInt(cur[1],10)==1) curinput.style.backgroundColor="#ffff00";
  else curinput.style.backgroundColor="#33cc00";
}

function red_green(cur,curinput) {
  if (parseInt(cur[1],10)==1)  curinput.style.backgroundColor="#ff3500";
  else curinput.style.backgroundColor="#33cc00";
}

function red_green_bound(curinput,bh,bl) {
  if ((curinput.value <= bh)&&(curinput.value >= bl)) {
    curinput.style.backgroundColor="#33cc00";
  } else {
    curinput.style.backgroundColor="#ff0000";
  }
}

function red_fon_bound(curinput,bh,bl) {
  if ((curinput.value <= bh)&&(curinput.value >= bl)) {
    curinput.style.backgroundColor="#FAFAFA";
  } else {
    curinput.style.backgroundColor="#ff0000";
  }
}

function show_pa(objd) {
  var compstyle;
  var dst;
  if (objd.currentStyle) dst = objd.currentStyle.display;
  else {
     compstyle = document.defaultView.getComputedStyle(objd,"");
     dst = compstyle.getPropertyValue("display");
  }
  //objd.style.display = "block";
}

function show_r(cur_id,vw) {  

  var objd = document.getElementById(cur_id);
  //var objd = cur_span;
  var compstyle;
  var dst;
  if (objd.currentStyle) dst = objd.currentStyle.display;
  else {
    compstyle = document.defaultView.getComputedStyle(objd,"");
    dst = compstyle.getPropertyValue("display");
  }
  
  if(vw == 0) {
    objd.style.display = "none";
  }
  else //if(vw == 1)
  {
    objd.style.display = "block";
  }
  
}



