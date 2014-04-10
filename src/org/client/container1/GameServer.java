package org.client.container1;

import com.google.gwt.json.client.*;

import java.util.List;
import java.util.Set;


public class GameServer {

  private final String url = "http://3-dot-smg-container-server3.appspot.com/";
  
  //for optimization
  private JSONString accessSignature = null;
  private JSONString matchId = null;
  private GameContainer gameContainer;
  private JSONString gameId = null;
  private JSONArray playerIds = new JSONArray();
  private JSONString myPlayerId = null;
 
  private JSONArray myLastMove = null;
  
  private int lastMovePlayerId = -1;
  
  public GameServer() {
    ServerMessageListener.setServer(this);
  }
  
  public JSONString getMatchId() {
    return matchId;
  }
  
  public void setMyPlayerId(JSONString myPlayerId) {
    this.myPlayerId = myPlayerId;
  }
 
  public void setGameContainer(GameContainer gameContainer) {
    this.gameContainer = gameContainer;
  }
  
  public void setAccessSignature(JSONString accessSignature) {
    this.accessSignature = accessSignature;
  }
  
  public void setGameId(JSONString gameId) {
    this.gameId = gameId;
  }
  
  public void setMatchId(JSONString matchId) {
    this.matchId = matchId;
  }
  
  public void setPlayerIds(JSONArray playerIds) {
    this.playerIds = playerIds;
  }
  
  public GameContainer getGameContainer() {
    return gameContainer;
  }
  
  public JSONArray getMyLastMove() {
    return myLastMove;
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
        @org.client.container1.ServerMessageListener::messageListener1(Ljava/lang/String;) (response);
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

	sendMessage2(message, integratedUrl);
  }
  
  private native void sendMessage2(String message, String url) /*-{
    var xmlHttp;
	if($wnd.XMLHttpRequest){
	  xmlHttp = new XMLHttpRequest();
	}else{
	  xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
	}
    xmlHttp.onreadystatechange = function() {
      if(xmlHttp.readyState==4 && xmlHttp.status==200) {
        var response = xmlHttp.responseText;
        @org.client.container1.ServerMessageListener::messageListener2(Ljava/lang/String;) (response);
      }
    }  
    xmlHttp.open("POST", url, true);
    xmlHttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlHttp.send(message);
  }-*/;
  
  public void getNewMatchInfo() {
    StringBuilder sb = new StringBuilder(url);
    sb.append("newMatch/")
      .append(myPlayerId.stringValue())
      .append("?")
      .append("accessSignature")
      .append("=")
      .append(accessSignature.stringValue());
    String integratedUrl = sb.toString();
    sendMessage3(integratedUrl);
  }
  
  private native void sendMessage3(String url) /*-{
    var xmlHttp;
    if($wnd.XMLHttpRequest) {
      xmlHttp = new XMLHttpRequest();
    }else {
      xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");	
    }
    xmlHttp.onreadystatechange = function() {
      if(xmlHttp.readyState==4 && xmlHttp.status==200) {
        var response = xmlHttp.responseText;
        @org.client.container1.ServerMessageListener::messageListener3(Ljava/lang/String;) (response);
      }
    }
    xmlHttp.open("GET", url, true);
    xmlHttp.send();
  }-*/;  
  
  public void getNewState() {
    StringBuilder sb = new StringBuilder(url);
    sb.append("state/")
      .append(matchId.stringValue())
      .append("?")
      .append("playerId")
      .append("=")
      .append(myPlayerId.stringValue())
      .append("&")
      .append("accessSignature")
      .append("=")
      .append(accessSignature.stringValue());
    String integratedUrl = sb.toString();
    sendMessage4(integratedUrl);
  }
  
  private native void sendMessage4(String url) /*-{
    var xmlHttp;
    if($wnd.XMLHttpRequest) {
      xmlHttp = new XMLHttpRequest();
    }else {
      xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");	
    }
    xmlHttp.onreadystatechange = function() {
      if(xmlHttp.readyState==4 && xmlHttp.status==200) {
        var response = xmlHttp.responseText;
        @org.client.container1.ServerMessageListener::messageListener4(Ljava/lang/String;) (response);
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
  
  private static void messageListener1(String response) {
    JSONObject res = JSONParser.parseStrict(response).isObject();
    if(res != null) {
      if(res.get("error") != null) {
        throw new RuntimeException("Response is error");
      }
      JSONString matchId = res.get("matchId").isString();
      gameServer.setMatchId(matchId);
      gameServer.getGameContainer().updateUi(new JSONObject(), new JSONArray(), new JSONString("1"));
    }else {
      throw new RuntimeException("Response is null");
    }
  }
  
  private static void messageListener2(String response) {
    JSONObject res = JSONParser.parseStrict(response).isObject();
	if(res != null) {
	  if(res.get("error") != null) {
	    throw new RuntimeException("Response is error");
	  }
	  JSONObject state = res.get("state").isObject();
      gameServer.getGameContainer().updateUi(state, gameServer.getMyLastMove(), new JSONString("1"));
	}else {
      throw new RuntimeException("Response is null");
	}
  }
  
  private static void messageListener3(String response) {
    JSONObject res = JSONParser.parseStrict(response).isObject();
	if(res != null) {
	  if(res.get("error") != null) {
	    throw new RuntimeException("Response is error");
	  }
	  JSONString matchId = res.get("matchId").isString();
	  JSONArray playerIds = res.get("playerIds").isArray();
	  gameServer.setMatchId(matchId);
	  gameServer.setPlayerIds(playerIds);
	}else {
	  throw new RuntimeException("Response is null");
	}
  }
  
  private static void messageListener4(String response) {
    JSONObject res = JSONParser.parseStrict(response).isObject();
    if(res != null) {
      if(res.get("error") != null) {
	    throw new RuntimeException("Response is error");
	  }
	  JSONString matchId = res.get("matchId").isString();
	  JSONObject state = res.get("state").isObject();
	  JSONArray lastMove = res.get("lastMove").isArray();
	  JSONString lastMovePlayerId = new JSONString("1");
	  gameServer.getGameContainer().updateUi(state, lastMove, lastMovePlayerId);
	}else {
	  throw new RuntimeException("Response is null");
	}
  }
  
  private static native void display(String message) /*-{
  	$doc.getElementById("text").innerHTML = message;
  }-*/;

  private static native void test(String message) /*-{
    $wnd.alert(message);
  }-*/;
}
