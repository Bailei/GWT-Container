package org.client.container1;

import com.google.gwt.json.client.*;

import java.util.List;
import java.util.Set;


public class GameServer {

  private final String url = "http://1-dot-smg-server.appspot.com/";
  
  //for optimization
  private JSONString accessSignature = null;
  private JSONString matchId;
  private GameContainer gameContainer;
  private JSONString gameId = null;
  private JSONArray playerIds = new JSONArray();
  private JSONString myPlayerId = null;
 
  private JSONArray myLastMove = null;
  
  private int lastMovePlayerId = -1;
  
  public GameServer() {
    ServerMessageListener.setServer(this);
  }
  
  public void setMatchId(JSONString matchId) {
    this.matchId = matchId;
  }
  
  public GameContainer getGameContainer() {
    return gameContainer;
  }
  
  public void sendInsertNewMatch(
      String accessSignature, 
      String gameId, 
      List<String> playerIds, 
      GameContainer gameContainer,
      String myPlayerId) {
	  
	this.myPlayerId = new JSONString(myPlayerId);
	this.gameContainer = gameContainer;
    this.accessSignature = new JSONString(accessSignature);
	this.gameId = new JSONString(gameId);
	for(int i=0; i<playerIds.size(); i++) {
	  this.playerIds.set(i, new JSONString(playerIds.get(i)));	
	}
	
	StringBuilder sb = new StringBuilder(url);
	sb.append("newMatch");
	String integratedUrl = sb.toString();
	
	JSONObject postInfo = new JSONObject();
	postInfo.put("accessSignature", this.accessSignature);
	postInfo.put("playerIds", this.playerIds);
	postInfo.put("gameId", this.gameId);
	String message = postInfo.toString();
	sendMessage1(message, integratedUrl);
  }
  
  private native void sendMessage1(String message, String url) /*-{
    var xmlHttp;
	  if($wnd.XMLHttpRequest){
	    xmlHttp = new XMLHttpRequest();
	  }else{
	    xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	  }
      xmlHttp.onreadystatechange = function() {
        if(xmlHttp.readyState==4 && xmlHttp.status==200) {
          var response = xmlHttp.responseText;
          @org.client.container1.ServerMessageListener::messageListener(Ljava/lang/String;) (response);
        }
      }  
    xmlHttp.open("POST", url, true);
    xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlHttp.send(message);
  }-*/;
  
  
  
  
  
  
  
  
  
  
  
	
  public void sendMakeMove(JSONArray operations) {

	myLastMove = operations;
	StringBuilder sb = new StringBuilder(url);
	sb.append("matches/")
	  .append(matchId.stringValue());

	String integratedUrl = sb.toString();
	JSONObject postInfo = new JSONObject();
	postInfo.put("accessSignature", this.accessSignature);
	postInfo.put("playerIds", this.playerIds);
	postInfo.put("operations", operations);
	String message = postInfo.toString();

	sendMessage3(message, integratedUrl);
  }
  
  private native void sendMessage3(String message, String url) /*-{
    var xmlHttp;
	if($wnd.XMLHttpRequest){
	  xmlHttp = new XMLHttpRequest();
	}else{
	  xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
    xmlHttp.onreadystatechange = function() {
      if(xmlHttp.readyState==4 && xmlHttp.status==500) {
        alert("message too long????");
      } 
      if(xmlHttp.readyState==4 && xmlHttp.status==200) {
      	alert("successful!!");
        var response = xmlHttp.responseText;
        @org.client.container1.ServerMessageListener::messageListener(Ljava/lang/String;) (response);
      }
    }  
    xmlHttp.open("POST", url, true);
    xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    //xmlHttp.send(JSON.stringify(jsonObjTmp));
    //xmlHttp.send(JSON.stringify(jsonObj));
    xmlHttp.send(message);
  }-*/;
  
  public void getMatchInfo() {
    StringBuilder sb = new StringBuilder(url);
	sb.append("matches/")
	  .append(matchId.stringValue())
	  .append("?")
	  .append("accessSignature")
	  .append("=")
	  .append(accessSignature.stringValue())
	  .append("&")
	  .append("playerId")
	  .append("=")
	  .append(myPlayerId.stringValue());
	  String integratedUrl = sb.toString();
	  sendMessage2(integratedUrl);
  }
  
  public JSONArray getMyLastMove() {
    return myLastMove;
  }
  
  private native void sendMessage2(String url) /*-{
    var xmlHttp;
    if($wnd.XMLHttpRequest) {
      xmlHttp = new XMLHttpRequest();
    }else {
      xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");	
    }
    xmlHttp.onreadystatechange = function() {
      if(xmlHttp.readyState==4 && xmlHttp.status==200) {
        var response = xmlHttp.responseText;
        @org.client.container1.ServerMessageListener::messageListener(Ljava/lang/String;) (response);
      }
    }
    xmlHttp.open("GET", url, true);
    xmlHttp.send();
  }-*/;
  
  private native void test(String message) /*-{
    $wnd.alert(message);
  }-*/;
}

class ServerMessageListener {
  
  private static GameServer gameServer = null;
  
  public static void setServer(GameServer gameServer) {
    ServerMessageListener.gameServer = gameServer;
  }
	
  private static void messageListener(String response) {
    JSONObject res = JSONParser.parseStrict(response).isObject();
    test("6666666666666666666666666666");
	if(res != null) {
	
	test(String.valueOf(res.size()));
	Set<String> keys = res.keySet();
	test(keys.toString());
	
	  if(res.get("error") != null) {
		test(res.get("error").isString().stringValue());
	    throw new RuntimeException("API Parameters Error!");
	  }
	  if(res.size() == 2 && res.get("matchId")!=null) {
		JSONString matchId = res.get("matchId").isString();
	    gameServer.setMatchId(matchId);
		gameServer.getGameContainer().updateUi(new JSONObject(), new JSONArray(), new JSONString("1"));
      }else if(res.size() == 1 && res.get("gameState")!=null) {
    	//test("9999999999999999999999");
	    JSONObject state = res.get("gameState").isObject();
		gameServer.getGameContainer().updateUi(state, gameServer.getMyLastMove(), new JSONString("1"));
	  }else if(res.size() == 6) {
		test(res.toString());
		Set<String> keySet = res.keySet();
		test(keySet.toString());
		JSONString history = res.get("history").isString();
		if(history!=null) {
		  if(history.toString().equals("")) {
			test("It is null");
		  }
		}
		//test(String.valueOf(history.size()));
		//JSONObject gameState = history.get("gameState").isObject();
		test("kkkkkkk");
		//JSONObject state = gameState.get("state").isObject();
		test("nnnnnnn");
		//JSONArray lastMove = history.get("lastMove").isArray();
		
		//gameServer.getGameContainer().updateUi(state, lastMove, new JSONString("1"));
	  }else {
	    throw new RuntimeException("Response Exception!");
	  }
	}else {
		throw new RuntimeException("JSON Parse Error!");
	}
  }	
  
  private static native void display(String message) /*-{
  	$doc.getElementById("text").innerHTML = message;
  }-*/;

  private static native void test(String message) /*-{
    $wnd.alert(message);
  }-*/;
}
