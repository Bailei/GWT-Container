//test for start a match
function testMakeMatch() {
	var xmlhttp = helper.getObjectXHR();
	var url = "1.smg-server.appspot.com/matches/..";
	var postInfo = {
		"accessSignature": "YSHEODJSGDK",
		"gameId": "5"
	}
    xmlhttp.onreadystatechange = function() {
      var resContent = JSON.parse(xmlhttp.responseText);
      helper.assert(resContent.hasOwnProperty("status"));
      helper.track("status",resContent.status);
      if(helper.equals(resContent.status, "SUCCESS_MATCH")) {
        helper.assert(resContent.hasOwnProperty("matchId"));
        helper.track("matchId",resContent.matchId);
      }
    }
    xmlhttp.open("POST", url, true);
    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlhttp.send(postInfo);
}

//test for get opponent info
function testGetOpponentInfo() {
  var xmlhttp = helper.getObjectXHR();
  var url = "1.smg-server.appspot.com/players/..?" + "accessSignature=YSHEODJSGDK&" +
       "gameId=5&" + "matchId=21";
  xmlhttp.onreadystatechange = function() {
    var resContent = JSON.parse(xmlhttp.responseText);
    helper.assert(resContent.hasOwnProperty("status"));
    if(equals(resContent.status, "SUCCESS_GET")) {
      helper.assert(resContent.hasOwnProperty("name"));
      helper.assert(resContent.hasOwnProperty("email"));
      helper.assert(resContent.hasOwnProperty("rate"));
    }
  }
  xmlhttp.open("GET", url , true);
  xmlhttp.send(null);
}

//test for cancel current match
function testCancelCurrentMatch() {
  var xmlhttp = helper.getObjectXHR();
  var url = "1.smg-server.appspot.com/matches/..?" + "accessSignature=YSHEODJSGDK&" +
       "gameId=5&" + "matchId=21";
  helper.xmlhttp.onreadystatechange = function() {
    var resContent = JSON.parse(xmlhttp.responseText);
    helper.assert(resContent.hasOwnProperty("status"));
    if(helper.equals(resContent.status, "SUCCESS_CANCEL")) {
        helper.assert(resContent.hasOwnProperty("matchId"));
    }
  }
  xmlhttp.open("DELETE", url, true);
  xmlhttp.send(null);
}

//test for get my info
function testGetMyInfo() {
  var xmlhttp = helper.getObjectXHR();
  var url = "1.smg-server.appspot.com/players?" + "accessSignature=YSHEODJSGDK";
  xmlhttp.onreadystatechange = function() {
    var resContent = JSON.parse(xmlhttp.responseText);
    helper.assert(resContent.hasOwnProperty("status"));
    if(helper.equals(resContent.hasOwnProperty("status"))) {
      helper.assert(resContent.hasOwnProperty("name"));
      helper.assert(resContent.hasOwnProperty("email"));
      helper.assert(resContent.hasOwnProperty("rate"));
    }
  }
  xmlhttp.open("GET", url, true);
  xmlhttp.send(null);
}  

//test for send result to the server
function sendGameOverResultToServerTest() {
  var xmlhttp = helper.getObjectXHR();
  var url = "1.smg-server.appspot.com/players/..?"
  if(!xmlhttp)
    alert("Error initializing XMLHttpRequest!");
  else{
    var postInfo = {
    "request": "GameOver",	
    "accessSignature":"YSHEODJSGDK",
    "gameId":"5",
    "matchId":"10",
    "playerId":"41",
    "tokensWon":"2",
    "gamingTime":"40"
    };
    xmlhttp.open("POST", url, true);
    xmlhttp.onreadystatechange = callbackForsendGameOverResultToServerTest;
    xmlhttp.send(postInfo);
  }
}

function callbackForsendGameOverResultToServerTest(){
  if(xmlhttp.readystate == 4){
    if(xmlhttp.status == 200){
     var resContent = JSON.parse(xmlhttp.responseText);
     helper.assert(resContent.hasOwnProperty("status"));
     helper.assert(resContent.hasOwnProperty("playerId"));
     helper.assert(resContent.hasOwnProperty("tokensLeft"));
    }else if(helper.status == 404){
     alert("Requset URL dose not exist!");  
    }else{
     alert("Error: status is " + xmlhttp.status);
    }
  }
}