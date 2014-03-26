//test for build a chat for a match
function testBuildChatCache() {
  var xmlhttp = helper.getObjectXHR();
  var url = "3.smg-server.appspot.com/matches/..";
  var postInfo = {
    "accessSignature":"YSHEODJSGDK",
    "gameId":"5",
    "matchId":"10"
  }
  xmlhttp.onreadystatechange = functions() {
    var resContent = JSON.parse(xmlhttp.responseText);
    helper.assert(resContent.hasOwnProperty("status"));
    if(helper.equals(resContent.status, "SUCCESS_BUILD")) {
        helper.assert(resContent.hasOwnProperty("matchId"));
        helper.assert(resContent.hasOwnProperty("playerIds"));
    }
  }
  xmlhttp.open("POST", url, true);
  xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
  xmlhttp.send(postInfo);
}

//test for send message
function testMessageSend() {
  var xmlhttp = helper.getObjectXHR();
  var url = "3.smg-server.appspot.com/matches/..";
  var postInfo = {
    "accessSignature":"YSHEODJSGDK",
    "gameId":"5",
    "matchId":"10",
    "message":"Hi"
  }
  xmlhttp.onreadystatechange = functions() {
    var resContent = JSON.parse(xmlhttp.responseText);
    helper.assert(resContent.hasOwnProperty("status"));
    if(helper.equals(resContent.status, "SUCCESS_SEND")) {
        helper.assert(resContent.hasOwnProperty("matchId"));
        helper.assert(resContent.hasOwnProperty("gameId"));
        helper.assert(resContent.hasOwnProperty("playerId"));
    }
  }
  xmlhttp.open("POST", url, true);
  xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
  xmlhttp.send(postInfo);
}
